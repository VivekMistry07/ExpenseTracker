package com.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.expensetracker.ui.screens.AddTransactionScreen
import com.expensetracker.ui.screens.CategoryScreen
import com.expensetracker.ui.screens.DashboardScreen
import com.expensetracker.ui.screens.ExportScreen
import com.expensetracker.ui.theme.ExpenseTrackerTheme
import com.expensetracker.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExpenseTrackerTheme {
                ExpenseTrackerApp()
            }
        }
    }
}

@Composable
private fun ExpenseTrackerApp() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "dashboard",
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable("dashboard") {
            DashboardScreen(
                viewModel = viewModel,
                onAddTransaction = { navController.navigate("add_transaction") },
                onManageCategories = { navController.navigate("categories") },
                onExport = { navController.navigate("export") }
            )
        }

        composable("add_transaction") {
            AddTransactionScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("categories") {
            CategoryScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("export") {
            ExportScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
