package com.example.android.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.android.navigation.databinding.FragmentTitleBinding

class TitleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Data Binding을 활용해서 Fragment's view를 만듬
        // inflater는 binding layout을 inflate 하기 위한 것, R.layout.fragment_title은 말 그대로 layout xml 파일임
        // container는 부모 ViewGroup을 가르키고 false는 attachToParent를 false 한 것
        val binding = DataBindingUtil.inflate<FragmentTitleBinding>(inflater, R.layout.fragment_title,container,false)
        return binding.root
    }


}