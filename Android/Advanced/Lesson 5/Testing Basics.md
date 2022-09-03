- 일반적으로 앱이 돌아가길 희망하면서 하는 테스트는 manual test일 경우가 높음

- 하지만 한 번 앱을 만들고 기능을 추가하고 테스트하는데 있어서 이런식으로 계속 수작업으로 테스트 하는 것은 실수가 날 수도 있고 굉장히 피로한 작업이며 효율적이지 못함

- 그래서 이런 경우에는 오히려 직접 테스트 하는 것보다 자동으로 테스트를 함, 이런 방식을 TDD라고도 함

### Familiarizing yourself with the code
- 해당 test code 환경은 다른 본인들만의 앱을 만든다고 했을 때 동일하게 적용할 수 있음

- 해당 앱은 app architecture를 따르고 testing에 최적화 되어있음

- 그리고 ViewModel과 Fragment와 repository, Room을 사용함, 이를 도식화 하면 아래와 같음

![one](/Android/img/fiftyeight.png)

- 그리고 package 별 구성요소와 layer는 아래와 같이 나뉘어짐

![one](/Android/img/fiftynine.png)

- Data layer에서 이 앱은 networking과 database layer 모두 가지고 있음, 실험을 위해 직접 network request보다 `Hashmap`으로 시뮬레이션을 함

- `DefaultTasksRepository`는 networking layer와 database layer 사이에서 중재를 하며 UI layer의 data를 리턴함

- UI layer(.addedittask, .statistics, .taskdetail, .tasks)는 각각 fragment와 view model에 대해 관련 UI에 대한 class를 담고 있음, `TaskActivity`는 모든 fragment를 담고 있는 activity임

- Navigation은 Navigation component를 사용한 것임, 기존에 사용한 Navigation component와 동일한 방식임

### Running tests
- Project를 파일을 보면 동일한 패키지명으로 3개의 source set이 있음

- 그 중 일반적으로 코드를 작성하고 만드는 부분은 main이고 녹색으로 칠해진 source set은 test를 포함한 곳임 그 중 `androidTest`는 instrumented test를 하는 곳이고 `test`는 local test를 하는 곳임

- Local test(test source set)는 개발하는 JVM 위에서 로컬로 수행하는 데이터라 애뮬레이터나 기계가 필요하지 않음, 빠르게 개발은 가능하지만 실제 반영에 있어서 한계가 존재함, `JUnit`을 씀

- Instrumented test(androidTest source set)는 실제 기기나 애뮬레이터를 통해 테스트를 하는 것임, 그래서 실제로 일어나는 상황을 반영하지만 그만큼 속도가 느림

- 테스트로 `ExampleUnitTest`를 돌려 볼 수 있음, 녹색이 뜬다면 테스트를 통과한 것임

- 테스트 코드를 보면 아래와 같음

- 이는 test source sets에 있는 클래스 중 하나임

- 이를 실행하면 `@Test`어노테이션이 붙어있는 함수를 실행함(각 함수는 단일 테스트임)

- 일반적으로 assertion statements를 포함하고 있음

- Android는 test를 위해서 JUnit을 사용함, assertions과 `@Test`모두 JUnit에서 가져온 것임

- assertion은 test의 핵심인데, 앱이 실행될 때의 값과 예상되는 값을 확인하는 것임, 아래 예시로는 2 + 2가 4로 되는게 맞는지 확인하는 것을 의미함

```kotlin
// A test class is just a normal class
class ExampleUnitTest {

   // Each test is annotated with @Test (this is a Junit annotation)
   @Test
   fun addition_isCorrect() {
       // Here you are checking that 4 is the same as 2+2
       assertEquals(4, 2 + 2)
   }
}
```

- 여기서 아래와 같이 실패케이스를 추가하면 X로 뜸, 그러면 일단 전체 테스트는 실패하고 실제 값과 예상값이 나오고 어떤 부분에서 실패했는지도 알려줌

