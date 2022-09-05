### Testing Strategy
- 이번에 테스트는 repository와 view model 그리고 test doubles과 dependency inject을 사용하는 fragment를 test 할 것임

#### The Testing Pyramid
- testing 전략을 생각해볼 때, test 측면과 관련되어 3가지를 볼 수 있음

   - Scope : 어느정도의 범주까지 test를 해야하는가? test는 하나의 메소드부터 전체 application까지 그 사이 어딘가로 범위를 잡을 수 있음

   - Speed : test 실행을 빠르게 할 방법이 무엇인지? test 속도는 milli-second부터 몇 분까지 다양함

   - Fidelity : 어떻게 실제앱을 test할지? 만약 test할 코드 일부가 네트워크 요청이 필요하면 실제 네트워크 요청을 해야하는지 아니면 fake result를 써야할 지? 만약 실제 네트워크를 요청하면 이는 fidelity가 높은 축에 속함, 이는 곧 test 실행이 오래걸릴 수 있고 network가 다운되면 error가 생기거나 cost가 높을수도 있음

- 이러한 측면을 고려하는 trade-off가 있음, 예를 들어 speed와 fidelity는 test가 빠르면 fidelity가 적고 그 반대도 성립함, 그래서 자동화된 test를 나누는데 일반적으로 아래의 3개의 카테고리로 나눌 수 있음

   - Unit test

      - 하나의 단일 클래스나 그 클래스 안에 단일 메소드를 테스트 하는데 포커스를 맞춤, Unit test가 실패한 것은 어떤 코드에서 문제가 발생했는지 바로 알 수 있음

      - fidelity는 낮음 왜냐하면 실제 앱에서는 하나의 메소드와 클래스 이상을 실행하기 때문임

      - 코드가 바뀔 때마다 매번 실행을 할만큼 빠름, 대부분 local test로 진행함

      - view model이나 repository의 단일 메소드를 테스트하는 경우가 이에 속함

   - Integration test

      - 이 test는 여러 클래스를 같이 사용했을 때 예상한대로 상호작용을 하는지 확인하는 테스트임

      - structure integration test를 하는 하나의 방법은 task를 저장하는 능력등 하나의 단일 기능으로써 테스트 하는 것임

      - unit test보다 더 넓은 범주의 코드를 테스트 하지만 full fidelity와 대비해 속도를 최적화 할 수는 있음

      - 상황에 따라 이를 locally하게 혹은 instrumentation test로 할 수 있음

      - 단일 fragment나 view model pair의 기능을 모두 테스트할 때가 이에 속함

   - End to end test(E2e)

      - 기능의 조합이 같이 작동하는지 테스트를 함

      - app의 넓은 범주를 test하고, 실제 사용과 가깝게 시뮬레이션 함, 그래서 속도가 느림

      - fidelity가 매우 높고 앱자체가 제대로 전부 작동하는지 말해줌

      - 이 test는 instrumented test로 될 것임

      - 전체 앱을 실행하고 각각 기능을 같이 테스트할 경우 이에 속함

- 위와 같은 테스트의 비율은 아래와 같이 제시함

![one](/Android/img/sixtysix.png)

#### Architecture and Testing
- 앞서 본대로 testing pyramid에 따라 모든 다른 부분에 대해서 앱을 테스트 하는 것은 app의 Architecture와 연관되어 있음

- 예를 들어 구조가 아주 안 좋은 앱의 경우 하나의 메소드 안의 모든 로직이 들어가 있을 것임, 이를 위해 모든 테스트를 다 써야하고 그리고 이는 앱의 거의 모든 부분을 테스트 하게 될 것임, 하지만 이 상황에서 unit과 integration test는?

- 한 곳에 모든 코드를 두는 것은 단일 unit과 기능으로써 코드를 test하는데 어려움이 있음

- 그러므로 각각 독립되게 테스트 할 수 있도록 다양한 메소드와 클래스로 application logic을 나누는 것이 가장 좋은 접근법임

- Architectuer는 code를 나누고 재조합해주고 unit과 integration test를 더 쉽게 해줌

![one](/Android/img/fiftyeight.png)

- 위의 구조를 보고 testing 할 부분을 생각할 수 있음 이를 분리해보면 아래와 같음

   - 먼저 repository에 대한 unit test를 진행함

   - 그리고 view model에 대해 test double을 쓸 것임, view model은 unit testing과 integration testing이 다 필요함

   - 그 다음 fragment와 그와 관련된 view model에 대한 integration test를 할 수 있음

   - 마지막으로 navigation component에 대해 integration test를 진행할 것임

### Make a Fake Data Source
- 클래스의 일부를 unit test를 작성한다면 목표는 오직 해당 클래스에 대한 코드만 test해야하는 것임

- 특정 클래스나 클래스들의 오직 code만 test하는 것은 하기 까다로울 수 있음, 예를 들어 app에 대한 repository를 보면 오직 해당 클래스만을 test 해야하지만 어쩔 수 없이 다른 클래스에 의존하게 되어 있음

- 즉, 해당 repository는 아래와 같이 `LocalTaskDataSource`와 `RemoteTaskDataSource`에 의존관계가 있음, 그래서 모든 메소드가 이러한 data source class의 메소드를 호출하게 됨

![one](/Android/img/sixtyseven.png)

- 예를 들어 아래의 코드를 볼 수 있음, 해당 코드는 repository에서 기본적인 호출 중 하나이지만, 이 메소드는 SQLite database와 network 호출을 만듬, 이는 단순히 repository code 이상의 code를 포함함

```kotlin
suspend fun getTasks(forceUpdate: Boolean = false): Result<List<Task>> {
        if (forceUpdate) {
            try {
                updateTasksFromRemoteDataSource()
            } catch (ex: Exception) {
                return Result.Error(ex)
            }
        }
        return tasksLocalDataSource.getTasks()
    }
```

- 이처럼 repository를 테스트하기 어려운 이유는 다음과 같음

   - repository의 간단한 테스트를 할때조차도 database를 관리하고 생성하는 부분에 대해서 반드시 생각해야함, 이때 이부분을 보면 이것이 local 인지 instrumented test인지 의문이 들게 됨, 그래서 Android의 가상환경을 위해 AndroidX Test를 써야만 함

   - networking code 같은 경우 실행에 오래걸릴 수 있고 때때로 실패하거나 생성이 오래걸리거나 flaky test(같은 코드를 반복적으로 테스트를 위해 실행시 언제는 실패하고 언제는 성공하는 test)일 수 있음

   - 이렇게 되면 test에서 어떤 부분이 실패의 원인이 되는 부분인지 진단하는 능력을 잃어버릴 수 있음, 그래서 해당 테스트는 non-repository code에서부터 시작되야함, 예를 들어 만약 repository의 unit test를 진행한다면 특정 dependant code(database, network)로 인해 테스트가 실패할 수 있음

