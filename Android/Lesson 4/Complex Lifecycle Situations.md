### Avoid lifecycle mistakes
- 앞서 봤던 Lifecycle을 응용해서 타이머를 각 lifecycle callback에서 호출할 수 있음

- 이 타이머를 키고 끄는 것도 그만큼 자원이 소모되는 것임

- 만약 `onStart`에서 타이머를 시작하고 `onStop`에서 멈춘다면 부분적으로 보이지 않는 상황에서도 계속 타이머는 진행이 됨 

- 그리고 이를 `onCreate`에서 타이머를 시작하면 한 번 시작되고 만약 멈춘 상황이 있다면 앱을 끄지 않는 이상 다시 타이머가 시작하진 않게됨

### Use the Android lifecycle library
- 그래서 단순하게 `onStart`에서 타이머를 시작하고 `onStop`에서 꺼버리는 식으로 진행할 수 있음

- 하지만 복잡한 상황에서는 무조건 다 `onStart`나 `onCreate`에서 시작하고 `onStop`이나 `onDestroy`에서 끝낼 수 없음, 이런 부분을 제대로 설정해주지 못하면 계속 실행이되거나 예상치 못한 버그가 발생하게됨

- 그래서 이렇게 매번 시작하고 종료하는 이런 lifecycle에 따른 설정을 간소화 시키기 위해서 Android Jetpack에선 lifecycle libray를 제공함

- 이 라이브러리는 서로 다른 lifecycle 상태에서 다양한 부분을 체크하고 보는데 있어서 유용함

- 원래는 Activity나 Fragment나 lifecycle callback이 호출되면 해당 컴포넌트에 알려주지만 lifecycle library는 컴포넌트 그 자체가 lifecycle의 변함에 따라 이런 변경사항에 따른 작업을 처리함

- lifecycle library는 3가지 부분으로 이루어짐

   - Lifecycle Owners, lifecycle을 가지고 있는 컴포넌트, Activity, Fragment가 Lifecycle owner임, Lifecycle owner는 `LifecycleOwner` 인터페이스를 implement할 수 있음

   - `Lifecycle` 클래스는 실제 lifecycle owner의 상태를 보유하고 lifecycle의 변화가 있을 때 이벤트를 발생시킴

   - Lifecycle observers는 lifecycle 상태를 관찰하고 lifecycle의 변화가 생길 때 task를 수행함, Lifecycle observer는 `LifecycleObserver` 인터페이스를 implement할 수 있음

- lifecycle library의 목표는 lifecycle observation이 중요한 컨셉임

- Observation은 class가(`DessertTimer` 같은 예시에서 쓴 타이머 클래스) activity나 fragment의 lifecycle에 대해 알 수 있고, 해당 lifecycle의 변화에 따라서 이런 class를 시작하고 중지할 수도 있음

- lifecycle observer가 있다면 activity나 fragment method로부터(`onStart`, `onStop`등) 객체를 시작하고 멈추는 책임을 제거할 수 있음

- 아래와 같이 timer가 observing 할 수 있게 lifecytcle 객체를 생성자가 가지고 `LifecycleObserver` 인터페이스를 구현함, 그리고 `init` block을 통해서 해당 클래스에 대해서 owner에게 lifecycle 객체를 연결해서 전달함

```kotlin
class DessertTimer(lifecycle: Lifecycle) : LifecycleObserver {
```
```kotlin
 init {
   lifecycle.addObserver(this)
}
```

- 그리고 타이머 시작과 정지에 대해서 어노테이션을 활용해서 lifecycle의 변화를 확인하게 되면 해당 메소드를 실행시키게끔 처리함 즉, onStart 상황, onStop 상황이 나타나게 된다면

```kotlin
@OnLifecycleEvent(Lifecycle.Event.ON_START)
fun startTimer() {
```
```kotlin
@OnLifecycleEvent(Lifecycle.Event.ON_STOP)
fun stopTimer()
```

- 그리고 이미 Activity는 `AppCompatActivity`의 하위 클래스이므로 이는 `FragmentActivity`의 하위 클래스이므로 `FragmentActivity`는 `LifecycleOwner`를 구현하기 때문에 lifecycle 객체를 가지고 있음

- 그래서 추가적으로 할 것 없이 단지 `DessertTimer` 생성자에 `this.lifecycle`로 Lifecycle 객체를 처리하면 됨

```kotlin
dessertTimer = DessertTimer(this.lifecycle)
```

- 그러면 별도로 Activity의 lifecycle callback에서 처리할 것 없이 Lifecycle library를 통해서 lifecycle의 변화가 생긴다면 인지를 하고 DessertTimer에서 선언한 어노테이션대로 해당 lifecycle일 때 메소드를 알아서 실행시켜서 처리함

### Simulate app shutdown and use onSaveInstanceState()
- 앱이 background로 가는 상황에서는 실제로 소멸된 것이 아님 잠시 멈춰있고 유저가 돌아오는 것을 기다리는 상황임

- 여기서 고민은 Activity가 foreground에서 스무스하게 유지되는 것임

- Android는 foreground app들이 문제없이 구동되게 하기 위해서 background app을 제한을 함 즉, background에서 할 수 있는 processing의 양을 제한함

- 그래서 이런 상황에서 유저 입장에선 닫은 것이 아닌데도 background에서 조용히 앱의 process가 소멸될 수 있음

- 일단 디버그를 해서 자세하게 process를 보면, 앱이 완벽하게 닫히는 것이 아님 그래서 이런 상황에선 OS가 앱을 다시 재시작할 때 Android는 이전의 앱의 상태를 reset 하는 것이 최상의 방식임

