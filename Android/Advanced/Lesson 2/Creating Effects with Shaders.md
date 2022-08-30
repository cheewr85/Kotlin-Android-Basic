- 컴퓨터 그래픽에서 shader는 shading을 위해 사용되었음 하지만 현재는 computer graphic의 special effect를 위한 다양한 필드에서 전문화된 함수로써 수행을 함

- Android에서는 `Shader`는 `Paint`객체가 그려야 할 색깔이나 texture를 정의함, 그리고 `Paint`가 사용하도록 다양한 `Shader`의 하위 클래스들을 정의함(`BitmapShader`, `ComposeShader`, `LinearGradient`, `RadialGradient`, 그리고 `SweepGradient`등)

- 예를 들어 `BitmapShader`를 `Paint`객체의 texture로 bitmap을 정의하기 위해서 사용할 수 있음

- 이는 custom theme를 적용할 수 있게하고, texture로써 bitmap을 custom view로 쓸 수 있음

- 그리고 효과적인 시각 표현을 위해 transition animation과 함께 mask를 쓸 수 있음

- 서로 다른 shape으로 image를 그리기 위해서 `Paint`객체를 위한 `BitmapShader`를 정의할 수 있고 둥근 모서리로 된 사각형을 그리기 위해 `drawRoundRect` 메소드를 사용할 수 있음

### Setting up
- string과 필요한 drawable 파일을 받아서 저장함

### Creating a custom ImageView
- 몇 개의 custom view와 helper variable이 필요함

- 해당 custom view의 역할은 아래와 같음

   - screen의 motion event의 반응을 함

   - game screen을 그림, user의 손가락의 현재 위치에 스포트라이트를 줌

   - 승리 조건에 만족하면 Android image를 보여줌

- 아래와 같이 custom view 클래스를 만듬

```kotlin
package com.example.findme

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class SpotLightImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
}
```

- 그런 다음 해당 클래스 안에 변수를 정의함

```kotlin
private var paint = Paint()
private var shouldDrawSpotLight = false
private var gameOver = false

private lateinit var winnerRect: RectF
private var androidBitmapX = 0f
private var androidBitmapY = 0f
```

- 그리고 앞서 받은 drawable 파일에 대한 초기화를 진행함

```kotlin
private val bitmapAndroid = BitmapFactory.decodeResource(
   resources,
   R.drawable.android
)
private val spotlight = BitmapFactory.decodeResource(resources, R.drawable.mask)
```

- 그리고 아래와 같이 xml에서 추가할 수 있음

```xml
<com.example.android.findme.SpotLightImageView
   android:id="@+id/spotLightImageView"
   android:layout_width="match_parent"
   android:layout_height="match_parent"
   app:layout_constraintBottom_toBottomOf="parent"
   app:layout_constraintEnd_toEndOf="parent"
   app:layout_constraintStart_toStartOf="parent"
   app:layout_constraintTop_toTopOf="parent" />
```

### Shaders
- `Shader`는 앞서 말했듯이 `Paint`객체의 texture를 정의함, 그래서 어떠한 객체든 `Paint`를 통해 그리게 된다면 shader로부터 색깔을 얻게됨, 해당 종류 형태는 아래에서 확인할 수 있음

