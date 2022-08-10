### Display an internet image
- web URL로부터 사진을 보여주는 것은 직관적인 것 같지만 꽤 여러가지의 작업이 수반됨

- 이미지가 다운로드 되고 내부적으로 저장되며 Android가 사용할 수 있는 이미지 포맷으로 압축된 것으로 decode를 함

- 그리고 이미지는 im-memory cache와 storage-based cache 둘 다 캐싱되어야 함

- 그러면서 이 모든 작업은 우선순위가 낮은 백그라운드 스레드에서 진행되어 UI는 응답 가능하게 남아있어야 함

- 또한 network와 CPU 성능을 최고로 유지하기 위해 하나 이상의 이미지를 한번에 fetch 하고 decode하길 바랄 것임

- 운 좋게 `Glide`라는 라이브러리가 이미지를 download, buffer, cache해줌, 이것이 없다면 상당히 많은 작업을 처리해줘야함

- Glide는 기본적으로 2가지를 필요로 함, load하고 보여줄 image의 URL과 이미지를 실제로 보여줄 `ImageView`객체를 필요로 함

- 먼저 Glide의 의존성을 추가함

```kotlin
implementation "com.github.bumptech.glide:glide:$version_glide"
```

- 그리고 `MarsProperty`객체를 단일 live data를 담기 위해서 `OverviewViewModel`을 갱신함

```kotlin
// 사진 URL을 담기 위한 LiveData 정의
    private val _property = MutableLiveData<MarsProperty>()
    
    val property: LiveData<MarsProperty>
        get() = _property
```

- 그리고 `getMarsRealEstateProperties()`메소드 안에 `try/catch {}`안에 `MarsProperty`객체가 이용 가능한 상태에서 해당 값을 쓸 수 있게 처리함

```kotlin
private fun getMarsRealEstateProperties() {
        // 코루틴을 활용해 API 통신을 함
        viewModelScope.launch {
            try {
                // API 통신 성공시 처리
                val listResult = MarsApi.retrofitService.getProperties()
                _response.value = "Success ${listResult.size} Mars properties retrieved"
                // API 통신 성공시 받은 값에서 해당 응답값을 받음(그 body 안에서 property 중 image URL을 받기 위해)
                if (listResult.size > 0) {
                    _property.value = listResult[0]
                }
            } catch (e: Exception) {
                // API 통신 실패시 처리
                _response.value = "Failure: ${e.message}"
            }
        }
    }
```

- 그리고 `layout` 파일에서 Data binding 활용을 위해 아래와 같이 적용함

```xml
<TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@{viewModel.property.imgSrcUrl}"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintRight_toRightOf="parent"
            app:layout_constraintTop_toTopOf="parent" />
```

- 이제 binding adapter를 사용해서 `ImageView`와 연결된 XML 속성에서 URL을 가져오고 Glide를 사용하여 해당 URL에 이미지를 로드함

- binding adapter는 데이터가 변경될 때 사용자 지정 동작을 제공하기 위해 View와 binding 된 data 사이에 있는 extension function임, 여기선 Glide를 호출하여 URL의 image를 load하여 `ImageView`에 넣음

- `ImageView`와 `String`을 매개변수로 `bindImage()`함수를 만듬, `@BindingAdapter` 어노테이션을 통해서 XML item이 `imageUrl` 요소를 가지고 있다면 이 binding adapter를 실행하는 것을 원한다고 data binding에 알려줌

- 그리고 먼저 URL 값을 Uri 객체로 변환함, `Uri` 객체는 HTTPS scheme으로 서버는 image를 secure scheme으로 pull 하기 원하기 때문에 이를 사용함, 그래서 아래와 같이 코드를 사용함

```kotlin
@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        // URL 값을 Uri 객체로 변환함
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
    }

}

```

- 그리고 `Glide`와 관련된 설정으로 `Uri` 객체를 불러와서 `ImageView`에 넣음

```kotlin
// Glide를 통해 imageView에 load한 이미지를 넣음
        Glide.with(imgView.context)
            .load(imgUri)
            .into(imgView)
```

- 그런 다음 layout과 fragment를 업데이트 함

- 먼저 `gridview_item`을 업데이트함, 추후 `RecyclerView`에 적용할 것인데 임시로 image만 보이게 씀, `data` 태그를 추가함

- 그리고 `imageUrl`을 추가해서 `viewModel`에서의 요소를 넣음

