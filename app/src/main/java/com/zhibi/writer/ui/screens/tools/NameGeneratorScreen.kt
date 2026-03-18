package com.zhibi.writer.ui.screens.tools

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zhibi.writer.util.*

/**
 * 随机取名界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameGeneratorScreen(
    viewModel: NameGeneratorViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedType by remember { mutableStateOf(NameType.MALE_NAME) }
    var count by remember { mutableStateOf(10) }
    var expanded by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("随机取名") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
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
            // 类型选择
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = getTypeName(selectedType),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("名称类型") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    NameType.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(getTypeName(type)) },
                            onClick = {
                                selectedType = type
                                expanded = false
                            }
                        )
                    }
                }
            }
            
            // 数量选择
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "生成数量：",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.width(16.dp))
                
                Slider(
                    value = count.toFloat(),
                    onValueChange = { count = it.toInt() },
                    valueRange = 1f..50f,
                    steps = 49,
                    modifier = Modifier.weight(1f)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            // 生成按钮
            Button(
                onClick = { viewModel.generate(selectedType, count) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("生成名称")
            }
            
            HorizontalDivider()
            
            // 生成结果
            if (uiState.names.isNotEmpty()) {
                Text(
                    text = "生成结果",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.names) { name ->
                        NameItem(
                            name = name,
                            onCopy = { viewModel.copyName(name) },
                            onFavorite = { viewModel.favoriteName(name) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * 名称项
 */
@Composable
private fun NameItem(
    name: String,
    onCopy: () -> Unit,
    onFavorite: () -> Unit
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
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            
            IconButton(onClick = onCopy) {
                Icon(Icons.Default.ContentCopy, contentDescription = "复制")
            }
            
            IconButton(onClick = onFavorite) {
                Icon(
                    imageVector = if (true) Icons.Default.FavoriteBorder else Icons.Default.Favorite,
                    contentDescription = "收藏"
                )
            }
        }
    }
}

/**
 * 获取类型名称
 */
private fun getTypeName(type: NameType): String {
    return when (type) {
        NameType.MALE_NAME -> "男性姓名"
        NameType.FEMALE_NAME -> "女性姓名"
        NameType.ANCIENT_NAME -> "古风姓名"
        NameType.LOCATION -> "地点名称"
        NameType.FACTION -> "宗门势力"
        NameType.SKILL -> "功法武学"
        NameType.MEDICINE -> "丹药灵草"
        NameType.WEAPON -> "武器法宝"
    }
}
