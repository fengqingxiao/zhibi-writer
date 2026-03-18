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
import com.zhibi.writer.ui.screens.chapters.ChapterListScreen
import com.zhibi.writer.ui.screens.tools.SensitiveWordScreen
import com.zhibi.writer.ui.screens.tools.NameGeneratorScreen
import com.zhibi.writer.ui.screens.tools.TextFormatScreen
import com.zhibi.writer.ui.screens.publish.PlatformManageScreen
import com.zhibi.writer.ui.screens.publish.PublishScreen
import com.zhibi.writer.ui.screens.stats.StatsScreen

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
                    },
                    onNavigateToChapters = { workId ->
                        navController.navigate(Screen.ChapterList.createRoute(workId))
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
                SettingsScreen(
                    onNavigateToSensitiveWord = {
                        navController.navigate(Screen.SensitiveWord.route)
                    },
                    onNavigateToNameGenerator = {
                        navController.navigate(Screen.NameGenerator.route)
                    },
                    onNavigateToTextFormat = {
                        navController.navigate(Screen.TextFormat.route)
                    },
                    onNavigateToPlatformManage = {
                        navController.navigate(Screen.PlatformManage.route)
                    },
                    onNavigateToStats = {
                        navController.navigate(Screen.Stats.createRoute(0))
                    }
                )
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
            
            composable(
                route = Screen.ChapterList.route,
                arguments = Screen.ChapterList.navArguments
            ) { backStackEntry ->
                val workId = backStackEntry.arguments?.getLong("workId") ?: 0L
                
                ChapterListScreen(
                    workId = workId,
                    onNavigateToEditor = { wId, cId ->
                        navController.navigate(Screen.Editor.createRoute(wId, cId))
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            
            // 创作工具
            composable(Screen.SensitiveWord.route) {
                SensitiveWordScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.NameGenerator.route) {
                NameGeneratorScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.TextFormat.route) {
                TextFormatScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // 发布管理
            composable(Screen.PlatformManage.route) {
                PlatformManageScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(
                route = Screen.Publish.route,
                arguments = Screen.Publish.navArguments
            ) { backStackEntry ->
                val workId = backStackEntry.arguments?.getLong("workId") ?: 0L
                PublishScreen(
                    workId = workId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // 数据统计
            composable(
                route = Screen.Stats.route,
                arguments = Screen.Stats.navArguments
            ) { backStackEntry ->
                val workId = backStackEntry.arguments?.getLong("workId") ?: 0L
                StatsScreen(
                    workId = workId,
                    onNavigateBack = { navController.popBackStack() }
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
