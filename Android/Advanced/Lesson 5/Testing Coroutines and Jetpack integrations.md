- 앞서 한 test와 별개로 이번에는 coroutine의 scope된 viewmodel과 coroutine을 그리고 Room, Data Binding, End-to-End test를 진행할 것임

### Introduction to and review of testing coroutines
- 코드는 동기적, 비동기적으로 실행이 가능함, 아래와 같이 동기적으로 실행한다면 다음 task로 가기 위해선 이전 task가 확실하게 끝나야하고 비동기적으로 실행되면 병렬적으로 실행이 가능함

![one](/Android/img/seventysix.png)

- 비동기 코드는 거의 항상 network나 database 호출등 시간이 오래걸리는 task에 사용됐음, 그래서 test하기 어려움이 있음

- 이는 2가지 이유가 있는데 먼저 비동기 코드는 결정이 되어 있지 않음 즉, 만약 A, B에 대해서 병렬적으로 그리고 여러번 test를 하고 있는데 어쩔 땐 A가 먼저 끝나고 어쩔 땐 B가 먼저 끝나는 등 이는 일관되지 않은 결과를 낼 수 있음

![one](/Android/img/seventyseven.png)

- test를 하기 위해서, 비동기 코드를 위해서 동기적 메커니증의 분류를 확실히 해야할 때가 있음, test는 testing thread에서 실행이 됨

- test가 서로 다른 thread나 새로운 coroutines에서 코드를 실행한다면 이 작업은 test thread에서 분리된 비동기적으로 시작이 됨

- 반면에 test coroutine은 병렬적으로 수행을 할 것임, 이렇게 된다면 먼저 시작된 task가 끝나기도 전에 test가 끝날 수 있음

![one](/Android/img/seventyeight.png)

- 그래서 Synchronization mechanisms은 test 실행에 대해서 비동기 작업이 끝날때까지 wait을 하게 할 수 있음

![one](/Android/img/seventynine.png)

- 코틀린에서는 코드를 비동기적으로 실행시키는 일반적인 매커니즘은 coroutine임, 비동기 코드를 테스트 할 때, 코드를 확실히 처리하고 동기적 기법을 제공해줘야함, 아래의 방식으로 도움을 받을 수 있음

   - `runBlockingTest`나 `runBlocking`을 사용함

   - local test를 위해 `TestCoroutineDispatcher`를 사용함

   - 시간안에 정확한 곳에 코드의 상태를 테스트 하기위해 coroutine 실행을 정지시킴

- `suspend` 함수를 포함한 코드를 test하기 위해서는 이전에 봤던것과 같이 의존성을 추가해 test class나 함수에 `@ExperimentalCoroutinesApi`를 어노테이션으로 쓰고 `runBlockingTest`에 코드를 사용함(그러면 coroutine이 끝날 때까지 test가 기다림)

- 아래의 코드를 볼 수 있음, `kotlinx-coroutines-test`의 어떠한 함수를 쓰기 위해서는 `@ExperimentalCoroutinesApi`를 써야함

- `runBlockingTest`는 코드가 확실하게 실행되어 처리되게 하는것과 동시에 동기적 기법을 제공함, 아래의 경우에도 모든 coroutine이 시작하고 끝날때까지 test thread를 막음, 또한 coroutine들을 호출된 순으로 동시에 실행시킴

- 그래서 해당 블럭이 coroutine이 test code를 위한 특정 coroutine context를 제공해줌으로써 마치 non-coroutine처럼 실행되게 만듬(이는 앞서 본대로 동기적 결정적으로 한 번에 동일하게 코드를 실행해야하는 입장에서 중요함)

```kotlin
@MediumTest
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi // LOOK HERE
class TaskDetailFragmentTest {

    //... Setup and teardown

    @Test
    fun activeTaskDetails_DisplayedInUi() = runBlockingTest{ // LOOK HERE
        // GIVEN - Add active (incomplete) task to the DB.
        val activeTask = Task("Active Task", "AndroidX Rocks", false)
        repository.saveTask(activeTask) // LOOK HERE Example of calling a suspend function

        // WHEN - Details fragment launched to display task.
        val bundle = TaskDetailFragmentArgs(activeTask.id).toBundle()
        launchFragmentInContainer<TaskDetailFragment>(bundle, R.style.AppTheme)

        // THEN - Task details are displayed on the screen.
        // Make sure that the title/description are both shown and correct.
        // Lots of Espresso code...
    }

    // More tests...
}
```

- 아래의 경우에는 `runBlocking`을 사용함, 이 때는 `ExperimentalCoroutinesApi`을 쓸 필요가 없어짐, `runBlockingTest`와 유사하게 suspend 함수에 사용이 됨

```kotlin
class FakeTestRepository : TasksRepository {

    var tasksServiceData: LinkedHashMap<String, Task> = LinkedHashMap()

    private val observableTasks = MutableLiveData<Result<List<Task>>>()


    // More code...

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        runBlocking { refreshTasks() } // LOOK HERE
        return observableTasks
    }

    override suspend fun refreshTasks() {
        observableTasks.value = getTasks()
    }
    
    // More code...
}
```

- `runBlocking`그리고 `runBlockingTest`둘 다 현재 스레드를 막고 lambda 안에서 코루틴과 연관된것이 실행되어 완료될때까지 기다림

- `runBlockingTest`는 test를 위해서 추가적인 사항이 있음

   - test를 더 빨리 하기 위해서 `delay`를 스킵함

   - coroutine 끝에 testing과 연관된 assertions을 추가함, 이 assertions은 coroutine에서 실행하면 실패할 것이고 `runBlocking`람다 끝 이후에 실행을 하거나 잡히지 않은 예외가 있다면 계속 진행될 것임

   - coroutine 시행을 넘어서는 타이밍 control을 줌

- 그럼 `runBlocking`을 왜 test double에 쓰는지 보면 이따금씩 test double을 위해서 coroutine을 써야할 때가 있는데 이 때는 실제로 현재 thread를 막아야 할 필요가 있기 때문임

- 그래서 test case에서 tset double이 사용될 때, thread는 block을 하고 coroutine이 test를 하기전에 끝나게 해줌

- 따라서 test double은 실제 test case를 정의하는 것이고 그래서 `runBlockingTest`로 쓸 필요가 없음

### Coroutines and ViewModels
- 모든 coroutine은 `CoroutineScope`를 필요로함, Coroutine scope는 coroutine의 생명주기를 관리함, 만약 scope를 취소하면 scope에서 실행되는 모든 coroutine은 취소됨

