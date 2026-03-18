package com.zhibi.writer.di

import android.content.Context
import androidx.room.Room
import com.zhibi.writer.data.local.ZhibiDatabase
import com.zhibi.writer.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 数据库模块
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): ZhibiDatabase {
        return Room.databaseBuilder(
            context,
            ZhibiDatabase::class.java,
            "zhibi_writer.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    fun provideWorkDao(database: ZhibiDatabase): WorkDao {
        return database.workDao()
    }
    
    @Provides
    fun provideChapterDao(database: ZhibiDatabase): ChapterDao {
        return database.chapterDao()
    }
    
    @Provides
    fun provideOutlineDao(database: ZhibiDatabase): OutlineDao {
        return database.outlineDao()
    }
    
    @Provides
    fun provideMaterialDao(database: ZhibiDatabase): MaterialDao {
        return database.materialDao()
    }
    
    @Provides
    fun provideHistoryDao(database: ZhibiDatabase): HistoryDao {
        return database.historyDao()
    }
}
