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

package com.example.android.devbyteviewer.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface VideoDao {
    // database로부터 모든 videos를 fetch 하는 메소드
    // LiveData를 리턴으로 받아 database에 있는 data가 변할 때 UI에 있는 data를 갱신할 수 있게 함
    @Query("select * from databasevideo")
    fun getVideos(): LiveData<List<DatabaseVideo>>

    // network로부터 fetch 된 video list를 database에 insert 하는 메소드, 충돌 전략은 교체로 정함
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll( videos: List<DatabaseVideo>)
}

// Database를 정의함, Dao에 접근할 수 있게 VideoDao 타입의 변수를 정의함
@Database(entities = [DatabaseVideo::class], version = 1)
abstract class VideosDatabase: RoomDatabase() {
    abstract val videoDao: VideoDao
}

// Database를 싱글톤으로 만들어서 사용할 수 있게 인스턴스를 정의함
private lateinit var INSTANCE: VideosDatabase

// Database 초기화
fun getDatabase(context: Context): VideosDatabase {
    synchronized(VideosDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
            VideosDatabase::class.java,
            "videos").build()
        }
    }
    return INSTANCE
}