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
import com.zhibi.writer.data.model.PlatformType

/**
 * 平台管理界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlatformManageScreen(
    viewModel: PlatformManageViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("平台管理") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "添加平台")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.platforms.isEmpty()) {
            // 空状态
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CloudUpload,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.outlineVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "还没有绑定平台",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "绑定平台后可一键发布作品",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(24.dp))
                FilledTonalButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("添加平台")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 支持的平台列表
                item {
                    Text(
                        text = "已支持平台",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                items(PlatformType.entries.toTypedArray()) { platform ->
                    PlatformCard(
                        platformType = platform,
                        isAuthorized = uiState.authorizedPlatforms.contains(platform),
                        onAuthorize = { viewModel.authorizePlatform(platform) },
                        onUnbind = { viewModel.unbindPlatform(platform) }
                    )
                }
            }
        }
    }
    
    // 添加平台对话框
    if (showAddDialog) {
        AddPlatformDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { platformType ->
                viewModel.authorizePlatform(platformType)
                showAddDialog = false
            }
        )
    }
}

/**
 * 平台卡片
 */
@Composable
private fun PlatformCard(
    platformType: PlatformType,
    isAuthorized: Boolean,
    onAuthorize: () -> Unit,
    onUnbind: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 平台图标
            Surface(
                modifier = Modifier.size(48.dp),
                shape = MaterialTheme.shapes.medium,
                color = getPlatformColor(platformType).copy(alpha = 0.15f)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getPlatformIcon(platformType),
                        contentDescription = null,
                        tint = getPlatformColor(platformType),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = getPlatformName(platformType),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (isAuthorized) "已授权" else "未授权",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isAuthorized) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.outline
                )
            }
            
            if (isAuthorized) {
                OutlinedButton(onClick = onUnbind) {
                    Text("解绑")
                }
            } else {
                Button(onClick = onAuthorize) {
                    Text("授权")
                }
            }
        }
    }
}

/**
 * 获取平台名称
 */
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

/**
 * 获取平台图标
 */
private fun getPlatformIcon(platform: PlatformType): androidx.compose.ui.graphics.vector.ImageVector {
    return when (platform) {
        PlatformType.YUEWEN -> Icons.Default.AutoStories
        PlatformType.FANQIE -> Icons.Default.MenuBook
        PlatformType.QIMAO -> Icons.Default.Book
        PlatformType.JINJIANG -> Icons.Default.Favorite
        PlatformType.ZONGHENG -> Icons.Default.Edit
        PlatformType.CUSTOM -> Icons.Default.CloudUpload
    }
}

/**
 * 获取平台颜色
 */
@Composable
private fun getPlatformColor(platform: PlatformType): androidx.compose.ui.graphics.Color {
    return when (platform) {
        PlatformType.YUEWEN -> androidx.compose.ui.graphics.Color(0xFFE53935)
        PlatformType.FANQIE -> androidx.compose.ui.graphics.Color(0xFFFF5722)
        PlatformType.QIMAO -> androidx.compose.ui.graphics.Color(0xFF2196F3)
        PlatformType.JINJIANG -> androidx.compose.ui.graphics.Color(0xFFE91E63)
        PlatformType.ZONGHENG -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
        PlatformType.CUSTOM -> androidx.compose.ui.graphics.Color(0xFF9E9E9E)
    }
}

/**
 * 添加平台对话框
 */
@Composable
private fun AddPlatformDialog(
    onDismiss: () -> Unit,
    onConfirm: (PlatformType) -> Unit
) {
    var selectedPlatform by remember { mutableStateOf(PlatformType.FANQIE) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加平台") },
        text = {
            Column {
                Text(
                    text = "选择要绑定的平台",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                PlatformType.entries.filter { it != PlatformType.CUSTOM }.forEach { platform ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedPlatform == platform,
                            onClick = { selectedPlatform = platform }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(getPlatformName(platform))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedPlatform) }) {
                Text("授权绑定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
