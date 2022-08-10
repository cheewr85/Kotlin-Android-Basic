## Headers in RecyclerView
- `RecyclerView`에서 서로 다른 레이아웃을 사용하는 items을 포함하는 일반적인 원리가 있음

- 하나의 흔한 예는 list나 grid의 headers를 가지고 있는 것임

- list의 경우 item content를 표현하기 위한 하나의 header를 가지고 있음, list는 또한 하나의 list에서 item을 그룹화하고 분리하기 위한 여러 header가 있을 수 있음

- `RecyclerView`는 당신의 데이터나 각 item이 어떤 타입의 layout인지에 대해서 알지 못함

- `LayoutManager`는 스크린에 item을 배치하지만 adapter는 data를 adapt하고 `RecyclerView`에 view holder의 전달함, 그래서 header를 만들기 위해선 adapter에 추가해야함

### Two ways of adding headers
- `RecyclerView`에선 list의 모든 item이 0부터 시작하는 index number와 일치함, 예를 들어 아래와 같음

![one](/Android/img/thirtyfive.png)

- list의 header를 추가하는 방법 중 하나는 header가 보여야할 곳의 index를 체크함으로써 다른 `ViewHolder`를 adapter가 사용해서 조정하는 것이 있음

- `Adapter`는 header를 계속 추적할 의무가 있음, 예를 들어 table의 위에 header를 보여주기 위해서 zero-index item을 올리면서 header를 위한 다른 `ViewHolder`를 반환해야함, 그러면 모든 다른 item이 아래와 같이 header offset에 매핑될 것임

![one](/Android/img/thirtysix.png)

- header를 추가하는 다른 방법 중 하나는 data grid를 위해 지원하는 dataset을 수정하는 것임

- 모든 data는 list에 저장되어서 보여지기 때문에 header로 나타내는 item을 포함하기 위해 list를 수정할 수 있음

- 이 부분은 이해하기는 쉽지만, 어떻게 객체를 설계해야할지 생각해야하고 단일 list안에 서로 다른 item 타입을 결합해야함

- 이 방식을 적용시키면 adapter에 전달된 항목이 표시될 것임

- 그래서 item의 위치가 0인 것은 header이고 위치가 1인 것은 화면에 있는 항목에 직접 매핑되는 `SleepNight`임

![one](/Android/img/thirtyseven.png)

- 각각의 방법 모두 장단점이 있음

- dataset을 직접 수정하는 것은 adapter code를 많이 수정하지 않으면서 data의 list를 조정하면서 header logic을 추가할 수 있음

- 반면에 headers를 위해 Index를 체크하면서 서로 다른 `ViewHolder`를 사용하는 것은 header의 layout에 더 많은 자율성을 줄 수 있음, 그리고 또한 adapter가 지원하는 데이터를 수정하지 않고 view의 어떻게 data가 adapt되는지 조정할 수 있음

- 예시 앱에선 다른 `ViewHolder`를 사용해 header를 쓸 것임, list의 index를 보고 `ViewHolder`를 사용할 것임

-------

### Add a header to your RecyclerView
- 먼저 data의 item을 나타내는 클래스를 정의함

- 여기서 `sealed` 클래스로 정의함, 이 타입은 닫힌 타입인데 `DataItem`의 모든 하위 클래스는 이 파일에서 정의해야함을 의미함

- 결과적으로 하위 클래스의 수를 컴파일러가 알게됨, 즉 `DataItem`에 대해서 다른 부분의 코드에선 정의할 수 없음(adapter를 손상시킴)

- 그리고 그 안에 data item의 서로 다른 타입을 나타내는 두 가지 클래스를 정의함, 첫 번째 클래스는 `sleepNight`라고 불리는 단일 값을 가지고 `SleepNight`로 wrapper한 `SleepNightItem`을 만듬, 여기서 sealed class의 일부로 만들기 위해서 `DataItem`을 상속받음 

- 두 번째 클래스는 header를 나타내는 `Header` 클래스임, header는 실제 데이터가 없기 때문에 `object`로 선언할 수 있음, 이는 `Header`인스턴스는 오직 하나라는 것임을 의미함, `DataItem`도 상속받음

- 그리고 `abstract Long` 속성의 `id`를 정의함, adapter가 `DiffUtil`을 item이 어떻게 언제 바뀌었는지 결정하는데 사용할 때 `DiffItemCallback`은 각 item의 id를 알아야만 함, 그리고 이 값을 `SleepNightItem`과 `Header`가 오버라이딩 해야함

- 각 값을 오버라이딩하여서 할당해줌, 여기서 `Header`에서 `id`의 경우 매우 작은 값으로 해서 `nightId`와 충돌이 일어나지 않게 해줌

