package com.scto.mcs.feature.terminal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.scto.mcs.core.terminal.session.TerminalClientImpl
import com.scto.mcs.core.terminalxed.virtualkeys.VirtualKeysView
import com.termux.view.TerminalView

@Composable
fun TerminalScreen(
    viewModel: TerminalViewModel = hiltViewModel(),
    terminalClient: TerminalClientImpl
) {
    val sessions by viewModel.sessions.collectAsState()
    val activeSession by viewModel.activeSession.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Sessions", modifier = Modifier.padding(16.dp))
                sessions.forEach { session ->
                    NavigationDrawerItem(
                        label = { Text(session.mHandle) },
                        selected = session == activeSession,
                        onClick = { 
                            viewModel.switchSession(session.mHandle)
                        }
                    )
                }
                Button(onClick = { viewModel.createNewSession() }) {
                    Text("Neue Session")
                }
            }
        }
    ) {
        Scaffold { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                // Terminal View
                Box(modifier = Modifier.weight(1f)) {
                    activeSession?.let { session ->
                        AndroidView(
                            factory = { context ->
                                TerminalView(context, null).apply {
                                    attachSession(session)
                                    terminalClient.setTerminalView(this)
                                }
                            },
                            update = { view ->
                                view.attachSession(session)
                            }
                        )
                    }
                }

                // Virtual Keys Pager
                val pagerState = rememberPagerState(pageCount = { 2 })
                HorizontalPager(state = pagerState, modifier = Modifier.height(100.dp)) { page ->
                    AndroidView(
                        factory = { context ->
                            VirtualKeysView(context).apply {
                                terminalClient.setVirtualKeysView(this)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