```kotlin
class ExampleUnitTest {

   // Each test is annotated with @Test (this is a Junit annotation)
   @Test
   fun addition_isCorrect() {
       assertEquals(4, 2 + 2)
       assertEquals(3, 1 + 1) // This should fail
   }
}
```

![one](/Android/img/sixty.png)

- 그 다음 `androidTest`에 있는 아래의 test를 실행할 수 있음, 이는 앞선 local test와 다르게 실제 디바이스가 필요함

```kotlin
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.android.architecture.blueprints.reactive",
            appContext.packageName)
    }
}
```

### Writing your first test
- `getActiveAndCompletedStats` 함수에 대해서 `StatsResult`는 2개의 숫자를 가지고 있는 data class인데 완료 퍼센티지와 진행중인 퍼센티지를 담고 있음

```kotlin
internal fun getActiveAndCompletedStats(tasks: List<Task>?): StatsResult {

   val totalTasks = tasks!!.size
   val numberOfActiveTasks = tasks.count { it.isActive }
   val activePercent = 100 * numberOfActiveTasks / totalTasks
   val completePercent = 100 * (totalTasks - numberOfActiveTasks) / totalTasks

   return StatsResult(
       activeTasksPercent = activePercent.toFloat(),
       completedTasksPercent = completePercent.toFloat()
   )
  
}

data class StatsResult(val activeTasksPercent: Float, val completedTasksPercent: Float)
``` 

- 여기서 해당 함수를 두고 Generate > Test를 통해서 Test Code를 만들게 아래와 같이 할 수 있음

![one](/Android/img/sixtyone.png)

![one](/Android/img/sixtytwo.png)

- 그리고 클래스 이름은 `StatisticsUtilsTest`로 만들고 나머진 그대로 디폴트로 둠, 그리고 앞서 설명한대로 `test` 패키지에 추가함(local test이므로), 그러면 해당 디렉토리에 `statistics` 패키지에 해당 UnitTest 클래스가 생김

- test로 확인할 내용은 만약 완료된 task가 없고 하나의 active한 task가 있다면 active test는 100%여야 하고 completed task는 0%여야함

- 해당 테스트를 위해 아래의 함수를 만듬, 그리고 위에 `@Test`를 붙임

```kotlin
class StatisticsUtilsTest {

	@Test
    fun getActiveAndCompletedStats_noCompleted_returnsHundredZero() {
        // Create an active task

        // Call your function

        // Check the result
    }
}
```

- 그리고 task의 list를 먼저 만듬

```kotlin
// Create an active task 
val tasks = listOf<Task>(
            Task("title", "desc", isCompleted = false)
        )
```

- 그리고 함수에서 이 task를 호출함

```kotlin
// Call your function
val result = getActiveAndCompletedStats(tasks)
```

- 마지막으로 `result`를 확인하고 예상 값과 assertions을 사용함

```kotlin
// Check the result
assertEquals(result.completedTasksPercent, 0f)
assertEquals(result.activeTasksPercent, 100f)
```

- 그러면 아래와 같이 만들어짐, 그리고 테스트를 돌리면 통과가 될 것임

```kotlin
class StatisticsUtilsTest {

    @Test
    fun getActiveAndCompletedStats_noCompleted_returnsHundredZero() {

        // Create an active task (the false makes this active)
        val tasks = listOf<Task>(
            Task("title", "desc", isCompleted = false)
        )
        // Call your function
        val result = getActiveAndCompletedStats(tasks)

        // Check the result
        assertEquals(result.completedTasksPercent, 0f)
        assertEquals(result.activeTasksPercent, 100f)
    }
}
```

- 여기서 테스트 코드가 문서상 사람이 읽기 쉽게 써져 있기 때문에 읽기 수월했음

- 하지만 Hamcrest를 사용하게 된다면 좀 더 가독성이 높아짐, 이를 위해서 해당 라이브러리를 아래와 같이 추가함

```kotlin
dependencies {
    // Other dependencies
    testImplementation "org.hamcrest:hamcrest:$hamcrestVersion"
}
```

