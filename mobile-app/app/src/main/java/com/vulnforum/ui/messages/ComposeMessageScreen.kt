package com.vulnforum.ui.messages

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
import com.vulnforum.network.MessageService
import com.vulnforum.util.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeMessageScreen(
    navController: NavController,

) {

    val context = LocalContext.current
    val messageService = remember {
        ApiClient.getClient(context).create(MessageService::class.java)
    }
    val factory = remember { ComposeMessageViewModelFactory(messageService) }
    val viewModel: ComposeMessageViewModel = viewModel(factory = factory)

    val sendStatus by viewModel.sendStatus.collectAsState()

    var recipient by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    val sessionManager = SessionManager(context)
    val username = sessionManager.getUsername()

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Nowa wiadomość") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Powrót")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = recipient,
                onValueChange = { recipient = it },
                label = { Text("Odbiorca") },
                singleLine = true
            )
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Treść wiadomości") },
                modifier = Modifier.height(150.dp),
                maxLines = 10
            )

            Button(
                onClick = {
                    viewModel.sendMessage(username.toString(), recipient, content)
                },
                enabled = recipient.isNotBlank() && content.isNotBlank()
            ) {
                Text("Wyślij")
            }

            sendStatus?.let { status ->
                Text(text = status, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
