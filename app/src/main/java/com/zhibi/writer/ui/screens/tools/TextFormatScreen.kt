package com.zhibi.writer.ui.screens.tools

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zhibi.writer.util.FormatOptions
import com.zhibi.writer.util.FormatPresets
import com.zhibi.writer.util.TextFormatter

/**
 * 一键排版界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFormatScreen(
    viewModel: TextFormatViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedPlatform by remember { mutableStateOf("default") }
    var expanded by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("一键排版") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 平台选择
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = TextFormatter.PLATFORM_FORMATS[selectedPlatform]?.name ?: "默认",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("目标平台") },
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
                    TextFormatter.PLATFORM_FORMATS.forEach { (key, format) ->
                        DropdownMenuItem(
                            text = { Text(format.name) },
                            onClick = {
                                selectedPlatform = key
                                expanded = false
                            }
                        )
                    }
                }
            }
            
            // 格式选项
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "格式选项",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    FormatOptionItem(
                        label = "添加首行缩进",
                        checked = uiState.options.addIndent,
                        onCheckedChange = { viewModel.updateOption(uiState.options.copy(addIndent = it)) }
                    )
                    
                    FormatOptionItem(
                        label = "去除多余空格",
                        checked = uiState.options.trimSpaces,
                        onCheckedChange = { viewModel.updateOption(uiState.options.copy(trimSpaces = it)) }
                    )
                    
                    FormatOptionItem(
                        label = "去除多余空行",
                        checked = uiState.options.removeEmptyLines,
                        onCheckedChange = { viewModel.updateOption(uiState.options.copy(removeEmptyLines = it)) }
                    )
                    
                    FormatOptionItem(
                        label = "标准化引号",
                        checked = uiState.options.normalizeQuotes,
                        onCheckedChange = { viewModel.updateOption(uiState.options.copy(normalizeQuotes = it)) }
                    )
                    
                    FormatOptionItem(
                        label = "标准化标点",
                        checked = uiState.options.normalizePunctuation,
                        onCheckedChange = { viewModel.updateOption(uiState.options.copy(normalizePunctuation = it)) }
                    )
                }
            }
            
            // 输入文本
            OutlinedTextField(
                value = uiState.inputText,
                onValueChange = { viewModel.updateInputText(it) },
                label = { Text("原始文本") },
                placeholder = { Text("粘贴需要排版的文本") },
                minLines = 5,
                maxLines = 10,
                modifier = Modifier.fillMaxWidth()
            )
            
            // 排版按钮
            Button(
                onClick = { viewModel.format(selectedPlatform) },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.inputText.isNotBlank()
            ) {
                Icon(Icons.Default.FormatAlignLeft, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("开始排版")
            }
            
            // 统计信息
            if (uiState.inputText.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatChip(
                        label = "字数",
                        value = uiState.wordCount.toString()
                    )
                    StatChip(
                        label = "段落",
                        value = uiState.paragraphCount.toString()
                    )
                    StatChip(
                        label = "字符",
                        value = uiState.charCount.toString()
                    )
                }
            }
            
            // 输出结果
            if (uiState.outputText.isNotBlank()) {
                HorizontalDivider()
                
                Text(
                    text = "排版结果",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = uiState.outputText,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(onClick = { viewModel.copyOutput() }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("复制")
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 格式选项项
 */
@Composable
private fun FormatOptionItem(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

/**
 * 统计标签
 */
@Composable
private fun StatChip(
    label: String,
    value: String
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
