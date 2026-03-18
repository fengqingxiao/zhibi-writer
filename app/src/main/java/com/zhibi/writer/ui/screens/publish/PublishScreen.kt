package com.zhibi.writer.ui.screens.publish

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zhibi.writer.data.local.entity.ChapterEntity
import com.zhibi.writer.data.model.PlatformType
import com.zhibi.writer.data.model.PublishStatus
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 发布管理界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishScreen(
    workId: Long,
    viewModel: PublishViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showPublishDialog by remember { mutableStateOf<ChapterEntity?>(null) }
    var showScheduleDialog by remember { mutableStateOf<ChapterEntity?>(null) }
    
    LaunchedEffect(workId) {
        viewModel.loadChapters(workId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("发布管理") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 统计卡片
            item {
                PublishStatsCard(
                    totalChapters = uiState.chapters.size,
                    publishedCount = uiState.publishedCount,
                    pendingCount = uiState.pendingCount
                )
            }
            
            // 章节列表
            items(uiState.chapters, key = { it.id }) { chapter ->
                ChapterPublishItem(
                    chapter = chapter,
                    publishStatus = uiState.publishStatuses[chapter.id],
                    onPublish = { showPublishDialog = chapter },
                    onSchedule = { showScheduleDialog = chapter },
                    onViewStats = { /* TODO */ }
                )
            }
        }
    }
    
    // 发布对话框
    showPublishDialog?.let { chapter ->
        PublishChapterDialog(
            chapterTitle = chapter.title,
            platforms = uiState.availablePlatforms,
            onDismiss = { showPublishDialog = null },
            onConfirm = { platforms ->
                viewModel.publishChapter(chapter.id, platforms)
                showPublishDialog = null
            }
        )
    }
    
    // 定时发布对话框
    showScheduleDialog?.let { chapter ->
        SchedulePublishDialog(
            chapterTitle = chapter.title,
            onDismiss = { showScheduleDialog = null },
            onConfirm = { dateTime ->
                viewModel.schedulePublish(chapter.id, dateTime)
                showScheduleDialog = null
            }
        )
    }
}

/**
 * 发布统计卡片
 */
@Composable
private fun PublishStatsCard(
    totalChapters: Int,
    publishedCount: Int,
    pendingCount: Int,
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
                label = "总章节",
                value = totalChapters.toString()
            )
            StatItem(
                label = "已发布",
                value = publishedCount.toString()
            )
            StatItem(
                label = "待发布",
                value = pendingCount.toString()
            )
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
 * 章节发布项
 */
@Composable
private fun ChapterPublishItem(
    chapter: ChapterEntity,
    publishStatus: PublishStatus?,
    onPublish: () -> Unit,
    onSchedule: () -> Unit,
    onViewStats: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = chapter.title.ifEmpty { "未命名章节" },
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${chapter.wordCount}字",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        publishStatus?.let { status ->
                            Spacer(modifier = Modifier.width(8.dp))
                            StatusChip(status = status)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (publishStatus == null || publishStatus == PublishStatus.DRAFT) {
                    Button(
                        onClick = onPublish,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("发布")
                    }
                    
                    OutlinedButton(
                        onClick = onSchedule,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("定时")
                    }
                } else {
                    OutlinedButton(
                        onClick = onViewStats,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.BarChart, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("查看数据")
                    }
                }
            }
        }
    }
}

/**
 * 状态标签
 */
@Composable
private fun StatusChip(status: PublishStatus) {
    val (text, color) = when (status) {
        PublishStatus.DRAFT -> "草稿" to MaterialTheme.colorScheme.outline
        PublishStatus.PENDING -> "审核中" to MaterialTheme.colorScheme.secondary
        PublishStatus.PUBLISHED -> "已发布" to MaterialTheme.colorScheme.primary
        PublishStatus.FAILED -> "发布失败" to MaterialTheme.colorScheme.error
        PublishStatus.SCHEDULED -> "待发布" to MaterialTheme.colorScheme.tertiary
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

/**
 * 发布章节对话框
 */
@Composable
private fun PublishChapterDialog(
    chapterTitle: String,
    platforms: List<PlatformType>,
    onDismiss: () -> Unit,
    onConfirm: (List<PlatformType>) -> Unit
) {
    var selectedPlatforms by remember { mutableStateOf(setOf<PlatformType>()) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("发布章节") },
        text = {
            Column {
                Text(
                    text = chapterTitle,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "选择发布平台",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                platforms.forEach { platform ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedPlatforms.contains(platform),
                            onCheckedChange = { checked ->
                                selectedPlatforms = if (checked) {
                                    selectedPlatforms + platform
                                } else {
                                    selectedPlatforms - platform
                                }
                            }
                        )
                        Text(getPlatformName(platform))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(selectedPlatforms.toList()) },
                enabled = selectedPlatforms.isNotEmpty()
            ) {
                Text("发布")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

/**
 * 定时发布对话框
 */
@Composable
private fun SchedulePublishDialog(
    chapterTitle: String,
    onDismiss: () -> Unit,
    onConfirm: (kotlinx.datetime.LocalDateTime) -> Unit
) {
    // 简化版：选择日期时间
    var selectedDate by remember { mutableStateOf(kotlinx.datetime.Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("定时发布") },
        text = {
            Column {
                Text(
                    text = chapterTitle,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "选择发布时间",
                    style = MaterialTheme.typography.bodyMedium
                )
                // TODO: 添加日期时间选择器
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedDate) }) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

private fun getPlatformName(platform: PlatformType): String {
    return when (platform) {
        PlatformType.YUEWEN -> "阅文集团"
        PlatformType.FANQIE -> "番茄小说"
        PlatformType.QIMAO -> "七猫小说"
        PlatformType.JINJIANG -> "晋江文学城"
        PlatformType.ZONGHENG -> "纵横中文网"
        PlatformType.CUSTOM -> "自定义平台"
    }
}
