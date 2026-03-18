package com.zhibi.writer.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

/**
 * 发布记录实体
 */
@Entity(
    tableName = "publish_records",
    foreignKeys = [
        ForeignKey(
            entity = WorkEntity::class,
            parentColumns = ["id"],
            childColumns = ["workId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workId"), Index("chapterId"), Index("platformType")]
)
data class PublishRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val workId: Long,
    val chapterId: Long,
    val platformType: String,  // PlatformType.name
    val platformChapterId: String? = null,
    val status: String,        // PublishStatus.name
    val publishedAt: LocalDateTime? = null,
    val scheduledAt: LocalDateTime? = null,
    val errorMessage: String? = null,
    val readCount: Long = 0,
    val commentCount: Long = 0,
    val likeCount: Long = 0,
    val createdAt: LocalDateTime
)

/**
 * 平台绑定实体
 */
@Entity(
    tableName = "platform_bindings",
    foreignKeys = [
        ForeignKey(
            entity = WorkEntity::class,
            parentColumns = ["id"],
            childColumns = ["workId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workId"), Index("platformType")]
)
data class PlatformBindingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val workId: Long,
    val platformType: String,
    val platformWorkId: String,
    val platformWorkTitle: String,
    val lastSyncedChapter: Int = 0,
    val createdAt: LocalDateTime
)

/**
 * 平台统计实体
 */
@Entity(
    tableName = "platform_stats",
    foreignKeys = [
        ForeignKey(
            entity = WorkEntity::class,
            parentColumns = ["id"],
            childColumns = ["workId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workId"), Index("platformType"), Index("date")]
)
data class PlatformStatsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val workId: Long,
    val platformType: String,
    val date: String,  // YYYY-MM-DD
    val readCount: Long = 0,
    val readerCount: Long = 0,
    val collectCount: Long = 0,
    val recommendCount: Long = 0,
    val commentCount: Long = 0,
    val rewardAmount: Double = 0.0,
    val subscribeCount: Long = 0,
    val createdAt: LocalDateTime
)

/**
 * 稿费记录实体
 */
@Entity(
    tableName = "income_records",
    foreignKeys = [
        ForeignKey(
            entity = WorkEntity::class,
            parentColumns = ["id"],
            childColumns = ["workId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workId"), Index("platformType"), Index("month")]
)
data class IncomeRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val workId: Long,
    val platformType: String,
    val month: String,  // YYYY-MM
    val type: String,   // IncomeType.name
    val amount: Double,
    val wordCount: Long = 0,
    val note: String? = null,
    val createdAt: LocalDateTime
)

/**
 * 平台账号配置实体
 */
@Entity(tableName = "platform_accounts")
data class PlatformAccountEntity(
    @PrimaryKey
    val platformType: String,
    
    val isEnabled: Boolean = false,
    val isAuthorized: Boolean = false,
    val username: String? = null,
    val authToken: String? = null,  // 加密存储
    val refreshToken: String? = null,
    val tokenExpireAt: LocalDateTime? = null,
    val lastSyncAt: LocalDateTime? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
