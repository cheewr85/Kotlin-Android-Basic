### Explore the MarsRealEstate starter app
- `ViewModel`에서 `Room` 데이터베이스를 통신하듯이, `overview ViewModel`에서 network 계층과 바로 통신을 함, 아래와 같은 구조를 형성함

![one](/Android/img/thirtyeight.png)

### Connect to a web service with Retrofit
- Mars real estate data는 REST web service를 통해 접근 가능한 웹 서버에 저장되어 있음, 그래서 앱에서 URI를 통해 표준화된 방식으로 web service에 요청함

- 친숙한 web URL은 사실 URI의 타입임, URL과 URI은 이 앱에서 서로 바꿔서 진행됨

- 모든 데이터를 받을 수 있는 기본 서버 URI는 다음과 같음 `https://android-kotlin-fun-mars-server.appspot.com`

- 그리고 브라우저의 아래의 URL 타입을 쓰면 Mars의 모든 활용가능한 real estate properties list를 얻을 수 있음 `https://android-kotlin-fun-mars-server.appspot.com/realestate`

- web service에서 받은 응답은 보통 구조화된 데이터로 나타내어 변화가능한 포맷인 JSON을 사용함

- JSON 객체는 key-value 쌍으로 묶인 집합체임, 이따금씩 dictionary, hash map, associative array로 불림

- JSON 객체의 집합체는 JSON array임, 이 array는 web service로부터 응답값으로 받는 것임

- 이 JSON 데이터를 앱으로부터 받기 위해서 server와 네트워크 연결을 하고 그 server와 통신을 하며 JSON 응답을 받고 앱이 사용할 수 있는 형태로 파싱해야함

- 이 때 Retrofit을 활용하여 이 연결을 만들 것임

- Retrofit 라이브러리와 Retrofit converter-scalars를 받음, 이 convert는 JSON 결과값을 `String`으로 변환해줌

- 그리고 Java8 language features를 쓰기 때문에 아래와 같이 의존성을 추가함

```markdown
android {
  ...

  compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
  }
  
  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_1_8.toString()
  }
}
```

- Retrofit은 web service로부터의 컨텐츠에 기반하여 앱을 위한 network API를 만듬, web service로부터 데이터를 fetch 받고 data를 어떻게 decode할지에 대해 각각의 converter library를 통해서 알고 유용한 객체의 형태로 리턴하게 함

- Retrofit은 XML이나 JSON 같은 유명한 data format에 대해 built-in support를 포함함

- Retrofit은 궁극적으로 백그라운드 스레드에서 요청을 실행하는 것과 같은 중요한 세부사항을 포함하여 대부분의 network layer를 만들어줌

- `MarsApiService` 클래스는 앱을 위한 network layer를 가진 클래스로 `ViewModel`이 web service와 통신하기 위해 사용할 API임, Retrofit service API를 구현하는 클래스임, 먼저 Retrofit 인스턴스 생성을 위해 아래와 같이 BASE_URL이 선언된 곳에 정의함

- 여기서 Retrofit이 web services API를 만들기 위해 최소 2가지는 필요함, web service를 위한 base URI와 converter factory는 반드시 있어야 함

- converter는 Retrofit에게 web service로부터 받은 것에 대해서 data에 무엇을 해야할 지 말해줌, 이 상황에서 Retrofit이 web service로부터 JSON 응답을 fetch하길 바랄 것이고 이를 `String`으로 리턴받길 원할 것임

- Retrofit은 string이나 다른 primitive type을 지원하는 `ScalarsConverter`를 가지고 있음, 이 인스턴스를 `addConverterFactory()`에 호출해서 넣음, 그리고 `build()`를 통해 Retrofit 객체를 만듬

```kotlin
private const val BASE_URL = "https://android-kotlin-fun-mars-server.appspot.com/"

// Retrofit 객체 생성을 위한 Retrofit builder
private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()
```

- 그리고 그 밑에 HTTP request를 사용해 Retrofit이 web server와 어떻게 통신할 것인지 인터페이스를 정의함

- web service로부터 JSON 응답 string을 얻기 위한 목표를 위해 인터페이스의 정의한 하나의 메소드인 `getProperties()`를 호출하면 됨

