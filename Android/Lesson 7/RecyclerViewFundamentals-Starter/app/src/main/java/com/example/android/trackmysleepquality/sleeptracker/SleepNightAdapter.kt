package com.example.android.trackmysleepquality.sleeptracker

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.TextItemViewHolder
import com.example.android.trackmysleepquality.convertDurationToFormatted
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight

// 해당 ViewHolder를 상속받음
class SleepNightAdapter: RecyclerView.Adapter<SleepNightAdapter.ViewHolder>() {
    var data = listOf<SleepNight>()
       set(value) {
           field = value
           notifyDataSetChanged()
       }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // data에서 position의 값을 가져옴
        val item = data[position]
        // viewHolder inner class에 정의한 함수 활용해서 처리함
        holder.bind(item)

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }


    // image, textview가 있는 해당 itemView에 대한 ViewHolder를 정의함
    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sleepLength: TextView = itemView.findViewById(R.id.sleep_length)
        val quality: TextView = itemView.findViewById(R.id.quality_string)
        val qualityImage: ImageView = itemView.findViewById(R.id.quality_image)

        fun bind(
            item: SleepNight,
        ) {
            // view의 resources를 레퍼런스로 참조함
            val res = itemView.context.resources

            // sleepLength에 text view를 설정함 time에 맞춰 format해서 변경경
            sleepLength.text =
                convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, res)

            // quality를 변환해서 나온 값을 설정함
            quality.text = convertNumericQualityToString(item.sleepQuality, res)

            // quality에 맞는 icon을 설정하게 함
            qualityImage.setImageResource(when (item.sleepQuality) {
                0 -> R.drawable.ic_sleep_0
                1 -> R.drawable.ic_sleep_1
                2 -> R.drawable.ic_sleep_2
                3 -> R.drawable.ic_sleep_3
                4 -> R.drawable.ic_sleep_4
                5 -> R.drawable.ic_sleep_5
                else -> R.drawable.ic_sleep_active
            })
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                // inflate할 context를 참조하여 layoutInflater를 만듬
                val layoutInflater = LayoutInflater.from(parent.context)

                // 해당 item layout을 inflate해서 나타냄
                val view = layoutInflater.inflate(R.layout.list_item_sleep_night, parent, false)

                // view로 만든 TextItemViewHolder를 리턴함
                return ViewHolder(view)
            }
        }
    }




}