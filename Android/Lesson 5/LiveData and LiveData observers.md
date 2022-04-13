### Add LiveData to the GameViewModel
- `LiveData`는 lifecycle을 인식하는 observable data holder임, 예시앱에 있는 current score를 `LiveData`로 감쌀 수 있음

- `LiveData`는 observable 한데 이 의미는 `LiveData`객체로 감싸고 있는 데이터의 변화가 생길 때, observer가 알리는 것을 의미함 즉, 변화를 알아챌 수 있음

- `LiveData`는 data를 가지고 있고 어떠한 데이터에도 wrapper로써 쓸 수 있음

- `LiveData`는 lifecycle을 인식함, observer에 `LiveData`를 붙일 때, observer는 `LifecycleOwner`와 연관됨(일반적으로 Fragment나 Activity)

- `LiveData`는 오직 observer가 `STARTED`나 `RESUMED` 같은 lifecycle 상태에서만 데이터를 업데이트함

- 예시앱에서 score와 word의 필드값을 `LiveData`로 바꿀 수 있음

- `MutableLiveData`는 수정 가능한 `LiveData`를 의미함, 이 클래스는 제네릭 클래스이므로 타입을 정해줘야함, 그리고 추가로 초기화도 해줘야함

```kotlin
// The current word
val word = MutableLiveData<String>()
// The current score
val score = MutableLiveData<Int>()
```
```kotlin
init {

   word.value = ""
   score.value = 0
  ...
}
```

- 그리고 LiveData 객체 참조로 함수에서 갱신해줘야함

```kotlin
fun onSkip() {
   score.value = (score.value)?.minus(1)
   nextWord()
}
```
```kotlin
fun onCorrect() {
   score.value = (score.value)?.plus(1)
   nextWord()
}
```
```kotlin
private fun nextWord() {
   if (!wordList.isEmpty()) {
       //Select and remove a word from the list
       word.value = wordList.removeAt(0)
   }
}
```

- 그리고 UI 갱신할 때 역시 수정해줘야함

```kotlin
/** Methods for updating the UI **/
private fun updateWordText() {
   binding.wordText.text = viewModel.word.value
}
```
```kotlin
private fun updateScoreText() {
   binding.scoreText.text = viewModel.score.value.toString()
}
```
```kotlin
private fun gameFinished() {
   Toast.makeText(activity, "Game has just finished", Toast.LENGTH_SHORT).show()
   val action = GameFragmentDirections.actionGameToScore()
   action.score = viewModel.score.value?:0
   NavHostFragment.findNavController(this).navigate(action)
}
```

### Attach observers to the LiveData objects
- 위에서 `LiveData`를 바탕으로 코드를 수정했다면 이제 여기서 `LiveData`객체에 `Observer`객체를 붙이는게 필요함, `FragmentView`가 `LifecycleOwner`임

- 여기선 `viewLifecycleOwner`로 넘김, Fragment 같은 경우, 자체적인 Fragment lifecycle과 fragment's view의 lifecycle로 두 가지의 lifecycle이 존재할 수 있음, 이미 쓰지 않은 Fragment여도 그렇기 때문에 `viewLifecycleOwner`를 사용함

- `GameFragment`에서 `onCreateView`에서 current score에 대한`viewModel.score`에 `LiveData`객체에 `Observer` 객체를 붙임

- `observe` 메소드를 사용해, `viewModel`을 초기화 한 이후에 코드를 넣음(람다식 사용, `androidx.lifecycle.Observer`를 import 해야함)

```kotlin
viewModel.score.observe(viewLifecycleOwner, Observer { newScore ->
})
```

- 그리고 위처럼 만든 observer에서 관찰하고 있는 `LiveData`객체가 변할 때, 이벤트를 받음, 그래서 observer 안에 새로운 score로 score `TextView`를 갱신하면 됨

```kotlin
// Setting up LiveData observation relationship
        viewModel.score.observe(viewLifecycleOwner, Observer {
            newScore -> binding.scoreText.text = newScore.toString()
        })
```

