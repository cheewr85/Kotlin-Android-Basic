## RecyclerView
- 아래와 같이 Android에서 grid의 형태나 list로 데이터를 보여주는 것은 흔한 UI임

- 여기서 list의 경우 단순한 것부터 복잡한 것까지 다양함, 단순한 것은 데이터가 간단해서 표현하기 쉬울 수 있지만 복잡한 리스트의 경우 이 그리드 내부에 많은 세부 정보가 있을 수 있음

![one](/Android/img/twentynine.png)

- 이런 모든 케이스를 지원하기 위해서 Android에서 `RecyclerView`를 사용함

- 이 `RecyclerView`의 경우 큰 list에 효과적으로 작용함, `RecyclerView`의 경우 스크린에서 현재 보여지는 item들만 그리고 작업을 수행함

- 예를 들어 수천개의 요소들이 있더라도 오직 10개만 보여줌 스크린에 10개의 아이템을 그리는게 충분하기 때문에, 그리고 스크롤을 하게되면 `RecyclerView`가 스크린의 새로운 아이템이 무엇인지 찾음, 그리고 이 아이템을 보여주기 위한 충분한 작업을 함

- 아이템이 스크린에서 사라지면 아이템 View는 재활용됨 이는 item이 스크린에서 스크롤할때 새로운 컨텐츠로 채워진다는 것을 의미함

- `RecyclerView`의 이러한 행동은 시간 소모를 많이 세이브하고 scroll하는데 부드럽게 하게끔 도와줌

- item이 바뀌면 전체 list를 다시 그리지 않고 `RecyclerView`는 하나의 아이템만 업데이트 함, 이는 복잡한 아이템의 긴 리스트를 보여줄때 상당한 이점을 가져옴

- 이 과정을 도식화하면 아래와 같음 `ABC`로 채워진 View가 스크롤 되서 사라질 때 `RecyclerView`가 해당 View를 `XYZ` 데이터로 재사용함

![one](/Android/img/thirty.png)

### The adapter pattern
- Adapter를 사용해서 다른 나라의 전기 소켓을 사용하듯이 한 유형의 플러그를 다른 유형으로 변환하듯이 한 인터페이스를 다른 인터페이스로 변환함

- adapter pattern 역시 한 클래스의 API를 다른 API로 사용할 수 있음

- 앱이 데이터를 저장하고 처리하는 방식을 변경하지 않고 `RecyclerView`는 adapter를 사용하여 앱 데이터를 `RecyclerView`에서 표시할 수 있는 것으로 변환함

- 예시 앱에서도 `ViewModel`을 변경하지 않고 `RecyclerView`가 표시할 수 있는 무언가로 `Room` database에 있는 데이터를 adapt 하는 adapter를 만들 것임

### Implementing a RecyclerView

![one](/Android/img/thirtyone.png)

- `RecyclerView`에 data를 보여주기 위해서는 아래와 같이 따라야함

- 보여줄 데이터가 있어야함

- View의 container 역할을 할 `RecyclerView` 인스턴스를 layout file에 정의함

- 데이터의 하나의 아이템을 위한 layout을 만듬, 모든 list의 item은 똑같이 보이면 이 모두를 위해서 같은 레이아웃을 사용할 수 있음 하지만 필수는 아님, item layout은 fragment's layout으로부터 별도로 생성되어야 하므로 한 번에 하나의 아이템 뷰를 생성하고 데이터로 채울 수 있음

- layout manager는 View에서 UI 구성 요소의 layout을 처리함

- View holder는 `ViewHolder` 클래스를 상속받음, 이것은 item's layout에서 하나의 item을 보여주기 위한 view의 정보를 담고 있음, View Holder는 또한 `RecyclerView`가 스크린에서 view를 더 효율적으로 움직일 수 있게 사용하는 정보를 추가함

- Adapter는 데이터를 `RecyclerView`에 연결함, 데이터는 `ViewHolder`안에 보여질 수 있게 하기 위해서 adapt됨, `RecyclerView`는 adapter를 스크린의 데이터를 어떻게 표시하는지 방법을 알아내는데 사용함

-------

### Implement RecyclerView and an Adapter
- Jetpack에 있기 때문에 별도로 Implementation을 해서 `RecyclerView`를 추가하지 않아도 됨

- 그리고 기본 설정을 한 뒤 layoutmanager도 설정해줌

```xml
<androidx.recyclerview.widget.RecyclerView
            android:id="@+id/sleep_list"
            android:layout_width="0dp"
            android:layout_height="0dp"
            app:layout_constraintBottom_toTopOf="@id/clear_button"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toBottomOf="@id/stop_button"
            app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager"
            />
```

