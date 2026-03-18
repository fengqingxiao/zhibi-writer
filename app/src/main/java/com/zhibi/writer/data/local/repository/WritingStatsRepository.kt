package com.zhibi.writer.data.local.repository

import com.zhibi.writer.data.local.dao.HistoryDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 写作统计仓库
 */
@Singleton
class WritingStatsRepository @Inject constructor(
    private val historyDao: HistoryDao
) {
    /**
     * 获取今日字数
     */
    fun getTodayWords(): Flow<Int> {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val startOfDay = today.atStartOfDayIn(TimeZone.currentSystemDefault())
            .toLocalDateTime(TimeZone.currentSystemDefault())
        
        return historyDao.getWritingRecordsBetween(0, startOfDay, startOfDay.plus(DatePeriod(days = 1)))
            .map { records -> records.sumOf { it.netWords } }
    }
    
    /**
     * 获取连续更新天数
     */
    fun getStreakDays(): Flow<Int> = kotlinx.coroutines.flow.flow {
        var streak = 0
        var currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        
        while (true) {
            val startOfDay = currentDate.atStartOfDayIn(TimeZone.currentSystemDefault())
                .toLocalDateTime(TimeZone.currentSystemDefault())
            val endOfDay = startOfDay.plus(DatePeriod(days = 1))
            
            val records = historyDao.getWritingRecordsBetween(0, startOfDay, endOfDay)
                .first()
            
            if (records.isNotEmpty() && records.sumOf { it.netWords } > 0) {
                streak++
                currentDate = currentDate.minus(DatePeriod(days = 1))
            } else {
                break
            }
        }
        
        emit(streak)
    }
}
