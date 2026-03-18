package com.zhibi.writer.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 导航路径定义
 */
sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val outlinedIcon: ImageVector
) {
    // 底部导航
    data object Home : Screen(
        route = "home",
        title = "作品",
        icon = Icons.Filled.LibraryBooks,
        outlinedIcon = Icons.Outlined.LibraryBooks
    )
    
    data object Outline : Screen(
        route = "outline?workId={workId}",
        title = "大纲",
        icon = Icons.Filled.AccountTree,
        outlinedIcon = Icons.Outlined.AccountTree
    ) {
        fun createRoute(workId: Long = 0) = "outline?workId=$workId"
    }
    
    data object Material : Screen(
        route = "material?workId={workId}",
        title = "素材",
        icon = Icons.Filled.Inventory2,
        outlinedIcon = Icons.Outlined.Inventory2
    ) {
        fun createRoute(workId: Long = 0) = "material?workId=$workId"
    }
    
    data object Settings : Screen(
        route = "settings",
        title = "我的",
        icon = Icons.Filled.Person,
        outlinedIcon = Icons.Outlined.Person
    )
    
    // 编辑器
    data object Editor : Screen(
        route = "editor/{workId}/{chapterId}",
        title = "编辑",
        icon = Icons.Filled.Edit,
        outlinedIcon = Icons.Outlined.Edit
    ) {
        fun createRoute(workId: Long, chapterId: Long = 0) = "editor/$workId/$chapterId"
        
        val navArguments = listOf(
            androidx.navigation.NavArgument("workId") {
                type = androidx.navigation.NavType.LongType
                defaultValue = 0L
            },
            androidx.navigation.NavArgument("chapterId") {
                type = androidx.navigation.NavType.LongType
                defaultValue = 0L
            }
        )
    }
    
    // 作品详情
    data object WorkDetail : Screen(
        route = "work/{workId}",
        title = "作品详情",
        icon = Icons.Filled.Book,
        outlinedIcon = Icons.Outlined.Book
    ) {
        fun createRoute(workId: Long) = "work/$workId"
    }
    
    // 章节管理
    data object ChapterList : Screen(
        route = "chapters/{workId}",
        title = "章节管理",
        icon = Icons.Filled.List,
        outlinedIcon = Icons.Outlined.List
    ) {
        fun createRoute(workId: Long) = "chapters/$workId"
    }
    
    // 新建作品
    data object NewWork : Screen(
        route = "new_work",
        title = "新建作品",
        icon = Icons.Filled.Add,
        outlinedIcon = Icons.Outlined.Add
    )
    
    // 新建章节
    data object NewChapter : Screen(
        route = "new_chapter/{workId}",
        title = "新建章节",
        icon = Icons.Filled.Add,
        outlinedIcon = Icons.Outlined.Add
    ) {
        fun createRoute(workId: Long) = "new_chapter/$workId"
    }
    
    companion object {
        val bottomScreens = listOf(Home, Outline, Material, Settings)
    }
}
