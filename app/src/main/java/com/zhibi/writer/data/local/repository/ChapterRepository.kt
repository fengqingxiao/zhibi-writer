package com.zhibi.writer.data.local.repository

import com.zhibi.writer.data.local.dao.ChapterDao
import com.zhibi.writer.data.local.entity.ChapterEntity
import com.zhibi.writer.data.local.entity.ChapterStatus
import com.zhibi.writer.data.local.entity.VolumeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 章节仓库
 */
@Singleton
class ChapterRepository @Inject constructor(
    private val chapterDao: ChapterDao
) {
    // 章节操作
    fun getChaptersByWork(workId: Long): Flow<List<ChapterEntity>> = 
        chapterDao.getChaptersByWork(workId)
    
    fun getChaptersByVolume(workId: Long, volumeId: Long): Flow<List<ChapterEntity>> = 
        chapterDao.getChaptersByVolume(workId, volumeId)
    
    suspend fun getChapterById(chapterId: Long): ChapterEntity? = 
        chapterDao.getChapterById(chapterId)
    
    fun getChapterByIdFlow(chapterId: Long): Flow<ChapterEntity?> = 
        chapterDao.getChapterByIdFlow(chapterId)
    
    suspend fun getLatestChapter(workId: Long): ChapterEntity? = 
        chapterDao.getLatestChapter(workId)
    
    suspend fun getNextChapter(workId: Long, currentOrder: Int): ChapterEntity? = 
        chapterDao.getNextChapter(workId, currentOrder)
    
    suspend fun getPreviousChapter(workId: Long, currentOrder: Int): ChapterEntity? = 
        chapterDao.getPreviousChapter(workId, currentOrder)
    
    fun searchChapters(workId: Long, query: String): Flow<List<ChapterEntity>> = 
        chapterDao.searchChapters(workId, query)
    
    fun getChapterCount(workId: Long): Flow<Int> = chapterDao.getChapterCount(workId)
    
    fun getTotalWordCount(workId: Long): Flow<Int?> = chapterDao.getTotalWordCount(workId)
    
    suspend fun insertChapter(chapter: ChapterEntity): Long = 
        chapterDao.insertChapter(chapter)
    
    suspend fun insertChapters(chapters: List<ChapterEntity>) = 
        chapterDao.insertChapters(chapters)
    
    suspend fun updateChapter(chapter: ChapterEntity) = chapterDao.updateChapter(chapter)
    
    suspend fun updateContent(chapterId: Long, content: String, wordCount: Int, updatedAt: LocalDateTime) =
        chapterDao.updateContent(chapterId, content, wordCount, updatedAt)
    
    suspend fun updateTitle(chapterId: Long, title: String, updatedAt: LocalDateTime) =
        chapterDao.updateTitle(chapterId, title, updatedAt)
    
    suspend fun updateStatus(chapterId: Long, status: ChapterStatus, updatedAt: LocalDateTime) =
        chapterDao.updateStatus(chapterId, status, updatedAt)
    
    suspend fun updateOrder(chapterId: Long, order: Int) = 
        chapterDao.updateOrder(chapterId, order)
    
    suspend fun updateOrders(chapters: List<Pair<Long, Int>>) = 
        chapterDao.updateOrders(chapters)
    
    suspend fun updateLocked(chapterId: Long, isLocked: Boolean) = 
        chapterDao.updateLocked(chapterId, isLocked)
    
    suspend fun deleteChapter(chapter: ChapterEntity) = chapterDao.deleteChapter(chapter)
    
    suspend fun deleteChapterById(chapterId: Long) = chapterDao.deleteChapterById(chapterId)
    
    // 卷操作
    fun getVolumesByWork(workId: Long): Flow<List<VolumeEntity>> = 
        chapterDao.getVolumesByWork(workId)
    
    suspend fun getVolumeById(volumeId: Long): VolumeEntity? = 
        chapterDao.getVolumeById(volumeId)
    
    suspend fun insertVolume(volume: VolumeEntity): Long = 
        chapterDao.insertVolume(volume)
    
    suspend fun updateVolume(volume: VolumeEntity) = 
        chapterDao.updateVolume(volume)
    
    suspend fun deleteVolume(volume: VolumeEntity) = 
        chapterDao.deleteVolume(volume)
}
