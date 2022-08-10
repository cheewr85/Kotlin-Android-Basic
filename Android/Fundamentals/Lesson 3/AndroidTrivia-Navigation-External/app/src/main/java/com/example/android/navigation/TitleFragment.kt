package com.example.android.navigation

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
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

        // 앞서 Navigation.xml에서 Title과 Game을 연결했기 때문에 play 버튼을 누르면 GameFragment로 가게끔 설정함
        // Navigation.xml에서 action에 대해서 설정해서 id값이 할당됐음 Play 버튼을 누르면 그렇게 실행되도록 리스너를 등록함
        binding.playButton.setOnClickListener { view : View ->
            view.findNavController().navigate(TitleFragmentDirections.actionTitleFragmentToGameFragment())
        }

        // 옵션 메뉴를 사용하기 위해서 true로 값 설정
        setHasOptionsMenu(true)
        return binding.root

    }

    // XML로 만든 옵션메뉴를 불러와서 적용함
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.options_menu, menu)
    }

    // 메뉴 아이템 클릭시 action이 실행되게함
    // 해당 action은 aboutfragment로 navigate하게끔 처리함
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController()) || super.onOptionsItemSelected(item)
    }
}