- view model에서 시간이 오래걸리는 작업을 할 수 있기 때문에, view model 안에서 coroutine을 만들고 실행할 것임

- 보통 어떤 coroutine을 실행하기 위해 각각의 view model을 위해 직접 새로운 `CoroutineScope`를 설정하고 만들 필요가 있음, 이는 매우 많은 보일러 플레이트 코드를 생성할 것임

- 이를 피하기 위해서 `viewModelScope()` 확장함수를 사용함

- `viewModelScope()`는 각각 view model에 연관된 `CoroutineScope`를 리턴함, 이는 `viewModelScope`가 특정 view model을 위해 사용되도록 설정되어서 그런 것임 

   - `viewModelScope`는 view model이 clean up됙고 scope가 취소됐을 때 같이 view model에 묶여있음, 이는 view model이 사라지게 되면 모든 관련된 coroutine 작업이 같이 있다는 것을 의미함, 이는 불필요한 작업과 메모리 누수를 막아줌

   - `viewModelScope`는 `Dispatchers.Main`이라는 coroutine dispatcher를 사용함, `CoroutineDispatcher`는 어떤 thread가 coroutine code를 실행하는지를 포함하여 어떻게 coroutine이 실행하는지 관리함, `Dispathcer.Main`은 coroutine을 UI나 main thread에 넣음, 이는 `ViewModel` coroutine이 기본적으로 종종 view model이 UI를 조정하는 경우가 있기 때문에 적절한 작업임

- 이는 production code에서는 잘 작동하지만 local test에선 `Dispatcher.Main` 사용은 문제를 일으킴

- `Dispatchers.Main`은 Android의 `Looper.getMainLooper`를 사용함, main looper는 실제 앱에서 loop을 실행함, main looper는 local test를 위해 사용할 수 없음, 왜냐하면 full application을 실행하지 않기 때문에

- 그래서 이를 처리하기 위해 `setMain`메소를 사용해, `TestCoroutineDispatcher`로 조정하게 함(해당 dispatcher는 test를 위한 것으로만 씀)

- 아래의 새로운 test 메소드를 추가함, 그러면 아마 실패를 할 것인데, 이 에러는 `Dispatcher.Main`의 초기화를 실패해서 나타나는 것임, 이는 앞서 말했듯이 `Looper.getMainLooper`에 문제로써 `Dispatcher.setMain`을 사용함을 의미함

```kotlin
@Test
fun completeTask_dataAndSnackbarUpdated() {
    // Create an active task and add it to the repository.
    val task = Task("Title", "Description")
    tasksRepository.addTasks(task)

    // Mark the task as complete task.
    tasksViewModel.completeTask(task, true)

    // Verify the task is completed.
   assertThat(tasksRepository.tasksServiceData[task.id]?.isCompleted, `is`(true))

    // Assert that the snackbar has been updated with the correct text.
    val snackbarText: Event<Int> =  tasksViewModel.snackbarText.getOrAwaitValue()
    assertThat(snackbarText.getContentIfNotHandled(), `is`(R.string.task_marked_complete))
}
```

- 그래서 아래와 같이 test를 위한 coroutine dispatcher를 사용하기 위해서 아래와 같이 변수를 선언하고 설정을 함

```kotlin
@ExperimentalCoroutinesApi
val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

@ExperimentalCoroutinesApi
@Before
fun setupDispatcher() {
    Dispatchers.setMain(testDispatcher)
}

@ExperimentalCoroutinesApi
@After
fun tearDownDispatcher() {
    Dispatchers.resetMain()
    testDispatcher.cleanupTestCoroutines()
}
```

- 거의 대부분의 view model에서 coroutine을 사용할 것이기 때문에 매번 test를 위해서 `TestCoroutinedispatcher`를 쓰는 것보다 재사용할만한 셋업 등에 대해서 JUnit을 활용해서 별도의 클래스를 만들어서 처리함

- JUnit rule에 맞춰서 만들기 위해서 `TestWatcher`를 확장하고 `TestRule` 인터페이스를 implement함

- `starting`과 `finished`메소드는 `@Before`과 `@After`안에 쓰는 함수와 매칭이 됨, 각 test에 before & after로 쓰임

- 또한 `TestCoroutineScope`를 implement하는데 이는 `MainCoroutineRule`이 coroutine timing을 관리할 능력을 줌 

```kotlin
@ExperimentalCoroutinesApi
class MainCoroutineRule(val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()):
   TestWatcher(),
   TestCoroutineScope by TestCoroutineScope(dispatcher) {
   override fun starting(description: Description?) {
       super.starting(description)
       Dispatchers.setMain(dispatcher)
   }

   override fun finished(description: Description?) {
       super.finished(description)
       cleanupTestCoroutines()
       Dispatchers.resetMain()
   }
}
```

- 이를 아래의 해당하는 부분을 한 줄로 바꿈

```kotlin
// REPLACE
@ExperimentalCoroutinesApi
val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

@ExperimentalCoroutinesApi
@Before
fun setupDispatcher() {
    Dispatchers.setMain(testDispatcher)
}

@ExperimentalCoroutinesApi
@After
fun tearDownDispatcher() {
    Dispatchers.resetMain()
    testDispatcher.cleanupTestCoroutines()
}
// WITH
@ExperimentalCoroutinesApi
@get:Rule
var mainCoroutineRule = MainCoroutineRule()
```

- 추가로 `DefaultTasksRepository`를 보면 생성자 의존성 주입을 했음, 이 때 `CoroutineDispatcher`도 하였는데 이를 통해서 `TestCoroutineDispatcher`도 사용할 수 있음

```kotlin
class DefaultTasksRepository(
    private val tasksRemoteDataSource: TasksDataSource,
    private val tasksLocalDataSource: TasksDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) : TasksRepository { ... }
```

- 이미 의존성주입으로 만들었었기 때문에 `MainCoroutineRule`을 `DefaultTasksRepositoryTest` 클래스에 추가할 수 있음

```kotlin
// Set the main coroutines dispatcher for unit testing.
@ExperimentalCoroutinesApi
@get:Rule
var mainCoroutineRule = MainCoroutineRule()
```

- 그리고 `Dispatcher.Main`로 repository에서 교체해서 사용함, 이는 `TestCoroutineDispatcher`의 장점을 위해서 교체를 한 것임

