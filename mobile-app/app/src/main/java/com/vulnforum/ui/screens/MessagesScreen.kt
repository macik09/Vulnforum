package com.vulnforum.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.vulnforum.data.Message
import com.vulnforum.network.ApiClient
import com.vulnforum.network.MessageService
import com.vulnforum.ui.messages.MessagesViewModel
import com.vulnforum.ui.messages.MessagesViewModelFactory
import com.vulnforum.ui.theme.AppBackground
import com.vulnforum.util.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(navController: NavController, onComposeClick: () -> Unit) {
    AppBackground {
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
            containerColor = Color.Transparent, // <- to jest kluczowe
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Wiadomości",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Wróć")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onComposeClick,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Nowa wiadomość",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        ) { paddingValues ->
            val filteredMessages = messages.filter {
                it.sender == username || it.recipient == username
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(vertical = 8.dp)
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
}
@Composable
fun MessageItem(message: Message, expanded: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() }
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = message.sender.take(1).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Od: ${message.sender}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = message.timestamp,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Zwiń wiadomość" else "Rozwiń wiadomość",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            AnimatedVisibility(visible = expanded) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (!expanded) {
                Text(
                    text = message.content.take(60) + if (message.content.length > 60) "..." else "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}



