package com.zhibi.writer.data.local.dao

import androidx.room.*
import com.zhibi.writer.data.local.entity.MaterialEntity
import com.zhibi.writer.data.local.entity.MaterialCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

/**
 * 素材数据访问对象
 */
@Dao
interface MaterialDao {
    
    // ========== 查询操作 ==========
    
    /**
     * 获取作品的所有素材
     */
    @Query("SELECT * FROM materials WHERE workId = :workId ORDER BY category, createdAt DESC")
    fun getMaterialsByWork(workId: Long): Flow<List<MaterialEntity>>
    
    /**
     * 获取作品指定分类的素材
     */
    @Query("SELECT * FROM materials WHERE workId = :workId AND category = :category ORDER BY createdAt DESC")
    fun getMaterialsByCategory(workId: Long, category: MaterialCategory): Flow<List<MaterialEntity>>
    
    /**
     * 获取全局素材
     */
    @Query("SELECT * FROM materials WHERE workId IS NULL ORDER BY category, createdAt DESC")
    fun getGlobalMaterials(): Flow<List<MaterialEntity>>
    
    /**
     * 获取素材详情
     */
    @Query("SELECT * FROM materials WHERE id = :id")
    suspend fun getMaterialById(id: Long): MaterialEntity?
    
    /**
     * 获取素材详情（Flow）
     */
    @Query("SELECT * FROM materials WHERE id = :id")
    fun getMaterialByIdFlow(id: Long): Flow<MaterialEntity?>
    
    /**
     * 搜索素材
     */
    @Query("SELECT * FROM materials WHERE (workId = :workId OR workId IS NULL) AND (title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%')")
    fun searchMaterials(workId: Long, query: String): Flow<List<MaterialEntity>>
    
    /**
     * 根据标签搜索素材
     */
    @Query("SELECT * FROM materials WHERE workId = :workId AND :tag IN (tags)")
    fun getMaterialsByTag(workId: Long, tag: String): Flow<List<MaterialEntity>>
    
    // ========== 插入操作 ==========
    
    /**
     * 插入素材
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: MaterialEntity): Long
    
    /**
     * 批量插入素材
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterials(materials: List<MaterialEntity>)
    
    // ========== 更新操作 ==========
    
    /**
     * 更新素材
     */
    @Update
    suspend fun updateMaterial(material: MaterialEntity)
    
    /**
     * 更新素材内容
     */
    @Query("UPDATE materials SET content = :content, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateContent(id: Long, content: String, updatedAt: LocalDateTime)
    
    // ========== 删除操作 ==========
    
    /**
     * 删除素材
     */
    @Delete
    suspend fun deleteMaterial(material: MaterialEntity)
    
    /**
     * 根据ID删除素材
     */
    @Query("DELETE FROM materials WHERE id = :id")
    suspend fun deleteMaterialById(id: Long)
    
    /**
     * 删除作品的所有素材
     */
    @Query("DELETE FROM materials WHERE workId = :workId")
    suspend fun deleteMaterialsByWork(workId: Long)
}
