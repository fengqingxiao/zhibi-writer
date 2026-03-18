package com.zhibi.writer.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

/**
 * 章节历史版本实体
 */
@Entity(
    tableName = "chapter_history",
    foreignKeys = [
        ForeignKey(
            entity = ChapterEntity::class,
            parentColumns = ["id"],
            childColumns = ["chapterId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("chapterId")]
)
data class ChapterHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val chapterId: Long,
    val content: String,                  // 历史版本内容
    val wordCount: Int,                   // 字数
    val createdAt: LocalDateTime,         // 创建时间
    val note: String? = null,             // 版本备注
)

/**
 * 写作记录实体（用于统计）
 */
@Entity(tableName = "writing_records")
data class WritingRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val workId: Long,
    val chapterId: Long?,
    val date: LocalDateTime,              // 记录日期
    
    // 写作统计
    val wordsWritten: Int = 0,            // 写入字数
    val wordsDeleted: Int = 0,            // 删除字数
    val netWords: Int = 0,                // 净增字数
    val writingTime: Long = 0,            // 写作时长（毫秒）
    val peakSpeed: Int = 0,               // 峰值速度（字/分钟）
    val averageSpeed: Int = 0,            // 平均速度
)

/**
 * 已删除章节回收站
 */
@Entity(
    tableName = "deleted_chapters",
    foreignKeys = [
        ForeignKey(
            entity = WorkEntity::class,
            parentColumns = ["id"],
            childColumns = ["workId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workId")]
)
data class DeletedChapterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val workId: Long,
    val chapterData: String,              // 章节数据（JSON序列化）
    val deletedAt: LocalDateTime,         // 删除时间
    val expiresAt: LocalDateTime,         // 过期时间（30天后自动删除）
)

/**
 * 自动保存缓存（实时保存）
 */
@Entity(tableName = "auto_save_cache")
data class AutoSaveCacheEntity(
    @PrimaryKey
    val chapterId: Long,
    val content: String,
    val cursorPosition: Int = 0,
    val savedAt: LocalDateTime,
)
