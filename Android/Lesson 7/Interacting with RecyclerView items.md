### Make items clickable
- `SleepDetailFragment`가 추가됨

- 여기서 `RecyclerView`에서 유저가 item을 클릭했을 때 detail 화면으로 넘어가게 할 수 있음 그러기 위해서 처음으로 click을 받고 어떤 아이템이 click 됐는지 알아야함, 그리고 그 click에 대해서 action으로 응답을 해줘야함

- 이러한 click listener를 추가하는 것은 `RecyclerView`에서 보는 것은 부적절함, 어떤 아이템을 클릭했는지 찾기가 힘듬

- `ViewHolder` 객체에서 아이템이 클릭된 것에 대한 정보를 두기 최적의 장소임

- `ViewHolder`가 click을 확인하기 좋은 곳이면 이 click을 handle 해줄 곳은 `ViewHolder`임, `Adapter`는 아키텍쳐 관점에서 보면 데이터를 adapt 해주는 곳이지 app logic을 다루는 곳이 아님

- 그래서 `ViewModel`을 통해 data에 접근하고 click의 응답으로 어떤일이 일어날지 정하는 로직을 둠

- 먼저 `SleepNightAdapter`에서 click listener를 만들고 item layout으로부터 onClcik이 발생하게 함, 그것을 클래스를 만들고 함수를 추가함

- 그리고 리스너 처리를 위해 정보에 대해서 넘기기 위해 `SleepNight`의 타입을 넘김

- 그리고 `onClickListener` 콜백 인자를 생성자 안에 할당하여 `onClick`에 처리함

```kotlin
// item click listener에 대한 클래스, 콜백을 인자로 넣음
class SleepNightListener(val clickListener: (sleepId: Long) -> Unit) {
    // 보여지는 list item을 클릭했을 때 onClick을 호출함, SleepNight의 타입 night를 넘김, 콜백으로 night의 id값을 넘김
    fun onClick(night: SleepNight) = clickListener(night.nightId)
}
```

- 그리고 `list_item_sleep_night`에서 `data` block에 `SleepNightListener` 클래스를 새로운 변수로 넣음(data binding이 가능하게끔 하기 위해), 그러면 `onClick()` 함수에 접근할 수 있게됨

```xml
<variable
            name="clickListener"
type="com.example.android.trackmysleepquality.sleeptracker.SleepNightListener" />
```

- 그리고 어떤 list item을 눌러도 반응하게끔 `constraintLayout`에 `onClick`을 추가함

```xml
<androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:onClick="@{() -> clickListener.onClick(sleep)}">
```

- 그리고 `SleepNightAdapter`의 생성자에 `clickListener`를 받아서 처ㅓ리하게끔 아래와 같이 Adapter를 재정의함

```kotlin
class SleepNightAdapter(val clickListener: SleepNightListener):
       ListAdapter<SleepNight, SleepNightAdapter.ViewHolder>(SleepNightDiffCallback()) {
```

- 그리고 `onBindViewHolder()`에도 추가함

```kotlin
holder.bind(getItem(position)!!,clickListener)
```

- 그리고 `clickListener`을 매개변수로 `bind`에 추가함

- 그리고 `ViewHolder` 클래스에서 `clickListener`를 할당해서 추가함

```kotlin
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
```

- 그리고 item이 tap 되면 toast를 띄우게끔 `SleepTrackerFragment`에서 `adapter` 변수에 람다식으로 추가해서 띄움

```kotlin
val adapter = SleepNightAdapter(SleepNightListener { nightId ->
   Toast.makeText(context, "${nightId}", Toast.LENGTH_LONG).show()
})
```

### Handle item clicks
- 여기서 Toast 메시지가 아닌 `RecyclerView` 아이템을 누르면 detail fragment로 넘어가게 할 것임

- `SleepTrackerViewModel`에서 click handler function을 만들어 처리하고 `LiveData`도 선언함

```kotlin
// Detail에 가기 위해 쓴 LiveData 변수
    private val _navigateToSleepDetail = MutableLiveData<Long>()
    val navigateToSleepDetail
      get() = _navigateToSleepDetail
    
    // id를 넘겨 Detail로 넘아가는 클릭 핸들러 함수
    fun onSleepNightClicked(id: Long) {
        _navigateToSleepDetail.value = id
    }
    
    // navigating이 끝나면 value를 초기화하는 함수
    fun onSleepDetailNavigated() {
        _navigateToSleepDetail.value = null
    }
```

- 그리고 `SleepTrackerFragment`에서 리스너 등록과 `ViewModel`에서 클릭 이벤트 처리를 추가함, 여기서 id를 값으로 넘겨줌, 기존의 toast 메시지를 보여준 것을 대체함

```kotlin
        // 앞서 만든 Adapter 생성
        val adapter = SleepNightAdapter(SleepNightListener { nightId ->
//            Toast.makeText(context, "${nightId}", Toast.LENGTH_LONG).show()
            // viewModel에서 클릭 이벤트 리스너 등록함 리스너에서 받은 id를 넘김
            sleepTrackerViewModel.onSleepNightClicked(nightId)
        })
```

- 그리고 `LiveData`에 대한 observe를 추가해서 `night`를 넘겨주고 처리함, 그러면 여기서 navigate 처리가 진행됨

```kotlin
// 아이템이 클릭될 때 navigating을 위해 variable의 상태를 Observer하는 것을 추가함
        sleepTrackerViewModel.navigateToSleepDetail.observe(viewLifecycleOwner, Observer { night ->
                night?.let {
                    this.findNavController().navigate(SleepTrackerFragmentDirections.actionSleepTrackerFragmentToSleepDetailFragment(night))
                    sleepTrackerViewModel.onSleepDetailNavigated()
                }
        })
```

- 마지막으로 binding adapter에 모두 `null`처리를 추가해줌, `null`이 호출될 수 있으므로

```kotlin
// TextView의 text를 변환해서 설정함
@BindingAdapter("sleepDurationFormatted")
fun TextView.setSleepDurationFormatted(item: SleepNight) {
    item?.let {
        text = convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, context.resources)
    }
}

// sleepQuality를 포맷에 맞게 변환해서 설정하는 함수
@BindingAdapter("sleepQualityString")
fun TextView.setSleepQualityString(item: SleepNight) {
    item?.let {
        text = convertNumericQualityToString(item.sleepQuality, context.resources)
    }
}

// imageView의 image를 설정하기 위한 함수
@BindingAdapter("sleepImage")
fun ImageView.setSleepImage(item: SleepNight) {
    item?.let {
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
}
```