### Setup and starter code walkthrough
- 예시앱에서 networking과 user interface module에 여러 가지로 구성되어 있음

- 먼저 `domain` 패키지를 본다면 app의 데이터를 담고 있는 `DevByteVideo` data class가 있음 단일 DevByte Video의 정보를 담음

- `network/DataTransferObjects`에선 `NetworkVideo`라는 data transfer object가 존재함, 이 객체는 network 결과값을 파싱함 그리고 `asDomainModel` 메소드를 통해서 network 결과를 domain 객체의 list로 변환해줌

- 이는 단순한 domain object와는 다름 domain object는 network 결과를 파싱하는 추가적인 로직이 존재하기 때문임

- 이처럼 network, domain, database 객체를 분리하는 것은 관심사 분리 원칙에 핵심적인 전략임, network 결과값이나 database 스키마가 변경했을 때, 전체 앱 코드를 갱신하지 않고 앱의 요소를 변경하고 관리하기 위해서 중요한 원칙임

- 나머지는 비슷함 `network/Service`에선 Retrofit service를 담고 `devbytes` playlist를 fetch하고 `DevByteViewModel`은 `LiveData`객체로 app 데이터를 가지고 있으며 UI Controller로 `DevByteFragment`는 `LiveData`를 observe하고 video list를 보여줄 `RecyclerView`를 포함하고 있음

--------

## Caching
- network로부터 data를 fetch한 후, app은 기기의 저장소에 data를 저장함으로써 data를 캐싱할 수 있음

- 이렇게 cache한 data는 기기가 오프라인이 되거나 같은 데이터를 다시 접근하려고 할 때 접근하게 할 수 있음

- network caching을 적용하는 것에는 다양한 방법이 있는데 하나씩 알아본다면


### Retrofit
- Retrofit은 Android를 위해 타입 안전성이 있는 REST client에 적용할 수 있는 networking library임

- 모든 네트워크 결과의 복사본을 로컬에 저장하도록 Retrofit을 설정할 수 있음

- 이는 간단한 request와 response가 있을 경우 간혈적으로 네트워크 호출을 할때나 소규모 데이터 셋에 적합함

### SharedPreferences
- key-value 쌍으로 SharedPreferences를 활용해서 캐싱을 할 수 있음

- 적은 수의 key나 간단한 value에 대해서 좋은 방법임, 단 이 방법을 큰 수의 구조화된 데이터를 저장하는데는 부적절함

### Internal Storage
- 바로 앱 내부의 저장소에 접근해서 data 파일안에 저장할 수 있음

- 앱의 패키지 이름이 앱의 내부 저장소 디렉토리를 특정하고 그리고 그것은 Android file system에서 특별한 장소임

- 이 디렉토리는 앱의 private한 것이고 앱이 삭제되면 같이 지워짐

- 만약 file system을 통해 해결해야할 특정 문제라면 좋은 방법임 예를 들어 media file이나 data file을 저장하려고 할 때 이 file 자체를 관리해야하기 때문에 그땐 유용한 방식임

- 단, 복잡하거나 구조화된 데이터에 사용할 수 없는 방식임

### Room
- Room을 활용해 캐싱을 할 수 있음

- SQLite를 넘어선 추상계층을 제공해주는 SQLite object-mapping 라이브러리임

- 복잡하고 구조화된 데이터에 대해서 강력히 권장됨, 내부 SQLite database안에 기기의 파일 시스템에 구조화된 데이터를 저장하기 가장 좋은 방법임

---------

### Add an offline cache
- `Room` database를 활용해서 offline cache를 사용해볼 것

- 여기서 cache를 사용하는 것은 앱을 시작할 때마다 매번 네트워크 호출을 해서 data를 가져오게 되면 앱 로딩이 오래 걸릴 수 있음, 이를 줄여줄 수 있는 방법 중 하나임

![one](/Android/img/thirtynine.png)

- network에서 data를 받으면 바로 data를 보여주지 않고 database에 저장할 것임

- 새로운 network 결과 값을 받으면 local database를 업데이트 하고 local database로부터 스크린에 새로운 컨텐츠를 보여줄 것임

- 이 방법은 offline cache가 항상 최신이 유지되도록 보장해줌 또한 offline일 경우 앱이 여전히 로컬 캐시 data를 저장하고 있음을 알 수 있음

- Room DB 사용을 위해 관련된 의존성을 추가함, 그리고 시작

- 먼저 database 객체를 추가함, `DatabaseVideo`라는 이름의 database 객체를 나타내는 database entity를 만들 수 있음

