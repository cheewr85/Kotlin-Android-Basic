## WorkManager
- `WorkManager`는 Android Jetpack의 AAC 중 하나임

- `WorkManager`는 지연 가능(Deferrable)하고 실행 보장(Guaranted execution)이 필요한 백그라운드 작업을 위한 것임

- Deferrable은 work가 당장 실행될 필요가 없는 것을 의미함, 예를 들어 server의 분석 데이터를 보내던가 background에서 database의 싱크를 맞추는 건 지연 가능한 일임

- Guaranted execution은 task가 app이 종료되거나 device가 재시작되어도 실행할 작업을 말함

- `WorkManager`를 background 작업을 돌릴 때 호환성 이슈와 시스템을 위한 battery 보완을 처리해줘야함

- `WorkManager`는 API level 14까지 지원을 함, `WorkManager`는 기기의 API level에 따라 background 작업의 schedule에 적절한 방법을 선택함

- API 23보다 높으면 `JobScheduler`를 쓰거나 `AlarmManager`와 `BroadcastReceiver`의 조합을 사용할 수 있음

![one](/Android/img/fourtyone.png)

- `WorkManager`는 background task를 수행할 때 기준을 설정함, 예를 들어 battery 상태, network 상태, charge 상태에 부합한 기준을 가지고 있을 때 task를 실행시키고 싶을 수 있음

- `WorkManager`는 앱 프로세스가 종료된 경우 안전하게 종료될 수 있는 in-process 백그라운드 작업을 위한 것이 아님

- `WorkManager`는 즉각적인 실행이 필요한 작업에는 적합하지 않음

- 예시앱에 응용하여 하루에 한 번 network로부터 DevBytes video playlist를 pre-fetch하라는 task를 schedule 할 것임, 이를 위해 `WorkManager`를 사용함

------

### Create a background worker
- workmanager와 관련된 의존성을 먼저 추가함, 그리고 background worker를 만듬 

- `WorkManager` 라이브러리 사용에 있어서 몇 가지 class를 사용해야함 

#### Worker
- 이 클래스는 background에서 실제 돌아가는 작업을 정의함, 이 클래스를 상속받아 `doWork()` 메소드를 오버라이딩함

- `doWork()`메소드에서 server나 image처리에 있어서 data를 sync하는 것 같은 background에서 수행될 작업에 대해서 code를 넣음

#### WorkRequest
- 이 클래스는 background에서 wroker를 실행할 요청을 나타냄

- 기기가 꽃혀있는지 와이파이 상태인지 같은 것을 `Constraints`의 도움으로 언제 어떻게 worker task를 실행할지 설정할 때 `WorkRequest`를 통해 설정함

#### WorkManager
- 이 클래스는 `WorkRequest`에 대해서 스케줄하고 실행을 함

- `WorkManager`는 지정한 제약조건을 준수하면서 system resources의 부하를 분산시키는 방식으로 작업 요청을 예약함

-------

- 먼저 DevBytes video playlist를 background에서 pre-fetch할 `Worker`를 추가함

- 패키지를 미리 만들고 아래와 같이 `Worker`를 상속받아서 처리함

- suspend fun을 통해서 main thread를 막지 않고 작업이 완료될 때까지 기다리고 긴 실행 작업을 실행하게 할 수 있음(백그라운드 작업하기에 딱임)

```kotlin
class RefreshDataWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        return Result.success()
    }

}
```

- `Worker` 클래스 안에 `doWork()` 메소드는 background thread에서 호출됨, 이 메소드는 작업을 동기적으로 수행하고 `ListenableWorker.Result`객체를 반드시 리턴해야함

- Android System은 `Worker`에 실행을 최대 10분 동안 끝내게 제공해주고 `ListenableWorker.Result` 객체를 반환하게 함, 이 시간이 만료되면 시스템은 강제로 `Worker`를 멈춤 

- `ListenableWorker.Result` 객체 생성시, background 작업의 완료 상태를 나타내는 static method 중 하나를 호출함

- `Result.success()` : 작업이 성공적으로 완료됨

- `Result.failure()` : 작업이 완전히 실패함

- `Result.retry()` : 작업이 일시적인 오류가 발생하여 다시 시도해야함

- 여기서 `doWork()` 내부에서 `VideosDatabase`객체와 `VideosRepository` 객체의 인스턴스를 만들어서 내부 메소드를 호출하여 처리함으로써 background 작업을 할 수 있음, 그리고 상태에 따라 `try-catch`처리를 하여 아래와 같이 쓸 수 있음

