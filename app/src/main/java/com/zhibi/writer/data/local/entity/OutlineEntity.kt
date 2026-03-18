package com.zhibi.writer.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

/**
 * 大纲节点实体
 */
@Entity(
    tableName = "outline_nodes",
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
data class OutlineNodeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // 关联作品
    val workId: Long,
    
    // 层级结构
    val parentId: Long? = null,           // 父节点ID（null表示根节点）
    val level: Int = 0,                   // 层级：0=卷，1=章节，2=情节节点
    val order: Int = 0,                   // 同级排序
    
    // 基本信息
    val title: String,                    // 节点标题
    val content: String = "",             // 节点内容
    
    // 类型标签
    val nodeType: OutlineNodeType = OutlineNodeType.PLOT,
    val tags: List<String> = emptyList(), // 标签（核心剧情、伏笔、人物出场等）
    val color: String? = null,            // 颜色标记
    
    // 章节关联
    val chapterId: Long? = null,          // 关联的章节ID
    
    // 状态
    val status: OutlineStatus = OutlineStatus.PLANNED,
    val estimatedWords: Int = 0,          // 预估字数
    
    // 时间戳
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

/**
 * 大纲节点类型
 */
enum class OutlineNodeType {
    VOLUME,       // 卷
    CHAPTER,      // 章节
    PLOT,         // 情节
    FUTURE,       // 伏笔
    CHARACTER,    // 人物出场
}

/**
 * 大纲节点状态
 */
enum class OutlineStatus {
    PLANNED,      // 计划中
    WRITING,      // 写作中
    COMPLETED,    // 已完成
}
