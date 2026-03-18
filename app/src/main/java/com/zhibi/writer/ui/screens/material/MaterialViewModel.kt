package com.zhibi.writer.ui.screens.material

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhibi.writer.data.local.entity.*
import com.zhibi.writer.data.local.repository.MaterialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * 素材ViewModel
 */
@HiltViewModel
class MaterialViewModel @Inject constructor(
    private val materialRepository: MaterialRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MaterialUiState())
    val uiState = _uiState.asStateFlow()
    
    private var currentMaterial: MaterialEntity? = null
    
    fun loadMaterials(workId: Long, category: MaterialCategory?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val flow = when {
                workId == 0L && category == null -> materialRepository.getGlobalMaterials()
                workId == 0L -> materialRepository.getMaterialsByCategory(0, category!!)
                category == null -> materialRepository.getMaterialsByWork(workId)
                else -> materialRepository.getMaterialsByCategory(workId, category)
            }
            
            flow
                .catch { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
                .collect { materials ->
                    _uiState.update { 
                        it.copy(materials = materials, isLoading = false)
                    }
                }
        }
    }
    
    fun loadMaterial(materialId: Long) {
        viewModelScope.launch {
            val material = materialRepository.getMaterialById(materialId)
            currentMaterial = material
            _uiState.update { it.copy(material = material) }
        }
    }
    
    fun createMaterial(workId: Long?, category: MaterialCategory, title: String) {
        viewModelScope.launch {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            
            val material = MaterialEntity(
                workId = workId?.takeIf { it > 0 },
                category = category,
                title = title,
                content = "",
                createdAt = now,
                updatedAt = now
            )
            
            materialRepository.insertMaterial(material)
        }
    }
    
    fun saveCharacter(name: String, data: CharacterData) {
        viewModelScope.launch {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val content = Json.encodeToString(data)
            
            val material = currentMaterial?.copy(
                title = name,
                content = content,
                updatedAt = now
            ) ?: MaterialEntity(
                category = MaterialCategory.CHARACTER,
                title = name,
                content = content,
                createdAt = now,
                updatedAt = now
            )
            
            materialRepository.insertMaterial(material)
        }
    }
    
    fun saveForeshadow(title: String, data: ForeshadowData) {
        viewModelScope.launch {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val content = Json.encodeToString(data)
            
            val material = currentMaterial?.copy(
                title = title,
                content = content,
                updatedAt = now
            ) ?: MaterialEntity(
                category = MaterialCategory.FORESHADOW,
                title = title,
                content = content,
                createdAt = now,
                updatedAt = now
            )
            
            materialRepository.insertMaterial(material)
        }
    }
    
    fun deleteMaterial(materialId: Long) {
        viewModelScope.launch {
            materialRepository.deleteMaterialById(materialId)
        }
    }
}

/**
 * 素材UI状态
 */
data class MaterialUiState(
    val isLoading: Boolean = true,
    val materials: List<MaterialEntity> = emptyList(),
    val material: MaterialEntity? = null,
    val error: String? = null
)