![Shader 종류 & PorterDuff.Mode](https://developer.android.com/codelabs/advanced-android-kotlin-training-shaders?hl=ko#4)

- `PorterDuff.Mode` 클래스는 몇몇의 Alpha compositing과 blending mode를 제공함

- Alpha compositing은 목표한 이미지에 대해서 부분적이나 완전히 투명하게 나타나게끔 compositing하는 과정이고 투명화는 alpha channel에 의해 정의됨

- alpha channel은 색깔의 투명성 정도를 나타냄 즉, red-green-blue channel에 대해

- blending mode에 대해서는 공식문서를 참고하면 됨

- `PorterDuff.Mode`에 대해서도 위의 링크를 참조해서 예시를 볼 수 있음

### Creating the BitmapShader
- shader를 통해 mask bitmap에서 사용할 texture를 만들 수 있음, 아래와 같은 과정을 겪음

   - spotlight bitmap으로 같은 사이즈의 bitmap을 만듬

   - bitmap에 그리기 위한 bitmap을 canvas에 만듬(배경색은 검은색)

   - `BitmapShader`를 통해 texture를 만듬

   - `PorterDuff.Mode`를 통해서 이전에 만든 mask bitmap과 새로운 bitmap을 합쳐서 spotlight를 만듬

   - 전체 화면을 texture로 만듬, 만들어진 texture bitmap은 스크린보다 작음 그래서 CLAMP TileMode를 사용해서 spotlight를 한 번 그리고 나머지를 모두 검은색으로 만듬

![one](/Android/img/fiftythree.png)

- destination bitmap을 customview에 `init`에서부터 만듬, 그리고 `Canvas`객체를 만들고 `Paint`객체에 초기화함

```kotlin
init {
        val bitmap = Bitmap.createBitmap(spotlight.width, spotlight.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val shaderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    }
```

- 그 다음, bitmap texture와 bitmap black을 만듬, 이후 나중에 spotlight effect를 앞서 받은 이미지를 활용해서 쓸 것임

```kotlin
// Draw a black rectangle.
shaderPaint.color = Color.BLACK
canvas.drawRect(0.0f, 0.0f, spotlight.width.toFloat(), spotlight.height.toFloat(), shaderPaint)
```

- 그러면 아무것도 없는 검은 화면이 나옴

- 그리고 spotlight에 대해서 `init` 안에서 `DST_OUT`을 통해서 적용을 함

```kotlin
shaderPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
canvas.drawBitmap(spotlight, 0.0f, 0.0f, shaderPaint)
```

- 그 다음, `Shader`에서 정의한 `TileMode`를 사용하여 bitmap drawable이 어떻게 반복되고 X,Y 방향에 따라 미러링되는지 정의할 수 있음(만약 bitmap drawable에 쓰는 texture가 screen보다 작다면)

- Tilemode는 아래와 같이 나눠서 볼 수 있음

![one](/Android/img/fiftyfour.png)

- 이를 적용하기 위해서 `Shader` 타입을 정의하고 `init`에서 초기화를 함

```kotlin
private var shader: Shader
```
```kotlin
shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
```

- 그러면 spotlight만 보이고 나머지는 검은 배경으로 남게됨, 그리고 `shader`에 대해서 아래와 같이 `init`에 추가함

```kotlin
init {
   val bitmap = Bitmap.createBitmap(spotlight.width, spotlight.height, Bitmap.Config.ARGB_8888)
   val canvas = Canvas(bitmap)
   val shaderPaint = Paint(Paint.ANTI_ALIAS_FLAG)

   // Draw a black rectangle.
   shaderPaint.color = Color.BLACK
   canvas.drawRect(0.0f, 0.0f, spotlight.width.toFloat(), spotlight.height.toFloat(), shaderPaint)

   // Use the DST_OUT compositing mode to mask out the spotlight from the black rectangle.
   shaderPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
   canvas.drawBitmap(spotlight, 0.0f, 0.0f, shaderPaint)

   shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
   paint.shader = shader
}
```

### Calculate a random location for the Android Image
- 앞서 추가한 Android Image bitmap에 대해서 random 위치를 계산하여 보여줄 수 있음, 아래와 같이 해당 좌표에 대해서 랜덤으로 생성을 해서 설정을 하면 됨

```kotlin
private fun setupWinnerRect() {
   androidBitmapX = floor(Random.nextFloat() * (width - bitmapAndroid.width))
   androidBitmapY = floor(Random.nextFloat() * (height - bitmapAndroid.height))

winnerRect = RectF(
   (androidBitmapX),
   (androidBitmapY),
   (androidBitmapX + bitmapAndroid.width),
   (androidBitmapY + bitmapAndroid.height)
)
}
```

### Use the BitmapShader
- 앞서 랜덤으로 생성한 이미지에 대해서 적용하기 위해서 `onSizeChanged`와 `onDraw`를 오버라이딩 할 것임

- 먼저 `onSizeChanged`를 오버라이딩 해서 앞서 만든 랜덤 위치에 대한 함수를 추가함

```kotlin
override fun onSizeChanged(
       newWidth: Int,
       newHeight: Int,
       oldWidth: Int,
       oldHeight: Int
) {
   super.onSizeChanged(newWidth, newHeight, oldWidth, oldHeight)
   setupWinnerRect()
}
```

- 그리고 이를 `onDraw`를 통해서 만든 부분을 바탕으로 추가하게 처리함

```kotlin
override fun onDraw(canvas: Canvas) {
   super.onDraw(canvas)
   canvas.drawColor(Color.WHITE)
   canvas.drawBitmap(bitmapAndroid, androidBitmapX, androidBitmapY, paint)
}
```

### Responding to motion events
- 이를 게임으로 활용하기 위해서 screen에서의 user의 모션의 응답을 하고 탐색해야함, 그래서 유저의 터치에 응답하여 spotlight가 유저의 터치를 따라가도록 spotlight shader matrix를 변환해야함

- `onTouchEvent`를 오버라이딩하여 유저 터치의 좌표를 저장하고 true로 바꿈(motion event를 쓸 것이므로)

```kotlin
override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
   val motionEventX = motionEvent.x
   val motionEventY = motionEvent.y
   return true
 }
```

- 여기서 return 이전의 `motionEvent.action`에 대한 값을 `when` 구문으로 구현하고 해당 케이스에 맞춰서 조건을 아래와 같이 처리함, 만약 터치를 땠을 때 숨겨진 것을 못 찾았으면 게임을 초기화하고 터치 중에 숨겨진 것을 찾았는지 해당 좌표를 체크를 함

```kotlin
when (motionEvent.action) {
   MotionEvent.ACTION_DOWN -> {
       shouldDrawSpotLight = true
       if (gameOver) {
           gameOver = false
           setupWinnerRect()
       }
   }
   MotionEvent.ACTION_UP -> {
       shouldDrawSpotLight = false
       gameOver = winnerRect.contains(motionEventX, motionEventY)
   }
}
```

- 유저가 스크린에 spotlight를 터치할 때, spotlight를 그려야 할 곳을 계산하는 대신에, shader matrix를 움직이는게 나음 즉 texture/shader 좌표 시스템을 해석된 좌표에서 동일한 곳에 spotlight texture를 그리는 것임

- 이 결과로 마치 spotlight texture를 다른 곳에서 그린 거처럼 보임, 이는 shader matrix가 변환한 장소와 같음

- 이와 관련된 코드를 아래와 같이 추가함

```kotlin
private val shaderMatrix = Matrix()
```

- 그리고 touchEvent에 아래를 return문 이전에 추가하고 `shaderMatrix`를 설정함

```kotlin
shaderMatrix.setTranslate(
   motionEventX - spotlight.width / 2.0f,
   motionEventY - spotlight.height / 2.0f
)
```
```kotlin
shader.setLocalMatrix(shaderMatrix)
```

- 그 다음 다시 그리기 위해서 `invalidate`를 호출함, 그러면 아래와 같이 구성이 됨

```kotlin
override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
   val motionEventX = motionEvent.x
   val motionEventY = motionEvent.y

   when (motionEvent.action) {
       MotionEvent.ACTION_DOWN -> {
           shouldDrawSpotLight = true
           if (gameOver) {
               // New Game
               gameOver = false
               setupWinnerRect()
           }
       }
       MotionEvent.ACTION_UP -> {
           shouldDrawSpotLight = false
           gameOver = winnerRect.contains(motionEventX, motionEventY)
       }
   }
   shaderMatrix.setTranslate(
       motionEventX - spotlight.width / 2.0f,
       motionEventY - spotlight.height / 2.0f
   )
   shader.setLocalMatrix(shaderMatrix)
   invalidate()
   return true
}
```

### Use the BitmapShader
- 이제 `BitmapShader`를 통해서 검은 화면과 spotlight를 합쳐서 표현할 수 있음, `onDraw`에 아래와 같이 코드르 추가할 수 있음

```kotlin
override fun onDraw(canvas: Canvas) {
   super.onDraw(canvas)
   canvas.drawColor(Color.WHITE)
   canvas.drawBitmap(bitmapAndroid, androidBitmapX, androidBitmapY, paint)

   if (!gameOver) {
       if (shouldDrawSpotLight) {
           canvas.drawRect(0.0f, 0.0f, width.toFloat(), height.toFloat(), paint)
       } else {
           canvas.drawColor(Color.BLACK)
       }
   }
}
```

### Add an instructions dialog
- MainActivity에 서 이와 관련한 `AlertDialog`를 만드는 함수를 아래와 같이 추가해서 보여줄 수 있음 ,이는 게임 설명에 해당함

```kotlin
private fun createInstructionsDialog(): Dialog {
   val builder = AlertDialog.Builder(this)
   builder.setIcon(R.drawable.android)
           .setTitle(R.string.instructions_title)
           .setMessage(R.string.instructions)
           .setPositiveButtonIcon(ContextCompat.getDrawable(this, android.R.drawable.ic_media_play))
   return builder.create()
}
```

- 그리고 `onCreate`에 아래를 추가함

```kotlin
val dialog = createInstructionsDialog()
dialog.show()
```

### Draw the BitmapShader texture
- 앞서 한 방식과 다르게 다른 배경과 texture를 활용함, 아래와 같이 `onDraw`를 추가해서 처리함

```kotlin
override fun onDraw(canvas: Canvas) {
   super.onDraw(canvas)

   // Color the background yellow.
   canvas.drawColor(Color.YELLOW)
   canvas.drawRect(0.0f, 0.0f,spotlight.width.toFloat(), spotlight.height.toFloat(), paint)
}
```

- 그리고 다른 tiling mode를 쓸 수 있음, 아래와 같이 보여짐

![one](/Android/img/fiftyfive.png)