```kotlin
@Before
fun createRepository() {
    tasksRemoteDataSource = FakeDataSource(remoteTasks.toMutableList())
    tasksLocalDataSource = FakeDataSource(localTasks.toMutableList())
    // Get a reference to the class under test.
    tasksRepository = DefaultTasksRepository(
    // HERE Swap Dispatcher.Unconfined
        tasksRemoteDataSource, tasksLocalDataSource, Dispatchers.Main
    )
}
```

- 일반적으로 `TestCoroutineDispatcher`는 test를 실행하기 위해 오직 하나를 만듬, 만약 `runBlockingTest`를 호출하면 특정하지 않은 것에 새로운 `TestCoroutineDispatcher`가 생성될 것임

- `MainCoroutineRule`은 `TestCoroutineDispatcher` 포함함, 그래서 `TestCoroutineDispatcher`의 인스턴스를 여러개 만들지 않게 하기 위해서 `runBlockingTest` 대신해서 `mainCoroutineRule.runBlockingTest`를 씀

```kotlin
// REPLACE
fun getTasks_requestsAllTasksFromRemoteDataSource() = runBlockingTest {

// WITH
fun getTasks_requestsAllTasksFromRemoteDataSource() = mainCoroutineRule.runBlockingTest {
```

### Testing Coroutine Timing
- coroutine에서 `TestCoroutineDispatcher`의 `pauseDispatcher`와 `resumeDispatcher`메소드를 사용해 test를 진행할 것임

- 먼저 view model에 fake repository를 inject해야함, 아래와 같이 이전에 했던바와 같이 `StatisticsViewModel`을 수정할 것임

```kotlin
// REPLACE
class StatisticsViewModel(application: Application) : AndroidViewModel(application) {

    private val tasksRepository = (application as TodoApplication).taskRepository

    // Rest of class
}

// WITH

class StatisticsViewModel(
    private val tasksRepository: TasksRepository
) : ViewModel() { 
    // Rest of class 
}
```
```kotlin
@Suppress("UNCHECKED_CAST")
class StatisticsViewModelFactory (
    private val tasksRepository: TasksRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (StatisticsViewModel(tasksRepository) as T)
}
```

- 동일하게 factory 방식으로 Fragment에서 갱신함

```kotlin
// REPLACE
private val viewModel by viewModels<TasksViewModel>()

// WITH

private val viewModel by viewModels<StatisticsViewModel> {
    StatisticsViewModelFactory(
        (requireContext().applicationContext as TodoApplication).taskRepository
    )
}
```

- 이전 방식과 동일하게 `test` source set에 `StatisticsViewModel`에 대한 test class를 만듬

- `InstantTaskExecutorRule`을 추가함, 이를 통해 동기적으로 task가 처리하게끔 쓸 수 있게함, 그리고 `MainCoroutineRule`도 추가함(coroutine과 view model을 테스트 할 것이기 때문에)

- test를 위한 viewmodel을 필드로 만들고, 의존성을 추가함(test double)

- `@Before`메소드를 생성함

```kotlin
@ExperimentalCoroutinesApi
class StatisticsViewModelTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Subject under test
    private lateinit var statisticsViewModel: StatisticsViewModel

    // Use a fake repository to be injected into the view model.
    private lateinit var tasksRepository: FakeTestRepository

    @Before
    fun setupStatisticsViewModel() {
        // Initialise the repository with no tasks.
        tasksRepository = FakeTestRepository()

        statisticsViewModel = StatisticsViewModel(tasksRepository)
    }
}
```

- task statistics이 로딩중이면 app에서 loading indicator를 보여주고 data가 로드되거나 statistics 계산이 완료되자마자 사라지게 나타낼 것임

- 이는 `refresh`메소드에서 구현되어 있음

```kotlin
fun refresh() {
   _dataLoading.value = true
       viewModelScope.launch {
           tasksRepository.refreshTasks()
           _dataLoading.value = false
       }
}
```

- 이 부분에 대해서 loading indicator가 정확하게 업데이트가 되는지 확인할 필요가 있음

```kotlin
@Test
fun loadTasks_loading() {
    
    // Load the task in the view model.
    statisticsViewModel.refresh()

    // Then progress indicator is shown.
    assertThat(statisticsViewModel.dataLoading.getOrAwaitValue(), `is`(true))

    // Then progress indicator is hidden.
    assertThat(statisticsViewModel.dataLoading.getOrAwaitValue(), `is`(false))
}
```

- 하지만 위의 코드를 실행하면 실패하게 됨 왜냐하면 `dataLoading`이 동시에 `true`와 `false`값으로 존재하기 때문임

- 이 때 `TestCoroutineDispatcher`는 task를 즉시 그리고 바로 실행하는데 이것은 assert statement가 실행되기 전에 이미 완료된다는 의미임

- 물론 이것이 test는 빨리 할 수 있지만, 이번 상황의 경우에는 refresh가 실행하는 도중에 loading indicator의 상태를 봐야하기 때문에 `pauseDispatcher`와 `resumeDispatcher`를 사용해야함

- `pause`의 경우 즉시 실행하는 것보다 새로운 어떠한 coroutiine이 큐에 추가되면 pause가 됨, 이는 `refresh`내부에서의 코드 실행이 coroutine이 실행되기 전에 pause되는 것을 의미함

- 그리고 coroutine의 모든 코드를 실행하기 위해 `resume`을 처리함, 그러면 이제 테스트를 통과할 것임

```kotlin
@Test
fun loadTasks_loading() {
    // Pause dispatcher so you can verify initial values.
    mainCoroutineRule.pauseDispatcher()

    // Load the task in the view model.
    statisticsViewModel.refresh()

    // Then assert that the progress indicator is shown.
    assertThat(statisticsViewModel.dataLoading.getOrAwaitValue(), `is`(true))

    // Execute pending coroutines actions.
    mainCoroutineRule.resumeDispatcher()

    // Then assert that the progress indicator is hidden.
    assertThat(statisticsViewModel.dataLoading.getOrAwaitValue(), `is`(false))
}
```

### Testing Error Handling
- testing에서 예상한대로 코드를 실행하는것도 중요하지만 error나 edge case를 마주했을 때 앱이 어떻게 처리되는지도 중요함

- 그래서 이번엔 task가 load가 안되는 그런 케이스에 대해서 다뤄볼 것임

- 먼저 error 상황을 인위적으로 발생시켜야함, 이렇게 하기 위해선 test double을 갱신하고, flag를 사용해서 error state를 set을 하면 됨

- 만약 flag가 `false`라면 test double function은 normal함, 하지만 만약 flag가 `true`라면 test double은 실제적인 에러를 리턴할 것임

