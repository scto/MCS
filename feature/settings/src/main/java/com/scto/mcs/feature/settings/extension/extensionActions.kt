package com.scto.mcs.feature.settings.extension

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.scto.mcs.app.App
import com.scto.mcs.app.ui.activities.settings.SettingsActivity
import com.scto.mcs.core.extensions.Extension
import com.scto.mcs.core.extensions.ExtensionError
import com.scto.mcs.core.extensions.InstallResult
import com.scto.mcs.core.extensions.LocalExtension
import com.scto.mcs.core.extensions.StoreExtension
import com.scto.mcs.core.extensions.installExtensionFromZip
import com.scto.mcs.core.extensions.load
import com.scto.mcs.core.files.toFileObject
import com.scto.mcs.core.resources.getFilledString
import com.scto.mcs.core.resources.getString
import com.scto.mcs.core.resources.strings
import com.scto.mcs.core.utils.LoadingPopup
import com.scto.mcs.core.utils.application
import com.scto.mcs.core.utils.dialog
import com.scto.mcs.core.utils.errorDialog
import com.scto.mcs.core.utils.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException

suspend fun runExtensionUninstallAction(
    extension: Extension,
    updateInstallState: (InstallState) -> Unit,
    activity: AppCompatActivity?,
) {
    App.extensionManager.uninstallExtension(extension.id).onFailure {
        errorDialog(it, activity)
        return
    }
    updateInstallState(InstallState.Idle)
}

suspend fun runExtensionInstallAction(
    extension: Extension,
    updateInstallState: (InstallState) -> Unit,
    scope: CoroutineScope,
    context: Context,
    activity: AppCompatActivity?,
) {
    updateInstallState(InstallState.Installing)
    var loading: LoadingPopup? = null

    runCatching {
            val extension = extension as? StoreExtension ?: return

            loading = LoadingPopup(activity).show()
            loading.setMessage(strings.installing.getString())

            val result =
                App.extensionManager.installStoreExtension(context, extension).getOrElse {
                    loading.hide()
                    errorDialog(it.message ?: strings.unknown_error.getString(), activity)
                    updateInstallState(InstallState.Idle)
                    return@runCatching
                }

            handleInstallResult(result, activity, { updateInstallState(InstallState.Idle) }) { ext ->
                updateInstallState(InstallState.Installed)

                scope.launch(Dispatchers.Default) {
                    ext.load(application!!).onFailure {
                        errorDialog(it.message ?: strings.unknown_error.getString(), activity)
                    }
                }
            }
            loading.hide()
        }
        .onFailure {
            loading?.hide()
            errorDialog(it, activity)
            updateInstallState(InstallState.Idle)
        }
}

fun installExtensionFromUri(scope: CoroutineScope, uri: Uri?, activity: AppCompatActivity?) {
    var loading: LoadingPopup? = null

    scope.launch(Dispatchers.IO) {
        runCatching {
                if (uri == null) return@runCatching

                val fileObject = uri.toFileObject(expectedIsFile = true)
                val exists = fileObject.exists()
                val canRead = fileObject.canRead()
                val isZip = fileObject.getName().endsWith(".zip")

                if (exists && canRead && isZip) {
                    withContext(Dispatchers.Main) {
                        loading = LoadingPopup(activity).show()
                        loading.setMessage(strings.installing.getString())
                    }

                    val result = App.extensionManager.installExtensionFromZip(fileObject)

                    withContext(Dispatchers.Main) {
                        handleInstallResult(result, activity) { ext ->
                            scope.launch(Dispatchers.Default) {
                                ext.load(application!!).onFailure {
                                    errorDialog(it.message ?: strings.unknown_error.getString(), activity)
                                }
                            }
                        }

                        loading?.hide()
                    }
                } else {
                    errorDialog(
                        "Install criteria failed \nis_zip = $isZip\ncan_read = $canRead\n exists = $exists\nuri = ${fileObject.getAbsolutePath()}",
                        activity,
                    )
                }
            }
            .onFailure {
                loading?.hide()
                errorDialog(it, activity)
            }
    }
}

@OptIn(ExperimentalSerializationApi::class)
private fun handleInstallResult(
    result: InstallResult,
    activity: Activity?,
    onError: () -> Unit = {},
    onSuccess: (LocalExtension) -> Unit = {},
) =
    when (result) {
        is InstallResult.AlreadyInstalled -> {
            //            errorDialog("Extension already installed", activity)
        }

        is InstallResult.Error -> {
            when (result.error) {
                ExtensionError.OUTDATED_CLIENT ->
                    errorDialog(strings.outdated_client.getString(), activity, strings.install_failed.getString())
                ExtensionError.OUTDATED_EXTENSION ->
                    errorDialog(strings.outdated_extension.getString(), activity, strings.install_failed.getString())
            }
            onError()
        }

        is InstallResult.Success -> {
            toast(strings.installed)
            onSuccess(result.extension)
        }

        is InstallResult.ValidationFailed -> {
            val e = result.error
            if (e is MissingFieldException) {
                val fields = e.missingFields.joinToString("\n") { "• $it" }
                dialog(
                    SettingsActivity.instance,
                    strings.extension_validation_failed.getString(),
                    strings.manifest_missing_fields.getFilledString(fields),
                    cancelable = false,
                )
                onError()
            } else {
                errorDialog(
                    e?.localizedMessage ?: strings.unknown_error.getString(),
                    activity,
                    strings.extension_validation_failed.getString(),
                )
                onError()
            }
        }
    }
