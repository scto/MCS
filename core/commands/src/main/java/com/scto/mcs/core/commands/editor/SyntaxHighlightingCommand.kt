package com.scto.mcs.core.commands.editor

import com.scto.mcs.core.commands.Command
import com.scto.mcs.core.commands.CommandContext
import com.scto.mcs.core.commands.EditorActionContext
import com.scto.mcs.core.commands.EditorCommand
import com.scto.mcs.core.files.FileTypeManager
import com.scto.mcs.core.ui.icons.Icon
import com.scto.mcs.core.resources.drawables
import com.scto.mcs.core.resources.getString
import com.scto.mcs.core.resources.strings

class SyntaxHighlightingCommand(commandContext: CommandContext) : EditorCommand(commandContext) {
    override val id: String = "editor.syntax_highlighting"

    override fun getLabel(): String = strings.highlighting.getString()

    override fun action(editorActionContext: EditorActionContext) {}

    override fun getIcon(): Icon = Icon.DrawableRes(drawables.edit_note)

    override val childCommands: List<Command> by lazy {
        FileTypeManager.allTypes()
            .filter { it.textmateScope != null }
            .map { fileType ->
                object : EditorCommand(commandContext) {
                    override val id: String = "editor.syntax_highlighting.${fileType.name.lowercase()}"

                    override fun getLabel(): String = fileType.title

                    override fun action(editorActionContext: EditorActionContext) {
                        editorActionContext.editorTab.editorState.textmateScope = fileType.textmateScope!!
                    }

                    override fun getIcon(): Icon = fileType.getIcon()
                }
            }
    }

    override fun getChildSearchPlaceholder(): String = strings.select_language.getString()
}
