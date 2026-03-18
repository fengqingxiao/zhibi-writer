package com.zhibi.writer.data.local

import androidx.room.TypeConverter
import com.zhibi.writer.data.local.entity.*
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Room类型转换器
 */
class Converters {
    
    // LocalDateTime
    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.toString()
    }
    
    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it) }
    }
    
    // WorkStatus
    @TypeConverter
    fun fromWorkStatus(value: WorkStatus): String {
        return value.name
    }
    
    @TypeConverter
    fun toWorkStatus(value: String): WorkStatus {
        return WorkStatus.valueOf(value)
    }
    
    // ChapterStatus
    @TypeConverter
    fun fromChapterStatus(value: ChapterStatus): String {
        return value.name
    }
    
    @TypeConverter
    fun toChapterStatus(value: String): ChapterStatus {
        return ChapterStatus.valueOf(value)
    }
    
    // SyncStatus
    @TypeConverter
    fun fromSyncStatus(value: SyncStatus): String {
        return value.name
    }
    
    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus {
        return SyncStatus.valueOf(value)
    }
    
    // OutlineNodeType
    @TypeConverter
    fun fromOutlineNodeType(value: OutlineNodeType): String {
        return value.name
    }
    
    @TypeConverter
    fun toOutlineNodeType(value: String): OutlineNodeType {
        return OutlineNodeType.valueOf(value)
    }
    
    // OutlineStatus
    @TypeConverter
    fun fromOutlineStatus(value: OutlineStatus): String {
        return value.name
    }
    
    @TypeConverter
    fun toOutlineStatus(value: String): OutlineStatus {
        return OutlineStatus.valueOf(value)
    }
    
    // MaterialCategory
    @TypeConverter
    fun fromMaterialCategory(value: MaterialCategory): String {
        return value.name
    }
    
    @TypeConverter
    fun toMaterialCategory(value: String): MaterialCategory {
        return MaterialCategory.valueOf(value)
    }
    
    // List<String>
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Json.encodeToString(value)
    }
    
    @TypeConverter
    fun toStringList(value: String): List<String> {
        return Json.decodeFromString(value)
    }
    
    // List<Long>
    @TypeConverter
    fun fromLongList(value: List<Long>): String {
        return Json.encodeToString(value)
    }
    
    @TypeConverter
    fun toLongList(value: String): List<Long> {
        return Json.decodeFromString(value)
    }
}
