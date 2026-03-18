package com.zhibi.writer.ui.screens.tools

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.ViewModel
import com.zhibi.writer.util.NameGenerator
import com.zhibi.writer.util.NameType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * 随机取名ViewModel
 */
@HiltViewModel
class NameGeneratorViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(NameGeneratorUiState())
    val uiState = _uiState.asStateFlow()
    
    private val favoriteNames = mutableSetOf<String>()
    
    fun generate(type: NameType, count: Int) {
        val names = NameGenerator.generateByType(type, count)
        _uiState.update { it.copy(names = names) }
    }
    
    fun copyName(name: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("name", name)
        clipboard.setPrimaryClip(clip)
    }
    
    fun favoriteName(name: String) {
        if (favoriteNames.contains(name)) {
            favoriteNames.remove(name)
        } else {
            favoriteNames.add(name)
        }
    }
    
    fun isFavorite(name: String): Boolean {
        return favoriteNames.contains(name)
    }
}

/**
 * 随机取名UI状态
 */
data class NameGeneratorUiState(
    val names: List<String> = emptyList()
)
