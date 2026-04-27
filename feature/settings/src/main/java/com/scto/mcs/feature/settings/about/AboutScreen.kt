package com.scto.mcs.feature.settings.about

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.pm.PackageInfoCompat
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.scto.mcs.core.ui.components.compose.preferences.base.SettingsToggle
import com.scto.mcs.core.ui.components.compose.preferences.base.PreferenceGroup
import com.scto.mcs.core.ui.components.compose.preferences.base.PreferenceLayout
import com.scto.mcs.core.ui.components.compose.preferences.base.PreferenceTemplate
import com.scto.mcs.core.resources.drawables
import com.scto.mcs.core.resources.getString
import com.scto.mcs.core.resources.strings
import com.scto.mcs.core.utils.copyToClipboard
import com.scto.mcs.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AboutScreen() {
    val context = LocalContext.current
    val pm = context.packageManager
    val appIcon = pm.getApplicationIcon(context.packageName)
    val packageInfo = pm.getPackageInfo(context.packageName, 0)
    val versionName = packageInfo.versionName
    val versionCode = PackageInfoCompat.getLongVersionCode(packageInfo)

    PreferenceLayout(label = stringResource(id = strings.about), backArrowVisible = true) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = appIcon,
                contentDescription = null,
                modifier = Modifier.size(64.dp).padding(bottom = 8.dp),
            )

            Text(
                text = stringResource(strings.app_name),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = versionName.toString().uppercase(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        PreferenceGroup(heading = stringResource(strings.build_info)) {
            PreferenceTemplate(
                modifier =
                    Modifier.combinedClickable(
                        enabled = true,
                        onClick = {},
                        onLongClick = { copyToClipboard(versionName.toString()) },
                    ),
                title = {
                    Text(text = stringResource(id = strings.version), style = MaterialTheme.typography.titleMedium)
                },
                description = { Text(text = versionName.toString(), style = MaterialTheme.typography.titleSmall) },
            )

            PreferenceTemplate(
                modifier =
                    Modifier.combinedClickable(
                        enabled = true,
                        onClick = {},
                        onLongClick = { copyToClipboard(versionCode.toString()) },
                    ),
                title = {
                    Text(text = stringResource(id = strings.version_code), style = MaterialTheme.typography.titleMedium)
                },
                description = { Text(text = versionCode.toString(), style = MaterialTheme.typography.titleSmall) },
            )
        }
    }
}