```kotlin
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
```

- 그리고 header를 위한 `ViewHolder`의 layout을 만듬

```xml
<?xml version="1.0" encoding="utf-8"?>
<TextView xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/text"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:textAppearance="?android:attr/textAppearanceLarge"
    android:text="@string/header_text"
    android:padding="8dp"/>
```

- 그 다음 header에 대한 `ViewHolder` 클래스를 만듬

```kotlin
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
```

- header를 추가는 완료하였고 이제 Adapter 클래스를 전반적으로 손을 봐야함

- 먼저 header와 item인지 구분하기 위한 상수를 정의함

```kotlin
    private val ITEM_VIEW_TYPE_HEADER = 0
    private val ITEM_VIEW_TYPE_ITEM = 1
```

- 그리고 `getItemViewType()`을 오버라이딩 하여서 현재 item 상수 타입을 기준으로 header와 item을 리턴하게 함

```kotlin
// 현재 item 타입을 기준으로 상수를 할당함
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.SleepNightItem -> ITEM_VIEW_TYPE_ITEM
        }
    }
```

- 그리고 Adapter에 정의를 수정해서 `DataItem`을 추가함
```kotlin
class SleepNightAdapter(val clickListener: SleepNightListener):
       ListAdapter<DataItem, RecyclerView.ViewHolder>(SleepNightDiffCallback()) {
```

- 그리고 기존에 오버라이딩한 함수도 수정함

```kotlin
override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            ITEM_VIEW_TYPE_HEADER -> TextViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }
```

```kotlin
  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val nightItem = getItem(position) as DataItem.SleepNightItem
                holder.bind(nightItem.sleepNight, clickListener)
            }
        }
    }
```

- DiffUtil도 수정함

```kotlin
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
```

- header에 대해서 submit하고 추가함 그와 관련된 함수 정의함

```kotlin
// submitList를 사용하는 대신 ListAdapter를 제공함
    fun addHeaderAndSubmitList(list: List<SleepNight>?) {
        val items = when (list) {
            // null을 준다면 header를 리턴하고 아니면 header와 list를 같이 넘김
            null -> listOf(DataItem.Header)
            else -> listOf(DataItem.Header) + list.map { DataItem.SleepNightItem(it) }
        }
        submitList(items)
    }
```

- 그리고 해당 함수를 Fragment에서 변경함

```kotlin
// nights 데이터를 adapter에 넣어줌
        sleepTrackerViewModel.nights.observe(viewLifecycleOwner, Observer{
            it?.let {
                // list의 변화를 ListAdapter에 알려줌
                adapter.addHeaderAndSubmitList(it)
            }
        })

```

### Use coroutines for list manipulations
- header만을 추가하는 것은 문제가 안되는데 위에서 정의한 `addHeaderAndSubmitList`의 경우 item이 수백개에 header가 여러개라면 이것이 UI thread에서 돌아가면 문제를 일으킬 수 있음

- 그래서 이를 coroutine을 활용하여 list 연산을 처리하고 연산이 끝날 때 `Dispatchers.Main`으로 바꿔서 `submitList` 처리를 하면 됨

```kotlin
 fun addHeaderAndSubmitList(list: List<SleepNight>?) {
        adapterScope.launch {
            val items = when (list) {
                null -> listOf(DataItem.Header)
                else -> listOf(DataItem.Header) + list.map { DataItem.SleepNightItem(it) }
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }
```

### Extend the header to span across the screen
- 현재 header의 경우 다른 grid의 span과 동일한 크기를 차지하면서 보여짐, 이를 아예 3개의 span을 horizontal하게 변경할 것임

- 이를 변경하기 위해서 `GridLayoutManager`에 columns을 넘어서 쓰게 하기 위해서 정의해야함

- `SpanSizeLookup`을 `GridLayoutManager`에서 설정함, 이 설정은 list의 각 아이템을 위해 몇 개의 span을 쓸 것인지 결정하는 설정임

- Fragment에서 `manager`를 아래와 같이 정의함

```kotlin
val manager = GridLayoutManager(activity, 3)
```

- 그리고 이를 확장해서 `SpanSizeLookup`을 설정하며 `getSpanSize`를 재정의함

- 여기서 index가 0인 것은 header이므로 3으로 사이즈를 설정하고 나머지는 1로 설정함

```kotlin
    manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) =  when (position) {
                0 -> 3
                else -> 1
            }
        }
```

- 그리고 header 디자인을 아래를 추가해서 개선함

```xml
android:textColor="@color/white_text_color"
android:layout_marginStart="16dp"
android:layout_marginTop="16dp"
android:layout_marginEnd="16dp"
android:background="@color/colorAccent"
```