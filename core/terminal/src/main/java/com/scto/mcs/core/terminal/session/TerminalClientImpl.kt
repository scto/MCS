package com.scto.mcs.core.terminal.session

import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import com.blankj.utilcode.util.ClipboardUtils
import com.scto.mcs.core.terminal.config.TerminalConfig
import com.scto.mcs.feature.settings.Settings
import com.termux.terminal.TerminalEmulator
import com.termux.terminal.TerminalSession
import com.termux.terminal.TerminalSessionClient
import com.termux.view.TerminalViewClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TerminalClientImpl @Inject constructor(
    private val terminalConfig: TerminalConfig
) : TerminalSessionClient, TerminalViewClient {

    // TerminalSessionClient implementation
    override fun onTextChanged(changedSession: TerminalSession) {
        // TODO: Notify UI via ViewModel
    }

    override fun onTitleChanged(changedSession: TerminalSession) {}
    override fun onSessionFinished(finishedSession: TerminalSession) {}

    override fun onCopyTextToClipboard(session: TerminalSession, text: String) {
        ClipboardUtils.copyText("Terminal", text)
    }

    override fun onPasteTextFromClipboard(session: TerminalSession?) {
        val clip = ClipboardUtils.getText().toString()
        if (clip.isNotBlank()) {
            session?.emulator?.paste(clip)
        }
    }

    override fun onBell(session: TerminalSession) {}
    override fun onColorsChanged(session: TerminalSession) {}
    override fun onTerminalCursorStateChange(state: Boolean) {}
    override fun setTerminalShellPid(session: TerminalSession, pid: Int) {}

    override fun getTerminalCursorStyle(): Int = when (Settings.terminal_cursor_style) {
        "bar" -> TerminalEmulator.TERMINAL_CURSOR_STYLE_BAR
        "underline" -> TerminalEmulator.TERMINAL_CURSOR_STYLE_UNDERLINE
        else -> TerminalEmulator.TERMINAL_CURSOR_STYLE_BLOCK
    }

    // Logging
    override fun logError(t: String?, m: String?) = Log.e(t ?: "Terminal", m ?: "")
    override fun logWarn(t: String?, m: String?) = Log.w(t ?: "Terminal", m ?: "")
    override fun logInfo(t: String?, m: String?) = Log.i(t ?: "Terminal", m ?: "")
    override fun logDebug(t: String?, m: String?) = Log.d(t ?: "Terminal", m ?: "")
    override fun logVerbose(t: String?, m: String?) = Log.v(t ?: "Terminal", m ?: "")
    override fun logStackTraceWithMessage(t: String?, m: String?, e: Exception?) { Log.e(t, m); e?.printStackTrace() }
    override fun logStackTrace(t: String?, e: Exception?) { e?.printStackTrace() }

    // TerminalViewClient implementation
    override fun onScale(scale: Float): Float {
        // TODO: Handle scaling via ViewModel
        return scale
    }

    override fun onSingleTapUp(e: MotionEvent) {
        // TODO: Show soft input via ViewModel
    }

    override fun shouldBackButtonBeMappedToEscape(): Boolean = false
    override fun shouldEnforceCharBasedInput(): Boolean = true
    override fun shouldUseCtrlSpaceWorkaround(): Boolean = true
    override fun isTerminalViewSelected(): Boolean = true
    override fun copyModeChanged(copyMode: Boolean) {}

    override fun onKeyDown(keyCode: Int, e: KeyEvent, session: TerminalSession): Boolean {
        // TODO: Handle key events via ViewModel
        return false
    }

    override fun onKeyUp(keyCode: Int, e: KeyEvent): Boolean = false
    override fun onLongPress(event: MotionEvent): Boolean = false

    // Virtual Keys Support
    override fun readControlKey(): Boolean {
        // TODO: Reactive check via VirtualKeysViewModel
        return false
    }
    override fun readAltKey(): Boolean {
        // TODO: Reactive check via VirtualKeysViewModel
        return false
    }
    override fun readShiftKey(): Boolean {
        // TODO: Reactive check via VirtualKeysViewModel
        return false
    }
    override fun readFnKey(): Boolean {
        // TODO: Reactive check via VirtualKeysViewModel
        return false
    }

    override fun onCodePoint(codePoint: Int, ctrlDown: Boolean, session: TerminalSession): Boolean = false

    override fun onEmulatorSet() {
        // TODO: Set cursor blinker state
    }
}
