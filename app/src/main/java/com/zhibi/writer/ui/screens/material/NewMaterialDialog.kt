package com.zhibi.writer.ui.screens.material

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhibi.writer.data.local.entity.MaterialCategory

/**
 * 新建素材对话框
 */
@Composable
fun NewMaterialDialog(
    categories: Array<MaterialCategory>,
    onDismiss: () -> Unit,
    onConfirm: (MaterialCategory, String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(MaterialCategory.CHARACTER) }
    var title by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf<String?>(null) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("新建素材") },
        text = {
            Column {
                // 分类选择
                Text(
                    text = "素材类型",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 分类选择器
                var expanded by remember { mutableStateOf(false) }
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = getCategoryName(selectedCategory),
                        onValueChange = {},
                        readOnly = true,
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
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(getCategoryName(category)) },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = getCategoryIcon(category),
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 标题输入
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = null
                    },
                    label = { Text("素材名称") },
                    placeholder = { 
                        Text(
                            when (selectedCategory) {
                                MaterialCategory.CHARACTER -> "人物名"
                                MaterialCategory.WORLD -> "世界观名称"
                                MaterialCategory.FACTION -> "势力名称"
                                MaterialCategory.LOCATION -> "地点名称"
                                MaterialCategory.ITEM -> "道具名称"
                                MaterialCategory.FORESHADOW -> "伏笔描述"
                                MaterialCategory.INSPIRATION -> "灵感标题"
                                MaterialCategory.REFERENCE -> "参考资料"
                            }
                        )
                    },
                    isError = titleError != null,
                    supportingText = titleError?.let { { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isBlank()) {
                        titleError = "请输入素材名称"
                    } else {
                        onConfirm(selectedCategory, title)
                    }
                }
            ) {
                Text("创建")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

private fun getCategoryName(category: MaterialCategory): String {
    return when (category) {
        MaterialCategory.CHARACTER -> "人物设定"
        MaterialCategory.WORLD -> "世界观设定"
        MaterialCategory.FACTION -> "势力/组织"
        MaterialCategory.LOCATION -> "地点/场景"
        MaterialCategory.ITEM -> "道具/功法"
        MaterialCategory.FORESHADOW -> "伏笔线索"
        MaterialCategory.INSPIRATION -> "灵感碎片"
        MaterialCategory.REFERENCE -> "参考资料"
    }
}

private fun getCategoryIcon(category: MaterialCategory): androidx.compose.ui.graphics.vector.ImageVector {
    return when (category) {
        MaterialCategory.CHARACTER -> androidx.compose.material.icons.Icons.Default.Person
        MaterialCategory.WORLD -> androidx.compose.material.icons.Icons.Default.Public
        MaterialCategory.FACTION -> androidx.compose.material.icons.Icons.Default.Groups
        MaterialCategory.LOCATION -> androidx.compose.material.icons.Icons.Default.Place
        MaterialCategory.ITEM -> androidx.compose.material.icons.Icons.Default.Diamond
        MaterialCategory.FORESHADOW -> androidx.compose.material.icons.Icons.Default.AutoAwesome
        MaterialCategory.INSPIRATION -> androidx.compose.material.icons.Icons.Default.Lightbulb
        MaterialCategory.REFERENCE -> androidx.compose.material.icons.Icons.Default.MenuBook
    }
}
