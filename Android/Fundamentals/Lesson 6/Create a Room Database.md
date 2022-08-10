### Create the SleepNight entity
- 데이터는 데이터 클래스에서 나타남, 데이터는 함수 호출을 활용해서 접근하고 수정할 수 있음

- 하지만 데이터베이스에선 데이터에 접근하고 수정하기 위해 `Entity`와 `Query`가 필요함

- `Entity`는 해당 property와 함께 데이터베이스에 저장할 개체 또는 개념을 나타냄

- 우리는 table로 정의된 entity class가 필요하고 해당 클래스의 각각의 인스턴스는 테이블의 행을 나타냄

- Entity class는 데이터베이스의 정보를 어떻게 표시하고 상호작용하는 방법을 `Room`에 알려주는 매핑이 있음, 예시앱에선 entity는 수면에 대한 정보를 가지고 있을 것임

- `Query`는 데이터베이스 테이블 또는 조합된 테이블로부터 정보나 데이터를 요청하거나 데이터에 대한 작업을 수행하라는 요청을 하는 것임

- 일반적인 `Query`는 엔티티를 생성, 읽기, 수정, 삭제하는 것임, 예를 들어 시작시간으로 정렬된 취침시간에 대한 모든 기록을 실행하도록 하게 할 수 있음

- 앱 경험상 몇 몇 데이터는 로컬로 지속되게 하는 것이 매우 큰 이익이 있음, 관련된 데이터 조각을 캐싱하는 것은 유저에게 오프라인이어도 앱을 즐길 수 있게해줌

- 앱이 서버에 의존하고 있다면 캐싱을 통해서 유저가 오프라인일 경우 로컬로 지속적으로 컨텐츠를 수정할 수 있게 하고 앱이 서버와 연결되었을 때 이러한 캐시가 백그라운드에서 서버와 원활하게 동기화 되어 처리될 수 있음

- `Room`은 코틀린 데이터 클래스에서 SQLite table로 저장을 할 수 있는 entity로 함수 선언에서 SQL query와 모든 작업을 할 수 있게함

- entity를 어노테이션이 있는 data class로 정의할 수 있고 그리고 이것을 Data Access Object(DAO)라고 볼리는 어노테이션이 된 인터페이스로 entity와 상호작용할 수 있음

- `Room`은 이 어노테이션된 클래스를 데이터베이스에서 테이블을 생성하는데 사용하고 데이터베이스에 사용하기 위한 Query를 생성함

![one](/Android/img/twentyseven.png)

- 그리고 여기서 먼저 `Entity` 만듬, 아래와 가팅 dataclass로 만들고 각 변수를 초기화함, 그리고 어노테이션을 붙여서 만듬, 여기서 table name도 씀(구분을 하기 위해서)

```kotlin
@Entity(tableName = "daily_sleep_quality_table")
data class SleepNight(
    var nightId: Long = 0L,
    val startTimeMilli: Long = System.currentTimeMillis(),
    var endTimeMilli: Long = startTimeMilli,
    var sleepQuality: Int = -1
)
```

- 그리고 primary key도 지정을 함, 여기서 추가로 `autoGenerate`를 `true`로 주어 각각 Entity에 대해서 ID를 생성하게끔 처리함, 그래서 각각 night가 고유하게 되게끔 처리함

- 그런 다음 `@ColumnInfo`를 통해서 각각의 property의 이름을 파라미터를 통해서 커스텀함

```kotlin
@Entity(tableName = "daily_sleep_quality_table")
data class SleepNight(
    @PrimaryKey(autoGenerate = true)
    var nightId: Long = 0L,
    
    @ColumnInfo(name = "start_time_milli")
    val startTimeMilli: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "end_time_milli")
    var endTimeMilli: Long = startTimeMilli,
    
    @ColumnInfo(name = "quality_rating")
    var sleepQuality: Int = -1
)
```