#### Test Doubles
- 여기서 해결책은 만약 repository를 테스트하게 된다면 실제 networking과 database를 사용하지 않는 것임, 대신 test double을 사용함

- test double은 test를 위해 만든 세부사항의 클래스 버전임, 이는 test에서 실제 클래스 버전과 대체될 수 있음을 의미함

- 이것은 마치 stunt double과 유사함, 스턴트에 전문화된 배우가 실제 배우의 위험한 씬을 대신 찍는것처럼

- 아래의 test double의 타입이 있음

   - Fake : 클래스의 작동하게 적용하는 것임, 하지만 그런 방식으로 적용하는 것은 test에는 적합하지만 production엔 적합하지 않음

   - Mock : 어떤 메소드를 호출한 것인지 추적함, 그럼 어떤 메소드를 정확히 호출했는지를 바탕으로 test를 성공하거나 실패하는지 확인함

   - Stub : 로직이 없고 오직 program이 러턴하는 값만을 포함하는 것, 예를 들어 `StubTaskRepository`는 `getTasks`의 tasks의 확실한 조합의 리턴으로 프로그래밍 되어 있을 수 있음

   - Dummy : 사용하지 않는 것을 넘김, 마치 매개변수로써 넘겨지길 필요하는 것처럼, 만약 `NoOpTaskRepository`가 있다면 어떠한 메소드의 코드 없이 `TaskRepository`를 적용할 것임

   - Spy : 몇몇 추가적인 정보를 추적함, 예를 들어 만약 `SpyTaskRepository`를 만들면 `addTask`메소드가 호출된 횟수를 추적할 것임

- Android에선 대부분 Fakes와 Mocks를 씀, 여기선 `FakeDataSource` test double을 `DefaultTasksRepository`에 대한 실제 data source를 바탕으로 만들 것임

- 아래와 같이 새로 pacakge를 만들고 클래스를 만듬

![one](/Android/img/sixtyeight.png)

- 여기서 이를 다 사용하기 위해서 `TasksDataSource` 인터페이스를 적용하는지 보면 아래와 같음

```kotlin
class TasksLocalDataSource internal constructor(
    private val tasksDao: TasksDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TasksDataSource { ... }

object TasksRemoteDataSource : TasksDataSource { ... }
```

- `FakeDataSource`도 똑같이 해당 인터페이스를 적용하고 멤버를 적용함

```kotlin
package com.example.android.architecture.blueprints.todoapp.data.source

import androidx.lifecycle.LiveData
import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Task

class FakeDataSource : TasksDataSource {
    override fun observeTasks(): LiveData<Result<List<Task>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getTasks(): Result<List<Task>> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshTasks() {
        TODO("Not yet implemented")
    }

    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        TODO("Not yet implemented")
    }

    override suspend fun getTask(taskId: String): Result<Task> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshTask(taskId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun saveTask(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun completeTask(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun completeTask(taskId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun activateTask(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun activateTask(taskId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun clearCompletedTasks() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllTasks() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTask(taskId: String) {
        TODO("Not yet implemented")
    }

}
```

- `FakeDataSource`는 fake라고 불리는 test double의 특정 타입임, 이 때 앞서 설명한대로 Fake타입으로 쓰는 것이고 이 Working implementation은 클래스가 주어진 input에 대해서 실제 output을 생성하는 클래스를 의미함

- 예를 들어 fake data source는 network나 database의 어떠한 것도 저장하는데 쓰이지 않음, 대신에 in-memory list로 쓰는 것임, 이는 마치 예상한대로 작동하는 방식으로 메소드를 get하거나 예상하는 결과를 리턴하게 task를 저장함

- 하지만 실제 production에서는 사용하지 않음, 왜냐하면 이는 서버와 database에 저장하는 것이 아니기 때문임

- 그래서 `FakeDataSource`는 실제 database와 network에 의존하지 않음, 대신 테스트에 충분한 implementation을 줌

- 아래와 같이 클래스를 변경함, 빈 mutable list를 생성자로 만듬, 이 list는 database와 network 응답에 대한 fake task임, 현재 목표는 repository의 `getTasks` 메소드를 테스트 하는 것임

```kotlin
class FakeDataSource(var tasks: MutableList<Task>? = mutableListOf()) : TasksDataSource { // Rest of class }

```

- 아래와 같이 `getTask`와 `deleteAllTasks`와 `saveTask`를 작성함, 이는 이제 실제 local과 remote data source처럼 작동을 하게 

```kotlin
override suspend fun getTasks(): Result<List<Task>> {
    tasks?.let { return Success(ArrayList(it)) }
    return Error(
        Exception("Tasks not found")
    )
}


override suspend fun deleteAllTasks() {
    tasks?.clear()
}

override suspend fun saveTask(task: Task) {
    tasks?.add(task)
}
```
```kotlin
import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Result.Error
import com.example.android.architecture.blueprints.todoapp.data.Result.Success
import com.example.android.architecture.blueprints.todoapp.data.Task
```

### Write a Test Using Dependency Injection
- 이번에는 수동적인 DI 기술을 써서 아까 만든 fake test double을 활용할 것임

- 여기서 주요 사항은 `FakeDataSource`에 대해서 오직 테스트를 위해서 `TasksRemoteDataSource`와 `TasksLocalDataSource`를 대체해야함

- 이 두개 모두 `DefaultTasksRepository`의 의존관계인데 이는 해당 repository를 실행하기 위해서 이 두 클래스가 필요함을 의미함

- 실제 해당 Repository 코드에서 보면 `init`에 생성자로 이 의존성을 추가하고 할당함, 이는 완전 하드코딩된 사안이라 test double로 치환하기 힘듬

- 대신, 이렇게 아예 하드코딩으로 클래스를 제공해주는 대신 dependency injection으로 dependencies를 제공하는 방향으로도 쓸 수 있음, 이는 생성자에 해당 의존성을 주입하기 때문에 test double에도 쓰기 용이함

![one](/Android/img/sixtynine.png)

- 우선 아래와 같이, `DefaultTasksRepository`의 생성자를 변경해줌

```kotlin
// REPLACE
class DefaultTasksRepository private constructor(application: Application) { // Rest of class }

// WITH

class DefaultTasksRepository(
    private val tasksRemoteDataSource: TasksDataSource,
    private val tasksLocalDataSource: TasksDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) { // Rest of class }
```

- 그러면 생성자로 의존성을 주입했기 때문에 더이상 `init`에서의 초기화 작업도 필요없어짐, 아예 없애도 됨, 그리고 기존에 클래스 내에 정의한 값도 삭제함

- 마지막으로 `getRepository`의 경우 아래와 같이 갱신할 수 있음

