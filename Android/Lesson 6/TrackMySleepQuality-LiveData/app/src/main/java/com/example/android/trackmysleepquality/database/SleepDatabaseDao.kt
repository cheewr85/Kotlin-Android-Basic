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

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

// DataBase에 접근해서 처리할 Dao 인터페이스 정의
@Dao
interface SleepDatabaseDao {
    // 데이터 베이스 삽입하기 위한 어노테이션 및 함수
    @Insert
    suspend fun insert(night: SleepNight)

    // 데이터 베이스 업데이트 하기 위한 어노테이션 및 함수
    @Update
    suspend fun update(night: SleepNight)

    // daily_sleep_quality_table에서 nightId가 key와 매칭되는 모든 column을 선택하는 함수
    @Query("SELECT * from daily_sleep_quality_table WHERE nightId = :key")
    suspend fun get(key: Long): SleepNight?

    // 테이블의 모든 항목을 지우는 쿼리문을 쓴 함수 (@Delete는 하나의 아이템만 지우므로)
    @Query("DELETE FROM daily_sleep_quality_table")
    suspend fun clear()

    // nightId 기준으로 내림차순으로 정렬된 테이블에서 하나의 아이템만 선택해서 가져오는 쿼리문을 적용한 함수
    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC LIMIT 1")
    suspend fun getTonight(): SleepNight?

    // 내림차순으로 정렬된 테이블의 모든 column을 반환하게함
    // LiveData를 적용시켰기 때문에 Room은 LiveData를 유지함(명시적으로 한 번만 가져와도)
    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC")
    fun getAllNights(): LiveData<List<SleepNight>>
}
