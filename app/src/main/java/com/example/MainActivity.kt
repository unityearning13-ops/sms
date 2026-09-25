package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.BkashConversationScreen
import com.example.ui.screens.MessagesHomeScreen
import com.example.ui.screens.UnityEarningPortalScreen
import com.example.ui.screens.WebPortalScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: MainViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "messages_home") {
        composable("messages_home") {
            MessagesHomeScreen(
                viewModel = viewModel,
                onOpenBkashChat = { navController.navigate("bkash_chat") },
                onOpenPortal = { navController.navigate("portal") },
                onOpenWebPortal = { navController.navigate("web_portal") }
            )
        }
        composable("bkash_chat") {
            BkashConversationScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("portal") {
            UnityEarningPortalScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToBkash = { navController.navigate("bkash_chat") }
            )
        }
        composable("web_portal") {
            WebPortalScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToBkash = { navController.navigate("bkash_chat") }
            )
        }
    }
}