```kotlin
companion object {
        @Volatile
        private var INSTANCE: DefaultTasksRepository? = null

        fun getRepository(app: Application): DefaultTasksRepository {
            return INSTANCE ?: synchronized(this) {
                val database = Room.databaseBuilder(app,
                    ToDoDatabase::class.java, "Tasks.db")
                    .build()
                DefaultTasksRepository(TasksRemoteDataSource, TasksLocalDataSource(database.taskDao())).also {
                    INSTANCE = it
                }
            }
        }
    }
```

- 그러면 위와 같이 constructor dependency injection을 사용하게 됨

- 이제 생성자 주입 패턴을 썼기 떄문에 `DefaultTasksRepository`에 대해서 fake data source를 사용할 수 있게됨, 앞서한 방식과 동일하게 test 클래스를 만들고 아래와 같이 해당 클래스의 데이터에 해당하는 멤버 변수를 추가함

```kotlin
    private val task1 = Task("Title1", "Description1")
    private val task2 = Task("Title2", "Description2")
    private val task3 = Task("Title3", "Description3")
    private val remoteTasks = listOf(task1, task2).sortedBy { it.id }
    private val localTasks = listOf(task3).sortedBy { it.id }
    private val newTasks = listOf(task3).sortedBy { it.id }
```

- 그리고 2개의 `FakeDataSource`의 멤버 변수와 테스트를 위한 `DefaultTasksRepository` 변수를 만듬

```kotlin
    private lateinit var tasksRemoteDataSource: FakeDataSource
    private lateinit var tasksLocalDataSource: FakeDataSource

    // Class under test
    private lateinit var tasksRepository: DefaultTasksRepository
```

- 그리고 Repository의 함수를 불러와서 data source를 사용하기 위해 인스턴스화를 하는 부분을 `@Before`로 사전 세팅함

```kotlin
    @Before
    fun createRepository() {
        tasksRemoteDataSource = FakeDataSource(remoteTasks.toMutableList())
        tasksLocalDataSource = FakeDataSource(localTasks.toMutableList())
        // Get a reference to the class under test
        tasksRepository = DefaultTasksRepository(
            // TODO Dispatchers.Unconfined should be replaced with Dispatchers.Main
            //  this requires understanding more about coroutines + testing
            //  so we will keep this as Unconfined for now.
            tasksRemoteDataSource, tasksLocalDataSource, Dispatchers.Unconfined
        )
    }
```

- 이제 test 코드를 작성할 때며, 아래와 같이 `getTasks` 메소드를 확인할 것임

```kotlin
@Test
    fun getTasks_requestsAllTasksFromRemoteDataSource(){
        // When tasks are requested from the tasks repository
        val tasks = tasksRepository.getTasks(true) as Success

        // Then tasks are loaded from the remote data source
        assertThat(tasks.data, IsEqual(remoteTasks))
    }
```

- 이 때, 에러가 뜰 것인데 이는 해당 함수를 호출하는데 있어서 Coroutine scope가 있어야 하기 때문에 아래와 같이 `testImplementation`으로 coroutine을 추가함

```kotlin
testImplementation "org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion"
```

- 해당 라이브러리는 testing coroutine을 위한 coroutine test 라이브러리임, 테스트를 위해서 `runBlockingTest` 함수를 사용할 것임

- 이는 coroutine test 라이브러리에서 제공하는 함수임, 해당 범위 안에서 특별한 coroutine context를 통해서 코드 블럭이 처리가됨, 이는 진짜 coroutine을 쓰는 것은 아니지만, testing code를 위해서 사용을 하는 것임

- `suspend`함수를 호출하기 위해서 test class에서 `runBlockingTest`를 사용할 것임

- 그 전에 먼저 해당 test class에서 `@ExperimentalCoroutinesApi`를 씀, 이는 `runBlockingTest`를 해당 클래스에서 쓴다는 것을 의미함, 그리고 해당 함수에 `runBlockingTest`를 추가함

```kotlin
import com.example.android.architecture.blueprints.todoapp.data.Result.Success
import com.example.android.architecture.blueprints.todoapp.data.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.IsEqual
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test


@ExperimentalCoroutinesApi
class DefaultTasksRepositoryTest {

    private val task1 = Task("Title1", "Description1")
    private val task2 = Task("Title2", "Description2")
    private val task3 = Task("Title3", "Description3")
    private val remoteTasks = listOf(task1, task2).sortedBy { it.id }
    private val localTasks = listOf(task3).sortedBy { it.id }
    private val newTasks = listOf(task3).sortedBy { it.id }

    private lateinit var tasksRemoteDataSource: FakeDataSource
    private lateinit var tasksLocalDataSource: FakeDataSource

    // Class under test
    private lateinit var tasksRepository: DefaultTasksRepository

    @Before
    fun createRepository() {
        tasksRemoteDataSource = FakeDataSource(remoteTasks.toMutableList())
        tasksLocalDataSource = FakeDataSource(localTasks.toMutableList())
        // Get a reference to the class under test
        tasksRepository = DefaultTasksRepository(
            // TODO Dispatchers.Unconfined should be replaced with Dispatchers.Main
            //  this requires understanding more about coroutines + testing
            //  so we will keep this as Unconfined for now.
            tasksRemoteDataSource, tasksLocalDataSource, Dispatchers.Unconfined
        )
    }

    @Test
    fun getTasks_requestsAllTasksFromRemoteDataSource() = runBlockingTest {
        // When tasks are requested from the tasks repository
        val tasks = tasksRepository.getTasks(true) as Success

        // Then tasks are loaded from the remote data source
        assertThat(tasks.data, IsEqual(remoteTasks))
    }

}
```

### Set up a Fake Repository
- 앞서 본 방식은 repository로 unit test를 어떻게 하는지 본 것임, 이번엔 view model에 대해서 어떻게 unit과 integration test를 할 것인지 볼 것임

- unit test는 오직 해당 클래스나 메소드에 대해서만 test를 진행해야함, 그래서 `ViewModel`테스트에 대해서도 database, network, repository 클래스에 대해서는 test를 하지 않았음

- 그래서 앞서 위에서 본 Repository에서 했듯이 view model의 테스트를 위해 fake repository를 만들고  dependency injection을 적용할 것임

![one](/Android/img/seventy.png)

- 이때 fake와 real class 사이에서 공유하는 일반적인 인터페이스를 만들어서 생성자 의존성 주입을 사용할 것임

- 앞서 `TasksRemoteDataSource`, `TasksLocalDataSource`와 `FakeDataSource`는 동일한 인터페이스인 `TasksDataSource`를 씀, 그래서 `DefaultTasksRepository`에서 `TasksDataSource`를 쓸 수 있었던 것임

- 그래서 `DefaultTasksRepository`에 대해서 data source에서 했던것처럼 모든 public 메소드를 포함하는 interface를 만들것임, 아래와 같이 만들 수 있음

![one](/Android/img/seventyone.png)

