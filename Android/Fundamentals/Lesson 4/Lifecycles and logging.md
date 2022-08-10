### Explore the lifecycle methods and add basic logging
- 모든 Activity와 Fragment에는 Lifecycle이 존재함

- Activity Lifecycle의 경우, Activity가 처음에 생성될 때부터 파괴될 때까지 그리고 시스템에 의해 메모리에서 다시 불러올 때까지의 Activity는 모든 상황을 겪을 수 있고 이에 따른 서로 다른 상태로 형성되어 있음

- 유저가 앱을 시작하고 Activity 사이를 탐색하고, 앱의 내외부를 탐색하고, 앱을 떠나는 등 이 모든 상황에서 Activity는 변함

- 아래와 같이 Activity Lifecycle의 상태를 나타낼 수 있음

![one](/Android/img/thirteen.png)

- Activity Lifecycle state가 변할 때, behavior를 바꾸거나 코드를 실행하려고 함

- 그래서 Activity class 자체적으로 Activity의 하위 클래스인 `AppCompatActivity`처럼 모두 다 Lifecycle callback method를 구현함

- Android는 이 callback을 Activity가 특정 상태에서 다른 상태로 옮겨갈 때 불러오고 그리고 해당 lifecycle state가 변경될 때의 상황에서 나의 Activity에 override해서 몇가지 작업을 수행할수도 있음

- 아래와 같이 callback에 따라 Lifecycle state가 바뀜

![one](/Android/img/fourteen.png)

- Fragment도 역시 Lifecycle이 있음, 이는 Activity의 Lifecycle과 상당히 유사함, 아래와 같이 구성되어 있음

![one](/Android/img/fifteen.png)

- 이러한 callback이 언제 불러와지고 각각 callback method가 무엇을 하는지 아는 것이 매우 중요함

- 먼저 `onCreate`의 경우 모든 Activity가 반드시 구현할 메소드 중에 하나임, 한 번 초기화가 되면 실행이 되는 메소드임

- 이 메소드는 layout을 그리고, click listener를 정의하고 data binding을 설정하는 등 처리를 함

- 이 `onCreate`는 Activity가 초기화 된 이후 실행이 되고(새로운 Activity 객체가 메모리안에서 생성될 때), `onCreate`가 실행된 이후 Activity가 생성이 됨

![one](/Android/img/sixteen.png)

- Log를 찍어서 생성되는 시점에서 메소드가 실행되는지 확인할 수 있음

- `onStart`의 경우, `onCreate`이후에 실행이 됨, `onStart`가 실행된 이후는 Activity가 스크린에 보이기 시작하게 됨

- `onCreate`는 Activity를 초기화할 때 한 번만 불리는 반면 `onStart`의 경우 Activity의 lifecycle안에서 여러번 불려질 수 있음

- `onStart`는 `onStop`과 상응된다고 볼 수 있음, 만약 유저가 앱을 시작하고 핸드폰의 홈화면으로 돌아가면 Activity는 멈추고 더 이상 화면에 보이지 않음

![one](/Android/img/seventeen.png)

### Use Timber for logging
- Log를 찍는것 외에 loggin library로 `Timber`가 존재함, 기존의 `Log` 클래스에서 확장된 이점을 가지고 활용할 수 있음

- 먼저 `Application` 클래스를 만들 것임, 이 `Application` 클래스는 앱을 위해서 전역적인 앱 상태를 포함하는 클래스임

- 또한 OS가 앱과 상호작용하기 위한 목적도 있음

- 기본적으로 특정하지 않는 이상 안드로이드가 사용하는 `Application` 기본 클래스가 존재함, 이 객체는 앱을 만들 때 무조건 나타나고 별도로 만들 필욘 없음

- `Timber`는 `Application` 클래스를 사용함, 모든 앱은 logging library를 사용하고, library는 모든게 세팅되기 전에 한 번 초기화할 필요가 있기 때문에 `Application` 클래스를 활용함

- `Application`의 하위 클래스를 만들어 기본값을 오버라이딩하여 커스텀을 할 수 있음

- `Application` 클래스 사용에 있어서는 신중해야함, 남용하면 안됨

- `Application` 클래스를 만든 뒤, Android manifest에서 해당 클래스를 지정해야함

```kotlin
package com.example.android.dessertclicker

import android.app.Application
import timber.log.Timber

class ClikcerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Timber 초기화
        Timber.plant(Timber.DebugTree())
    }
}
```
```xml
<application
   android:name=".ClickerApplication"
...
```

- 그리고 Log처럼 Timber에 대해 log를 아래와 같이 찍을 수 있음, Timber는 log tag를 추가할 필요 없이 자동으로 클래스 명으로 log tag가 찍힘

- 나머지 lifecycle method도 추가하여 확인할 수 있음

- Logcat을 통해서 보면 앱이 생성되고 `onCreate`가 실행된 뒤 `onStart`를 통해서 스크린에 화면이 보이고 `onResume`을 통해 해당 Activity에 포커스를 맞춰 유저와 상호작용할 준비가 된 상태임을 나타냄

