package com.zhibi.writer.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zhibi.writer.data.local.entity.WorkEntity
import com.zhibi.writer.data.local.entity.WorkStatus

/**
 * 作品卡片
 */
@Composable
fun WorkCard(
    work: WorkEntity,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onOutlineClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 标题行
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 作品封面占位
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = work.title.firstOrNull()?.toString() ?: "书",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = work.title,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        if (work.isTopped) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = "置顶",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusChip(status = work.status)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${work.totalWordCount}字",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        if (work.latestChapterTitle != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "• ${work.chapterCount}章",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
                
                // 更多菜单
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
                            text = { Text("查看大纲") },
                            onClick = {
                                showMenu = false
                                onOutlineClick()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.AccountTree, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("作品设置") },
                            onClick = {
                                showMenu = false
                                // TODO
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Settings, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("导出作品") },
                            onClick = {
                                showMenu = false
                                // TODO
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Download, contentDescription = null)
                            }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("删除作品") },
                            onClick = {
                                showMenu = false
                                // TODO
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
            
            // 最新章节
            if (work.latestChapterTitle != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "最新：${work.latestChapterTitle}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
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
private fun StatusChip(
    status: WorkStatus,
    modifier: Modifier = Modifier
) {
    val (text, color) = when (status) {
        WorkStatus.DRAFT -> "草稿" to MaterialTheme.colorScheme.outline
        WorkStatus.SERIAL -> "连载" to MaterialTheme.colorScheme.primary
        WorkStatus.COMPLETED -> "完结" to MaterialTheme.colorScheme.tertiary
        WorkStatus.PAUSED -> "暂停" to MaterialTheme.colorScheme.secondary
    }
    
    Surface(
        modifier = modifier,
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
