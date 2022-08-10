package com.example.android.trackmysleepquality.sleeptracker

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.ListItemSleepNightBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ClassCastException


// header와 item 구분
private val ITEM_VIEW_TYPE_HEADER = 0
private val ITEM_VIEW_TYPE_ITEM = 1

// 해당 ViewHolder를 상속받음
class SleepNightAdapter(val clickListener: SleepNightListener) : ListAdapter<DataItem, RecyclerView.ViewHolder>(SleepNightDiffCallback()) {

    // list 연산을 위한 코루틴 스코프 정의
    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            // ViewHolder로 반환될 경우 해당 타입으로 SleepNightItem으로 캐스팅하여서 어댑터에 아이템 연결 및 리스너 연결함
            is ViewHolder -> {
                val nightItem = getItem(position) as DataItem.SleepNightItem
                holder.bind(nightItem.sleepNight, clickListener)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // 타입에 따라서 ViewHolder 다르게 생성함
        return when(viewType) {
            ITEM_VIEW_TYPE_HEADER -> TextViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    // submitList를 사용하는 대신 ListAdapter를 제공함
    fun addHeaderAndSubmitList(list: List<SleepNight>?) {
        // list 연산에 대해서 코루틴에서 연산을 함(Main thread가 아닌)
        adapterScope.launch {
            val items = when (list) {
                // null을 준다면 header를 리턴하고 아니면 header와 list를 같이 넘김
                null -> listOf(DataItem.Header)
                else -> listOf(DataItem.Header) + list.map { DataItem.SleepNightItem(it) }
            }
            // 연산이 끝나면 Main Thread로 변경, items을 submit해서 UI에 그리게 함함
           withContext(Dispatchers.Main) {
                submitList(items)
            }
        }

    }

    // 현재 item 타입을 기준으로 상수를 할당함
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.SleepNightItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    // Header에 사용하기 위한 ViewHolder
    class TextViewHolder(view: View): RecyclerView.ViewHolder(view) {
        companion object {
            fun from(parent: ViewGroup): TextViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.header, parent, false)
                return TextViewHolder(view)
            }
        }
    }


    // image, textview가 있는 해당 itemView에 대한 ViewHolder를 정의함
    class ViewHolder private constructor(val binding: ListItemSleepNightBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: SleepNight,
            clickListener: SleepNightListener,
        ) {
            // sleep에 item을 할당함(db에서 불러온 값)
            binding.sleep = item
            // 최적화를 위해 호출
            binding.executePendingBindings()
            // clicklistener를 할당해줌
            binding.clickListener = clickListener
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
class SleepNightDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        // To change body of created functions use File | Settings | File Templates.
        // item의 변화를 확인하기 위해 id를 비교함
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        // To change body of created functions use File | Settings | File Templates.
        // item이 업데이트 됐다고 비교를 해서 ㅓ알려줌
        return oldItem == newItem

    }

}

// item click listener에 대한 클래스, 콜백을 인자로 넣음
class SleepNightListener(val clickListener: (sleepId: Long) -> Unit) {
    // 보여지는 list item을 클릭했을 때 onClick을 호출함, SleepNight의 타입 night를 넘김, 콜백으로 night의 id값을 넘김
    fun onClick(night: SleepNight) = clickListener(night.nightId)
}

sealed class DataItem {
    // SleepNight의 정보를 담고 있는 data class
    data class SleepNightItem(val sleepNight: SleepNight): DataItem() {
        // id 값을 할당해줌
        override val id = sleepNight.nightId
    }

    // header를 표현하기 위한 object
    object Header: DataItem() {
        // nightId와 충돌이 일어나지 않도록 매우 작은 값으로 설정함
        override val id = Long.MIN_VALUE
    }

    abstract val id: Long
}
