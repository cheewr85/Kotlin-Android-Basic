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

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import androidx.lifecycle.*
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.launch

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application) {

        // SleepNight 담을 LiveData 선언
        private var tonight = MutableLiveData<SleepNight?>()

        // SleepQualityFragment로 갈 때 LiveData 변화를 체크하기 위한 LiveData 선언
        private val _navigateToSleepQuality = MutableLiveData<SleepNight>()
        // 캡슐화를 통해 LiveData를 설정함
        val navigateToSleepQuality: LiveData<SleepNight>
          get() = _navigateToSleepQuality

        // Snackbar 관련 LiveData
        private var _showSnackbarEvent = MutableLiveData<Boolean>()
        // 캡슐화 진행
        val showSnackBarEvent: LiveData<Boolean>
          get() = _showSnackbarEvent


        // ViewModel 초기화 하는 block
        init {
                initializeTonight()
        }

        // nights 값을 가져옴(DB에 접근해서)
        private val nights = database.getAllNights()

        // nights를 nightsString으로 변환을 함(Util.kt의 함수를 씀)
        val nightsString = Transformations.map(nights) {
                        nights -> formatNights(nights, application.resources)
        }

        /*Room은 Dispatchers.IO를 사용하므로 main thread를 쓸 일 없음*/
        // viewModelScope를 실행시켜 Coroutine에서 Database에서 tonight 값을 가져오는 함수
        private fun initializeTonight() {
                viewModelScope.launch {
                        tonight.value = getTonightFromDatabase()
                }
        }

        // Database에서 night값을 가져오는 함수, database를 불러오고 coroutine을 사용하므로 suspend로 선언
        private suspend fun getTonightFromDatabase(): SleepNight? {
                // database에 접근해서 가져옴
                var night = database.getTonight()
                // 시간체크를 해서 같지 않으면 null 반환
                if (night?.endTimeMilli != night?.startTimeMilli) {
                        night = null
                }
                return night
        }

        fun onStartTracking() {
                viewModelScope.launch {
                        val newNight = SleepNight()
                        // SleepNight만든 것을 insert함, 이 함수는 private suspend로 Dao의 함수가 아님
                        insert(newNight)
                        // tonight을 업데이트 함
                        tonight.value = getTonightFromDatabase()
                }
        }

        // DAO의 insert를 suspend로 Coroutine을 활용해서 처리하는 함수
        private suspend fun insert(night: SleepNight) {
                database.insert(night)
        }

        // Tracking을 멈추는 함수
        fun onStopTracking() {
                viewModelScope.launch {
                        val oldNight = tonight.value ?: return@launch
                        oldNight.endTimeMilli = System.currentTimeMillis()
                        update(oldNight)
                        // SleepQualityFragment로 navigate하고 처리하기 위해 변수를 설정함, 값이 있을 때 SleepQualityFragment로 navigate함
                        _navigateToSleepQuality.value = oldNight
                }
        }

        // insert 함수와 동일하게 database를 활용하여 update하는 함수
        private suspend fun update(night: SleepNight) {
                database.update(night)
        }

        // claer하는 함수
        fun onClear() {
                viewModelScope.launch {
                        clear()
                        tonight.value = null
                        _showSnackbarEvent.value = true
                }
        }

        // database에서 clear 처리함
        suspend fun clear() {
                database.clear()
        }

        // naviagtion으로 가기 때문에 variable을 null로 함
        fun doneNavigating() {
                _navigateToSleepQuality.value = null
        }

        // 버튼이 활성화 유무를 판단하게 설정하는 값
        val startButtonVisible = Transformations.map(tonight) {
                it == null
        }

        val stopButtonVisible = Transformations.map(tonight) {
                it != null
        }

        val clearButtonVisible = Transformations.map(nights) {
                it?.isNotEmpty()
        }

        // snackbar에 대한 완료 처리
        fun doneShowingSnackbar() {
                _showSnackbarEvent.value = false
        }

}

