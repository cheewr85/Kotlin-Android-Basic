### Get started and review what you have so far
![one](/Android/img/thirtytwo.png)

- 앞서 만든 앱에서 `RecyclerView`의 활용도를 보면 위와 같이 돌아가는 것을 알 수 있음

- 유저의 입력으로부터 앱은 `SleepNight` 객체들의 리스트를 만들고 각 `SleepNight` 객체는 해당 객체내의 field와 관련된 내용이 들어있음

- `SleepNightAdapter`는 `SleepNight`객체들의 리스트를 `RecyclerView`가 사용하고 보여줄 수 있는 것으로 adapt 해 줌

- `SleepNightAdatper`는 views와 data와 data를 보여주기 위한 recycler view의 met 정보에 대한 `ViewHolder`를 만듬

- `RecyclerView`는 얼마나 많은 아이템을 보여줘야 하는지 결정하기 위해 `SleepNightAdapter`를 사용함(`getItemCount()`)

- `RecyclerView`는 `onCreateViewHolder()`와 `onBindViweHolder()`를 통해 나타내기 위한 data를 view holder가 bound하기 위해 사용함

- `RecyclerView`가 list안에 있는 item이 변경되고 이것에 대해서 업데이트 한 것을 알려주기 위해 현재 코드는 `notifyDataSetChanged()`를 `SleepNightAdapter`에서 사용함

```kotlin
var data =  listOf<SleepNight>()
   set(value) {
       field = value
       notifyDataSetChanged()
   }
```

- 하지만 `notifyDataSetChanged()`가 `RecyclerView`에게 전체 list가 invalid할 가능성이 있다고 말하게 되면 `RecyclerView`는 rebind하고 스크린에 item이 보이지 않더라도 list의 전체 아이템을 redraw함

- 이러한 작업은 상당히 불필요한 작업이고 만약 list가 복잡하거나 커서 시간이 오래걸리면 화면이 깜빡이거나 스크롤시 버벅일 수 있음

- 이 문제를 해결하기 위해서 `RecyclerView`에게 정확히 무엇이 바뀌었는지 말해줄 필요가 있음, 그러면 `RecyclerView`가 오직 그 view만 바꿀 수 있음

- `RecyclerView`는 단일 element를 업데이트하는 많은 API를 가지고 있음 `notifyItemChanged()`를 통해서 바뀐 것을 알려줘도 되고 다른 비슷한 함수로 item을 추가하거나 제거하거나 옮길 수 있음, 이것을 모두 수작업으로 하는 것인데 이런 작업은 상당히 코드가 길어지고 불필요한 작업일 수 있음, 그래서 더 나은 방식이 존재함

- `DiffUtil`이 이런 작업을 효율적으로 하게끔 처리해줌

- `RecyclerView`는 2개의 리스트의 차이를 연산해주는 `DiffUtil`이라는 클래스를 가지고 있음

- `DiffUtil`은 오래된 list와 새로운 list를 가지고 무엇이 다른지 찾아냄, 그리고 아이템이 추가되고 제거되고 옮겨진 것도 찾아냄, 이 때 old list로부터 new list를 생산하는데 변화를 최소화해서 찾아내는 알고리즘을 씀

- `DiffUtil`이 변화점을 찾으면 `RecyclerView`는 이 정보를 사용하여 변경되거나 추가되거나 삭제되거나 옮겨진 아이템만을 업데이트함, 이 방식이 앞서 함수를 호출해서 전체 list를 다시 그리는 것보다 훨씬 효율적임

### Refresh list content with DiffUtil
- 이제 `SleepNightAdatper`에 `DiffUtil`을 사용해 `RecyclerView`가 데이터를 바꾸것을 최적화함

- `DiffUtil` 기능을 쓰기 위해 `DiffUtil.ItemCallback`을 상속받음, 그리고 `SleepNightAdapter` 맨 밑에 `DiffUtil` 클래스를 정의함

- 그리고 오버라이딩 하는 메소드를 통해 어떻게 list를 찾고 item이 변했는지 알아냄

- `areItemsTheSame`에서는 `SleepNight`의 `oldItem`과 `newItem`이 같다는 것을 반환함, 만약 items이 같은 `nightId`를 가지고 있다면 그것은 같고 `true`를 반환함, 만약 아니라면 `false`를 반환함