- 여기서 2개의 companion 멤버와 private 메소드를 제외하고 모두 체크를 함, 그리고 refactor를 하면 아래와 같이 생성될 것임, 그러면 이제 `DefaultTasksRepository`는 `TasksRepository`를 implement했을 것임

![one](/Android/img/seventytwo.png)

![one](/Android/img/seventythree.png)

- 이제 `DefaultTasksRepository`에 대해 test double이 가능해짐, 아래와 같이 `FakeTestRepository`를 만듬, 그리고 멤버를 implement를 함

```kotlin
class FakeTestRepository : TasksRepository  {
}
```

- `FakeTestRepository`에선 implement가 안 된 메소드를 가지고 있음, `FakeDataSource`에서처럼 `FakeTestRepository`에서 자료구조를 통해 백업을 할 것임, 실제 local & remote data source를 다루지 않고

- 아래와 같이 변수를 추가함(`LinkedHashMap`변수는 현재 list의 task를 나타내고 `MutableLiveData`는 observable task를 위해서 씀)

```kotlin
    var tasksServiceData: LinkedHashMap<String, Task> = LinkedHashMap()

    private val observableTasks = MutableLiveData<Result<List<Task>>>()
```

- 각각 메소드를 아래와 같이 적용할 것임

   - `getTasks`의 경우, `tasksServiceData`를 가지고 `tasksServiceData.values.toList()`를 통해 list로 바꿈, 그리고 `Success` result를 리턴함

   - `refreshTasks`는 `getTasks()`가 리턴하는 것이 무엇인지 보기 위해 `observableTasks`의 값을 갱신함

   - `observeTasks`는 `runBlocking`을 통해 coroutine을 쓰고 `refreshTasks`를 실행함, 그러면 `observableTasks`를 리턴함

- 여기서 test double을 위해 `runBlocking`을 사용함, 이는 repository에서 실제 적용하는 것과 가깝게 시뮬레이션 하는 것임 `Fakes`에선 `runBlockingTest`를 사용했음

```kotlin
class FakeTestRepository : TasksRepository {

    var tasksServiceData: LinkedHashMap<String, Task> = LinkedHashMap()

    private val observableTasks = MutableLiveData<Result<List<Task>>>()

    override suspend fun getTasks(forceUpdate: Boolean): Result<List<Task>> {
        return Result.Success(tasksServiceData.values.toList())
    }

    override suspend fun refreshTasks() {
        observableTasks.value = getTasks()
    }

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        runBlocking { refreshTasks() }
        return observableTasks
    }

    override suspend fun completeTask(task: Task) {
       val completedTask = task.copy(isCompleted = true)
       tasksServiceData[task.id] = completedTask
       refreshTasks()
     }

    // Rest of class

}
```

- 이 때 미리 task가 repository에 있는게 쉽기 때문에 `saveTask`를 여러번 호출할 수 있지만 더 쉽게 하기 위해 `addTasks` 메소드를 추가함

```kotlin
fun addTasks(vararg tasks: Task) {
        for (task in tasks) {
            tasksServiceData[task.id] = task
        }
        runBlocking { refreshTasks() }
    }
```

### Use the Fake Repository inside a ViewModel
- 이번엔 `ViewModel`에서 fake class를 사용할 것임, 생성자 의존성 주입 패턴을 통해서 `TasksRepository`의 변수를 추가할 것임

- 먼저 아래와 같이 `ViewModel`에서 생성자 안에 `TasksRepository`를 추가함

```kotlin
// REPLACE
class TasksViewModel(application: Application) : AndroidViewModel(application) {

    private val tasksRepository = DefaultTasksRepository.getRepository(application)

    // Rest of class
}

// WITH

class TasksViewModel( private val tasksRepository: TasksRepository ) : ViewModel() { 
    // Rest of class 
}
```

- 생성자를 바꿨기 때문에, `TasksViewModel`을 생성하기 위한 factory가 필요함, 원래는 분리를 해야하지만 편의를 위해서 `TasksViewModel`에 추가함

- 아래와 같이 `TasksViewModel` 클래스 외부에 Factory를 추가함, 이제 view model을 어디서든 만들 수 있음

```kotlin
@Suppress("UNCHECKED_CAST")
class TasksViewModelFactory (
    private val tasksRepository: TasksRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (TasksViewModel(tasksRepository) as T)
}
```

- 이제 `TasksFragment`에서 아래와 같이 factory를 사용하여 수정함

```kotlin
// REPLACE
private val viewModel by viewModels<TasksViewModel>()

// WITH

private val viewModel by viewModels<TasksViewModel> {
    TasksViewModelFactory(DefaultTasksRepository.getRepository(requireActivity().application))
}
```

- `TasksViewModelTest`에 `FakeTestRepository`를 추가함

```kotlin
@RunWith(AndroidJUnit4::class)
class TasksViewModelTest {

    // Use a fake repository to be injected into the viewmodel
    private lateinit var tasksRepository: FakeTestRepository
    
    // Rest of class
}
```

- 그리고 `setupViewModel`을 3개의 task를 가진 `FakeTestRepository`를 만들고 repository에서 `tasksViewModel`을 생성함

```kotlin
@Before
    fun setupViewModel() {
        // We initialise the tasks to 3, with one active and two completed
        tasksRepository = FakeTestRepository()
        val task1 = Task("Title1", "Description1")
        val task2 = Task("Title2", "Description2", true)
        val task3 = Task("Title3", "Description3", true)
        tasksRepository.addTasks(task1, task2, task3)

        tasksViewModel = TasksViewModel(tasksRepository)
        
    }
```

- 이제 더 이상 AndroidXTest를 쓸 필요가 없기 때문에 `@RunWith(AndroidJUnit4::class)`를 제거해도 됨, 그래도 테스트는 진행이 됨

- 그리고 똑같이 `TasksDetailFragment`와 `TasksDetailViewModel`에도 적용을 함

```kotlin
// REPLACE
class TaskDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val tasksRepository = DefaultTasksRepository.getRepository(application)

    // Rest of class
}

// WITH

class TaskDetailViewModel(
    private val tasksRepository: TasksRepository
) : ViewModel() { // Rest of class }
```

- 그리고 factory도 아래와 같이 만들고 Fragment 역시 수정함

```kotlin
@Suppress("UNCHECKED_CAST")
class TaskDetailViewModelFactory (
    private val tasksRepository: TasksRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (TaskDetailViewModel(tasksRepository) as T)
}
```
```kotlin
// REPLACE
private val viewModel by viewModels<TaskDetailViewModel>()

// WITH

private val viewModel by viewModels<TaskDetailViewModel> {
    TaskDetailViewModelFactory(DefaultTasksRepository.getRepository(requireActivity().application))
}
```

- 이제 `TasksFragment`와 `TaskDetailFragment`에서 실제 repository를 쓰지 않고 `FakeTestRepository`를 사용할 수 있음