```kotlin
override suspend fun doWork(): Result {

        // database와 repository 인스턴스를 만들어서 연결
        val database = getDatabase(applicationContext)
        val repository = VideosRepository(database)

        try {
            // repository와 database 연결했으므로 refresh 메소드로 data fetch함
            repository.refreshVideos()
            Timber.d("Work request for sync is run")
        } catch (e: HttpException) {
            // 에러 발생시 다시 시도함
            return Result.retry()
        }

        return Result.success()
    }
```

### Define a periodic WorkRequest
- `Worker`는 unit 단위의 work를 정의하고 `WorkRequest`는 언제 어떻게 work가 실행되어야 하는지를 정의함

- `WorkRequest`가 구체적으로 구현할 것이 2가지 있음

- `OneTimeWorkRequest` : one-off task를 위한 것임, 오직 한 번만 일어나는 것을 말함

- `PeriodicWorkRequest` : periodic work를 위한 것임, work가 간격을 두고 반복함(periodic work의 최소 간격은 15분임)

- Task는 one-off일수도 periodic일수도 있음, 그에 적절한 class를 선택해야함

- `Application` class가 존재하는데 이는 activity나 service 같은 다른 모든 요소드를 포함하고 있는 base class임, application 이나 package가 만들어져 진행할 때 `Application` class는(혹은 `Application`의 하위 클래스든) 다른 class 전에 인스턴스화 됨

- 그래서 예시에서도 `DevByteApplication`은 `Application`의 하위 클래스로 `WorkManager`가 스케줄링 할 좋을 클래스임

```kotlin
/**
     * Setup WorkManager background job to 'fetch' new network data daily
     */
    // 반복되는 백그라운드 작업을 설정하기 위한 메소드
    private fun setupRecurringWork() {
        // 하루에 한 번 주기적으로 반복될 work request를 초기화하고 만듬
        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(1, TimeUnit.DAYS)
            .build()
    }
```

- `WorkRequest` 선언 이후 `enqueueUniquePeriodicWork()`메소드를 사용하는 `WorkManager`를 통해서 이를 스케줄링 할 수 있음

- 이 메소드를 통해 한 번에 특정 이름 중 하나만 활성화 되게 queue에 `PeriodicWorkRequest`를 추가할 수 있게 함

- 예를 들어 하나의 동기화 작업만 활성화되기를 원할 수 있음, 하나의 동기화 작업이 보류중인 경우 ExistingPeriodicWorkPolicy를 사용하여 실행하거나 새 작업으로 교체하도록 선택할 수 있음

- `WorkRequest`의 스케줄링 하는 방법은 `WorkManager` 공식문서에 더 나와 있음

- `RefreshDataWorker` 클래스 안에 클래스에 시작점에 companion object를 추가함, 이 worker를 식별할 수 있는 특별한 이름으로 정의함

```kotlin
companion object {
   const val WORK_NAME = "com.example.android.devbyteviewer.work.RefreshDataWorker"
}
```

- 그리고 `DevByteApplication` 클래스 안에서 `setupRecurringWork` 메소드 끝에 `enqueueUniquePeriodicWork`메소드를 사용해서 work를 스케줄링 함, ExisitingPeriodicWorkPolicy의 enum `KEEP`을 전달함

- `PeriodicWorkRequest`의 매개변수로 `repeatingRequest`를 전달함

```kotlin
WorkManager.getInstance().enqueueUniquePeriodicWork(
       RefreshDataWorker.WORK_NAME,
       ExistingPeriodicWorkPolicy.KEEP,
       repeatingRequest)
```

- 완료되지 않은 작업이 같은 이름으로 있을 때 `ExistingPeriodicWorkPolicy.KEEP` 매개변수는 `WorkManager`가 이전 periodic work를 진행하게 하고 새로운 work request를 버리게끔 만듬

- `onCreate` 메소드는 main thread에서 실행되기 때문에 `onCreate`에서 시간이 오래걸리는 작업은 UI thread를 막고 앱 로딩에 딜레이를 줄 수 있음, 이 문제를 피하기 위해서 `WorkManger`의 스케줄링이나 Timber를 초기화 하는 작업을 실행하는 것은 main thread가 아닌 coroutine에서 작업하는게 좋음

