package com.vulnforum.ui.messages

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.rememberNavController
import com.vulnforum.ui.screens.MessagesScreen


class ExportedMessagesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContent {
            val navController = rememberNavController()
            MaterialTheme {
                MessagesScreen(
                    navController = navController,
                    onComposeClick = {

                    },

                )
            }
        }
    }
}