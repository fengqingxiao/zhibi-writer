package com.zhibi.writer.data.model

import kotlinx.serialization.Serializable

/**
 * 平台配置
 */
@Serializable
data class PlatformConfig(
    val id: String,
    val name: String,
    val icon: String,
    val baseUrl: String,
    val isEnabled: Boolean = false,
    val isAuthorized: Boolean = false,
    val username: String? = null
)

/**
 * 平台类型
 */
enum class PlatformType {
    YUEWEN,       // 阅文集团（起点、QQ阅读等）
    FANQIE,       // 番茄小说
    QIMAO,        // 七猫小说
    JINJIANG,     // 晋江文学城
    ZONGHENG,     // 纵横中文网
    CUSTOM        // 自定义平台
}

/**
 * 平台发布状态
 */
enum class PublishStatus {
    DRAFT,        // 草稿
    PENDING,      // 待审核
    PUBLISHED,    // 已发布
    FAILED,       // 发布失败
    SCHEDULED     // 定时发布
}

/**
 * 发布记录
 */
@Serializable
data class PublishRecord(
    val id: Long = 0,
    val workId: Long,
    val chapterId: Long,
    val platformType: PlatformType,
    val platformChapterId: String? = null,
    val status: PublishStatus,
    val publishedAt: kotlinx.datetime.LocalDateTime? = null,
    val scheduledAt: kotlinx.datetime.LocalDateTime? = null,
    val errorMessage: String? = null,
    val readCount: Long = 0,
    val commentCount: Long = 0,
    val likeCount: Long = 0
)

/**
 * 平台作品绑定
 */
@Serializable
data class PlatformWorkBinding(
    val id: Long = 0,
    val workId: Long,
    val platformType: PlatformType,
    val platformWorkId: String,
    val platformWorkTitle: String,
    val lastSyncedChapter: Int = 0,
    val createdAt: kotlinx.datetime.LocalDateTime
)

/**
 * 平台数据统计
 */
@Serializable
data class PlatformStats(
    val platformType: PlatformType,
    val workId: Long,
    val date: kotlinx.datetime.LocalDate,
    val readCount: Long = 0,
    val readerCount: Long = 0,
    val collectCount: Long = 0,
    val recommendCount: Long = 0,
    val commentCount: Long = 0,
    val rewardAmount: Double = 0.0,
    val subscribeCount: Long = 0
)

/**
 * 稿费记录
 */
@Serializable
data class IncomeRecord(
    val id: Long = 0,
    val workId: Long,
    val platformType: PlatformType,
    val month: String,  // YYYY-MM
    val type: IncomeType,
    val amount: Double,
    val wordCount: Long = 0,
    val note: String? = null,
    val createdAt: kotlinx.datetime.LocalDateTime
)

/**
 * 稿费类型
 */
enum class IncomeType {
    SUBSCRIBE,    // 订阅收入
    REWARD,       // 打赏收入
    RECOMMEND,    // 推荐票收入
    ACTIVITY,     // 活动奖励
    FULL_ATTEND,  // 全勤奖
    CONTRACT,     // 签约奖金
    OTHER         // 其他
}