- 즉, `onCreate -> onStart -> onResume` 형태로 진행되는데, 여기서 `onResume`의 경우 resume 할 게 없더라도 시작을 위해 호출되는 함수임

### Explore lifecycle use cases
- 이제 lifecycle에 맞춰서 사용예시를 하나씩 알아볼 수 있음, 모든 method에 log를 설정했으므로

- 처음 Activity를 키면 `onCreate -> onStart -> onResume`으로 진행이 됨

- 그리고 Back Button을 누르게 되면 `onPause -> onStop -> onDestroy`로 진행되서 처리됨

- Back Button을 누른 것이 곧, 앱을 아예 닫아버리게 됨, `onDestroy`의 실행은 Activity가 완전히 끝나고 garbage-collected 되는 것을 의미함

- `onDestroy`실행 후, OS는 해당 자원을 삭제하고 memory 정리를 시작함

- 그리고 `finish`메소드를 호출하면 Activity가 완전히 종료될 것임(혹은 유저가 빠르게 종료 하려고 한다면)

- 그리고 안드로이드 시스템은 만약 해당 앱이 스크린에 오랫동안 보여지지 않는다면 Activity를 바로 닫을 것임, 안드로이드는 이를 배터리를 지키고 앱의 자원이 다른 앱에 사용되기 위해서 이렇게 처리함

- Activity가 destroy되고 난 뒤 앱에 돌아오면 Android는 다시 새로운 Activity를 시작하고 `onCreate -> onStart -> onResume`이 과정을 다시 반복함(그리고 예시 앱에서 가격과 숫자값은 초기화되어 있음, 이 부분은 손을 대면 고칠 수 있음)

- 중요한 것은 `onCreate`와 `onDestroy`는 단일 Activity 인스턴스 생명주기동안 한 번 호출되는 것임(처음 앱을 초기화 할 때 `onCreate`가 실행되고 `onDestroy`사용시 앱에서 해당 리소스를 정리함)

#### 만약 activity에 대해서 navigate하고 뒤로 돌아가는 식의 방식에서의 Activity 생명주기를 본다면

- Activity는 유저가 다른 Activity로 간다고 할 때 무조건 매번 닫히지 않음 

- Activity가 더 이상 화면에 보이지 않을 때, Activity를 background에 넣어둠, 그리고 유저가 다시 앱에 돌아옸을 때 같은 Activity를 재시작하고 다시 보이게 함, 이러한 Lifecycle을 앱의 visible lifecycle이라고 함

- 앱이 background에 있을 때, 배터리와 자원을 지키기 위해서 활발하게 실행되진 않음, `Activity` 생명주기를 사용하고, background로 넘어갈 때, pause하고 ongoing operation을 할 수 있음

- 그 때, 이 operations을 재시작하면 app이 foreground로 나오게 됨 

- 앱이 background로 갔을 때, foreground로 돌아오게 될 때 Activity lifecycle을 볼 수 있음

- `onCreate -> onStart -> onResume`으로 앱을 시작하고 여기서 홈버튼을 눌러서 나갔을 때 `onPause -> onStop`은 되지만 `onDestroy`는 불리지않음 이는 앱에 대해서 아예 닫아버리는 대신 background에 보낸것임을 알 수 있음

- `onPause`가 불리게 된다면 앱은 더이상 focus되어 있지않고 `onStop`이 불리고 나면 앱이 스크린에 더이상 보이지 않게됨

- 비록 Activity는 멈췄지만, `Activity` 객체는 여전히 메모리, background에 존재함, Activity가 아직 소멸되지 않은 것임

- 유저가 돌아올수도 있기 때문에, Android가 Activity 자원을 계속 가지고 있는 것임

- 그럼 이 상황에서 다시 앱으로 돌아온다면 `onRestart -> onStart -> onResume`이 실행이 됨

- 여기서 포인트는 `onCreate`가 불리지 않은것임, 왜냐하면 Activity 객체가 없어진 것이 아니기 때문에 `onCreate`를 할 필요가 없는 것이고 대신 `onRestart`를 호출한 것

- 이때 앱의 있는 값은 여전히 유지되고 있음

- 여기서 눈여겨 볼 점은 `onStart`와 `onStop`의 경우 유저가 Activity 사이를 탐색할 때 여러번 호출될 수 있는 것임, 그리고 이 메소드는 앱이 background로 갈 때와 다시 foreground로 시작할 때가 있는 경우 반드시 오버라이딩 해줘야함

- `onRestart`의 경우 위에서 말한 포인트처럼 다시 불릴 때 호출되는 것임 대개 `onCreate`가 한 번 호출되면 그 이후 Destroy되지 않는 이상 `onRestart`로 호출되는 것임, 이 메소드는 앱이 처음 시작하지 않을 경우 불러오게 되는 경우가 있을 때 활용하면 좋음

#### 마지막으로 만약 Activity가 부분적으로 안보인다면?

- `onStart`를 통해 스크린에 앱이 보이고 `onResume`이 호출될 때, 앱의 focus를 얻게 되는 것을 알았는데 이러한 lifecycle을 interactive lifecycle이라고 함

- 앱이 background로 가서 `onPause`이후 focus를 잃게 되고 `onStop`이후 더 이상 보이지 않게됨