- 그리고 `DatabaseVideo`객체를 간단하게 메소드로 구현한 것을 통해 domain 객체 그리고 `DatabaseVideo`객체를 network 객체로 변환할 수 있음

```kotlin
/**
 * Database entities go in this file. These are responsible for reading and writing from the
 * database.
 */
@Entity
data class DatabaseVideo constructor(
    @PrimaryKey
    val url: String,
    val updated: String,
    val title: String,
    val description: String,
    val thumbnail: String
)

/**
 * Map DatabaseVideos to domain entities
 */
// DatabaseVideo를 domain 객체로 변환하는 확장함수
fun List<DatabaseVideo>.asDomainModel(): List<DevByteVideo> {
    return map {
        DevByteVideo(
            url = it.url,
            title = it.title,
            description = it.description,
            updated = it.updated,
            thumbnail = it.thumbnail)
    }
}

```

- 그리고 `DataTransferObjects`에서도 `asDatabaseModel`로 network 객체를 `DatabaseVideo` database 객체로 변환할 수 있는 확장함수를 만듬

```kotlin
/**
 * Convert Network results to database objects
 */
// Network 결과로 받은 객체를 database 객체로 변환함
fun NetworkVideoContainer.asDatabaseModel(): List<DatabaseVideo> {
    return videos.map {
        DatabaseVideo(
            title = it.title,
            description = it.description,
            url = it.url,
            updated = it.updated,
            thumbnail = it.thumbnail)
    }
}
```

- 위에서 Database Entity를 만들고 그 객체를 각각 Domain 객체와 Network 결과 객체에서 database 객체 변환하는 처리를 함

- 이제 `VideoDao`를 만들어서 database의 접근하는 것을 도와주는 메소드를 만듬, 하나는 database로부터 videos를 가져오는 메소드이고 다른 하나는 database에 videos를 insert하는 메소드임

```kotlin
@Dao
interface VideoDao {
    // database로부터 모든 videos를 fetch 하는 메소드
    // LiveData를 리턴으로 받아 database에 있는 data가 변할 때 UI에 있는 data를 갱신할 수 있게 함
    @Query("select * from databasevideo")
    fun getVideos(): LiveData<List<DatabaseVideo>>

    // network로부터 fetch 된 video list를 database에 insert 하는 메소드, 충돌 전략은 교체로 정함
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll( videos: List<DatabaseVideo>)
}
```

- 그리고 offline cache를 위해 `RoomDatabase`를 적용할 database를 추가함

```kotlin
// Database를 정의함, Dao에 접근할 수 있게 VideoDao 타입의 변수를 정의함
@Database(entities = [DatabaseVideo::class], version = 1)
abstract class VideosDatabase: RoomDatabase() {
    abstract val videoDao: VideoDao
}
```

- 그 다음 외부에서 사용할 수 있게 인스턴스를 정의하고 초기화함, 싱글톤으로 처리함

```kotlin
// Database를 싱글톤으로 만들어서 사용할 수 있게 인스턴스를 정의함
private lateinit var INSTANCE: VideosDatabase

// Database 초기화
fun getDatabase(context: Context): VideosDatabase {
    synchronized(VideosDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
            VideosDatabase::class.java,
            "videos").build()
        }
    }
    return INSTANCE
}
```

--------

## Repository
- Repository pattern은 app의 나머지로부터 data source를 독립시키는 패턴임

- repository는 data source(models, web services, cache 같은)와 앱의 나머지를 중재함

- 아래의 그림과 같이 `LiveData`를 사용하는 activity 같은 app component가 어떻게 repository를 통해 data source와 상호작용하는지 알 수 있음

![one](/Android/img/fourty.png)

- repository를 적용하기 위해 repository class를 사용함

- 이 repository class는 앱의 나머지로부터 data source를 독립시키고 앱의 나머지 부분들이 data를 access 하기 위한 깔끔한 API를 제공함

- 이 클래스는 코드 분리와 아키텍쳐를 위한 최적화로 권장됨

- repository module은 data 조작을 처리하고 여러개의 backend를 사용하게끔 허용함, 실제 앱에선 repository는 network로부터 data를 fetch할 건지 local database에 캐시 된 결과를 사용할 것인지 결정하기 위한 로직을 구현함

- 이것은 code가 모듈화되고 테스트하는데 도움을 줌, 쉽게 repository를 통해 mock up을 할 수 있고 코드의 나머지를 test 할 수 있음

-------

