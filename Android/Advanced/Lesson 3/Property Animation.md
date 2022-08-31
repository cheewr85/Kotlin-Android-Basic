- Animation은 유저가 잠재적으로 복잡하거나 헷갈린 스크린에 대한 정보를 이해하는데 도움을 주는 강력한 도구임

- UI로써 쓸 쑤 있는 animation은 매우 다양한 종류가 있음, fade-in으로 나타나거나 fade-out으로 사라지거나, screen에서 움직이거나 도형이 다양한 방식으로 변한다던가

- animation은 그들 자체로 단일 객체로 변화 상태로써 motion을 제공하거나 다른 animation과 동등하게 많은 변화가 함께 일어날 수도 있음

- Android는 UI 객체에 대해 animation을 위한 다양한 기능을 제공해줌

### Familiarizing yourself with the UI code
- 파일을 잘 탐색해본다면 `FrameLayout`이 background가 없음을 알 수 있음, 이것은 `ImageView`를 활용해서 animation을 표현할 것이기 때문임

- 그래서 처음에 본다면 아래와 같이 초기화 된 코드가 있음을 확인할 수 있음, 이는 초기화와 버튼 클릭시 해당 animation 처리를 기본 세팅한 것임

```kotlin
class MainActivity : AppCompatActivity() {

    lateinit var star: ImageView
    lateinit var rotateButton: Button
    lateinit var translateButton: Button
    lateinit var scaleButton: Button
    lateinit var fadeButton: Button
    lateinit var colorizeButton: Button
    lateinit var showerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        star = findViewById(R.id.star)
        rotateButton = findViewById<Button>(R.id.rotateButton)
        translateButton = findViewById<Button>(R.id.translateButton)
        scaleButton = findViewById<Button>(R.id.scaleButton)
        fadeButton = findViewById<Button>(R.id.fadeButton)
        colorizeButton = findViewById<Button>(R.id.colorizeButton)
        showerButton = findViewById<Button>(R.id.showerButton)

        rotateButton.setOnClickListener {
            rotater()
        }

        translateButton.setOnClickListener {
            translater()
        }

        scaleButton.setOnClickListener {
            scaler()
        }

        fadeButton.setOnClickListener {
            fader()
        }

        colorizeButton.setOnClickListener {
            colorizer()
        }

        showerButton.setOnClickListener {
            shower()
        }
    }

    private fun rotater() {
    }

    private fun translater() {
    }

    private fun scaler() {
    }

    private fun fader() {
    }

    private fun colorizer() {
    }

    private fun shower() {
    }

}
```

### Rotating the Star
- `rotater` 함수에 대해서 버튼 클릭시 star가 rotate하게 적용할 것임

- 아래의 코드를 추가할 것임 이는 해당 view에 대해서 회전을 한다는 것을 의미하는 animation임

```kotlin
val animator = ObjectAnimator.ofFloat(star, View.ROTATION, -360f, 0f)
```

- star를 타겟으로 행동할 수 있게 `ObjectAnimator`를 만드는 것임, 이는 star의 `ROTATION`이라는 속성을 animation으로 실행시키게 해 줌

- 이 설정에 대해서 다른 rotation properties 역시 쓸 수 있음, 회전을 어떻게 하냐에 대해서

- 이 때 설정값에 따라서 property, View properties, Property animation으로 설정하는 것임 위와 같이 자세한 상황은 공식문서 참조

- 그리고 rotate 하는 설정을 했기 때문에 그 하단에 시작을 추가함

```kotlin
animator.start()
```

- 하지만 여기서 이 회전하는 시간이 빠르게 설정되어 있어서 빠르게 회전하는데 이때 `duration` 속성을 추가해서 처리함

```kotlin
val animator = ObjectAnimator.ofFloat(star, View.ROTATION, -360f, 0f)
animator.duration = 1000
animator.start()
```

- 이 때 회전을 하는 와중에 animation을 다시 클릭할 수도 있음 하지만 이 때 상황이 어떻든간에 다시 리셋되서 animation이 실행됨 이런 상황은 유저에게 불편함을 안겨주기 때문에 지양해야함

