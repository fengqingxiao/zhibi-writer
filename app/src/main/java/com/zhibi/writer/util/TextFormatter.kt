package com.zhibi.writer.util

/**
 * 文本排版工具
 */
object TextFormatter {
    
    /**
     * 平台格式配置
     */
    data class PlatformFormat(
        val name: String,
        val indent: String,         // 首行缩进
        val paragraphGap: String,   // 段落间距
        val lineBreak: String       // 换行符
    )
    
    // 各平台格式配置
    val PLATFORM_FORMATS = mapOf(
        "default" to PlatformFormat(
            name = "默认",
            indent = "　　",  // 两个全角空格
            paragraphGap = "\n",
            lineBreak = "\n"
        ),
        "yuewen" to PlatformFormat(
            name = "阅文集团",
            indent = "　　",
            paragraphGap = "\n",
            lineBreak = "\n"
        ),
        "fanqie" to PlatformFormat(
            name = "番茄小说",
            indent = "　　",
            paragraphGap = "\n",
            lineBreak = "\n"
        ),
        "qimao" to PlatformFormat(
            name = "七猫小说",
            indent = "　　",
            paragraphGap = "\n",
            lineBreak = "\n"
        ),
        "jinjiang" to PlatformFormat(
            name = "晋江文学城",
            indent = "　　",
            paragraphGap = "\n\n",
            lineBreak = "\n"
        )
    )
    
    /**
     * 格式化文本
     * @param text 原始文本
     * @param platform 目标平台
     * @param options 格式化选项
     */
    fun format(
        text: String,
        platform: String = "default",
        options: FormatOptions = FormatOptions()
    ): String {
        val format = PLATFORM_FORMATS[platform] ?: PLATFORM_FORMATS["default"]!!
        
        var result = text
        
        // 1. 统一换行符
        result = normalizeLineBreaks(result)
        
        // 2. 去除多余空格
        if (options.trimSpaces) {
            result = result.lines()
                .joinToString("\n") { it.trimEnd() }
        }
        
        // 3. 去除多余空行
        if (options.removeEmptyLines) {
            result = result.replace(Regex("\n{3,}"), "\n\n")
        }
        
        // 4. 添加首行缩进
        if (options.addIndent) {
            result = addIndent(result, format.indent)
        }
        
        // 5. 标准化引号
        if (options.normalizeQuotes) {
            result = normalizeQuotes(result)
        }
        
        // 6. 标准化标点
        if (options.normalizePunctuation) {
            result = normalizePunctuation(result)
        }
        
        // 7. 添加段落间距
        if (options.addParagraphGap) {
            result = result.replace("\n", format.paragraphGap)
        }
        
        return result
    }
    
    /**
     * 统一换行符
     */
    private fun normalizeLineBreaks(text: String): String {
        return text.replace("\r\n", "\n").replace("\r", "\n")
    }
    
    /**
     * 添加首行缩进
     */
    private fun addIndent(text: String, indent: String): String {
        return text.lines()
            .joinToString("\n") { line ->
                val trimmed = line.trim()
                if (trimmed.isEmpty()) {
                    ""
                } else if (trimmed.startsWith("　") || trimmed.startsWith("  ")) {
                    trimmed
                } else {
                    indent + trimmed
                }
            }
    }
    
    /**
     * 标准化引号
     */
    private fun normalizeQuotes(text: String): String {
        return text
            // 中文引号
            .replace("\"", "「")
            .replace("\"", "」")
            .replace("'", "『")
            .replace("'", "』")
            // 英文引号转中文
            .replace(Regex("\"([^\"]+)\""), "「$1」")
            .replace(Regex("'([^']+)'"), "『$1』")
    }
    
    /**
     * 标准化标点符号
     */
    private fun normalizePunctuation(text: String): String {
        return text
            // 英文标点转中文
            .replace(",", "，")
            .replace(".", "。")
            .replace("?", "？")
            .replace("!", "！")
            .replace(":", "：")
            .replace(";", "；")
            .replace("(", "（")
            .replace(")", "）")
            // 修正连续标点
            .replace(Regex("，+"), "，")
            .replace(Regex("。+"), "。。。")
            .replace(Regex("！+"), "！！")
            .replace(Regex("？+"), "？？")
    }
    
    /**
     * 清除所有格式
     */
    fun clearFormat(text: String): String {
        return text
            .replace(Regex("[　 \\t]+"), " ")
            .replace(Regex("\n+"), "\n")
            .trim()
    }
    
    /**
     * 统计字数（不含空格和标点）
     */
    fun countWords(text: String): Int {
        return text
            .replace(Regex("[\\s\\p{Punct}]"), "")
            .length
    }
    
    /**
     * 统计段落
     */
    fun countParagraphs(text: String): Int {
        return text.lines().count { it.trim().isNotEmpty() }
    }
    
    /**
     * 分割章节（自动检测）
     */
    fun splitChapters(text: String): List<Pair<String, String>> {
        val chapterPattern = Regex(
            "^(第[零一二三四五六七八九十百千万\\d]+[章节回])[\\s　]*(.*)$",
            RegexOption.MULTILINE
        )
        
        val chapters = mutableListOf<Pair<String, String>>()
        val matches = chapterPattern.findAll(text).toList()
        
        if (matches.isEmpty()) {
            // 没有检测到章节标题，整体作为一个章节
            chapters.add(Pair("第一章", text.trim()))
        } else {
            matches.forEachIndexed { index, match ->
                val title = match.groupValues[1] + if (match.groupValues[2].isNotEmpty()) " " + match.groupValues[2] else ""
                val startIndex = match.range.first
                val endIndex = matches.getOrNull(index + 1)?.range?.first ?: text.length
                val content = text.substring(startIndex, endIndex).trim()
                chapters.add(Pair(title, content))
            }
        }
        
        return chapters
    }
    
    /**
     * 合并章节
     */
    fun mergeChapters(chapters: List<Pair<String, String>>): String {
        return chapters.joinToString("\n\n") { (title, content) ->
            "$title\n\n$content"
        }
    }
}

/**
 * 格式化选项
 */
data class FormatOptions(
    val addIndent: Boolean = true,              // 添加首行缩进
    val trimSpaces: Boolean = true,             // 去除多余空格
    val removeEmptyLines: Boolean = true,       // 去除多余空行
    val normalizeQuotes: Boolean = true,        // 标准化引号
    val normalizePunctuation: Boolean = true,   // 标准化标点
    val addParagraphGap: Boolean = false        // 添加段落间距
)

/**
 * 格式化预设
 */
object FormatPresets {
    val DEFAULT = FormatOptions()
    
    val NOVEL = FormatOptions(
        addIndent = true,
        trimSpaces = true,
        removeEmptyLines = true,
        normalizeQuotes = true,
        normalizePunctuation = true,
        addParagraphGap = false
    )
    
    val CLEAN = FormatOptions(
        addIndent = false,
        trimSpaces = true,
        removeEmptyLines = true,
        normalizeQuotes = false,
        normalizePunctuation = false,
        addParagraphGap = false
    )
}
