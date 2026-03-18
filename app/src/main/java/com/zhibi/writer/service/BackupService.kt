package com.zhibi.writer.service

import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.zhibi.writer.R
import com.zhibi.writer.data.local.repository.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * 自动备份服务
 */
@AndroidEntryPoint
class BackupService : LifecycleService() {
    
    @Inject
    lateinit var workRepository: WorkRepository
    
    @Inject
    lateinit var chapterRepository: ChapterRepository
    
    @Inject
    lateinit var materialRepository: MaterialRepository
    
    @Inject
    lateinit var outlineRepository: OutlineRepository
    
    @Inject
    lateinit var historyRepository: HistoryRepository
    
    private var backupJob: Job? = null
    
    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "backup_channel"
        
        const val ACTION_START_BACKUP = "com.zhibi.writer.START_BACKUP"
        const val ACTION_STOP_BACKUP = "com.zhibi.writer.STOP_BACKUP"
        
        fun start(context: Context) {
            val intent = Intent(context, BackupService::class.java).apply {
                action = ACTION_START_BACKUP
            }
            context.startForegroundService(intent)
        }
        
        fun stop(context: Context) {
            val intent = Intent(context, BackupService::class.java).apply {
                action = ACTION_STOP_BACKUP
            }
            context.startService(intent)
        }
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_BACKUP -> startBackup()
            ACTION_STOP_BACKUP -> stopBackup()
        }
        return super.onStartCommand(intent, flags, startId)
    }
    
    private fun startBackup() {
        // 创建通知渠道
        createNotificationChannel()
        
        // 启动前台服务
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("执笔写作")
            .setContentText("自动备份服务运行中")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        
        startForeground(NOTIFICATION_ID, notification)
        
        // 启动定时备份任务
        backupJob = lifecycleScope.launch {
            while (true) {
                delay(10 * 60 * 1000) // 每10分钟检查一次
                performAutoBackup()
            }
        }
    }
    
    private fun stopBackup() {
        backupJob?.cancel()
        backupJob = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
    
    private suspend fun performAutoBackup() {
        try {
            val backupDir = getBackupDirectory()
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val backupFile = File(backupDir, "auto_backup_$timestamp.json")
            
            // 收集所有数据
            val works = workRepository.getAllWorks().first()
            val backupData = mutableMapOf<String, Any>()
            
            works.forEach { work ->
                val chapters = chapterRepository.getChaptersByWork(work.id).first()
                val materials = materialRepository.getMaterialsByWork(work.id).first()
                val outlines = outlineRepository.getOutlineNodesByWork(work.id).first()
                
                backupData[work.id.toString()] = mapOf(
                    "work" to work,
                    "chapters" to chapters,
                    "materials" to materials,
                    "outlines" to outlines
                )
            }
            
            // 写入备份文件
            val json = kotlinx.serialization.json.Json.encodeToString(backupData)
            FileOutputStream(backupFile).use { it.write(json.toByteArray()) }
            
            // 清理旧备份（保留最近10个）
            cleanupOldBackups(backupDir)
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun getBackupDirectory(): File {
        val dir = File(filesDir, "backups")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
    
    private fun cleanupOldBackups(backupDir: File) {
        val backups = backupDir.listFiles { file ->
            file.name.startsWith("auto_backup_")
        }?.sortedByDescending { it.lastModified() } ?: return
        
        if (backups.size > 10) {
            backups.drop(10).forEach { it.delete() }
        }
    }
    
    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                CHANNEL_ID,
                "自动备份",
                android.app.NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "自动备份服务通知"
            }
            
            val notificationManager = getSystemService(android.app.NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    override fun onBind(intent: Intent): IBinder? = null
}
