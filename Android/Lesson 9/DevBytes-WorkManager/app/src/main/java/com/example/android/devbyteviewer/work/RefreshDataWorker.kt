package com.example.android.devbyteviewer.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.android.devbyteviewer.database.getDatabase
import com.example.android.devbyteviewer.repository.VideosRepository
import retrofit2.HttpException
import timber.log.Timber

class RefreshDataWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "com.example.android.devbyteviewer.work.RefreshDataWorker"
    }

    override suspend fun doWork(): Result {

        // database와 repository 인스턴스를 만들어서 연결
        val database = getDatabase(applicationContext)
        val repository = VideosRepository(database)

        try {
            // repository와 database 연결했으므로 refresh 메소드로 data fetch함
            repository.refreshVideos()
            Timber.d("Work request for sync is run")
        } catch (e: HttpException) {
            // 에러 발생시 다시 시도함
            return Result.retry()
        }

        return Result.success()
    }

}