package com.zhibi.writer.util

/**
 * 敏感词检测工具
 */
object SensitiveWordDetector {
    
    // 内置敏感词库
    private val defaultSensitiveWords = setOf(
        // 政治敏感词（示例）
        "政治敏感词1", "政治敏感词2",
        
        // 色情相关（示例）
        "色情词1", "色情词2",
        
        // 暴力相关（示例）
        "暴力词1", "暴力词2",
        
        // 其他违规词
        "违禁词1", "违禁词2"
    )
    
    // 用户自定义词库
    private val customSensitiveWords = mutableSetOf<String>()
    
    // 白名单
    private val whitelist = mutableSetOf<String>()
    
    /**
     * 添加自定义敏感词
     */
    fun addCustomWords(words: List<String>) {
        customSensitiveWords.addAll(words)
    }
    
    /**
     * 添加白名单词汇
     */
    fun addToWhitelist(words: List<String>) {
        whitelist.addAll(words)
    }
    
    /**
     * 清除自定义词库
     */
    fun clearCustomWords() {
        customSensitiveWords.clear()
    }
    
    /**
     * 获取所有敏感词（合并默认和自定义）
     */
    private fun getAllSensitiveWords(): Set<String> {
        return defaultSensitiveWords + customSensitiveWords - whitelist
    }
    
    /**
     * 检测文本中的敏感词
     * @return 检测结果列表，包含敏感词、位置、建议替换
     */
    fun detect(text: String): List<SensitiveWordResult> {
        val results = mutableListOf<SensitiveWordResult>()
        val allWords = getAllSensitiveWords()
        
        allWords.forEach { word ->
            var startIndex = 0
            while (true) {
                val index = text.indexOf(word, startIndex, ignoreCase = true)
                if (index == -1) break
                
                results.add(
                    SensitiveWordResult(
                        word = word,
                        startIndex = index,
                        endIndex = index + word.length,
                        type = getWordType(word),
                        suggestion = getSuggestion(word)
                    )
                )
                startIndex = index + 1
            }
        }
        
        return results.sortedBy { it.startIndex }
    }
    
    /**
     * 获取敏感词类型
     */
    private fun getWordType(word: String): SensitiveWordType {
        return when {
            defaultSensitiveWords.contains(word) -> SensitiveWordType.DEFAULT
            customSensitiveWords.contains(word) -> SensitiveWordType.CUSTOM
            else -> SensitiveWordType.UNKNOWN
        }
    }
    
    /**
     * 获取替换建议
     */
    private fun getSuggestion(word: String): String {
        // 简单的替换建议：用*替代
        return "*".repeat(word.length)
    }
    
    /**
     * 替换文本中的敏感词
     */
    fun replace(text: String, replacement: String = "*"): String {
        var result = text
        getAllSensitiveWords().forEach { word ->
            result = result.replace(word, replacement.repeat(word.length), ignoreCase = true)
        }
        return result
    }
    
    /**
     * 检查文本是否包含敏感词
     */
    fun containsSensitiveWord(text: String): Boolean {
        val allWords = getAllSensitiveWords()
        return allWords.any { text.contains(it, ignoreCase = true) }
    }
    
    /**
     * 统计敏感词数量
     */
    fun countSensitiveWords(text: String): Int {
        return detect(text).size
    }
}

/**
 * 敏感词检测结果
 */
data class SensitiveWordResult(
    val word: String,           // 敏感词
    val startIndex: Int,        // 起始位置
    val endIndex: Int,          // 结束位置
    val type: SensitiveWordType,// 类型
    val suggestion: String      // 替换建议
)

/**
 * 敏感词类型
 */
enum class SensitiveWordType {
    DEFAULT,    // 默认词库
    CUSTOM,     // 自定义词库
    UNKNOWN     // 未知
}

/**
 * 敏感词等级
 */
enum class SensitiveLevel {
    HIGH,       // 高危（必须修改）
    MEDIUM,     // 中危（建议修改）
    LOW         // 低危（可选修改）
}
