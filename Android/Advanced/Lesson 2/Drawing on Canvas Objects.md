- drawable을 사용하기 위해서 `Canvas` 클래스에서의 drawing methods를 사용해서 2D drawing을 만들 수 있음

- `Canvas`는 drawing을 위한 메소드를 제공하는 2D drawing surface임, 이는 앱이 그 자체로 주기적으로 redraw를 할 때 유용함 왜냐하면 유저가 변화를 계속 봐야하기 때문에

- `View`에 나타난 canvas에서 어떻게 그리거나 만들지 알 수 있음

- Canvas는 아래와 같은 동작을 할 수 있음

   - canvas 전체를 색깔로 채울 수 있음

   - `Paint` 객체가 스타일링한 도형들을 그릴 수 있음, `Paint`객체는 어떻게 기하학적으로 그릴지에 대한 style과 color 정보를 가지고 있음

   - translation, scaling, custom transformation 같은 변화를 적용시킬 수 있음

   - 보이는 부분을 정의하는 canvas의 clip을 적용할 수 있음

![one](/Android/img/fourtynine.png)

- 실제로 이렇게 View에 직접 그린다는 것은 상당히 복잡한 과정인데, 이를 순서대로 정리하면 아래와 같이 볼 수 있음

![one](/Android/img/fifty.png)

- 1.먼저 그린 것을 나타내는 View가 필요함 앞서 본 것처럼 Custom View도 가능함

- 2.이 모든 View들은 그들만의 canvas가 존재함

- 3.view의 canvas의 그리는 가장 흔한 방식은 `onDraw`를 오버라이딩하고 canvas의 draw하는 것임

- 4.drawing을 만들 때, 이전에 그린 것에 대한 cache가 필요함, data를 caching하는 다양한 방식이 있는데, 하나는 bitmap이고 다른 하나는 그린 좌표와 그린 것에 대한 기록을 저장하는 것임

- 5.caching bitmap을 그리기 위해서 canvas drawing API를 사용함, 이 때 caching bitmap을 위한 caching canvas를 사용할 수 있음

- 6.그 때 caching canvas로 그릴 수 있음(이는 caching bitmap에서 그린것과 같음)

- 7.screen의 그린 모든것을 나타내기 위해, caching bitmap을 그리기 위해 view의 canvas에 알려줘야함

### Create a custom view for drawing
- 먼저 색깔을 추가하고 Theme를 NoActionBar로 바꿈

- 그리고 drawing을 위한 custom view를 아래와 같이 만들 수 있음, 이 역시 View를 상속받아서 처리함

```kotlin
import android.content.Context
import android.view.View

class MyCanvasView(context: Context) : View(context) {
}
```

- 그 다음 view의 content 설명을 추가하고 Activity의 `onCreate`에서 `setContentView`를 없애고 `MyCanvasView`의 인스턴스를 만듬

```kotlin
val myCanvasView = MyCanvasView(this)
```

- 그리고 하단에 full screen layout에 대한 요청과 content description을 추가하고 content view를 setting함

```kotlin
package com.example.minipaint

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myCanvasView = MyCanvasView(this)
        myCanvasView.systemUiVisibility = SYSTEM_UI_FLAG_FULLSCREEN
        myCanvasView.contentDescription = getString(R.string.canvasContentDescription)
        setContentView(myCanvasView)
    }
}
```

### Display the Canvas
- `onSizeChanged`메소드는 view가 size를 바꾸면 어디에서든 호출되서 적용됨, 이는 처음에 view가 만들어질 땐 size가 없기 때문에 해당 메소드를 호출해서 적용이 됨 즉, Activity가 처음 만들어지고 inflate 될 때도 호출이 되는 것임

- 그래서 이 메소드는 view's canvas를 세팅하고 만드는데 최적의 장소임

- canvas와 bitmap 정의를 상단에 정의함, 이는 이전에 그린 사항을 저장하는 caching 역할로써 bitmap과 canvas를 선언한 것임

```kotlin
private lateinit var extraCanvas: Canvas
private lateinit var extraBitmap: Bitmap
```

- 그리고 canvas의 배경색 역시 정의하고 초기화함

```kotlin
private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)
```

- 그런 다음 `onSizeChanged`를 오버라이딩함, 이 메소드는 새로운 width와 height, 이전 width와 height를 모두 가지고 있음

```kotlin
override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
   super.onSizeChanged(width, height, oldWidth, oldHeight)
}
```

