package com.zhibi.writer.ui.screens.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhibi.writer.data.local.entity.ChapterEntity
import com.zhibi.writer.data.local.entity.ChapterStatus
import com.zhibi.writer.data.local.entity.SyncStatus
import com.zhibi.writer.data.local.repository.ChapterRepository
import com.zhibi.writer.data.local.repository.HistoryRepository
import com.zhibi.writer.data.local.repository.WorkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

/**
 * 编辑器ViewModel
 */
@HiltViewModel
class EditorViewModel @Inject constructor(
    private val chapterRepository: ChapterRepository,
    private val workRepository: WorkRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState = _uiState.asStateFlow()
    
    private var currentChapter: ChapterEntity? = null
    private var autoSaveJob: Job? = null
    private var undoStack = mutableListOf<String>()
    private var redoStack = mutableListOf<String>()
    
    fun loadChapter(workId: Long, chapterId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            if (chapterId == 0L) {
                // 新建章节
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                val order = chapterRepository.getLatestChapter(workId)?.order?.plus(1) ?: 0
                
                currentChapter = ChapterEntity(
                    workId = workId,
                    title = "",
                    content = "",
                    order = order,
                    status = ChapterStatus.DRAFT,
                    createdAt = now,
                    updatedAt = now
                )
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        chapterTitle = "",
                        content = "",
                        wordCount = 0
                    )
                }
            } else {
                // 加载现有章节
                val chapter = chapterRepository.getChapterById(chapterId)
                currentChapter = chapter
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        chapterTitle = chapter?.title ?: "",
                        content = chapter?.content ?: "",
                        wordCount = chapter?.wordCount ?: 0
                    )
                }
                
                // 保存初始状态到撤销栈
                undoStack.add(chapter?.content ?: "")
            }
        }
    }
    
    fun updateTitle(title: String) {
        _uiState.update { it.copy(chapterTitle = title) }
        scheduleAutoSave()
    }
    
    fun updateContent(content: String) {
        // 保存当前状态到撤销栈
        _uiState.value.content.let { current ->
            if (undoStack.lastOrNull() != current) {
                undoStack.add(current)
                redoStack.clear()
            }
        }
        
        _uiState.update { 
            it.copy(
                content = content,
                wordCount = content.length,
                canUndo = undoStack.size > 1,
                canRedo = false
            )
        }
        
        scheduleAutoSave()
    }
    
    fun undo() {
        if (undoStack.size > 1) {
            val current = undoStack.removeLast()
            redoStack.add(current)
            val previous = undoStack.last()
            
            _uiState.update { 
                it.copy(
                    content = previous,
                    wordCount = previous.length,
                    canUndo = undoStack.size > 1,
                    canRedo = true
                )
            }
        }
    }
    
    fun redo() {
        if (redoStack.isNotEmpty()) {
            val next = redoStack.removeLast()
            undoStack.add(next)
            
            _uiState.update { 
                it.copy(
                    content = next,
                    wordCount = next.length,
                    canUndo = undoStack.size > 1,
                    canRedo = redoStack.isNotEmpty()
                )
            }
        }
    }
    
    fun applyFormat(format: String) {
        val currentContent = _uiState.value.content
        val formattedContent = when (format) {
            "paragraph" -> currentContent + "\n\n    "  // 段落缩进
            "bold" -> currentContent + "****"  // 加粗占位
            "italic" -> currentContent + "**"  // 斜体占位
            "quote" -> currentContent + "\n「」"  // 中文引号
            else -> currentContent
        }
        
        updateContent(formattedContent)
    }
    
    fun saveContent() {
        viewModelScope.launch {
            val chapter = currentChapter ?: return@launch
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            
            val updatedChapter = chapter.copy(
                title = _uiState.value.chapterTitle ?: "",
                content = _uiState.value.content,
                wordCount = _uiState.value.wordCount,
                updatedAt = now
            )
            
            val id = chapterRepository.insertChapter(updatedChapter)
            
            // 更新作品统计
            val totalWords = chapterRepository.getTotalWordCount(chapter.workId).first() ?: 0
            workRepository.updateWordCount(chapter.workId, totalWords.toLong(), now)
            
            // 保存历史版本
            if (chapter.content != updatedChapter.content && chapter.id != 0L) {
                historyRepository.insertHistory(
                    com.zhibi.writer.data.local.entity.ChapterHistoryEntity(
                        chapterId = chapter.id,
                        content = chapter.content,
                        wordCount = chapter.wordCount,
                        createdAt = now
                    )
                )
            }
            
            currentChapter = updatedChapter.copy(id = id)
        }
    }
    
    private fun scheduleAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            delay(2000) // 2秒后自动保存
            saveContent()
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        // 退出时强制保存
        saveContent()
    }
}

/**
 * 编辑器UI状态
 */
data class EditorUiState(
    val isLoading: Boolean = false,
    val chapterTitle: String? = null,
    val content: String = "",
    val wordCount: Int = 0,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val error: String? = null
)
