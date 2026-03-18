package com.zhibi.writer.data.local.dao

import androidx.room.*
import com.zhibi.writer.data.local.entity.ChapterEntity
import com.zhibi.writer.data.local.entity.ChapterStatus
import com.zhibi.writer.data.local.entity.VolumeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

/**
 * 章节数据访问对象
 */
@Dao
interface ChapterDao {
    
    // ========== 章节查询 ==========
    
    /**
     * 获取作品的所有章节
     */
    @Query("SELECT * FROM chapters WHERE workId = :workId ORDER BY `order` ASC")
    fun getChaptersByWork(workId: Long): Flow<List<ChapterEntity>>
    
    /**
     * 获取作品指定卷的章节
     */
    @Query("SELECT * FROM chapters WHERE workId = :workId AND volumeId = :volumeId ORDER BY `order` ASC")
    fun getChaptersByVolume(workId: Long, volumeId: Long): Flow<List<ChapterEntity>>
    
    /**
     * 获取章节详情
     */
    @Query("SELECT * FROM chapters WHERE id = :chapterId")
    suspend fun getChapterById(chapterId: Long): ChapterEntity?
    
    /**
     * 获取章节详情（Flow）
     */
    @Query("SELECT * FROM chapters WHERE id = :chapterId")
    fun getChapterByIdFlow(chapterId: Long): Flow<ChapterEntity?>
    
    /**
     * 获取作品的最新章节
     */
    @Query("SELECT * FROM chapters WHERE workId = :workId ORDER BY `order` DESC LIMIT 1")
    suspend fun getLatestChapter(workId: Long): ChapterEntity?
    
    /**
     * 获取下一章
     */
    @Query("SELECT * FROM chapters WHERE workId = :workId AND `order` > :currentOrder ORDER BY `order` ASC LIMIT 1")
    suspend fun getNextChapter(workId: Long, currentOrder: Int): ChapterEntity?
    
    /**
     * 获取上一章
     */
    @Query("SELECT * FROM chapters WHERE workId = :workId AND `order` < :currentOrder ORDER BY `order` DESC LIMIT 1")
    suspend fun getPreviousChapter(workId: Long, currentOrder: Int): ChapterEntity?
    
    /**
     * 搜索章节内容
     */
    @Query("SELECT * FROM chapters WHERE workId = :workId AND (title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%')")
    fun searchChapters(workId: Long, query: String): Flow<List<ChapterEntity>>
    
    /**
     * 获取章节总数
     */
    @Query("SELECT COUNT(*) FROM chapters WHERE workId = :workId")
    fun getChapterCount(workId: Long): Flow<Int>
    
    /**
     * 获取作品的总字数
     */
    @Query("SELECT SUM(wordCount) FROM chapters WHERE workId = :workId")
    fun getTotalWordCount(workId: Long): Flow<Int?>
    
    // ========== 章节插入 ==========
    
    /**
     * 插入章节
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: ChapterEntity): Long
    
    /**
     * 批量插入章节
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<ChapterEntity>)
    
    // ========== 章节更新 ==========
    
    /**
     * 更新章节
     */
    @Update
    suspend fun updateChapter(chapter: ChapterEntity)
    
    /**
     * 更新章节内容
     */
    @Query("UPDATE chapters SET content = :content, wordCount = :wordCount, updatedAt = :updatedAt WHERE id = :chapterId")
    suspend fun updateContent(chapterId: Long, content: String, wordCount: Int, updatedAt: LocalDateTime)
    
    /**
     * 更新章节标题
     */
    @Query("UPDATE chapters SET title = :title, updatedAt = :updatedAt WHERE id = :chapterId")
    suspend fun updateTitle(chapterId: Long, title: String, updatedAt: LocalDateTime)
    
    /**
     * 更新章节状态
     */
    @Query("UPDATE chapters SET status = :status, updatedAt = :updatedAt WHERE id = :chapterId")
    suspend fun updateStatus(chapterId: Long, status: ChapterStatus, updatedAt: LocalDateTime)
    
    /**
     * 更新章节排序
     */
    @Query("UPDATE chapters SET `order` = :order WHERE id = :chapterId")
    suspend fun updateOrder(chapterId: Long, order: Int)
    
    /**
     * 批量更新章节排序
     */
    @Transaction
    suspend fun updateOrders(chapters: List<Pair<Long, Int>>) {
        chapters.forEach { (id, order) ->
            updateOrder(id, order)
        }
    }
    
    /**
     * 锁定/解锁章节
     */
    @Query("UPDATE chapters SET isLocked = :isLocked WHERE id = :chapterId")
    suspend fun updateLocked(chapterId: Long, isLocked: Boolean)
    
    // ========== 章节删除 ==========
    
    /**
     * 删除章节
     */
    @Delete
    suspend fun deleteChapter(chapter: ChapterEntity)
    
    /**
     * 根据ID删除章节
     */
    @Query("DELETE FROM chapters WHERE id = :chapterId")
    suspend fun deleteChapterById(chapterId: Long)
    
    // ========== 卷管理 ==========
    
    /**
     * 获取作品的所有卷
     */
    @Query("SELECT * FROM volumes WHERE workId = :workId ORDER BY `order` ASC")
    fun getVolumesByWork(workId: Long): Flow<List<VolumeEntity>>
    
    /**
     * 获取卷详情
     */
    @Query("SELECT * FROM volumes WHERE id = :volumeId")
    suspend fun getVolumeById(volumeId: Long): VolumeEntity?
    
    /**
     * 插入卷
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVolume(volume: VolumeEntity): Long
    
    /**
     * 更新卷
     */
    @Update
    suspend fun updateVolume(volume: VolumeEntity)
    
    /**
     * 删除卷
     */
    @Delete
    suspend fun deleteVolume(volume: VolumeEntity)
}
