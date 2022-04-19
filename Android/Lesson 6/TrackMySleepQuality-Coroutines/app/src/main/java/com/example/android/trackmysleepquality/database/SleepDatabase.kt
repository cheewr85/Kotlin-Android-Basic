/*
 * Copyright 2019, The Android Open Source Project
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

package com.example.android.trackmysleepquality.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Database 관련 기본 설정 처리함
@Database(entities = [SleepNight::class], version = 1, exportSchema = false)
abstract class SleepDatabase : RoomDatabase() {
    // Dao를 불러옴(다른 다양한 Dao도 불러올 수 있음)
    abstract val sleepDatabaseDao: SleepDatabaseDao

    // 인스턴스화 하지 않고 메소드에 접근가능함, 해당 클래스를 인스턴스화 할 필요 없음, 싱글톤으로 처리한 것
    companion object {
        @Volatile
        private var INSTANCE: SleepDatabase? = null

        // 인스턴스를 반환하는 함수
        fun getInstance(context: Context): SleepDatabase {
            // 여러개의 인스턴스가 생길 수 있으므로 synchronized 처리함
            synchronized(this) {
                // INSTANCE를 반환하게 함
                var instance = INSTANCE

                if(instance == null) {
                    // instance가 null이면 아직 database가 만들어지지 않은 것임
                    // database builder를 통해서 database를 만듬
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SleepDatabase::class.java,
                        "sleep_history_database"
                    ).fallbackToDestructiveMigration() //
                        .build()
                    INSTANCE = instance
                }

                return instance
            }
        }

    }
}
