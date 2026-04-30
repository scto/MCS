package com.scto.mcs.core.terminal.session

import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.scto.mcs.core.terminal.config.TerminalConfig
import com.scto.mcs.core.terminalxed.virtualkeys.SpecialButton
import com.scto.mcs.core.terminalxed.virtualkeys.VirtualKeysView
import com.scto.mcs.feature.settings.Settings
import com.termux.terminal.TerminalEmulator
import com.termux.terminal.TerminalSession
import com.termux.terminal.TerminalSessionClient
import com.termux.view.TerminalView
import com.termux.view.TerminalViewClient
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TerminalClientImpl @Inject constructor(
    private val terminalConfig: TerminalConfig
) : TerminalSessionClient, TerminalViewClient {

    private var terminalView: WeakReference<TerminalView>? = null
    private var virtualKeysView: WeakReference<VirtualKeysView>? = null

    fun setTerminalView(view: TerminalView) {
        terminalView = WeakReference(view)
    }

    fun setVirtualKeysView(view: VirtualKeysView) {
        virtualKeysView = WeakReference(view)
    }

    // TerminalSessionClient implementation
    override fun onTextChanged(changedSession: TerminalSession) {
        terminalView?.get()?.onScreenUpdated()
    }

    override fun onTitleChanged(changedSession: TerminalSession) {}
    override fun onSessionFinished(finishedSession: TerminalSession) {}

    override fun onCopyTextToClipboard(session: TerminalSession, text: String) {
        ClipboardUtils.copyText("Terminal", text)
    }

    override fun onPasteTextFromClipboard(session: TerminalSession?) {
        val clip = ClipboardUtils.getText().toString()
        if (clip.isNotBlank()) {
            terminalView?.get()?.mEmulator?.paste(clip)
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
        val fontScale = scale.coerceIn(11f, 45f)
        terminalView?.get()?.setTextSize(fontScale.toInt())
        return fontScale
    }

    override fun onSingleTapUp(e: MotionEvent) {
        showSoftInput()
    }

    override fun shouldBackButtonBeMappedToEscape(): Boolean = false
    override fun shouldEnforceCharBasedInput(): Boolean = true
    override fun shouldUseCtrlSpaceWorkaround(): Boolean = true
    override fun isTerminalViewSelected(): Boolean = true
    override fun copyModeChanged(copyMode: Boolean) {}

    override fun onKeyDown(keyCode: Int, e: KeyEvent, session: TerminalSession): Boolean {
        // Basic key handling logic
        return false
    }

    override fun onKeyUp(keyCode: Int, e: KeyEvent): Boolean = false
    override fun onLongPress(event: MotionEvent): Boolean = false

    // Virtual Keys Support
    override fun readControlKey(): Boolean = virtualKeysView?.get()?.readSpecialButton(SpecialButton.CTRL, true) ?: false
    override fun readAltKey(): Boolean = virtualKeysView?.get()?.readSpecialButton(SpecialButton.ALT, true) ?: false
    override fun readShiftKey(): Boolean = virtualKeysView?.get()?.readSpecialButton(SpecialButton.SHIFT, true) ?: false
    override fun readFnKey(): Boolean = virtualKeysView?.get()?.readSpecialButton(SpecialButton.FN, true) ?: false

    override fun onCodePoint(codePoint: Int, ctrlDown: Boolean, session: TerminalSession): Boolean = false

    override fun onEmulatorSet() {
        terminalView?.get()?.setTerminalCursorBlinkerState(true, true)
    }

    private fun showSoftInput() {
        terminalView?.get()?.apply {
            requestFocus()
            KeyboardUtils.showSoftInput(this)
        }
    }
}