### Create the DAO
- 그 다음 Data Access Object(DAO)를 정의할 것임, DAO은 데이터베이스를 삽입, 삭제, 수정하는 것과 관련된 메소드를 제공함

- 코틀린 함수로 호출해서 데이터베이스에 Query를 날려야함

- 이 코틀린 함수는 SQL Query와 매핑되어 있음, 이런 맵핑을 DAO에 어노테이션을 사용해서 정의할 수 있음

- 일반적으로 `@Insert`, `@Delete`, `@Update`의 어노테이션도 있지만 `@Query` 어노테이션을 통해 직접 쿼리문을 쓸 수 있음, 직접 쓸 경우 컴파일러가 이 쿼리문의 에러여부를 체크함

- 예시앱에서 필요한 것은 하나씩 작성해 볼 것임, 먼저 Dao를 어노테이션을 통해 인터페이스에 적용하고 하나씩 쓸 것임

- 아래와 같이 먼저 `Insert`하는 함수를 만듬, 이렇게 하면 `Room`이 `SleepNight`를 데이터베이스에 insert 하기 위해 필요한 코드를 알아서 생성해줌, `insert()` 함수를 호출하면 `Room`은 데이터베이스에 entity를 insert 하기 위한 SQL query문을 실행할 것임

```kotlin
@Dao
interface SleepDatabaseDao {
    @Insert
    fun insert(night: SleepNight)
}
```

- 이제 여기서 기본 어노테이션을 써 `@Update`도 추가함

- 여기서 이런 기본 어노테이션 말고 쿼리문을 직접 활용해야할 수도 있음, 이때 `@Query` 어노테이션을 활용해 SQLite Query를 직접 씀

- 아래 쿼리문의 경우, key와 매칭이 되는 nightId를 가진 entity 테이블의 모든 column을 가져오는 것임

```kotlin
@Query("SELECT * from daily_sleep_quality_table WHERE nightId = :key")
    fun get(key: Long): SleepNight?
```

- 이처럼 쿼리문을 직접 짜서 쓸 수도 있음

```kotlin
// daily_sleep_quality_table에서 nightId가 key와 매칭되는 모든 column을 선택하는 함수
    @Query("SELECT * from daily_sleep_quality_table WHERE nightId = :key")
    fun get(key: Long): SleepNight?

    // 테이블의 모든 항목을 지우는 쿼리문을 쓴 함수 (@Delete는 하나의 아이템만 지우므로)
    @Query("DELETE FROM daily_sleep_quality_table")
    fun clear()

    // nightId 기준으로 내림차순으로 정렬된 테이블에서 하나의 아이템만 선택해서 가져오는 쿼리문을 적용한 함수
    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC LIMIT 1")
    fun getTonight(): SleepNight?

    // 내림차순으로 정렬된 테이블의 모든 column을 반환하게함
    // LiveData를 적용시켰기 때문에 Room은 LiveData를 유지함(명시적으로 한 번만 가져와도)
    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC")
    fun getAllNights(): LiveData<List<SleepNight>>
```

### Create and test a Room database
- 마지막으로 `@Database` 어노테이션을 활용해 abstract database holder class가 필요함

- 이 클래스는 database가 존재하지 않으면 database의 인스턴스를 만들거나 존재하는 database의 참조를 반환하는 하나의 메소드를 가지고 있는 클래스임

- 그 프로세스는 아래와 같음

- 먼저 `extends RoomDatabase`를 한 `public abstract` 클래스를 만듬, 이 클래스는 database holder 클래스로 작동함(클래스가 추상 클래스여도 `Room`이 구현체를 만들어줌)

- 그리고 `@Database`를 클래스에 어노테이션으로 씀, 여기서 변수로 database의 entity를 선언하고 version number를 설정함

- `companion`객체 안에는 `SleepDatabaseDao`를 반환하는 추상 메소드나 속성을 정의함(`Room`이 구현을 생성해줄 것임)

- 전체 앱에서 오직 `Room` database의 하나의 인스턴스만 필요함, 그래서 싱글톤으로 만듬