- 그리고 해당 메소드 안에서 `Bitmap`의 인스턴스를 새로운 width와 height로 생성함, 그리고 이를 할당해줌(색깔 역시 설정해줌)

```kotlin
extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
```

- 그리고 `Canvas` 인스턴스도 만들고 배경색도 정의함

```kotlin
extraCanvas = Canvas(extraBitmap)
extraCanvas.drawColor(backgroundColor)
```

- 이제 새로운 bitmap과 canvas가 생성될 때마다 `onSizeChanged`안에 쓴 기능들이 실행됨, 새로운 bitmap이 필요한 것은 size가 변하기 때문임

- 하지만 이는 이전 bitmap이 남아있다면 메모리가 누수됨, 그래서 이를 고치기 위해서 `super` 밑의 아래에 코드를 추가함

```kotlin
if (::extraBitmap.isInitialized) extraBitmap.recycle()
```

- 위에서 정의한 MyCanvasView는 모두 `onDraw`에서 일어남 해당 메소드를 오버라이딩해서 아래와 같이 할당을 해 줌, 그러면 설정한 바와 같이 처리가 됨

```kotlin
override fun onDraw(canvas: Canvas) {
   super.onDraw(canvas)
canvas.drawBitmap(extraBitmap, 0f, 0f, null)
}
```

### Set up a Paint and a Path for Drawing
- 그리기 위해서는 그렸을 때 무엇을 스타일 해서 특정 지었는지 `Paint` 객체가 필요하고 그린 것에 대한 세부적인 `Path`가 필요함

- stroke width를 먼저 정의하고 `drawColor`에 대해서도 아래와 같이 정의함

```kotlin
private const val STROKE_WIDTH = 12f // has to be float
private val drawColor = ResourcesCompat.getColor(resources, R.color.colorPaint, null)
```

- 그리고 `Paint` 객체를 정의함

```kotlin
// Set up the paint with which to draw.
private val paint = Paint().apply {
   color = drawColor
   // Smooths out edges of what is drawn without affecting shape.
   isAntiAlias = true
   // Dithering affects how colors with higher-precision than the device are down-sampled.
   isDither = true
   style = Paint.Style.STROKE // default: FILL
   strokeJoin = Paint.Join.ROUND // default: MITER
   strokeCap = Paint.Cap.ROUND // default: BUTT
   strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
}
```

- `color`는 앞서 정의한 `Color`를 씀

- `isAntiAlias`는 이를 통해 그릴 때 형태의 영향을 미치지 않게 모서리를 부드럽게 해둠

- `isDither`는 기기가 down-sampled할 때보다 색깔에 high-precision하게 어떻게 영향을 끼치는지 작용함

- `style`은 선이 필수적인 한 획을 완료했을 때의 painting type을 설정함, 기본값은 paint가 적용될 때 fill하는 설정임

- `strokeJoin`은 그어진 경로에 line이나 curve가 어떻게 특정되는지 정해줌

- `strokeCap`은 cap이 되는데 라인의 끝의 형태를 정해줌, 시작과 끝에 대해서 어떻게 되는지 특정해줌

- `strokeWidth`는 pixel에서의 stroke의 width를 특정함, 이를 조정해서 두껍게도 가능함

- `Path`는 유저가 그린 경로를 의미함, 이를 아래와 같이 정의가능함

```kotlin
private var path = Path()
```

### Draw following the user's touch
- `onTouchEvent`메소드는 View에서 user가 터치할 때마다 호출이 됨

- 그래서 아래와 같이 해당 메소드를 오버라이딩하고 `event`에서 통과되는 `x`와 `y`의 좌표를 캐싱할 수 있음, 그리고 `when`문을 통해서 각 이벤트 처리를 할 수 있음

```kotlin
override fun onTouchEvent(event: MotionEvent): Boolean {
   motionTouchEventX = event.x
   motionTouchEventY = event.y

   when (event.action) {
       MotionEvent.ACTION_DOWN -> touchStart()
       MotionEvent.ACTION_MOVE -> touchMove()
       MotionEvent.ACTION_UP -> touchUp()
   }
   return true
}
```

- 그리고 캐싱을 위한 x, yw좌표에 대한 변수를 추가함

```kotlin
private var motionTouchEventX = 0f
private var motionTouchEventY = 0f
```

- 그리고 아래와 같이 메소드를 추가함

```kotlin
private fun touchStart() {}

private fun touchMove() {}

private fun touchUp() {}
```

- `touchStart`의 경우 최근의 x와 y의 좌표를 캐싱으로 저장함, 이는 유저가 그들의 터치를 멈출 때 해당 다음 path에 대한 시작점이 됨