- 이런 상황을 처리할 수 있는 다양한 방법이 있음, 이때 먼저 고려할 것은 유저가 animation이 진행되는 와중에 리셋을 시키지 않고 animation을 끝까지 보게하는것이 우선임

- animator는 일종의 listener로써 animation의 상태가 바뀔 때 코드로써 콜백으로 알려주는 것임

- 그래서 animation의 start, end, pause, resume, repeat에 대한 콜백이 모두 존재함

- 이번에는 ROTATE 버튼에 대해서 animation이 시작하면 비활성화하고 끝나면 활성화하게 설정할 수 있음, 그렇게 되면 animation이 시작되면 버튼이 비활성화되지만 끝나면 다시 활성화됨

```kotlin
val animator = ObjectAnimator.ofFloat(star, View.ROTATION, -360f, 0f)
animator.duration = 1000
animator.addListener(object : AnimatorListenerAdapter() {
    override fun onAnimationStart(animation: Animator?) {
        rotateButton.isEnabled = false
    }
    override fun onAnimationEnd(animation: Animator?) {
        rotateButton.isEnabled = true
    }
})
animator.start()
```

### Translating the Star
- 해당 버튼을 통해서 star를 앞뒤로 움직이게 할 수 있음

- 아래와 같이 animation을 추가하게 된다면, star가 오른쪽으로 200 픽셀 움직이게 됨

```kotlin
val animator = ObjectAnimator.ofFloat(star, View.TRANSLATION_X, 200f)
animator.start()
```

- 이 때 단순히 오른쪽으로 옮기는 처리만 했기 때문에 다시 돌아오게 하기 위해선 추가적인 작업이 필요함, 그리고 그 다음의 후속처리는 되지 않음 왜냐면 단지 animation이 200으로 옮기게만 처리됐기 때문임, 이를 repetition을 통해서 고칠 수 있음

- 이 repetition을 통해서 반복을 할 수 있는데 이전 행동의 반대로 하던가 다시 시작하는 등의 처리가 가능함

- 그래서 animation의 repeat을 변경하여 원래의 지점으로 돌아오게끔 처리를 함

```kotlin
val animator = ObjectAnimator.ofFloat(star, View.TRANSLATION_X, 200f)
animator.repeatCount = 1
animator.repeatMode = ObjectAnimator.REVERSE
animator.start()
```

- 하지만 앞선 rotate와는 다른 문제에 직면하게 됨, 이는 오직 end value만 있기 때문에, animation이 시작하는 와중에 이를 클릭하게 된다면 그 중간값에서 다시 animation을 시작해서 처리하는 것임 그래서 animation 처리가 빠르게 지나가게 됨

- 그래서 animation이 실행중일 때는 restarts를 막게끔 설정할 수 있음, 하지만 이는 이전의 rotate에서 썼던 것과 유사하므로 아래와 같이 메소드화를 진행함

```kotlin
private fun disableViewDuringAnimation(view: View,
                                       animator: ObjectAnimator) {
    animator.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator?) {
            view.isEnabled = false
        }

        override fun onAnimationEnd(animation: Animator?) {
            view.isEnabled = true
        }
    })
}
```

- 그래서 아래와 같이 변경함

```kotlin
private fun rotater() {
    val animator = ObjectAnimator.ofFloat(star, View.ROTATION,
                                          -360f, 0f)
    animator.duration = 1000
    disableViewDuringAnimation(rotateButton, animator)
    animator.start()
}

private fun translater() {
    val animator = ObjectAnimator.ofFloat(star, View.TRANSLATION_X,
                                          200f)
    animator.repeatCount = 1
    animator.repeatMode = ObjectAnimator.REVERSE
    disableViewDuringAnimation(translateButton, animator)
    animator.start()
}
```

- 여기서 더 나아가 이를 확장함수를 써서 `ObjectAnimator`의 일부로 아래와 같이 수정할 수 있음

```kotlin
private fun ObjectAnimator.disableViewDuringAnimation(view: View) {
    addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator?) {
            view.isEnabled = false
        }

        override fun onAnimationEnd(animation: Animator?) {
            view.isEnabled = true
        }
    })
}
```

- 그리고 각각 `translater`와 `rotate`  함수를 아래와 같이 수정할 수 있음