- 보통 dependency를 추가할 때 `implementation`을 썼지만 아직 `testImplementation`은 쓰지 않았을 것임, 이 때 일반적으로 실제 앱을 출시할 때 크기 문제도 있기 때문에 test와 관련된 코드와 dependency는 추가하지 않음

- 라이브러리 추가하는데 있어서 구분을 하면 크게 3가지로 아래와 같이 나눌 수 있음

   - `implementation` : 모든 source set에서 사용가능한 dependency를 추가함, test source set도 포함

   - `testImplementation` : test source set에서만 사용가능한 dependency를 추가함

   - `androidTestImplementaion` : `androidTest` source set에서만 사용가능한 dependency를 추가함

- Hamcrerst를 쓴다면 아래와 같이 바뀜, 그리고 

```kotlin
// REPLACE
assertEquals(result.completedTasksPercent, 0f)
assertEquals(result.activeTasksPercent, 100f)

// WITH
assertThat(result.activeTasksPercent, `is`(100f))
assertThat(result.completedTasksPercent, `is`(0f))
```
```kotlin
import com.example.android.architecture.blueprints.todoapp.data.Task
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Test

class StatisticsUtilsTest {

    @Test
    fun getActiveAndCompletedStats_noCompleted_returnsHundredZero() {

        // Create an active tasks (the false makes this active)
        val tasks = listOf<Task>(
            Task("title", "desc", isCompleted = false)
        )
        // Call your function
        val result = getActiveAndCompletedStats(tasks)

        // Check the result
        assertThat(result.activeTasksPercent, `is`(100f))
        assertThat(result.completedTasksPercent, `is`(0f))

    }
}
```

- 자세한 건 공식문서를 보는게 좋음

- 일반적으로 test code를 작성하는데 전략이 있음

- Given, When, Then으로 3개로 나눌 수 있음

   - Given : test를 위해 필요로 하는 앱의 상태나 객체를 세팅함, 이 테스트는 active가 될 task의 모든 task list에 대해서 가지고 있는 것임

   - When : 테스트할 객체에 대해서 실제 action을 함, 위에서는 `getActiveAndCompletedStats`로 볼 수 있음

   - Then : 이 곳에서 action을 한 뒤 test의 성공실패 여부를 확인하는 실제 체크를 하는 곳임, 보통 몇 개의 assertion function을 호출함, 위에서는 2개의 테스트를 진행함

   - 이는 Arrange, Act, Assert(AAA) testing과 유사함

- Test Name은 설명을 요구함 일반적으로 `subjectUnderTest_actionOrInput_resultState`로 이름을 지음

- 즉 `subjectUnderTest`는 테스트할 메소드나 클래스를 의미하고 `actionOrInput`은 말 그대로 Input이나 action을 의미하며, `resultState`는 예상하는 결과를 의미함, 이 3가지 조합으로 Test Name을 지음

### Writing more tests
- 이제 추가적인 테스트를 진행할 것이고 TDD 기법을 활용할 것임, 이 기법은 feature code를 먼저 쓰는 대신 test를 먼저 작성하는 기법으로 이를 바탕으로 test를 통과한 코드를 바탕으로 feature code를 작성하는 방법임

- 2가지 케이스에 대해서 test를 작성할 수 있음

  - 하나의 task가 완료되고, active한 task가 없다면 `activeTasks`의 퍼센티지는 `0f`가 되고, 완료된 퍼센티지는 `100f`임

  - 만약 2개의 완료된 task가 있고 3개의 active한 task가 있다면 완료한 퍼센티지는 `40f`이고 active 퍼센티지는 `60f`가 됨

- 아래와 같이 에러가 존재하는 코드가 있음, 여기서 list가 비어있거나 null일 때 적절하게 작동되지 않음, 이 때 두 퍼센티지가 0이어야만 함

