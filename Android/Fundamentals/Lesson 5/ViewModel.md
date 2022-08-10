### Explore the starter code
- MainActivity에서 Fragment를 담고 있고 각각 Fragment들이 구현되어 있고 Navigation으로 Fragment간의 설정이 되어 있는 구조임

### Find problems in the starter app
- 우선 여기서 configuration change가 발생하면 그 값의 상태가 저장되어 있지 않은 상황임, 여기서 우리가 배운 해결책은 `onSaveInstanceState()`를 활용하는 것임

- 하지만 해당 메소드는 bundle에 상태를 저장하게끔 추가적인 코드를 발생하게 하고 상태를 복원하기 위해 로직을 구현해야함 그리고 저장할 수 있는 데이터가 적음

- 그리고 마지막 화면에서 버튼을 누르면 navigate도 안되어 있음

- 이 부분을 해결하기 위해서 App Architecture 요소를 활용할 것임

#### App Architecture
- App Architecture는 코드가 구성되고 특정 시나리오에서 잘 수행되며 작업하기 쉽도록 앱의 클래스와 이들 간의 관계를 디자인하는 방법임 

- 안드로이드 앱 아키텍쳐는 MVVM 아키텍쳐 패턴과 유사함

- 예시에서의 앱은 관심사가 분리되어 설계되고 각각의 클래스는 별도의 관심사를 처리함

- 여기서 class는 UI Controller인 `ViewModel`과 `ViewModelFactory`임

- UI Controller는 `Activity`나 `Fragment`같은 UI 기반의 클래스임, 이 부분에선 오직 UI를 처리하고 View를 보여준다던지 유저의 input을 잡는 것과 같은 OS의 상호작용에 관한 로직만 있어야함

- 예를 들어 UI Controller에 text를 보여주는 것을 정하는 로직같은 의사결정 로직은 있으면 안됨

- 예시에서 각각 `GameFragment`, `ScoreFragment`, `TitleFragment`가 있는데 관심사 분리에 따라 `GameFragment`는 오직 화면에 game과 관련한 요소를 그리고 유저가 버튼을 눌렀을 때 아는정도만의 책임만 있음(그 이상의 무언가는 없음), 유저가 버튼을 누르면 이 정보는 `GameViewModel`로 넘어감

- `ViewModel`은 Fragment나 Activity에 표시해야할 데이터를 `ViewModel`로 가지고 있음

- `ViewModel`은 간단한 계산이나 data를 변형시켜 UI Controller에 표시할 데이터를 준비함, 이 Architecture에선 `ViewModel`이 의사결정을 수행함

- `GameViewModel`은 score 값, words의 리스트, 현재 값등을 가지고 있음, 왜냐하면 이 값들이 화면에 보여지기 때문임 또한 현재 data의 상태를 결정하기 위한 간단한 연산을 수행하는 비즈니스 로직도 담고 있음

- `ViewModelFactory`는 `ViewModel`객체를 인스턴스화 한 것으로 생성자 매개변수가 있거나 없음

![one](/Android/img/twenty.png)

### Create the GameViewModel
- `ViewModel`은 UI와 관련된 데이터를 관리하고 저장하는데 예시 앱에선 Fragment와 관련된 값을 그렇게 함

- `GameViewModel`은 `GameFragment`와 연관되어 있음

- 의존성 역시 추가를 해주고 `GameViewModel`을 만들어서 `ViewModel`을 상속받아 초기화를 시킴(Lifecycle을 보기 위해서)

```kotlin
class GameViewModel : ViewModel() {
   init {
       Log.i("GameViewModel", "GameViewModel created!")
   }
}
```

- ViewModel은 관련된 Fragment가 detact되거나 activity가 finish 되면 소멸됨

- `ViewModel`이 소멸되기 전에 `onCleared()` 콜백을 호출하여 자원을 정리함

