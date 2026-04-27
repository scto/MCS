package com.scto.mcs.core.commands.editor

import android.view.KeyEvent

import com.scto.mcs.core.DefaultScope
import com.scto.mcs.core.commands.CommandContext
import com.scto.mcs.core.commands.CommandProvider
import com.scto.mcs.core.commands.EditorActionContext
import com.scto.mcs.core.commands.EditorCommand
import com.scto.mcs.core.commands.EditorNonActionContext
import com.scto.mcs.core.commands.KeyCombination
import com.scto.mcs.core.ui.icons.Icon
import com.scto.mcs.core.resources.drawables
import com.scto.mcs.core.resources.getString
import com.scto.mcs.core.resources.strings
import com.scto.mcs.core.runner.Runner
import com.scto.mcs.feature.settings.Settings

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
class RunCommand(commandContext: CommandContext) : EditorCommand(commandContext) {
    override val id: String = "editor.run"

    override fun getLabel(): String = strings.run.getString()

    override fun action(editorActionContext: EditorActionContext) {
        val editorTab = editorActionContext.editorTab
        val activity = editorActionContext.currentActivity
        CommandProvider.SaveCommand.action(editorActionContext)
        DefaultScope.launch {
            Settings.runs += 1
            Runner.run(
                context = activity,
                fileObject = editorTab.file,
                onMultipleRunners = {
                    editorTab.editorState.showRunnerDialog = true
                    editorTab.editorState.runnersToShow = it
                },
            )
        }
    }

    override fun isSupported(editorNonActionContext: EditorNonActionContext): Boolean {
        return Runner.isRunnable(editorNonActionContext.editorTab.file)
    }

    override fun getIcon(): Icon = Icon.DrawableRes(drawables.run)

    override val defaultKeybinds: KeyCombination = KeyCombination(keyCode = KeyEvent.KEYCODE_F5)
}
