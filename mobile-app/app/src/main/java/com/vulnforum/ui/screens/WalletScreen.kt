package com.vulnforum.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.vulnforum.network.ApiClient
import com.vulnforum.network.WalletService
import com.vulnforum.ui.wallet.WalletViewModel
import com.vulnforum.ui.wallet.WalletViewModelFactory
import com.vulnforum.util.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    navController: NavController,
    walletViewModel: WalletViewModel = viewModel(
        factory = WalletViewModelFactory(
            // Musisz dostarczyć WalletService do ViewModelFactory
            ApiClient.getClient(LocalContext.current).create(WalletService::class.java)
        )
    )
) {
    val balance by walletViewModel.balance.collectAsState() // Obserwuj StateFlow z ViewModelu
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val username = sessionManager.getUsername() // Username może być wciąż z SessionManager

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Portfel $username") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wróć")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Wyświetl saldo, obsłuż przypadek gdy jest jeszcze null
            Text("Saldo: ${balance ?: "Ładowanie..."} vulndolców", style = MaterialTheme.typography.titleLarge)

            Button(onClick = {
                walletViewModel.addFunds(5f) // Wywołaj funkcję dodawania środków w ViewModelu
            }) {
                Text("Kup 5 vulndolców")
            }
        }
    }
}
