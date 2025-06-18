package com.vulnforum.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.vulnforum.network.ApiClient
import com.vulnforum.network.WalletService
import com.vulnforum.ui.theme.AppBackground
import com.vulnforum.ui.wallet.WalletViewModel
import com.vulnforum.ui.wallet.WalletViewModelFactory
import com.vulnforum.util.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    navController: NavController,
    walletViewModel: WalletViewModel = viewModel(
        factory = WalletViewModelFactory(
            ApiClient.getClient(LocalContext.current).create(WalletService::class.java)
        )
    )
) {
    val balance by walletViewModel.balance.collectAsState()
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val username = sessionManager.getUsername() ?: "Użytkownik"

    val currentOnResume by rememberUpdatedState(newValue = {
        walletViewModel.fetchBalance()
    })
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                currentOnResume()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    AppBackground {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Portfel $username",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Wróć",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = "Saldo",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(bottom = 48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = "Ikona portfela",
                        modifier = Modifier.size(120.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(24.dp))
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (balance == null) {
                            LoadingBalancePlaceholder(size = 100.dp)
                        } else {
                            Text(
                                text = "$balance",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "vulndolców",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                )
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        val uri = Uri.parse("vulnforum://addfunds?amount=1")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AddShoppingCart,
                        contentDescription = "Kup vulndolce",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Kup vulndolce",
                        style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onPrimary)
                    )
                }
            }
        }
    }
}
@Composable
fun LoadingBalancePlaceholder(size: Dp = 80.dp) {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha))
    )
}