- Retrofit에게 이 메소드가 무엇을 하는지 알려주기 위해 web service 메소드를 위해 `@GET` 어노테이션과 특정 경로나 endpoint를 사용함, 여기서 endpoint는 `realestate`임

- `getProperties()`를 호출하면 Retrofit은 기본 URL에 `realestate`를 추가함 그리고 `Call` 객체를 만듬, `Call` 객체는 요청을 시작하는데 사용됨

```kotlin
interface MarsApiService {
    @GET("realestate")
    fun getProperties():
            Call<String>
}
```

- `MarsApiService` 인터페이스 밑에 `MarsApi`라는 Retrofit service를 초기화 하는 public 객체를 정의함, 아래와 같이 쓰는 것은 service object를 만들 때 사용하는 일반적인 코틀린 패턴임

- `create()` 메소드를 통해 Retrofit srevice는 `MarsApiService` 인터페이스와 함께 그 자체의 Retrofit service를 만듬, 이 자체가 비용이 크기 때문에 Retrofit service를 늦은 초기화를 함

- 그리고 앱이 오직 하나의 Retrofit service 인스턴스를 사용하기 때문에 `MarsApi`라는 public 객체를 사용해 앱의 나머지 부분의 서비스를 노출함

```kotlin
object MarsApi {
    val retrofitService : MarsApiService by lazy { 
       retrofit.create(MarsApiService::class.java) }
}
```

- 이제 `MarsApi.retrofitService`를 호출할 때마다 `MarsApiServiec`를 구현한 싱글톤 Retrofit 객체를 얻을 것임

- 위와 같이 정의가 완료됐으면 이제 `OverviewViewModel`에서 사용함, 위에서 정의한 내용을 호출함, 이 메소드는 `Call` 객체를 반환할 것임, 그리고 `enqueue()`를 호출해 백그라운드 스레드에 네트워크 요청을 시작하게끔 사용함

- 그리고 그와 관련된 메소드를 구현함

```kotlin
private fun getMarsRealEstateProperties() {
        MarsApi.retrofitService.getProperties().enqueue(
            object: Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    TODO("Not yet implemented")
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
    }
```

- 그리고 `onFailure()` callback을 통해 응답이 실패할 경우 응답 상태를 갱신하게 정의함

```kotlin
override fun onFailure(call: Call<String>, t: Throwable) {
   _response.value = "Failure: " + t.message
}
```

- 응답 성공시에는 `_response`에 response body를 설정하게 함 `onResponse()` callback은 요청 성공시 호출되고 web service는 응답을 리턴함

```kotlin
override fun onResponse(call: Call<String>, 
   response: Response<String>) {
      _response.value = response.body()
}
```

- 그리고 인터넷 허가를 추가함

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### Parse the JSON response with Moshi
- 여기서 JSON 객체에 대해서 단순하게 이를 받는게 아닌 사용가능하게 써야 하는데 이 때 Moshi를 통해서 JSON string을 Kotlin 객체로써 변환할 수 있음

- Retrofit은 Moshi와 잘 작동하는 converter를 가지고 있음, 그래서 이 Moshi를 활용해서 Retrofit을 통해서 받은 JSON 응답값을 사용하기 유용한 Mars Property Kotlin 객체로 변환함, 이 Moshi의 의존성을 추가함

- JSON 응답값을 보면 아래와 같이 옴

```markdown
[{"price":450000,
"id":"424906",
"type":"rent",
"img_src":"http://mars.jpl.nasa.gov/msl-raw-images/msss/01000/mcam/1000ML0044631300305227E03_DXXX.jpg"},
...]
```

- 각각의 객체는 `,`으로 구분된 name-value 쌍의 집합을 포함함, name은 `" "`으로 감싸고 value는 숫자, string, boolean 혹은 다른 객체나 배열일 수도 있음, value가 string이면 `" "`로 감쌈

- 위의 예를 보면 `price` 요소는 $450,000이고 `img_src`는 server에 저장된 이미지 파일의 위치를 URL로 둠

