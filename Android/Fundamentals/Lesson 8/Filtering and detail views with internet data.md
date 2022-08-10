### Add for sale images to the overview
- 앞서 예시앱에서 `MarsProperty` 클래스에서 image에 대한 내용만 활용하고 다른 데이터에 대해서는 활용하지 않음

- 이 부분에 대해서 다른 데이터를 활용하여 상세화면이나 옵션화면 등 추가적인 로직에 활용할 것임

- 먼저 type에 대해서 체크를 하게끔 그 값을 쓸 수 있게 아래와 같이 추가함

```kotlin
data class MarsProperty(
    val id: String, @Json(name = "img_src") val imgSrcUrl: String,
    val type: String,
    val price: Double
) {
    // type이 rent인 경우 true 반환함
    val isRental
        get() = type == "rent"
}
```

- 여기서 grid item layout에서 property image에 type이 만약 buy인 경우 살 수 있다는 표시를 추가하기 위해서 layout을 전반적으로 수정할 것임

- 달러 이미지가 있는 ImageView를 하나 더 추가하고 type에 따라서 보이는지 안 보이는지 처리를 할 수 있게 처리할 것임

```xml
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools">

    <data>

        <import type="android.view.View" />

        <variable
            name="property"
            type="com.example.android.marsrealestate.network.MarsProperty" />
    </data>

    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="170dp">

        <ImageView
            android:id="@+id/mars_image"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:adjustViewBounds="true"
            android:padding="2dp"
            android:scaleType="centerCrop"
            app:imageUrl="@{property.imgSrcUrl}"
            tools:src="@tools:sample/backgrounds/scenic" />
        
        <ImageView
            android:id="@+id/mars_property_type"
            android:layout_width="wrap_content"
            android:layout_height="45dp"
            android:layout_gravity="bottom|end"
            android:adjustViewBounds="true"
            android:padding="5dp"
            android:scaleType="fitCenter"
            android:visibility="@{property.rental ? View.GONE : View.VISIBLE}"
            android:src="@drawable/ic_for_sale_outline"
            tools:src="@drawable/ic_for_sale_outline"/>
        
    </FrameLayout>
</layout>

```

### Filter the results
- 현재 상태에선 type의 상관없이 모든 요소를 보여주게끔 설정해뒀는데 이때 옵션 메뉴에서 선택을 해서 rentals, buy의 옵션을 선택하면 그에 해당하는 것만 보여주게끔 필터링 할 수 있음

- 이런 필터링을 하기 위해서 각각 `MarsProperty`를 검사하여서 일치하는 것을 확인해야함, 여기서 web service에선 이를 query parameter를 통해서 이 타입의 요소를 정의함 `rent`이거나 `buy`이거나 그래서 이를 filter를 하는데 있어서 아래와 같이 web service URL을 설정하여 처리할 수 있음

- `https://android-kotlin-fun-mars-server.appspot.com/realestate?filter=buy`

- 그래서 그에 맞게 `MarsApiService`에 대해서 query 옵션을 추가해서 서비스를 요청하게끔 처리할 것임, 먼저 그와 관련된 enum class를 추가함

```kotlin
// web service가 예상하는 query value와 매칭하기 위해 정의한 상수 값들
enum class MarsApiFilter(val value: String) {
    SHOW_RENT("rent"),
    SHOW_BUY("buy"),
    SHOW_ALL("all")
}
```

- 그리고 이 filter query를 적용하기 위해서 `getProperties` 메소드를 아래와 같이 수정함

```kotlin
suspend fun getProperties(@Query("filter") type: String): List<MarsProperty>  
```

- 그런 다음 view model 역시 수정함, `OverviewViewModel`에서 이 `getMarsRealEstateProperties`메소드에 `MarsApiFilter`를 매개변수로 추가함

