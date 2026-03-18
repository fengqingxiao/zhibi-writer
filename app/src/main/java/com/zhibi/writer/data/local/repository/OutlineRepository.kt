package com.zhibi.writer.data.local.repository

import com.zhibi.writer.data.local.dao.OutlineDao
import com.zhibi.writer.data.local.entity.OutlineNodeEntity
import com.zhibi.writer.data.local.entity.OutlineStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 大纲仓库
 */
@Singleton
class OutlineRepository @Inject constructor(
    private val outlineDao: OutlineDao
) {
    fun getOutlineNodesByWork(workId: Long): Flow<List<OutlineNodeEntity>> = 
        outlineDao.getOutlineNodesByWork(workId)
    
    fun getRootNodes(workId: Long): Flow<List<OutlineNodeEntity>> = 
        outlineDao.getRootNodes(workId)
    
    fun getChildNodes(parentId: Long): Flow<List<OutlineNodeEntity>> = 
        outlineDao.getChildNodes(parentId)
    
    suspend fun getNodeById(id: Long): OutlineNodeEntity? = 
        outlineDao.getNodeById(id)
    
    fun getNodeByIdFlow(id: Long): Flow<OutlineNodeEntity?> = 
        outlineDao.getNodeByIdFlow(id)
    
    suspend fun getNodeByChapter(chapterId: Long): OutlineNodeEntity? = 
        outlineDao.getNodeByChapter(chapterId)
    
    fun getNodesByStatus(workId: Long, status: OutlineStatus): Flow<List<OutlineNodeEntity>> = 
        outlineDao.getNodesByStatus(workId, status)
    
    suspend fun insertNode(node: OutlineNodeEntity): Long = 
        outlineDao.insertNode(node)
    
    suspend fun insertNodes(nodes: List<OutlineNodeEntity>) = 
        outlineDao.insertNodes(nodes)
    
    suspend fun updateNode(node: OutlineNodeEntity) = 
        outlineDao.updateNode(node)
    
    suspend fun updateNodeContent(id: Long, title: String, content: String, updatedAt: LocalDateTime) =
        outlineDao.updateNodeContent(id, title, content, updatedAt)
    
    suspend fun updateNodeStatus(id: Long, status: OutlineStatus, updatedAt: LocalDateTime) =
        outlineDao.updateNodeStatus(id, status, updatedAt)
    
    suspend fun updateNodeOrder(id: Long, order: Int) = 
        outlineDao.updateNodeOrder(id, order)
    
    suspend fun updateParent(id: Long, parentId: Long?, level: Int) = 
        outlineDao.updateParent(id, parentId, level)
    
    suspend fun linkToChapter(id: Long, chapterId: Long?) = 
        outlineDao.linkToChapter(id, chapterId)
    
    suspend fun deleteNode(node: OutlineNodeEntity) = 
        outlineDao.deleteNode(node)
    
    suspend fun deleteNodeById(id: Long) = 
        outlineDao.deleteNodeById(id)
    
    suspend fun deleteNodeRecursive(id: Long) = 
        outlineDao.deleteNodeRecursive(id)
    
    suspend fun deleteNodesByWork(workId: Long) = 
        outlineDao.deleteNodesByWork(workId)
}
