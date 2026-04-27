package com.scto.mcs.core.runner.runners.web.html

import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

import com.scto.mcs.core.files.BuiltinFileType
import com.scto.mcs.core.files.FileObject
import com.scto.mcs.core.ui.icons.Icon
import com.scto.mcs.core.resources.getFilledString
import com.scto.mcs.core.resources.getString
import com.scto.mcs.core.resources.strings
import com.scto.mcs.core.runner.RunnerImpl
import com.scto.mcs.core.runner.runners.web.HttpServer
import com.scto.mcs.feature.settings.Settings
import com.scto.mcs.core.utils.toast

import java.net.BindException

class HtmlRunner : RunnerImpl() {
    companion object {
        var httpServer: HttpServer? = null
    }

    override suspend fun run(context: Context, fileObject: FileObject) {
        stop()

        val port = Settings.http_server_port
        try {
            httpServer = HttpServer(context, port, fileObject.getParentFile() ?: fileObject)
        } catch (_: BindException) {
            toast(strings.http_server_port_error.getFilledString(port))
            return
        }

        val address = "http://localhost:$port"
        toast(strings.http_server_at.getFilledString(address))

        val url = "$address/${fileObject.getName()}"
        if (Settings.launch_in_browser) {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            context.startActivity(intent)
            return
        }
        CustomTabsIntent.Builder()
            .setShowTitle(true)
            .setShareState(CustomTabsIntent.SHARE_STATE_OFF)
            .build()
            .launchUrl(context, url.toUri())
    }

    override fun getName(): String {
        return strings.html_preview.getString()
    }

    override fun getIcon(context: Context): Icon {
        return Icon.DrawableRes(BuiltinFileType.HTML.icon!!)
    }

    override suspend fun isRunning(): Boolean = httpServer?.isAlive == true

    override suspend fun stop() {
        if (isRunning()) {
            httpServer?.closeAllConnections()
            httpServer?.stop()
        }
        httpServer = null
    }
}
