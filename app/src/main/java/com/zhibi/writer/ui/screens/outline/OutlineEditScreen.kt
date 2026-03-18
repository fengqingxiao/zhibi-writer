package com.zhibi.writer.ui.screens.outline

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zhibi.writer.data.local.entity.OutlineNodeEntity
import com.zhibi.writer.data.local.entity.OutlineNodeType
import com.zhibi.writer.data.local.entity.OutlineStatus

/**
 * 大纲编辑界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlineEditScreen(
    workId: Long,
    nodeId: Long? = null,
    parentId: Long? = null,
    viewModel: OutlineViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(workId, nodeId) {
        if (nodeId != null) {
            viewModel.loadNode(nodeId)
        } else {
            viewModel.initNewNode(workId, parentId)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (nodeId == null) "新建大纲节点" else "编辑大纲") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    if (nodeId != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "删除")
                        }
                    }
                    IconButton(onClick = { viewModel.saveNode() }) {
                        Icon(Icons.Default.Check, contentDescription = "保存")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 节点类型选择
            Text(
                text = "节点类型",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NodeTypeChip(
                    type = OutlineNodeType.VOLUME,
                    selected = uiState.nodeType == OutlineNodeType.VOLUME,
                    onClick = { viewModel.updateNodeType(OutlineNodeType.VOLUME) }
                )
                NodeTypeChip(
                    type = OutlineNodeType.CHAPTER,
                    selected = uiState.nodeType == OutlineNodeType.CHAPTER,
                    onClick = { viewModel.updateNodeType(OutlineNodeType.CHAPTER) }
                )
                NodeTypeChip(
                    type = OutlineNodeType.PLOT,
                    selected = uiState.nodeType == OutlineNodeType.PLOT,
                    onClick = { viewModel.updateNodeType(OutlineNodeType.PLOT) }
                )
                NodeTypeChip(
                    type = OutlineNodeType.FUTURE,
                    selected = uiState.nodeType == OutlineNodeType.FUTURE,
                    onClick = { viewModel.updateNodeType(OutlineNodeType.FUTURE) }
                )
            }
            
            // 标题
            OutlinedTextField(
                value = uiState.title,
                onValueChange = { viewModel.updateTitle(it) },
                label = { Text("标题") },
                placeholder = { Text("输入大纲标题") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            
            // 内容
            OutlinedTextField(
                value = uiState.content,
                onValueChange = { viewModel.updateContent(it) },
                label = { Text("内容描述") },
                placeholder = { Text("描述这个大纲节点的内容") },
                minLines = 3,
                maxLines = 6,
                modifier = Modifier.fillMaxWidth()
            )
            
            // 状态选择
            Text(
                text = "状态",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusButton(
                    status = OutlineStatus.PLANNED,
                    selected = uiState.status == OutlineStatus.PLANNED,
                    onClick = { viewModel.updateStatus(OutlineStatus.PLANNED) }
                )
                StatusButton(
                    status = OutlineStatus.WRITING,
                    selected = uiState.status == OutlineStatus.WRITING,
                    onClick = { viewModel.updateStatus(OutlineStatus.WRITING) }
                )
                StatusButton(
                    status = OutlineStatus.COMPLETED,
                    selected = uiState.status == OutlineStatus.COMPLETED,
                    onClick = { viewModel.updateStatus(OutlineStatus.COMPLETED) }
                )
            }
            
            // 预估字数
            OutlinedTextField(
                value = if (uiState.estimatedWords > 0) uiState.estimatedWords.toString() else "",
                onValueChange = { 
                    viewModel.updateEstimatedWords(it.toIntOrNull() ?: 0)
                },
                label = { Text("预估字数") },
                placeholder = { Text("0") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            
            // 标签
            OutlinedTextField(
                value = uiState.tags.joinToString(", "),
                onValueChange = { 
                    viewModel.updateTags(it.split(",").map { it.trim() }.filter { it.isNotEmpty() })
                },
                label = { Text("标签") },
                placeholder = { Text("核心剧情, 伏笔, 人物出场") },
                supportingText = { Text("用逗号分隔多个标签") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    
    // 删除确认对话框
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("删除大纲节点") },
            text = { Text("确定要删除此大纲节点吗？子节点也会被一起删除。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteNode()
                        showDeleteDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

/**
 * 节点类型选择器
 */
@Composable
private fun NodeTypeChip(
    type: OutlineNodeType,
    selected: Boolean,
    onClick: () -> Unit
) {
    val label = when (type) {
        OutlineNodeType.VOLUME -> "卷"
        OutlineNodeType.CHAPTER -> "章节"
        OutlineNodeType.PLOT -> "情节"
        OutlineNodeType.FUTURE -> "伏笔"
        OutlineNodeType.CHARACTER -> "人物"
    }
    
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        } else null
    )
}

/**
 * 状态选择按钮
 */
@Composable
private fun StatusButton(
    status: OutlineStatus,
    selected: Boolean,
    onClick: () -> Unit
) {
    val label = when (status) {
        OutlineStatus.PLANNED -> "计划中"
        OutlineStatus.WRITING -> "写作中"
        OutlineStatus.COMPLETED -> "已完成"
    }
    
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) }
    )
}
