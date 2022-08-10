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

package com.example.android.guesstheword.screens.score

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.guesstheword.R
import com.example.android.guesstheword.databinding.ScoreFragmentBinding

/**
 * Fragment where the final score is shown, after the game is over
 */
class ScoreFragment : Fragment() {

    // ViewModel과 ViewModelFactory 초기화
    private lateinit var viewModel: ScoreViewModel
    private lateinit var viewModelFactory: ScoreViewModelFactory

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        // Inflate view and obtain an instance of the binding class.
        val binding: ScoreFragmentBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.score_fragment,
                container,
                false
        )

        // ScoreFragment에서의 나온 값의 최종을 가져옴
        viewModelFactory = ScoreViewModelFactory(ScoreFragmentArgs.fromBundle(requireArguments()).score)

        // viewModel을 ViewModelProvider를 통해서 만듬
        // 이때 ScoreFragment context인 this와 Factory 패턴으로 만들기 위해 viewModelFactory도 같이 넘김
        viewModel = ViewModelProvider(this, viewModelFactory).get(ScoreViewModel::class.java)

        // scoreText에 viewModel에서 값을 가져와 업데이트함
//        binding.scoreText.text = viewModel.score.toString()

//        // Add observer for score
//        viewModel.score.observe(viewLifecycleOwner, Observer {
//            newScore -> binding.scoreText.text = newScore.toString()
//        })

        // Specify the fragment view as the lifecycle owner of the binding.
        // This is used so that the binding can observe LiveData updates
        binding.lifecycleOwner = viewLifecycleOwner

        // Navigate back to game when button is pressed
        viewModel.eventPlayAgain.observe(viewLifecycleOwner, Observer {
            playAgain -> if(playAgain) {
                findNavController().navigate(ScoreFragmentDirections.actionRestart())
                viewModel.onPlayAgainComplete()
            }
        })

//        binding.playAgainButton.setOnClickListener { viewModel.onPlayAgain() }

        // Set the viewmodel for databinding - this allows the bound layout access
        // to all the data in the ViewModel
        binding.scoreViewModel = viewModel

        return binding.root
    }
}
