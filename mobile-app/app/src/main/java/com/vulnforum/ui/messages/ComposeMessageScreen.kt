package com.vulnforum.ui.messages

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.vulnforum.network.ApiClient
import com.vulnforum.network.MessageService
import com.vulnforum.ui.theme.AppBackground
import com.vulnforum.util.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeMessageScreen(
    navController: NavController,
) {
    val context = LocalContext.current
    val messageService = remember { ApiClient.getClient(context).create(MessageService::class.java) }
    val factory = remember { ComposeMessageViewModelFactory(messageService) }
    val viewModel: ComposeMessageViewModel = viewModel(factory = factory)
    val sendStatus by viewModel.sendStatus.collectAsState()

    var recipient by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    val sessionManager = SessionManager(context)
    val username = sessionManager.getUsername()
    AppBackground {
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = {
                        Text(
                            "New Message",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                OutlinedTextField(
                    value = recipient,
                    onValueChange = { recipient = it },
                    label = {
                        Text("Recipient", color = MaterialTheme.colorScheme.secondary)
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.secondary,
                        cursorColor = MaterialTheme.colorScheme.secondary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.secondary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = {
                        Text("Message content", color = MaterialTheme.colorScheme.secondary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    maxLines = 10,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.secondary,
                        cursorColor = MaterialTheme.colorScheme.secondary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.secondary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Button(
                    onClick = { viewModel.sendMessage(username ?: "", recipient, content) },
                    enabled = recipient.isNotBlank() && content.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Send", style = MaterialTheme.typography.titleMedium)
                }

                sendStatus?.let { status ->
                    Text(
                        text = status,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        }
    }
}
