package com.example.android.trackmysleepquality.sleeptracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.convertDurationToFormatted
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.ListItemSleepNightBinding

// 해당 ViewHolder를 상속받음
class SleepNightAdapter : ListAdapter<SleepNight, SleepNightAdapter.ViewHolder>(SleepNightDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // data에서 position의 값을 가져왔으나 ListAdapter 및 DiffUtil 사용해서 item만을 쓰면 됨
        val item = getItem(position)
        // viewHolder inner class에 정의한 함수 활용해서 처리함
        holder.bind(item)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }


    // image, textview가 있는 해당 itemView에 대한 ViewHolder를 정의함
    class ViewHolder private constructor(val binding: ListItemSleepNightBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: SleepNight,
        ) {
            // sleep에 item을 할당함(db에서 불러온 값)
            binding.sleep = item
            // 최적화를 위해 호출
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                // inflate할 context를 참조하여 layoutInflater를 만듬
                val layoutInflater = LayoutInflater.from(parent.context)

                // data binding을 활용해 inflate해서 나타냄
                val binding = ListItemSleepNightBinding.inflate(layoutInflater, parent, false)

                // ViewHolder에서 binding을 넣어서 리턴함
                return ViewHolder(binding)
            }
        }
    }
}

// DiffUtil 사용을 위해 클래스 정의, 필요한 메소드 오버라이딩
class SleepNightDiffCallback : DiffUtil.ItemCallback<SleepNight>() {
    override fun areItemsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        // To change body of created functions use File | Settings | File Templates.
        // item의 변화를 확인하기 위해 nightId를 비교함
        return oldItem.nightId == newItem.nightId
    }

    override fun areContentsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        // To change body of created functions use File | Settings | File Templates.
        // item이 업데이트 됐다고 비교를 해서 ㅓ알려줌
        return oldItem == newItem

    }

}
