package com.zhibi.writer.ui.screens.publish

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhibi.writer.data.model.PlatformType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 平台管理ViewModel
 */
@HiltViewModel
class PlatformManageViewModel @Inject constructor(
    // TODO: 注入平台仓库
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PlatformManageUiState())
    val uiState = _uiState.asStateFlow()
    
    fun authorizePlatform(platformType: PlatformType) {
        viewModelScope.launch {
            // TODO: 实现平台授权
            // 1. 打开平台登录页面
            // 2. 获取授权token
            // 3. 保存账号信息
            
            _uiState.update { 
                it.copy(
                    authorizedPlatforms = it.authorizedPlatforms + platformType
                )
            }
        }
    }
    
    fun unbindPlatform(platformType: PlatformType) {
        viewModelScope.launch {
            // TODO: 实现平台解绑
            
            _uiState.update { 
                it.copy(
                    authorizedPlatforms = it.authorizedPlatforms - platformType
                )
            }
        }
    }
    
    fun syncData(platformType: PlatformType) {
        viewModelScope.launch {
            // TODO: 同步平台数据
        }
    }
}

/**
 * 平台管理UI状态
 */
data class PlatformManageUiState(
    val platforms: List<PlatformType> = PlatformType.entries.toList(),
    val authorizedPlatforms: Set<PlatformType> = emptySet(),
    val isLoading: Boolean = false
)
