package com.zhibi.writer.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhibi.writer.data.local.entity.WorkEntity
import com.zhibi.writer.data.local.entity.WorkStatus
import com.zhibi.writer.data.local.entity.SyncStatus
import com.zhibi.writer.data.local.repository.WorkRepository
import com.zhibi.writer.data.local.repository.WritingStatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

/**
 * 首页ViewModel
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val workRepository: WorkRepository,
    private val writingStatsRepository: WritingStatsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadWorks()
        loadStats()
    }
    
    private fun loadWorks() {
        viewModelScope.launch {
            workRepository.getAllWorks()
                .catch { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
                .collect { works ->
                    _uiState.update { it.copy(works = works, isLoading = false) }
                }
        }
    }
    
    private fun loadStats() {
        viewModelScope.launch {
            // 获取总字数
            workRepository.getTotalWordCount()
                .collect { totalWords ->
                    _uiState.update { it.copy(totalWords = totalWords ?: 0L) }
                }
        }
        
        viewModelScope.launch {
            // 获取今日字数
            writingStatsRepository.getTodayWords()
                .collect { todayWords ->
                    _uiState.update { it.copy(todayWords = todayWords) }
                }
        }
        
        viewModelScope.launch {
            // 获取连续更新天数
            writingStatsRepository.getStreakDays()
                .collect { streakDays ->
                    _uiState.update { it.copy(streakDays = streakDays) }
                }
        }
    }
    
    fun createWork(title: String, summary: String) {
        viewModelScope.launch {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val work = WorkEntity(
                title = title,
                summary = summary,
                status = WorkStatus.DRAFT,
                createdAt = now,
                updatedAt = now,
                lastEditedAt = now
            )
            workRepository.insertWork(work)
        }
    }
    
    fun deleteWork(workId: Long) {
        viewModelScope.launch {
            workRepository.deleteWorkById(workId)
        }
    }
    
    fun toggleTop(workId: Long, isTopped: Boolean) {
        viewModelScope.launch {
            workRepository.updateTopped(workId, isTopped)
        }
    }
}

/**
 * 首页UI状态
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    val works: List<WorkEntity> = emptyList(),
    val todayWords: Int = 0,
    val totalWords: Long = 0L,
    val streakDays: Int = 0,
    val error: String? = null
)
