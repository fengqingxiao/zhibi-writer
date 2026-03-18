package com.zhibi.writer.data.local.repository

import com.zhibi.writer.data.local.dao.HistoryDao
import com.zhibi.writer.data.local.entity.*
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 历史记录仓库
 */
@Singleton
class HistoryRepository @Inject constructor(
    private val historyDao: HistoryDao
) {
    // 章节历史版本
    fun getChapterHistory(chapterId: Long): Flow<List<ChapterHistoryEntity>> = 
        historyDao.getChapterHistory(chapterId)
    
    suspend fun getHistoryById(id: Long): ChapterHistoryEntity? = 
        historyDao.getHistoryById(id)
    
    suspend fun insertHistory(history: ChapterHistoryEntity): Long = 
        historyDao.insertHistory(history)
    
    suspend fun deleteOldHistory(chapterId: Long, before: LocalDateTime) = 
        historyDao.deleteOldHistory(chapterId, before)
    
    suspend fun deleteAllHistoryForChapter(chapterId: Long) = 
        historyDao.deleteAllHistoryForChapter(chapterId)
    
    // 写作记录
    fun getWritingRecords(workId: Long): Flow<List<WritingRecordEntity>> = 
        historyDao.getWritingRecords(workId)
    
    fun getWritingRecordsBetween(
        workId: Long, 
        startDate: LocalDateTime, 
        endDate: LocalDateTime
    ): Flow<List<WritingRecordEntity>> = 
        historyDao.getWritingRecordsBetween(workId, startDate, endDate)
    
    suspend fun getTotalWordsSince(workId: Long, startDate: LocalDateTime): Int? = 
        historyDao.getTotalWordsSince(workId, startDate)
    
    suspend fun insertWritingRecord(record: WritingRecordEntity) = 
        historyDao.insertWritingRecord(record)
    
    // 已删除章节
    fun getDeletedChapters(workId: Long): Flow<List<DeletedChapterEntity>> = 
        historyDao.getDeletedChapters(workId)
    
    suspend fun getDeletedChapterById(id: Long): DeletedChapterEntity? = 
        historyDao.getDeletedChapterById(id)
    
    suspend fun insertDeletedChapter(deletedChapter: DeletedChapterEntity): Long = 
        historyDao.insertDeletedChapter(deletedChapter)
    
    suspend fun deleteDeletedChapter(deletedChapter: DeletedChapterEntity) = 
        historyDao.deleteDeletedChapter(deletedChapter)
    
    suspend fun deleteExpiredChapters(now: LocalDateTime) = 
        historyDao.deleteExpiredChapters(now)
    
    // 自动保存缓存
    suspend fun getAutoSaveCache(chapterId: Long): AutoSaveCacheEntity? = 
        historyDao.getAutoSaveCache(chapterId)
    
    suspend fun saveAutoSaveCache(cache: AutoSaveCacheEntity) = 
        historyDao.saveAutoSaveCache(cache)
    
    suspend fun deleteAutoSaveCache(chapterId: Long) = 
        historyDao.deleteAutoSaveCache(chapterId)
}
