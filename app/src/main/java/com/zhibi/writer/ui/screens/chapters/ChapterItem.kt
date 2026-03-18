package com.zhibi.writer.ui.screens.chapters

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zhibi.writer.data.local.entity.ChapterEntity
import com.zhibi.writer.data.local.entity.ChapterStatus
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 章节列表项
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterItem(
    chapter: ChapterEntity,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onMenuClick: (ChapterAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    
    val formatter = remember { DateTimeFormatter.ofPattern("MM-dd HH:mm") }
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        color = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        } else {
            MaterialTheme.colorScheme.surface
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 选择模式下显示复选框
            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onClick() },
                    modifier = Modifier.padding(end = 12.dp)
                )
            }
            
            // 章节序号
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = when (chapter.status) {
                    ChapterStatus.COMPLETED -> MaterialTheme.colorScheme.primaryContainer
                    ChapterStatus.PUBLISHED -> MaterialTheme.colorScheme.tertiaryContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${chapter.order + 1}",
                        style = MaterialTheme.typography.labelMedium,
                        color = when (chapter.status) {
                            ChapterStatus.COMPLETED -> MaterialTheme.colorScheme.onPrimaryContainer
                            ChapterStatus.PUBLISHED -> MaterialTheme.colorScheme.onTertiaryContainer
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // 章节信息
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (chapter.isLocked) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "已锁定",
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    
                    Text(
                        text = chapter.title.ifEmpty { "未命名章节" },
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 状态标签
                    StatusBadge(status = chapter.status)
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // 字数
                    Text(
                        text = "${chapter.wordCount}字",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // 更新时间
                    Text(
                        text = chapter.updatedAt.toJavaLocalDateTime().format(formatter),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            
            // 更多操作菜单
            if (!isSelectionMode) {
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "更多"
                        )
                    }
                    
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("编辑") },
                            onClick = {
                                showMenu = false
                                onMenuClick(ChapterAction.EDIT)
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (chapter.isLocked) "解锁" else "锁定") },
                            onClick = {
                                showMenu = false
                                onMenuClick(ChapterAction.LOCK)
                            },
                            leadingIcon = {
                                Icon(
                                    if (chapter.isLocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                    contentDescription = null
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("移动到...") },
                            onClick = {
                                showMenu = false
                                onMenuClick(ChapterAction.MOVE)
                            },
                            leadingIcon = {
                                Icon(Icons.Default.DriveFileMove, contentDescription = null)
                            }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("删除") },
                            onClick = {
                                showMenu = false
                                onMenuClick(ChapterAction.DELETE)
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null)
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = MaterialTheme.colorScheme.error
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * 状态徽章
 */
@Composable
private fun StatusBadge(
    status: ChapterStatus,
    modifier: Modifier = Modifier
) {
    val (text, containerColor, contentColor) = when (status) {
        ChapterStatus.DRAFT -> Triple("草稿", 
            MaterialTheme.colorScheme.outlineVariant,
            MaterialTheme.colorScheme.outline)
        ChapterStatus.WRITING -> Triple("写作中",
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer)
        ChapterStatus.COMPLETED -> Triple("已完成",
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer)
        ChapterStatus.PUBLISHED -> Triple("已发布",
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer)
        ChapterStatus.SCHEDULED -> Triple("定时",
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer)
    }
    
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraSmall,
        color = containerColor
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
