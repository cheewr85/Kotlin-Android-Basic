### Add navigation
- `SleepQualityFragment`에서 `SleepQuality`를 체크하고 arugment로 `sleepNightKey`를 넘겨줌

- 여기서 `ViewModel`과 `LiveData`를 설정하여 클릭 이벤트 처리를 하고 `LiveData`를 observing함, 그리고 navigation에 맞춰서 진행하게 함

- `ViewModel`에서 `SleepQuality`에 대해서 `LiveData`를 만들어서 캡슐화를 하고 값에 따라 navigation 설정을 하게끔 처리함

```kotlin
// SleepQualityFragment로 갈 때 LiveData 변화를 체크하기 위한 LiveData 선언
        private val _navigateToSleepQuality = MutableLiveData<SleepNight>()
        // 캡슐화를 통해 LiveData를 설정함
        val navigateToSleepQuality: LiveData<SleepNight>
          get() = _navigateToSleepQuality

....

// Tracking을 멈추는 함수
        fun onStopTracking() {
                viewModelScope.launch {
                        val oldNight = tonight.value ?: return@launch
                        oldNight.endTimeMilli = System.currentTimeMillis()
                        update(oldNight)
                        // SleepQualityFragment로 navigate하고 처리하기 위해 변수를 설정함, 값이 있을 때 SleepQualityFragment로 navigate함
                        _navigateToSleepQuality.value = oldNight
                }
        }

....

// naviagtion으로 가기 때문에 variable을 null로 함
        fun doneNavigating() {
                _navigateToSleepQuality.value = null
        }
```

- 그리고 `SleepTrackerFragment`에서 위에서 정한 `LiveData`를 navigate를 할 때 observe 해야함

- 그래서 `onCreateView`안에서 observer를 추가함

```kotlin
// LiveData 값을 관찰하기 위한 observe
        sleepTrackerViewModel.navigateToSleepQuality.observe(this, Observer { night ->
            night?.let {
                // nav 설정을 통해 sleepquality로 넘어가게 설정함(값이 생기게 되면)
                this.findNavController().navigate(SleepTrackerFragmentDirections
                    .actionSleepTrackerFragmentToSleepQualityFragment(night.nightId))
                sleepTrackerViewModel.doneNavigating()

            }
        })
```

### Record the sleep quality
- `SleepQuality`에서 저장을 하고 `SleepTrackerFramgnet`로 돌아가게 해야함, 이때 유저에게 보여지는 값에 대해서 자동으로 업데이트 하게 해줘야함

- 이때 동일하게 `ViewModel`과 `ViewModelFactory`를 만들고 `SleepQualityFragment`에 적용해야함

- 이 과정은 이전에 `SleepTracker`에서 한 것과 동일함

```kotlin
// Factory로부터 보낼 database와 navigation으로부터 sleepnightkey를 전달해야함
class SleepQualityViewModel(
    private val sleepNightKey: Long = 0L,
    val database: SleepDatabaseDao) : ViewModel() {

    // SleepTrackerFragment로 돌아가기 위해서 LiveData 패턴을 동일하게 적용함
    private val _navigateToSleepTracker = MutableLiveData<Boolean?>()

    val navigateToSleepTracker: LiveData<Boolean?>
      get() = _navigateToSleepTracker

    fun doneNavigating() {
        _navigateToSleepTracker.value = null
    }

    // 클릭 이벤트 리스너 동일하게 이전과 같이 Coroutine을 활용해서 처리함
    fun onSetSleepQuality(quality: Int) {
        viewModelScope.launch {
            val tonight = database.get(sleepNightKey) ?: return@launch
            tonight.sleepQuality = quality
            database.update(tonight)


            // Setting this state variable to true will alert the observer and trigger navigation.
            _navigateToSleepTracker.value = true
        }
    }


}
```

