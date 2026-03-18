package com.zhibi.writer.ui.screens.outline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhibi.writer.data.local.entity.OutlineNodeEntity
import com.zhibi.writer.data.local.entity.OutlineNodeType
import com.zhibi.writer.data.local.entity.OutlineStatus
import com.zhibi.writer.data.local.repository.OutlineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

/**
 * 大纲ViewModel
 */
@HiltViewModel
class OutlineViewModel @Inject constructor(
    private val outlineRepository: OutlineRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(OutlineUiState())
    val uiState = _uiState.asStateFlow()
    
    private var currentNode: OutlineNodeEntity? = null
    
    fun loadNode(nodeId: Long) {
        viewModelScope.launch {
            val node = outlineRepository.getNodeById(nodeId)
            currentNode = node
            
            _uiState.update {
                it.copy(
                    title = node?.title ?: "",
                    content = node?.content ?: "",
                    nodeType = node?.nodeType ?: OutlineNodeType.PLOT,
                    status = node?.status ?: OutlineStatus.PLANNED,
                    estimatedWords = node?.estimatedWords ?: 0,
                    tags = node?.tags ?: emptyList()
                )
            }
        }
    }
    
    fun initNewNode(workId: Long, parentId: Long?) {
        viewModelScope.launch {
            val parent = parentId?.let { outlineRepository.getNodeById(it) }
            
            _uiState.update {
                it.copy(
                    workId = workId,
                    parentId = parentId,
                    level = (parent?.level ?: -1) + 1,
                    nodeType = when {
                        parent == null -> OutlineNodeType.VOLUME
                        parent.level == 0 -> OutlineNodeType.CHAPTER
                        else -> OutlineNodeType.PLOT
                    }
                )
            }
        }
    }
    
    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }
    
    fun updateContent(content: String) {
        _uiState.update { it.copy(content = content) }
    }
    
    fun updateNodeType(type: OutlineNodeType) {
        _uiState.update { it.copy(nodeType = type) }
    }
    
    fun updateStatus(status: OutlineStatus) {
        _uiState.update { it.copy(status = status) }
    }
    
    fun updateEstimatedWords(words: Int) {
        _uiState.update { it.copy(estimatedWords = words) }
    }
    
    fun updateTags(tags: List<String>) {
        _uiState.update { it.copy(tags = tags) }
    }
    
    fun saveNode() {
        viewModelScope.launch {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            
            val node = OutlineNodeEntity(
                id = currentNode?.id ?: 0,
                workId = _uiState.value.workId,
                parentId = _uiState.value.parentId,
                level = _uiState.value.level,
                order = currentNode?.order ?: 0,
                title = _uiState.value.title,
                content = _uiState.value.content,
                nodeType = _uiState.value.nodeType,
                status = _uiState.value.status,
                estimatedWords = _uiState.value.estimatedWords,
                tags = _uiState.value.tags,
                createdAt = currentNode?.createdAt ?: now,
                updatedAt = now
            )
            
            outlineRepository.insertNode(node)
        }
    }
    
    fun deleteNode() {
        viewModelScope.launch {
            currentNode?.let { node ->
                outlineRepository.deleteNodeRecursive(node.id)
            }
        }
    }
}

/**
 * 大纲UI状态
 */
data class OutlineUiState(
    val workId: Long = 0,
    val parentId: Long? = null,
    val level: Int = 0,
    val title: String = "",
    val content: String = "",
    val nodeType: OutlineNodeType = OutlineNodeType.PLOT,
    val status: OutlineStatus = OutlineStatus.PLANNED,
    val estimatedWords: Int = 0,
    val tags: List<String> = emptyList()
)
