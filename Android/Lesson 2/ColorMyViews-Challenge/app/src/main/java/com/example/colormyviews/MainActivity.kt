package com.example.colormyviews

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setListeners()
    }

    private fun makeColored(view: View) {

        // Boxes using custom colors for background
//            R.id.red_button -> box_three_text.setBackgroundResource(R.color.my_red)
//            R.id.yellow_button -> box_four_text.setBackgroundResource(R.color.my_yellow)
//            R.id.green_button -> box_five_text.setBackgroundResource(R.color.my_green)

        // CodeLab에선 kotlinx를 통해서 View를 직접 import해서 넣는 방식을 위처럼 했지만
        // 현재에는 Kotlinx의 null 안정성 문제등 직접 사용하기 위해서 라이브러리를 추가해서 해야함
        // 그러므로 직접 View를 불러와서 처리하는 방식으로 바꿈
        val boxOneText = findViewById<TextView>(R.id.box_one_text)
        val boxTwoText = findViewById<TextView>(R.id.box_two_text)
        val boxThreeText = findViewById<TextView>(R.id.box_three_text)
        val boxFourText = findViewById<TextView>(R.id.box_four_text)
        val boxFiveText = findViewById<TextView>(R.id.box_five_text)

        val redButton = findViewById<Button>(R.id.red_button)
        val greenButton = findViewById<Button>(R.id.green_button)
        val yellowButton = findViewById<Button>(R.id.yellow_button)

        val labelText = findViewById<TextView>(R.id.label_text)
        val infoText = findViewById<TextView>(R.id.info_text)

        when (view.id) {
            // Box에 따라 색깔을 바꿈
            R.id.box_one_text -> view.setBackgroundColor(Color.DKGRAY)
            R.id.box_two_text -> view.setBackgroundColor(Color.GRAY)
            R.id.box_three_text -> view.setBackgroundColor(Color.BLUE)
            R.id.box_four_text -> view.setBackgroundColor(Color.MAGENTA)
            R.id.box_five_text -> view.setBackgroundColor(Color.BLUE)


            // 버튼 색 설정
            R.id.red_button -> boxThreeText.setBackgroundResource(R.color.my_red)
            R.id.yellow_button -> boxFourText.setBackgroundResource(R.color.my_yellow)
            R.id.green_button -> boxFiveText.setBackgroundResource(R.color.my_green)

            else -> {
                view.setBackgroundColor(Color.LTGRAY)
                boxOneText.setBackgroundColor(Color.DKGRAY)
                boxTwoText.setBackgroundColor(Color.GRAY)
                boxThreeText.setBackgroundColor(Color.BLUE)
                boxFourText.setBackgroundColor(Color.MAGENTA)
                boxFiveText.setBackgroundColor(Color.BLUE)
                redButton.visibility = View.GONE
                greenButton.visibility = View.GONE
                yellowButton.visibility = View.GONE
                labelText.visibility = View.GONE
                infoText.visibility = View.GONE
            }
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

        // 버튼을 불러옴
        val redButton = findViewById<Button>(R.id.red_button)
        val greenButton = findViewById<Button>(R.id.green_button)
        val yellowButton = findViewById<Button>(R.id.yellow_button)

        // 위에서 정의한 View 인스턴스를 List로 정의해서 저장함
        val clickableViews: List<View> = listOf(
            boxOneText,
            boxTwoText,
            boxThreeText,
            boxFourText,
            boxFiveText,
            rootConstraintLayout,
            redButton,
            greenButton,
            yellowButton
        )

        // 그리고 각 리스트 별로 makeColored 메소드를 적용시킴
        for (item in clickableViews) {
            item.setOnClickListener { makeColored(it) }
        }
    }
}