```kotlin
private fun translater() {
    val animator = ObjectAnimator.ofFloat(star, View.TRANSLATION_X,
                                          200f)
    animator.repeatCount = 1
    animator.repeatMode = ObjectAnimator.REVERSE
    animator.disableViewDuringAnimation(translateButton)
    animator.start()
}
```
```kotlin
private fun rotater() {
    val animator = ObjectAnimator.ofFloat(star, View.ROTATION,
                                          -360f, 0f)
    animator.duration = 1000
    animator.disableViewDuringAnimation(rotateButton)
    animator.start()
}
```

### Scaling the Star
- 이 `scaler`를 진행할 때는 두개의 요소를 병렬적으로 animating 할 것임

- 이 `scale`에선 x와 y를 동시에 진행함, 오직 한 개의 축에서만 움직이는 것을 막기위해서

- 그래서 2개의 properties를 써야함

- 이를 활용하는데 다양한 방법이 존재하는데 그 중 가장 나은 방식은 `PropertyValuesHolder`를 쓰는 것임, 이를 사용하면 두 개의 property 정보와 두 property 사이에서 animate할 값을 가지고 있게 하는 객체로써 쓸 수 있음

- 그래서 아래와 같이 `PropertyValuesHolder`로 2개의 값을 가지고 이를 `ObjectAnimator`에 하나로 넣어서 처리할 수 있음, animation 처리는 이전과 비슷하지만 여기서는 property의 값을 하나로 묶어서 처리하는 방식이 다름

```kotlin
val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 4f)
val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 4f)

val animator = ObjectAnimator.ofPropertyValuesHolder(
        star, scaleX, scaleY)
```

- 그리고 `scale`을 떠날 때 repeat이나 reverse를 설정하기 위해서 아래와 같이 값을 추가하고, disable 설정도 추가함

```kotlin
private fun scaler() {
    val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 4f)
    val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 4f)
    val animator = ObjectAnimator.ofPropertyValuesHolder(
            star, scaleX, scaleY)
    animator.repeatCount = 1
    animator.repeatMode = ObjectAnimator.REVERSE
    animator.disableViewDuringAnimation(scaleButton)
    animator.start()
}
```

### Fading the Star
- Fading 기법을 통해서 UI에서 사라지게 할 수 있음, 이는 우리가 하는 흔한 fader 기법을 생각할 수 있음

```kotlin
private fun fader() {
    val animator = ObjectAnimator.ofFloat(star, View.ALPHA, 0f)
    animator.repeatCount = 1
    animator.repeatMode = ObjectAnimator.REVERSE
    animator.disableViewDuringAnimation(fadeButton)
    animator.start()
}
```

### Colorizing
- `ObjectAnimator`의 장점은 무엇이든 animator가 접근 가능한 property라면 animate 할 수 있는 것임

- 만약 직접적으로 `Property`의 객체에 접근을 하지 못하고 `String`으로 이름으로 변수를 전달한다면 그 이름과 적절한 타겟 객체로 setter/getter로써 정보를 맵핑함

- 여기서 그렇게 하기 위해서 `int` 값으로 넘기고 아래와 같이 설정을 진행함

```kotlin
var animator = ObjectAnimator.ofInt(star.parent,
    "backgroundColor", Color.BLACK, Color.RED).start()
```

- 하지만 위와 같이 넘기게되면 그 사이의 값이 너무 많아서 그런 것임

- 차라리 여기서 해당 색에 대한 integer 값을 주는 것보다 이를 어떻게 해석할지 직접 제시해주는 것이 좋음

- 아래와 같이 쓸 수 있음, 이를 사용하면 알아서 적절하게 그에 해당하는 값으로 수렴을 할 수 있게됨

```kotlin
var animator = ObjectAnimator.ofArgb(star.parent,
    "backgroundColor", Color.BLACK, Color.RED).start()
```

- 그 외에 앞서 한 것과 마찬가지로 설정을 추가하여 시간을 늘리고 다시 검은색으로 돌아가게 처리하는 등 아래와 같이 함수를 추가할 수 있음

