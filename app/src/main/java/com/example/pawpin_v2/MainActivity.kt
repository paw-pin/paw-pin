package com.example.pawpin_v2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.pawpin_v2.screens.walks.WalksScreen
import com.example.pawpin_v2.screens.map.MapScreen
import com.example.pawpin_v2.screens.profile.CombinedProfileScreen
import com.example.pawpin_v2.screens.community.CommunityScreen
import com.example.pawpin_v2.screens.requests.RequestsScreen
import com.example.pawpin_v2.ui.theme.DogWalkerAppTheme

import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MailOutline
import com.example.pawpin_v2.ui.ChooseRoleScreen
import com.example.pawpin_v2.ui.UserRole

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DogWalkerAppTheme {
                var selectedRole by remember { mutableStateOf<UserRole?>(null) }

                if (selectedRole == null) {
                    ChooseRoleScreen { role -> selectedRole = role }
                } else {
                    val navController = rememberNavController()
                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                val navBackStackEntry by navController.currentBackStackEntryAsState()
                                val currentRoute = navBackStackEntry?.destination?.route
                                bottomNavItems.forEach { item ->
                                    NavigationBarItem(
                                        icon = { Icon(item.icon, contentDescription = item.label) },
                                        label = { Text(item.label) },
                                        selected = currentRoute == item.route,
                                        onClick = {
                                            navController.navigate(item.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Map.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(Screen.Map.route) {
                                MapScreen(isOwner = selectedRole == UserRole.DogOwner)
                            }
                            composable(Screen.History.route) {
                                WalksScreen(isOwner = selectedRole == UserRole.DogOwner)
                            }
                            composable(Screen.Profile.route) {
                                CombinedProfileScreen(isOwner = selectedRole == UserRole.DogOwner)
                            }
                            composable(Screen.Ratings.route) {
                                CommunityScreen(isOwner = selectedRole == UserRole.DogOwner)
                            }
                            composable(Screen.Requests.route) {
                                RequestsScreen(isOwner = selectedRole == UserRole.DogOwner)
                            }
                        }
                    }
                }
            }
        }

    }
}

private val bottomNavItems = listOf(
    Screen.Map, Screen.History, Screen.Profile, Screen.Ratings, Screen.Requests
)

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Map : Screen("map", "Map", Icons.Default.Map)
    object History : Screen("history", "Walks", Icons.Default.DirectionsWalk)
    object Profile : Screen("profile", "Profile", Icons.Default.AccountCircle)
    object Ratings : Screen("ratings", "Community", Icons.Default.People)
    object Requests : Screen("requests", "Requests", Icons.Default.Inbox)
}
