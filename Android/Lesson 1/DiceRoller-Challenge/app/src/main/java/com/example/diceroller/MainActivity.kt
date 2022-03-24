package com.example.diceroller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.isDigitsOnly

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rollButton: Button = findViewById(R.id.roll_button)
        rollButton.setOnClickListener { rollDice() }

        val countupButton: Button = findViewById(R.id.countup_button)
        countupButton.setOnClickListener { countUp() }

    }

    private fun rollDice() {
        val randomInt = (1..6).random()

        val resultText: TextView = findViewById(R.id.result_text)
        resultText.text = randomInt.toString()
    }

    private fun countUp() {
        val resultText: TextView = findViewById(R.id.result_text)

        // 만약 해당 문자열이 숫자가 아니라면 1로 설정하게 함
        if (!resultText.text.toString().isDigitsOnly()) {
            resultText.text = "1"
        } else {
            // 6보다 작은 경우 1씩 카운팅 하게끔 정함
            if (resultText.text.toString() < "6") {
                var temp = resultText.text.toString().toInt()
                temp++
                resultText.text = temp.toString()
            } else { // 6이상인 경우 return하게 함(아무것도 안하게)
                return
            }
        }
    }
}