- JSON을 분석해보면 `price`는 Mars property의 가격이며 숫자이고 `id`는 property의 ID이며 string이고 `type`은 `rent`나 `buy`이고 `img_src`는 image URL의 string임 

- Moshi는 이 JSON 데이터를 kotlin 객체로 변환해줌, 이를 위해서 파싱한 결과를 저장할 Kotlin data class가 필요함, 아래와 같이 만듬

```kotlin
data class MarsProperty(
    val id: String, val img_src: String,
    val type: String,
    val price: Double
)
```

- 여기서 알아둘 것은 `MarsProperty` 클래스의 각각의 변수는 JSON 객체의 key name과 동일함 JSON 응답과 타입을 맞추기 위해서 모든 값을 `String`을 사용함 `price`는 `Double`을 씀, `Double`은 어떤 JSON 숫자여도 나타낼 수 있음

- Moshi가 JSON을 파싱할 때 keys를 name에 의해 매칭하고 적절한 값으로 data 객체를 채움

- 그래서 `img_src`는 아래와 같이 수정함

```kotlin
@Json(name = "img_src") val imgSrcUrl: String,
```

- 가끔씩 JSON 응답의 key name이 Kotlin 요소와 혼동되거나 coding style에 안 맞을 수 있음, 그래서 위의 예시처럼 `img_src`는 Kotlin의 카멜 케이스 스타일이 아님 

- 그래서 이를 JSON 응답값과 다르게 쓰고 활용하기 위해서 `@Json` 어노테이션을 활용함, 그러면 이름이 다르더라도 `imgSrcUrl`의 변수와 `img_src`는 `@Json(name = "img_src")`를 통해서 매핑이 됨

- 이제 Moshi를 썼기 때문에 `MarsApiService`를 수정함 먼저 Moshi 인스턴스를 아래와 같이 만듬

```kotlin
private val moshi = Moshi.Builder()
   .add(KotlinJsonAdapterFactory())
   .build()
```

- 그리고 `Retrofit`인스턴스 생성에 대해서 수정을 함

```kotlin
// Retrofit 객체 생성을 위한 Retrofit builder
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()
```

- 그리고 응답값에 대해서 data class인 `MarsProperty`를 만들었기 때문에 인터페이스에서 반환으로 `MarsProperty` 객체의 list를 반환하도록 선언함

```kotlin
interface MarsApiService {
   @GET("realestate")
   fun getProperties():
      Call<List<MarsProperty>>
}
```

- 추가로 `OverviewViewModel`에서 네트워크 통신 호출 부분에서도 callback과 구현한 메소드의 응답과 call을 수정함

```kotlin
private fun getMarsRealEstateProperties() {
        MarsApi.retrofitService.getProperties().enqueue(
            object: Callback<List<MarsProperty>> {
                override fun onResponse(call: Call<List<MarsProperty>>, response: Response<List<MarsProperty>>) {
                    _response.value = "Success: ${response.body()?.size} Mars properties retrieved"
                }

                override fun onFailure(call: Call<List<MarsProperty>>, t: Throwable) {
                    _response.value = "Failure: " + t.message
                }

            })
    }
```

### Use coroutines with Retrofit
- 위에서 callback을 통해서 API 호출을 하였는데 이 부분에 대해서 call back이 아닌 coroutine을 사용해서 더 효율적으로 관리할 수 있음

- 먼저 `MarsApiService`에서 `getProperties()`를 suspend function으로 만듬

```kotlin
@GET("realestate")
suspend fun getProperties(): List<MarsProperty>
```

- 그리고 위에서 사용한 `OverviewViewModel`에서의 네트워크 호출을 다 지우고 coroutine을 활용한 방식으로 개선을 함

```kotlin
private fun getMarsRealEstateProperties() {
        // 코루틴을 활용해 API 통신을 함
        viewModelScope.launch {
            try {
                // API 통신 성공시 처리
                val listResult = MarsApi.retrofitService.getProperties()
                _response.value = "Success ${listResult.size} Mars properties retrieved"
            } catch (e: Exception) {
                // API 통신 실패시 처리
                _response.value = "Failure: ${e.message}"
            }
        }
    }
```