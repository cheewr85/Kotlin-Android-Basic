- geofencing API는 특정 구역에 대해서 geofences를 치게 할 수 있음

- 이 때 유저가 만약 해당 geofence를 지나가게 되면 유저에게 fenced area에 있다고 알려주게끔 앱이 공지를 함

- 그래서 각각 transition의 상태가 아래와 같이 있음

![one](/Android/img/fiftyseven.png)

- Enter는 device가 geofence에 들어가는 상태를 나타냄, Dwell은 device가 geofence에 들어가고 주어진 시간동안 해당 영역에 있는걸 의미함, Exit은 device가 geofence를 나간 상태를 의미함

- Geofencing은 목적지에 가까워질 때 remide하는 등의 목적으로도 쓸 수 있고, 혹은 아이들이 geofence를 떠났는지 부모님들에게 알려주거나 출석 시스템으로도 쓸 수 있음

- 아니면 treasure hunt app처럼 유저가 geofence로 되어 있는 특정 숨겨진 장소를 가면 알려주는 앱으로도 쓸 수 있음(포켓몬 GO)

### Familiarizing yourself with the code
- `HuntMainActivity`는 권한을 다루는 함수와 geofence를 추가하고 삭제하는 함수의 빈 원형이 있음

- `GeofenceViewModel`은 `HuntMainActivity`와 관련된 `ViewModel`이 있음, `GeofenceIndex` LiveData를 통해서 화면에 힌트가 보여져야 하는지를 결정함

- `NotificationUtils`은 geofence에 진입하면, notification이 뜨게 됨, notification의 style을 관장함

- `activity_main.xml`은 Android image를 보여주지만 추후 다음 장소에 대한 힌트를 유저에게 제공해줄 것임

- `GeofenceBroadcastReceiver`는 `BroadcastReceiver`에 대한 `onReceive` 메소드를 포함함, 추후 업데이트 할 것임

### Requesting permissions
- 먼저 유저에 대해서 location 권한을 받아야함, 이 과정은 권한을 받는 모든 상황에 똑같이 적용될 것임

   - 1.Android manifest에 권한을 추가함

   - 2.권한을 확인하는 메소드를 만듬

   - 3.메소드를 호출함으로써 그러한 권한을 요청함

   - 4.권한 요청에 대해 유저에게 물어본 결과를 다룸

