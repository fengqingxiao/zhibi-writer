package com.zhibi.writer.data.local.repository

import com.zhibi.writer.data.local.dao.WorkDao
import com.zhibi.writer.data.local.entity.WorkEntity
import com.zhibi.writer.data.local.entity.WorkStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 作品仓库
 */
@Singleton
class WorkRepository @Inject constructor(
    private val workDao: WorkDao
) {
    fun getAllWorks(): Flow<List<WorkEntity>> = workDao.getAllWorks()
    
    fun getWorksByStatus(status: WorkStatus): Flow<List<WorkEntity>> = 
        workDao.getWorksByStatus(status)
    
    fun searchWorks(query: String): Flow<List<WorkEntity>> = 
        workDao.searchWorks(query)
    
    suspend fun getWorkById(id: Long): WorkEntity? = workDao.getWorkById(id)
    
    fun getWorkByIdFlow(id: Long): Flow<WorkEntity?> = workDao.getWorkByIdFlow(id)
    
    fun getWorkCount(): Flow<Int> = workDao.getWorkCount()
    
    fun getTotalWordCount(): Flow<Long?> = workDao.getTotalWordCount()
    
    suspend fun insertWork(work: WorkEntity): Long = workDao.insertWork(work)
    
    suspend fun updateWork(work: WorkEntity) = workDao.updateWork(work)
    
    suspend fun updateWordCount(workId: Long, wordCount: Long, updatedAt: LocalDateTime) =
        workDao.updateWordCount(workId, wordCount, updatedAt)
    
    suspend fun updateStatus(workId: Long, status: WorkStatus, updatedAt: LocalDateTime) =
        workDao.updateStatus(workId, status, updatedAt)
    
    suspend fun updateTopped(workId: Long, isTopped: Boolean) =
        workDao.updateTopped(workId, isTopped)
    
    suspend fun deleteWork(work: WorkEntity) = workDao.deleteWork(work)
    
    suspend fun deleteWorkById(workId: Long) = workDao.deleteWorkById(workId)
}
