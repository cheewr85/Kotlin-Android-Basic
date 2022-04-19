### Inspect the starter code
- `activity_main`에서 `nav_host_fragment`와 함께 `merge` 태그가 있음 이 `merge` 태그는 레이아웃 포함시 불필요한 레이아웃을 제거하는데 유용하게 쓸 수 있음

- 예를 들어서 `ConstraintLayout > LinearLayout > TextView`로 되어 있다면 이 `merge` 태그는 `LinearLayout`을 삭제할 것임

- 이러한 방식의 최적화는 View를 계층적으로 단순하게 하고 앱의 성능을 향상시킬 수 있음

### Add a ViewModel
- 앞서 database를 만들어 이 database를 활용하여 data를 저장하고 추가하고 모으고 보여질 수 있게함, 이 모든 작업을 이제 ViewModel에서 처리할 것임

- 이 ViewModel에선 button click과 DAO를 통해 database와 상호작용하고 `LiveData`를 통해서 UI에 data를 제공할 것임

- 그리고 모든 database 작업은 main UI thread에서 작업을 하면 안되기 때문에 이 때 coroutines을 쓸 것임

- 우선 해당 ViewModel에선 생성자 매개변수로 application context를 받고 이 것을 속성으로 활용함

```kotlin
class SleepTrackerViewModel(
       val database: SleepDatabaseDao,
       application: Application) : AndroidViewModel(application) {
}
```

- 그리고 SleepTrackerViewModelFactory의 경우 ViewModel과 같은 argument를 가지고 `ViewModelProvider.Factory`를 상속받음

- `create()`는 argument로 어떤 클래스 타입을 받게 두고 `ViewModel`을 반환함

- `create()`안에는 `SleepTrackerViewModel`이 이용 가능한지 확인하고 가능하면 해당 인스턴스를 반환함, 반면에 그렇지 않으면 예외 처리를 함

```kotlin
class SleepTrackerViewModelFactory(
       private val dataSource: SleepDatabaseDao,
       private val application: Application) : ViewModelProvider.Factory {
   @Suppress("unchecked_cast")
   override fun <T : ViewModel?> create(modelClass: Class<T>): T {
       if (modelClass.isAssignableFrom(SleepTrackerViewModel::class.java)) {
           return SleepTrackerViewModel(dataSource, application) as T
       }
       throw IllegalArgumentException("Unknown ViewModel class")
   }
}
```

- ViewModel을 만들었다면, 이제 Fragment에 적용시키면 됨

- 먼저 application context의 참조 값을 가져와서 view-model factory provider에 줘야함(이 application context는 해당 fragment가 attach 될 때의 reference를 주는 것임)

- 그리고 해당 reference를 DAO에 넘겨줘야함, 그래야 database의 DAO reference를 얻을 수 있음

```kotlin
class SleepTrackerFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_tracker, container, false)

        // application context를 가져옴
        val application = requireNotNull(this.activity).application
        
        // DAO를 얻어와서 인스턴스를 만들기 위해서 해당 Fragment의 context 넘김
        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao
        

        return binding.root
    }
}
```

- 그리고 ViewModel을 만들면 됨

```kotlin
// viewModel을 만들기 위해 ViewModelFactory 인스턴스를 만듬(앞서 얻은 database와 context를 넘겨줌)
        val viewModelFactory = SleepTrackerViewModelFactory(dataSource, application)
        
        // Factory가 있으므로 이를 가지고 ViewModel을 만듬
        val sleepTrackerVIewModel = ViewModelProvider(this, viewModelFactory).get(SleepTrackerViewModel::class.java)
```

- 그리고 data-binding을 통해 Fragment와 ViewModel을 연결시켜줌

```xml
<data>
   <variable
       name="sleepTrackerViewModel"
       type="com.example.android.trackmysleepquality.sleeptracker.SleepTrackerViewModel" />
</data>
```

- 그런 다음 lifecycle owner를 연결시켜줌, 그리고 ViewModel도 할당해주면 됨

```kotlin
binding.setLifecycleOwner(this)
binding.sleepTrackerViewModel = sleepTrackerViewModel
```

