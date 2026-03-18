package com.zhibi.writer.ui.screens.tools

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.ViewModel
import com.zhibi.writer.util.FormatOptions
import com.zhibi.writer.util.TextFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * 文本排版ViewModel
 */
@HiltViewModel
class TextFormatViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TextFormatUiState())
    val uiState = _uiState.asStateFlow()
    
    fun updateInputText(text: String) {
        _uiState.update { 
            it.copy(
                inputText = text,
                wordCount = TextFormatter.countWords(text),
                paragraphCount = TextFormatter.countParagraphs(text),
                charCount = text.length
            )
        }
    }
    
    fun updateOption(options: FormatOptions) {
        _uiState.update { it.copy(options = options) }
    }
    
    fun format(platform: String) {
        val text = _uiState.value.inputText
        val options = _uiState.value.options
        val formatted = TextFormatter.format(text, platform, options)
        
        _uiState.update { 
            it.copy(
                outputText = formatted,
                wordCount = TextFormatter.countWords(formatted),
                paragraphCount = TextFormatter.countParagraphs(formatted),
                charCount = formatted.length
            )
        }
    }
    
    fun copyOutput() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("formatted_text", _uiState.value.outputText)
        clipboard.setPrimaryClip(clip)
    }
}

/**
 * 文本排版UI状态
 */
data class TextFormatUiState(
    val inputText: String = "",
    val outputText: String = "",
    val options: FormatOptions = FormatOptions(),
    val wordCount: Int = 0,
    val paragraphCount: Int = 0,
    val charCount: Int = 0
)