- database가 존재하지 않을 때만 `Room`의 database builder를 활용해 database를 만듬, 그렇지 않으면 기존 database를 반환함

- 이 과정대로 만들면 됨, `@Database`만 좀 더 자세히 보면 `entities`의 `SleepNight`를 두고 `version`은 1로 하였는데 schema가 변경되면 version number를 증가시켜야함 `exportSchema`는 `false`로 했으므로 schema 버전 기록을 백업하지 않을 것임

```kotlin
@Database(entities = [SleepNight::class], version = 1, exportSchema = false)
```

- 그리고 나머지 과정은 그대로 진행 Dao를 선언하고 `companion` object를 정의함, 이렇게 함으로써 클라이언트가 클래스를 인스턴스화 하지 않고 데이터베이스를 생성하고 얻는 메소드에 접근할 수 있게함(즉 싱글톤으로 만든 것)

- 즉 해당 클래스는 오직 데이터베이스 제공을 위한 것임, 인스턴스화 할 이유가 없음, 그리고 `companion` object 안에서 database를 위해 `INSTANCE`를 정의함 그리고 `null`로 초기화함, 이 `INSTANCE` 변수는 한 번 만들어지면 데이터베이스의 참조를 계속 유지할 것임, 이것은 데이터베이스 연결이 계산 비용이 크기 때문에 이를 반복적으로 연결하는 것을 피하게 함

- `INSTANCE`에 대한 어노테이션은 `@Volatile`임, 모든 volatile 변수는 캐싱이 되지 않고 읽고 쓰는 모든 작업은 main memory에서 처리됨, 이것이 `INSTANCE` 값이 항상 최신상태가 되고 모든 실행하는 같은 스레드에 확실하게 있게 도와줌

- 이 말은 만약 `INSTANCE`가 하나의 스레드에서 변화가 생기면 다른 스레드에서 동시에 알아차리게 되는 것이고 그래서 두 개의 스레드가 캐시에서 같은 엔티티를 업데이트하여 발생하는 문제가 절대로 일어날 수 없게함

```kotlin
// 인스턴스화 하지 않고 메소드에 접근가능함, 해당 클래스를 인스턴스화 할 필요 없음, 싱글톤으로 처리한 것
    companion object {
        @Volatile
        private var INSTANCE: SleepDatabase? = null

    }
```

- 그리고 database builder가 필요로 한 `Context`를 매개변수로 `getInstance()`를 정의함 `SleepDatabase`를 반환함

- 여기서 이것만 쓰면 에러가나고 `synchronized{}` block을 통해서 `this`를 넘겨 context에 접근할 수 있게함

- 여러개의 Thread가 동시에 database instance에 접근할 수 있기 때문에 `synchronized{}` 처리를 함, 그렇게 하지 않으면 두 개의 database가 생성될 수 있음

- 물론 간단한 앱에서는 문제가 없지만, 복잡한 앱에선 발생할 수 있기 때문에 써줘야함

- `synchronized{}`의 의미는 해당 block에 있는 코드의 실행을 오직 하나의 스레드에서만 실행하라고 처리하는 것임, 이 block안에서 처리하면 이제 오직 하나의 database만 초기화되게 할 수 있음

```kotlin
// Database 관련 기본 설정 처리함
@Database(entities = [SleepNight::class], version = 1, exportSchema = false)
abstract class SleepDatabase : RoomDatabase() {
    // Dao를 불러옴(다른 다양한 Dao도 불러올 수 있음)
    abstract val sleepDatabaseDao: SleepDatabaseDao

    // 인스턴스화 하지 않고 메소드에 접근가능함, 해당 클래스를 인스턴스화 할 필요 없음, 싱글톤으로 처리한 것
    companion object {
        @Volatile
        private var INSTANCE: SleepDatabase? = null
        
        // 인스턴스를 반환하는 함수
        fun getInstance(context: Context): SleepDatabase {
            // 여러개의 인스턴스가 생길 수 있으므로 synchronized 처리함
            synchronized(this) {}
        }

    }
}
```