```kotlin
// filter를 추가해서 API 요청을 filter 기준으로 나뉘게 처리함
    private fun getMarsRealEstateProperties(filter: MarsApiFilter) {
        // 로딩중 상태로 갱신(메소드 호출시)
        _status.value = MarsApiStatus.LOADING
        // 코루틴을 활용해 API 통신을 함
        viewModelScope.launch {
            try {
                // API 통신 성공시 해당 value를 성공적으로 받아왔는지 체크
                _properties.value = MarsApi.retrofitService.getProperties(filter.value)
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

- 그리고 `init`에서도 `filter` 조건을 초기화함, 기본값으로 모두 보여주는 것으로 함

```kotlin
init {
   getMarsRealEstateProperties(MarsApiFilter.SHOW_ALL)
}
```

- 마지막으로 `updateFilter()` 메소드를 추가함

```kotlin
fun updateFilter(filter: MarsApiFilter) {
   getMarsRealEstateProperties(filter)
}
```

- 여기서 이제 이 Filter의 적용은 Options menu에서 하는데 이 부분은 `OverviewFragment`에서 옵션 메뉴 선택에 따라 수정할 수 있게끔 변경함

```kotlin
// 옵션 아이템에 따라서 Filter 설정을 변경하는 메소드
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.updateFilter(
            when (item.itemId) {
                R.id.show_rent_menu -> MarsApiFilter.SHOW_RENT
                R.id.show_buy_menu -> MarsApiFilter.SHOW_BUY
                else -> MarsApiFilter.SHOW_ALL
            }
        )
        return true
    }
```

### Create a detail page and set up navigation
- 이제 type에 상관없이 `DetailFragment`를 만들어서 `RecyclerView`의 itme을 클릭하면 세부 화면으로 넘어갈 수 있게끔 처리하는 View를 만듬, 이떄 `MarsProperty` 정보를 받아서 보여줌

```kotlin
class DetailViewModel(@Suppress("UNUSED_PARAMETER")marsProperty: MarsProperty, app: Application) : AndroidViewModel(app) {
    
    // 선택된 MarsProperty 값을 저장하기 위한 LiveData를 추가함
    private val _selectedProperty = MutableLiveData<MarsProperty>()
    val selectedProperty: LiveData<MarsProperty>
        get() = _selectedProperty
    
    init {
        // 선택된 MarsProperty 값을 설정하여 초기화 함
        _selectedProperty.value = marsProperty
    }
    
}
```

- 이제 `fragment_detail`에서 `data` 태그를 추가해서 data binding을 적용함

```xml
<data>
   <variable
       name="viewModel"
       type="com.example.android.marsrealestate.detail.DetailViewModel" />
</data>
```

- 그리고 `ImageView`에 해당 요소를 추가함

```xml
 app:imageUrl="@{viewModel.selectedProperty.imgSrcUrl}"
```

- 그 다음 `OverviewViewModel`에서 navigation에 대한 설정을 추가함

```kotlin
// 선택된 값에 대해서 Property를 처리하기 위한 LiveData 정의
    private val _navigateToSelectedProperty = MutableLiveData<MarsProperty>()
    val navigateToSelectedProperty: LiveData<MarsProperty>
        get() = _navigateToSelectedProperty
```

```kotlin
// 선택된 MarsProperty를 설정하기 위한 메소드
    fun displayPropertyDetails(marsProperty: MarsProperty) {
        _navigateToSelectedProperty.value = marsProperty
    }

    // 선택 후 완료 상태를 null로 처리하기 위한 메소드
    fun displayPropertyDetailsComplete() {
        _navigateToSelectedProperty.value = null
    }
```

- adapter와 fragment에서 click listener를 설정하기 위해 해당 클릭 리스너 클래스를 만듬, 그리고 `marsProperty`를 넘김, 그리고 `PhotoGridAdapter`에서 `OnClickListener` property를 추가함

```kotlin
class OnClickListener(val clickListener: (marsProperty:MarsProperty) -> Unit) {
        fun onClick(marsProperty: MarsProperty) = clickListener(marsProperty)
    }
