package com.scto.mcs.core.commands

import com.scto.mcs.app.activities.MainActivity
import com.scto.mcs.core.commands.editor.CopyCommand
import com.scto.mcs.core.commands.editor.CutCommand
import com.scto.mcs.core.commands.editor.DuplicateLineCommand
import com.scto.mcs.core.commands.editor.EmulateKeyCommand
import com.scto.mcs.core.commands.editor.JumpToLineCommand
import com.scto.mcs.core.commands.editor.LowerCaseCommand
import com.scto.mcs.core.commands.editor.PasteCommand
import com.scto.mcs.core.commands.editor.RedoCommand
import com.scto.mcs.core.commands.editor.RefreshCommand
import com.scto.mcs.core.commands.editor.ReplaceCommand
import com.scto.mcs.core.commands.editor.RunCommand
import com.scto.mcs.core.commands.editor.SaveCommand
import com.scto.mcs.core.commands.editor.SearchCommand
import com.scto.mcs.core.commands.editor.SelectAllCommand
import com.scto.mcs.core.commands.editor.SelectWordCommand
import com.scto.mcs.core.commands.editor.ShareCommand
import com.scto.mcs.core.commands.editor.SyntaxHighlightingCommand
import com.scto.mcs.core.commands.editor.ToggleReadOnlyCommand
import com.scto.mcs.core.commands.editor.ToggleWordWrapCommand
import com.scto.mcs.core.commands.editor.UndoCommand
import com.scto.mcs.core.commands.editor.UpperCaseCommand
import com.scto.mcs.core.commands.global.CommandPaletteCommand
import com.scto.mcs.core.commands.global.DocumentationCommand
import com.scto.mcs.core.commands.global.NewFileCommand
import com.scto.mcs.core.commands.global.SaveAllCommand
import com.scto.mcs.core.commands.global.SearchCodeCommand
import com.scto.mcs.core.commands.global.SearchFileFolderCommand
import com.scto.mcs.core.commands.global.SettingsCommand
import com.scto.mcs.core.commands.global.TerminalCommand
import com.scto.mcs.core.commands.lsp.FormatDocumentCommand
import com.scto.mcs.core.commands.lsp.FormatSelectionCommand
import com.scto.mcs.core.commands.lsp.GoToDefinitionCommand
import com.scto.mcs.core.commands.lsp.GoToReferencesCommand
import com.scto.mcs.core.commands.lsp.RenameSymbolCommand

object CommandProvider {
    private val _commandList = mutableListOf<Command>()
    val commandList: List<Command>
        get() = _commandList.toList()

    lateinit var DocumentationCommand: DocumentationCommand
    lateinit var TerminalCommand: TerminalCommand
    lateinit var SettingsCommand: SettingsCommand
    lateinit var NewFileCommand: NewFileCommand
    lateinit var CommandPaletteCommand: CommandPaletteCommand
    lateinit var SearchFileFolderCommand: SearchFileFolderCommand
    lateinit var SearchCodeCommand: SearchCodeCommand
    lateinit var CutCommand: CutCommand
    lateinit var CopyCommand: CopyCommand
    lateinit var PasteCommand: PasteCommand
    lateinit var SelectAllCommand: SelectAllCommand
    lateinit var SelectWordCommand: SelectWordCommand
    lateinit var DuplicateLineCommand: DuplicateLineCommand
    lateinit var LowerCaseCommand: LowerCaseCommand
    lateinit var UpperCaseCommand: UpperCaseCommand
    lateinit var SaveCommand: SaveCommand
    lateinit var SaveAllCommand: SaveAllCommand
    lateinit var UndoCommand: UndoCommand
    lateinit var RedoCommand: RedoCommand
    lateinit var RunCommand: RunCommand
    lateinit var ToggleReadOnlyCommand: ToggleReadOnlyCommand
    lateinit var SearchCommand: SearchCommand
    lateinit var ReplaceCommand: ReplaceCommand
    lateinit var RefreshCommand: RefreshCommand
    lateinit var SyntaxHighlightingCommand: SyntaxHighlightingCommand
    lateinit var ToggleWordWrapCommand: ToggleWordWrapCommand
    lateinit var JumpToLineCommand: JumpToLineCommand
    lateinit var ShareCommand: ShareCommand
    lateinit var EmulateKeyCommand: EmulateKeyCommand
    lateinit var GoToDefinitionCommand: GoToDefinitionCommand
    lateinit var GoToReferencesCommand: GoToReferencesCommand
    lateinit var RenameSymbolCommand: RenameSymbolCommand
    lateinit var FormatDocumentCommand: FormatDocumentCommand
    lateinit var FormatSelectionCommand: FormatSelectionCommand