- 이 focus와 visibility는 매우 중요한 차이임, Activity가 부분적으로 스크린에 보이게 하지만 user focus가 없을 수 있는 경우가 있기 때문임

- 여기서 activity가 부분적으로 볼 수 있지만 focus가 없는 경우가 있음

- 만약 공유하기 버튼 같은 것을 누르면 화면이 무엇으로 공유할지 인텐트를 불러와서 유저가 공유할 것을 선택하는 선택지를 보여주기 위해서 부분적으로 Activity가 보이는데, 이때 `onPause`만이 호출이 됨

- 이때 `onStop`의 경우 호출되지 않음, 왜냐하면 Activity가 여전히 부분적으로 보이기 때문임 하지만 단지 focus 자체가 앱이 아니라 공유하기를 위해서 선택할 선택지에 가 있기 때문에 focus만 없어서 `onPause`만 호출된 것임

- 그리고 다시 돌아오면 `onResume`이 실행이 됨

### Explore the fragment lifecycle
- Fragment도 역시 Activity와 유사하게 아래와 같은 lifecycle을 가지고 있음

![one](/Android/img/eighteen.png)

- 똑같이 위의 사진에서 나온대로 lifecycle method를 호출할 수 있음, 그리고 그 과정의 로그는 아래와 같

```markdown
21933-21933/com.example.android.navigation I/TitleFragment: onAttach called
21933-21933/com.example.android.navigation I/TitleFragment: onCreate called
21933-21933/com.example.android.navigation I/TitleFragment: onCreateView called
21933-21933/com.example.android.navigation I/TitleFragment: onViewCreated called
21933-21933/com.example.android.navigation I/TitleFragment: onStart called
21933-21933/com.example.android.navigation I/TitleFragment: onResume called
```

- 먼저 `onAttach`가 호출이 됨, Fragment가 그와 관련된 Activity에 있을 때 호출이 됨

- `onCreate`는 Activity에서 `onCreate`와 유사하게 초기에 fragment가 생성될 때 호출이 됨

- `onCreateView`는 Fragment Layout을 inflate 할 때 호출이 됨

- `onViewCreated`는 `onCreateView`에서 리턴되자마자 호출이 됨, 하지만 저장된 상태가 View로 복원되기 전에 호출됨

- `onStart`는 Fragment가 보이기 시작할 때 호출됨 Activity에서 `onStart`와 같음

- `onResume`은 Fragment가 user의 focus를 얻을 때 호출됨 Activity에서 `onResume`과 같음

- 그리고 Navigation대로 진행을 하고 Logcat을 보면 아래와 같음 

```markdown
21933-21933/com.example.android.navigation I/TitleFragment: onAttach called
21933-21933/com.example.android.navigation I/TitleFragment: onCreate called
21933-21933/com.example.android.navigation I/TitleFragment: onCreateView called
21933-21933/com.example.android.navigation I/TitleFragment: onViewCreated called
21933-21933/com.example.android.navigation I/TitleFragment: onStart called
21933-21933/com.example.android.navigation I/TitleFragment: onResume called
21933-21933/com.example.android.navigation I/TitleFragment: onPause called
21933-21933/com.example.android.navigation I/TitleFragment: onStop called
21933-21933/com.example.android.navigation I/TitleFragment: onDestroyView called
```

- 만약 Navigation대로 다음 Fragment로 가고 TitleFragment가 닫히게 된다면 `onPause`, `onStop`, `onDestroyView`가 호출됨

- `onPause`의 경우 fragment가 focus를 잃을 때 호출됨, 이는 Activity에서 `onPause`와 같음

- `onStop`의 경우 fragment가 더 이상 screen에서 보이지 않을 때 호출됨 `onStop`과 같음

- `onDestroyView`의 경우 fragment view가 더 이상 필요로 하지 않을 때 호출됨 해당 View와 관련된 자원을 정리함

- Up Button을 눌러서 원래의 Fragment로 가게 된다면 아래와 같이 나타남

```markdown
21933-21933/com.example.android.navigation I/TitleFragment: onPause called
21933-21933/com.example.android.navigation I/TitleFragment: onStop called
21933-21933/com.example.android.navigation I/TitleFragment: onDestroyView called
21933-21933/com.example.android.navigation I/TitleFragment: onCreateView called
21933-21933/com.example.android.navigation I/TitleFragment: onViewCreated called
21933-21933/com.example.android.navigation I/TitleFragment: onStart called
21933-21933/com.example.android.navigation I/TitleFragment: onResume called
``` 

- 이 때는 `onAttach`와 `onCreate`가 호출되지 않음 왜냐하면 여전히 Fragment 객체가 해당 Activity에 붙어있기 때문에 lifecycle은 `onCreateView`에서 다시 시작이 됨

- 홈 버튼을 누르게 된다면 `onPause`와 `onStop`이 호출이 됨, 이는 Activity에서와 똑같이 진행되는 것임, fragment를 background에 두는 것임

- 다시 앱에 돌아오게 된다면 `onStart`와 `onResume`이 호출이 됨 이때 fragment는 foreground로 돌아오게 됨