```xml
<data>
        <variable
            name="viewModel"
            type="com.example.android.marsrealestate.overview.OverviewViewModel" />
    </data>
    
    <ImageView
        android:id="@+id/mars_image"
        android:layout_width="match_parent"
        android:layout_height="170dp"
        android:scaleType="centerCrop"
        android:adjustViewBounds="true"
        android:padding="2dp"
        app:imageUrl="@{viewModel.property.imgSrcUrl}"
        tools:src="@tools:sample/backgrounds/scenic"/>
``` 

- 그리고 단일 이미지만 보이게 하기 위해서 Fragment에서 아래의 binding을 추가함 

```kotlin
val binding = GridViewItemBinding.inflate(inflater)
```

- 그리고 추가로 할 수 있는 작업이 있는데 Glide는 로딩 중일 때와 이미지 로드를 실패했을 때의 에러 이미지를 보여줄 수 있게끔 설정함

- 여기서 `Glide`에서 `apply()` 함수를 사용 `placeholder`를 통해 로딩하는 이미지와 `error`를 통해 에러 발생시 나타내는 이미지를 설정할 수 있음, 아래와 같이 수정 가능

```kotlin
@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = 
           imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
                .load(imgUri)
                .apply(RequestOptions()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image))
                .into(imgView)
    }
}
```

### Display a grid of images with a RecyclerView
- 이제 이를 `GridLayoutManager`의 `RecyclerView`로써 이미지를 보여주게 할 것임

- 먼저 `OverviewViewModel`에서 `LiveData`에 대한 `_property`를 아래와 같이 수정함, 이제 `MarsProperty`객체의 list로써 쓸 것임

```kotlin
// 사진 URL 객체들을 담기 위한 LiveData 정의
    private val _properties = MutableLiveData<List<MarsProperty>>()

    val properties: LiveData<List<MarsProperty>>
        get() = _properties
```

- 그리고 `getMarsRealEstateProperties()` 메소드에서 내부에서도 코드를 아래와 같이 수정함

```kotlin
try {
    _properties.value = MarsApi.retrofitService.getProperties()   
    _response.value = "Success: Mars properties retrieved"
} catch (e: Exception) {
   _response.value = "Failure: ${e.message}"
}
```

- 그 다음 `grid_view_item`에서도 data binding의 `data` 태그와 `ImageView`를 수정함

```xml
<variable
   name="property"
   type="com.example.android.marsrealestate.network.MarsProperty" />
```
```xml
app:imageUrl="@{property.imgSrcUrl}"
```

- 그리고 `OverViewFragment`도 단일 이미지가 아닌 Fragment를 보이게끔 수정하고 추가로 `RecyclerView`도 Fragment에 추가함

```kotlin
val binding = FragmentOverviewBinding.inflate(inflater)
 // val binding = GridViewItemBinding.inflate(inflater)
```

```xml
<androidx.recyclerview.widget.RecyclerView
            android:id="@+id/photos_grid"
            android:layout_width="0dp"
            android:layout_height="0dp"
            android:padding="6dp"
            android:clipToPadding="false"
            app:layoutManager=
               "androidx.recyclerview.widget.GridLayoutManager"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintRight_toRightOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:spanCount="2"
            tools:itemCount="16"
            tools:listitem="@layout/grid_view_item" />
```

- 그 다음 `RecyclerView` adapter를 통해 `RecyclerView`의 data를 bind할 것임

- 먼저 아래와 같이 list item type과 view holder와 `DiffUtil.ItemCallback`이 생성자로 필요한 `ListAdapter`를 상속받은 클래스를 만듬

```kotlin
class PhotoGridAdapter : ListAdapter<MarsProperty,
        PhotoGridAdapter.MarsPropertyViewHolder>(DiffCallback) {

}
```

- 그리고 Adapter에 필요한 메소드를 아래와 같이 오버라이딩 함

```kotlin
override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoGridAdapter.MarsPropertyViewHolder {
   TODO("not implemented") 
}

override fun onBindViewHolder(holder: PhotoGridAdapter.MarsPropertyViewHolder, position: Int) {
   TODO("not implemented") 
}
```

- 그리고 `DiffCallback`을 선언하고 `DiffUtil.ItemCallback`을 상속받음, 그리고 내부 메소드를 구현

```kotlin
// DiffUtil 선언
    companion object DiffCallback : DiffUtil.ItemCallback<MarsProperty>() {
        override fun areItemsTheSame(oldItem: MarsProperty, newItem: MarsProperty): Boolean {
            // 객체 레퍼런스가 같은지 확인(객체로 선언했으므로)
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: MarsProperty, newItem: MarsProperty): Boolean {
            // id가 같은지 확인
            return oldItem.id == newItem.id
        }

    }
```

