package com.zhibi.writer.data.local.repository

import com.zhibi.writer.data.local.dao.MaterialDao
import com.zhibi.writer.data.local.entity.MaterialCategory
import com.zhibi.writer.data.local.entity.MaterialEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 素材仓库
 */
@Singleton
class MaterialRepository @Inject constructor(
    private val materialDao: MaterialDao
) {
    fun getMaterialsByWork(workId: Long): Flow<List<MaterialEntity>> = 
        materialDao.getMaterialsByWork(workId)
    
    fun getMaterialsByCategory(workId: Long, category: MaterialCategory): Flow<List<MaterialEntity>> = 
        materialDao.getMaterialsByCategory(workId, category)
    
    fun getGlobalMaterials(): Flow<List<MaterialEntity>> = 
        materialDao.getGlobalMaterials()
    
    suspend fun getMaterialById(id: Long): MaterialEntity? = 
        materialDao.getMaterialById(id)
    
    fun getMaterialByIdFlow(id: Long): Flow<MaterialEntity?> = 
        materialDao.getMaterialByIdFlow(id)
    
    fun searchMaterials(workId: Long, query: String): Flow<List<MaterialEntity>> = 
        materialDao.searchMaterials(workId, query)
    
    fun getMaterialsByTag(workId: Long, tag: String): Flow<List<MaterialEntity>> = 
        materialDao.getMaterialsByTag(workId, tag)
    
    suspend fun insertMaterial(material: MaterialEntity): Long = 
        materialDao.insertMaterial(material)
    
    suspend fun insertMaterials(materials: List<MaterialEntity>) = 
        materialDao.insertMaterials(materials)
    
    suspend fun updateMaterial(material: MaterialEntity) = 
        materialDao.updateMaterial(material)
    
    suspend fun updateContent(id: Long, content: String, updatedAt: LocalDateTime) =
        materialDao.updateContent(id, content, updatedAt)
    
    suspend fun deleteMaterial(material: MaterialEntity) = 
        materialDao.deleteMaterial(material)
    
    suspend fun deleteMaterialById(id: Long) = 
        materialDao.deleteMaterialById(id)
    
    suspend fun deleteMaterialsByWork(workId: Long) = 
        materialDao.deleteMaterialsByWork(workId)
}
