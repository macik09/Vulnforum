package com.vulnforum.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.vulnforum.data.Message
import com.vulnforum.network.ApiClient
import com.vulnforum.network.MessageService
import com.vulnforum.ui.messages.MessagesViewModel
import com.vulnforum.ui.messages.MessagesViewModelFactory
import com.vulnforum.util.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(navController: NavController, onComposeClick: () -> Unit) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val username = sessionManager.getUsername() ?: ""

    val messageService = remember {
        ApiClient.getClient(context).create(MessageService::class.java)
    }
    val factory = remember { MessagesViewModelFactory(messageService) }
    val viewModel: MessagesViewModel = viewModel(factory = factory)

    val messages by viewModel.messages.collectAsState()
    var expandedMessageId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadMessages()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wiadomości") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wróć")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onComposeClick) {
                Icon(Icons.Default.Add, contentDescription = "Nowa wiadomość")
            }
        }
    ) { paddingValues ->
        val filteredMessages = messages.filter {
            it.sender == username || it.recipient == username
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(filteredMessages, key = { it.id }) { message ->
                MessageItem(
                    message = message,
                    expanded = expandedMessageId == message.id,
                    onClick = {
                        expandedMessageId = if (expandedMessageId == message.id) null else message.id
                    }
                )
            }
        }
    }
}

@Composable
fun MessageItem(message: Message, expanded: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Od: ${message.sender}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = message.timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (expanded) message.content else message.content.take(40) + if (message.content.length > 40) "..." else "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )


        }
    }
}