- 예를 들어 data error를 load하는데 실패를 리턴할 것임, 그래서 FakeTestRepository에 error flag를 포함시킴(`true`가 기본임, 실질적인 에러를 리턴하게끔)

- 아래와 같이 초기값은 `false`로 flag를 추가함

```kotlin
private var shouldReturnError = false
```

- 그리고 해당 메소드를 만들고 `getTask`에 `if`문으로 감싸서 처리함

```kotlin
fun setReturnError(value: Boolean) {
    shouldReturnError = value
}
```

- 그리고 `StatisticsViewModel`에서 2개의 `LiveData` 값이 존재함, 이는 `tasks`가 제대로 load 됐는지 확인하는 것임, 만약 error가 있다면 `error`과 `empty`는 `true`가 되야함

```kotlin
class StatisticsViewModel(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    private val tasks: LiveData<Result<List<Task>>> = tasksRepository.observeTasks()

    // Other variables...    
  
    val error: LiveData<Boolean> = tasks.map { it is Error }
    val empty: LiveData<Boolean> = tasks.map { (it as? Success)?.data.isNullOrEmpty() }

    // Rest of the code...    
}
```

- 아래의 새로운 Test 코드를 작성함, 이 때 `tasksRepository`의 함수를 호출해서 `empty`,`error`를 체크함

```kotlin
@Test
fun loadStatisticsWhenTasksAreUnavailable_callErrorToDisplay() {
    // Make the repository return errors.
    tasksRepository.setReturnError(true)
    statisticsViewModel.refresh()

    // Then empty and error are true (which triggers an error message to be shown).
    assertThat(statisticsViewModel.empty.getOrAwaitValue(), `is`(true))
    assertThat(statisticsViewModel.error.getOrAwaitValue(), `is`(true))
}
```

### Testing Room
- 라이브러리 추가 이후, Room DAO 인터페이스 test 코드를 작성할 것임, 하지만 여기서 이 인터페이스를 test class를 위해 직접 만드는 것은 옳지 않음, 그래서 직접 패키지를 만들고 파일을 만들어야함

- 여기서 database test의 경우 instrumented tests이기 때문에, `androidTest` source set에서 진행해야함, 왜냐하면 locally하게 진행한다면 Android Device에 따른 SQLite 버전 차이가 나타나게 될 것임, 그래서 이 경우, 서로 다른 기기로 instrumented test를 진행해야함, 그래서 해당 set에 만듬

- 먼저 아래와 같은 setup을 진행함, 아래의 코드는 앞서 한 방식과 유사함 `runBlockingTest`를 위해서 쓴 어노테이션과 small run-time integration test를 test하기 위해 SmallTest를 그리고 AndroidX Test를 하기 위해서 RunWith을 씀

```kotlin
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class TasksDaoTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

}
```

- 먼저 database field를 만듬

```kotlin
private lateinit var database: ToDoDatabase
```

- 그리고 database에 대해서 초기화를 진행함, 이때 `Room.inMemoryDatabaseBuilder`를 통해, in-memory database를 만듬, 이는 기존 Database와 달리 실제 disk에 저장되지 않기 때문에 한 번 생성되고 끝나면 삭제가 됨, test를 위해서 계속 써야함, 그리고 AndroidX Test를 통해 application context를 가져옴

```kotlin
@Before
fun initDb() {
    // Using an in-memory database so that the information stored here disappears when the
    // process is killed.
    database = Room.inMemoryDatabaseBuilder(
        getApplicationContext(),
        ToDoDatabase::class.java
    ).build()
}
```

- 그리고 끝냈을때의 처리도 추가함

```kotlin
@After
fun closeDb() = database.close()
```

- 그리고 taks를 insert하는 것으로 DAO test를 진행함, id를 통해서 task를 가져옴, 해당 test는 task를 만들고 database에 넣음, 그리고 이를 id를 통해 가져옴, task를 가져왔을 때 inserted task와 매칭이 되는지 확인함

- 둘 다 suspend function이라서 `runBlockingTest`를 통해서 test를 

```kotlin
@Test
fun insertTaskAndGetById() = runBlockingTest {
    // GIVEN - Insert a task.
    val task = Task("title", "description")
    database.taskDao().insertTask(task)

    // WHEN - Get the task by id from the database.
    val loaded = database.taskDao().getTaskById(task.id)

    // THEN - The loaded data contains the expected values.
    assertThat<Task>(loaded as Task, notNullValue())
    assertThat(loaded.id, `is`(task.id))
    assertThat(loaded.title, `is`(task.title))
    assertThat(loaded.description, `is`(task.description))
    assertThat(loaded.isCompleted, `is`(task.isCompleted))
}
```

- update에 대해서도 아래와 같이 test를 진행함

```kotlin
@Test
    fun updateTaskAndGetById() = runBlockingTest {
        // When inserting a task
        val originalTask = Task("title", "description")
        database.taskDao().insertTask(originalTask)

        // When the task is updated
        val updatedTask = Task("new title", "new description", true, originalTask.id)
        database.taskDao().updateTask(updatedTask)

        // THEN - The loaded data contains the expected values
        val loaded = database.taskDao().getTaskById(originalTask.id)
        assertThat(loaded?.id, `is`(originalTask.id))
        assertThat(loaded?.title, `is`("new title"))
        assertThat(loaded?.description, `is`("new description"))
        assertThat(loaded?.isCompleted, `is`(true))
    }
```

- 앞선 test들은 TasksDao에 대한 unit test였음, 이제 TasksLocalDataSource와 함께 integration test를 진행해봐야함

- TasksLocalDataSource는 DAO를 통해 정보를 리턴받고 repository calss가 원하는 방향으로 형식을 변환하는 클래스임, 실제 code를 test할 예정임

- `TasksLocalDataSource` 클래스를 `androidTest` source set으로 만듬, 아래 코드의 내용 중 다른 것은 실제 test를 진행할 것이므로 `MediumTest`인 것 말고는 구성은 동일함

```kotlin
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class TasksLocalDataSourceTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

}
```

- 먼저 `TasksDataSource`와 `database`를 test하기 위해서 선언함

```kotlin
private lateinit var localDataSource: TasksLocalDataSource
private lateinit var database: ToDoDatabase
```

- 그리고 database와 datasource를 초기화할 메소드를 만듬, database는 앞서 말한대로 일회성으로 만들고, 여기다가 `allowMainThreadQueries`를 통해 main thread에서 처리하게 함(실제 production code에선 하면 안됨!)