----------

## Coroutines
- 작업을 계속하기 전에 작업을 완료될 때까지 기다려야 하는 경우 응용 프로그램이 정상적으로 실행되지 않을수도 있음

- 예를 들어 큰 파일을 읽거나 긴 데이터베이스를 호출하거나 하는 작업은 전체 앱의 실행을 막거나 금지하는 작업이 될 수 있음

- 이것은 사용자에게 애플리케이션 응답성을 감소시킬 뿐 아니라 하드웨어적으로 효율적인 사용이 아님

- main thread를 막지 않고 긴 작업을 수행하는 하나의 패턴은 callbacks을 활용하는 것임, 하지만 이것은 몇몇 불이익이 존재함, 콜백을 많이 사용하면 코드는 읽기 어렵고 추론하기가 더 여뤄지고 코드는 순차적으로 보여도 콜백 코드는 나중에 비동기 시간에 실행되는 단점이 존재함([기본지침서](https://developer.android.com/courses/extras/multithreading?hl=ko))

- 코틀린에서는 콜백을 대신하여 이러한 오랜 시간이 걸리는 작업을 효율적이고 깔끔하게 처리하는 방법으로 **Coroutines**을 사용함

- 이 `Coroutine`을 사용하면 콜백 기반의 코드를 순차코드로 바꿀 수 있음, 이런 순차기반 코드는 일반적으로 읽고 유지하기 쉬움

- 콜백과 다르게 `Coroutine`은 예외 같은 중요한 언어 기능을 안전하게 쓸 수 있음

- 가장 중요한 것은 `Coroutine`은 높은 수준의 유지보수성과 유연성을 가지고 있음

- 마지막으로 `Coroutine`과 콜백은 같은 기능을 수행함, 둘 다 앱 내에서 잠재적으로 오래 실행되는 비동기 작업을 처리하는데 사용됨

- `Coroutine`은 2가지 속성이 있음 하나는 비동기적이며 non-blocking이고 suspend function을 사용해서 비동기적인 코드를 순차 코드로 바꿈

### Coroutines are asynchronous
- `Coroutine`은 프로그램의 주요 실행 단계와 독립적으로 실행됨, 이것은 병렬적으로 진행되거나 혹은 별도의 프로세스에서 진행될 수 있음 

- 또한 앱의 나머지 부분이 입력을 기다리는 동안 약간의 처리를 몰래 할 수 있음

- 비동기 프로그래밍의 중요한 측면 중의 하나는 당신이 명시적으로 그것을 기다리는 동안에 이 결과가 즉각적으로 이용 가능하다고 기대할 수 없다는 것이 있음

- 예를 들어 조사가 필요한 질문이 있어서 그리고 동료에게 정중하게 그 질문의 요청을 부탁함, 여기서 당신의 동료는 그때부터 스스로 작업을 시작할 것임

- 그리고 당신의 동료가 답변을 가져올 때까지 당신은 답변과 상관없는 다른 일을 진행할 수 있음, 이러한 예에서 동료는 별도의 스레드에서 비동기적으로 작업을 하는 것임

### Coroutines are non-blocking
- `Non-blocking`의 의미는 `Coroutine`은 main이나 UI Thread의 진행을 막거나 간섭하지 않는다는 것을 의미함

- 따라서 `Coroutine`을 사용하면, Main thread에서 실행되는 UI 상호작용이 항상 우선순위를 가지기 때문에 사용자는 가능한 한 가장 부드러운 경험을 할 수 있음

### Coroutines use suspend functions to make asynchronous code sequential
- `suspend` 키워드는 Kotlin에서 `Coroutine`을 사용 가능하게 만드는 함수 타입 혹은 함수에 쓰는 방법임

- `Coroutine` 이 `suspend`가 붙어있는 함수를 호출하면 일반적인 함수를 호출할 때 함수 반환이 올 때까지 막는 대신에 `Coroutine`은 결과가 준비될 때까지 실행을 일시중지함(suspend함)

- 그런 다음 `Coroutine`은 중단된 위치에서 결과와 함께 재개함

- `Coroutine`이 일시 중단되고 결과를 기다리는 동안, 실행 중인 스레드의 차단을 해제함, 그렇게 하면 다른 함수나 `Coroutine`을 실행할 수 있음 

- `suspend`는 코드가 실행되는 thread를 특정짓지 않음, `suspend` 함수는 background thread 혹은 main thread 위에서 실행이 됨

- `blocking`과 `suspending`의 차이는 thread가 block되면 작업이 발생하지 없고 thread가 suspend되면 결과를 사용할 수 있을 때까지 다른 작업이 수행됨

![one](/Android/img/twentyeight.png)

### Coroutine Three things

#### Job
- Job은 기본적으로 취소 할 수 있는 모든 것을 의미함, 모든 `Coroutine`은 Job이 있고 `Coroutine`을 취소할 수 있는 Job을 사용할 수 있음

- Jobs은 부모-자식 위계구조로 배치될 수 있음, 부모 Job을 취소하는 즉시 모든 자식 Job이 취소됨, 이는 일일이 직접 `Coroutine`을 취소하는 것보다 훨씬 더 편함

#### Dispatcher
- Dispatcher는 다양한 threads에서 실행되도록 `Coroutine`을 보냄

- 예를 들어 `Dispatchers.Main`은 task를 main thread에서 실행하고 `Dispatchers.IO`는 blocking I/O task를 thread의 공유 pool로 보냄

#### Scope
- `Coroutine's scope`는 `Coroutine`이 실행되는 context를 정의함

- `scope`는 `Coroutine` Job과 Dispatchers에 대한 정보가 결합되어 있음

- Scope는 `Coroutine`을 추적함, `Coroutine`을 시작하면 Scope에 있는 상태임, 이는 `Coroutine`을 추적할 범위를 지정함을 의미함

### Kotlin Coroutines with Architecture components
- `CoroutineScope`는 모든 `Coroutine`을 추적하고 `Coroutine`이 실행되어야 하는 시기를 관리하는데 도움이 됨

- 또한 그 안에서 시작된 모든 `Coroutine`을 취소할 수 있음

- 각각의 비동기 실행과 `Coroutine`은 특정 `CoroutineScope` 안에서 실행됨

- Architecture components는 앱의 논리적 범위에 대한 `Coroutine`의 최고 수준의 지원을 제공함

- Architecture components는 앱에서 사용할 수 있는 아래와 같은 built-in scope를 제공함

- 이 built-in coroutine scope는 각각 상응하는 Architecture components에 대한 KTX extensions에 있음

- 아래의 Scope를 쓰는데 있어서 적절한 dependencies를 추가해줘야 함

    - `ViewModelScope`

    - `LifecycleScope`

    - `liveData`

- `ViewModelScope`는 앱의 각각 `ViewModel`에 대해서 정의할 수 있음, 이 scope에서 실행되는 어떠한 `Coroutine`이던간에 만약 `ViewModel`이 사라지면 자동으로 취소가 됨

- 해당 예시앱에선 database 작업을 시작하는데 `ViewModelScope`를 쓸 것임

### Room and Dispatcher
- database 작업을 수행하기 위해 Room 라이브러리를 사용할 때 Room은 background thread에서 database 작업을 수행하기 위해 `Dispatchers.IO`를 사용함

- 굳이 특정 `Dispatchers`를 명시해줄 필요없음, Room이 알아서 해 줌

----------

### Collect and display the data
- 각각의 요구사항은 아래와 같음 Start 버튼을 누르면 app은 새로운 sleep night를 생성하고 데이터베이스에 sleep night를 저장함

- Stop 버튼을 누르면 app은 마지막에서의 night를 업데이트 함

- Clear 버튼을 누르면 app은 데이터베이스에 data를 삭제함

- `Coroutine` 사용을 위해서 `suspend` 키워드를 `Dao`의 추가함, 여기서 `getAllNights`의 경우 `LiveData`를 리턴하기 때문에 Room이 이미 background thread를 쓰기 때문에 `suspend`는 쓸 필요없음

```kotlin
// DataBase에 접근해서 처리할 Dao 인터페이스 정의
@Dao
interface SleepDatabaseDao {
    // 데이터 베이스 삽입하기 위한 어노테이션 및 함수
    @Insert
    suspend fun insert(night: SleepNight)

    // 데이터 베이스 업데이트 하기 위한 어노테이션 및 함수
    @Update
    suspend fun update(night: SleepNight)

    // daily_sleep_quality_table에서 nightId가 key와 매칭되는 모든 column을 선택하는 함수
    @Query("SELECT * from daily_sleep_quality_table WHERE nightId = :key")
    suspend fun get(key: Long): SleepNight?

    // 테이블의 모든 항목을 지우는 쿼리문을 쓴 함수 (@Delete는 하나의 아이템만 지우므로)
    @Query("DELETE FROM daily_sleep_quality_table")
    suspend fun clear()

    // nightId 기준으로 내림차순으로 정렬된 테이블에서 하나의 아이템만 선택해서 가져오는 쿼리문을 적용한 함수
    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC LIMIT 1")
    suspend fun getTonight(): SleepNight?

    // 내림차순으로 정렬된 테이블의 모든 column을 반환하게함
    // LiveData를 적용시켰기 때문에 Room은 LiveData를 유지함(명시적으로 한 번만 가져와도)
    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC")
    fun getAllNights(): LiveData<List<SleepNight>>
}

```

- 그리고 `ViewModel`에서 `tonight`의 변수를 `MutableLiveData`로 만듬, data를 observe하고 바꿀 것이므로

- 그리고 `init` block에서 `viewModelScope.launch`를 사용, `ViewModelScope`에서 `Coroutine`을 시작하게 사용함

- 이 `scope` 안에서 `getTonightFromDatabase()`를 호출함으로써 database에서 `tonight` 변수 값을 가져오고 `tonight.value`에 할당함

```kotlin
// SleepNight 담을 LiveData 선언
        private var tonight = MutableLiveData<SleepNight?>()

        init {
                initializeTonight()
        }
        
        private fun initializeTonight() {
                viewModelScope.launch { 
                        tonight.value = getTonightFromDatabase()
                }
        }
```

- 그리고 `getTonightFromDatabase` 함수를 구현함, `private suspend` 함수로 `SleepNight`을 반환하는 함수를 정의함(nullable하게 정함, 현재 `SleepNight` 값이 없을 수 있으므로)

- 그 내부에는 database에서 `tonight`변수를 가져옴, start와 end time이 다른 것은 이미 night이 끝난 것이므로 `null`을 반환하고 그렇지 않다면 night을 반환함

```kotlin
private suspend fun getTonightFromDatabase(): SleepNight? {
                var night = database.getTonight()
                if (night?.endTimeMilli != night?.startTimeMilli) {
                        night = null
                }
                return night
        }
```

- 그리고 Start button에 대한 click handler를 추가할 것임

- 아래와 같이 `onStartTracking()` 함수를 만들어서 그 안에 `viewModelScope` 활용, `Coroutine`을 실행하여 `SleepNight`를 생성해서 `insert()`를 통해 database에 넣는 것을 정의함

```kotlin
fun onStartTracking() {
                viewModelScope.launch { 
                        val newNight = SleepNight()
                        // SleepNight만든 것을 insert함, 이 함수는 private suspend로 Dao의 함수가 아님
                        insert(newNight)
                        // tonight을 업데이트 함
                        tonight.value = getTonightFromDatabase()
                }
        }
        
        // DAO의 insert를 suspend로 Coroutine을 활용해서 처리하는 함수
        private suspend fun insert(night: SleepNight) {
                database.insert(night)
        }
```

- 그리고 xml 상에서 `onClick`을 아래와 같이 추가함

```xml
android:onClick="@{() -> sleepTrackerViewModel.onStartTracking()}"
```

- 여기서 하나 짚고 넘어갈 부분은 `Coroutine`은 기본적으로 main UI thread에서 실행됨, 왜냐하면 `Coroutine`의 결과가 UI의 나타내는 것에 영향을 끼치기 때문임

- `ViewModel`의 `CoroutineScope`는 `viewModelScope`를 통해서 접근할 수 있음, 아래와 같이

- 여기서 오랜 시간이 걸리는 작업을 하기 위해 suspend function을 호출함, 결과를 기다리는데 UI thread를 막을 필요가 없음

- 오랜 시간이 걸리는 작업의 결과는 UI의 영향을 줌 하지만 UI로부터 독립된 작업을 함, 효율을 위해서 I/O dispatchers로 변환함(Room은 자동으로 변환하여 처리함)

- I/O dispatchers는 최적화를 위해 thread pool을 사용하고 이러한 작업을 위해 설정을 함, 그러면 시간이 오래걸리는 작업에 대해 호출을 함

- 직접적인 비교를 보면 아래와 같음

```kotlin
// without room

fun someWorkNeedsToBeDone {
   viewModelScope.launch {
        suspendFunction()
   }
}

suspend fun suspendFunction() {
   withContext(Dispatchers.IO) {
       longrunningWork()
   }
}
```

```kotlin
// Using Room
fun someWorkNeedsToBeDone {
   viewModelScope.launch {
        suspendDAOFunction()
   }
}

suspend fun suspendDAOFunction() {
   // No need to specify the Dispatcher, Room uses Dispatchers.IO.
   longrunningDatabaseWork()
}
```

- 여기서 `nights` 변수는 `LiveData`를 참조함, 여기서 `Room`이 database의 변화가 있을 때마다 `LiveData` `nights`가 최신 data로 업데이트 되어 있음

- 굳이 추가로 명시적으로 `LiveData`를 보여주거나 업데이트 할 필요가 없음, `Room`이 database와 매칭이 되는 data를 업데이트함 

- 만약, `nights`를 text view에 보여주게 하기 위해서 객체 참조를 보여줄 것임, 객체의 content를 보여주기 위해서 data를 형식이 있는 string으로 바꿀 것임

- `Transformation` map을 사용하여 database로부터 `nights`가 매번 실행될 때마다 새로운 data를 받게함

- 미리 정의된 함수를 활용해서 HTML-form의 string으로 반환하는 함수를 씀

- 이를 활용하기 위해서 먼저 `SleepTrackerViewModel`에서 `nights`변수를 정의하고 database로부터 nights를 가져와 `nights` 변수에 할당함

```kotlin
// nights 값을 가져옴(DB에 접근해서)
        private val nights = database.getAllNights()

        // nights를 nightsString으로 변환을 함(Util.kt의 함수를 씀)
        val nightsString = Transformations.map(nights) {
                nights -> formatNights(nights, application.resources)
        }
```

- 그리고 해당 부분을 xml에서 연결시킴

```xml
android:text="@{sleepTrackerViewModel.nightsString}"
```

- 그리고 Stop 버튼에 대해서도 click handler를 추가함

- 동일하게 `Coroutine`을 활용, end time 설정을 해주고 `update()`를 호출함, 여기서 `return@label`은 여러 중첩 함수 중에서 이 문이 반환하는 함수를 지정함

```kotlin
// Tracking을 멈추는 함수
        fun onStopTracking() {
                viewModelScope.launch { 
                        val oldNight = tonight.value ?: return@launch
                        oldNight.endTimeMilli = System.currentTimeMillis()
                        update(oldNight)
                }
        }
        
        // insert 함수와 동일하게 database를 활용하여 update하는 함수
        private suspend fun update(night: SleepNight) {
                database.update(night)
        }
```

- 그리고 xml 상에서 설정을 추가해줌

```xml
android:onClick="@{() -> sleepTrackerViewModel.onStopTracking()}"
```

- 마지막으로 Clear button에 대해서 설정을 함, 위의 작업과 유사함

```kotlin
// claer하는 함수
        fun onClear() {
                viewModelScope.launch { 
                        clear()
                        tonight.value = null
                }
        }
        
        // database에서 clear 처리함
        suspend fun clear() {
                database.clear()
        }
```
```xml
android:onClick="@{() -> sleepTrackerViewModel.onClear()}"
```