- `DiffUtil`은 item이 추가된 건지 제거된건지 옮겨진건지 발견하기 위해 이 테스트를 사용함

```kotlin
override fun areItemsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
   return oldItem.nightId == newItem.nightId
}
```

- `areContentsTheSame`에서는 `oldItem`과 `newItem`이 같은 데이터를 담고 있는지 체크함, 즉 그들이 같은지 확인함

- 이 equality 체크는 모든 필드를 확인할 것임, 왜냐하면 `SleepNigth`는 data class이기 때문임

- `Data` 클래스들은 `equals`와 당신을 위한 몇 가지 메소드를 자동으로 정의함

- 만약 `oldItem`과 `newItem` 사이에 차이가 있다면 이 코드는 `DiffUtil`에게 item이 업데이트 됐다고 알려줌

```kotlin
override fun areContentsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
   return oldItem == newItem
}
```

### Use ListAdapter to manage your list
- `RecyclerView`에서 list가 변할 때 보여주는 것은 흔한 패턴임, 이 때 `RecyclerView`는 list를 지원하는 `RecyclerView` adapter를 만드는데 도움을 주는 `ListAdapter`라는 adapter class를 제공함

- `ListAdapter`는 list를 계속해서 추적하고 list가 업데이트 되었을 때 adapter에게 알려줌

- 이를 적용하기 위해서 기존의 Adapter에 상속을 바꿈

- `ListAdapter`의 첫번째 인자로 `SleepNight`를 그 다음에 `SleepNightAdapter.ViewHolder`를 두고 생성자 매개변수로 `SleepNightDiffCallback()`을 둠, `ListAdapter`가 해당 `DiffUtil`을 list가 변할 때 알아채기위해 사용할 것임

```kotlin
class SleepNightAdapter : ListAdapter<SleepNight, SleepNightAdapter.ViewHolder>(SleepNightDiffCallback()) {
```

- 이렇게 되면 이전 Adapter에서 있던 setter가 있는 `data` 필드를 지워도 되고 `getItemCount()` 역시 `ListAdapter`가 알아서 구현해주기 때문에 지워도 됨

- 그리고 `onBindViewHolder()`에서 `item`의 경우, `data` 대신 `item`을 사용해서 `getItem(position)`을 통해 `ListAdapter`에 제공해줌

```kotlin
val item = getItem(position)
```

- 그리고 코드로 `ListAdatper`에게 변한 list가 사용하가능할 때를 알려줘야함, 이때 `ListAdapter`는 `submitList()` 메소드를 통해 `ListAdapter`에 새로운 버전의 list가 이용가능하다고 알려줌

- 이 메소드가 호출되면 `ListAdapter`는 새로운 list를 old list와 차이점과 item이 추가되고 제거되고 옮겨지고 변경된 것을 탐지함 그리고 `ListAdapter`가 `RecyclerView`에 보여지는 item을 업데이트 함

- 예시앱에서 `SleepTrackerFragment`에서 `sleepTrackerViewModel`에 observer에 `data` 변수를 삭제하고 `adapter.submitList(it)`으로 변경하면 됨

```kotlin
sleepTrackerViewModel.nights.observe(viewLifecycleOwner, Observer {
   it?.let {
       adapter.submitList(it)
   }
})
```

### Use DataBinding with RecyclerView
- `list_item_sleep_night.xml`를 만들 때 그리고 이를 inflate 할 때 `findViewById`를 활용했음

- 이를 Databinding으로 개선할 수 있음, 먼저 `list_item_sleep_night`의 경우, data binding으로 xml을 다시 재구성함, 그리고 `data` 태그로 `SleepNight`도 추가함

