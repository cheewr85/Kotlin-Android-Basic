### Add a timer
- 예시앱에서 시간이 초과되면 게임을 종료하게 할 것인데 이때 Android에서 제공하는 `CountDownTimer`를 활용할 것임

- `GameViewModel`에서 timer 로직을 추가해서 시간이 지나가는 표현을 나타낼 것임

- 시간의 범위에 대한 상수와 `LiveData`를 먼저 정의함

```kotlin
companion object {

   // Time when the game is over
   private const val DONE = 0L

   // Countdown time interval
   private const val ONE_SECOND = 1000L

   // Total time for the game
   private const val COUNTDOWN_TIME = 60000L

}

// Countdown time
private val _currentTime = MutableLiveData<Long>()
val currentTime: LiveData<Long>
   get() = _currentTime
```

- 그리고 `timer` 사용을 위해 `CountDownTimer`를 쓰고 `init` 블럭에서 `CountDownTimer`에 대한 정의를 추가함

```kotlin
// Creates a timer which triggers the end of the game when it finishes
timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {

   override fun onTick(millisUntilFinished: Long) {
       
   }

   override fun onFinish() {
       
   }
}

timer.start()
```

- 그리고 시간이 진행될 때마다 그리고 끝날때마다 호출되는 메소드를 각각 채워줌, `_currentTime`의 변화시켜줌

```kotlin
override fun onTick(millisUntilFinished: Long)
{
   _currentTime.value = millisUntilFinished/ONE_SECOND
}

override fun onFinish() {
   _currentTime.value = DONE
   onGameFinish()
}
```

- `nextWord`도 `wordList`가 비면 게임을 끝내지말고 List를 리셋시킴, 어차피 시간이 끝나면 게임이 종료되므로

- 그리고 `ViewModel`이 사라질 때 `onCleared` 메소드에서 `timer`를 종료시킴

### Add transformation for the LiveData
- `Transformations.map()` 메소드는 `LiveData`에서 데이터 조작을 하여서 `LiveData`객체로 반환을 함, 여기서 `LiveData`객체를 반환하지 않는 이상 계산을 하진 않음

- 이 메소드는 `LiveData`소스를 가져서 함수를 매개변수로 함, 이 함수는 `LiveData`소스를 조작할 수 있음

- 이 람다함수는 main thread에서 실행되기 때문에 너무 긴 작업을 포함하면 안됨

- 이를 활용하여 `LiveData`객체의 시간표현은 새로운 String `LiveData`객체에서 `MM:SS`형태로 변환할 것임

- 이전 형태 또한 나타낼 것임

- `GameViewModel`에서 `currentTime`에 대해서 새로운 `LiveData`객체를 만들어서 String으로 변환하는 작업을 `Transformation.map`을 통해서 진행함

- 이 때 아래와 같이 람다식을 활용 유틸리티 메소드를 써서 형태를 바꿀 수 있음

```kotlin
// The String version of the current time
val currentTimeString = Transformations.map(currentTime) { time ->
   DateUtils.formatElapsedTime(time)
}
```

- 그리고 `game_fragment.xml`에서 `text`속성을 `gameViewModel`에 있는것으로 data binding을 활용함

```xml
<TextView
   android:id="@+id/timer_text"
   ...
   android:text="@{gameViewModel.currentTimeString}"
   ... />
```

