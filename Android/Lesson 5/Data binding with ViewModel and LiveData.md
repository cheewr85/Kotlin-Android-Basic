### Add ViewModel data binding
- 이전의 data binding은 view의 액세스하는데 type-safe한 방식을 위해서 쓴 것임, 하지만 data binding의 진정한 힘은 이름 그대로 data를 view 객체에 직접 binding 하는 것임

- 현재 앱의 구조는 아래와 같음, XML 레이아웃에 view를 정의하고 이 view의 데이터는 `ViewModel` 객체가 가지고 있음

- 각 view와 그에 상응하는 `ViewModel` 사이에는 그 사이를 중계하는 UI Controller가 있음

![one](/Android/img/twentyfive.png)

- 예를 들어 `Got It` 버튼 view는 xml 레이아웃 파일에 저장되어 있고 `Got It` 버튼을 누르면 `GameFragment`에 있는 클릭 리스너는 `GameViewModel`에 있는 그에 상응하는 클릭 리스너를 호출함

- 그 score는 `GameViewModel`에서 업데이트 됨

- 여기서 `Button` view는 `GameViewModel`과 직접적으로 통신을 하지 않음 `GameFragment`안에 있기 위해 클릭 리스너가 필요함

- 만약 레이아웃에 있는 view가 `ViewModel`객체에 있는 data와 직접적으로 통신한다면 `UI Controller`가 중계해줄 필요없이 훨씬 간소화 될 것임

![one](/Android/img/twentysix.png)

- `ViewModel` 객체는 모든 UI data를 가지고 있음 `ViewModel` 객체를 data binding으로 통과시켜 views와 `ViewModel` 객체 사이의 통신을 자동으로 할 수 있음

- 예시 앱에서 `GameViewModel`과 `ScoreViewModel` 클래스와 상응하는 XML 레이아웃을 연결시킬 것임 click event를 다루기 위해 listener binding도 세팅할 것임

- 먼저 `GameFragment`에 맞는 `game_fragment.xml`에 `GameViewModel` 타입에 data-binding 변수를 추가함

```xml
<layout ...>

   <data>

       <variable
           name="gameViewModel"
           type="com.example.android.guesstheword.screens.game.GameViewModel" />
   </data>
  
   <androidx.constraintlayout...
```

- `GameFragment` 파일에서 `GameViewModel`을 데이터 바인딩으로 전달함

- 이것을 처리하기 위해서 `binding.gameViewModel`에서 `viewModel`을 할당시켜줌(`onCreateView`안에 `viewModel`초기화 한 이후에 추가함)

```kotlin
// Set the viewmodel for databinding - this allows the bound layout access 
// to all the data in the ViewModel
binding.gameViewModel = viewModel
```

- Listener bindings은 `onClick`, `onZoomIn`, `onZoomOut`같은 이벤트가 발생시 실행되는 binding 표현임, 람다식으로 표현함

- Data binding은 listener를 만들고 view의 listener를 설정함, 이 listener 이벤트가 발생하면 이를 람다식으로 나타냄

- 그래서 이 부분에선 `GameFragment`에서의 click listener를 xml파일에 listener binding으로 대체할 수 있음

```xml
<Button
   android:id="@+id/skip_button"
   ...
   android:onClick="@{() -> gameViewModel.onSkip()}"
   ... />
```

- `correct_button`, `end_game_button`도 수정가능함

```xml
<Button
   android:id="@+id/correct_button"
   ...
   android:onClick="@{() -> gameViewModel.onCorrect()}"
   ... />
```
```xml
<Button
   android:id="@+id/end_game_button"
   ...
   android:onClick="@{() -> gameViewModel.onGameFinish()}"
   ... />
```

- 그러면 기존의 `GameFragment`에서의 클릭 리스너를 설정하는 부분과 함수를 다 지워도 됨, 아래의 부분

```kotlin
binding.correctButton.setOnClickListener { onCorrect() }
binding.skipButton.setOnClickListener { onSkip() }
binding.endGameButton.setOnClickListener { onEndGame() }

/** Methods for buttons presses **/
private fun onSkip() {
   viewModel.onSkip()
}
private fun onCorrect() {
   viewModel.onCorrect()
}
private fun onEndGame() {
   gameFinished()
}
```

- `ScoreViewModel`에도 동일하게 적용

```xml
<layout ...>
   <data>
       <variable
           name="scoreViewModel"
           type="com.example.android.guesstheword.screens.score.ScoreViewModel" />
   </data>
   <androidx.constraintlayout.widget.ConstraintLayout
```
```xml
<Button
   android:id="@+id/play_again_button"
   ...
   android:onClick="@{() -> scoreViewModel.onPlayAgain()}"
   ... />
```
```kotlin
viewModel = ...
binding.scoreViewModel = viewModel
```

- data-binding의 경우 컴파일 시점에서 error를 알 수 있기 때문에, 이 부분과 관련하여 알 수 없는 에러 메시지를 받을 수 있음, 이때 `databinding`과 XML에서 `onClick` 부분과 `<data>` 부분을 다 따져봐야함

- 즉 만약 에러가 생긴다면 명확하게 어디서 에러가 났는지 파악이 힘들 수 있다는 것임

### Add LiveData to data binding
- `ViewModel` 객체를 사용하는 `LiveData`에서 Data binding은 잘 활용됨

- 이 방법은 observer method 방법을 사용하지 않고 data의 변화에 대해 data-binding source를 통해 UI에 알릴 수 있음

- 먼저 textview에 `text`를 binding 변수인 `gameViewModel`을 활용해서 설정함

```xml
<TextView
   android:id="@+id/word_text"
   ...
   android:text="@{gameViewModel.word}"
   ... />
```

- 여기서 `word.value`가 아닌 실제 `LiveData` 객체를 연결함 `LiveData`객체는 현재 `word`의 값을 표시함 `word`가 null이라면 `LiveData`객체는 빈 String을 보여줄 것임

- `GameFragment`에서 `onCreateView()`안에 `gameViewModel` 초기화 후에 fragment view를 binding 변수의 lifecycle owner로 설정함, 이 부분은 위에서 설정한 `LiveData`범위에 정해지고 xml 레이아웃 파일에서 자동으로 객체가 업데이트 됨

```kotlin
binding.gameViewModel = ...
// Specify the fragment view as the lifecycle owner of the binding.
// This is used so that the binding can observe LiveData updates
binding.lifecycleOwner = viewLifecycleOwner
```

- 위와 같이 처리하고 기존의 `observe` 방식을 제거함, 어차피 위에서 Lifecycle owner를 설정했으므로 LiveData는 ViewModel에 있기 때문에 알아서 그 변화를 체크해서 처리하게됨 

- score LiveData도 동일하게 적용함

```xml
<TextView
   android:id="@+id/score_text"
   ...
   android:text="@{String.valueOf(scoreViewModel.score)}"
   ... />
```
```kotlin
binding.scoreViewModel = ...
// Specify the fragment view as the lifecycle owner of the binding.
// This is used so that the binding can observe LiveData updates
binding.lifecycleOwner = viewLifecycleOwner
```

- 추가로 layout에서 String format을 data binding을 통해서 추가를 할 수 있음

- 우선 `string.xml`에 해당 string을 추가하고 xml에 아래와 같이 씀

```xml
<TextView
   android:id="@+id/word_text"
   ...
   android:text="@{@string/quote_format(gameViewModel.word)}"
   ... />
```
```xml
<TextView
   android:id="@+id/score_text"
   ...
   android:text="@{@string/score_format(scoreViewModel.score)}"
   ... />
```