```kotlin
internal fun getActiveAndCompletedStats(tasks: List<Task>?): StatsResult {

   val totalTasks = tasks!!.size
   val numberOfActiveTasks = tasks.count { it.isActive }
   val activePercent = 100 * numberOfActiveTasks / totalTasks
   val completePercent = 100 * (totalTasks - numberOfActiveTasks) / totalTasks

   return StatsResult(
       activeTasksPercent = activePercent.toFloat(),
       completedTasksPercent = completePercent.toFloat()
   )
  
}
```

- 이 문제를 해결하기 위해서 TDD 방식을 활용할 것임

![one](/Android/img/sixtythree.png)

- 위의 과정은 아래의 흐름을 따름

  - 1.Given,When,Then을 사용해서 test를 진행함

  - 2.test 실패를 함

  - 3.test를 통과하기 위한 최소한의 코드를 작성함

  - 4.위 과정을 반복해서 고쳐나감

- bug를 고치는것으로 시작하는대신 test를 먼저 작성해서 시작을 함, 그리고 이렇게 test를 먼저 작성함으로써, 나중에 이 bug에 대해서 다시 재도입이 되는 경우를 막아줄 수 있음

- emptyList로 테스트를 해보면 에러가 발생할 것임, 그럼 그에 맞게 아래와 같이 버그를 고칠 수 있음, 그러면 테스트를 다시 통과함

```kotlin
internal fun getActiveAndCompletedStats(tasks: List<Task>?): StatsResult {

    return if (tasks == null || tasks.isEmpty()) {
        StatsResult(0f, 0f)
    } else {
        val totalTasks = tasks.size
        val numberOfActiveTasks = tasks.count { it.isActive }
        StatsResult(
            activeTasksPercent = 100f * numberOfActiveTasks / tasks.size,
            completedTasksPercent = 100f * (totalTasks - numberOfActiveTasks) / tasks.size
        )
    }
}
```

- TDD와 test를 먼저 적용함으로써 아래의 사항을 확실히 할 수 있음

   - 새로운 기능은 항상 관련된 테스트를 가짐,, 그래서 test는 코드가 동작하는 바에 대한 document가 될 수 있음

   - 통과한 결과를 바탕으로 test를 확인하고 이미 보여진 것을 통해 bug를 막음

- test코드는 아래와 같음

```kotlin
class StatisticsUtilsTest {

    @Test
    fun getActiveAndCompletedStats_noCompleted_returnsHundredZero {
        val tasks = listOf(
            Task("title", "desc", isCompleted = false)
        )
        // When the list of tasks is computed with an active task
        val result = getActiveAndCompletedStats(tasks)

        // Then the percentages are 100 and 0
        assertThat(result.activeTasksPercent, `is`(100f))
        assertThat(result.completedTasksPercent, `is`(0f))
    }

    @Test
    fun getActiveAndCompletedStats_noActive_returnsZeroHundred() {
        val tasks = listOf(
            Task("title", "desc", isCompleted = true)
        )
        // When the list of tasks is computed with a completed task
        val result = getActiveAndCompletedStats(tasks)

        // Then the percentages are 0 and 100
        assertThat(result.activeTasksPercent, `is`(0f))
        assertThat(result.completedTasksPercent, `is`(100f))
    }

    @Test
    fun getActiveAndCompletedStats_both_returnsFortySixty() {
        // Given 3 completed tasks and 2 active tasks
        val tasks = listOf(
            Task("title", "desc", isCompleted = true),
            Task("title", "desc", isCompleted = true),
            Task("title", "desc", isCompleted = true),
            Task("title", "desc", isCompleted = false),
            Task("title", "desc", isCompleted = false)
        )
        // When the list of tasks is computed
        val result = getActiveAndCompletedStats(tasks)

        // Then the result is 40-60
        assertThat(result.activeTasksPercent, `is`(40f))
        assertThat(result.completedTasksPercent, `is`(60f))
    }

    @Test
    fun getActiveAndCompletedStats_error_returnsZeros() {
        // When there's an error loading stats
        val result = getActiveAndCompletedStats(null)

        // Both active and completed tasks are 0
        assertThat(result.activeTasksPercent, `is`(0f))
        assertThat(result.completedTasksPercent, `is`(0f))
    }

    @Test
    fun getActiveAndCompletedStats_empty_returnsZeros() {
        // When there are no tasks
        val result = getActiveAndCompletedStats(emptyList())

        // Both active and completed tasks are 0
        assertThat(result.activeTasksPercent, `is`(0f))
        assertThat(result.completedTasksPercent, `is`(0f))
    }
}
```