- database와 `Dispatchers.Main`을 통해 `TasksLocalDataSource`의 인스턴스를 만듬, 이는 쿼리문이 main thread에서 진행됨을 의미함, after 세팅도 진행함

```kotlin
@Before
fun setup() {
    // Using an in-memory database for testing, because it doesn't survive killing the process.
    database = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        ToDoDatabase::class.java
    )
        .allowMainThreadQueries()
        .build()

    localDataSource =
        TasksLocalDataSource(
            database.taskDao(),
            Dispatchers.Main
        )
}

@After
    fun cleanUp() {
        database.close()
    }
```

- 이제 TasksLocalDataSourceTest를 진행함, 이는 DAO test와 유사함, 다른 점은 result에 대한 처리만 다름

```kotlin
// runBlocking is used here because of https://github.com/Kotlin/kotlinx.coroutines/issues/1204
// TODO: Replace with runBlockingTest once issue is resolved
@Test
fun saveTask_retrievesTask() = runBlocking {
    // GIVEN - A new task saved in the database.
    val newTask = Task("title", "description", false)
    localDataSource.saveTask(newTask)

    // WHEN  - Task retrieved by ID.
    val result = localDataSource.getTask(newTask.id)

    // THEN - Same task is returned.
    assertThat(result.succeeded, `is`(true))
    result as Success
    assertThat(result.data.title, `is`("title"))
    assertThat(result.data.description, `is`("description"))
    assertThat(result.data.isCompleted, `is`(false))
}
```

- 그리고 추가 테스트를 진행함

```kotlin
@Test
    fun completeTask_retrievedTaskIsComplete() = runBlocking {
        // Given a new task in the persistent repository
        val newTask = Task("title")
        localDataSource.saveTask(newTask)

        // When completed in the persistent repository
        localDataSource.completeTask(newTask)
        val result = localDataSource.getTask(newTask.id)

        // Then the task can be retrieved from the persistent repository and is complete
        assertThat(result.succeeded, `is`(true))
        result as Success
        assertThat(result.data.title, `is`(newTask.title))
        assertThat(result.data.isCompleted, `is`(true))
    }
```

### End-to-End Testing with Data Binding
- End-to-end test(E2E)는 feature의 조합을 함께 작업하여 테스트 하는 것임

- app의 거대한 일부를 test하고 실제 사용을 시뮬에이션함, 그래서 이 test는 instrumented test임

- E2E test는 integration test와 다른점이 있음

   - 첫 화면에서부터 앱이 시작됨

   - 실제 activity와 repository를 생성함

   - 여러개의 fragment가 함께 작동하는 것을 test함

- E2E test의 경우 복잡하기 때문에 이를 쉽게 하기 위해 다양한 툴과 라이브러리를 씀, 앞서 본 Espresso 역시 사용할 수 있음

- 먼저 적절한 테스트를 하기 위해서 animation을 꺼야함

- 그리고 `TasksActivityTest`로 `androidTest` source set에 만들고 기본 세팅을 함, 기본적으로 테스트를 위한 세팅과 그리고 실제 `Repository`를 사욯애서 할 것이기 때문에 이와 관련해서 `ServiceLocator`를 활용한 아래와 같이 코드를 작성함

```kotlin
@RunWith(AndroidJUnit4::class)
@LargeTest
class TasksActivityTest {

    private lateinit var repository: TasksRepository

    @Before
    fun init() {
        repository = ServiceLocator.provideTasksRepository(getApplicationContext())
        runBlocking {
            repository.deleteAllTasks()
        }
    }

    @After
    fun reset() {
        ServiceLocator.resetRepository()
    }
}
```

- 그리고 아래와 같이 저장된 task를 edit하는 end-to-end test를 작성할 수 있음, 아래의 코드는 앞서 처리한 suspend function에 대해서 활용하는 점이나 Scenario처리 등 동일하게 작용함

- 여기서 `ActivityScenarioRule`이 다른데, 이 rule을 `launch`로 호출하기 전에 repository에 task를 추가하는 등 data state를 설정함, 앞서 본 `FragmentScenario`와 유사함

```kotlin
@Test
fun editTask() = runBlocking {
    // Set initial state.
    repository.saveTask(Task("TITLE1", "DESCRIPTION"))
    
    // Start up Tasks screen.
    val activityScenario = ActivityScenario.launch(TasksActivity::class.java)


    // Espresso code will go here.


    // Make sure the activity is closed before resetting the db:
    activityScenario.close()
}
```

- 그리고 Espresso 코드를 추가함

```kotlin
@Test
fun editTask() = runBlocking {

    // Set initial state.
    repository.saveTask(Task("TITLE1", "DESCRIPTION"))
    
    // Start up Tasks screen.
    val activityScenario = ActivityScenario.launch(TasksActivity::class.java)

    // Click on the task on the list and verify that all the data is correct.
    onView(withText("TITLE1")).perform(click())
    onView(withId(R.id.task_detail_title_text)).check(matches(withText("TITLE1")))
    onView(withId(R.id.task_detail_description_text)).check(matches(withText("DESCRIPTION")))
    onView(withId(R.id.task_detail_complete_checkbox)).check(matches(not(isChecked())))

    // Click on the edit button, edit, and save.
    onView(withId(R.id.edit_task_fab)).perform(click())
    onView(withId(R.id.add_task_title_edit_text)).perform(replaceText("NEW TITLE"))
    onView(withId(R.id.add_task_description_edit_text)).perform(replaceText("NEW DESCRIPTION"))
    onView(withId(R.id.save_task_fab)).perform(click())

    // Verify task is displayed on screen in the task list.
    onView(withText("NEW TITLE")).check(matches(isDisplayed()))
    // Verify previous task is not displayed.
    onView(withText("TITLE1")).check(doesNotExist())
    // Make sure the activity is closed before resetting the db.
    activityScenario.close()
}
```

- 하지만 위의 테스트가 어쩔 땐 실패하고 어쩔 땐 성공하게끔 처리가 됨, 이는 test synchronization issue로 인해서 timing이 어쩔 땐 실패할 때도 존재해서 그런 것임

- Espresso는 UI에서 UI action과 resulting change사이에서 synchronizes를 함, 이는 아래의 예시로 설명하면 step1의 클릭이 일어난 후에 새로운 view가 보여질 때까지 기다림, step2의 test가 있는지 확인하기 전에

```kotlin
onView(withId(R.id.next_screen_button)).perform(click()) // Step 1
onView(withId(R.id.screen_description)).check(matches(withText("The next screen"))) // Step 2
```