- current word에도 동일하게 적용시킴

```kotlin
/** Setting up LiveData observation relationship **/
viewModel.word.observe(viewLifecycleOwner, Observer { newWord ->
   binding.wordText.text = newWord
})
```

- `score`와 `word`의 값이 변할 때, 화면에 보여지는 `score`와 `word`는 자동으로 변함

- `GameFragment`에서 `updateWordText()`, `updateScoreText()` 등 UI를 갱신하는 메소드는 없어도 됨, `LiveData` observer 메소드가 자동으로 갱신해주기 때문에

- 즉, `LiveData`와 `LiveData` observer를 활용하는 것임

### Encapsulate the LiveData
- Encapsulation은 객체 필드의 직접적인 접근을 제한하는 방법임

- 객체를 encapsulate하면 내부 private 필드를 수정하기 위해서 public method를 사용해야함, 이 encapsulation을 통해 어떻게 다른 클래스가 내부 필드를 조정할지 컨트롤 할 수 있음

- `score`와 `word`가 `value`를 통해서 외부에 오픈되어 있지만 이를 `private`으로 해야함, 그렇다고 완전히 막으면 안되고 데이터를 읽긴 해야하기 때문에 `MutableLiveData`와 `LiveData`를 섞어서 씀

- `MutableLiveData`는 수정이 가능함, `ViewModel`안에서는 수정이 가능해야 하므로 `MutableLiveData`를 사용함

- `LiveData`는 읽기만 가능하고 수정은 안됨, `ViewModel` 외부에서는 data는 읽을 수 있고 수정이 불가능하므로 `LiveData`로 노출시켜야함

- 이에 맞게 수정을 해주면 됨, 아래와 같이 수정할 부분은 `_score`로 그리고 그걸 `score`에 저장해서 외부에서 쓰게

```kotlin
// The current score
private val _score = MutableLiveData<Int>()
val score: LiveData<Int>
   get() = _score
```

- 그리고 내부 로직도 수정을 함

```kotlin
init {
   ...
   _score.value = 0
   ...
}

...
fun onSkip() {
   _score.value = (score.value)?.minus(1)
  ...
}

fun onCorrect() {
   _score.value = (score.value)?.plus(1)
   ...
}
```

- `word`도 동일하게 작업을 해 줌

```kotlin
// The current word
private val _word = MutableLiveData<String>()
val word: LiveData<String>
   get() = _word
...
init {
   _word.value = ""
   ...
}
...
private fun nextWord() {
   if (!wordList.isEmpty()) {
       //Select and remove a word from the list
       _word.value = wordList.removeAt(0)
   }
}
``` 

### Add a game-finished event
- 예시앱에서 주어진 단어를 모두 사용했다면 그 부분에 대해서 자동으로 끝나게끔 기능을 구현하기 위해서 해당 Fragment의 `ViewModel`과 통신을 해야함, 이를 위해서 `LiveData` observer pattern을 활용함

- observer pattern은 객체간의 통신을 지정함, observable과 observers로 observable은 상태의 변화에 대해 observer에 알리는 객체임

- 여기서 `LiveData`의 상황에서는 `LiveData`객체가 observable이고 observers는 Fragment같은 UI Controller의 메소드임

- `LiveData`로 감싼 데이터가 변화가 일어나 상태가 변하면 Fragment의 `ViewModel`에서 `LiveData` 클래스가 통신을 함

![one](/Android/img/twentyfour.png)

- 이를 활용하기 위해서 `LiveData` observer pattern을 사용할 것임

- `GameViewModel`에서 `Boolean`인 `MutableLiveData`를 만듬, 이 객체는 game-finished event를 잡고 있음

- 이 값을 초기화하고 backing property를 아래와 같이 초기화함

```kotlin
// Event which triggers the end of the game
private val _eventGameFinish = MutableLiveData<Boolean>()
val eventGameFinish: LiveData<Boolean>
   get() = _eventGameFinish
```