```xml
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools">

    <data>
        <variable
            name="sleep"
            type="com.example.android.trackmysleepquality.database.SleepNight" />
    </data>

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

        <ImageView
            android:id="@+id/quality_image"
            android:layout_width="@dimen/icon_size"
            android:layout_height="60dp"
            android:layout_marginStart="16dp"
            android:layout_marginTop="8dp"
            android:layout_marginBottom="8dp"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            tools:srcCompat="@drawable/ic_sleep_5" />

        <TextView
            android:id="@+id/sleep_length"
            android:layout_width="0dp"
            android:layout_height="20dp"
            android:layout_marginStart="8dp"
            android:layout_marginTop="8dp"
            android:layout_marginEnd="16dp"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toEndOf="@id/quality_image"
            app:layout_constraintTop_toTopOf="@id/quality_image"
            tools:text="Wednesday" />

        <TextView
            android:id="@+id/quality_string"
            android:layout_width="0dp"
            android:layout_height="20dp"
            android:layout_marginTop="8dp"
            app:layout_constraintEnd_toEndOf="@id/sleep_length"
            app:layout_constraintStart_toStartOf="@id/sleep_length"
            app:layout_constraintTop_toBottomOf="@id/sleep_length"
            app:layout_constraintHorizontal_bias="0.0"
            tools:text="Excellent!!!" />

    </androidx.constraintlayout.widget.ConstraintLayout>
</layout>
```

- 그리고 여기서 `Binding` 객체가 생성되어 `ListItemSleepNightBinding`이 생김

- 그리고 `SleepNightAdapter`로 가서 `companion object`에 있는 `view` 변수를 삭제해도 됨

```kotlin
val view = layoutInflater
       .inflate(R.layout.list_item_sleep_night, parent, false)
```

- 그리고 이것을 `binding`을 새로운 변수로 정의해서 불러옴, 그리고 이를 반환함

```kotlin
val binding =
ListItemSleepNightBinding.inflate(layoutInflater, parent, false)

return ViewHolder(binding)
```

- 그리고 여기서 `ViewHolder`에 대해서도 생성자로 `binding`으로 받게끔 아래와 같이 수정함

```kotlin
class ViewHolder private constructor(val binding: ListItemSleepNightBinding) : RecyclerView.ViewHolder(binding.root){
```

- 그리고 `ViewHolder` 안에 초기화도 `binding` 객체로 바꿈

```kotlin
val sleepLength: TextView = binding.sleepLength
val quality: TextView = binding.qualityString
val qualityImage: ImageView = binding.qualityImage
```

- 하지만 사실 `binding`객체를 쓰기 때문에 위와 같이 정의하는게 의미가 없음 어차피 아래와 같이 그대로 적용해서 쓸 수 있기 때문임 원래는 변수로 선언해서 썼지만

```kotlin
fun bind(
            item: SleepNight,
        ) {
            // view의 resources를 레퍼런스로 참조함
            val res = itemView.context.resources

            // sleepLength에 text view를 설정함 time에 맞춰 format해서 변경경
            binding.sleepLength.text =
                convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, res)

            // quality를 변환해서 나온 값을 설정함
            binding.qualityString.text = convertNumericQualityToString(item.sleepQuality, res)

            // quality에 맞는 icon을 설정하게 함
            binding.qualityImage.setImageResource(when (item.sleepQuality) {
                0 -> R.drawable.ic_sleep_0
                1 -> R.drawable.ic_sleep_1
                2 -> R.drawable.ic_sleep_2
                3 -> R.drawable.ic_sleep_3
                4 -> R.drawable.ic_sleep_4
                5 -> R.drawable.ic_sleep_5
                else -> R.drawable.ic_sleep_active
            })
        }
```

### Create binding adapters
- 이전에 `Transformations`를 활용하여 `LiveData`에 대해서 TextView에 보여줄 string으로 변환했음

- 하지만 data 타입이 다르고 복잡한 타입을 처리할 땐 이러한 타입을 data binding 하는데 사용하는데 도울 binding adapter가 필요함

- binding adapter는 data를 가져와 data binding이 view에 bind하여 사용할 수 있게 adapt하는 adapters를 말함

- 이를 쓰기 위해서 item과 view를 가지는 메소드를 정의하고 `@BindingAdapter` 어노테이션을 사용할 것임

- 그리고 메소드 안에는 transformation을 구현할 것임, 이 binding adapter의 경우 data를 받을 view class의 extension function으로 만들 것임

- 우선 `BindingUtil` 클래스를 만듬, 그리고 static function을 만들 것임, 여기서 내부는 static function만으로 채워야함, class가 있으면 안됨(생성하고 이름만)

