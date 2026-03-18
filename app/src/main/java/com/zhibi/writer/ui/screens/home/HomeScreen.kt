package com.zhibi.writer.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 首页 - 作品列表
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToEditor: (Long, Long) -> Unit,
    onNavigateToOutline: (Long) -> Unit,
    onNavigateToChapters: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showNewWorkDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("执笔写作")
                },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "更多")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("导入作品") },
                            onClick = {
                                showMenu = false
                                // TODO: 导入作品
                            },
                            leadingIcon = {
                                Icon(Icons.Default.CloudUpload, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("设置") },
                            onClick = {
                                showMenu = false
                                // TODO: 导航到设置
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Settings, contentDescription = null)
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showNewWorkDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("新建作品") }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.works.isEmpty() -> {
                // 空状态
                EmptyWorkState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    onCreateWork = { showNewWorkDialog = true }
                )
            }
            else -> {
                // 作品列表
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 今日统计卡片
                    item {
                        TodayStatsCard(
                            todayWords = uiState.todayWords,
                            totalWords = uiState.totalWords,
                            streakDays = uiState.streakDays
                        )
                    }
                    
                    // 作品卡片列表
                    items(
                        items = uiState.works,
                        key = { it.id }
                    ) { work ->
                        WorkCard(
                            work = work,
                            onClick = {
                                onNavigateToChapters(work.id)
                            },
                            onLongClick = {
                                // TODO: 显示操作菜单
                            },
                            onOutlineClick = {
                                onNavigateToOutline(work.id)
                            }
                        )
                    }
                }
            }
        }
    }
    
    // 新建作品对话框
    if (showNewWorkDialog) {
        NewWorkDialog(
            onDismiss = { showNewWorkDialog = false },
            onConfirm = { title, summary ->
                viewModel.createWork(title, summary)
                showNewWorkDialog = false
            }
        )
    }
}

/**
 * 空状态
 */
@Composable
private fun EmptyWorkState(
    modifier: Modifier = Modifier,
    onCreateWork: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.MenuBook,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.outlineVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "开始你的创作之旅",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "点击下方按钮创建你的第一部作品",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(24.dp))
        FilledTonalButton(onClick = onCreateWork) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("创建作品")
        }
    }
}

/**
 * 今日统计卡片
 */
@Composable
private fun TodayStatsCard(
    todayWords: Int,
    totalWords: Long,
    streakDays: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                label = "今日字数",
                value = todayWords.toString(),
                modifier = Modifier.weight(1f)
            )
            StatItem(
                label = "总字数",
                value = formatWordCount(totalWords),
                modifier = Modifier.weight(1f)
            )
            StatItem(
                label = "连续更新",
                value = "${streakDays}天",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * 统计项
 */
@Composable
private fun StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

/**
 * 格式化字数
 */
private fun formatWordCount(count: Long): String {
    return when {
        count >= 10000 -> String.format("%.1f万", count / 10000.0)
        else -> count.toString()
    }
}

/**
 * 格式化时间
 */
private fun formatDateTime(dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm")
    return dateTime.toJavaLocalDateTime().format(formatter)
}