### Setting up a ViewModel Test with AndroidX Test
- 이번에는 가장 많이 쓰이는 요소인 `ViewModel`과 `LiveData`에 대해서 test를 해볼 것임

- 이 부분은 view model에 있는 로직에 집중하는 것임, 여기서 Repository code는 제외함

- repository 부분 자체는 비동기 코드, 데이터베이스, 네트워크 호출등 복잡성을 늘리기 때문에 지양해야함

- 그래서 현재는 피하고 `ViewModel`에 대한 기능을 주로 테스트 할 것임

![one](/Android/img/sixtyfour.png)

- ViewModel에서 아래의 code를 test 할 것임

- 여기서 `Event` 클래스는 하나의 이벤트에 대해서 `LiveData`를 만들어주는 클래스임(이는 `TasksFragment`가 observe함)

```kotlin
fun addNewTask() {
   _newTaskEvent.value = Event(Unit)
}
```

- 테스트 하기 위한 `ViewModel`클래스에 대해서 Generate -> Test를 해서 만듬(이전 `StatisticsUtilTest`와 동일하게)

- 그리고 아래와 같이 test 코드 작성을 준비함

```kotlin
class TasksViewModelTest {

    @Test
    fun addNewTask_setsNewTaskEvent() {

        // Given a fresh TasksViewModel


        // When adding a new task


        // Then the new task event is triggered

    }
    
}
```

- 하지만 여기서 ViewModel을 테스트 하기 위해서는 생성자로 Application context가 필요한데 이 test에서 Activity나 UI나 Fragment에 대한 full application을 만들 순 없음 

- 그래서 이때 AndroidX Test libraries를 통해서 Application이나 Activity에 대해 test를 위한 용도로 컴포넌트를 제공하는 클래스와 메소드를 사용함

- 즉, Android framework class(Application context)등이 필요하면 AndroidX Test를 사용해야함

- 해당 dependency를 추가하고 어노테이션을 붙이고 test 코드를 작성함

- 그리고 이를 적용하기 위해 먼저 test 클래스에 위의 어노테이션을 붙임

```kotlin
@RunWith(AndroidJUnit4::class)
class TasksViewModelTest {
    // Test code
}
```

- 그 다음 AndroidX Test를 위해 Application Context를 아래와 같이 가져옴, 그리고 호출을 

```kotlin
@Test
    fun addNewTask_setsNewTaskEvent() {

        // Given a fresh ViewModel
        val tasksViewModel = TasksViewModel(ApplicationProvider.getApplicationContext())

        // When adding a new task
        tasksViewModel.addNewTask()

        // Then the new task event is triggered
        // TODO test LiveData
    }
```

#### How does AndroidX Test work?
- AndroidX Test는 테스트를 위한 라이브러리의 집합체임, 이는 Application & Activity 등 테스트를 위해 컴포넌트가 주어진 클래스와 메소드를 포함함, 즉 아래와 같이 Application Context를 받아올 수 있음

```kotlin
ApplicationProvider.getApplicationContext()
```

- 이 AndroidX Test API를 쓰는데 유리한 이유는 local test와 instrumented test 둘 다 쓸 수 있음

- 여기서 차이는 Application context를 어떻게 활용하냐의 차이임

   - instrumented test에서는 애뮬레이터나 실제 연결한 기기를 켤 때 제공되는 실제 application context를 받아옴

   - 반면 local test의 경우 안드로이드 환경에서의 사용되는 것으로 쓰임

- Robolectric은 test하기 위한 Android 환경과 애뮬레이터와 실제 기기를 실행하는것보다 더 빠르게 만들어줌, 해당 부분이 없다면 에러가 발생할 수 있음

