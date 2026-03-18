package com.zhibi.writer.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

/**
 * 素材实体
 */
@Entity(
    tableName = "materials",
    foreignKeys = [
        ForeignKey(
            entity = WorkEntity::class,
            parentColumns = ["id"],
            childColumns = ["workId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workId"), Index("category")]
)
data class MaterialEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // 关联作品（null表示全局素材）
    val workId: Long? = null,
    
    // 分类
    val category: MaterialCategory,
    val tags: List<String> = emptyList(),
    
    // 基本信息
    val title: String,                    // 素材名称
    val content: String = "",             // 素材内容（JSON格式存储详细信息）
    
    // 关联
    val relatedMaterialIds: List<Long> = emptyList(), // 关联的其他素材
    
    // 时间戳
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

/**
 * 素材分类
 */
enum class MaterialCategory {
    CHARACTER,    // 人物设定
    WORLD,        // 世界观
    FACTION,      // 势力/组织
    LOCATION,     // 地点/场景
    ITEM,         // 道具/功法
    FORESHADOW,   // 伏笔线索
    INSPIRATION,  // 灵感碎片
    REFERENCE,    // 参考资料
}

/**
 * 人物设定数据类
 */
data class CharacterData(
    val name: String,
    val alias: String? = null,            // 别名/昵称
    val gender: String? = null,
    val age: String? = null,
    val appearance: String? = null,       // 外貌描述
    val personality: String? = null,      // 性格特点
    val background: String? = null,       // 背景故事
    val abilities: List<String> = emptyList(), // 能力/技能
    val relationships: List<CharacterRelation> = emptyList(), // 人物关系
    val growthArc: String? = null,        // 成长线
    val quotes: List<String> = emptyList(), // 经典台词
    val notes: String? = null,            // 备注
)

/**
 * 人物关系
 */
data class CharacterRelation(
    val targetCharacterId: Long,
    val targetName: String,
    val relation: String,                 // 关系描述
)

/**
 * 伏笔数据类
 */
data class ForeshadowData(
    val description: String,              // 伏笔内容
    val plantedChapterId: Long?,          // 埋设章节
    val plantedChapterTitle: String?,     // 埋设章节名
    val plannedChapterId: Long?,          // 计划回收章节
    val plannedChapterTitle: String?,     // 计划回收章节名
    val status: ForeshadowStatus,         // 回收状态
    val notes: String? = null,            // 备注
)

/**
 * 伏笔状态
 */
enum class ForeshadowStatus {
    PLANTED,      // 已埋设
    REVEALED,     // 已揭示
    ABANDONED,    // 已废弃
}