### Create a repository
- 앞서 offline cache로 만든 것을 repository를 통해서 관리할 수 있게 만들 수 있음

- repository를 통해 network 결과를 fetch하고 database를 최신으로 유지하는 로직을 적용시킬 수 있음

- 아래와 같이 network를 호출하고 database를 갱신하는 메소드 작성

```kotlin
/**
 * Repository for fetching devbyte videos from the network and storing them on disk
 */
// Dao 메소드에 접근하기 위해 생성자 매개변수로 VideosDatabase 전달함
class VideosRepository(private val database: VideosDatabase) {
    /**
     * Refresh the videos stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     */
    // offline cache를 refresh하는데 사용하는 API 메소드
    suspend fun refreshVideos() {
        withContext(Dispatchers.IO) {
            Timber.d("refresh videos is called")
            // DevByte video playlist를 network를 불러서 fetch함
            val playlist = DevByteNetwork.devbytes.getPlaylist()
            // fetch한 것을 database에 저장하기 위해 Dao 호출해서 처리함
            database.videoDao.insertAll(playlist.asDatabaseModel())
        }
    }
}
```

- 그리고 database로부터 data를 찾는 것을 추가함

- `LiveData` 객체를 통해서 database로부터 video playlist를 읽어옴, 이 `LiveData`는 database가 업데이트되면 자동으로 업데이트 됨

- fragment든 activity든 새로운 값에 갱신이 됨

```kotlin
// database로부터 받아온 playlist 값, domain에서 사용할 수 있는 것으로 변경
    val videos: LiveData<List<DevByteVideo>> = Transformations.map(database.videoDao.getVideos()) {
        it.asDomainModel()
    }
```

### Integrate the repository using a refresh strategy
- 이제 위에서 만든 repository와 간단한 refresh 전략을 가진 `ViewModel`을 합칠 것임

- 현재 상황은 network로부터 바로 fetch하지 않고 `Room` database로부터 video playlist를 보여줌

- database refresh는 network로부터 data를 맞추기 위해서 local database를 update하거나 refresh하는 과정임

- 예시앱은 repository로부터 data를 요청하는 module이 local data를 refresh 하는 책임이 있는 간단한 refresh 전략을 쓸 것임

- 실제 앱에선 이 전략은 복잡할 수 있음, 예를 들어 coed가 자동으로 background에서 data를 refresh 하거나 user가 다음에도 사용할 data를 자동으로 cache할 수 있음

- 먼저 이 전략을 쓰기 위해서 `ViewModel`에서 `private` member로 `VideosRepository`를 정의하고 `VideosDatabase`싱글톤 객체를 변수로 넘겨줘 인스턴스화 함

```kotlin
/**
* The data source this ViewModel will fetch results from.
*/
private val videosRepository = VideosRepository(getDatabase(application))
```

- 그리고 기존의 `refreshDataFromNetwork()`를 `Repository`로 바꿈, 이전 방식은 network를 사용해서 fetch 했지만 이 방식은 repository를 통해 fetch 할 것임

```kotlin
/**
     * init{} is called immediately when this ViewModel is created.
     */
    init {
        refreshDataFromRepository()
    }

    /**
     * Refresh data from the repository, Use a coroutine launch to run in a
     * background thread.
     */
    // Repository를 이용해 refresh함
    private fun refreshDataFromRepository() {
        viewModelScope.launch { 
            try {
                videosRepository.refreshVideos()
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false
            } catch (networkError: IOException) {
                // Show a Toast error message and hide the progress bar.
                if(playlist.value.isNullOrEmpty()) 
                    _eventNetworkError.value = true
            }
        }
    }
```

- 그리고 `playlist`의 `LiveData`를 삭제함, 어차피 Repository에 있기 때문에 해당 값을 할당해주면 됨, 아래와 같이

```kotlin
/**
* A playlist of videos displayed on the screen.
*/
val playlist = videosRepository.videos
```

- 이제 앱이 실행되기 전에 playlist fetch는 network로 하고 `Room`에 바로 저장을 함, 화면에 보여지는 것은 `Room`에서 얻은 것이지 Network를 통해서 얻어서 처리한 값이 아님

- 그러므로 이제 오프라인 상황에서도 화면이 그대로 보임, `Room`에 저장되어 있으므로 그 값이 그대로 나타남

- 그리고 만약 network로부터 새로운 data가 온다면 알아서 그 새로운 data를 반영해줄 것임, 하지만 서버가 content를 refresh 하지 않는다면 data도 update 하지 않을 것임