### Launch a Fragment from a Test
- 이제 fragment와 view-model 상호작용을 테스트 하는 integration test를 할 것임, 이 때 view model 코드가 정확하게 해당하는 UI를 갱신하는 확인할 것임

- 이를 활용하기 위해 ServiceLocator pattern과 Espresso와 Mockito 라이브러리를 쓸 것임

- Integration test는 같이 사용했을 때 예상한대로 실행이 되는지 확실히 하기 위해 몇개의 클래스들간의 상호작용을 테스트 하는 것임, 이 test는 locally(`test` soruce set) 뿐 아니라 instrumentation test(`androidTest` source set)에서도 실행함

- 이 상황에선 각각 fragment와 fragment에서의 메인 기능을 테스트 하기 위한 fragment와 view model에 대한 intergration test를 쓸 것임

- 먼저 아래의 의존성 추가를 함

   - `junit:junit`의 경우 기본적인 test statement를 위해 필요함

   - `androidx.test:core`는 Core AndroidX 테스트 라이브러리임

   - `kotlinx-coroutine-test`는 Coroutine 테스트 라이브러리임

   - `androidx.fragment:fragment-testing`의 경우 상태를 테스트하고 변화에 대해서 fragment를 만드는 AndroidX test 라이브러리

```kotlin
    // Dependencies for Android instrumented unit tests
    androidTestImplementation "junit:junit:$junitVersion"
    androidTestImplementation "org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion"

    // Testing code should not be included in the main code.
    // Once https://issuetracker.google.com/128612536 is fixed this can be fixed.

    implementation "androidx.fragment:fragment-testing:$fragmentVersion"
    implementation "androidx.test:core:$androidXTestCoreVersion"
```

- `TaskDetailFragment`는 단일 task에 대한 정보를 보여줌, 해당 fragment에 대해서 test를 진행할 것임

- 동일하게 test를 추가하는데 이 때 default에 `androidTest` source set으로 둠

- 이때 `androidTest` source set에 넣은 이유는 Fragment가 시각적인 UI를 만들기 때문임, 그래서 test를 할 때도 앱이 실행할 때 screen의 render 하는 것을 도와줘야함, 그래서 fragment를 테스트 할 때, `android` source set에서 instrumented test를 작성해야함

- 일반적으로 시각적인 무엇인가를 testing하기 위해선 instrumented test에서 실행을 함, 아래와 같이 설정함

   - `@MediumTest`는 medium run-time integration test임을 의미함(`@SmallTest`는 unit test이고 `@LargeTest`는 end-to-end test임), 이는 어떤 test의 크기를 실행하기 나을지 그룹핑하고 선택하는데 도움을 줌

   - `@RunWith(AndroidJUnit4::class)`의 경우 AndroidX Test를 쓰는 어떤 클래스든 사용함

```kotlin
@MediumTest
@RunWith(AndroidJUnit4::class)
class TaskDetailFragmentTest {

}
```

- AndroidX Test 라이브러리를 사용해 `TaskDetailFragment`를 실행시킬 수 있음

- `FragmentScenario`는 fragment로써 테스트를 위해 해당 생명주기를 직접적으로 컨트롤 할 수 있게 해주는 클래스임

- fragment를 테스트 하기 위해 `FragmentScenario`를 생성할 것임

- 아래와 같이 작성할 수 있음, 이는 task를 만들고 task를 fragment로 넘겨주기 위한 fragment argument로 나타내는 `Bundle`을 만들고, `FragmentScenario`를 만드는 `launchFragmentInContainer`함수를 만듬(bundle과 theme와 함께)

- theme이 필요한 이유는 해당 theme을 적용하지 않으면 아예 빈화면으로 시작이 될 것임

```kotlin
    @Test
    fun activeTaskDetails_DisplayedInUi() {
        // GIVEN - Add active (incomplete) task to the DB
        val activeTask = Task("Active Task", "AndroidX Rocks", false)

        // WHEN - Details fragment launched to display task
        val bundle = TaskDetailFragmentArgs(activeTask.id).toBundle()
        launchFragmentInContainer<TaskDetailFragment>(bundle, R.style.AppTheme)

    }
``` 

- 아직 완성은 안된것이지만 이제 실행을 하면 실제 기기나 애뮬레이터에서 테스트가 진행되고 fragment가 실행될 것임, 여기서 아직 아무것도 assert를 하지 않아서 무엇인가 뜨진 않을 것임

- 그리고 repository에 저장을 안해서 task를 만들어도 나타나지 않음, 여기서 이를 실제 repository를 fake로 바꿀 필요가 있음


### Make a ServiceLocator
- 여기서 fragment에 fake repository를 `ServiceLocator`를 통해 제공해줄 수 있음

- 이를 통해 fragment와 view model integration test를 위한 코드 작성이 가능해짐

- 여기선 앞서 했듯이 생성자 의존성 주입이 불가능함, 그래서 view model과 repository를 못 씀

- 생성자 의존성 주입은 클래스에 생성자가 필요함, Fragment와 Activity는 생성자가 필요없고 생성자의 접근하지 않게 하는 예시임

- 그렇기 때문에 fragment에 생성자를 만들 필요가 없고, fragment를 위해 repository test double을 생성자 의존성 주입으로 사용할 수 없음

- 대신 Service Locator 패턴을 사용할 수 있음, 이는 Dependency Injection을 대신할 수 있는 패턴임

- 이는 Service Locator라는 싱글톤 클래스를 만들어서 의존성 주입을 위한 목적으로 씀, 이는 실제와 test code 둘 다를 위해서 쓰임

- 일반적인 상황말고 test에서는 Service Locator를 통해 의존성의 test double을 제공해줌

![one](/Android/img/seventyfour.png)

- 이를 위해 아래의 과정을 거침

   - 1.repository를 생성하고 저장할 수 있는 Service Locator 클래스를 만듬, 기본적으론 기본 repository를 만듬

   - 2.Service Locator를 사용해 repository가 필요한 상황을 리팩토링함

   - 3.testing class에선 Service Locator 메소드를 호출해 기본 repository를 test double로 교체함

- `ServiceLocator` 클래스는 main source에 만듬, 이는 application code이므로 top-level에 클래스를 만들고 싱글톤으로 만듬(`Volatile`은 캐싱이 되지 않게 해주는 것임, 그래서 싱글톤을 유지가능해짐)

```kotlin
object ServiceLocator {

    private var database: ToDoDatabase? = null
    @Volatile
    var tasksRepository: TasksRepository? = null

}
```

- 이제 여기서 `ServiceLocator`에서 `TasksRepository`를 리턴하게끔 하는 것만 처리하면 됨, 이는 이미 존재하는 `DefaultTasksRepository`를 리턴하거나 혹은 새로운 `DefaultTasksRepository`를 필요하면 생성하거나 리턴할 것임