```kotlin
override fun onCleared() {
   super.onCleared()
   Log.i("GameViewModel", "GameViewModel destroyed!")
}
```

- 위와 같이 Log를 추가해서 Lifecycle을 알아볼 수 있음

- 그리고 `ViewModel`은 UI Controller와 연관되어야만 하는데 두 개를 묶기 위해서 UI Controller 안에 `ViewModel`을 참조하면 됨

- 즉 아래와 같이 `GameFragment`에 필드타입을 `GameViewModel`로 추가해서 저장함

```kotlin
private lateinit var viewModel: GameViewModel
```

- Configuration change가 있으면 UI Controller는 재생성됨, 하지만 `ViewModel` 인스턴스는 생존해 있음

- `ViewModel` 클래스를 사용해서 `ViewModel` 인스턴스를 생성하면 Fragment가 재생성될 때마다 새로운 객체가 매번 생성됨, 대신 `ViewModelProvider`를 사용하는 `ViewModel` 인스턴스를 생성함

![one](/Android/img/twentyone.png)

- `ViewModel` 인스턴스를 바로 생성하는 것보다 항상 `ViewModelProvider`를 통해서 `ViewModel`을 생성해야함

#### ViewModelProvider
- `ViewModelProvider`는 이미 존재하고 있다면 존재하는 `ViewModel`을 반환하고, 만약 이미 존재하고 있지 않다면 새로운 것을 만들어서 `ViewModel`을 반환함

- `ViewModelProvider`는 주어진 범위에 연관된(Activity나 Fragment) `ViewModel` 인스턴스를 생성함

- 생성된 `ViewModel`은 범위가 살아있는 한 계속됨 만약 그 범위가 Fragment라면 `ViewModel`은 Fragment가 detach 될 때까지 유지가 됨

- `ViewModelProvider.get()`메소드를 통해 `ViewModel`을 초기화함

- 앞서 예시에서 `GameFragment`에 `private lateinit var viewModel : GameViewModel`로 초기화한 변수값에 대해서 초기화를 해야함

- `onCreateView`에서 바인딩 값 정의 뒤에 `ViewModelProvider.get()`메소드를 사용하고 여기서 연관된 `GameFragment` context와 `GameViewModel` 클래스를 보냄

```kotlin
Log.i("GameFragment", "Called ViewModelProvider.get")
viewModel = ViewModelProvider(this).get(GameViewModel::class.java)
```

- 여기서 이제 Logcat을 보면 `onCreateView`가 호출될 때 `GameFragment`는 `ViewModelProvider.get`메소드를 `GameViewModel`을 만들기 위해서 호출함, 그럼 아래와 같이 나옴

```markdown
I/GameFragment: Called ViewModelProvider.get
I/GameViewModel: GameViewModel created!
```

- 그리고 여기서 만약 화면을 돌리게 된다면 `GameFragment`는 매번 소멸되고 재생성됨 그래서 `ViewModelProvider.get()`이 계속 호출됨, 하지만 `GameViewModel`은 오직 한 번만 생성되고 재생성되거나 소멸되지 않음

```markdown
I/GameFragment: Called ViewModelProvider.get
I/GameViewModel: GameViewModel created!
I/GameFragment: Called ViewModelProvider.get
I/GameFragment: Called ViewModelProvider.get
I/GameFragment: Called ViewModelProvider.get
```

- 추가로 game fragment를 나가거나 게임을 종료하면 `GameFragment`는 아예 소멸됨, 그래서 이와 연관된 `GameViewModel`도 사라짐, 그래서 그 때 `onCleared()`가 호출됨

```markdown
I/GameFragment: Called ViewModelProvider.get
I/GameViewModel: GameViewModel created!
I/GameFragment: Called ViewModelProvider.get
I/GameFragment: Called ViewModelProvider.get
I/GameFragment: Called ViewModelProvider.get
I/GameViewModel: GameViewModel destroyed!
```