- 그리고 그 안에서 database를 만들고 처리를 함

- 일반적으로 builder와 함께 migration 관련해서도 추가해줘야함

- migration의 경우, schema가 변할 경우 migration 전략을 제공하는 것임

- migration object는 예전 schema의 모든 열을 어떻게 새로운 schema의 모든 열로 변환할 것인지 정의하는 객체임, 그렇게 하면 데이터 손실이 없음

- 이 부분은 좀 더 깊게 다뤄야 하는 부분이고 아래의 예시에선 간단하게 없애고 database를 재빌드하는것으로 함 즉, 데이터 손실이 발생함

```kotlin
// Database 관련 기본 설정 처리함
@Database(entities = [SleepNight::class], version = 1, exportSchema = false)
abstract class SleepDatabase : RoomDatabase() {
    // Dao를 불러옴(다른 다양한 Dao도 불러올 수 있음)
    abstract val sleepDatabaseDao: SleepDatabaseDao

    // 인스턴스화 하지 않고 메소드에 접근가능함, 해당 클래스를 인스턴스화 할 필요 없음, 싱글톤으로 처리한 것
    companion object {
        @Volatile
        private var INSTANCE: SleepDatabase? = null

        // 인스턴스를 반환하는 함수
        fun getInstance(context: Context): SleepDatabase {
            // 여러개의 인스턴스가 생길 수 있으므로 synchronized 처리함
            synchronized(this) {
                // INSTANCE를 반환하게 함
                var instance = INSTANCE

                if(instance == null) {
                    // instance가 null이면 아직 database가 만들어지지 않은 것임
                    // database builder를 통해서 database를 만듬
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SleepDatabase::class.java,
                        "sleep_history_database"
                    ).fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }

                return instance
            }
        }

    }
}

```

- 그리고 이를 Test Code로 확인해 볼 수 있음, 실제 출시한 앱에서는 DAO의 모든 함수와 Query를 실행시켜봄
```kotlin
/**
 * This is not meant to be a full set of tests. For simplicity, most of your samples do not
 * include tests. However, when building the Room, it is helpful to make sure it works before
 * adding the UI.
 */

@RunWith(AndroidJUnit4::class)
class SleepDatabaseTest {

    private lateinit var sleepDao: SleepDatabaseDao
    private lateinit var db: SleepDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, SleepDatabase::class.java)
                // Allowing main thread queries, just for testing.
                .allowMainThreadQueries()
                .build()
        sleepDao = db.sleepDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetNight() {
        val night = SleepNight()
        sleepDao.insert(night)
        val tonight = sleepDao.getTonight()
        assertEquals(tonight?.sleepQuality, -1)
    }
}
```

- 여기서 Test Code를 보면 `@RunWith` 어노테이션은 tests를 진행할 test runner를 식별하는 어노테이션임

- setup에서 `@Before`의 경우 `SleepDatabaseDao`가 있는 `SleepDatabase`를 in-memory로 만드는 것임 이 말은 테스트가 끝나면 파일 시스템에서 삭제되는 것을 의미함

- 여기서 추가로 `allowMainThreadQueries`는 main thread에서 쿼리를 실행하면 에러가 뜨게끔 되어 있고 위의 메소드에서는 test를 하기 위해서 main thread에서 실행할 수 있도록 허용한 것임

- `@Test`에선 test method로 `SleepNight`를 생성하고 삽입하고 반환하는 그리고 그 값이 같은지 `assert`를 통해서 확인함, 여기서 잘못되면 예외를 던짐, 실제에선 다양한 `@Test` 메소드가 있음

- 테스트를 실행하면 성공인데 여기서 테스트는 먼저 database를 만들고 database에 `SleepNight`를 넣고 `SleepNight`를 확인하고 맞는지 보는 테스트임