- 이런 상황에서 Espresso의 내장된 동기화 매커니즘은 view가 업데이트 될 때까지 기다릴 정도까진 아님, 즉 데이터가 업데이트 되어 load 되었는지 모름, 그래서 data binding이 view를 update하는 것도 모름

- 그래서 이 때 idling resource 동기화 매커니즘을 사용해야함, 이는 명시적으로 Espresso에게 app이 idle한지 말하는 것을 의미함, 이를 위해 해당 resource를 추가할 수 있음

- 아래와 같이 라이브러리와 설정을 추가함, 해당 설정은 application code에서 idling resource code가 실행되어도 unit test는 유지하게끔 해주는 설정임

```kotlin
implementation "androidx.test.espresso:espresso-idling-resource:$espressoVersion"

    testOptions.unitTests {
        includeAndroidResources = true
        returnDefaultValues = true
    }
```

- 이제 이를 쓰기 위해서 2개의 idling resource를 쓸 수 있음 하나는 view를 위해 data binding의 동기화를 다루고 다른 하나는 repositoy에서의 오래걸리는 작업을 다룸

- 먼저 오래걸리는 작업에 대해서 아래와 같이 싱글톤 객체로 만들 수 있음(util패키지에 만듬)

- 여기서 `CountingIdlingResource` 클래스를 만드는데 이 클래스는 2개를 counting함

   - counter가 0보다 크면 app이 작업하는거로 간주함

   - counter가 0이면 app이 idle로 여김

- 일반적으로 app이 작업을 시작할 때 counter를 증가시킴, 그리고 작업이 완료되면 counter를 감소시킴, 그러므로 `CountingIdlingResource`는 아무런 작업도 완료되지 않았다면 0이 되어 있음, 싱글톤이기 때문에 긴 작업이 완료되면 어디서든 접근할 수 있음

```kotlin
object EspressoIdlingResource {

    private const val RESOURCE = "GLOBAL"

    @JvmField
    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
}
```

- 여기서 `EspressoIdlingResource`를 쓰는것에 대해서 인라인 함수로 제공해줄 수 있음

```kotlin
inline fun <T> wrapEspressoIdlingResource(function: () -> T): T {
    // Espresso does not work well with coroutines yet. See
    // https://github.com/Kotlin/kotlinx.coroutines/issues/982
    EspressoIdlingResource.increment() // Set app as busy.
    return try {
        function()
    } finally {
        EspressoIdlingResource.decrement() // Set app as idle.
    }
}
```

- 그래서 이를 아래와 같이 쓸 수 있음

```kotlin
wrapEspressoIdlingResource {
    doWorkThatTakesALongTime()
}
```

- 이제 이를 DefaultTasksRepository에서 `wrapEspressoIdlingResource`로 감싸서 처리할 수 있음, 이 때 모든 Repository 메소드를 아래와 같이 작업함

```kotlin
override suspend fun getTasks(forceUpdate: Boolean): Result<List<Task>> {
        wrapEspressoIdlingResource {
            if (forceUpdate) {
                try {
                    updateTasksFromRemoteDataSource()
                } catch (ex: Exception) {
                    return Result.Error(ex)
                }
            }
            return tasksLocalDataSource.getTasks()
        }
    }

    override suspend fun refreshTasks() {
        wrapEspressoIdlingResource {
            updateTasksFromRemoteDataSource()
        }
    }

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        wrapEspressoIdlingResource {
            return tasksLocalDataSource.observeTasks()
        }
    }

    override suspend fun refreshTask(taskId: String) {
        wrapEspressoIdlingResource {
            updateTaskFromRemoteDataSource(taskId)
        }
    }

    private suspend fun updateTasksFromRemoteDataSource() {
        wrapEspressoIdlingResource {
            val remoteTasks = tasksRemoteDataSource.getTasks()

            if (remoteTasks is Success) {
                // Real apps might want to do a proper sync.
                tasksLocalDataSource.deleteAllTasks()
                remoteTasks.data.forEach { task ->
                    tasksLocalDataSource.saveTask(task)
                }
            } else if (remoteTasks is Result.Error) {
                throw remoteTasks.exception
            }
        }
    }

    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        wrapEspressoIdlingResource {
            return tasksLocalDataSource.observeTask(taskId)
        }
    }

    private suspend fun updateTaskFromRemoteDataSource(taskId: String) {
        wrapEspressoIdlingResource {
            val remoteTask = tasksRemoteDataSource.getTask(taskId)

            if (remoteTask is Success) {
                tasksLocalDataSource.saveTask(remoteTask.data)
            }
        }
    }

    /**
     * Relies on [getTasks] to fetch data and picks the task with the same ID.
     */
    override suspend fun getTask(taskId: String, forceUpdate: Boolean): Result<Task> {
        wrapEspressoIdlingResource {
            if (forceUpdate) {
                updateTaskFromRemoteDataSource(taskId)
            }
            return tasksLocalDataSource.getTask(taskId)
        }
    }

    override suspend fun saveTask(task: Task) {
        wrapEspressoIdlingResource {
            coroutineScope {
                launch { tasksRemoteDataSource.saveTask(task) }
                launch { tasksLocalDataSource.saveTask(task) }
            }
        }
    }

    override suspend fun completeTask(task: Task) {
        wrapEspressoIdlingResource {
            coroutineScope {
                launch { tasksRemoteDataSource.completeTask(task) }
                launch { tasksLocalDataSource.completeTask(task) }
            }
        }
    }

    override suspend fun completeTask(taskId: String) {
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                (getTaskWithId(taskId) as? Success)?.let { it ->
                    completeTask(it.data)
                }
            }
        }
    }

    override suspend fun activateTask(task: Task) = withContext<Unit>(ioDispatcher) {
        wrapEspressoIdlingResource {
            coroutineScope {
                launch { tasksRemoteDataSource.activateTask(task) }
                launch { tasksLocalDataSource.activateTask(task) }
            }
        }
    }

    override suspend fun activateTask(taskId: String) {
      wrapEspressoIdlingResource {
          withContext(ioDispatcher) {
              (getTaskWithId(taskId) as? Success)?.let { it ->
                  activateTask(it.data)
              }
          }
      }
    }

    override suspend fun clearCompletedTasks() {
      wrapEspressoIdlingResource {
          coroutineScope {
              launch { tasksRemoteDataSource.clearCompletedTasks() }
              launch { tasksLocalDataSource.clearCompletedTasks() }
          }
      }
    }

    override suspend fun deleteAllTasks() {
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                coroutineScope {
                    launch { tasksRemoteDataSource.deleteAllTasks() }
                    launch { tasksLocalDataSource.deleteAllTasks() }
                }
            }
        }
    }

    override suspend fun deleteTask(taskId: String) {
        wrapEspressoIdlingResource {
            coroutineScope {
                launch { tasksRemoteDataSource.deleteTask(taskId) }
                launch { tasksLocalDataSource.deleteTask(taskId) }
            }
        }
    }

    private suspend fun getTaskWithId(id: String): Result<Task> {
        wrapEspressoIdlingResource {
            return tasksLocalDataSource.getTask(id)
        }
    }
```