- `RecyclerView`는 단지 container에 불과함, 여기서 layout과 `RecyclerView`안에 보이는 item에 대한 infrastructure를 만듬

- 여기서 단순한 list 형태로 표현할 것임 그러기 위해서 ViewHolder가 필요한데 `TextItemViewHolder`를 쓸 것임, 이 때 데이터를 위한 `TextView`가 필요함

- 이를 layout 파일에서 item_view를 아래와 같이 만듬

```xml
<?xml version="1.0" encoding="utf-8"?>
<TextView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:textSize="24sp"
    android:paddingStart="16dp"
    android:paddingEnd="16dp"/>
```

- 그리고 `Util.kt`에 `TextItemViewHolder`클래스를 만드는 것을 정의함, view holder는 임시이고 나중에 교체될 것이므로 여기다가 정의하는 것
```kotlin
class TextItemViewHolder(val textView: TextView): RecyclerView.ViewHolder(textView)
```

- 가장 핵심은 `RecyclerView`를 구현하게 할 adapter를 만드는 것임

- 앞선 작업을 통해 item view를 위한 간단한 view holdfer와 각 item을 위한 layout을 가지고 있음, 이제 이 두 가지를 가지고 adapter를 만들어 view holder를 만들고 `RecyclerView`가 보여줄 데이터로 채움

- `sleeptracker` 패키지 안에 `SleepNightAdapter`라는 클래스를 만듬, 이때 이 클래스는 `RecyclerView.Adapter`를 상속받음 여기서 `RecyclerView`가 사용할 `SleepNight` 객체를 무언가로 adapt함

- adapter는 어떤 view holder를 쓰는지 알아야 함 그래서 `TextItemViewHolder`를 넣음

```kotlin
class SleepNightAdapter: RecyclerView.Adapter<TextItemViewHolder> {
    
}
```

- 그러면 필요한 함수를 구현하기 위한 필수 메소드를 써야함, 그리고 `SleepNight` 변수를 갖는 `listOf`를 먼저 선언함

```kotlin
var data =  listOf<SleepNight>()
```

- 그 다음 메소드를 오버라이딩 할 것인데, `getItemCount()`는 `data`에 있는 sleep nights의 list 사이즈를 반환함, `RecyclerView`는 `getItemCount()`를 호출하여 얼마나 많은 item을 adapter가 보여줘야 하는지 알아야 할 필요가 있음

```kotlin
override fun getItemCount() = data.size
```

- 그리고 `onBindViewHolder()` 함수를 오버라이딩함 `RecyclerView`에서 호출하는데 특정 위치의 list item 하나를 위한 data를 보여주기 위해서 호출함, 그래서 이 함수는 2가지 인자를 사용함

- view holder와 bind하기 위한 데이터의 position을 사용함, 이 앱에선 `TextItemViewHolder`와 list의 position을 불러와야함

- 그리고 그 안에 data 안에 하나의 아이템이 주어진 위치를 변수로 만듬, 그리고 이렇게 불러온 변수를 `ViewHolder`에 있는 `textView`에 numbers를 설정함

- 이 코드는 numbers의 list를 보여줌 하지만 이것은 adapter가 data를 view holder에 넣고 screen에 보여주는 방식임 

```kotlin
override fun onBindViewHolder(holder: TextItemViewHolder, position: Int) {
        // data에서 position의 값을 가져옴
        val item = data[position]
        // 해당 값을 holder에 textView에 보여줌 이것을 adapter의 역할로 볼 수 있음
        holder.textView.text = item.sleepQuality.toString()
    }
```

- 그 다음 `onCreateViewHolder`를 오버라이딩 함 `RecyclerView`가 view holder를 필요로 할 때 호출함

- 이 함수는 2개의 매개변수와 리턴 타입은 `ViewHolder`로 함, `parent` 매개변수는 view holder를 가지고 있는 view group으로 항상 `RecyclerView`임, `viewType` 매개변수는 같은 `RecyclerView`에 여러개의 viwe가 있을 때 사용함, 예를 들어 만약 text views, image, video의 리스트가 모든 같은 `RecyclerView`에 들어갈 때 `onCreateViewHolder()`함수는 어떤 타입의 viwe를 사용할 것인지 알아야 함

```kotlin
override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextItemViewHolder {
        
    }
```

