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
import com.idrolife.app.presentation.screen.CreatePlantScreen
import com.idrolife.app.presentation.screen.DetailDeviceScreen
import com.idrolife.app.presentation.screen.FertigationDeviceScreen
import com.idrolife.app.presentation.screen.FertigationProgrammationECSettingScreen
import com.idrolife.app.presentation.screen.FertigationProgrammationScreen
import com.idrolife.app.presentation.screen.FertigationStatusScreen
import com.idrolife.app.presentation.screen.ForgotPasswordScreen
import com.idrolife.app.presentation.screen.IrrigationConfigAdvanceConfigScreen
import com.idrolife.app.presentation.screen.IrrigationConfigEVConfigScreen
import com.idrolife.app.presentation.screen.IrrigationConfigEVRadioStatusScreen
import com.idrolife.app.presentation.screen.IrrigationConfigGeneralSettingScreen
import com.idrolife.app.presentation.screen.IrrigationConfigNominalFlowScreen
import com.idrolife.app.presentation.screen.IrrigationConfigScreen
import com.idrolife.app.presentation.screen.IrrigationDeviceScreen
import com.idrolife.app.presentation.screen.IrrigationSettingGeneralParameterScreen
import com.idrolife.app.presentation.screen.IrrigationSettingScreen
import com.idrolife.app.presentation.screen.IrrigationSettingSensorManagementScreen
import com.idrolife.app.presentation.screen.IrrigationStatusIdrosatStatusScreen
import com.idrolife.app.presentation.screen.IrrigationStatusProgramStatusScreen
import com.idrolife.app.presentation.screen.IrrigationStatusScreen
import com.idrolife.app.presentation.screen.IrrigationStatusStationStatusScreen
import com.idrolife.app.presentation.screen.LoginScreen
import com.idrolife.app.presentation.screen.MainScreen
import com.idrolife.app.presentation.screen.ManualEVStartScreen
import com.idrolife.app.presentation.screen.ManualProgramStartScreen
import com.idrolife.app.presentation.screen.MapScreen
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
            route = Screen.DetailDevice.route + "/{deviceID}/{deviceName}/{deviceCode}",
            arguments = listOf(
                navArgument("deviceID") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("deviceName") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("deviceCode") {
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
            val deviceCode = it.arguments?.getString("deviceCode") ?: ""
            DetailDeviceScreen(navController, deviceID, deviceName, deviceCode)
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
            route = Screen.IrrigationDevice.route + "/{deviceID}/{deviceCode}",
            arguments = listOf(
                navArgument("deviceID") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("deviceCode") {
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
            val deviceCode = it.arguments?.getString("deviceCode") ?: ""
            IrrigationDeviceScreen(navController, deviceID, deviceCode)
        }

        composable(
            route = Screen.IrrigationConfig.route + "/{deviceID}/{deviceCode}",
            arguments = listOf(
                navArgument("deviceID") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("deviceCode") {
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
            val deviceCode = it.arguments?.getString("deviceCode") ?: ""
            IrrigationConfigScreen(navController, deviceID, deviceCode)
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

        composable(
            route = Screen.IrrigationConfigGeneralSetting.route + "/{deviceID}",
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
            IrrigationConfigGeneralSettingScreen(navController, deviceID)
        }

        composable(
            route = Screen.IrrigationConfigAdvanceConfig.route + "/{deviceID}",
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
            IrrigationConfigAdvanceConfigScreen(navController, deviceID)
        }

        composable(
            route = Screen.IrrigationConfigEVRadioStatus.route + "/{deviceID}",
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
            IrrigationConfigEVRadioStatusScreen(navController, deviceID)
        }

        composable(
            route = Screen.IrrigationConfigEVConfig.route + "/{deviceID}/{deviceCode}",
            arguments = listOf(
                navArgument("deviceID") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("deviceCode") {
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
            val deviceCode = it.arguments?.getString("deviceCode") ?: ""
            IrrigationConfigEVConfigScreen(navController, deviceID, deviceCode)
        }

        composable(
            route = Screen.IrrigationSetting.route + "/{deviceID}/{deviceCode}",
            arguments = listOf(
                navArgument("deviceID") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("deviceCode") {
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
            val deviceCode = it.arguments?.getString("deviceCode") ?: ""
            IrrigationSettingScreen(navController, deviceID, deviceCode)
        }

        composable(
            route = Screen.IrrigationSettingGeneralParameter.route + "/{deviceID}/{deviceCode}",
            arguments = listOf(
                navArgument("deviceID") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("deviceCode") {
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
            val deviceCode = it.arguments?.getString("deviceCode") ?: ""
            IrrigationSettingGeneralParameterScreen(navController, deviceID, deviceCode)
        }

        composable(
            route = Screen.CreatePlant.route,
            arguments = listOf(),
            exitTransition = exitTransition,
            popExitTransition = popExitTransition,
            enterTransition = enterTransition,
            popEnterTransition = popEnterTransition
        ) {
            CreatePlantScreen(navController)
        }

        composable(
            route = Screen.IrrigationSettingSensorManagement.route + "/{deviceID}/{deviceCode}",
            arguments = listOf(
                navArgument("deviceID") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("deviceCode") {
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
            val deviceCode = it.arguments?.getString("deviceCode") ?: ""
            IrrigationSettingSensorManagementScreen(navController, deviceID, deviceCode)
        }

        composable(
            route = Screen.IrrigationStatus.route + "/{deviceID}/{deviceCode}",
            arguments = listOf(
                navArgument("deviceID") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("deviceCode") {
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
            val deviceCode = it.arguments?.getString("deviceCode") ?: ""
            IrrigationStatusScreen(navController, deviceID, deviceCode)
        }

        composable(
            route = Screen.IrrigationStatusProgramStatus.route + "/{deviceID}/{deviceCode}",
            arguments = listOf(
                navArgument("deviceID") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("deviceCode") {
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
            val deviceCode = it.arguments?.getString("deviceCode") ?: ""
            IrrigationStatusProgramStatusScreen(navController, deviceID, deviceCode)
        }

        composable(
            route = Screen.IrrigationStatusStationStatus.route + "/{deviceID}/{deviceCode}",
            arguments = listOf(
                navArgument("deviceID") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("deviceCode") {
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
            val deviceCode = it.arguments?.getString("deviceCode") ?: ""
            IrrigationStatusStationStatusScreen(navController, deviceID, deviceCode)
        }

        composable(
            route = Screen.IrrigationStatusIdrosatStatus.route + "/{deviceID}/{deviceCode}",
            arguments = listOf(
                navArgument("deviceID") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("deviceCode") {
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
            val deviceCode = it.arguments?.getString("deviceCode") ?: ""
            IrrigationStatusIdrosatStatusScreen(navController, deviceID, deviceCode)
        }

        composable(
            route = Screen.ManualEVStart.route + "/{deviceID}/{deviceCode}",
            arguments = listOf(
                navArgument("deviceID") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("deviceCode") {
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
            val deviceCode = it.arguments?.getString("deviceCode") ?: ""
            ManualEVStartScreen(navController, deviceID, deviceCode)
        }

        composable(
            route = Screen.ManualProgramStart.route + "/{deviceID}/{deviceCode}",
            arguments = listOf(
                navArgument("deviceID") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("deviceCode") {
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
            val deviceCode = it.arguments?.getString("deviceCode") ?: ""
            ManualProgramStartScreen(navController, deviceID, deviceCode)
        }

        composable(
            route = Screen.FertigationDevice.route + "/{deviceID}/{deviceCode}",
            arguments = listOf(
                navArgument("deviceID") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("deviceCode") {
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
            val deviceCode = it.arguments?.getString("deviceCode") ?: ""
            FertigationDeviceScreen(navController, deviceID, deviceCode)
        }

        composable(
            route = Screen.FertigationProgrammation.route + "/{deviceID}/{deviceCode}",
            arguments = listOf(
                navArgument("deviceID") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("deviceCode") {
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
            val deviceCode = it.arguments?.getString("deviceCode") ?: ""
            FertigationProgrammationScreen(navController, deviceID, deviceCode)
        }

        composable(
            route = Screen.FertigationProgrammationECSetting.route + "/{deviceID}/{deviceCode}",
            arguments = listOf(
                navArgument("deviceID") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("deviceCode") {
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
            val deviceCode = it.arguments?.getString("deviceCode") ?: ""
            FertigationProgrammationECSettingScreen(navController, deviceID, deviceCode)
        }

        composable(
            route = Screen.FertigationStatus.route + "/{deviceID}/{deviceCode}",
            arguments = listOf(
                navArgument("deviceID") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("deviceCode") {
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
            val deviceCode = it.arguments?.getString("deviceCode") ?: ""
            FertigationStatusScreen(navController, deviceID, deviceCode)
        }

        composable(
            route = Screen.Map.route + "/{deviceID}/{deviceCode}",
            arguments = listOf(
                navArgument("deviceID") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("deviceCode") {
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
            val deviceCode = it.arguments?.getString("deviceCode") ?: ""
            MapScreen(navController, deviceID, deviceCode)
        }
    }
}