- idling resource를 사용했기 때문에 Espresso는 data가 load 될 때까지 기다리게 됨, 이제 다음에는 data binding을 위해서 만들것임

- 이 별도의 작업을 해야하는 이유는 Espresso는 data binding 라이브러리와 자동으로 작업을 하지 않기 때문에 이를 알려줘야 하기 때문에 idling resource를 만들어줘야함(아예 다른 매커니즘을 따름)

- `androidTest`에 util 패키지를 새로 만들어서 해당 클래스 파일을 만듬

- 코드가 다양하게 있는데 기본 컨셉은 `ViewDataBinding`이 data binding layout을 쓰는 어디서든 생성됨, 그래서 `hasPendingBindings` 메소드가 UI의 갱신이나 data에서 변화를 반영할 때 data binding 라이브러리가 쓰게 됨

- 아래의 코드는 pending bindings은 없게 처리를 함, 마지막에 확장함수의 경우 activity와 fragment를 각각 받음, 그들은 `DataBindingIdlingResource`와 연관된 것으로 진행을 함, 그래서 layout state를 track할 수 있음

- 그리고 이 2개 중 하나를 test를 위해서 호출할 것임, `DataBindingIdlingResource`는 이와 무관함

```kotlin
class DataBindingIdlingResource : IdlingResource {
    // List of registered callbacks
    private val idlingCallbacks = mutableListOf<IdlingResource.ResourceCallback>()
    // Give it a unique id to work around an Espresso bug where you cannot register/unregister
    // an idling resource with the same name.
    private val id = UUID.randomUUID().toString()
    // Holds whether isIdle was called and the result was false. We track this to avoid calling
    // onTransitionToIdle callbacks if Espresso never thought we were idle in the first place.
    private var wasNotIdle = false

    lateinit var activity: FragmentActivity

    override fun getName() = "DataBinding $id"

    override fun isIdleNow(): Boolean {
        val idle = !getBindings().any { it.hasPendingBindings() }
        @Suppress("LiftReturnOrAssignment")
        if (idle) {
            if (wasNotIdle) {
                // Notify observers to avoid Espresso race detector.
                idlingCallbacks.forEach { it.onTransitionToIdle() }
            }
            wasNotIdle = false
        } else {
            wasNotIdle = true
            // Check next frame.
            activity.findViewById<View>(android.R.id.content).postDelayed({
                isIdleNow
            }, 16)
        }
        return idle
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
        idlingCallbacks.add(callback)
    }

    /**
     * Find all binding classes in all currently available fragments.
     */
    private fun getBindings(): List<ViewDataBinding> {
        val fragments = (activity as? FragmentActivity)
            ?.supportFragmentManager
            ?.fragments

        val bindings =
            fragments?.mapNotNull {
                it.view?.getBinding()
            } ?: emptyList()
        val childrenBindings = fragments?.flatMap { it.childFragmentManager.fragments }
            ?.mapNotNull { it.view?.getBinding() } ?: emptyList()

        return bindings + childrenBindings
    }
}

private fun View.getBinding(): ViewDataBinding? = DataBindingUtil.getBinding(this)

/**
 * Sets the activity from an [ActivityScenario] to be used from [DataBindingIdlingResource].
 */
fun DataBindingIdlingResource.monitorActivity(
    activityScenario: ActivityScenario<out FragmentActivity>
) {
    activityScenario.onActivity {
        this.activity = it
    }
}

/**
 * Sets the fragment from a [FragmentScenario] to be used from [DataBindingIdlingResource].
 */
fun DataBindingIdlingResource.monitorFragment(fragmentScenario: FragmentScenario<out Fragment>) {
    fragmentScenario.onFragment {
        this.activity = it.requireActivity()
    }
}
```

- Espresso는 idling resource만을 기다림, 이전에 했던 activity test에서 이를 등록하고 해제하는 것만이 남았음

- 먼저 아래와 같이 idling resource를 변수로 선언하고 등록과 해제를 아래와 같이 처리함

- 여기서 `countingIdlingResource`와 `dataBindingIdlingResource`모두 idle인지 아닌지 app code를 모니터링함

- test에서 이 요소를 등록했는데 resource가 busy하다면, Espresso는 다음 명령을 처리하기전 idle을 기다릴 것임, 이는 `countingIdlingResource`가 0보다 큰 값으로 체크되거나 pending data binding layout이 있다면 Espresso는 기다린다는 것을 의미함

```kotlin
    // An idling resource that waits for Data Binding to have no pending bindings.
    private val dataBindingIdlingResource = DataBindingIdlingResource()
```
```kotlin
    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }
```

- 그리고 기존 test 코드를 아래와 같이 갱신할 수 있음, 추가적인 setup을 함, activity scenario실행 후, 모니터링을 하는 것임, 그러면 더이상 test 결과가 중구난방으로 실패와 성공이 매번 바뀌지 않을 것임

```kotlin
  @Test
    fun editTask() = runBlocking {
        repository.saveTask(Task("TITLE1", "DESCRIPTION"))

        // Start up Tasks screen.
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario) // LOOK HERE

        // Rest of test...
    }
```

- 그리고 다른 test 코드로 아래와 같이 확인 가능함, 메뉴화면을 통해 추가 확인

```kotlin
@Test
    fun createOneTask_deleteTask() {

        // start up Tasks screen
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Add active task
        onView(withId(R.id.add_task_fab)).perform(click())
        onView(withId(R.id.add_task_title_edit_text))
            .perform(typeText("TITLE1"), closeSoftKeyboard())
        onView(withId(R.id.add_task_description_edit_text)).perform(typeText("DESCRIPTION"))
        onView(withId(R.id.save_task_fab)).perform(click())

        // Open it in details view
        onView(withText("TITLE1")).perform(click())
        // Click delete task in menu
        onView(withId(R.id.menu_delete)).perform(click())

        // Verify it was deleted
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_all)).perform(click())
        onView(withText("TITLE1")).check(doesNotExist())
        // Make sure the activity is closed before resetting the db:
        activityScenario.close()
    }
```

