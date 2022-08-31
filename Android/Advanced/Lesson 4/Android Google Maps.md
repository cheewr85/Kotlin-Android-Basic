- Google Map은 위성 이미지, map에 대한 robust UI control과 location tracking, location markers의 기능을 제공해줌

- Google Maps에 나만이 알고 있는 정보에 대해서 보여주게끔 값을 추가할 수 있고 AR 게임 같은 거로 활용도 할 수 있음

### Set up the project and get an API Key
- Google Cloud에서 API Key를 발급받고 Google Maps 기능을 사용할 수 있음

- 그리고 해당 Template으로 만든 뒤, API Key를 발급받고 xml 상에서 추가해서 서비스를 사용할 수 있음

### Add map types
![one](/Android/img/fiftysix.png)

- 위와 같이 Google Maps는 여러 타입을 가지고 있음, 이를 각각 활용해서 처리할 수 있음

- 먼저 아래와 같이 Menu를 먼저 만듬

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android"
   xmlns:app="http://schemas.android.com/apk/res-auto">
   <item
       android:id="@+id/normal_map"
       android:title="@string/normal_map"
       app:showAsAction="never" />
   <item
       android:id="@+id/hybrid_map"
       android:title="@string/hybrid_map"
       app:showAsAction="never" />
   <item
       android:id="@+id/satellite_map"
       android:title="@string/satellite_map"
       app:showAsAction="never" />
   <item
       android:id="@+id/terrain_map"
       android:title="@string/terrain_map"
       app:showAsAction="never" />
</menu>
```

- 그리고 title에 대한 string을 추가함

```xml
<resources>
   ...
   <string name="normal_map">Normal Map</string>
   <string name="hybrid_map">Hybrid Map</string>
   <string name="satellite_map">Satellite Map</string>
   <string name="terrain_map">Terrain Map</string>
   <string name="lat_long_snippet">Lat: %1$.5f, Long: %2$.5f</string>
   <string name="dropped_pin">Dropped Pin</string>
   <string name="poi">poi</string>