- 그래서 Android는 view의 상태를 가져와 다른 activity로부터 탐색을 할 때 bundle에 그 값을 저장해둠, EditText의 text나 activity를 back stack에 두는 등 몇 가지 데이터는 자동으로 저장되더라도 Android OS가 모든 데이터에 대해서는 알지 못함

- Android가 특정 변수에 대한 값에 대해서 아무리 그 값이 중요하더라도 알지 못하기 때문에 이를 bundle data에 직접 추가를 해줘야할 필요가 있음

- 이 때 `onSaveInstanceState()` 메소드는 Android OS가 app을 소멸시킬 때 저장을 하려고 사용할 데이터에 대한 callback임

- lifecycle에서 본다면 아래와 같이 activity가 중지되고 background에 갈 때 항상 호출이 됨

![one](/Android/img/nineteen.png)

- 이 `onSaveInstanceState`를 활용하여 Activity가 foreground에 존재할 때 bundle로 값을 저장할 수 있음, 이런 데이터를 매번 저장하는 것은 필요하다면 복원이 가능한 bundle속에서 데이터를 갱신하는 것을 확실하게 해 줌

- 이 부분을 사용하기 위해서 `onSaveInstanceState()`를 오버라이딩 해줘야함(아래 예시는 `outState` 매개변수만 있는 것)

```kotlin
override fun onSaveInstanceState(outState: Bundle) {
   super.onSaveInstanceState(outState)

   Timber.i("onSaveInstanceState Called")
}
```

- 우선 이 State를 사용해서 bundle로 저장하기 위해서 상수 값을 아래와 같이 class 선언 이전에 정의함

```kotlin
const val KEY_REVENUE = "revenue_key"
const val KEY_DESSERT_SOLD = "dessert_sold_key"
const val KEY_TIMER_SECONDS = "timer_seconds_key"
```

- outState의 타입은 bundle임을 알 수 있는데, 이 bundle은 key-value 쌍의 collection으로 key는 항상 string이어야 함

- value에는 int, boolean 같은 primitive 타입도 넣을 수 있음 이 bundle의 사이즈는 그리고 제한되어 있어서 bundle이 작으면 좋기 때문에 그런 경향도 있음

- 그리고 그 값을 아래와 같이 key-value 쌍으로 `putInt`이런식으로 넣어줄 수 있음

```kotlin
outState.putInt(KEY_REVENUE, revenue)
outState.putInt(KEY_DESSERT_SOLD, dessertsSold)
outState.putInt(KEY_TIMER_SECONDS, dessertTimer.secondsCount)
```

- 그리고 `onCreate`에서 `Bundle`을 매번 불러올 수 있음 이 bundle 값은 activity를 재시작하거나 소멸이 될 때 `onCreate`에 저장이 됨

- 만약 activity를 처음 만든 것이라면 bundle은 `null`이지만 만약 `null`이 아니라는 것은 activity가 이전 포인트를 기점으로 재생성된 것임

- 추가로 알 것은 만약 이렇게 복원할 값이 `onStart`가 호출되기 전에 있다면 `onRestoreInstanceState`를 호출하면 됨 하지만 대부분 activity 상태는 `onCreate`에서 복원하기 때문에 `onSaveInstanceState`를 쓰는 것임

- 복원을 위해서 아래와 같이 `onCreate`에 조건문 처리를 함, 이 값이 null이 아니면 이는 bundle에 값이 있는 것이고 한 번 종료됐다가 다시 재생성된 것임을 의미함

```kotlin
if (savedInstanceState != null) {
   revenue = savedInstanceState.getInt(KEY_REVENUE, 0)
}
```

- 그 다음 bundle에서 값을 불러올 때 위에서 정의한 상수 key를 기준으로 똑같이 해당 key 값에 맞춰서 아래와 같이 불러오면 됨

```kotlin
if (savedInstanceState != null) {
   revenue = savedInstanceState.getInt(KEY_REVENUE, 0)
   dessertsSold = savedInstanceState.getInt(KEY_DESSERT_SOLD, 0)
   dessertTimer.secondsCount =
       savedInstanceState.getInt(KEY_TIMER_SECONDS, 0)
}
```

- 그리고 앱을 실행시키고 앞서 배운대로 실습을 해보면 그 값이 InstanceState에 성공적으로 저장되어 있음을 알 수 있음(background에), 그리고 복원도 정상적으로 진행이 됨

### Explore configuration changes
- Configuration change는 device의 상태가 갑자기 바귀게 되면 시스템이 activity를 완전히 닫고 재빌드를 함

- 이에 해당하는 경우는 유저가 언어를 바꾸는 경우 혹은 유저가 물리적 키보드를 추가한다던지, 혹은 폰을 회전하는 경우 등 앱의 상태가 바뀌게 된다면 이에 따라서 lifecycle 역시도 변함

- 테스트로 애뮬레이터나 device를 돌리게 되면 시스템은 activity를 소멸시키기 위해 해당 lifecycle callback을 호출함, 그대 activity가 다시 생성되고 activity를 다시 시작하기 위해 lifecycle callback을 호출함

- configuartion change가 나타날 때, Android는 앱의 상태를 복원하고 저장하는데 같은 instance state bundle을 사용함 즉, 만약 이런 configuration change가 발생하면 `onSaveInstanceState()`를 통해서 data bundle을 처리할 수 있음, 그리고 state data를 잃지않고 이 data를 `onCreate`에서 복원을 할 수 있음