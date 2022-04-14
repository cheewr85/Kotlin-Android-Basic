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

package com.example.android.guesstheword.screens.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.android.guesstheword.R
import com.example.android.guesstheword.databinding.GameFragmentBinding

/**
 * Fragment where the game is played
 */
class GameFragment : Fragment() {

    // ViewModel 사용하기 위해 초기화
    private lateinit var viewModel: GameViewModel

    // ViewModel 사용 전
//    // The current word
//    private var word = ""
//
//    // The current score
//    private var score = 0
//
//    // The list of words - the front of the list is the next word to guess
//    private lateinit var wordList: MutableList<String>

    private lateinit var binding: GameFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.game_fragment,
                container,
                false
        )

        // ViewModel 사용 전
//        resetList()
//        nextWord()

        binding.correctButton.setOnClickListener { onCorrect() }
        binding.skipButton.setOnClickListener { onSkip() }
        // EndGame 버튼 클릭리스너
        binding.endGameButton.setOnClickListener { onEndGame() }

        Log.i("GameFragment", "Called ViewModelProvider.get")
        // ViewModelProvider에 GameFragment context를 넘기고 GameViewModel 클래스를 넘김
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        updateScoreText()
        updateWordText()
        return binding.root

    }

    /**
     * Resets the list of words and randomizes the order
     */
    // ViewModel 사용 전
//    private fun resetList() {
//        wordList = mutableListOf(
//                "queen",
//                "hospital",
//                "basketball",
//                "cat",
//                "change",
//                "snail",
//                "soup",
//                "calendar",
//                "sad",
//                "desk",
//                "guitar",
//                "home",
//                "railway",
//                "zebra",
//                "jelly",
//                "car",
//                "crow",
//                "trade",
//                "bag",
//                "roll",
//                "bubble"
//        )
//        wordList.shuffle()
//    }

    /** Methods for buttons presses **/

    private fun onSkip() {
        // ViewModel 사용 전
//        score--
//        nextWord()
        // ViewModel 사용 후, ViewModel에서 데이터를 갱신하고 Fragment에선 Update만 진행
        viewModel.onSkip()
        updateWordText()
        updateScoreText()
    }

    private fun onCorrect() {
        // ViewModel 사용 전
//        score++
//        nextWord()
        // ViewModel 사용 후, ViewModel에서 데이터를 갱신하고 Fragment에서 Update만 진행
        viewModel.onCorrect()
        updateScoreText()
        updateWordText()
    }

    // ViewModel 사용전
//    /**
//     * Moves to the next word in the list
//     */
//    private fun nextWord() {
//        if (!wordList.isEmpty()) {
//            //Select and remove a word from the list
//            word = wordList.removeAt(0)
//        }
//        updateWordText()
//        updateScoreText()
//    }


    /** Methods for updating the UI **/

    private fun updateWordText() {
        // ViewModel 사용 전
//        binding.wordText.text = word
        // ViewModel 사용 후, 해당 ViewModel에서 처리된 데이터를 반영해서 넣음
        binding.wordText.text = viewModel.word
    }

    private fun updateScoreText() {
        // ViewModel 사용 전
//        binding.scoreText.text = score.toString()
        // ViewModel 사용 후, 해당 ViewModel에서 처리된 데이터를 반영해서 넣음
        binding.scoreText.text = viewModel.score.toString()
    }

    /**
     * Called when the game is finished
     */
    private fun gameFinished() {
        Toast.makeText(activity, "Game has just finished", Toast.LENGTH_SHORT).show()
        val action = GameFragmentDirections.actionGameToScore()
        action.score = viewModel.score
        NavHostFragment.findNavController(this).navigate(action)
    }

    // End Game 버튼을 누르면 넘어가는 메소드
    private fun onEndGame() {
        gameFinished()
    }
}