```

```kotlin
class PhotoGridAdapter( private val onClickListener: OnClickListener ) :
       ListAdapter<MarsProperty,              
           PhotoGridAdapter.MarsPropertyViewHolder>(DiffCallback) {
```

- 그 다음 `onBindViewHolder()`에서 click listener를 정의함

```kotlin
override fun onBindViewHolder(holder: MarsPropertyViewHolder, position: Int) {
   val marsProperty = getItem(position)
   holder.itemView.setOnClickListener {
       onClickListener.onClick(marsProperty)
   }
   holder.bind(marsProperty)
}
```

- 마지막으로 `OverviewFragment`에서 `onCreateView`에서 `OnClickListener`를 처리해서 넘기도록 설정함

```kotlin
// RecyclerView adapter를 초기화함, 클릭 이벤트 처리를 추가함
        binding.photosGrid.adapter = PhotoGridAdapter(PhotoGridAdapter.OnClickListener {
            // 클릭한 MarsProperty를 넘김
            viewModel.displayPropertyDetails(it)
        })
```

- 앞서 한 작업을 통해 `RecyclerView`에서 아이템을 클릭하면 `DetailFragment`로 넘어가게끔 처리를 함, 하지만 아직 `MarsProperty`객체를 전달하게끔 하지 않았기 때문에 이를 navigation component의 Safe Args를 활용하여서 전달할 것임

- `nav_graph.xml`에서 detail framgent의 `fragmet` 태그안에 `MarsProperty`타입을 가진 `argument`를 추가함

```xml
<argument
   android:name="selectedProperty"
   app:argType="com.example.android.marsrealestate.network.MarsProperty"
   />
```

- 여기서 `MarsProperty`가 parcelable이 아니어서 에러가 생김, 이 `Parcelable` 인터페이스는 객체를 직렬화 될 수 있게끔 해 줌, 직렬화를 통해 객체 데이터는 fragment와 activity 사이에 전달될 수 있음

- 해당 앱에선 `MarsProperty` 객체가 data로 들어가 detail fragment에 Safe Args를 통해서 전달됨, 그래서 `MarsProperty`는 반드시 `Parcelable`인터페이스를 구현해야함

- 이는 아래와 같이 `@Parcelize`어노테이션을 통해 간단히 구현하게 할 수 있음

```kotlin
@Parcelize
data class MarsProperty (
       val id: String,
       @Json(name = "img_src") val imgSrcUrl: String,
       val type: String,
       val price: Double) : Parcelable {
```

- 그 다음 Fragment에서 overview에서 detail로 넘어가게끔 navigation 할 수 있게 `onCreateView`에서 ViewModel에서 `navigatedToSelectedProperty`를 observe 하는 것을 추가하고 `findNavController()`를 통해 action을 처리함

```kotlin
viewModel.navigateToSelectedProperty.observe(this, Observer {
            if (null != it) {
                // null이 아니면 즉 MarsProperty가 있으면 Detail로 넘어가게 처리함
                this.findNavController().navigate(
                    OverviewFragmentDirections.actionShowDetail(it))
                viewModel.displayPropertyDetailsComplete()
            }
        })
```

- 그 다음 `DetailFragment`에서 선택된 `MarsProperty` 객체를 Safe Args로부터 받아오는 것을 처리함

```kotlin
// 선택된 MarsProperty 객체를 가져옴
        val marsProperty = DetailFragmentArgs.fromBundle(arguments!!).selectedProperty
        
        // DetailViewModel 인스턴스를 얻기 위해 DetailViewModelFactory를 얻어서 처리함
        val viewModelFactory = DetailViewModelFactory(marsProperty, application)
        
        // DetailViewModel을 연결함
        binding.viewModel = ViewModelProvider(
            this, viewModelFactory).get(DetailViewModel::class.java)
```

### Create a more useful detail page
- `LiveData` transformation을 통해 view model에서 출력 양식에 대해서 수정을 할 것임

```kotlin
// 선택된 property가 rental이면 그에 적절한 string으로 transformation을 함
    val displayPropertyPrice = Transformations.map(selectedProperty) {
        app.applicationContext.getString(
            when (it.isRental) {
                true -> R.string.display_price_monthly_rental
                false -> R.string.display_price
            }, it.price)
    }
    
    // 그리고 여러개의 string을 묶는데 transformation을 함, 타입에 따라 변환을 함
    val displayPropertyType = Transformations.map(selectedProperty) {
        app.applicationContext.getString(R.string.display_type,
        app.applicationContext.getString(
            when (it.isRental) {
                true -> R.string.type_rent
                false -> R.string.type_sale
            }
        ))
    }
```

- 그리고 이를 `fragment_detail.xml`에서 적절한 text로 변환할 수 있게 text를 아래와 같이 처리함

```xml
<TextView
   android:id="@+id/property_type_text"
...
android:text="@{viewModel.displayPropertyType}"
...
   tools:text="To Rent" />

<TextView
   android:id="@+id/price_value_text"
...
android:text="@{viewModel.displayPropertyPrice}"
...
   tools:text="$100,000" />
```