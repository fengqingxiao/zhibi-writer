package com.zhibi.writer.ui.screens.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zhibi.writer.ui.theme.ZhibiWriterTheme

/**
 * 编辑器界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    workId: Long,
    chapterId: Long,
    viewModel: EditorViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showMenu by remember { mutableStateOf(false) }
    
    LaunchedEffect(workId, chapterId) {
        viewModel.loadChapter(workId, chapterId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiState.chapterTitle ?: "新章节",
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (uiState.wordCount > 0) {
                            Text(
                                text = "${uiState.wordCount}字",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    // 撤销
                    IconButton(
                        onClick = { viewModel.undo() },
                        enabled = uiState.canUndo
                    ) {
                        Icon(Icons.Default.Undo, contentDescription = "撤销")
                    }
                    // 恢复
                    IconButton(
                        onClick = { viewModel.redo() },
                        enabled = uiState.canRedo
                    ) {
                        Icon(Icons.Default.Redo, contentDescription = "恢复")
                    }
                    // 更多
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "更多")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("章节管理") },
                            onClick = {
                                showMenu = false
                                // TODO
                            },
                            leadingIcon = { Icon(Icons.Default.List, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("大纲") },
                            onClick = {
                                showMenu = false
                                // TODO
                            },
                            leadingIcon = { Icon(Icons.Default.AccountTree, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("素材") },
                            onClick = {
                                showMenu = false
                                // TODO
                            },
                            leadingIcon = { Icon(Icons.Default.Inventory2, contentDescription = null) }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("查找替换") },
                            onClick = {
                                showMenu = false
                                // TODO
                            },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("敏感词检测") },
                            onClick = {
                                showMenu = false
                                // TODO
                            },
                            leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null) }
                        )
                    }
                }
            )
        },
        bottomBar = {
            EditorBottomBar(
                onFormatClick = { format -> viewModel.applyFormat(format) },
                onSaveClick = { viewModel.saveContent() }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // 章节标题输入
                OutlinedTextField(
                    value = uiState.chapterTitle ?: "",
                    onValueChange = { viewModel.updateTitle(it) },
                    placeholder = { Text("章节标题") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                // 正文编辑区
                BasicTextField(
                    value = uiState.content,
                    onValueChange = { viewModel.updateContent(it) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 28.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = { innerTextField ->
                        if (uiState.content.isEmpty()) {
                            Text(
                                "开始写作...",
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                        }
                        innerTextField()
                    }
                )
            }
        }
    }
}

/**
 * 编辑器底部工具栏
 */
@Composable
private fun EditorBottomBar(
    onFormatClick: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // 段落格式
            IconButton(onClick = { onFormatClick("paragraph") }) {
                Icon(Icons.Default.FormatAlignLeft, contentDescription = "段落")
            }
            // 加粗
            IconButton(onClick = { onFormatClick("bold") }) {
                Icon(Icons.Default.FormatBold, contentDescription = "加粗")
            }
            // 斜体
            IconButton(onClick = { onFormatClick("italic") }) {
                Icon(Icons.Default.FormatItalic, contentDescription = "斜体")
            }
            // 引号
            IconButton(onClick = { onFormatClick("quote") }) {
                Icon(Icons.Default.FormatQuote, contentDescription = "引号")
            }
            // 保存
            IconButton(onClick = onSaveClick) {
                Icon(Icons.Default.Save, contentDescription = "保存")
            }
        }
    }
}