</resources>
```

- 그런 다음 `onCreateOptionsMenu`를 오버라이딩 한 뒤 앞서 설정한 menu.xml을 설정함

```kotlin
override fun onCreateOptionsMenu(menu: Menu?): Boolean {
   val inflater = menuInflater
   inflater.inflate(R.menu.map_options, menu)
   return true
}
```

- 그리고 각각 옵션을 선택했을 때 mapType이 바뀌게끔 아래와 같이 오버라이딩함

```kotlin
override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
   // Change the map type based on the user's selection.
   R.id.normal_map -> {
       map.mapType = GoogleMap.MAP_TYPE_NORMAL
       true
   }
   R.id.hybrid_map -> {
       map.mapType = GoogleMap.MAP_TYPE_HYBRID
       true
   }
   R.id.satellite_map -> {
       map.mapType = GoogleMap.MAP_TYPE_SATELLITE
       true
   }
   R.id.terrain_map -> {
       map.mapType = GoogleMap.MAP_TYPE_TERRAIN
       true
   }
   else -> super.onOptionsItemSelected(item)
}
```

- 그러면 상단 Appbar에 생기고 옵션에 따라서 지도 스타일을 다르게 할 수 있음

### Add markers
- `onMapReady`에 주요 도시에 Google Maps가 만든 marker를 등록하는 콜백이 존재함, 여기서 zoom과 특정 위치에 marker를 찍는 기능을 추가할 것임

- 그래서 기존의 코드를 지우고 위도와 경도를 저장해둠 아래와 같이(위도 경도는 해당 부분 참고![가이드](https://support.google.com/maps/answer/18539?co=GENIE.Platform%3DDesktop&hl=en))

```kotlin
val latitude = 37.422160
val longitude = -122.084270
```

- 그리고 `LatLng`객체를 만듬

```kotlin
val homeLatLng = LatLng(latitude, longitude)
```

- 얼마나 줌할지에 대해서도 정해줌(이 ZoomLevel은 1은 World, 5는 Landmass,continent, 10은 city, 15는 Streets, 20은 Buildings임)

```kotlin
val zoomLevel = 15f
```

- 그리고 해당 위치로 camera가 가게끔 map 객체에 설정을 둠, 이 때 zoom level도 적용됨

```kotlin
map.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, zoomLevel))
```

- 그리고 해당 부분의 마커를 찍게함

```kotlin
map.addMarker(MarkerOptions().position(homeLatLng))
```

- 완성코드는 아래와 같음, 이제 앱을 시작하면 해당 부분으로 넘어가면서 줌과 마커까지 찍히게 됨

```kotlin
override fun onMapReady(googleMap: GoogleMap) {
   map = googleMap

   //These coordinates represent the latitude and longitude of the Googleplex.
   val latitude = 37.422160
   val longitude = -122.084270
   val zoomLevel = 15f

   val homeLatLng = LatLng(latitude, longitude)
   map.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, zoomLevel))
   map.addMarker(MarkerOptions().position(homeLatLng))
}
```

- 그 다음 유저가 길게 누르면 marker를 추가하게 허용할 수 있음

- 아래와 같이 리스너를 추가하고 marker에 대해서 처리를 함

```kotlin
private fun setMapLongClick(map: GoogleMap) {
   map.setOnMapLongClickListener { latLng ->
       map.addMarker(
           MarkerOptions()
               .position(latLng)
       )
   }
}
```

- 그리고 앞서 한 `onMapReady`에 넣음

```kotlin
override fun onMapReady(googleMap: GoogleMap) {
   ...
  
   setMapLongClick(map)
}
```

- 그러면 위와 같이 하면 길게 누른 부분의 위도 경도가 알아서 처리되고 해당 위치에 마커가 추가됨

- 그 다음 marker에 대한 정보를 담은 창을 추가할 수 있음, 이 역시 리스너를 추가해서 아래와 같이 넣을 수 있음 snippet으로 선언해서 넣음, 그러면 `title`과 함께 snippet에 설정한 위도 경도 내용이 담겨서 추가됨

```kotlin
private fun setMapLongClick(map: GoogleMap) {
   map.setOnMapLongClickListener { latLng ->
       // A Snippet is Additional text that's displayed below the title.
       val snippet = String.format(
           Locale.getDefault(),
           "Lat: %1$.5f, Long: %2$.5f",
           latLng.latitude,
           latLng.longitude
       )
       map.addMarker(
           MarkerOptions()
               .position(latLng)
               .title(getString(R.string.dropped_pin))
               .snippet(snippet)
              
       )
   }
}
```

- POI는 그와 상응하는 icon과 함께 맵에 나타나는 것이 기본 값임, 이는 공원, 학교, 정부기관등이 있음

- map type이 `normal`이면 business POI또한 나타나게 됨, 이는 상점, 음식점, 호텔을 의미함

- 이 때 이와 관련된 클릭 리스너를 통해서 user가 POI를 클릭할 때 map의 마커가 바로 위치하게 할 것임, 그리고 POI 이름을 포함한 정보 창을 보이게 할 것임

- 아래와 같이 poi기준으로 마커와 정보창이 뜨게끔 메소드를 추가함

```kotlin
private fun setPoiClick(map: GoogleMap) {
   map.setOnPoiClickListener { poi ->
       val poiMarker = map.addMarker(
           MarkerOptions()
               .position(poi.latLng)
               .title(poi.name)
       )
       poiMarker.showInfoWindow()
   }
}
```

- 그리고 `onMapReady`에 추가함

```kotlin
override fun onMapReady(googleMap: GoogleMap) {
   ...

   setPoiClick(map)
}
```

### Style your map
- `GoogleMap`객체를 통해서 `MapFragment`에 있는 content를 커스텀할 수 있음

- 이를 적용하기 위해서 map에 적용할 기능들에 대해서 JSON 파일을 생성해서 처리할 수 있음, 이에 대해서 Maps Platform Styling Wizard를 통해서 map의 style에 대해 JSON을 생성할 수 있음(![참고](https://mapstyle.withgoogle.com/))

- 여기서 생성된 JSON 코드에 대해서 `raw` 디렉토리를 만들고 해당 코드를 붙여넣음

- logging 목적의 TAG를 먼저 만듬

```kotlin
private val TAG = MapsActivity::class.java.simpleName
```

- 그리고 새로운 함수를 만들어서 JSON 스타일의 map을 `setMapStyle`을 통해 `GoogleMap`객체에 적용을 함

```kotlin
private fun setMapStyle(map: GoogleMap) {
   try {
       // Customize the styling of the base map using a JSON object defined
       // in a raw resource file.
       val success = map.setMapStyle(
           MapStyleOptions.loadRawResourceStyle(
               this,
               R.raw.map_style
           )
       )
   }
}
```

- 그리고 실패한 경우와 예외 처리를 아래와 같이 추가함

```kotlin
private fun setMapStyle(map: GoogleMap) {
   try {
       // Customize the styling of the base map using a JSON object defined
       // in a raw resource file.
       val success = map.setMapStyle(
           MapStyleOptions.loadRawResourceStyle(
               this,
               R.raw.map_style
           )
       )

       if (!success) {
           Log.e(TAG, "Style parsing failed.")
       }
   } catch (e: Resources.NotFoundException) {
       Log.e(TAG, "Can't find style. Error: ", e)
   }
}
```

- 마지막으로 `onMapReady`에 해당 메소드를 추가함, 그러면 앞서 정한 style대로 map이 바뀜

```kotlin
override fun onMapReady(googleMap: GoogleMap) {
   ...
   setMapStyle(map)
}
```

- 추가로 marker에 대해서도 style을 적용할 수 있음, 이 부분은 `onMapLongClick`에서 추가를 함, 그리고 해당 부분에서 `MarkerOptiopns`의 일부로 추가를 아래와 같이 하면 Marker가 바뀜

```kotlin
map.setOnMapLongClickListener { latLng ->
   // A snippet is additional text that's displayed after the title.
   val snippet = String.format(
       Locale.getDefault(),
       "Lat: %1$.5f, Long: %2$.5f",
       latLng.latitude,
       latLng.longitude
   )
   map.addMarker(
       MarkerOptions()
           .position(latLng)
           .title(getString(R.string.dropped_pin))
           .snippet(snippet)
         .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
   )
}
```

### Add an overlay
- 해당 테크닉은 특별한 타입의 위치를 강조하길 원하면 유용함, 먼저 Android Image를 다운 받은 뒤, drawable 파일에 넣음

- 그리고 `onMapReady`에서 `GroundOverlayOptions` 객체를 추가해서 정의함

```kotlin
val androidOverlay = GroundOverlayOptions()
```

- 그리고 앞서 다운받은 이미지를 받기 위해서 `BitmapDescriptionFactory`를 통해서 해당 resource를 가져옴

```kotlin
val androidOverlay = GroundOverlayOptions()
   .image(BitmapDescriptorFactory.fromResource(R.drawable.android))
