package com.example.android.trackmysleepquality.sleeptracker

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.convertDurationToFormatted
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight


// TextView의 text를 변환해서 설정함
@BindingAdapter("sleepDurationFormatted")
fun TextView.setSleepDurationFormatted(item: SleepNight) {
    text = convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, context.resources)
}

// sleepQuality를 포맷에 맞게 변환해서 설정하는 함수
@BindingAdapter("sleepQualityString")
fun TextView.setSleepQualityString(item: SleepNight) {
    text = convertNumericQualityToString(item.sleepQuality, context.resources)
}

// imageView의 image를 설정하기 위한 함수
@BindingAdapter("sleepImage")
fun ImageView.setSleepImage(item: SleepNight) {
    setImageResource(when (item.sleepQuality) {
        0 -> R.drawable.ic_sleep_0
        1 -> R.drawable.ic_sleep_1
        2 -> R.drawable.ic_sleep_2
        3 -> R.drawable.ic_sleep_3
        4 -> R.drawable.ic_sleep_4
        5 -> R.drawable.ic_sleep_5
        else -> R.drawable.ic_sleep_active
    })
}