- 아래의 함수를 정의함

   - `provideTasksRepository` : 기존에 존재하는 repository를 제공해주거나 새로운 것을 만듬, 이 메소드는 멀티 스레드가 돌아가서 2개의 repository 인스턴스를 만드는걸 방지하기 위해 `synchronized(this)`가 되야함

   - `createTasksRepository` : 새로운 repository를 만드는 코드임, `createTaskLocalDataSource`를 호출하고 새로운 `TasksRemoteDataSource`를 만들것임

   - `createTaskLocalDataSource` : 새로운 local data source를 만드는 코드임, `createDataBase`를 호출할 것임

   - `createDataBase` : 새로운 database를 만드는 코드임

```kotlin
object ServiceLocator {

    private var database: ToDoDatabase? = null
    @Volatile
    var tasksRepository: TasksRepository? = null

    fun provideTasksRepository(context: Context): TasksRepository {
        synchronized(this) {
            return tasksRepository ?: createTasksRepository(context)
        }
    }

    private fun createTasksRepository(context: Context): TasksRepository {
        val newRepo = DefaultTasksRepository(TasksRemoteDataSource, createTaskLocalDataSource(context))
        tasksRepository = newRepo
        return newRepo
    }

    private fun createTaskLocalDataSource(context: Context): TasksDataSource {
        val database = database ?: createDataBase(context)
        return TasksLocalDataSource(database.taskDao())
    }

    private fun createDataBase(context: Context): ToDoDatabase {
        val result = Room.databaseBuilder(
            context.applicationContext,
            ToDoDatabase::class.java, "Tasks.db"
        ).build()
        database = result
        return result
    }
}
```

- `ServiceLocator`에서만 repository를 만들도록 main application code를 변경할 것임 

- repository class의 인스턴스를 오직 한 곳에만 만들기 위해서 TodoApplication 클래스에 Service Locator를 만들것임

- 해당 폴더 안에 repository를 만들고 할당시켜줄 수 있음

```kotlin
class TodoApplication : Application() {

    val taskRepository: TasksRepository
        get() = ServiceLocator.provideTasksRepository(this)

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(DebugTree())
    }
}
```

- 그리고 `DefaultTaskRepository`에 있는 companion object를 삭제함

```kotlin
// DELETE THIS COMPANION OBJECT
companion object {
    @Volatile
    private var INSTANCE: DefaultTasksRepository? = null

    fun getRepository(app: Application): DefaultTasksRepository {
        return INSTANCE ?: synchronized(this) {
            val database = Room.databaseBuilder(app,
                ToDoDatabase::class.java, "Tasks.db")
                .build()
            DefaultTasksRepository(TasksRemoteDataSource, TasksLocalDataSource(database.taskDao())).also {
                INSTANCE = it
            }
        }
    }
}
```

- 그러면 이제 어디서든 `getRepository`를 통해 만든대신, application에서의 `taskRepository`를 쓸 수 있음

- 이러면 직접 repository를 만드는 대신, 어디서든지 `ServiceLocator`가 제공하는 repository를 얻을 수 있음

- 이제 Fragment에서 `getRepository` 부분을 아래와 같이 바꿈

```kotlin
// REPLACE this code
private val viewModel by viewModels<TaskDetailViewModel> {
    TaskDetailViewModelFactory(DefaultTasksRepository.getRepository(requireActivity().application))
}

// WITH this code

private val viewModel by viewModels<TaskDetailViewModel> {
    TaskDetailViewModelFactory((requireContext().applicationContext as TodoApplication).taskRepository)
}
```
```kotlin
// REPLACE this code
    private val viewModel by viewModels<TasksViewModel> {
        TasksViewModelFactory(DefaultTasksRepository.getRepository(requireActivity().application))
    }


// WITH this code

    private val viewModel by viewModels<TasksViewModel> {
        TasksViewModelFactory((requireContext().applicationContext as TodoApplication).taskRepository)
    }
```

- 추가로 `ViewModel`에서도 repository 부분을 아래와 같이 바꿈

```kotlin
// REPLACE this code
    private val tasksRepository = DefaultTasksRepository.getRepository(application)



// WITH this code

    private val tasksRepository = (application as TodoApplication).taskRepository

```

- 단순히 코드만 리팩토링 한 것이므로 에러가 생기지 않을것임

- 그리고 `test`와 `androidTest` source set사이에 test class가 공유되면 안됨, 그러므로 `FakeTestRepository`를 복사해서 `androidTest` source set에 저장해야함

- 그래서 동일하게 만들고 아래의 코드를 추가함

```kotlin
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Result.Error
import com.example.android.architecture.blueprints.todoapp.data.Result.Success
import com.example.android.architecture.blueprints.todoapp.data.Task
import kotlinx.coroutines.runBlocking
import java.util.LinkedHashMap


class FakeAndroidTestRepository : TasksRepository {

    var tasksServiceData: LinkedHashMap<String, Task> = LinkedHashMap()

    private var shouldReturnError = false

    private val observableTasks = MutableLiveData<Result<List<Task>>>()

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun refreshTasks() {
        observableTasks.value = getTasks()
    }

    override suspend fun refreshTask(taskId: String) {
        refreshTasks()
    }

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        runBlocking { refreshTasks() }
        return observableTasks
    }

    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        runBlocking { refreshTasks() }
        return observableTasks.map { tasks ->
            when (tasks) {
                is Result.Loading -> Result.Loading
                is Error -> Error(tasks.exception)
                is Success -> {
                    val task = tasks.data.firstOrNull() { it.id == taskId }
                        ?: return@map Error(Exception("Not found"))
                    Success(task)
                }
            }
        }
    }

    override suspend fun getTask(taskId: String, forceUpdate: Boolean): Result<Task> {
        if (shouldReturnError) {
            return Error(Exception("Test exception"))
        }
        tasksServiceData[taskId]?.let {
            return Success(it)
        }
        return Error(Exception("Could not find task"))
    }

    override suspend fun getTasks(forceUpdate: Boolean): Result<List<Task>> {
        if (shouldReturnError) {
            return Error(Exception("Test exception"))
        }
        return Success(tasksServiceData.values.toList())
    }

    override suspend fun saveTask(task: Task) {
        tasksServiceData[task.id] = task
    }

    override suspend fun completeTask(task: Task) {
        val completedTask = Task(task.title, task.description, true, task.id)
        tasksServiceData[task.id] = completedTask
    }

    override suspend fun completeTask(taskId: String) {
        // Not required for the remote data source.
        throw NotImplementedError()
    }

    override suspend fun activateTask(task: Task) {
        val activeTask = Task(task.title, task.description, false, task.id)
        tasksServiceData[task.id] = activeTask
    }

    override suspend fun activateTask(taskId: String) {
        throw NotImplementedError()
    }

    override suspend fun clearCompletedTasks() {
        tasksServiceData = tasksServiceData.filterValues {
            !it.isCompleted
        } as LinkedHashMap<String, Task>
    }

    override suspend fun deleteTask(taskId: String) {
        tasksServiceData.remove(taskId)
        refreshTasks()
    }

    override suspend fun deleteAllTasks() {
        tasksServiceData.clear()
        refreshTasks()
    }

   
    fun addTasks(vararg tasks: Task) {
        for (task in tasks) {
            tasksServiceData[task.id] = task
        }
        runBlocking { refreshTasks() }
    }
}
```