```kotlin
class SleepQualityViewModelFactory(
    private val sleepNightKey: Long,
    private val dataSource: SleepDatabaseDao) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SleepQualityViewModel::class.java)) {
            return SleepQualityViewModel(sleepNightKey, dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }   
        
        
        
}
```

- 그리고 Fragment의 동일하게 적용시킴

```kotlin
// navigation과 함께 제공된 argument를 가져와야 함, 이 argument는 SleepQualityFragmentArgs에 있었음(bundle로 뽑아야함)
        val arguments = SleepQualityFragmentArgs.fromBundle(requireArguments())
        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao

        // ViewModelFactory를 만들고 ViewModel을 만듬
        val viewModelFactory = SleepQualityViewModelFactory(arguments.sleepNightKey, dataSource)
        val sleepQualityViewModel = ViewModelProvider(this, viewModelFactory).get(SleepQualityViewModel::class.java)

        // binding 객체의 추가함
        binding.sleepQualityViewModel = sleepQualityViewModel

        // observer를 추가함
        sleepQualityViewModel.navigateToSleepTracker.observe(this, Observer {
            if (it == true) { // observed state is true.
                this.findNavController().navigate(
                    SleepQualityFragmentDirections.actionSleepQualityFragmentToSleepTrackerFragment())
                sleepQualityViewModel.doneNavigating()
            }
        })
```

- 그 다음, 각각의 이미지에 대해서 onClick을 아래와 같이 이미지 rating에 맞게 적용해줘야함

```xml
android:onClick="@{() -> sleepQualityViewModel.onSetSleepQuality(5)}"
```

### Control button visibility and add a snackbar
- 여기서 유저가 버튼에 대해서 무한정 누르게 할 순 없음, 그래서 버튼에 대해서 visibility를 정해 제한을 둘 것임

- 여기서 `android:enabled` 속성을 주어서 버튼의 사용여부를 줄 것임, 세 버튼 모두 각각에 대해서

```xml
android:enabled="@{sleepTrackerViewModel.startButtonVisible}"
```

- 그리고 `ViewModel`에서 관련된 변수를 만들고 할당할 것임

- start 버튼은 `tonight`이 `null`일 때 활성화 되게하고, stop 버튼은 `tonight`이 `null`이 아닐때 활성화하게 하고 clear 버튼은 `nights`를 담고 있을 때만 enable 하게함, 이를 코드로 설정함 

```kotlin
val startButtonVisible = Transformations.map(tonight) {
   it == null
}
val stopButtonVisible = Transformations.map(tonight) {
   it != null
}
val clearButtonVisible = Transformations.map(nights) {
   it?.isNotEmpty()
}
```

- 여기서 한 가지 알아야 할 것은 이 `enable` 속성은 `visibility` 속성과는 결이 다름

- `enable`은 오직 `View`의 사용 가능성 즉, 활성화해서 버튼이면 누르게 할 수 있는지 없는지를 판단하는 것이지 `View`는 보임

- 여기서 `enable`의 의미는 하위 클래스에 따라서 다양하게 적용됨

- 추가로 `Snackbar`를 통해서 상황에 대해서 알려줄 수 있음

- 이 부분 역시 `LiveData`로 캡슐화해서 만들 수 있음

```kotlin
// Snackbar 관련 LiveData
        private var _showSnackbarEvent = MutableLiveData<Boolean>()
        // 캡슐화 진행
        val showSnackBarEvent: LiveData<Boolean>
          get() = _showSnackbarEvent

....

// snackbar에 대한 완료 처리
        fun doneShowingSnackbar() {
                _showSnackbarEvent.value = false
        }
```

- 그리고 Fragment에서 observer를 추가해서 처리함

```kotlin
sleepTrackerViewModel.showSnackBarEvent.observe(this, Observer {
            if (it == true) {
                // observed state is true Snackbar 생성
                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                getString(R.string.cleared_message),
                Snackbar.LENGTH_SHORT).show()
            }
            sleepTrackerViewModel.doneShowingSnackbar()
        })
```