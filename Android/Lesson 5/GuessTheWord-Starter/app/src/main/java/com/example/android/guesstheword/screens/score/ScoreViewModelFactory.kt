package com.example.android.guesstheword.screens.score

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

// ScoreViewModel을 인스턴스화 하기 위한 Factory
class ScoreViewModelFactory(private val finalScore: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        // ScoreViewModel 클래스로 인스턴스화 됐다면 해당 값을 넘겨주게 리턴함
        if(modelClass.isAssignableFrom(ScoreViewModel::class.java)) {
            return ScoreViewModel(finalScore) as T
        }
        // 아니라면 예외처리함
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}