package com.scto.mcs.feature.setup

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scto.mcs.core.ui.theme.MCSTheme

@Composable
fun SetupScreen(
    viewModel: SetupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    MCSTheme {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(id = R.string.setup_title), style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            
            // JDK Selection
            Text(text = stringResource(id = R.string.setup_select_jdk))
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = uiState.jdkVersion == "17", onClick = { viewModel.setJdk("17") })
                Text(stringResource(id = R.string.setup_jdk_17))
                RadioButton(selected = uiState.jdkVersion == "21", onClick = { viewModel.setJdk("21") })
                Text(stringResource(id = R.string.setup_jdk_21))
            }

            // SDK Selection
            Text(text = stringResource(id = R.string.setup_select_sdk))
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = uiState.sdkVersion == "33", onClick = { viewModel.setSdk("33") })
                Text(stringResource(id = R.string.setup_sdk_33))
                RadioButton(selected = uiState.sdkVersion == "35", onClick = { viewModel.setSdk("35") })
                Text(stringResource(id = R.string.setup_sdk_35))
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            if (uiState.isInstalling) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = uiState.statusMessage)
            } else {
                Button(onClick = { viewModel.startSetup() }) {
                    Text(text = stringResource(id = R.string.setup_start))
                }
            }
        }
    }
}
