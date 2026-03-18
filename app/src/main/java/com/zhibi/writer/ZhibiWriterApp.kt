package com.zhibi.writer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.zhibi.writer.ui.navigation.Screen
import com.zhibi.writer.ui.screens.home.HomeScreen
import com.zhibi.writer.ui.screens.outline.OutlineScreen
import com.zhibi.writer.ui.screens.material.MaterialScreen
import com.zhibi.writer.ui.screens.settings.SettingsScreen
import com.zhibi.writer.ui.screens.editor.EditorScreen

/**
 * 执笔写作主应用
 */
@Composable
fun ZhibiWriterApp(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // 判断是否显示底部导航栏
    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Outline.route,
        Screen.Material.route,
        Screen.Settings.route
    )
    
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToEditor = { workId, chapterId ->
                        navController.navigate(Screen.Editor.createRoute(workId, chapterId))
                    },
                    onNavigateToOutline = { workId ->
                        navController.navigate(Screen.Outline.createRoute(workId))
                    }
                )
            }
            
            composable(Screen.Outline.route) {
                OutlineScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable(Screen.Material.route) {
                MaterialScreen()
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
            
            composable(
                route = Screen.Editor.route,
                arguments = Screen.Editor.navArguments
            ) { backStackEntry ->
                val workId = backStackEntry.arguments?.getLong("workId") ?: 0L
                val chapterId = backStackEntry.arguments?.getLong("chapterId") ?: 0L
                
                EditorScreen(
                    workId = workId,
                    chapterId = chapterId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

/**
 * 底部导航栏
 */
@Composable
private fun BottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar {
        Screen.bottomScreens.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = { onNavigate(screen.route) },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.title
                    )
                },
                label = {
                    Text(text = screen.title)
                }
            )
        }
    }
}