### Populate the GameViewModel
- `ViewModel`은 configuration change에도 남아있기 때문에 data를 저장하기 아주 좋은 곳임

- 화면에 보여질 data를 담거나 데이터를 처리할 코드를 `ViewModel`에 씀

- `ViewModel`엔 fragments나 Activity, View에 대한 참조를 담으면 안됨, 왜냐하면 해당 요소들은 configuration change때 사라지기 떄문임

![one](/Android/img/twentytwo.png)

- `ViewModel`을 사용하기 전에는 configuration change가 생기면 `GameFragment`가 소멸되고 재생성되면서 data는 사라짐

- `ViewModel`을 추가하고 `GameFragment`의 UI 데이터를 `ViewModel`로 옮기면 fragment에 나타날 모든 데이터들은 `ViewModel`에 있는 것임

- configuration change가 일어나도 `ViewModel`은 존재하기 때문에 data도 유지가 됨

![one](/Android/img/twentythree.png)

- 그래서 기존의 코드를 개선해서 이러한 데이터들을 `ViewModel`로 옮길것임

- Fragment에 정의한 필드값에 대해서 먼저 `ViewModel`로 옮김(여기서 바인딩 객체는 옮기면 안됨)

- 그리고 word를 화면에 보여주는 걸 결정해주는 메소드도 역시 옮김

```kotlin
// The current word
   var word = ""
   // The current score
   var score = 0
   // The list of words - the front of the list is the next word to guess
   private lateinit var wordList: MutableList<String>

   /**
    * Resets the list of words and randomizes the order
    */
   private fun resetList() {
       wordList = mutableListOf(
               "queen",
               "hospital",
               "basketball",
               "cat",
               "change",
               "snail",
               "soup",
               "calendar",
               "sad",
               "desk",
               "guitar",
               "home",
               "railway",
               "zebra",
               "jelly",
               "car",
               "crow",
               "trade",
               "bag",
               "roll",
               "bubble"
       )
       wordList.shuffle()
   }

   init {
       resetList()
       nextWord()
       Log.i("GameViewModel", "GameViewModel created!")
   }
   /**
    * Moves to the next word in the list
    */
   private fun nextWord() {
       if (!wordList.isEmpty()) {
           //Select and remove a word from the list
           word = wordList.removeAt(0)
       }
       updateWordText()
       updateScoreText()
   }
```

- 그리고 이 data를 다루는 메소드는 반드시 init 블럭에 넣어줘야함, 왜냐하면 `ViewModel`이 생성될 때 word list를 초기화 할 것이므로 넣어줘야함

- 추가로 UI를 업데이트 해주는 클릭 리스너 역시 옮겨줌, 여기서 중요한 건 UI를 업데이트 하는 것은 Fragment에 남아있어도 되지만 data를 처리하는 부분은 `ViewModel`로 옮겨줘야함

- 여기서 UI 업데이트 하는 부분은 옮기지 않음, 그리고 `private` 설정을 뺌 왜냐하면 fragment에서 해당 메소드를 호출해서 값을 처리한 것을 받으므로
```kotlin
// 데이터 처리를 하는 함수이기 때문에 ViewModel로 옮김
    /** Methods for buttons presses **/
    fun onSkip() {
        score--
        nextWord()
    }

    fun onCorrect() {
        score++
        nextWord()
    }
```

- 그리고 `ViewModel` 적용을 위해 내부 함수도 업데이트 해줘야함 `GameFragment`에서의 함수는 `viewModel`을 불러와서 처리함

```kotlin
    /** Methods for buttons presses **/

    private fun onSkip() {
        // ViewModel 사용 전
//        score--
//        nextWord()
        // ViewModel 사용 후, ViewModel에서 데이터를 갱신하고 Fragment에선 Update만 진행
        viewModel.onSkip()
        updateWordText()
        updateScoreText()
    }

    private fun onCorrect() {
        // ViewModel 사용 전
//        score++
//        nextWord()
        // ViewModel 사용 후, ViewModel에서 데이터를 갱신하고 Fragment에서 Update만 진행
        viewModel.onCorrect()
        updateScoreText()
        updateWordText()
    }
```

