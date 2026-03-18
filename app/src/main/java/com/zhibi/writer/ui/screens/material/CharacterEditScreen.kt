package com.zhibi.writer.ui.screens.material

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zhibi.writer.data.local.entity.CharacterData
import com.zhibi.writer.data.local.entity.ForeshadowData
import com.zhibi.writer.data.local.entity.MaterialCategory
import com.zhibi.writer.data.local.entity.MaterialEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * 人物设定编辑界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterEditScreen(
    materialId: Long,
    viewModel: MaterialViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // 人物数据
    var name by remember { mutableStateOf("") }
    var alias by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var appearance by remember { mutableStateOf("") }
    var personality by remember { mutableStateOf("") }
    var background by remember { mutableStateOf("") }
    var abilities by remember { mutableStateOf("") }
    var growthArc by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    
    LaunchedEffect(materialId) {
        viewModel.loadMaterial(materialId)
    }
    
    // 加载已有数据
    LaunchedEffect(uiState.material) {
        uiState.material?.let { material ->
            name = material.title
            try {
                val data = Json.decodeFromString<CharacterData>(material.content)
                alias = data.alias ?: ""
                gender = data.gender ?: ""
                age = data.age ?: ""
                appearance = data.appearance ?: ""
                personality = data.personality ?: ""
                background = data.background ?: ""
                abilities = data.abilities.joinToString("\n")
                growthArc = data.growthArc ?: ""
                notes = data.notes ?: ""
            } catch (e: Exception) {
                // 解析失败，使用空数据
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("人物设定") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val data = CharacterData(
                            name = name,
                            alias = alias.ifEmpty { null },
                            gender = gender.ifEmpty { null },
                            age = age.ifEmpty { null },
                            appearance = appearance.ifEmpty { null },
                            personality = personality.ifEmpty { null },
                            background = background.ifEmpty { null },
                            abilities = abilities.split("\n").filter { it.isNotEmpty() },
                            growthArc = growthArc.ifEmpty { null },
                            notes = notes.ifEmpty { null }
                        )
                        viewModel.saveCharacter(name, data)
                        onNavigateBack()
                    }) {
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
            // 基本信息
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "基本信息",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("姓名") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = alias,
                            onValueChange = { alias = it },
                            label = { Text("别名/昵称") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        
                        OutlinedTextField(
                            value = gender,
                            onValueChange = { gender = it },
                            label = { Text("性别") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        
                        OutlinedTextField(
                            value = age,
                            onValueChange = { age = it },
                            label = { Text("年龄") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            // 外貌与性格
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "外貌与性格",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    OutlinedTextField(
                        value = appearance,
                        onValueChange = { appearance = it },
                        label = { Text("外貌描述") },
                        minLines = 3,
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    OutlinedTextField(
                        value = personality,
                        onValueChange = { personality = it },
                        label = { Text("性格特点") },
                        minLines = 2,
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            // 背景故事
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "背景故事",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    OutlinedTextField(
                        value = background,
                        onValueChange = { background = it },
                        label = { Text("背景故事") },
                        minLines = 3,
                        maxLines = 6,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    OutlinedTextField(
                        value = abilities,
                        onValueChange = { abilities = it },
                        label = { Text("能力/技能（每行一个）") },
                        minLines = 2,
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            // 成长线
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "成长线",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    OutlinedTextField(
                        value = growthArc,
                        onValueChange = { growthArc = it },
                        label = { Text("人物成长线") },
                        minLines = 2,
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("备注") },
                        minLines = 2,
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
