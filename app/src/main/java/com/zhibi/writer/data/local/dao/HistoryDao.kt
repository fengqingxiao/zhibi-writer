package com.zhibi.writer.data.local.dao

import androidx.room.*
import com.zhibi.writer.data.local.entity.*
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

/**
 * 历史记录数据访问对象
 */
@Dao
interface HistoryDao {
    
    // ========== 章节历史版本 ==========
    
    @Query("SELECT * FROM chapter_history WHERE chapterId = :chapterId ORDER BY createdAt DESC")
    fun getChapterHistory(chapterId: Long): Flow<List<ChapterHistoryEntity>>
    
    @Query("SELECT * FROM chapter_history WHERE id = :id")
    suspend fun getHistoryById(id: Long): ChapterHistoryEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: ChapterHistoryEntity): Long
    
    @Query("DELETE FROM chapter_history WHERE chapterId = :chapterId AND createdAt < :before")
    suspend fun deleteOldHistory(chapterId: Long, before: LocalDateTime)
    
    @Query("DELETE FROM chapter_history WHERE chapterId = :chapterId")
    suspend fun deleteAllHistoryForChapter(chapterId: Long)
    
    // ========== 写作记录 ==========
    
    @Query("SELECT * FROM writing_records WHERE workId = :workId ORDER BY date DESC")
    fun getWritingRecords(workId: Long): Flow<List<WritingRecordEntity>>
    
    @Query("SELECT * FROM writing_records WHERE workId = :workId AND date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getWritingRecordsBetween(workId: Long, startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<WritingRecordEntity>>
    
    @Query("SELECT SUM(netWords) FROM writing_records WHERE workId = :workId AND date >= :startDate")
    suspend fun getTotalWordsSince(workId: Long, startDate: LocalDateTime): Int?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWritingRecord(record: WritingRecordEntity)
    
    // ========== 已删除章节 ==========
    
    @Query("SELECT * FROM deleted_chapters WHERE workId = :workId ORDER BY deletedAt DESC")
    fun getDeletedChapters(workId: Long): Flow<List<DeletedChapterEntity>>
    
    @Query("SELECT * FROM deleted_chapters WHERE id = :id")
    suspend fun getDeletedChapterById(id: Long): DeletedChapterEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeletedChapter(deletedChapter: DeletedChapterEntity): Long
    
    @Delete
    suspend fun deleteDeletedChapter(deletedChapter: DeletedChapterEntity)
    
    @Query("DELETE FROM deleted_chapters WHERE expiresAt < :now")
    suspend fun deleteExpiredChapters(now: LocalDateTime)
    
    // ========== 自动保存缓存 ==========
    
    @Query("SELECT * FROM auto_save_cache WHERE chapterId = :chapterId")
    suspend fun getAutoSaveCache(chapterId: Long): AutoSaveCacheEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAutoSaveCache(cache: AutoSaveCacheEntity)
    
    @Query("DELETE FROM auto_save_cache WHERE chapterId = :chapterId")
    suspend fun deleteAutoSaveCache(chapterId: Long)
}