- 이제 test를 위해서 `ServiceLocator`를 사용해야함, 그러기 위해서 아래의 코드를 추가해줘야함, 해당 어노테이션은 test를 위해 setter가 public이 된다고 설명해주는 것임

- test를 하는 시점에는 다른 것에 의존성 되어 시작이 되면 안됨, `ServiceLocator`는 싱글톤이라 그럴 가능성이 있기 때문에 이를 방지하고자 해당 상태를 리셋하는 메소드를 별도로 만들어주는게 좋음

```kotlin
    @Volatile
    var tasksRepository: TasksRepository? = null
        @VisibleForTesting set
```

- 그러기 위해서 `lock`이라는 인스턴스 변수를 추가함

```kotlin
private val lock = Any()
```

- 그 후, `resetRepository`라고 볼리는 test에 특화된 메소드를 추가함, 이 메소드는 database와 repository를 초기화시켜주는 메소드임

```kotlin
    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
            runBlocking {
                TasksRemoteDataSource.deleteAllTasks()
            }
            // Clear all data to avoid test pollution.
            database?.apply {
                clearAllTables()
                close()
            }
            database = null
            tasksRepository = null
        }
    }
```

- 이제 `TaskDetailFragmentTest`에서 `ServiceLocator`를 쓸 수 있음

- 아래와 같이 변수 선언과 repository를 setup하고 사후처리를 하는 메소드를 추가함

```kotlin
    private lateinit var repository: TasksRepository

    @Before
    fun initRepository() {
        repository = FakeAndroidTestRepository()
        ServiceLocator.tasksRepository = repository
    }

    @After
    fun cleanupDb() = runBlockingTest {
        ServiceLocator.resetRepository()
    }
```

- 그리고 이제 이전에 했던 test코드에 테스트를 추가함, 그러면 이제 Detail에 대한 부분의 테스트가 진행되어 완료됨

```kotlin
    @Test
    fun activeTaskDetails_DisplayedInUi()  = runBlockingTest{
        // GIVEN - Add active (incomplete) task to the DB
        val activeTask = Task("Active Task", "AndroidX Rocks", false)
        repository.saveTask(activeTask)

        // WHEN - Details fragment launched to display task
        val bundle = TaskDetailFragmentArgs(activeTask.id).toBundle()
        launchFragmentInContainer<TaskDetailFragment>(bundle, R.style.AppTheme)

    }
```

### Writing your first Integration Test with Espresso
- Integration Test를 위해 Espresso testing 라이브러리를 사용할 것임, 이 라이브러리는

   - button 클릭, sliding a bar, 스크롤 하는 것 등 view와 상호작용함

   - screen에 특정 view가 있는지 혹은 특정 상태인지 확인함(특정 text가 포함되어 있는지, checkbox가 체크되어 있는지 등)

- 기본적으로 의존성이 추가되어 있을 것임

- Espresso test는 실제 기기에서 실행하기 때문에 instrumentation을 자연스럽게 처리함

- 이때 주의할 점이 animation임 왜냐하면 view에 test를 하면서 animation lag가 있으면 animation은 여전히 진행중이라면 test를 실패할 수 있음, 이는 아예 test를 다른 방향으로 이끌 수 있음

- 그래서 해당 기기의 animation을 끄는게 좋음(개발자 옵션 통해서)

- 들어가기 앞서 Espresso code와 함께 테스트를 보면 아래와 같음

```kotlin
onView(withId(R.id.task_detail_complete_checkbox)).perform(click()).check(matches(isChecked()))
```

- 위의 코드는 checkbox의 view를 id로 찾은뒤, 클릭을 한 다음, 체크가 됐는지 assert하는 것임

- Espresso statement는 4개의 파트로 구성됨

   - `Static Espresso Method` : `onView`는 Espresso statement를 시작하는 static Espresso method임, `onView`는 가장 일반적인 것 중 하나로 다른 옵션으로는 `onData`가 있음

   - `ViewMatcher` : `withId`가 `ViewMatcher`의 예시로 해당 `ID`의 view를 가져오는 것임, 다른 view matchers 역시 존재함

   - `ViewAction` : `perform`은 `ViewAction` 중 하나임, `ViewAction`은 view가 완료한 것에 대한 것을 나타냄, 예를 들어 view를 클릭하는 등

   - `ViewAssertion` : `check`는 `ViewAssertion`을 가진 것임, `ViewAssertion`은 view에 대해 무언가를 체크하거나 assert함, 가장 일반적인 것은 `matches` assertion을 사용할 것임, assertion을 끝내기 위해 다른 `ViewMatcher`인 `isChecked`를 씀

![one](/Android/img/seventyfive.png)

- 반드시 `perform`과 `check` 두 개를 통해서 Espresso statement를 확인할 필요는 없음

- 이제 Fragment에서 아래와 같이 테스트 해볼 수 있음, `THEN` 코멘트 이후에는 Espresso를 씀, test 구조를 탐색하고, `withId`를 사용해 어떻게 detail page가 보여야 하는지에 대해서 assertions을 만들고 확인함

```kotlin
    @Test
    fun activeTaskDetails_DisplayedInUi() = runBlockingTest{
        // GIVEN - Add active (incomplete) task to the DB
        val activeTask = Task("Active Task", "AndroidX Rocks", false)
        repository.saveTask(activeTask)

        // WHEN - Details fragment launched to display task
        val bundle = TaskDetailFragmentArgs(activeTask.id).toBundle()
        launchFragmentInContainer<TaskDetailFragment>(bundle, R.style.AppTheme)

        // THEN - Task details are displayed on the screen
        // make sure that the title/description are both shown and correct
        onView(withId(R.id.task_detail_title_text)).check(matches(isDisplayed()))
        onView(withId(R.id.task_detail_title_text)).check(matches(withText("Active Task")))
        onView(withId(R.id.task_detail_description_text)).check(matches(isDisplayed()))
        onView(withId(R.id.task_detail_description_text)).check(matches(withText("AndroidX Rocks")))
        // and make sure the "active" checkbox is shown unchecked
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(isDisplayed()))
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(not(isChecked())))
    }
```

- 또다른 테스트 코드를 아래와 같이 작성해볼 수 있음