- 그리고 이벤트를 처리하는 메소드를 만듬

```kotlin
/** Method for the game completed event **/
    fun onGameFinish() {
        _eventGameFinish.value = true
    }
```

- 그런 다음 word list가 비면 해당 메소드를 호출함

```kotlin
private fun nextWord() {
   if (wordList.isEmpty()) {
       onGameFinish()
   } else {
       //Select and remove a _word from the list
       _word.value = wordList.removeAt(0)
   }
}
```

- 그리고 `GameFragment`에서 `viewModel`을 초기화함 그리고 `eventGameFinish`에 해당하는 observer를 붙이고 `observe()`를 함, 그리고 람다식으로 해당 메소드를 호출함

```kotlin
// Observer for the Game finished event
viewModel.eventGameFinish.observe(viewLifecycleOwner, Observer<Boolean> { hasFinished ->
   if (hasFinished) gameFinished()
})
```

- 하지만 여기서 모든 단어를 다 쓰고 자동으로 넘어가더라도 configuration change가 발생하면 이 부분에 대해서 계속적으로 gameFinished가 나타남

- 보통 `LiveData` 전달자는 오직 데이터가 변화가 있을 때만 observers를 업데이트함, 하지만 이와 같은 예외는 observers는 또한 observer가 inactive에서 active한 상태로 변할 때도 update를 하기 때문에 일어남

- 만약 gameFragment가 configuration change로 인해 inactive에서 active 상태로 가면 이 역시 변화를 감지한 것이라서 다시 `gameFinished`메소드가 호출되는 것임

- 이 상황에선 `GameViewModel`에 `eventGameFinish` flag를 다시 세팅해주면 됨

```kotlin
/** Method for the game completed event **/
    fun onGameFinishComplete() {
        _eventGameFinish.value = false
    }
```

- 그리고 `GameFragment`에서 끝내는 함수에 이 함수를 호출

```kotlin
    private fun gameFinished() {
        Toast.makeText(activity, "Game has just finished", Toast.LENGTH_SHORT).show()
        val action = GameFragmentDirections.actionGameToScore()
////        action.score = viewModel.score
//        // LiveData 사용 후
        action.score = viewModel.score.value?:0
        findNavController(this).navigate(action)
        viewModel.onGameFinishComplete()
    }
```

### Add LiveData to the ScoreViewModel
- 위처럼 처리한 LiveData를 이제 `ScoreViewModel`에도 적용

```kotlin
private val _score = MutableLiveData<Int>()
val score: LiveData<Int>
   get() = _score

init {
   _score.value = finalScore
}
```

- 그리고 `ScoreFragment`에 동일하게 적용

```kotlin
// Add observer for score
viewModel.score.observe(viewLifecycleOwner, Observer { newScore ->
   binding.scoreText.text = newScore.toString()
})
```

### Add the Play Again button
- Play Again 버튼을 추가하고 `LiveData` 이벤트를 통해 클릭 리스너를 적용시킬 수 있음

```kotlin
// eventPlayAgain을 위한 LiveData
    private val _eventPlayAgain = MutableLiveData<Boolean>()
    val eventPlayAgain: LiveData<Boolean>
       get() = _eventPlayAgain
```

- 그리고 set하고 reset하는 메소드도 정의함

```kotlin
fun onPlayAgain() {
   _eventPlayAgain.value = true
}
fun onPlayAgainComplete() {
   _eventPlayAgain.value = false
}
```

- 그리고 그에 맞춰서 `ScoreFragment`에도 observer를 추가함

```kotlin
// Navigate back to game when button is pressed
        viewModel.eventPlayAgain.observe(viewLifecycleOwner, Observer {
            playAgain -> if(playAgain) {
                findNavController().navigate(ScoreFragmentDirections.actionRestart())
                viewModel.onPlayAgainComplete()
            }
        })
```

- 그리고 클릭 리스너를 추가함

```kotlin
binding.playAgainButton.setOnClickListener {  viewModel.onPlayAgain()  }
```