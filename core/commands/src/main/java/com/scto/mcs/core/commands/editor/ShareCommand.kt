package com.scto.mcs.core.commands.editor

import android.content.Context
import android.content.Intent

import androidx.core.content.FileProvider

import com.scto.mcs.app.scope.DefaultScope
import com.scto.mcs.core.commands.CommandContext
import com.scto.mcs.core.commands.EditorActionContext
import com.scto.mcs.core.commands.EditorCommand
import com.scto.mcs.core.files.FileWrapper
import com.scto.mcs.core.ui.icons.Icon
import com.scto.mcs.core.resources.drawables
import com.scto.mcs.core.resources.getString
import com.scto.mcs.core.resources.strings
import com.scto.mcs.core.utils.toast

import kotlinx.coroutines.launch

class ShareCommand(commandContext: CommandContext) : EditorCommand(commandContext) {
    override val id: String = "editor.share"

    override fun getLabel(): String = strings.share.getString()

    override fun action(editorActionContext: EditorActionContext) {
        val activity = editorActionContext.currentActivity
        val file = editorActionContext.editorTab.file

        DefaultScope.launch {
            if (file.getAbsolutePath().contains(activity.filesDir.parentFile!!.absolutePath)) {
                toast(strings.permission_denied)
                return@launch
            }

            val fileUri =
                if (file is FileWrapper) {
                    FileProvider.getUriForFile(activity as Context, "${activity.packageName}.fileprovider", file.file)
                } else {
                    file.toUri()
                }

            val intent =
                Intent(Intent.ACTION_SEND).apply {
                    type = activity.contentResolver.getType(fileUri) ?: "*/*"
                    setDataAndType(fileUri, activity.contentResolver.getType(fileUri) ?: "*/*")
                    putExtra(Intent.EXTRA_STREAM, fileUri)
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }

            activity.startActivity(Intent.createChooser(intent, strings.core_commands_share_file_chooser_title.getString()))
        }
    }

    override fun getIcon(): Icon = Icon.DrawableRes(drawables.send)
}