- 그 안의 로직은 먼저 생각해보면 아래와 같음
```kotlin
// view의 resources를 레퍼런스로 참조함
            val res = itemView.context.resources

            // sleepLength에 text view를 설정함 time에 맞춰 format해서 변경경
            binding.sleepLength.text =
                convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, res)
```

- `TextView`의 extension function인 `setSleepDurationFormatted`를 호출할 것임, 그리고 `SleepNight`를 넘겨줌, 이 함수는 sleep duration을 계산하고 formatting 하는 adapter가 될 것임, 위의 로직을 활용하기 위해 TextView에서 확장한 것

- 그리고 아래와 같이 위의 로직처럼 `text`를 변환해주고 set을 함

```kotlin
    // TextView의 text를 변환해서 설정함
    fun TextView.setSleepDurationFormatted(item: SleepNight) {
        text = convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, context.resources)
    }
```

- 그리고 이 binding adapter를 data binding에 알려주기 위해서 `@BindingAdapter` 어노테이션을 붙임

```kotlin
// TextView의 text를 변환해서 설정함
    @BindingAdapter("sleepDurationFormatted")
    fun TextView.setSleepDurationFormatted(item: SleepNight) {
        text = convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, context.resources)
    }
```

- 그 다음 `SleepNight` 객체의 값을 기반으로 한 sleep quality를 설정하는 adapter를 만듬, 똑같이 extenstion function으로 만듬

```kotlin
// sleepQuality를 포맷에 맞게 변환해서 설정하는 함수
    @BindingAdapter("sleepQualityString")
    fun TextView.setSleepQualityString(item: SleepNight) {
        text = convertNumericQualityToString(item.sleepQuality, context.resources)
    }
```

- 마지막으로 ImageView에 대한 binding adapter를 extension function으로 만듬

```kotlin
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
```

- 아래와 같이 `BindingUtils`를 만들긴 했지만 다 지우고 함수만 남겨둠

```kotlin
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
```

- 위와 같이 bindingadapter를 썼기 때문에 이제 아래의 기존의 `SleepNightAdapter`의 `bind` 함수를 지워도 됨

```kotlin
fun bind(
            item: SleepNight,
        ) {
            // view의 resources를 레퍼런스로 참조함
            val res = itemView.context.resources

            // sleepLength에 text view를 설정함 time에 맞춰 format해서 변경경
            binding.sleepLength.text =
                convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, res)

            // quality를 변환해서 나온 값을 설정함
            binding.qualityString.text = convertNumericQualityToString(item.sleepQuality, res)

            // quality에 맞는 icon을 설정하게 함
            binding.qualityImage.setImageResource(when (item.sleepQuality) {
                0 -> R.drawable.ic_sleep_0
                1 -> R.drawable.ic_sleep_1
                2 -> R.drawable.ic_sleep_2
                3 -> R.drawable.ic_sleep_3
                4 -> R.drawable.ic_sleep_4
                5 -> R.drawable.ic_sleep_5
                else -> R.drawable.ic_sleep_active
            })
        }
```

- 간단하게 binding에서 `item`을 할당하고 `binding.executePendingBindings()`를 호출함, 이 함수를 호출해서 data binding 요청시 최적화가 되게끔 함, 이 함수는 `RecyclerView`에서 binding adapter를 쓴다고 할 때 좋은 방법임, 속도를 높여줌

```kotlin
fun bind(
            item: SleepNight,
        ) {
            // sleep에 item을 할당함(db에서 불러온 값)
            binding.sleep = item
            // 최적화를 위해 호출
            binding.executePendingBindings()
        }
```

- 앞서 `list_item_sleep_night`에 대해서 data binding을 활용했는데 이제 binding adapter를 각각 넘겨주면 됨

- 여기서 앞에서 extension function으로 만들었기 때문에 이 설정에 대해서 xml 안에 `app:`로 해당 함수를 불러와서 설정할 수 있음

- 이러면 db에서 온 값을 바탕으로 알아서 처리함

```xml
app:sleepImage="@{sleep}"
app:sleepDurationFormatted="@{sleep}"
app:sleepQualityString="@{sleep}"
```