```kotlin
    @Test
    fun completedTaskDetails_DisplayedInUi() = runBlockingTest{
        // GIVEN - Add completed task to the DB
        val completedTask = Task("Completed Task", "AndroidX Rocks", true)
        repository.saveTask(completedTask)

        // WHEN - Details fragment launched to display task
        val bundle = TaskDetailFragmentArgs(completedTask.id).toBundle()
        launchFragmentInContainer<TaskDetailFragment>(bundle, R.style.AppTheme)

        // THEN - Task details are displayed on the screen
        // make sure that the title/description are both shown and correct
        onView(withId(R.id.task_detail_title_text)).check(matches(isDisplayed()))
        onView(withId(R.id.task_detail_title_text)).check(matches(withText("Completed Task")))
        onView(withId(R.id.task_detail_description_text)).check(matches(isDisplayed()))
        onView(withId(R.id.task_detail_description_text)).check(matches(withText("AndroidX Rocks")))
        // and make sure the "active" checkbox is shown unchecked
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(isDisplayed()))
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(isChecked()))
    }
```

### Using Mockito to write Navigation tests
- 이번엔 mock이라는 test double을 통해 Navigation component를 test 해볼 것인데 이 때 Mockito라는 test 라이브러리를 씀

- Navigating은 `TaskDetailFragment`를 초기화하는 것을 넘어서는 뚜렷한 output이나 state 변화의 결과를 내지 못하는 복잡한 action임

- `navigate`가 제대로 된 action parameter를 호출했는지 assert 하는 것이 최대임

- 그래서 mock을 통해 test double을 하는 것임 특정 메소드가 호출됐는지 확인하기 위해서

- Mockito는 test double을 만들기 위한 프레임워크임, API나 name에서 사용하는 word mock과는 다르게 이것은 단순히 mock을 만드는 것에 끝나지 않음, stub, spy도 만들 수 있음

- 여기서 mock `NavigationController`을 만들어서 navigate method가 정확히 잘 호출되는지 assert 할 것임

- 이 때, 라이브러리를 추가해줘야함, 맨 처음은 Mockito dependency임

- 두번째 라이브러리는 Android project에서 Mockito를 사용하기 위해 필요한 라이브러리임, Mockito는 runtime에서 클래스들을 생성해야하는데 안드로이드에선 dex byte code로 완료됨, 그래서 이 라이브러리가 Android runtime동안 object를 생성해줌

- 마지막 라이브러리는 `DatePicker`나 `RecyclerView`와 같은 발전된 view에 대한 testing code를 포함하게끔 외부 contribution을 만드는 라이브러리임, 이는 또한 접근성도 포함되어 있음

```kotlin
    // Dependencies for Android instrumented unit tests
    androidTestImplementation "org.mockito:mockito-core:$mockitoVersion"

    androidTestImplementation "com.linkedin.dexmaker:dexmaker-mockito:$dexMakerVersion" 

    androidTestImplementation "androidx.test.espresso:espresso-contrib:$espressoVersion"
```

- 그리고 동일하게 `TasksFragment`에 대해서 test코드를 `androidTest` source set에서 만들고 아래와 같이 코드를 짬, 이는 이전에 썼던 `TaskDetailFragmentTest`와 유사함, `FakeAndroidTestRepository`를 만들고 없앰

```kotlin
@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
class TasksFragmentTest {

    private lateinit var repository: TasksRepository

    @Before
    fun initRepository() {
        repository = FakeAndroidTestRepository()
        ServiceLocator.tasksRepository = repository
    }

    @After
    fun cleanupDb() = runBlockingTest {
        ServiceLocator.resetRepository()
    }

}
```

- 그리고 test code로 선택한 task에 대해서 정확한 `TaskDetailFragment`로 가게끔 클릭처리를 함

```kotlin
    @Test
    fun clickTask_navigateToDetailFragmentOne() = runBlockingTest {
        repository.saveTask(Task("TITLE1", "DESCRIPTION1", false, "id1"))
        repository.saveTask(Task("TITLE2", "DESCRIPTION2", true, "id2"))

        // GIVEN - On the home screen
        val scenario = launchFragmentInContainer<TasksFragment>(Bundle(), R.style.AppTheme)
        
    }
```

- 그리고 mock을 만들기 위해서 `mock`함수를 씀, 여기에 클래스를 준다면 mock을 하고 싶은 클래스가 만들어짐, 그리고 Fragment에 해당하는 `NavController`처리를 진행함

```kotlin
 val navController = mock(NavController::class.java)
```
```kotlin
scenario.onFragment {
    Navigation.setViewNavController(it.view!!, navController)
}
```

- 그 다음 `RecyclerView`의 item을 하나 클릭하게 함

```kotlin
// WHEN - Click on the first list item
        onView(withId(R.id.tasks_list))
            .perform(RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                hasDescendant(withText("TITLE1")), click()))
```

- `RecyclerViewActions`은 `espresso-contrib`라이브러리의 일부로 RecyclerView에서 Espresso action을 수행하도록 처리함

- `navigate`가 호출되면 적합한 argument인지 검증함, `verify` 메소드를 통해 mock `navController`에서 파라미터와 특정 메소드가 호출되었는지 확인할 수 있음

```kotlin
// THEN - Verify that we navigate to the first detail screen
verify(navController).navigate(
    TasksFragmentDirections.actionTasksFragmentToTaskDetailFragment( "id1")
```

- 전체 코드는 아래와 같음

```kotlin
@Test
fun clickTask_navigateToDetailFragmentOne() = runBlockingTest {
    repository.saveTask(Task("TITLE1", "DESCRIPTION1", false, "id1"))
    repository.saveTask(Task("TITLE2", "DESCRIPTION2", true, "id2"))

    // GIVEN - On the home screen
    val scenario = launchFragmentInContainer<TasksFragment>(Bundle(), R.style.AppTheme)
    
                val navController = mock(NavController::class.java)
    scenario.onFragment {
        Navigation.setViewNavController(it.view!!, navController)
    }

    // WHEN - Click on the first list item
    onView(withId(R.id.tasks_list))
        .perform(RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
            hasDescendant(withText("TITLE1")), click()))


    // THEN - Verify that we navigate to the first detail screen
    verify(navController).navigate(
        TasksFragmentDirections.actionTasksFragmentToTaskDetailFragment( "id1")
    )
}
```

- 정리하면 먼저 Mockito를 통해 `NavController` mock을 만듬, 그리고 fragment에 mock `NavController`를 붙임, 그 다음 적합한 action과 parameter와 함께 navigate가 호출되었는지 검증함

- 추가로 아래의 테스트 코드로 add가 되는지도 확인할 수 있음

```kotlin
   @Test
    fun clickAddTaskButton_navigateToAddEditFragment() {
        // GIVEN - On the home screen
        val scenario = launchFragmentInContainer<TasksFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // WHEN - Click on the "+" button
        onView(withId(R.id.add_task_fab)).perform(click())

        // THEN - Verify that we navigate to the add screen
        verify(navController).navigate(
            TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(
                null, getApplicationContext<Context>().getString(R.string.add_task)
            )
        )
    }
```