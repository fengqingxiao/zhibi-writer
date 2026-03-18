package com.zhibi.writer.data.local.dao

import androidx.room.*
import com.zhibi.writer.data.local.entity.OutlineNodeEntity
import com.zhibi.writer.data.local.entity.OutlineStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

/**
 * 大纲数据访问对象
 */
@Dao
interface OutlineDao {
    
    // ========== 查询操作 ==========
    
    /**
     * 获取作品的所有大纲节点
     */
    @Query("SELECT * FROM outline_nodes WHERE workId = :workId ORDER BY `order` ASC")
    fun getOutlineNodesByWork(workId: Long): Flow<List<OutlineNodeEntity>>
    
    /**
     * 获取根节点（顶层节点）
     */
    @Query("SELECT * FROM outline_nodes WHERE workId = :workId AND parentId IS NULL ORDER BY `order` ASC")
    fun getRootNodes(workId: Long): Flow<List<OutlineNodeEntity>>
    
    /**
     * 获取子节点
     */
    @Query("SELECT * FROM outline_nodes WHERE parentId = :parentId ORDER BY `order` ASC")
    fun getChildNodes(parentId: Long): Flow<List<OutlineNodeEntity>>
    
    /**
     * 获取节点详情
     */
    @Query("SELECT * FROM outline_nodes WHERE id = :id")
    suspend fun getNodeById(id: Long): OutlineNodeEntity?
    
    /**
     * 获取节点详情（Flow）
     */
    @Query("SELECT * FROM outline_nodes WHERE id = :id")
    fun getNodeByIdFlow(id: Long): Flow<OutlineNodeEntity?>
    
    /**
     * 获取关联章节的大纲节点
     */
    @Query("SELECT * FROM outline_nodes WHERE chapterId = :chapterId")
    suspend fun getNodeByChapter(chapterId: Long): OutlineNodeEntity?
    
    /**
     * 获取指定状态的大纲节点
     */
    @Query("SELECT * FROM outline_nodes WHERE workId = :workId AND status = :status")
    fun getNodesByStatus(workId: Long, status: OutlineStatus): Flow<List<OutlineNodeEntity>>
    
    // ========== 插入操作 ==========
    
    /**
     * 插入大纲节点
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNode(node: OutlineNodeEntity): Long
    
    /**
     * 批量插入大纲节点
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNodes(nodes: List<OutlineNodeEntity>)
    
    // ========== 更新操作 ==========
    
    /**
     * 更新大纲节点
     */
    @Update
    suspend fun updateNode(node: OutlineNodeEntity)
    
    /**
     * 更新节点内容
     */
    @Query("UPDATE outline_nodes SET title = :title, content = :content, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateNodeContent(id: Long, title: String, content: String, updatedAt: LocalDateTime)
    
    /**
     * 更新节点状态
     */
    @Query("UPDATE outline_nodes SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateNodeStatus(id: Long, status: OutlineStatus, updatedAt: LocalDateTime)
    
    /**
     * 更新节点排序
     */
    @Query("UPDATE outline_nodes SET `order` = :order WHERE id = :id")
    suspend fun updateNodeOrder(id: Long, order: Int)
    
    /**
     * 更新父节点
     */
    @Query("UPDATE outline_nodes SET parentId = :parentId, level = :level WHERE id = :id")
    suspend fun updateParent(id: Long, parentId: Long?, level: Int)
    
    /**
     * 关联章节
     */
    @Query("UPDATE outline_nodes SET chapterId = :chapterId WHERE id = :id")
    suspend fun linkToChapter(id: Long, chapterId: Long?)
    
    // ========== 删除操作 ==========
    
    /**
     * 删除大纲节点
     */
    @Delete
    suspend fun deleteNode(node: OutlineNodeEntity)
    
    /**
     * 根据ID删除大纲节点
     */
    @Query("DELETE FROM outline_nodes WHERE id = :id")
    suspend fun deleteNodeById(id: Long)
    
    /**
     * 删除节点及其所有子节点
     */
    @Transaction
    suspend fun deleteNodeRecursive(id: Long) {
        // 递归获取所有子节点
        val childIds = getAllChildIds(id)
        // 删除所有子节点
        childIds.forEach { childId ->
            deleteNodeById(childId)
        }
        // 删除当前节点
        deleteNodeById(id)
    }
    
    /**
     * 递归获取所有子节点ID
     */
    @Query("WITH RECURSIVE children AS (SELECT id FROM outline_nodes WHERE parentId = :parentId UNION ALL SELECT o.id FROM outline_nodes o INNER JOIN children c ON o.parentId = c.id) SELECT id FROM children")
    suspend fun getAllChildIds(parentId: Long): List<Long>
    
    /**
     * 删除作品的所有大纲节点
     */
    @Query("DELETE FROM outline_nodes WHERE workId = :workId")
    suspend fun deleteNodesByWork(workId: Long)
}
