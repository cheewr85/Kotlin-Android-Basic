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

package com.example.android.trackmysleepquality

import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import org.junit.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


/**
 * This is not meant to be a full set of tests. For simplicity, most of your samples do not
 * include tests. However, when building the Room, it is helpful to make sure it works before
 * adding the UI.
 */

@RunWith(AndroidJUnit4::class)
class SleepDatabaseTest {

    private lateinit var sleepDao: SleepDatabaseDao
    private lateinit var db: SleepDatabase

    // Test Code로 RoomDB를 테스트 하기 위해 DB를 만들고 처리함
    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, SleepDatabase::class.java)
                // Allowing main thread queries, just for testing.
                .allowMainThreadQueries()
                .build()
        sleepDao = db.sleepDatabaseDao
    }

    // 테스트 이후 DB를 삭제함
    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    // 만든 테스트 DB 기준 Insert 함수 확인, 정상적으로 SleepNight가 들어갔는지 확인함, Dao를 통해서 불러오고 확인함
    @Test
    @Throws(Exception::class)
    fun insertAndGetNight() {
        val night = SleepNight()
        sleepDao.insert(night)
        val tonight = sleepDao.getTonight()
        assertEquals(tonight?.sleepQuality, -1)
    }

    // Update 하고 Get 함수를 통해서 SleepNight 조회하기
    @Test
    @Throws(Exception::class)
    fun updateAndGetNight() {
        val night = SleepNight()
        sleepDao.insert(night)
        val updatenight = SleepNight()
        sleepDao.update(updatenight)
        val getnight = sleepDao.get(1)
        assertEquals(getnight?.sleepQuality, updatenight?.sleepQuality)
    }

    // Clear 하고 Get 함수로 통해서 조회해보기
    @Test
    @Throws(Exception::class)
    fun clearAndGetNight() {
        val night = SleepNight()
        sleepDao.insert(night)

        val getNight = sleepDao.get(1)
        assertEquals(getNight?.sleepQuality, -1)

        sleepDao.clear()
        assertEquals(sleepDao.get(1), null)
    }

    // GetAllNight를 통해 모두 조회, LiveData 관련해서 테스트 코드 작성은 현재 코드랩에서 문제가 있음
    // LiveData 자체가 옵저버 패턴을 쓰기 때문에 이를 제대로 체크하기 위해서 확장함수 처리해야함
    // 그렇기 때문에 해당 챌린지에서 하기 쉽지 않음
    // 참고 https://stackoverflow.com/questions/44270688/unit-testing-room-and-livedata
//    @Test
//    @Throws(Exception::class)
//    fun getAllNight() {
//        val night1 = SleepNight()
//        val night2 = SleepNight()
//        sleepDao.insert(night1)
//        sleepDao.insert(night2)
//
//        val allNight = sleepDao.getAllNights()
//        val size = allNight
//        assertEquals(allNight.value, )
//
//    }

}