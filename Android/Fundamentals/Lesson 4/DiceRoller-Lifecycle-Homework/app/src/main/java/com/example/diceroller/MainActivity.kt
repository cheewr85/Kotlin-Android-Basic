package com.example.diceroller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

const val KEY_DICE = "dice_key"

class MainActivity : AppCompatActivity() {

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val resultText: TextView = findViewById(R.id.result_text)
        val dice = resultText.text.toString()

        outState.putString(KEY_DICE, dice)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val resultText: TextView = findViewById(R.id.result_text)

        val rollButton: Button = findViewById(R.id.roll_button)
        rollButton.setOnClickListener { rollDice() }

        if (savedInstanceState != null) {
            resultText.text = savedInstanceState.getString(KEY_DICE)
        }

    }

    private fun rollDice() {
        val randomInt = (1..6).random()

        val resultText: TextView = findViewById(R.id.result_text)
        resultText.text = randomInt.toString()
    }
}