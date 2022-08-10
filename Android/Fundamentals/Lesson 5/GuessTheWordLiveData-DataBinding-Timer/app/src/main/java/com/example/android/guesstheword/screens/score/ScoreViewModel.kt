package com.example.android.guesstheword.screens.score

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// score 값을 처리하기 위한 ViewModel
class ScoreViewModel(finalScore: Int) : ViewModel() {
    // The final score
//    var score = finalScore
    // LiveData 사용
    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int>
       get() = _score

    // eventPlayAgain을 위한 LiveData
    private val _eventPlayAgain = MutableLiveData<Boolean>()
    val eventPlayAgain: LiveData<Boolean>
       get() = _eventPlayAgain

    init {
        _score.value = finalScore

    }

    // 이벤트 상황에 따른 Boolean 설정 메소드
    fun onPlayAgain() {
        _eventPlayAgain.value = true
    }

    fun onPlayAgainComplete() {
        _eventPlayAgain.value = false
    }
}