- test runner는 test를 실행하기 위한 JUnit에서의 컴포넌트임, 이것이 없다면 test가 진행되지 않을 것임

- default test runner를 자동으로 받아서 처리함, 이것을 통해 AndroidX Test와 실제 instrumented 나 local test도 아래와 같이 진행가능함

![one](/Android/img/sixtyfive.png)

### Writing Assertions for LiveData
- `LiveData`를 테스트 하기위해서 2가지를 추천함

   - `InstantTaskExecutorRule`을 사용함

   - `LiveData` observation을 확실히 함

- `InstantTaskExecutorRule`은 JUnit Rule임, `@get:Rule`어노테이션을 쓰게 된다면 test 이전과 이후에 `InstantTaskExecutorRule` 클래스에 있는 몇개의 코드를 시작함

- 이 rule은 같은 스레드에서 모든 아키텍쳐 컴포넌트와 관련된 백그라운드 작업에 실행됨, 그래서 test 결과가 동기적으로 일어나고 반복적으로 처리해줌, LiveData를 테스트 하기 위해서는 해당 test를 작성해야함

- 라이브러리를 먼저 추가함(라이브러리 변경사항이 있어서 공식문서 참고 필요)

```kotlin
testImplementation "androidx.arch.core:core-testing:2.1.0"
```

- 그리고 `TasksViewModelTest` 클래스에 아래와 같이 추가함

```kotlin
class TasksViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    
    // Other code...
}
```

- 그리고 아래와 같이 `LiveData`를 테스트를 위해 추가함

```kotlin
viewModel.resultLiveData.observe(fragment, Observer {
    // Observer code here
})
```

- 이 observation은 매우 중요함 이 observers가 `LiveData`에서 active할 때 `onChanged`이벤트를 그리고 어떠한 Transformations이 필요함

- ViewModel의 `LiveData`을 위해 예상하는 `LiveData`의 상황을 얻기 위해, `LifecycleOwner`와 함께 `LiveData`를 봐야함

- 하지만 여기서 문제가 존재하는데, `TaskViewModel` test에서 `LiveData`를 observe할 activity나 fragment가 없는 문제가 있음, 이를 처리하기 위해서 `observeForever` 메소드를 사용할 것임

- 이를 사용하면 `LifecyclerOwner`가 필요없이 `LiveData`를 계속해서 observe 할 수 있음, 그리고 이를 쓸 때 메모리 누수의 위험으로 반드시 observer를 제거해야만 함

```kotlin
@Test
fun addNewTask_setsNewTaskEvent() {

    // Given a fresh ViewModel
    val tasksViewModel = TasksViewModel(ApplicationProvider.getApplicationContext())


    // Create observer - no need for it to do anything!
    val observer = Observer<Event<Unit>> {}
    try {

        // Observe the LiveData forever
        tasksViewModel.newTaskEvent.observeForever(observer)

        // When adding a new task
        tasksViewModel.addNewTask()

        // Then the new task event is triggered
        val value = tasksViewModel.newTaskEvent.value
        assertThat(value?.getContentIfNotHandled(), (not(nullValue())))

    } finally {
        // Whatever happens, don't forget to remove the observer!
        tasksViewModel.newTaskEvent.removeObserver(observer)
    }
}
```

- 하지만 단일 `LiveData`를 observe 하기에 너무 많은 보일러 플레이트 코드가 있음, 그래서 이를 제거하기 위해서 `LiveDataTestUtil`이라는 확장함수를 만들어서 observer를 더 간단히 만들 수 있게 할 수 있음

- `getOrAwaitValue`라는 코틀린 확장 함수를 사용해서 observer가 추가되거나 `LiveData` 값을 가져올 때나 observer를 제거할 때 쓸 수 있는 메소드를 만듬, 이는 앞서 본 `observerForever`보다 재사용성이 높은 코드임(`LiveData` 테스트를 위해서 해당 메소드를 활용할 수 있음)

