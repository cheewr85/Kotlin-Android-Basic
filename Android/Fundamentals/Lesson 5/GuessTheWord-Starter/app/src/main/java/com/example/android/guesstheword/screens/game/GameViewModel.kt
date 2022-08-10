package com.example.android.guesstheword.screens.game

import android.util.Log
import androidx.lifecycle.ViewModel

// GameFragmet에 쓸 ViewModel 정의
class GameViewModel : ViewModel() {
    // GameFragment에 있는 data를 옮김
    // The current word
    var word = ""
    // The current score
    var score = 0
    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>

    // ViewModel에 데이터를 다루는 함수도 옮김
    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
            "queen",
            "hospital",
            "basketball",
            "cat",
            "change",
            "snail",
            "soup",
            "calendar",
            "sad",
            "desk",
            "guitar",
            "home",
            "railway",
            "zebra",
            "jelly",
            "car",
            "crow",
            "trade",
            "bag",
            "roll",
            "bubble"
        )
        wordList.shuffle()
    }

    init {
        // ViewModel 사용 후 메소드 초기화에 넣어줌
        resetList()
        nextWord()
        Log.i("GameViewModel", "GameViewModel created!")
    }

    // ViewModel에 데이터를 다루는 함수도 옮김
    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        if (!wordList.isEmpty()) {
            //Select and remove a word from the list
            word = wordList.removeAt(0)
        }
        // 이 부분은 GameFragment에서 호출되어 UI가 갱신될 것임
//        updateWordText()
//        updateScoreText()
    }

    // 데이터 처리를 하는 함수이기 때문에 ViewModel로 옮김
    /** Methods for buttons presses **/
    fun onSkip() {
        score--
        nextWord()
    }

    fun onCorrect() {
        score++
        nextWord()
    }


    override fun onCleared() {
        super.onCleared()
        Log.i("GameViewModel", "GameViewModel destroyed!")
    }
}