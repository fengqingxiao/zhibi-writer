package com.zhibi.writer.ui.screens.tools

import androidx.lifecycle.ViewModel
import com.zhibi.writer.util.SensitiveWordDetector
import com.zhibi.writer.util.SensitiveWordResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * 敏感词检测ViewModel
 */
@HiltViewModel
class SensitiveWordViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(SensitiveWordUiState())
    val uiState = _uiState.asStateFlow()
    
    fun updateInputText(text: String) {
        _uiState.update { 
            it.copy(
                inputText = text,
                hasChecked = false
            )
        }
    }
    
    fun detect() {
        val text = _uiState.value.inputText
        val results = SensitiveWordDetector.detect(text)
        
        _uiState.update {
            it.copy(
                sensitiveWords = results,
                sensitiveWordCount = results.size,
                hasSensitiveWords = results.isNotEmpty(),
                hasChecked = true,
                outputText = ""
            )
        }
    }
    
    fun replace() {
        val text = _uiState.value.inputText
        val replaced = SensitiveWordDetector.replace(text)
        
        _uiState.update {
            it.copy(
                outputText = replaced,
                sensitiveWords = emptyList(),
                hasSensitiveWords = false
            )
        }
    }
    
    fun replaceWord(startIndex: Int, endIndex: Int) {
        val text = _uiState.value.inputText
        val word = text.substring(startIndex, endIndex)
        val replaced = text.replaceRange(startIndex, endIndex, "*".repeat(word.length))
        
        _uiState.update {
            it.copy(
                inputText = replaced,
                sensitiveWords = SensitiveWordDetector.detect(replaced),
                sensitiveWordCount = SensitiveWordDetector.countSensitiveWords(replaced),
                hasSensitiveWords = SensitiveWordDetector.containsSensitiveWord(replaced)
            )
        }
    }
    
    fun clearAll() {
        _uiState.value = SensitiveWordUiState()
    }
}

/**
 * 敏感词检测UI状态
 */
data class SensitiveWordUiState(
    val inputText: String = "",
    val outputText: String = "",
    val sensitiveWords: List<SensitiveWordResult> = emptyList(),
    val sensitiveWordCount: Int = 0,
    val hasSensitiveWords: Boolean = false,
    val hasChecked: Boolean = false
)