    fun buildCommands() =
        synchronized(this) {
            val commandContext = CommandContext { MainActivity.instance!!.viewModel }

            registerBuiltin(DocumentationCommand(commandContext)) { DocumentationCommand = it }
            registerBuiltin(TerminalCommand(commandContext)) { TerminalCommand = it }
            registerBuiltin(SettingsCommand(commandContext)) { SettingsCommand = it }
            registerBuiltin(NewFileCommand(commandContext)) { NewFileCommand = it }
            registerBuiltin(CommandPaletteCommand(commandContext)) { CommandPaletteCommand = it }
            registerBuiltin(SearchFileFolderCommand(commandContext)) { SearchFileFolderCommand = it }
            registerBuiltin(SearchCodeCommand(commandContext)) { SearchCodeCommand = it }
            registerBuiltin(CutCommand(commandContext)) { CutCommand = it }
            registerBuiltin(CopyCommand(commandContext)) { CopyCommand = it }
            registerBuiltin(PasteCommand(commandContext)) { PasteCommand = it }
            registerBuiltin(SelectAllCommand(commandContext)) { SelectAllCommand = it }
            registerBuiltin(SelectWordCommand(commandContext)) { SelectWordCommand = it }
            registerBuiltin(DuplicateLineCommand(commandContext)) { DuplicateLineCommand = it }
            registerBuiltin(LowerCaseCommand(commandContext)) { LowerCaseCommand = it }
            registerBuiltin(UpperCaseCommand(commandContext)) { UpperCaseCommand = it }
            registerBuiltin(SaveCommand(commandContext)) { SaveCommand = it }
            registerBuiltin(SaveAllCommand(commandContext)) { SaveAllCommand = it }
            registerBuiltin(UndoCommand(commandContext)) { UndoCommand = it }
            registerBuiltin(RedoCommand(commandContext)) { RedoCommand = it }
            registerBuiltin(RunCommand(commandContext)) { RunCommand = it }
            registerBuiltin(ToggleReadOnlyCommand(commandContext)) { ToggleReadOnlyCommand = it }
            registerBuiltin(SearchCommand(commandContext)) { SearchCommand = it }
            registerBuiltin(ReplaceCommand(commandContext)) { ReplaceCommand = it }
            registerBuiltin(RefreshCommand(commandContext)) { RefreshCommand = it }
            registerBuiltin(SyntaxHighlightingCommand(commandContext)) { SyntaxHighlightingCommand = it }
            registerBuiltin(ToggleWordWrapCommand(commandContext)) { ToggleWordWrapCommand = it }
            registerBuiltin(JumpToLineCommand(commandContext)) { JumpToLineCommand = it }
            registerBuiltin(ShareCommand(commandContext)) { ShareCommand = it }
            registerBuiltin(EmulateKeyCommand(commandContext)) { EmulateKeyCommand = it }
            registerBuiltin(GoToDefinitionCommand(commandContext)) { GoToDefinitionCommand = it }
            registerBuiltin(GoToReferencesCommand(commandContext)) { GoToReferencesCommand = it }
            registerBuiltin(RenameSymbolCommand(commandContext)) { RenameSymbolCommand = it }
            registerBuiltin(FormatDocumentCommand(commandContext)) { FormatDocumentCommand = it }
            registerBuiltin(FormatSelectionCommand(commandContext)) { FormatSelectionCommand = it }
        }

    private fun <T : Command> registerBuiltin(command: T, assign: (T) -> Unit) {
        if (_commandList.contains(command)) return
        assign(command)
        _commandList.add(command)
    }

    fun registerCommand(command: Command) {
        if (!_commandList.contains(command)) {
            _commandList.add(command)
        }
    }

    fun unregisterCommand(command: Command) {
        _commandList.remove(command)
    }

    fun getForId(id: String): Command? = findRecursive(id, commandList)

    fun getParentCommand(command: Command): Command? = findParent(command, commandList)

    private fun findParent(target: Command, commands: List<Command>): Command? {
        for (parent in commands) {
            val children = parent.childCommands
            if (children.any { it.id == target.id }) return parent

            val match = findParent(target, children)
            if (match != null) return match
        }
        return null
    }

    private fun findRecursive(id: String, commands: List<Command>): Command? {
        for (command in commands) {
            if (command.id == id) return command
            val children = command.childCommands

            val match = findRecursive(id, children)
            if (match != null) return match
        }
        return null
    }
}
