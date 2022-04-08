/*
 * Copyright 2018, The Android Open Source Project
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

package com.example.android.navigation

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.android.navigation.databinding.FragmentGameWonBinding


class GameWonFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding: FragmentGameWonBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_game_won, container, false)
        // Next Match 버튼 리스너 등록함
        binding.nextMatchButton.setOnClickListener { view: View ->
            view.findNavController().navigate(GameWonFragmentDirections.actionGameWonFragmentToGameFragment())
        }

        // GameFragment에서 넘겨받은 값에 대해서 argument로 받아서 나타냄
        val args = GameWonFragmentArgs.fromBundle(requireArguments())
        Toast.makeText(context, "NumCorrect: ${args.numCorrect}, NumQuestions: ${args.numQuestions}", Toast.LENGTH_LONG).show()

        // 공유버튼을 옵션에 추가하기 위해서 설정
        setHasOptionsMenu(true)

        return binding.root
    }

    // 공유하기 위한 Intent를 보내는 메소드
    private fun getShareIntent() : Intent {
        val args = GameWonFragmentArgs.fromBundle(requireArguments())
        // ACTION_SEND를 통해서 공유하고 싶은 메시지를 인텐트로 전달함
        // 데이터 타입은 setType으로 정하고, 실제 데이터는 EXTRA_TEXT로 구체화함
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/plain")
            .putExtra(Intent.EXTRA_TEXT, getString(R.string.share_success_text, args.numCorrect, args.numQuestions))
        return shareIntent
    }

    // Intent를 불러와서 실행하는 메소드
    private fun shareSuccess() {
        startActivity(getShareIntent())
    }

    // 옵션 메뉴를 만드는 메소드 오버라이딩함
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.winner_menu, menu)
        // Activity에서 잘 처리되는지 확인하기 위해 PackageManager로 체크함
        // PackageManager는 앱과 액티비티를 계속 추적을 함
        // 권한을 얻기 위해서 resolveActivity를 불러옴, 만약 null이라면 shareIntent가 제대로 처리된 것이 아니므로 menu item을 보이게 처리함
        if(getShareIntent().resolveActivity(requireActivity().packageManager)==null) {
            menu.findItem(R.id.share).isVisible = false
        }
    }

    // 메뉴 아이템 클릭시 처리하는 메소드를 오버라이딩함
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.share -> shareSuccess()
        }
        return super.onOptionsItemSelected(item)
    }
}
