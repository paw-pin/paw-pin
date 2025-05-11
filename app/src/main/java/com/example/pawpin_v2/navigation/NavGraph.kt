package com.example.pawpin_v2.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.pawpin_v2.screens.walks.WalksScreen
import com.example.pawpin_v2.screens.map.MapScreen
import com.example.pawpin_v2.screens.profile.CombinedProfileScreen
import com.example.pawpin_v2.screens.community.CommunityScreen
import com.example.pawpin_v2.screens.requests.RequestsScreen
import com.example.pawpin_v2.ui.ChooseRoleScreen
import com.example.pawpin_v2.ui.UserRole

sealed class Screen(val route: String) {
    object Map : Screen("map")
    object History : Screen("history")
    object Profile : Screen("profile")
    object Ratings : Screen("ratings")
    object Requests : Screen("requests")
    object RoleChooser : Screen("roleChooser")

}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Map.route
    ) {
        composable(Screen.Map.route) {
            MapScreen()
        }
        composable(Screen.History.route) {
            WalksScreen()
        }
        composable(Screen.Profile.route) {
            CombinedProfileScreen()
        }
        composable(Screen.Ratings.route) {
            CommunityScreen()
        }
        composable(Screen.Requests.route) {
            RequestsScreen()
        }

        composable(Screen.RoleChooser.route) {
            ChooseRoleScreen { selectedRole ->
                // Navigate based on role selection
                val route = when (selectedRole) {
                    UserRole.DogOwner -> Screen.Map.route
                    UserRole.DogWalker -> Screen.Map.route
                }
                navController.navigate(route) {
                    popUpTo(Screen.RoleChooser.route) { inclusive = true }
                }
            }
        }

    }
}
