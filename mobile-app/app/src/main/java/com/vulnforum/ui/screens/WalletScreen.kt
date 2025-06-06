package com.vulnforum.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.vulnforum.ui.wallet.WalletViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(navController: NavController, walletViewModel: WalletViewModel = viewModel()) {
    val wallet by walletViewModel.wallet.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Twój portfel") },
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
            Text("Saldo: ${wallet.balance} vulndolców", style = MaterialTheme.typography.titleLarge)

            Button(onClick = {
                // Tutaj podłączysz API płatności (np. dialog z wyborem kwoty itp.)
                walletViewModel.purchaseVulndolcs(5)
            }) {
                Text("Kup 5 vulndolców (demo)")
            }
        }
    }
}
