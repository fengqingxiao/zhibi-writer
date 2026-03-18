package com.zhibi.writer.ui.screens.outline

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhibi.writer.data.local.entity.OutlineNodeEntity
import com.zhibi.writer.data.local.entity.OutlineNodeType
import com.zhibi.writer.data.local.entity.OutlineStatus

/**
 * 大纲树形结构组件
 */
@Composable
fun OutlineTree(
    nodes: List<OutlineNodeEntity>,
    onNodeClick: (OutlineNodeEntity) -> Unit,
    onNodeLongClick: (OutlineNodeEntity) -> Unit,
    onAddChild: (OutlineNodeEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    // 构建树形结构
    val rootNodes = remember(nodes) {
        nodes.filter { it.parentId == null }
            .sortedBy { it.order }
    }
    
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(
            items = rootNodes,
            key = { it.id }
        ) { node ->
            OutlineTreeNode(
                node = node,
                allNodes = nodes,
                level = 0,
                onClick = onNodeClick,
                onLongClick = onNodeLongClick,
                onAddChild = onAddChild
            )
        }
    }
}

/**
 * 大纲树节点
 */
@Composable
fun OutlineTreeNode(
    node: OutlineNodeEntity,
    allNodes: List<OutlineNodeEntity>,
    level: Int,
    onClick: (OutlineNodeEntity) -> Unit,
    onLongClick: (OutlineNodeEntity) -> Unit,
    onAddChild: (OutlineNodeEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(true) }
    
    val children = remember(allNodes, node.id) {
        allNodes.filter { it.parentId == node.id }
            .sortedBy { it.order }
    }
    
    val hasChildren = children.isNotEmpty()
    
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 90f else 0f,
        label = "rotation"
    )
    
    Column(modifier = modifier) {
        // 节点行
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(node) },
            color = getNodeBackgroundColor(node.nodeType, level)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = (level * 24 + 12).dp,
                        end = 12.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 展开/收起按钮
                if (hasChildren) {
                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = if (isExpanded) "收起" else "展开",
                            modifier = Modifier.rotate(rotationAngle)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(24.dp))
                }
                
                // 节点图标
                NodeIcon(
                    nodeType = node.nodeType,
                    status = node.status,
                    color = getNodeColor(node.nodeType)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // 节点内容
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = node.title,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        
                        // 状态标签
                        if (node.status != OutlineStatus.PLANNED) {
                            Spacer(modifier = Modifier.width(4.dp))
                            StatusChip(
                                status = node.status,
                                estimatedWords = node.estimatedWords
                            )
                        }
                    }
                    
                    // 节点内容预览
                    if (node.content.isNotEmpty()) {
                        Text(
                            text = node.content,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                // 添加子节点按钮
                IconButton(
                    onClick = { onAddChild(node) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "添加子节点",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
        
        // 子节点
        AnimatedVisibility(visible = isExpanded && hasChildren) {
            Column {
                children.forEach { child ->
                    OutlineTreeNode(
                        node = child,
                        allNodes = allNodes,
                        level = level + 1,
                        onClick = onClick,
                        onLongClick = onLongClick,
                        onAddChild = onAddChild
                    )
                }
            }
        }
    }
}

/**
 * 节点图标
 */
@Composable
private fun NodeIcon(
    nodeType: OutlineNodeType,
    status: OutlineStatus,
    color: Color,
    modifier: Modifier = Modifier
) {
    val icon = when (nodeType) {
        OutlineNodeType.VOLUME -> Icons.Default.Folder
        OutlineNodeType.CHAPTER -> Icons.Default.Description
        OutlineNodeType.PLOT -> Icons.Default.AutoStories
        OutlineNodeType.FUTURE -> Icons.Default.Lightbulb
        OutlineNodeType.CHARACTER -> Icons.Default.Person
    }
    
    Surface(
        modifier = modifier.size(28.dp),
        shape = CircleShape,
        color = color.copy(alpha = 0.15f)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/**
 * 状态标签
 */
@Composable
private fun StatusChip(
    status: OutlineStatus,
    estimatedWords: Int,
    modifier: Modifier = Modifier
) {
    val (text, color) = when (status) {
        OutlineStatus.PLANNED -> "计划" to MaterialTheme.colorScheme.outline
        OutlineStatus.WRITING -> "写作中" to MaterialTheme.colorScheme.primary
        OutlineStatus.COMPLETED -> "完成" to MaterialTheme.colorScheme.tertiary
    }
    
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraSmall,
        color = color.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
            if (estimatedWords > 0) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${estimatedWords}字",
                    style = MaterialTheme.typography.labelSmall,
                    color = color
                )
            }
        }
    }
}

/**
 * 获取节点颜色
 */
@Composable
private fun getNodeColor(nodeType: OutlineNodeType): Color {
    return when (nodeType) {
        OutlineNodeType.VOLUME -> MaterialTheme.colorScheme.primary
        OutlineNodeType.CHAPTER -> MaterialTheme.colorScheme.secondary
        OutlineNodeType.PLOT -> MaterialTheme.colorScheme.tertiary
        OutlineNodeType.FUTURE -> Color(0xFFFFB300) // 琥珀色
        OutlineNodeType.CHARACTER -> Color(0xFFE91E63) // 粉色
    }
}

/**
 * 获取节点背景色
 */
@Composable
private fun getNodeBackgroundColor(nodeType: OutlineNodeType, level: Int): Color {
    return if (level == 0 && nodeType == OutlineNodeType.VOLUME) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    } else {
        Color.Transparent
    }
}