- 그래서 먼저 `CoroutineScope`객체를 정의하여서 `Dispatchers` 설정을 하고 `delayedInit`메소드를 정의해서 coroutine을 시작한 뒤 위에서 만든 `setupRecurringWork`메소드와 `Timber` 초기화를 담음

```kotlin
// main thread에서 오래 걸려서 UI thread를 block 하는 것을 막기위해 coroutine 활용
    private val applicationScope = CoroutineScope(Dispatchers.Default)

....


// coroutine으로 초기화시키는 함수
    private fun delayedInit() {
        // coroutine 실행, 시간이 걸리는 작업 처리함
        applicationScope.launch {
            Timber.plant(Timber.DebugTree())
            setupRecurringWork()
        }
    }
```

- 그리고 `onCreate`에 해당 메소드를 실행함

```kotlin
override fun onCreate() {
   super.onCreate()
   delayedInit()
}
```

- 추가로 15분 주기로 아래와 같이 설정해서 바꿀 수도 있음

```kotlin
val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(15, TimeUnit.MINUTES)
       .build()
```

### Add constraints
- worker와 work request 스케줄링을 `WorkManager`로 만들었지만 제약조건을 정의하지 않음, `WorkManger`가 하루에 한 번 스케줄링 할 때 배터리가 낮고 취침모드거나 네트워크 연결이 없어서도 스케줄링이 되버림

- 이것은 기기 배터리와 성능에 영향이 가므로 이 제약조건도 걸어줘야함

- 즉, 이 작업을 실행할 조건을 추가할 것임

- `WorkRequest`를 정의할 때, `Worker`가 실행되기 위한 특정 제약조건을 정의할 수 있음 예를 들어 이 작업을 device 상태가 이상적일 때만 혹은 충전중이거나 Wi-Fi 연결되었을 때 등 그때 작업을 처리하고 싶을 수 있음

- 그리고 작업을 재시작 할 때 backoff 정책도 정할 수 있음

- 이를 위해 `Constraints.Builder` 메소드를 통해 제약조건을 정할 수 있음

- `DevByteApplication` 클래스에서 `setupRecurringWork()` 위에 `val` 타입으로 `Constraints.Builder`를 사용한 `Constraints`를 정의함

```kotlin
val constraints = Constraints.Builder()
```

- 그리고 `setRequiredNetworkType` 메소드를 통해서 `constraints`객체의 network-type 제약조건을 추가함 `UNMETERED`를 사용해서 device가 무제한 네트워크에 연결되었을 때 work request를 실행하게끔 사용함, 그리고 build

```kotlin
// 제약조건을 걸기 위한 변수, 네트워크 제약조건을 걸고 build함
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.UNMETERED)
        .build()
```

- 그 다음 `setupRecurringWork` 메소드 안에 `repeatingRequest` periodic work request에 `Constraints` 객체를 설정함, `setConstraints`를 통해 추가함

```kotlin
// 15분 주기로 실행하는 작업임
       val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(15, TimeUnit.MINUTES)
           .setConstraints(constraints)
           .build()
```

- 여기서 battery가 낮은 경우 device가 충전중인 경우 API level 23보다 높은 경우에만 `PeriodicWorkRequest`가 돌이가도록 제약조건을 추가할 것임

- 먼저 `build()` 전에 `setRequiresBatteryNotLow`를 통해서 battery가 낮으면 실행하지 못하도록 처리함

```kotlin
.setRequiresBatteryNotLow(true)
```

- 그리고 충전중일 때만 실행되도록 아래의 제약조건을 추가함

```kotlin
.setRequiresCharging(true)
```

- 그리고 sdk 버전 이상일 때 유저의 활동이 거의 없을 때 실행하도록 함

- 이를 종합하면 아래와 같음

```kotlin
// 제약조건을 걸기 위한 변수, 네트워크 제약조건을 걸고 build함
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.UNMETERED)
        .setRequiresBatteryNotLow(true) // 배터리가 낮은 경우 실행 못하게함
        .setRequiresCharging(true) // 충전중일 때만 실행하게함
        .apply {
            // 23보다 높은 경우에만 user가 device를 활발히 쓰지 않을 때 진행시킴
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setRequiresDeviceIdle(true)
            }
        }
        .build()
```

- 그리고 다시 하루에 한 번 작업하는것으로 변경하고 constraint를 추가함

```kotlin
val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(1, TimeUnit.DAYS)
       .setConstraints(constraints)
       .build()
```