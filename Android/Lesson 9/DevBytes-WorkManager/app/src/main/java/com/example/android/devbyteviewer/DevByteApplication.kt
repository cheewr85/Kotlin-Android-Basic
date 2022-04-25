/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.devbyteviewer

import android.app.Application
import android.os.Build
import androidx.work.*
import com.example.android.devbyteviewer.work.RefreshDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Override application to setup background work via WorkManager
 */
class DevByteApplication : Application() {

    // main thread에서 오래 걸려서 UI thread를 block 하는 것을 막기위해 coroutine 활용
    private val applicationScope = CoroutineScope(Dispatchers.Default)



    /**
     * Setup WorkManager background job to 'fetch' new network data daily
     */
    // 반복되는 백그라운드 작업을 설정하기 위한 메소드
    private fun setupRecurringWork() {

        // 제약조건을 걸기 위한 변수, 네트워크 제약조건을 걸고 build함
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true) // 배터리가 낮은 경우 실행 못하게함
            .setRequiresCharging(true) // 충전중일 때만 실행하게함
            .apply {
                // 23보다 높은 경우에만 user가 device를 활발히 쓰지 않을 때 진행시킴
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }
            .build()

        // 하루에 한 번 주기적으로 반복될 work request를 초기화하고 만듬
        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

//        // 15분 주기로 실행하는 작업임
//       val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(15, TimeUnit.MINUTES)
//           .setConstraints(constraints)
//           .build()

        Timber.d("Periodic Work request for sync is scheduled")

        // WorkManager로 스케줄링 설정을 함함
       WorkManager.getInstance().enqueueUniquePeriodicWork(
            RefreshDataWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest)
    }

    // coroutine으로 초기화시키는 함수
    private fun delayedInit() {
        // coroutine 실행, 시간이 걸리는 작업 처리함
        applicationScope.launch {
            Timber.plant(Timber.DebugTree())
            setupRecurringWork()
        }
    }

    /**
     * onCreate is called before the first screen is shown to the user.
     *
     * Use it to setup any background tasks, running expensive setup operations in a background
     * thread to avoid delaying app start.
     */
    override fun onCreate() {
        super.onCreate()
        delayedInit()
    }
}
