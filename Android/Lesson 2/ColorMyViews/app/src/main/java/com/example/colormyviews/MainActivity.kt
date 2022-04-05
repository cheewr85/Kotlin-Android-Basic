package com.example.colormyviews

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setListeners()
    }

    private fun makeColored(view: View) {
        // Box에 따라 색깔을 바꿈
        when (view.id) {

            R.id.box_one_text -> view.setBackgroundColor(Color.DKGRAY)
            R.id.box_two_text -> view.setBackgroundColor(Color.GRAY)
            R.id.box_three_text -> view.setBackgroundColor(Color.BLUE)
            R.id.box_four_text -> view.setBackgroundColor(Color.MAGENTA)
            R.id.box_five_text -> view.setBackgroundColor(Color.BLUE)
            else -> view.setBackgroundColor(Color.LTGRAY)
        }
        /*
        CodingChallenge -> view.setBackgroundResource를 통해서 색깔이 아닌 해당하는 이미지를 적용시켜서 변경하게 할 수 있음(박스별로)
        기존의 로직과 별반 다르지 않으므로 생략함
         */
    }

    private fun setListeners() {
        // makeColored 메소드를 각각의 View에 맞게 클릭 리스너를 달아주는 메소드, 각각 View를 불러옴
        val boxOneText = findViewById<TextView>(R.id.box_one_text)
        val boxTwoText = findViewById<TextView>(R.id.box_two_text)
        val boxThreeText = findViewById<TextView>(R.id.box_three_text)
        val boxFourText = findViewById<TextView>(R.id.box_four_text)
        val boxFiveText = findViewById<TextView>(R.id.box_five_text)

        val rootConstraintLayout = findViewById<View>(R.id.constraint_layout)

        // 위에서 정의한 View 인스턴스를 List로 정의해서 저장함
        val clickableViews: List<View> = listOf(boxOneText, boxTwoText, boxThreeText, boxFourText, boxFiveText, rootConstraintLayout)

        // 그리고 각 리스트 별로 makeColored 메소드를 적용시킴
        for (item in clickableViews) {
            item.setOnClickListener {makeColored(it)}
        }
    }
}