```

- 그리고 크기를 먼저 설정하고 위치를 설정하기 위해서 `position`을 호출해서 처리함

```kotlin
val overlaySize = 100f
val androidOverlay = GroundOverlayOptions()
   .image(BitmapDescriptorFactory.fromResource(R.drawable.android))
   .position(homeLatLng, overlaySize)
```

- 그 다음, map에서 추가함

```kotlin
map.addGroundOverlay(androidOverlay)
```

### Enable location tracking
- 위치를 지도에 나타내기 위해서 location-data layer를 쓸 수 있음, 해당 버튼으로 기기의 위치를 알 수 있게됨

- 먼저 그러기 위해서 manifest에서 권한을 추가해야함

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

- 그리고 Activity에서 상수로 권한 처리를 별도로 정의하고 권한을 확인하기 위해서 아래와 같이 메소드를 추가함, 유저의 권한이 확인되었는지 파악함

```kotlin
private val REQUEST_LOCATION_PERMISSION = 1

private fun isPermissionGranted() : Boolean {
  return ContextCompat.checkSelfPermission(
       this,
      Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
}
```

- 그리고 위치를 추적하기 위해서 Activity에서 매개변수가 없는 `enableMyLocation`을 호출함, 여기서 return을 아무것도 하지 않지만, 권한을 확인하고 확인되면 location layer를 허가하게 함

```kotlin
private fun enableMyLocation() {
   if (isPermissionGranted()) {
       map.isMyLocationEnabled = true 
   }
   else {
       ActivityCompat.requestPermissions(
           this,
           arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
           REQUEST_LOCATION_PERMISSION
       )
   }
}
```

- 그리고 `onMapReady`에 추가함

```kotlin
override fun onMapReady(googleMap: GoogleMap) {
   ...
   enableMyLocation()
}
```

- 그리고 앞서 `requestCode`에 대해서 확인을 해서 권한이 확실히 체크됐는지 확인하게 아래 메소드를 오버라이딩해서 처리함

```kotlin
override fun onRequestPermissionsResult(
   requestCode: Int,
   permissions: Array<String>,
   grantResults: IntArray) {
   if (requestCode == REQUEST_LOCATION_PERMISSION) {
       if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
           enableMyLocation()
       }
   }
}
```

![유사실습](https://github.com/cheewr85/SampleProject/tree/master/UpperIntermediate/LocationMap)
![유사실습2](https://github.com/cheewr85/SampleProject/tree/master/Intermediate/AirBaB)
