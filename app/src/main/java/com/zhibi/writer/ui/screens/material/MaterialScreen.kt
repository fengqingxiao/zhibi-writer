package com.zhibi.writer.ui.screens.material

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zhibi.writer.data.local.entity.MaterialCategory
import com.zhibi.writer.data.local.entity.MaterialEntity

/**
 * 素材库主页
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialScreen(
    viewModel: MaterialViewModel = hiltViewModel(),
    workId: Long = 0 // 0表示全局素材
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedCategory by remember { mutableStateOf<MaterialCategory?>(null) }
    var showNewMaterialDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(workId, selectedCategory) {
        viewModel.loadMaterials(workId, selectedCategory)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("素材库") },
                actions = {
                    IconButton(onClick = { /* TODO: 搜索 */ }) {
                        Icon(Icons.Default.Search, contentDescription = "搜索")
                    }
                    IconButton(onClick = { showNewMaterialDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "添加")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 分类标签
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("全部") }
                    )
                }
                
                items(MaterialCategory.entries.toTypedArray()) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(getCategoryName(category)) },
                        leadingIcon = {
                            Icon(
                                imageVector = getCategoryIcon(category),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }
            
            HorizontalDivider()
            
            // 素材列表
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.materials.isEmpty()) {
                EmptyMaterialState(
                    modifier = Modifier.fillMaxSize(),
                    onCreateMaterial = { showNewMaterialDialog = true }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = uiState.materials,
                        key = { it.id }
                    ) { material ->
                        MaterialCard(
                            material = material,
                            onClick = { /* TODO: 编辑素材 */ }
                        )
                    }
                }
            }
        }
    }
    
    // 新建素材对话框
    if (showNewMaterialDialog) {
        NewMaterialDialog(
            categories = MaterialCategory.entries.toTypedArray(),
            onDismiss = { showNewMaterialDialog = false },
            onConfirm = { category, title ->
                viewModel.createMaterial(workId, category, title)
                showNewMaterialDialog = false
            }
        )
    }
}

/**
 * 空状态
 */
@Composable
private fun EmptyMaterialState(
    modifier: Modifier = Modifier,
    onCreateMaterial: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Inventory2,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.outlineVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "还没有素材",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "创建人物设定、世界观、伏笔线索等素材",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(24.dp))
        FilledTonalButton(onClick = onCreateMaterial) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("新建素材")
        }
    }
}

/**
 * 素材卡片
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialCard(
    material: MaterialEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 分类图标
            Surface(
                modifier = Modifier.size(48.dp),
                shape = MaterialTheme.shapes.medium,
                color = getCategoryColor(material.category).copy(alpha = 0.15f)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getCategoryIcon(material.category),
                        contentDescription = null,
                        tint = getCategoryColor(material.category),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = material.title,
                    style = MaterialTheme.typography.titleMedium
                )
                
                if (material.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        material.tags.take(3).forEach { tag ->
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.extraSmall
                            ) {
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        if (material.tags.size > 3) {
                            Text(
                                text = "+${material.tags.size - 3}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}

/**
 * 获取分类名称
 */
private fun getCategoryName(category: MaterialCategory): String {
    return when (category) {
        MaterialCategory.CHARACTER -> "人物"
        MaterialCategory.WORLD -> "世界观"
        MaterialCategory.FACTION -> "势力"
        MaterialCategory.LOCATION -> "地点"
        MaterialCategory.ITEM -> "道具"
        MaterialCategory.FORESHADOW -> "伏笔"
        MaterialCategory.INSPIRATION -> "灵感"
        MaterialCategory.REFERENCE -> "参考"
    }
}

/**
 * 获取分类图标
 */
private fun getCategoryIcon(category: MaterialCategory): androidx.compose.ui.graphics.vector.ImageVector {
    return when (category) {
        MaterialCategory.CHARACTER -> Icons.Default.Person
        MaterialCategory.WORLD -> Icons.Default.Public
        MaterialCategory.FACTION -> Icons.Default.Groups
        MaterialCategory.LOCATION -> Icons.Default.Place
        MaterialCategory.ITEM -> Icons.Default.Diamond
        MaterialCategory.FORESHADOW -> Icons.Default.AutoAwesome
        MaterialCategory.INSPIRATION -> Icons.Default.Lightbulb
        MaterialCategory.REFERENCE -> Icons.Default.MenuBook
    }
}

/**
 * 获取分类颜色
 */
@Composable
private fun getCategoryColor(category: MaterialCategory): androidx.compose.ui.graphics.Color {
    return when (category) {
        MaterialCategory.CHARACTER -> androidx.compose.ui.graphics.Color(0xFFE91E63)
        MaterialCategory.WORLD -> androidx.compose.ui.graphics.Color(0xFF9C27B0)
        MaterialCategory.FACTION -> androidx.compose.ui.graphics.Color(0xFF3F51B5)
        MaterialCategory.LOCATION -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
        MaterialCategory.ITEM -> androidx.compose.ui.graphics.Color(0xFFFF9800)
        MaterialCategory.FORESHADOW -> androidx.compose.ui.graphics.Color(0xFFFFB300)
        MaterialCategory.INSPIRATION -> androidx.compose.ui.graphics.Color(0xFF00BCD4)
        MaterialCategory.REFERENCE -> androidx.compose.ui.graphics.Color(0xFF607D8B)
    }
}
