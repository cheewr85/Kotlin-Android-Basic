package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

// GameFragmet에 쓸 ViewModel 정의
class GameViewModel : ViewModel() {

    companion object {

        // Time when the game is over
        private const val DONE = 0L

        // Countdown time interval
        private const val ONE_SECOND = 1000L

        // Total time for the game
        private const val COUNTDOWN_TIME = 60000L
    }

    // Countdown time
    private val _currentTime = MutableLiveData<Long>()
    val currentTime: LiveData<Long>
       get() = _currentTime

    // The String version of the current time
    val currentTimeString = Transformations.map(currentTime) { time ->
        DateUtils.formatElapsedTime(time)
    }

    // CountDownTimer 변수 선언
    private val timer: CountDownTimer

    // GameFragment에 있는 data를 옮김
    // The current word - LiveData 변환 전
//    var word = ""
    // LiveData 변환 후
//    val word = MutableLiveData<String>()
    // ViewModel 내부에서만 쓰기 위해서 MutableLiveData, LiveData로 나눔
    private val _word = MutableLiveData<String>()
    val word: LiveData<String>
       get() = _word


    // The current score - LiveData 변환 전
//    var score = 0
    // LiveData 변환 후
//    val score = MutableLiveData<Int>()
    // MutableLiveData 수정을 위해서 ViewModel 내부에서 사용
    private val _score = MutableLiveData<Int>()
    // 외부에서 사용하기 위한 score LiveData, setter를 통한 접근은 불가하지만 그런 작업은 _score로 처리하고 그 값으로 초기화를 함
    val score: LiveData<Int>
       get() = _score

    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>

    // Event which triggers the end of the game
    private val _eventGameFinish = MutableLiveData<Boolean>()
    val eventGameFinish: LiveData<Boolean>
       get() = _eventGameFinish

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
        // LiveData에 대해서 초기화를 해줘야함(setValue하지 않고 property만으로 설정 가능)
//        word.value = ""
        // ViewModel 내부에서만 수정함
        _word.value = ""

//        score.value = 0
        // LiveData 수정을 위해 변경한 값으로 설정(ViewModel 내부에서만 씀)
        _score.value = 0

        // Creates a timer which triggers the end of the game when it finishes
        timer = object: CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {
            override fun onTick(millisUntilFinished: Long) {
                // 매 tick 혹은 interval마다 호출됨, 이때 매개변수를 활용해서 값을 업데이트함
                _currentTime.value = millisUntilFinished / ONE_SECOND
            }

            override fun onFinish() {
                // 타이머가 끝났을 때 호출되는 함수, LiveData를 끝내고 이벤트를 끝내는 함수 호출
                _currentTime.value = DONE
                onGameFinish()
            }

        }

        timer.start()
    }

    // ViewModel에 데이터를 다루는 함수도 옮김
    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        if (wordList.isEmpty()) {
            // 남아있는 단어가 없으면 다시 리스트 리셋함, 타이머가 끝나면 종료하는 것으로 바뀌었으므로
            resetList()
        } else {
            //Select and remove a word from the list
//            word = wordList.removeAt(0)
            // LiveData 이후
//            word.value = wordList.removeAt(0)
            // ViewModel 내부에서만 접근
            _word.value = wordList.removeAt(0)
        }
        // 이 부분은 GameFragment에서 호출되어 UI가 갱신될 것임
//        updateWordText()
//        updateScoreText()
    }

    // 데이터 처리를 하는 함수이기 때문에 ViewModel로 옮김
    /** Methods for buttons presses **/
    fun onSkip() {
        // LiveData 전
//          score--

        // LiveData 이후
        // null일 수도 있기 때문에 null체크를 하고 기존처럼 빼기연산을 함
//        score.value = (score.value)?.minus(1)
        // ViewModel 내부에서만 접근하게함
        _score.value = (score.value)?.minus(1)
        nextWord()
    }

    fun onCorrect() {
        // LiveData 전
//        score++

        // LiveData 이후
//        score.value = (score.value)?.plus(1)
        // ViewModel 내부에서만 접근하게함
        _score.value = (score.value)?.plus(1)
        nextWord()
    }

    /** Method for the game completed event **/
    fun onGameFinish() {
        _eventGameFinish.value = true
    }

    /** Method for the game completed event **/
    fun onGameFinishComplete() {
        _eventGameFinish.value = false
    }


    override fun onCleared() {
        super.onCleared()
        // Cancel the timer
        timer.cancel()
    }
}