```kotlin
private var currentX = 0f
private var currentY = 0f
```

- 그리고 이를 아래와 같이 해당 메소드를 채워줌, path를 초기화하고 터치이벤트에 움직인 path에 대해서 현재 x,y좌표로써 할당해줌

```kotlin
private fun touchStart() {
   path.reset()
   path.moveTo(motionTouchEventX, motionTouchEventY)
   currentX = motionTouchEventX
   currentY = motionTouchEventY
}
```

- path를 쓰면 보여줄 때 새로고침이 되면 매번 매 pixel을 그릴 필요가 없음 대신에, point 사이에 path를 추가해 넣음으로써 효율을 늘릴 수 있음

  - 만약 손가락이 거의 움직이지 않으면 그릴 필요가 없음

  - 만약 `touchTolerance`거리보다 덜 움직이면 그릴 필요가 없음

  - `scaledTouchSlop`은 유저가 스크롤을 하려고 하기전에 터치가 배회하고 있을 때 픽셀안에서의 거리를 리턴함

- 아래와 같이 `touchMove` 메소드를 정의가능함, 그린 `dx, dy` 값을 계산하고, 두 점 사이의 curve와 `path`를 저장함, 그리고 현재 진행중인 `currentX`와 `currentY`를 갱신하고 `path`를 그림(그때 `invalidate`가 호출해야함)

   - 1.`dx, dy`로 움직인 거리를 계산함

   - 2.만약 거리가 touch tolerance보다 더 길면 path의 segment를 추가함

   - 3.다음 segment를 위한 시작 포인트와 현재 segment에 대한 endpoint를 설정함

   - 4.`lineTo` 대신 `quadTo`를 사용하여 corner 없이 선을 부드럽게 그림

   - 5.`invalidate`를 호출해서 view를 다시 그림

```kotlin
private fun touchMove() {
   val dx = Math.abs(motionTouchEventX - currentX)
   val dy = Math.abs(motionTouchEventY - currentY)
   if (dx >= touchTolerance || dy >= touchTolerance) {
       // QuadTo() adds a quadratic bezier from the last point,
       // approaching control point (x1,y1), and ending at (x2,y2).
       path.quadTo(currentX, currentY, (motionTouchEventX + currentX) / 2, (motionTouchEventY + currentY) / 2)
       currentX = motionTouchEventX
       currentY = motionTouchEventY
       // Draw the path in the extra bitmap to cache it.
       extraCanvas.drawPath(path, paint)
   }
   invalidate()
}
```

- 유저가 touch를 끝내면 path를 초기화하고 그리는 것을 다시 함

```kotlin
private fun touchUp() {
   // Reset the path so it doesn't get drawn again.
   path.reset()
}
```
- 앞서 한 작업을 바탕으로 캐싱으로 정리하고 이를 그림, 여기서 `onDraw`에서 더 그릴 수 있음

- 예를 들어 bitmap을 그린 후 shape을 추가로 그릴 수 있음

- 아래의 코드에선 pictuer의 edge 주변의 frame을 그리는 것임

```kotlin
private lateinit var frame: Rect
```

- 그리고 `onSizeChanged`에 아래를 추가함

```kotlin
// Calculate a rectangular frame around the picture.
val inset = 40
frame = Rect(inset, inset, width - inset, height - inset)
```

- 끝으로 `onDraw`에 아래를 추가함

```kotlin
// Draw a frame around the canvas.
canvas.drawRect(frame, paint)
```

### Storing data in a Path
- 앞서 한 작업은 그린 정보가 bitmap에 있지만, 항상 이것이 최선의 방법이진 않음, 여기서 이 데이터를 `Path`에도 저장할 수 있음

- 아래와 같이 Path를 추가함
```kotlin
// Path representing the drawing so far
private val drawing = Path()

// Path representing what's currently being drawn
private val curPath = Path()
```

- 그리고 bitmap을 그리는 대신 `onDraw`에 path를 그릴 수 있음

```kotlin
// Draw the drawing so far
canvas.drawPath(drawing, paint)
// Draw any current squiggle
canvas.drawPath(curPath, paint)
// Draw a frame around the canvas
canvas.drawRect(frame, paint)
```

- 마지막으로 `touchUp`에 아래를 추가함

```kotlin
// Add the current path to the drawing so far
drawing.addPath(curPath)
// Rewind the current path for the next touch
curPath.reset()
```

- 그러면 Path로 그린 것뿐 Bitmap과 큰 차이는 없음

