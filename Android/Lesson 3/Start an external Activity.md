### Set up and use the Safe Args plugin
- 유저가 만약 결과를 공유하기 위한다면 그 전에 Fragment에서 보낼 때 파라미터로 보내줘야함

- 이러한 transactions의 버그를 방지하고 type-safe하게 하기 위해서 `Safe Args`라고 하는 Gradle plugin을 쓸 수 있음

- 이 plugin은 `NavDirection`을 생성하고 Fragment 사이에서 argument를 전달할 수 있음

- 만약 Fragment 사이에서 data를 전달해준다면 일반적으로 `Bundle` 클래스를 쓸 수 있음(key-value형태인)

- 이 key-value는 딕셔너리 같은 자료구조 중 하나이고 이를 `Bundle`을 통해서 Fragment A에서 Fragment B로 전달할 수 있음

- 하지만 여기서 몇 가지 에러가 발생할 수 있음 `Type mismatch error`나 `Missing key error`가 발생할 수 있음, 이러한 에러는 `Bundle`을 통한 key-value 형태를 쓸 때 얼마든지 발생할 수 있는 에러이고 이 부분을 방지하기 위해서 Android's Navigation Architecture Component에 `Safe Args`가 포함되어 있음

- 이 `Safe Args`는 Gradle plugin으로 앱을 실행하기 전에 컴파일 시점에서 에러를 탐지할 수 있게 해 줌

- 이를 사용하기 위해서 `project` 단위와 `app-level`에서 의존성 추가를 해줘야함, 그러면 `NavDirection` 클래스가 생성되서 쓸 수 있음

- 공식문서상에서 더 상세히 다룸 
- [Safe Args](https://developer.android.com/guide/navigation/navigation-pass-data?hl=ko)

- 이는 예를 들어서 `GameFragment`가 있다면 `GameFragmentDirections`라는 클래스가 있어서 여기서 데이터 전달을 위해서 actionID를 대체할 수 있음

### Add and pass argument
- 앞서 `Direction` 클래스를 활용해서 `navController`를 설정했으므로 argument는 안전하게 넘어감, 이와 유사하게 다른 Fragment 역시 똑같이 처리할 수 있음

- 그리고 `navigation.xml`에 Argument를 직접 추가함, 이제 이 값에 대해서 `GameFragment` 상에서 Parameter로 넘기면 그 값을 받게끔 할 수 있음(`navController` 안에서)

- ![one](/Android/img/twelve.png)

- 그리고 `GameWonFragment`에서 넘겨받은 값을 bundle에서 argument를 뽑아 `args.numCorrect` 등으로 받아서 표현할 수 있음

```kotlin
val args = GameWonFragmentArgs.fromBundle(requireArguments())
Toast.makeText(context, "NumCorrect: ${args.numCorrect}, NumQuestions: ${args.numQuestions}", Toast.LENGTH_LONG).show()
```

- 추가적으로 `safe argument`를 사용하기 위해서 기존 Fragment 클래스에서도 `navController`에서 `NavDirection`으로 넘겨서 처리할 수 있음(아래와 같이)

```kotlin
binding.playButton.setOnClickListener { view: View ->
    view.findNavController()
            .navigate(TitleFragmentDirections.actionTitleFragmentToGameFragment())
}
```

- 결과에 변화는 크게 없지만 `NavDirection`을 활용해서 arguments를 넘기는게끔 설정이 된 것임

### Sharing game results
- Android intent에서 Implicit Intent를 통해서 공유 기능을 활용할 수 있음

- 이 Intent는 Activity에 대한 navigate 역할도 함, 아주 간단한 메시지 객체로써 Android 컴포넌트 사이들에서 통신을 할 수 있음

- Explicit Intent는 특정 타켓에 메시지를 전달하는 것이라고 볼 수 있고, Implicit Intent는 어떤 앱인지 모르고 Activity가 문제를 다루지 않고 시작할 수 있음

- 예를 들어 사진을 찍을때라고 하면 어떤 앱을 쓰는지 Activity가 그 일을 하는 것을 신경쓰지 않음, 왜냐하면 다양한 App들이 이 Implicit Intent를 처리할 수 있기 때문임

- 그래서 안드로이드는 이 요청을 다루게 할 앱을 선택하게 보여줌

- 이 Implicit Intent는 반드시 어떤 작업을 처리하는지 나타내 줄 `ACTION`을 가지고 있어야함

- 흔한 것으로 `ACTION_VIEW`, `ACTION_EDIT`, `ACTION_DIAL`등이 있음, 이외에도 많은 것들이 존재함

- [공식문서](https://developer.android.com/training/basics/intents/sending?hl=ko)