- 그리고 그 안에 먼저 XML layout으로 부터 views를 어떻게 만들지 알기 위한 layout inflater를 선언함, `context`는 어떻게 view를 적절하게 inflate할지 정보를 가지고 있음, adapter에서 recycler viwe를 위해 항상 `RecyclerView`인 `parent` view group의 context를 넘겨줘야함

```kotlin
val layoutInflater = LayoutInflater.from(parent.context)
```

- 그리고 `layoutInflater`를 view에게 요청함으로써 inflate를 함, 이 때 view를 위한 XML layout과 view를 위한 `parent` view group을 넘김, 세번째로 boolean 값인데 `attachToRoot`인자임, 이 인자는 `false`여야함 왜냐하면 `RecyclerView`는 때가 되면 이 item을 view 계층구조에 추가하기 때문임

- 그리고 `TextItemViewHolder`를 리턴하게 함

```kotlin
// 해당 item layout을 inflate해서 나타냄
        val view = layoutInflater.inflate(R.layout.text_item_view, parent, false) as TextView

        // view로 만든 TextItemViewHolder를 리턴함
        return TextItemViewHolder(view)
```

- adapter는 `RecyclerView`의 데이터가 바뀔 때 알아야 함 왜냐하면 `RecyclerView`는 data에 대해서 아는 것이 없기 때문임, 오직 viwe holder와 adapter가 주는 것만 알고 있음

- data가 바뀔 때 이를 알리기 위해서 adapter의 최상단에 `data`의 새로운 값 `notifyDataSetChanged()`를 통해 새로운 데이터에 대한 list를 그리게끔 하는 함수를 호출함

```kotlin
var data = listOf<SleepNight>()
       set(value) {
           field = value
           notifyDataSetChanged()
       }
```

- 이제 `RecyclerView`는 view holders를 얻는 것을 사용하기 위해 adapter를 알아야 함

- `SleepTrackerFragment`에 `onCreateView()`에서 `ViewModel` 선언 이후에 아래와 같이 adapter를 만듬

```kotlin
// 앞서 만든 Adapter 생성
        val adapter = SleepNightAdapter()

        // RecyclerView에 adapter를 연결함
        binding.sleepList.adapter = adapter
```

- 이제 adapter로부터 데이터를 얻어 `RecyclerView`안에 넣기 위해서 `ViewModel`로부터 adapter로 데이터를 얻게할 필요가 있음

- 여기서 해당 값을 가져오기 위해서 기존의 `nights` 변수에 있는 `private`을 제거함

- 그 다음 `SleepDatabaseDao`에서 `getAllNights`가 `SleepNight` list르 반환하여 `LiveData`로 씀을 알 수 있음

- 이제 `SleepTrackerFragment`에서 `nights` 변수에 대해서 observer를 만듬, 이렇게 함으로써 `RecyclerView`가 보일 때 observerr가 작동되게 함, 그리고 이제 이 값을 adpater에 할당해줌

```kotlin
// nights 데이터를 adapter에 넣어줌
        sleepTrackerViewModel.nights.observe(viewLifecycleOwner, Observer{
            it?.let {
                adapter.data = it
            }
        })
```

- `RecyclerView`가 view holders를 재활용한다는 것은 그것을 재사용한다는 것을 의미함 즉 view를 scroll하면 view를 재사용한다는 것임

- 그래서 `onBindViewHolder()`에서 이전 item에 대해선 view holder에서 set을 하고 reset을 할 때 어떤 커스터마이징도 할 수 있음

- 아래와 같이 `onBindViewHolder()`안에 조건문을 통해 조건을 설정할 수 있음

```kotlin
override fun onBindViewHolder(holder: TextItemViewHolder, position: Int) {
        // data에서 position의 값을 가져옴
        val item = data[position]
        // 해당 값을 holder에 textView에 보여줌 이것을 adapter의 역할로 볼 수 있음
        holder.textView.text = item.sleepQuality.toString()
        
        // textView를 조건에 따라 색깔을 설정함
        if (item.sleepQuality <= 1) {
            holder.textView.setTextColor(Color.RED)
        } else {
            // reset
            holder.textView.setTextColor(Color.BLACK)
        }
    }
```

### Create a ViewHolder for all the sleep data
```kotlin
class TextItemViewHolder(val textView: TextView): RecyclerView.ViewHolder(textView)
```

- 위의 코드 하나가 상당히 많은 기능을 제공함, `ViewHolder`가 item view과 `RecyclerView`안에 위치하는 것에 대한 metadata를 묘사함

- `RecyclerView`는 list를 스크롤할 때 view에 정확한 위치와 item이 `Adapter`에서 추가되거나 삭제될 때 view를 애니메이션 하는 것등은 이 기능에 의존함

