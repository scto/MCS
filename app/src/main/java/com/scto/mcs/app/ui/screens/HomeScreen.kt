package com.scto.mcs.app.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

class HomeViewModel: ViewModel()

@Composable
fun HomeScreen(vm: HomeViewModel = viewModel()){
    Text("Home Screen")
}