```kotlin
private fun colorizer() {
    var animator = ObjectAnimator.ofArgb(star.parent,
        "backgroundColor", Color.BLACK, Color.RED)
    animator.setDuration(500)
    animator.repeatCount = 1
    animator.repeatMode = ObjectAnimator.REVERSE
    animator.disableViewDuringAnimation(colorizeButton)
    animator.start()
}
```

### Star Shower
- 이제 앞선 animation과 다르게 다양한 properties와 다양한 객체의 animating을 할 수 있음

- 이 효과를 통해서 star의 크기가 랜덤하게 만들어지면서 background container에 담기면서 그 위에서부터 시작하게끔 비가 내리듯이 떨어지는데 이 때 회전도 추가해서 할 수 있음

- 우선 이를 위해서, 몇 가지 상태를 변수로 정의해서 가지고 있어야함

   - star field의 ViewGroup(현재 star view의 부모)

   - container의 width와 height

   - star의 기본 width와 height

- 그래서 아래와 같이 코드를 추가할 수 있음

```kotlin
val container = star.parent as ViewGroup
val containerW = container.width
val containerH = container.height
var starW: Float = star.width.toFloat()
var starH: Float = star.height.toFloat()
```

- 그리고 star grapic을 가지고 있는 `View`를 만들 수 있음(star는 `VectorDrawable`이라서 `AppCompatImageView`를 사용해야함)

```kotlin
val newStar = AppCompatImageView(this)
newStar.setImageResource(R.drawable.ic_star)
newStar.layoutParams = FrameLayout.LayoutParams(
                           FrameLayout.LayoutParams.WRAP_CONTENT,
                           FrameLayout.LayoutParams.WRAP_CONTENT)
container.addView(newStar)
```

- 이 때 container에서 image를 어디에 위치할 지 아직 정하지 않았기 때문에(디폴트값만 있음) 이를 아래와 같이 설정해줌

- 이 때 star의 크기를 랜덤하게 하고 디폴트 사이즈에서 1 ~ 1.6배가 되게끔 처리함

```kotlin
newStar.scaleX = Math.random().toFloat() * 1.5f + .1f
newStar.scaleY = newStar.scaleX
starW *= newStar.scaleX
starH *= newStar.scaleY
```

- 그리고 수평적으로 star를 위치하게 함, 이는 왼쪽 끝에서 오른쪽 끝까지 위치하게 할 수 있음

```kotlin
newStar.translationX = Math.random().toFloat() *
                       containerW - starW / 2
```

- 즉 위와 같이 일단 star의 초기 상태에서 설정은 끝난 것임, 이제 이 위치에서 수직으로 떨어지게 처리하는 것과 interploation을 설정해서 두 animation에 대해서 각각 다르게 랜덤하게 처리가 되도록 처리함, 그리고 떨어지면서 회전하는 설정도 추가함

```kotlin
val mover = ObjectAnimator.ofFloat(newStar, View.TRANSLATION_Y,
                                   -starH, containerH + starH)
mover.interpolator = AccelerateInterpolator(1f)
val rotator = ObjectAnimator.ofFloat(newStar, View.ROTATION,
        (Math.random() * 1080).toFloat())
rotator.interpolator = LinearInterpolator()
```

- 여기서 `mover`는 Y축 기준으로 떨어지게 설정이 되어 있음(수직적으로), 그리고 `AccelerateInterpolator`를 통해서 점점더 가속하는 모션으로 처리함

- `LinearInterpolator`를 통해서는 떨어질수록 rotate가 천천히 되게 함

- 그리고 앞서 설정한 mover와 rotate를 동시에 적용하기 위해서 `AnimatorSet`을 사용함, 이 `AnimatorSet`은 다른 `AnimatorSet`도 담을 수 있어서 더 복잡한 animation을 묶어서 할 수 있음

- 그래서 아래와 같이 두 개를 묶고 시간 설정을 랜덤으로 하고 끝나게 되면 사라지게끔 처리할 수 있음

```kotlin
val set = AnimatorSet()
set.playTogether(mover, rotator)
set.duration = (Math.random() * 1500 + 500).toLong()

set.addListener(object : AnimatorListenerAdapter() {
    override fun onAnimationEnd(animation: Animator?) {
        container.removeView(newStar)
    }
})
set.start()
```