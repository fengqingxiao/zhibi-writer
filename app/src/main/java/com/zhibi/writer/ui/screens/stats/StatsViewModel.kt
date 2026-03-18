package com.zhibi.writer.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhibi.writer.data.local.repository.WorkRepository
import com.zhibi.writer.data.local.repository.WritingStatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 数据统计ViewModel
 */
@HiltViewModel
class StatsViewModel @Inject constructor(
    private val workRepository: WorkRepository,
    private val writingStatsRepository: WritingStatsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState = _uiState.asStateFlow()
    
    fun loadStats(workId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // TODO: 从数据库加载真实统计数据
            // 目前使用模拟数据
            
            _uiState.update { 
                it.copy(
                    isLoading = false,
                    todayWords = 3520,
                    todayTime = "2h 15m",
                    avgSpeed = 1567,
                    weekWords = 15680,
                    weekDays = 5,
                    streakDays = 12,
                    totalWords = 286540,
                    totalTime = "186h",
                    workCount = 3,
                    hourlyStats = mapOf(
                        6 to 500,
                        9 to 1200,
                        12 to 800,
                        15 to 350,
                        18 to 2000,
                        21 to 1500
                    ),
                    platformStats = listOf(
                        Pair("番茄小说", 12580L),
                        Pair("七猫小说", 8960L),
                        Pair("阅文集团", 5620L)
                    ),
                    totalIncome = 1256.80,
                    monthIncome = 358.50,
                    monthWords = 42560,
                    incomeRecords = listOf(
                        IncomeRecord("订阅收入", "2026-03", 256.50),
                        IncomeRecord("全勤奖", "2026-03", 100.00),
                        IncomeRecord("打赏收入", "2026-03", 2.00)
                    )
                )
            }
        }
    }
}

/**
 * 数据统计UI状态
 */
data class StatsUiState(
    val isLoading: Boolean = true,
    
    // 写作统计
    val todayWords: Int = 0,
    val todayTime: String = "0h 0m",
    val avgSpeed: Int = 0,
    val weekWords: Int = 0,
    val weekDays: Int = 0,
    val streakDays: Int = 0,
    val totalWords: Int = 0,
    val totalTime: String = "0h",
    val workCount: Int = 0,
    val hourlyStats: Map<Int, Int> = emptyMap(),
    
    // 平台数据
    val platformStats: List<Pair<String, Long>> = emptyList(),
    
    // 稿费收入
    val totalIncome: Double = 0.0,
    val monthIncome: Double = 0.0,
    val monthWords: Int = 0,
    val incomeRecords: List<IncomeRecord> = emptyList()
)