- 그리고 추가로 `RecyclerView.ViewHolder`를 상속받은 `MarsPropertyViewHolder` 내부 클래스를 정의함

```kotlin
// ViewHolder 생성
    class MarsPropertyViewHolder(private var binding: GridViewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        // MarsProperty 요소를 인자로 받아 객체와 binding에 있는 요소를 bind함
        fun bind(marsProperty: MarsProperty) {
            binding.property = marsProperty
            // 업데이트와 실행을 즉시하기 위해서 호출함
            binding.executePendingBindings()
        }
    }
```

- 그리고 Adapter에서 `onCreateViewHolder`와 `onBindViewHolder`를 수정함

```kotlin
override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoGridAdapter.MarsPropertyViewHolder {
        // ViewHolder에 대해서 Layout을 리턴함
        return MarsPropertyViewHolder(GridViewItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: PhotoGridAdapter.MarsPropertyViewHolder, position: Int) {
        // 현재 RecyclerView position에 있는 MarsProperty 객체를 getItem으로 가져옴
        val marsProperty = getItem(position)
        // 그리고 bind 메소드의 매개변수로 넘김
        holder.bind(marsProperty)
    }
```

- 마지막으로 `RecyclerView`에 data를 설정하기 위해 `BindingAdapter`를 사용하는것으로 `MarsProperty`객체의 list를 위한 `LiveData`를 자동으로 observe 할 수 있음(data binding을 통해)

- 이때 binding adapter는 `MarsProperty` list가 변할 때 자동으로 호출됨

```kotlin
// RecyclerView의 data를 설정하는 함수
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<MarsProperty>?) {
    // PhotoGridAdapter로 캐스팅해서 넘겨받은 List 데이터를 연결함
    val adapter = recyclerView.adapter as PhotoGridAdapter
    adapter.submitList(data)
}

```

- 그리고 `fragment_overview.xml`에 있는 `RecyclerView` 요소에 data binding을 사용해 `viewModel.properties`를 추가함

```xml
app:listData="@{viewModel.properties}"
```

- 그 다음 `OverviewFragment`에서 `onCreateView`에 `binding` 객체를 통해서 adapter를 초기화함

```kotlin
// RecyclerView adapter를 초기화함
        binding.photosGrid.adapter = PhotoGridAdapter()
```

### Add error handling in RecyclerView
- 만약 네트워크 연결에 오류가 있다면 이 부분에 대해서 에러 아이콘을 보여주게 할 것임

- 이를 위해서 먼저 web 요청에 대해 상태를 저장한 `LiveData`와 enum class를 아래와 같이 만들 것임

```kotlin
// 상태를 나타내는 enum class
enum class MarsApiStatus { LOADING, ERROR, DONE }
```

- 그 다음 `getMarsRealEstateProperties()`메소드를 업데이트함, enum class의 값으로 처리를 하는 것으로 바꿈

```kotlin
private fun getMarsRealEstateProperties() {
        // 로딩중 상태로 갱신(메소드 호출시)
        _status.value = MarsApiStatus.LOADING
        // 코루틴을 활용해 API 통신을 함
        viewModelScope.launch {
            try {
                // API 통신 성공시 해당 value를 성공적으로 받아왔는지 체크
                _properties.value = MarsApi.retrofitService.getProperties()
                _status.value = MarsApiStatus.DONE
            } catch (e: Exception) {
                // API 통신 실패시 처리
                _status.value = MarsApiStatus.ERROR
                // LiveData를 empty 값으로 나타냄
                _properties.value = ArrayList()
            }
        }
    }
```

- view model의 상태값을 지정했으므로 `ImageView`를 상태에 따라서 다른 이미지가 보이게끔 data binding과 연결하여서 binding adapter를 추가할 것임

```kotlin
// Status 값에 따라서 ImageView를 처리하는 메소드
@BindingAdapter("marsApiStatus")
fun bindStatus(statusImageView: ImageView, status: MarsApiStatus?) {
    // status 상태값에 따라 ImageView를 다르게 보이게 함
    when (status) {
        MarsApiStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        
        MarsApiStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        
        MarsApiStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
    }
}
```

- 마지막으로 `ImageView`를 추가함, 이 `ImageView`는 `RecyclerView`의 상태에 따라서 보여주기 위한 것이므로 해당 xml을 추가한 뒤 ViewModel을 활용하여 상태에 따라 설정을 바꾸어 주면 됨

```xml
<ImageView
   android:id="@+id/status_image"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent"
    app:layout_constraintTop_toTopOf="parent"
    app:marsApiStatus="@{viewModel.status}" />
```