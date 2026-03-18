package com.zhibi.writer.ui.screens.publish

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhibi.writer.data.local.entity.ChapterEntity
import com.zhibi.writer.data.local.repository.ChapterRepository
import com.zhibi.writer.data.model.PlatformType
import com.zhibi.writer.data.model.PublishStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

/**
 * 发布管理ViewModel
 */
@HiltViewModel
class PublishViewModel @Inject constructor(
    private val chapterRepository: ChapterRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PublishUiState())
    val uiState = _uiState.asStateFlow()
    
    fun loadChapters(workId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            chapterRepository.getChaptersByWork(workId).collect { chapters ->
                val publishedCount = chapters.count { /* TODO: 检查发布状态 */ false }
                _uiState.update { 
                    it.copy(
                        chapters = chapters,
                        isLoading = false,
                        publishedCount = publishedCount,
                        pendingCount = chapters.size - publishedCount
                    )
                }
            }
        }
    }
    
    fun publishChapter(chapterId: Long, platforms: List<PlatformType>) {
        viewModelScope.launch {
            // TODO: 实现发布逻辑
            // 1. 获取章节内容
            // 2. 格式化文本
            // 3. 调用各平台API发布
            
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val statuses = _uiState.value.publishStatuses.toMutableMap()
            statuses[chapterId] = PublishStatus.PENDING
            
            _uiState.update { 
                it.copy(publishStatuses = statuses)
            }
        }
    }
    
    fun schedulePublish(chapterId: Long, scheduledTime: LocalDateTime) {
        viewModelScope.launch {
            // TODO: 实现定时发布
            // 1. 保存定时任务
            // 2. 设置系统定时器
            
            val statuses = _uiState.value.publishStatuses.toMutableMap()
            statuses[chapterId] = PublishStatus.SCHEDULED
            
            _uiState.update { 
                it.copy(publishStatuses = statuses)
            }
        }
    }
    
    fun cancelSchedule(chapterId: Long) {
        viewModelScope.launch {
            // TODO: 取消定时发布
            
            val statuses = _uiState.value.publishStatuses.toMutableMap()
            statuses.remove(chapterId)
            
            _uiState.update { 
                it.copy(publishStatuses = statuses)
            }
        }
    }
}

/**
 * 发布管理UI状态
 */
data class PublishUiState(
    val isLoading: Boolean = true,
    val chapters: List<ChapterEntity> = emptyList(),
    val publishStatuses: Map<Long, PublishStatus> = emptyMap(),
    val publishedCount: Int = 0,
    val pendingCount: Int = 0,
    val availablePlatforms: List<PlatformType> = listOf(
        PlatformType.FANQIE,
        PlatformType.QIMAO,
        PlatformType.YUEWEN
    )
)