### End-to-End App Navigation Testing
- 마지막으로 navigation drawer, app toolbar, Up button, Back button에 대해서 test를 진행할 예정임

- `androidTest` source set에서 해당 파일을 만들고 아래와 같이 초기 세팅을 진행함

```kotlin
@RunWith(AndroidJUnit4::class)
@LargeTest
class AppNavigationTest {

    private lateinit var tasksRepository: TasksRepository

    // An Idling Resource that waits for Data Binding to have no pending bindings.
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun init() {
        tasksRepository = ServiceLocator.provideTasksRepository(getApplicationContext())
    }

    @After
    fun reset() {
        ServiceLocator.resetRepository()
    }

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your idling resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

}
```

- 그리고 앞서 한 E2E testing과 같이 ActivityScenario와 DataBindingIdingResource를 setup을 진행함

```kotlin
@Test
fun tasksScreen_clickOnDrawerIcon_OpensNavigation() {
    // Start the Tasks screen.
    val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
    dataBindingIdlingResource.monitorActivity(activityScenario)

    // 1. Check that left drawer is closed at startup.

    // 2. Open drawer by clicking drawer icon.

    // 3. Check if drawer is open.

    // When using ActivityScenario.launch(), always call close()
    activityScenario.close()
}

@Test
fun taskDetailScreen_doubleUpButton() = runBlocking {
    val task = Task("Up button", "Description")
    tasksRepository.saveTask(task)

    // Start the Tasks screen.
    val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
    dataBindingIdlingResource.monitorActivity(activityScenario)

    // 1. Click on the task on the list.
    
    // 2. Click on the edit task button.
   
    // 3. Confirm that if we click Up button once, we end up back at the task details page.
   
    // 4. Confirm that if we click Up button a second time, we end up back at the home screen.
   
    // When using ActivityScenario.launch(), always call close().
    activityScenario.close()
}


@Test
fun taskDetailScreen_doubleBackButton() = runBlocking {
    val task = Task("Back button", "Description")
    tasksRepository.saveTask(task)

    // Start Tasks screen.
    val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
    dataBindingIdlingResource.monitorActivity(activityScenario)

    // 1. Click on the task on the list.
    
    // 2. Click on the Edit task button.
    
    // 3. Confirm that if we click Back once, we end up back at the task details page.
    
    // 4. Confirm that if we click Back a second time, we end up back at the home screen.
    
    // When using ActivityScenario.launch(), always call close()
    activityScenario.close()
}
```

- 여기서 test를 위해서 global navigation view에 대한 assertion을 할 수 있도록 access가 되야함, 그래서 이에 대한 확장함수를 아래와 같이 추가해서 처리할 수 있음

```kotlin
fun <T : Activity> ActivityScenario<T>.getToolbarNavigationContentDescription()
        : String {
    var description = ""
    onActivity {
        description =
            it.findViewById<Toolbar>(R.id.toolbar).navigationContentDescription as String
    }
    return description
}
```

- 위와 같이 확장함수를 통해서 navigation button에 대한 클릭한 것을 확인가능함

- 그리고 drawer가 열렸는지 닫혔는지도 back button에 대한 것도 확인이 가능함

- test를 마무리하면 아래와 같음

```kotlin
/**
 * Tests for the [DrawerLayout] layout component in [TasksActivity] which manages
 * navigation within the app.
 *
 * UI tests usually use [ActivityTestRule] but there's no API to perform an action before
 * each test. The workaround is to use `ActivityScenario.launch()` and `ActivityScenario.close()`.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class AppNavigationTest {

    private lateinit var tasksRepository: TasksRepository

    // An Idling Resource that waits for Data Binding to have no pending bindings
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun init() {
        tasksRepository = ServiceLocator.provideTasksRepository(getApplicationContext())
    }

    @After
    fun reset() {
        ServiceLocator.resetRepository()
    }

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun tasksScreen_clickOnAndroidHomeIcon_OpensNavigation() {
        // Start up Tasks screen
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Check that left drawer is closed at startup
        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START))) // Left Drawer should be closed.

        // Open drawer by clicking drawer icon
        onView(
            withContentDescription(
                activityScenario
                    .getToolbarNavigationContentDescription()
            )
        ).perform(click())

        // Check if drawer is open
        onView(withId(R.id.drawer_layout))
            .check(matches(isOpen(Gravity.START))) // Left drawer is open open.
        // When using ActivityScenario.launch, always call close()
        activityScenario.close()
    }

    @Test
    fun taskDetailScreen_doubleUpButton() = runBlocking {
        val task = Task("Up <- button", "Description")
        tasksRepository.saveTask(task)

        // Start up Tasks screen
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the task on the list
        onView(withText("Up <- button")).perform(click())

        // Click on the edit task button
        onView(withId(R.id.edit_task_fab)).perform(click())

        // Confirm that if we click up button once, we end up back at the task details page
        onView(
            withContentDescription(
                activityScenario
                    .getToolbarNavigationContentDescription()
            )
        ).perform(click())
        onView(withId(R.id.task_detail_title_text)).check(matches(isDisplayed()))

        // Confirm that if we click up button a second time, we end up back at the home screen
        onView(
            withContentDescription(
                activityScenario
                    .getToolbarNavigationContentDescription()
            )
        ).perform(click())
        onView(withId(R.id.tasks_container_layout)).check(matches(isDisplayed()))

        // When using ActivityScenario.launch, always call close()
        activityScenario.close()
    }

    @Test
    fun taskDetailScreen_doubleBackButton() = runBlocking {
        val task = Task("Back button", "Description")
        tasksRepository.saveTask(task)

        // Start up Tasks screen
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the task on the list
        onView(withText("Back button")).perform(click())

        // Click on the edit task button
        onView(withId(R.id.edit_task_fab)).perform(click())

        // Confirm that if we click back once, we end up back at the task details page
        pressBack()
        onView(withId(R.id.task_detail_title_text)).check(matches(isDisplayed()))

        // Confirm that if we click back a second time, we end up back at the home screen
        pressBack()
        onView(withId(R.id.tasks_container_layout)).check(matches(isDisplayed()))

        // When using ActivityScenario.launch, always call close()
        activityScenario.close()
    }
}

fun <T : Activity> ActivityScenario<T>.getToolbarNavigationContentDescription()
        : String {
    var description = ""
    onActivity {
        description =
            it.findViewById<Toolbar>(R.id.toolbar).navigationContentDescription as String
    }
    return description
}
```