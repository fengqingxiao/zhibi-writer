package com.zhibi.writer.data.local.dao

import androidx.room.*
import com.zhibi.writer.data.local.entity.WorkEntity
import com.zhibi.writer.data.local.entity.WorkStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

/**
 * 作品数据访问对象
 */
@Dao
interface WorkDao {
    
    // ========== 查询操作 ==========
    
    /**
     * 获取所有作品（按最后编辑时间降序）
     */
    @Query("SELECT * FROM works ORDER BY isTopped DESC, lastEditedAt DESC")
    fun getAllWorks(): Flow<List<WorkEntity>>
    
    /**
     * 获取指定状态的作品
     */
    @Query("SELECT * FROM works WHERE status = :status ORDER BY isTopped DESC, lastEditedAt DESC")
    fun getWorksByStatus(status: WorkStatus): Flow<List<WorkEntity>>
    
    /**
     * 搜索作品
     */
    @Query("SELECT * FROM works WHERE title LIKE '%' || :query || '%' ORDER BY lastEditedAt DESC")
    fun searchWorks(query: String): Flow<List<WorkEntity>>
    
    /**
     * 根据ID获取作品
     */
    @Query("SELECT * FROM works WHERE id = :id")
    suspend fun getWorkById(id: Long): WorkEntity?
    
    /**
     * 根据ID获取作品（Flow）
     */
    @Query("SELECT * FROM works WHERE id = :id")
    fun getWorkByIdFlow(id: Long): Flow<WorkEntity?>
    
    /**
     * 获取作品总数
     */
    @Query("SELECT COUNT(*) FROM works")
    fun getWorkCount(): Flow<Int>
    
    /**
     * 获取总字数
     */
    @Query("SELECT SUM(totalWordCount) FROM works")
    fun getTotalWordCount(): Flow<Long?>
    
    // ========== 插入操作 ==========
    
    /**
     * 插入作品
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWork(work: WorkEntity): Long
    
    /**
     * 批量插入作品
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorks(works: List<WorkEntity>)
    
    // ========== 更新操作 ==========
    
    /**
     * 更新作品
     */
    @Update
    suspend fun updateWork(work: WorkEntity)
    
    /**
     * 更新作品字数
     */
    @Query("UPDATE works SET totalWordCount = :wordCount, updatedAt = :updatedAt WHERE id = :workId")
    suspend fun updateWordCount(workId: Long, wordCount: Long, updatedAt: LocalDateTime)
    
    /**
     * 更新作品状态
     */
    @Query("UPDATE works SET status = :status, updatedAt = :updatedAt WHERE id = :workId")
    suspend fun updateStatus(workId: Long, status: WorkStatus, updatedAt: LocalDateTime)
    
    /**
     * 置顶/取消置顶
     */
    @Query("UPDATE works SET isTopped = :isTopped WHERE id = :workId")
    suspend fun updateTopped(workId: Long, isTopped: Boolean)
    
    /**
     * 更新最新章节信息
     */
    @Query("""
        UPDATE works 
        SET latestChapterTitle = :chapterTitle, 
            chapterCount = chapterCount + :delta,
            updatedAt = :updatedAt
        WHERE id = :workId
    """)
    suspend fun updateLatestChapter(workId: Long, chapterTitle: String, delta: Int, updatedAt: LocalDateTime)
    
    // ========== 删除操作 ==========
    
    /**
     * 删除作品
     */
    @Delete
    suspend fun deleteWork(work: WorkEntity)
    
    /**
     * 根据ID删除作品
     */
    @Query("DELETE FROM works WHERE id = :workId")
    suspend fun deleteWorkById(workId: Long)
    
    /**
     * 删除所有作品
     */
    @Query("DELETE FROM works")
    suspend fun deleteAllWorks()
}
