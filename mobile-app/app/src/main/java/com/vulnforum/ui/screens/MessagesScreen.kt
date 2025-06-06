package com.vulnforum.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.vulnforum.data.Message
import com.vulnforum.ui.messages.MessagesViewModel

@Composable
fun MessagesScreen(
    navController: NavController,
    messagesViewModel: MessagesViewModel = viewModel(),
    onComposeClick: () -> Unit
) {
    val messages by messagesViewModel.messages.collectAsState()

    var expandedMessageId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        messagesViewModel.loadMessages()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onComposeClick, modifier = Modifier.padding(16.dp)) {
                Icon(Icons.Default.Add, contentDescription = "Nowa wiadomość")
            }
        },
        floatingActionButtonPosition = FabPosition.Start
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues
        ) {
            items(messages) { message ->
                MessageItem(
                    message = message,
                    expanded = expandedMessageId == message.id,
                    onClick = {
                        expandedMessageId = if (expandedMessageId == message.id) null else message.id
                    }
                )
                Divider()
            }
        }
    }
}

@Composable
fun MessageItem(message: Message, expanded: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(text = "Od: ${message.sender}", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (expanded) message.content else message.content.take(30) + if (message.content.length > 30) "..." else "",
            style = MaterialTheme.typography.bodyMedium
        )
        if (expanded) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Otrzymano: ${message.timestamp}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