- 위와 같이 진행됨으로써 이제 `ViewModel`에서 데이터를 저장하고 복원할 수 있게 분리됨

### Use a ViewModelFactory
- EndGame Button을 누르고 화면은 넘어갔지만 score은 보이지 않았음 이때 `ViewModel`에서 가지고 있는 score를 `ScoreFragment`에 보여주게끔 처리할 수 있음

- 이때 Factory Method Pattern을 사용해서 `ViewModel`이 초기화 되는동안 score value를 넘길 수 있음

- Factory Method Pattern은 factory methods를 사용해 객체를 생성하는 creational design pattern임

- Factory Method는 같은 클래스의 인스턴스를 반환하는 메소드를 말함

- 여기서 `ScoreFragment`를 위해 매개변수가 있는 `ViewModel`을 만들고 `ViewModel`을 인스턴스화하는 Factory Method도 만들 수 있음

- `ViewModel`은 아래와 같이 같은 패키지안에 쉽게 만들 수 있음

```kotlin
package com.example.android.guesstheword.screens.score

import android.util.Log
import androidx.lifecycle.ViewModel

class ScoreViewModel(finalScore: Int) : ViewModel() {
    // The final score
    var score = finalScore
    init {
        Log.i("ScoreViewModel", "Final score is $finalScore")
    }
}
```

- 그리고 추가적으로 `ScoreViewModelFactory`도 만듬(`ScoreViewModel`객체를 인스턴스화 하기 위한 클래스)

```kotlin
package com.example.android.guesstheword.screens.score

import androidx.lifecycle.ViewModelProvider

class ScoreViewModelFactory(private val finalScore: Int) : ViewModelProvider.Factory {
}
```

- 여기서 끝나지 않고 `create()`메소드를 오버라이딩 하고 반환 값으로 새로운 `ScoreViewModel`객체를 반환함

```kotlin
package com.example.android.guesstheword.screens.score

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

// ScoreViewModel을 인스턴스화 하기 위한 Factory
class ScoreViewModelFactory(private val finalScore: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        // ScoreViewModel 클래스로 인스턴스화 됐다면 해당 값을 넘겨주게 리턴함
        if(modelClass.isAssignableFrom(ScoreViewModel::class.java)) {
            return ScoreViewModel(finalScore) as T
        }
        // 아니라면 예외처리함
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
```

- 그리고 마지막으로 이를 `ScoreFragment`에 적용시키면 됨, 위에서 정의한 ViewModel과 ViewModelFactory를 필드값으로 먼저 만듬

```kotlin
private lateinit var viewModel: ScoreViewModel
private lateinit var viewModelFactory: ScoreViewModelFactory
```

- 그런 다음, `onCreateView()`에서 `viewModelFactory`와 `viewModel` 객체를 초기화시킴

- `viewModelFactory`의 경우 argument bundle의 최종값을 넘기게끔 초기화함

```kotlin
viewModelFactory = ScoreViewModelFactory(ScoreFragmentArgs.fromBundle(requireArguments()).score)
```

- 이때 `ViewModelProvider.get()` 메소드를 활용하여 관련된 fragment context와 `viewModelFactory`를 넘겨줌, 여기서 `ScoreViewModel`객체는 factory method를 사용하여 `viewModelFactory`에 정의된대로 생성될 것임

```kotlin
viewModel = ViewModelProvider(this, viewModelFactory)
       .get(ScoreViewModel::class.java)
```

- 그런 다음 `scoreText`에 이를 적용시킴

```kotlin
binding.scoreText.text = viewModel.score.toString()
```

- 이 Factory 패턴이 꼭 필요한 것은 아니지만 사용하면 유용한 경우가 있음