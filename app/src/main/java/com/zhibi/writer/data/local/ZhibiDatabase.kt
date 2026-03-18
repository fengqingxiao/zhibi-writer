package com.zhibi.writer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zhibi.writer.data.local.dao.*
import com.zhibi.writer.data.local.entity.*

/**
 * 执笔写作数据库
 */
@Database(
    entities = [
        WorkEntity::class,
        VolumeEntity::class,
        ChapterEntity::class,
        OutlineNodeEntity::class,
        MaterialEntity::class,
        ChapterHistoryEntity::class,
        WritingRecordEntity::class,
        DeletedChapterEntity::class,
        AutoSaveCacheEntity::class,
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class ZhibiDatabase : RoomDatabase() {
    abstract fun workDao(): WorkDao
    abstract fun chapterDao(): ChapterDao
    abstract fun outlineDao(): OutlineDao
    abstract fun materialDao(): MaterialDao
    abstract fun historyDao(): HistoryDao
}
