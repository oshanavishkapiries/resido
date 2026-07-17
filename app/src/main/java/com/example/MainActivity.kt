package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.ForgotPasswordScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SignupScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.PropertyListingScreen
import com.example.ui.screens.PropertyDetailScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.AddPropertyScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.MyPropertiesScreen
import com.example.ui.screens.StaticTextScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          val navController = rememberNavController()
          NavHost(
            navController = navController,
            startDestination = "splash"
          ) {
            composable("splash") { SplashScreen(navController) }
            composable("onboarding") { OnboardingScreen(navController) }
            composable("login") { LoginScreen(navController) }
            composable("signup") { SignupScreen(navController) }
            composable("forgot_password") { ForgotPasswordScreen(navController) }
            composable("home") { HomeScreen(navController) }
            
            // Property Listing Screen Route
            composable(
              route = "property_listing?city={city}&category={category}&type={type}",
              arguments = listOf(
                navArgument("city") { defaultValue = "All"; type = NavType.StringType },
                navArgument("category") { defaultValue = "All"; type = NavType.StringType },
                navArgument("type") { defaultValue = "All"; type = NavType.StringType }
              )
            ) { backStackEntry ->
              val city = backStackEntry.arguments?.getString("city") ?: "All"
              val category = backStackEntry.arguments?.getString("category") ?: "All"
              val type = backStackEntry.arguments?.getString("type") ?: "All"
              PropertyListingScreen(
                navController = navController,
                initialCity = city,
                initialCategory = category,
                initialType = type
              )
            }

            // Property Details Route
            composable(
              route = "property_details/{propertyId}",
              arguments = listOf(navArgument("propertyId") { type = NavType.StringType })
            ) { backStackEntry ->
              val propertyId = backStackEntry.arguments?.getString("propertyId")
              PropertyDetailScreen(navController = navController, propertyId = propertyId)
            }

            // Notifications Route
            composable("notifications") { NotificationsScreen(navController) }

            // Add/Edit Property Route
            composable(
              route = "add_property?propertyId={propertyId}",
              arguments = listOf(
                navArgument("propertyId") { nullable = true; defaultValue = null; type = NavType.StringType }
              )
            ) { backStackEntry ->
              val propertyId = backStackEntry.arguments?.getString("propertyId")
              AddPropertyScreen(navController = navController, propertyId = propertyId)
            }

            // Favorites Route
            composable("favorites") { FavoritesScreen(navController) }

            // My Properties Route
            composable("my_properties") { MyPropertiesScreen(navController) }

            // Chat Route
            composable(
              route = "chat/{conversationId}",
              arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
            ) { backStackEntry ->
              val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
              ChatScreen(navController = navController, conversationId = conversationId)
            }

            // Static Doc Routes
            composable("privacy_policy") { StaticTextScreen(navController, "Privacy Policy", "privacy") }
            composable("terms") { StaticTextScreen(navController, "Terms & Conditions", "terms") }
            composable("support") { StaticTextScreen(navController, "Need Help? - Support", "support") }
          }
        }
      }
    }
  }
}