- 만약 `RecyclerView`가 `ViewHolder`에 저장된 view에 접근할 때 view holder의 `itemView` 요소를 사용할 수 있음

- `RecyclerView`는 스크린에 binding된 item을 보여줄 때 border와 같은 view 주변의 꾸미기를 그릴 때 그리고 접근성을 구현할 때 `itemView`를 사용함

- 여기서 sleep quality를 만들기 위해 새로운 layout을 만듬

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools">
    
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
        tools:srcCompat="@drawable/ic_sleep_5"/>
    
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
        tools:text="Wednesday"/>
    
    <TextView
        android:id="@+id/quality_string"
        android:layout_width="0dp"
        android:layout_height="20dp"
        android:layout_marginTop="8dp"
        app:layout_constraintEnd_toEndOf="@id/sleep_length"
        app:layout_constraintStart_toStartOf="@id/sleep_length"
        app:layout_constraintTop_toTopOf="@id/sleep_length"
        app:layout_constraintHorizontal_bias="0.0"
        tools:text="Excellent!!!"/>

</androidx.constraintlayout.widget.ConstraintLayout>
```

- 여기서 `SleepNightAdapter`에서 `ViewHolder`를 만듬

```kotlin
class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){}
```

- 여기서 `ViewHolder`를 업데이트하고 매번 bind하기 위해서 각각의 view의 레퍼런스를 참조함(추후 data binding으로 변환 가능)

```kotlin
val sleepLength: TextView = itemView.findViewById(R.id.sleep_length)
val quality: TextView = itemView.findViewById(R.id.quality_string)
val qualityImage: ImageView = itemView.findViewById(R.id.quality_image)
```

- 그리고 이전에는 이 Adapter에서 `TextItemViewHolder`만 썼는데 이를 `ViewHolder`로 바꿈, 그리고 layout resource 역시 바꾸고 TextView casting도 삭제하고 `ViewHolder`로 리턴도 바꿈

```kotlin
class SleepNightAdapter: RecyclerView.Adapter<SleepNightAdapter.ViewHolder>() {
```

- 그에 맞춰서 `onCreateViewHolder` 수정함
```kotlin
    override fun onCreateViewHolder(
            parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = 
            LayoutInflater.from(parent.context)
        val view = layoutInflater
                .inflate(R.layout.list_item_sleep_night, 
                         parent, false)
        return ViewHolder(view)
    }
```

- 그리고 `onBindViewHolder`도 수정함, item을 제외하고 다 지우고 싹 다 수정함, `convertDurationToFormatted`의 경우 `Util.kt`에 있는 함수 활용함

```kotlin
override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // data에서 position의 값을 가져옴
        val item = data[position]

        // view의 resources를 레퍼런스로 참조함
        val res = holder.itemView.context.resources

        // sleepLength에 text view를 설정함 time에 맞춰 format해서 변경경
        holder.sleepLength.text = convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, res)
        
        // quality를 변환해서 나온 값을 설정함
        holder.quality.text = convertNumericQualityToString(item.sleepQuality, res)
        
        // quality에 맞는 icon을 설정하게 함
        holder.qualityImage.setImageResource(when (item.sleepQuality) {
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

### Improve your code
- `onBindViewHolder`를 개선할 수 있음 설정하는 코드에 대해서 Function으로 추출 가능함

- 그리고 이렇게 추출한 Function을 `ViewHolder` 내부 클래스에 넣어둠, 그리고 이 holder를 바탕으로 bind 함수만 쓰게끔 아래와 같이 처리함

```kotlin
// image, textview가 있는 해당 itemView에 대한 ViewHolder를 정의함
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
    }
```
```kotlin
override fun onBindViewHolder(holder: ViewHolder, position: Int) {
   val item = data[position]
   holder.bind(item)
}
```

- 마지막으로 `onCreateViewHolder`를 개선함

- 모든 것이 adapter가 아닌 `ViewHolder`에서 일어나므로 이에 맞춰서 개선할 수 있음

- `onCreateViewHolder`를 모두 Function으로 extract한 뒤 companion object로 만들고 이를 `ViewHolder` 클래스 안에 옮기면 됨(함수를 public으로 해야함)

```kotlin
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
```

```kotlin
override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }
```

- 최종적으로 `ViewHolder`를 private에 constructor로 만듬, `from()`이 `ViewHolder` 이외에 호출할 이유가 없기 때문에 이 인스턴스 생성을 public으로 할 필요가 없음

```kotlin
class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
```