package com.example.aboutme

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.example.aboutme.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    // myName 클래스 인스턴스 만들어 변수 할당
    private val myName: MyName = MyName("Aleks Haecky")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // 인스턴스 만든 변수를 data 태그로 정의한 myName 변수에 할당함
        binding.myName = myName

//        // 버튼을 불러오고 리스너를 달아서 처리함
//        findViewById<Button>(R.id.done_button).setOnClickListener {
//            // it은 DoneButton이 됨됨
//           addNickname(it)
//        }

        // Data binding 적용
        binding.doneButton.setOnClickListener {
            addNickname(it)
        }

        // TextView를 불러오고 리스너를 달아 처리함
        findViewById<TextView>(R.id.nickname_text).setOnClickListener {
            updateNickName(it)
        }
    }

    // 함수를 호출할 때 파라미터로 View를 넘김 여기선 DONE 버튼이 옴
    // Data binding 사용시 => 굳이 View를 파라미터로 받을 필요가 없음
    private fun addNickname(view: View) {
//        // EditText, TextView 불러옴
//        val editText = findViewById<EditText>(R.id.nickname_edit)
//        val nicknameTextView = findViewById<TextView>(R.id.nickname_text)

//        // TextView에 입력받은 text를 할당받고 EditText는 안보이게 처리함
//        nicknameTextView.text = editText.text
//        editText.visibility = View.GONE
//        // 버튼도 안보이게 처리함(view로 받아왔음)
//        view.visibility = View.GONE
//        // TextView는 보이게 함
//        nicknameTextView.visibility = View.VISIBLE

        // Data binding 적용
        // nicknameEdit.text는 Editable한 값이므로 이를 nicknameText에 text로 적용하기 위해서 toString으로 변환해서 줌
//        binding.nicknameText.text = binding.nicknameEdit.text.toString()
//        binding.nicknameEdit.visibility = View.GONE
//        binding.doneButton.visibility = View.GONE
//        binding.nicknameText.visibility = View.VISIBLE

        // 위의 DataBinding에서 Kotlin스럽게 초기화 할 수 있음 apply 사용해서 data binding 적용시킬 수 있음
        binding.apply {
            // 데이터 태그에 있는 myName 클래스에서 nickname 값을 할당함
            myName?.nickname = nicknameEdit.text.toString()
            invalidateAll()
            nicknameEdit.visibility = View.GONE
            doneButton.visibility = View.GONE
            nicknameText.visibility = View.VISIBLE
        }

        // TextView가 보이고 난 뒤 키보드도 사라지게 함
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)

    }

    // 다시 EditText로 입력받기 위한 함수
    private fun updateNickName(view: View) {
        val editText = findViewById<EditText>(R.id.nickname_edit)
        val doneButton = findViewById<Button>(R.id.done_button)

        // TextView를 누르면 다시 입력받기 위해 EditText, Button이 보이고 TextView는 사라지게 함
        editText.visibility = View.VISIBLE
        doneButton.visibility = View.VISIBLE
        view.visibility = View.GONE

        // 다시 입력을 위해서 focus를 주고 키보드가 나오게 함
        editText.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, 0)
    }
}