```kotlin
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)

    try {
        afterObserve.invoke()

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }

    } finally {
        this.removeObserver(observer)
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}
```

- 그래서 심플하게 아래와 같이 위에서 만든 메소드를 통해서 assert statement를 확인할 수 있음

```kotlin
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.getOrAwaitValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.nullValue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TasksViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    @Test
    fun addNewTask_setsNewTaskEvent() {
        // Given a fresh ViewModel
        val tasksViewModel = TasksViewModel(ApplicationProvider.getApplicationContext())

        // When adding a new task
        tasksViewModel.addNewTask()

        // Then the new task event is triggered
        val value = tasksViewModel.newTaskEvent.getOrAwaitValue()

        assertThat(value.getContentIfNotHandled(), not(nullValue()))


    }

}
```

### Writing multiple ViewModel tests
- 이제 다른 `ViewModel`에서의 테스트를 추가로 진행할 수 있음, 아래와 같이 새로운 테스트를 만듬

```kotlin
@Test
    fun setFilterAllTasks_tasksAddViewVisible() {

        // Given a fresh ViewModel

        // When the filter type is ALL_TASKS

        // Then the "Add task" action is visible
        
    }
```

- 여기서 `TasksFilterType` enum은 모든 task에 대해 `ALL_TASKS`여야함

- 그리고 taks를 추가하기 위한 버튼의 visibility는 `LiveData` `tasksAddViewVisible`로 컨트롤 됨

- 여기서 `tasksViewModel`은 `ApplicationProvider.getApplicationContext`를 사용해서 만듬, 그리고 `setFiltering` 메소드에 `ALL_TASKS` filter type enum을 넘겨줌, `tasksAddViewVisible`을 확인하기 위해 `getOrAwaitValue`메소드를 사용함

```kotlin
@Test
    fun setFilterAllTasks_tasksAddViewVisible() {

        // Given a fresh ViewModel
        val tasksViewModel = TasksViewModel(ApplicationProvider.getApplicationContext())

        // When the filter type is ALL_TASKS
        tasksViewModel.setFiltering(TasksFilterType.ALL_TASKS)

        // Then the "Add task" action is visible
        assertThat(tasksViewModel.tasksAddViewVisible.getOrAwaitValue(), `is`(true))
    }
```

- 앞서 `TasksViewModel` 둘 다 테스트 전에 아래와 같이 씀

```kotlin
// Given a fresh ViewModel
        val tasksViewModel = TasksViewModel(ApplicationProvider.getApplicationContext())
```

- 위와 같이 setup하는 코드가 반복된다면 `@Before`를 활용하여 setup method를 만들고 반복되는 곳을 지움, 지금 같은 경우 모든 test에서 view model이 필요하기 때문에 이를 `@Before` block으로 아래와 같이 옮길 수 있음

```kotlin
// Subject under test
    private lateinit var tasksViewModel: TasksViewModel

    @Before
    fun setupViewModel() {
        tasksViewModel = TasksViewModel(ApplicationProvider.getApplicationContext())
    }
```

- 그러면 아래와 같이 Test Code를 개선할 수 있음

```kotlin
@RunWith(AndroidJUnit4::class)
class TasksViewModelTest {

    // Subject under test
    private lateinit var tasksViewModel: TasksViewModel

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        tasksViewModel = TasksViewModel(ApplicationProvider.getApplicationContext())
    }


    @Test
    fun addNewTask_setsNewTaskEvent() {

        // When adding a new task
        tasksViewModel.addNewTask()

        // Then the new task event is triggered
        val value = tasksViewModel.newTaskEvent.getOrAwaitValue()
        assertThat(
            value?.getContentIfNotHandled(), (not(nullValue()))
        )
    }

    @Test
    fun getTasksAddViewVisible() {

        // When the filter type is ALL_TASKS
        tasksViewModel.setFiltering(TasksFilterType.ALL_TASKS)

        // Then the "Add task" action is visible
        assertThat(tasksViewModel.tasksAddViewVisible.getOrAwaitValue(), `is`(true))
    }
    
}
```