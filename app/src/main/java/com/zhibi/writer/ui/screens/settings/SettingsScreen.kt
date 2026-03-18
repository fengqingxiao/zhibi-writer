package com.zhibi.writer.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 设置界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToSensitiveWord: () -> Unit = {},
    onNavigateToNameGenerator: () -> Unit = {},
    onNavigateToTextFormat: () -> Unit = {}
) {
    var darkTheme by remember { mutableStateOf(false) }
    var eyeCareMode by remember { mutableStateOf(false) }
    var autoSave by remember { mutableStateOf(true) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("我的") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 用户信息卡片
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        Text(
                            text = "执笔作者",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "本地模式",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
            
            // 设置列表
            Text(
                text = "显示设置",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column {
                    SettingItem(
                        title = "深色模式",
                        subtitle = "保护眼睛，适合夜间写作",
                        icon = Icons.Default.DarkMode,
                        checked = darkTheme,
                        onCheckedChange = { darkTheme = it }
                    )
                    HorizontalDivider()
                    SettingItem(
                        title = "护眼模式",
                        subtitle = "米黄色背景，舒适阅读",
                        icon = Icons.Default.RemoveRedEye,
                        checked = eyeCareMode,
                        onCheckedChange = { eyeCareMode = it }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "写作设置",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column {
                    SettingItem(
                        title = "自动保存",
                        subtitle = "每输入字符自动保存",
                        icon = Icons.Default.Save,
                        checked = autoSave,
                        onCheckedChange = { autoSave = it }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 创作工具
            Text(
                text = "创作工具",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column {
                    SettingItemSimple(
                        title = "敏感词检测",
                        subtitle = "检测文本中的敏感词",
                        icon = Icons.Default.Warning,
                        onClick = onNavigateToSensitiveWord
                    )
                    HorizontalDivider()
                    SettingItemSimple(
                        title = "随机取名",
                        subtitle = "生成姓名、地点、功法等名称",
                        icon = Icons.Default.AutoAwesome,
                        onClick = onNavigateToNameGenerator
                    )
                    HorizontalDivider()
                    SettingItemSimple(
                        title = "一键排版",
                        subtitle = "适配各平台格式要求",
                        icon = Icons.Default.FormatAlignLeft,
                        onClick = onNavigateToTextFormat
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 数据管理
            Text(
                text = "数据管理",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column {
                    SettingItemSimple(
                        title = "备份数据",
                        subtitle = "导出所有作品到本地",
                        icon = Icons.Default.CloudUpload,
                        onClick = { /* TODO */ }
                    )
                    HorizontalDivider()
                    SettingItemSimple(
                        title = "恢复数据",
                        subtitle = "从备份文件恢复",
                        icon = Icons.Default.CloudDownload,
                        onClick = { /* TODO */ }
                    )
                    HorizontalDivider()
                    SettingItemSimple(
                        title = "清除缓存",
                        subtitle = "释放存储空间",
                        icon = Icons.Default.CleaningServices,
                        onClick = { /* TODO */ }
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        leadingContent = {
            Icon(icon, contentDescription = null)
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        },
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun SettingItemSimple(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        leadingContent = {
            Icon(icon, contentDescription = null)
        },
        trailingContent = {
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        },
        modifier = Modifier.padding(vertical = 4.dp)
    )
}
