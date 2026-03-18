package com.zhibi.writer.ui.screens.chapters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhibi.writer.data.local.entity.ChapterEntity
import com.zhibi.writer.data.local.entity.ChapterStatus
import com.zhibi.writer.data.local.repository.ChapterRepository
import com.zhibi.writer.data.local.repository.WorkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

/**
 * 章节列表ViewModel
 */
@HiltViewModel
class ChapterListViewModel @Inject constructor(
    private val chapterRepository: ChapterRepository,
    private val workRepository: WorkRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ChapterListUiState())
    val uiState: StateFlow<ChapterListUiState> = _uiState.asStateFlow()
    
    fun loadChapters(workId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, workId = workId) }
            
            // 加载作品信息
            workRepository.getWorkById(workId)?.let { work ->
                _uiState.update { it.copy(workTitle = work.title) }
            }
            
            // 加载章节列表
            chapterRepository.getChaptersByWork(workId)
                .catch { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
                .collect { chapters ->
                    val totalWords = chapters.sumOf { it.wordCount }
                    _uiState.update { 
                        it.copy(
                            chapters = chapters,
                            totalWords = totalWords,
                            isLoading = false
                        )
                    }
                }
        }
    }
    
    fun createChapter(workId: Long, title: String) {
        viewModelScope.launch {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val order = _uiState.value.chapters.size
            
            val chapter = ChapterEntity(
                workId = workId,
                title = title.ifEmpty { "第${order + 1}章" },
                content = "",
                order = order,
                status = ChapterStatus.DRAFT,
                createdAt = now,
                updatedAt = now
            )
            
            chapterRepository.insertChapter(chapter)
            
            // 更新作品章节数
            workRepository.getWorkById(workId)?.let { work ->
                workRepository.updateWordCount(workId, work.totalWordCount, now)
            }
        }
    }
    
    fun toggleLock(chapterId: Long) {
        viewModelScope.launch {
            val chapter = chapterRepository.getChapterById(chapterId) ?: return@launch
            chapterRepository.updateLocked(chapterId, !chapter.isLocked)
        }
    }
    
    fun deleteChapter(chapterId: Long) {
        viewModelScope.launch {
            chapterRepository.deleteChapterById(chapterId)
        }
    }
    
    fun moveChapters(chapterIds: List<Long>, targetPosition: Int) {
        viewModelScope.launch {
            // TODO: 实现章节移动
        }
    }
    
    fun mergeChapters(chapterIds: List<Long>) {
        viewModelScope.launch {
            // TODO: 实现章节合并
        }
    }
}

/**
 * 章节列表UI状态
 */
data class ChapterListUiState(
    val isLoading: Boolean = true,
    val workId: Long = 0,
    val workTitle: String? = null,
    val chapters: List<ChapterEntity> = emptyList(),
    val totalWords: Int = 0,
    val error: String? = null
)
