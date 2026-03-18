package com.zhibi.writer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

/**
 * 作品实体
 */
@Entity(tableName = "works")
data class WorkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // 基本信息
    val title: String,                    // 作品名
    val coverPath: String? = null,        // 封面路径
    val summary: String = "",             // 简介
    
    // 分类和状态
    val category: String = "",            // 分类（玄幻/都市/言情等）
    val status: WorkStatus = WorkStatus.DRAFT,
    val isTopped: Boolean = false,        // 是否置顶
    
    // 统计数据
    val totalWordCount: Long = 0,         // 总字数
    val chapterCount: Int = 0,            // 章节数
    val latestChapterTitle: String? = null, // 最新章节名
    
    // 时间戳
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val lastEditedAt: LocalDateTime,      // 最后编辑时间
    
    // 同步状态
    val syncStatus: SyncStatus = SyncStatus.LOCAL_ONLY,
    val remoteId: String? = null,         // 云端ID
)

/**
 * 作品状态
 */
enum class WorkStatus {
    DRAFT,        // 草稿
    SERIAL,       // 连载中
    COMPLETED,    // 已完结
    PAUSED,       // 暂停更新
}

/**
 * 同步状态
 */
enum class SyncStatus {
    LOCAL_ONLY,   // 仅本地
    SYNCED,       // 已同步
    PENDING,      // 待同步
    CONFLICT,     // 冲突
}
