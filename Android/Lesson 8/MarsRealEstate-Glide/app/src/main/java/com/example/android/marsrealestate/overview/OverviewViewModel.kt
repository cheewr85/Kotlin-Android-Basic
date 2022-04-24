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
 *
 */

package com.example.android.marsrealestate.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.marsrealestate.network.MarsApi
import com.example.android.marsrealestate.network.MarsProperty
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


// 상태를 나타내는 enum class
enum class MarsApiStatus { LOADING, ERROR, DONE }
/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */
class OverviewViewModel : ViewModel() {

    // 현재 status에 대한 LiveData
    private val _status = MutableLiveData<MarsApiStatus>()
    val status: LiveData<MarsApiStatus>
        get() = _status

    // 사진 URL 객체들을 담기 위한 LiveData 정의
    private val _properties = MutableLiveData<List<MarsProperty>>()

    val properties: LiveData<List<MarsProperty>>
        get() = _properties


    /**
     * Call getMarsRealEstateProperties() on init so we can display status immediately.
     */
    init {
        getMarsRealEstateProperties()
    }

    /**
     * Sets the value of the status LiveData to the Mars API status.
     */
    private fun getMarsRealEstateProperties() {
        // 로딩중 상태로 갱신(메소드 호출시)
        _status.value = MarsApiStatus.LOADING
        // 코루틴을 활용해 API 통신을 함
        viewModelScope.launch {
            try {
                // API 통신 성공시 해당 value를 성공적으로 받아왔는지 체크
                _properties.value = MarsApi.retrofitService.getProperties()
                _status.value = MarsApiStatus.DONE
            } catch (e: Exception) {
                // API 통신 실패시 처리
                _status.value = MarsApiStatus.ERROR
                // LiveData를 empty 값으로 나타냄
                _properties.value = ArrayList()
            }
        }
    }
}
