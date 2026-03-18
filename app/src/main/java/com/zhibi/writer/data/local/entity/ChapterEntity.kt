package com.zhibi.writer.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

/**
 * 章节实体
 */
@Entity(
    tableName = "chapters",
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
data class ChapterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // 关联作品
    val workId: Long,
    
    // 所属卷ID（0表示不属于任何卷）
    val volumeId: Long = 0,
    
    // 基本信息
    val title: String,                    // 章节名
    val content: String = "",             // 正文内容
    
    // 排序和状态
    val order: Int = 0,                   // 排序序号
    val status: ChapterStatus = ChapterStatus.DRAFT,
    val isLocked: Boolean = false,        // 是否锁定（防止误删）
    val isHidden: Boolean = false,        // 是否隐藏
    
    // 统计数据
    val wordCount: Int = 0,               // 字数
    
    // 时间戳
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val publishedAt: LocalDateTime? = null, // 发布时间（定时发布）
    
    // 大纲关联
    val outlineNodeId: Long? = null,      // 关联的大纲节点
    
    // 同步状态
    val syncStatus: SyncStatus = SyncStatus.LOCAL_ONLY,
    val remoteId: String? = null,
    
    // 历史版本
    val hasHistory: Boolean = false,      // 是否有历史版本
)

/**
 * 章节状态
 */
enum class ChapterStatus {
    DRAFT,        // 草稿
    WRITING,      // 写作中
    COMPLETED,    // 已完成
    PUBLISHED,    // 已发布
    SCHEDULED,    // 定时发布
}

/**
 * 卷实体
 */
@Entity(
    tableName = "volumes",
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
data class VolumeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val workId: Long,
    val title: String,                    // 卷名
    val summary: String = "",             // 卷简介
    val order: Int = 0,                   // 排序序号
    val createdAt: LocalDateTime,
)
