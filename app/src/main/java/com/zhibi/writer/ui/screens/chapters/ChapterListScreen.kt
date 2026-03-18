package com.zhibi.writer.ui.screens.chapters

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
 * 章节管理界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterListScreen(
    workId: Long,
    viewModel: ChapterListViewModel = hiltViewModel(),
    onNavigateToEditor: (Long, Long) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showNewChapterDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var selectedChapters by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var isSelectionMode by remember { mutableStateOf(false) }
    
    LaunchedEffect(workId) {
        viewModel.loadChapters(workId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(uiState.workTitle ?: "章节管理")
                        if (isSelectionMode) {
                            Text(
                                "已选择 ${selectedChapters.size} 章",
                                style = MaterialTheme.typography.bodySmall
                            )
                        } else {
                            Text(
                                "共 ${uiState.chapters.size} 章 · ${uiState.totalWords}字",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isSelectionMode) {
                            isSelectionMode = false
                            selectedChapters = emptySet()
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(
                            if (isSelectionMode) Icons.Default.Close else Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    if (isSelectionMode) {
                        IconButton(onClick = { selectedChapters = uiState.chapters.map { it.id }.toSet() }) {
                            Icon(Icons.Default.SelectAll, contentDescription = "全选")
                        }
                        IconButton(onClick = {
                            // TODO: 批量操作
                        }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "更多")
                        }
                    } else {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "更多")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("新建章节") },
                                onClick = {
                                    showMenu = false
                                    showNewChapterDialog = true
                                },
                                leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text("批量管理") },
                                onClick = {
                                    showMenu = false
                                    isSelectionMode = true
                                },
                                leadingIcon = { Icon(Icons.Default.Checklist, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text("排序管理") },
                                onClick = {
                                    showMenu = false
                                    // TODO: 排序模式
                                },
                                leadingIcon = { Icon(Icons.Default.Sort, contentDescription = null) }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("回收站") },
                                onClick = {
                                    showMenu = false
                                    // TODO: 回收站
                                },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (!isSelectionMode) {
                ExtendedFloatingActionButton(
                    onClick = { showNewChapterDialog = true },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("新建章节") }
                )
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.chapters.isEmpty()) {
            EmptyChapterState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                onCreateChapter = { showNewChapterDialog = true }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                state = rememberLazyListState(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(
                    items = uiState.chapters,
                    key = { it.id }
                ) { chapter ->
                    ChapterItem(
                        chapter = chapter,
                        isSelected = selectedChapters.contains(chapter.id),
                        isSelectionMode = isSelectionMode,
                        onClick = {
                            if (isSelectionMode) {
                                selectedChapters = if (selectedChapters.contains(chapter.id)) {
                                    selectedChapters - chapter.id
                                } else {
                                    selectedChapters + chapter.id
                                }
                            } else {
                                onNavigateToEditor(workId, chapter.id)
                            }
                        },
                        onLongClick = {
                            if (!isSelectionMode) {
                                isSelectionMode = true
                                selectedChapters = setOf(chapter.id)
                            }
                        },
                        onMenuClick = { action ->
                            when (action) {
                                ChapterAction.EDIT -> onNavigateToEditor(workId, chapter.id)
                                ChapterAction.LOCK -> viewModel.toggleLock(chapter.id)
                                ChapterAction.DELETE -> viewModel.deleteChapter(chapter.id)
                                ChapterAction.MOVE -> { /* TODO */ }
                            }
                        }
                    )
                }
            }
        }
    }
    
    // 新建章节对话框
    if (showNewChapterDialog) {
        NewChapterDialog(
            onDismiss = { showNewChapterDialog = false },
            onConfirm = { title ->
                viewModel.createChapter(workId, title)
                showNewChapterDialog = false
            }
        )
    }
}

/**
 * 空状态
 */
@Composable
private fun EmptyChapterState(
    modifier: Modifier = Modifier,
    onCreateChapter: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Description,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.outlineVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "还没有章节",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "点击下方按钮开始创作第一章",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(24.dp))
        FilledTonalButton(onClick = onCreateChapter) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("新建章节")
        }
    }
}

/**
 * 章节操作类型
 */
enum class ChapterAction {
    EDIT, LOCK, MOVE, DELETE
}