- 먼저 앞서 말한대로 manifest의 아래 권한을 추가함

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
```

- 그런 다음 API 29 버전 이상에서는 추가로 background location 권한을 물어야 하기 때문에 아래와 같이 `HuntMainActivity`의 값을 추가함

```kotlin
private val runningQOrLater = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q
```

- 권한을 체크하는 메소드를 코드를 아래와 같이 추가함

- 여기서 `ACCESS_FINE_LOCATION`의 권한이 승인되었는지 먼저 확인함, 그리고 만약 API 29 이상 버전이라면 `ACCESS_BACKGROUND_LOCATION`의 권한이 승인되었는지 확인함, 그리고 만약 버전이 29 밑이라면 해당 권한을 확인할 필요가 없기 때문에 `true`를 리턴하게함

```kotlin
@TargetApi(29)
private fun foregroundAndBackgroundLocationPermissionApproved(): Boolean {
   val foregroundLocationApproved = (
           PackageManager.PERMISSION_GRANTED ==
           ActivityCompat.checkSelfPermission(this,
               Manifest.permission.ACCESS_FINE_LOCATION))
   val backgroundPermissionApproved =
       if (runningQOrLater) {
           PackageManager.PERMISSION_GRANTED ==
           ActivityCompat.checkSelfPermission(
               this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
           )
       } else {
           true
       }
   return foregroundLocationApproved && backgroundPermissionApproved
}
```

- 그리고 location permission이 승인되었는지 유저에게 물어보는 메소드를 추가함

- 여기서 만약 `foregroundAndBackgroundLocationPermissionApproved`가 승인되었다면 다시 권한을 물어볼 필요가 없기 때문에 `return`을 함

- 그리고 `permissionsArray`에서 요청한 권한을 포함하게함, 처음에는 `ACCESS_FINE_LOCATION`임

- 그 이후 `resultCode`가 필요함, 만약 API 29버전 이상이라면 하나 혹은 그 이상의 권한을 했는지 확인하는지 결정하는 것임(유저가 권한 요청 화면에서의 결과를 리턴할 때)

- 그 다음 `when`구절에서 버전을 확인하고 `resultCode`에 만약 API 29 이상이면 `REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE`를 아니면 `REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE`를 할당함

- 마지막으로 현재 Activity에 permissions array와 result code에 대한 request permissions을 넘김

```kotlin
@TargetApi(29 )
private fun requestForegroundAndBackgroundLocationPermissions() {
   if (foregroundAndBackgroundLocationPermissionApproved())
       return
   var permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
   val resultCode = when {
       runningQOrLater -> {
           permissionsArray += Manifest.permission.ACCESS_BACKGROUND_LOCATION
           REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
       }
       else -> REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
   }
   Log.d(TAG, "Request foreground only location permission")
   ActivityCompat.requestPermissions(
       this@HuntMainActivity,
       permissionsArray,
       resultCode
   )
}
```

- permissions request에 대해 유저의 응답이 있다면, `onRequestPermissionsResult`에서 응답을 다뤄야함

- permissions은 여러 방식으로 거절당할 수 있음

   - 1.`grantResults` array가 비어있다면 interaction이 개입하고 permission request가 취소됨

   - 2.`LOCATION_PERMISSION_INDEX`에서의 `grantResults` array의 값이 `PERMISSION_DENIED`를 가지고 있다면, 유저가 권한을 거절 한 것임

   - 3.그리고 만약 `REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE`와 request code와 같은데 `BACKGROUND_LOCATION_PERMISSION_INDEX`가 거절되었다면, API 29 이상 버전에서 background permissions이 거절된 것임

- 해당 앱은 권한 처리가 많지 않기 때문에 이 부분에 대해서 Snackbar 형태로 권한이 필요함을 알려줌

- 만약 권한이 정상적으로 처리됐다면 `checkDeviceLocationSettingsAndStartGeofence`를 호출함

```kotlin
override fun onRequestPermissionsResult(
   requestCode: Int,
   permissions: Array<String>,
   grantResults: IntArray
) {
   Log.d(TAG, "onRequestPermissionResult")

   if (
       grantResults.isEmpty() ||
       grantResults[LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED ||
       (requestCode == REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE &&
               grantResults[BACKGROUND_LOCATION_PERMISSION_INDEX] ==
               PackageManager.PERMISSION_DENIED))
   {
       Snackbar.make(
           binding.activityMapsMain,
           R.string.permission_denied_explanation, 
           Snackbar.LENGTH_INDEFINITE
       )
           .setAction(R.string.settings) {
               startActivity(Intent().apply {
                   action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                   data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                   flags = Intent.FLAG_ACTIVITY_NEW_TASK
               })
           }.show()
   } else {
       checkDeviceLocationSettingsAndStartGeofence()
   }
}
```

### Checking device location
- 하지만 유저가 device location을 꺼놨다면 권한은 의미가 없어짐

- 그래서 유저가 location을 켜놨는지 확인하는 것과 만약 켜져 있지 않다면 location request를 켜게끔 activity로 알려주게함

- 먼저 `LocationRequest`를 만들어서 사용함

- 그리고 `locationSettingsResponseTask`를 통해서 location settings을 확인함

- 만약 location setting이 충족되어 있지 않다면 `onFailureListener`를 통해서 예외 처리를 추가함, 만약 해당하는 예외가 아니면 snackbar로 안내를 해 줌

- 그리고 해당하는 권한 처리가 정상적으로 완료됐는지 `locationSettingsResponseTask`로 확인함

```kotlin
private fun checkDeviceLocationSettingsAndStartGeofence(resolve:Boolean = true) {
   val locationRequest = LocationRequest.create().apply {
       priority = LocationRequest.PRIORITY_LOW_POWER
   }
   val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
   val settingsClient = LocationServices.getSettingsClient(this)
   val locationSettingsResponseTask =
       settingsClient.checkLocationSettings(builder.build())
   locationSettingsResponseTask.addOnFailureListener { exception ->
       if (exception is ResolvableApiException && resolve){
           try {
               exception.startResolutionForResult(this@HuntMainActivity,
                   REQUEST_TURN_DEVICE_LOCATION_ON)
           } catch (sendEx: IntentSender.SendIntentException) {
               Log.d(TAG, "Error getting location settings resolution: " + sendEx.message)
           }
       } else {
           Snackbar.make(
               binding.activityMapsMain,
               R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
           ).setAction(android.R.string.ok) {
               checkDeviceLocationSettingsAndStartGeofence()
           }.show()
       }
   }
   locationSettingsResponseTask.addOnCompleteListener {
       if ( it.isSuccessful ) {
           addGeofenceForClue()
       }
   }
}
```

- 마지막으로 `onActivityResult`에서 유저가 device location permission을 허용했는지 거절했는지 확인을 함, 허용했다면 상관없지만, 그렇지 않다면 권한을 요청함

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
   super.onActivityResult(requestCode, resultCode, data)
   if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {
       checkDeviceLocationSettingsAndStartGeofence(false)
   }
}
```

### Adding geofences
- 앞서 권한 체크가 끝났다면 이제 geofence를 추가할 수 있음

- 이 geofence transition의 경우 `PendingIntent`를 활용함, 그리고 이를 다루기 위해 `BroadcastReceiver`를 응용함

- 먼저 `HuntMainActivity`에서 `PendingIntent`를 추가해서 다룸, 해당 Intent는 `GeofenceTransitionsBroadcastReceiver`와 연결함

```kotlin
private val geofencePendingIntent: PendingIntent by lazy {
   val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
   intent.action = ACTION_GEOFENCE_EVENT
   PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
}
```
- 그리고 geofence를 사용하기 위해서 해당 API와 상호작용을 해야하는데 먼저 `onCreate`에서 인스턴스를 먼저 만듬

```kotlin
geofencingClient = LocationServices.getGeofencingClient(this)
```

- 그리고 geofence를 추가하는 함수를 추가함

- 먼저 geofence가 활성화된 게 있는지 확인함, 이미 있다면 한 번에 하나의 treasure만을 찾기 때문에 geofence를 추가할 필요가 없으므로 `return`처리함

- 그리고 `viewModel`에서 `currentGeofenceIndex`를 찾음, 현재 존재하는 것을 제거하고 `viewModel`에서 `geofenceActivated`를 호출하고 리턴함

- 그리고 geofence index가 있고 값이 유효하다면 geofence 주변의 data를 얻음(이는 id와 위도 경도가 포함됨)

- 그리고 현재 위치(`currentGeofenceData`)에 대해서 geofence builder를 통해서 만들고, 만료기한을 설정하고 transition을 정하고 만듬

- 그리고 geofence request를 만듬, 그리고 만약 하나라도 `PendingIntent`와 관련된 geofence가 있으면 제거함

- 제거가 완료되면 성공 실패와 상관없이 새로운 geofence를 추가함, 만약 제거가 실패되도 geofence 추가의 영향을 주진 않음, 그리고 geofence가 추가되면 이를 toast로 알려줌, 실패해도 알려줌

```kotlin
private fun addGeofenceForClue() {
   if (viewModel.geofenceIsActive()) return
   val currentGeofenceIndex = viewModel.nextGeofenceIndex()
   if(currentGeofenceIndex >= GeofencingConstants.NUM_LANDMARKS) {
       removeGeofences()
       viewModel.geofenceActivated()
       return
   }
   val currentGeofenceData = GeofencingConstants.LANDMARK_DATA[currentGeofenceIndex]

   val geofence = Geofence.Builder()
       .setRequestId(currentGeofenceData.id)
       .setCircularRegion(currentGeofenceData.latLong.latitude,
           currentGeofenceData.latLong.longitude,
           GeofencingConstants.GEOFENCE_RADIUS_IN_METERS
       )
       .setExpirationDuration(GeofencingConstants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
       .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
       .build()

   val geofencingRequest = GeofencingRequest.Builder()
       .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
       .addGeofence(geofence)
       .build()

   geofencingClient.removeGeofences(geofencePendingIntent)?.run {
       addOnCompleteListener {
           geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)?.run {
               addOnSuccessListener {
                   Toast.makeText(this@HuntMainActivity, R.string.geofences_added,
                       Toast.LENGTH_SHORT)
                       .show()
                   Log.e("Add Geofence", geofence.requestId)
                   viewModel.geofenceActivated()
               }
               addOnFailureListener {
                   Toast.makeText(this@HuntMainActivity, R.string.geofences_not_added,
                       Toast.LENGTH_SHORT).show()
                   if ((it.message != null)) {
                       Log.w(TAG, it.message)
                   }
               }
           }
       }
   }
}
```

### Updating the Broadcast Receiver
- 앞서 geofence는 추가했지만 이 때 이를 확실하게 알리기 위해서 broadcast receiver를 통해 geofence transition event를 처리해야함

- 그러기 위해 `GeofenceBroadcastReceiver`에 `onReceive`안에 아래와 같이 채워줌

- broadcast receiver는 많은 타입의 action을 받는데 현재로는 `ACTION_GEOFENCE_EVENT` 타입을 intent.action으로 확인해야함

- 그리고 `GeofencingEvent`를 받은 intent를 넘겨줘서 초기화함, 에러가 있다면 에러 코드와 로그를 띄우고 멈춤

- 그리고 앞서 설명한 geofence 상태를 기억해서 `ENTER` 상태인지 확인함, 만약 그 상태인데 `triggeringGeofences`가 빈값이 아니라면 `fenceID`를 설정함, 만약 비어있지 않다면 오직 하나만 상호작용해야하기 때문에 하나의 값으로 하는 것, 비어있다면 log를 띄우고 종료함

- 그 다음 `GeofenceUtil`의 constant list에 있는지 계속 확인하고 없다면 종료함, 그리고 이 과정을 다 거쳤다면 유저가 유효한 geofence에 들어간 것이므로 notification으로 알려줌

```kotlin
override fun onReceive(context: Context, intent: Intent) {
   if (intent.action == ACTION_GEOFENCE_EVENT) {
       val geofencingEvent = GeofencingEvent.fromIntent(intent)

       if (geofencingEvent.hasError()) {
           val errorMessage = errorMessage(context, geofencingEvent.errorCode)
           Log.e(TAG, errorMessage)
           return
       }

       if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
           Log.v(TAG, context.getString(R.string.geofence_entered))
           val fenceId = when {
               geofencingEvent.triggeringGeofences.isNotEmpty() ->
                   geofencingEvent.triggeringGeofences[0].requestId
               else -> {
                   Log.e(TAG, "No Geofence Trigger Found! Abort mission!")
                   return
               }
           }
           val foundIndex = GeofencingConstants.LANDMARK_DATA.indexOfFirst {
               it.id == fenceId
           }
           if ( -1 == foundIndex ) {
               Log.e(TAG, "Unknown Geofence: Abort Mission")
               return
           }
           val notificationManager = ContextCompat.getSystemService(
               context,
               NotificationManager::class.java
           ) as NotificationManager

           notificationManager.sendGeofenceEnteredNotification(
               context, foundIndex
           )
       }
   }
}
```

### Mocking a location on the emulator
- 에뮬레이터로 추가 control로 location 설정을 직접해서 테스트 할 수 있음

### Removing geofences
- geofence가 더 이상 필요없다면, 이것을 지우고 모니터링을 멈추는 것이 최고의 방식임, 그래서 아래의 코드를 추가함

- 이는 초기에 권한이 있는지 확인한 뒤에 `geofencePendingIntent`에 `geofencingClient`에서 제거하는 함수를 호출함, 그리고 성공 & 실패 케이스에 대해서 성공은 토스트 메시지를 실패는 로그를 띄워줌

```kotlin
private fun removeGeofences() {
   if (!foregroundAndBackgroundLocationPermissionApproved()) {
       return
   }
   geofencingClient.removeGeofences(geofencePendingIntent)?.run {
       addOnSuccessListener {
           Log.d(TAG, getString(R.string.geofences_removed))
           Toast.makeText(applicationContext, R.string.geofences_removed, Toast.LENGTH_SHORT)
               .show()
       }
       addOnFailureListener {
           Log.d(TAG, getString(R.string.geofences_not_removed))
       }
   }
}
```

### Navigating to the winning location
- 이제 해당 위치에 가면 성공화면으로 뜨게됨
