package com.idrolife.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.idrolife.app.presentation.screen.DetailDeviceScreen
import com.idrolife.app.presentation.screen.ForgotPasswordScreen
import com.idrolife.app.presentation.screen.IrrigationConfigNominalFlowScreen
import com.idrolife.app.presentation.screen.IrrigationConfigScreen
import com.idrolife.app.presentation.screen.IrrigationDeviceScreen
import com.idrolife.app.presentation.screen.LoginScreen
import com.idrolife.app.presentation.screen.MainScreen
import com.idrolife.app.presentation.screen.RegisterScreen
import com.idrolife.app.presentation.screen.SensorDeviceScreen
import com.idrolife.app.presentation.screen.SensorSoilMoistureScreen
import com.idrolife.app.presentation.screen.SplashScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition? =
            {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(400)
                )
            }

        val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition? =
            {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(400)
                )
            }

        val popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition? =
            {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }


        val popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition? =
            {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(350)
                )
            }

        composable(
            route = Screen.Splash.route,
            exitTransition = exitTransition,
            popExitTransition = popExitTransition,
            enterTransition = enterTransition,
            popEnterTransition = popEnterTransition
        ) {
            SplashScreen(navController)
        }

        composable(
            route = Screen.Login.route,
            exitTransition = exitTransition,
            popExitTransition = popExitTransition,
            enterTransition = enterTransition,
            popEnterTransition = popEnterTransition
        ) {
            LoginScreen(navController)
        }

        composable(
            route = Screen.Main.route,
            exitTransition = exitTransition,
            popExitTransition = popExitTransition,
            enterTransition = enterTransition,
            popEnterTransition = popEnterTransition
        ) {
            MainScreen(navController)
        }

        composable(
            route = Screen.Register.route,
            exitTransition = exitTransition,
            popExitTransition = popExitTransition,
            enterTransition = enterTransition,
            popEnterTransition = popEnterTransition
        ) {
            RegisterScreen(navController)
        }

        composable(
            route = Screen.ForgotPassword.route,
            exitTransition = exitTransition,
            popExitTransition = popExitTransition,
            enterTransition = enterTransition,
            popEnterTransition = popEnterTransition
        ) {
            ForgotPasswordScreen(navController)
        }

        composable(
            route = Screen.DetailDevice.route + "/{deviceID}/{deviceName}",
            arguments = listOf(
                navArgument("deviceID") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("deviceName") {
                    type = NavType.StringType
                    nullable = false
                },
            ),
            exitTransition = exitTransition,
            popExitTransition = popExitTransition,
            enterTransition = enterTransition,
            popEnterTransition = popEnterTransition
        ) {
            val deviceID = it.arguments?.getString("deviceID") ?: ""
            val deviceName = it.arguments?.getString("deviceName") ?: ""
            DetailDeviceScreen(navController, deviceID, deviceName)
        }

        composable(
            route = Screen.SensorDevice.route + "/{deviceID}",
            arguments = listOf(
                navArgument("deviceID") {
                    type = NavType.StringType
                    nullable = false
                },
            ),
            exitTransition = exitTransition,
            popExitTransition = popExitTransition,
            enterTransition = enterTransition,
            popEnterTransition = popEnterTransition
        ) {
            val deviceID = it.arguments?.getString("deviceID") ?: ""
            SensorDeviceScreen(navController, deviceID)
        }

        composable(
            route = Screen.SensorSoilMoisture.route + "/{deviceID}",
            arguments = listOf(
                navArgument("deviceID") {
                    type = NavType.StringType
                    nullable = false
                },
            ),
            exitTransition = exitTransition,
            popExitTransition = popExitTransition,
            enterTransition = enterTransition,
            popEnterTransition = popEnterTransition
        ) {
            val deviceID = it.arguments?.getString("deviceID") ?: ""
            SensorSoilMoistureScreen(navController, deviceID)
        }

        composable(
            route = Screen.IrrigationDevice.route + "/{deviceID}",
            arguments = listOf(
                navArgument("deviceID") {
                    type = NavType.StringType
                    nullable = false
                },
            ),
            exitTransition = exitTransition,
            popExitTransition = popExitTransition,
            enterTransition = enterTransition,
            popEnterTransition = popEnterTransition
        ) {
            val deviceID = it.arguments?.getString("deviceID") ?: ""
            IrrigationDeviceScreen(navController, deviceID)
        }

        composable(
            route = Screen.IrrigationConfig.route + "/{deviceID}",
            arguments = listOf(
                navArgument("deviceID") {
                    type = NavType.StringType
                    nullable = false
                },
            ),
            exitTransition = exitTransition,
            popExitTransition = popExitTransition,
            enterTransition = enterTransition,
            popEnterTransition = popEnterTransition
        ) {
            val deviceID = it.arguments?.getString("deviceID") ?: ""
            IrrigationConfigScreen(navController, deviceID)
        }

        composable(
            route = Screen.IrrigationConfigNominalFlow.route + "/{deviceID}",
            arguments = listOf(
                navArgument("deviceID") {
                    type = NavType.StringType
                    nullable = false
                },
            ),
            exitTransition = exitTransition,
            popExitTransition = popExitTransition,
            enterTransition = enterTransition,
            popEnterTransition = popEnterTransition
        ) {
            val deviceID = it.arguments?.getString("deviceID") ?: ""
            IrrigationConfigNominalFlowScreen(navController, deviceID)
        }
    }
}
