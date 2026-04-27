package com.scto.mcs.core.runner.runners.web.markdown

import android.content.Context
import android.content.Intent

import com.scto.mcs.core.files.BuiltinFileType
import com.scto.mcs.core.files.FileObject
import com.scto.mcs.core.ui.icons.Icon
import com.scto.mcs.core.resources.getString
import com.scto.mcs.core.resources.strings
import com.scto.mcs.core.runner.RunnerImpl
import com.scto.mcs.core.runner.runners.web.html.HtmlRunner

import java.lang.ref.WeakReference

var mdViewerRef = WeakReference<MDViewer?>(null)
var toPreviewFile: FileObject? = null

class MarkdownRunner : RunnerImpl() {
    override suspend fun run(context: Context, fileObject: FileObject) {
        val intent = Intent(context, MDViewer::class.java)
        toPreviewFile = fileObject
        context.startActivity(intent)
    }

    override fun getName(): String {
        return strings.markdown_preview.getString()
    }

    override fun getIcon(context: Context): Icon {
        return Icon.DrawableRes(BuiltinFileType.MARKDOWN.icon!!)
    }

    override suspend fun isRunning(): Boolean {
        return mdViewerRef.get() != null
    }

    override suspend fun stop() {
        HtmlRunner.httpServer?.let {
            it.closeAllConnections()
            if (it.isAlive) {
                it.stop()
            }
        }
        HtmlRunner.httpServer = null
        mdViewerRef.get()?.finish()
        mdViewerRef = WeakReference(null)
    }
}
