- Clipping은 image, canvas, bitmap을 스크린에 그릴지 그리지 말지 regions을 정의하는 방법임

- 이 Clipping을 하는 목적은 overdraw를 줄이기 위해서임, 이 overdraw는 screen의 pixel에서 마지막 이미지를 보여주기 위해서 한 번 이상 그리는 것을 의미함

- 이 overdraw를 줄이기 위해서 그려지는 display의 region 혹은 pixel의 수를 최소화해야함, 그렇게 해야 drawing performance를 최대화시킬 수 있음

- 이 clipping을 통해서, UI design이나 animation에 흥미로운 효과를 만들 수 있음

- 마치 카드를 겹쳐서 쌓아둔 것을 그리기 위해서 이 겹쳐진 카드를 모두 그릴 필요없이, 보여지는 일부분만 그리게끔 하는 것 역시 Clipping을 쓰는 것임

- 이러한 것을 쓰면 마치 캡쳐 기능을 하듯이 일부분만을 그리게끔 Clipping을 하고 보여줄 수 있음

### Project and shapes setup
- 먼저 이것을 적용하기 위해서 default content를 바꾸고 새로운 custom view를 만듬

```kotlin
setContentView(ClippedView(this))
```
```kotlin
class ClippedView @JvmOverloads constructor(
   context: Context,
   attrs: AttributeSet? = null,
   defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
}
```

- 그리고 dimensions과 string 요소를 추가함

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
   <dimen name="clipRectRight">90dp</dimen>
   <dimen name="clipRectBottom">90dp</dimen>
   <dimen name="clipRectTop">0dp</dimen>
   <dimen name="clipRectLeft">0dp</dimen>

   <dimen name="rectInset">8dp</dimen>
   <dimen name="smallRectOffset">40dp</dimen>

   <dimen name="circleRadius">30dp</dimen>
   <dimen name="textOffset">20dp</dimen>
   <dimen name="strokeWidth">4dp</dimen>

   <dimen name="textSize">18sp</dimen>
</resources>
```
```xml
<string name="clipping">Clipping</string>
<string name="translated">translated text</string>
<string name="skewed">"Skewed and "</string>
```

- 여기서 추가로 Smallest screen width에 해당하는 부분을 추가해서 넣음

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
   <dimen name="clipRectRight">120dp</dimen>
   <dimen name="clipRectBottom">120dp</dimen>

   <dimen name="rectInset">10dp</dimen>
   <dimen name="smallRectOffset">50dp</dimen>

   <dimen name="circleRadius">40dp</dimen>
   <dimen name="textOffset">25dp</dimen>
   <dimen name="strokeWidth">6dp</dimen>
</resources>
```

- 그리고 그리기 위해서 `Paint` 변수를 추가해서 설정하고 `Path` 변수를 만들고 초기화함

```kotlin
private val paint = Paint().apply {
   // Smooth out edges of what is drawn without affecting shape.
   isAntiAlias = true
   strokeWidth = resources.getDimension(R.dimen.strokeWidth)
   textSize = resources.getDimension(R.dimen.textSize)
}
```
```kotlin
private val path = Path()
```

- 여기서 모양에 대해서 앞서 dimensions에서 정의한대로 처리할 것임

- 전체 모형 주변의 clipping rectangle을 위해 dimension에서 정의한 값을 변수로 선언함

```kotlin
private val clipRectRight = resources.getDimension(R.dimen.clipRectRight)
private val clipRectBottom = resources.getDimension(R.dimen.clipRectBottom)
private val clipRectTop = resources.getDimension(R.dimen.clipRectTop)
private val clipRectLeft = resources.getDimension(R.dimen.clipRectLeft)
```

- 그리고 small rectangle의 inset과 offset을 선언함, 또 circle의 radius를 정의하고 drawn inside rectangle에서의 offset과 size를 선언함

```kotlin
private val rectInset = resources.getDimension(R.dimen.rectInset)
private val smallRectOffset = resources.getDimension(R.dimen.smallRectOffset)

private val circleRadius = resources.getDimension(R.dimen.circleRadius)

private val textOffset = resources.getDimension(R.dimen.textOffset)
private val textSize = resources.getDimension(R.dimen.textSize)
```

- 그런 다음 행과 열에 대한 정의 역시 추가함

```kotlin
private val columnOne = rectInset
private val columnTwo = columnOne + rectInset + clipRectRight

private val rowOne = rectInset
private val rowTwo = rowOne + rectInset + clipRectBottom
private val rowThree = rowTwo + rectInset + clipRectBottom
private val rowFour = rowThree + rectInset + clipRectBottom
private val textRow = rowFour + (1.5f * clipRectBottom)
```

### Understanding the drawing algorithm
- 다양한 형태를 그리는 부분은 `Canvas`의 원본에서 움직이면서 그리게 됨, 개념적으로 아래와 같음

![one](/Android/img/fiftyone.png)

- 먼저, rectangle을 어디에 그릴지 `Canvas`가 확인함, 즉 다음 rectangle과 다른 형태가 어디에 있어야 할 지 계산을 하는게 아니라, 원본의 `Canvas`를 움직이는 것임, 좌표시스템처럼

- 그때 canvas의 새로운 원본의 rectangle을 그림, 즉 해석된 좌표에서 똑같이 모형을 그리는 것임 이런 것이 더 효율적임

- 최종적으로 원본으로부터 `Canvas`를 복원함

- 적용할 알고리즘을 보면 아래와 같음

   - `onDraw`에서 `Canvas`를 회색 배경과 원본 shape을 그려 채울 함수를 호출함

   - 그려야할 clipped rectangle 각각의 함수를 호출함

- 각각의 rectangle이나 text는

   - 초기 상태를 reset할 수 있게 `Canvas`의 현재 상태를 저장함

   - canvas의 `Origin`의 그리고 싶은 곳의 위치를 받아옴

   - clipping shape이나 path를 적용함

   - rectangle이나 text를 그림

   - `Canvas`의 상태를 복원함

- 아래와 같이 `onDraw`를 오버라이딩하고 각각 형태를 그릴 함수를 호출을 함

```kotlin
 override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackAndUnclippedRectangle(canvas)
        drawDifferenceClippingExample(canvas)
        drawCircularClippingExample(canvas)
        drawIntersectionClippingExample(canvas)
        drawCombinedClippingExample(canvas)
        drawRoundedRectangleClippingExample(canvas)
        drawOutsideClippingExample(canvas)
        drawSkewedTextExample(canvas)
        drawTranslatedTextExample(canvas)
        // drawQuickRejectExample(canvas)
    }
```
```kotlin
private fun drawBackAndUnclippedRectangle(canvas: Canvas){
}
private fun drawDifferenceClippingExample(canvas: Canvas){
}
private fun drawCircularClippingExample(canvas: Canvas){
}
private fun drawIntersectionClippingExample(canvas: Canvas){
}
private fun drawCombinedClippingExample(canvas: Canvas){
}
private fun drawRoundedRectangleClippingExample(canvas: Canvas){
}
private fun drawOutsideClippingExample(canvas: Canvas){
}
private fun drawTranslatedTextExample(canvas: Canvas){
}
private fun drawSkewedTextExample(canvas: Canvas){
}
private fun drawQuickRejectExample(canvas: Canvas){
}
```

### Create a method to draw the shapes
- 처음에는 clipping 없는 것을 그릴 것인데 그러기 위해서 해당 메소드를 아래와 같이 선언해서 처리함

```kotlin
private fun drawClippedRectangle(canvas: Canvas) {
        canvas.clipRect(
            clipRectLeft,clipRectTop,
            clipRectRight,clipRectBottom
        )
    }
```

- `Canvas.clipRect` 메소드의 경우 미래에 그려질 부분에 대해서 쓸 수 있도록 screen의 region을 줄일 수 있음, 그리고 이 clipping boundaries 역시 설정할 수 있음

- 그리고 색깔 설정을 추가함, clipping을 하고 있기 때문에, clipping rectangle이 채워져 정의된 구역만 있는 것이기 때문에 이를 하얀색으로 처리함 나머지 전체 배경은 여전히 회색임

```kotlin
canvas.drawColor(Color.WHITE)
```

- 그리고 clipping rectangle 안에서 line과 circle을 추가로 그림

```kotlin
paint.color = Color.RED
canvas.drawLine(
   clipRectLeft,clipRectTop,
   clipRectRight,clipRectBottom,paint
)

paint.color = Color.GREEN
canvas.drawCircle(
   circleRadius,clipRectBottom - circleRadius,
   circleRadius,paint
)
```

- 그리고 text 위치와 색깔을 처리해줌

```kotlin
paint.color = Color.BLUE
// Align the RIGHT side of the text with the origin.
paint.textSize = textSize
paint.textAlign = Paint.Align.RIGHT
canvas.drawText(
   context.getString(R.string.clipping),
   clipRectRight,textOffset,paint
)
```

- 그 다음, 처음 unclipped rectangle에 대해서 그릴 수 있게 아래와 같이 해당 메소드를 씀, `canvas`를 저장하고 처음 row와 column의 위치를 저장하고 앞서 정의한 메소드를 호출하고 이전 `canvas`상태를 복원함

```kotlin
private fun drawBackAndUnclippedRectangle(canvas: Canvas){
   canvas.drawColor(Color.GRAY)
   canvas.save()
   canvas.translate(columnOne,rowOne)
   drawClippedRectangle(canvas)
   canvas.restore()
}
```

### Implement the clipping methods
- clipping을 하는데 있어서 다양한 효과를 적용하고 다양한 모양을 묶어서 처리할 수 있음

- 각각의 메소드는 동일한 패턴을 가지고 있음

  - 1.canvas의 현재 상태를 저장함

- activity context는 drawing state의 stack을 유지함, drawing state는 현재 transformation matrix와 clipping region로 구성되어 있음

- 현재 상태를 저장하거나 drawing state를 변하게 action을 수행할 수 있음, 그리고 저장된 drawing state를 복원할 수 있음

- 만약 drawing이 transformations, chaining과 error가 생겨서 되돌려서 transformation을 취소하는 등이 포함되어 있을 수 있음. 예를 들어 translate, stretch, 그리고 rotate하는 것은 꽤 복잡함, 대신 canvas의 상태를 저장하고, transformations, draw와 이전 상태를 저장하는 것을 적용할 수 있음

- 예를 들어 clipping region을 정의하거나 state를 저장할 수 있음, 그 때 canvas는 이를 해석하여 clipping region을 추가하고 rotate함

- 몇 개의 drawing이후, 원래의 clipping state를 복원할 수 있고 아래의 그림처럼 아예 다른 변환과 skew transformation을 진행할 수도 있음

![one](/Android/img/fiftytwo.png)


  - 2.row/column 좌표로 canvas의 원본을 해석함(위의 1번과 이어짐)

- 모든 요소를 그리기 위해 움직이는 것보다 똑같은 것을 새로운 좌표에 그리는 것과 원본 canvas를 옮기는 것이 더 쉬운 방식임

  - 3.`path`에 transformation을 적용함

  - 4.`canvas.clipPath(path)`로 clipping을 적용함

  - 5.`drawClippedRectangle() or drawText`로 shape을 그림

  - 6.`canvas.restore()`을 통해 이전의 canvas state를 복원함

- 또 다른 clipping을 아래와 같이 추가할 것임 이 방식은 앞서 설명한 방식과 유사하지만 적용하는 세부 값이 조금 다름

```kotlin
private fun drawDifferenceClippingExample(canvas: Canvas) {
   canvas.save()
   // Move the origin to the right for the next rectangle.
   canvas.translate(columnTwo,rowOne)
   // Use the subtraction of two clipping rectangles to create a frame.
   canvas.clipRect(
       2 * rectInset,2 * rectInset,
       clipRectRight - 2 * rectInset,
       clipRectBottom - 2 * rectInset
   )
   // The method clipRect(float, float, float, float, Region.Op
   // .DIFFERENCE) was deprecated in API level 26. The recommended
   // alternative method is clipOutRect(float, float, float, float),
   // which is currently available in API level 26 and higher.
   if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
       canvas.clipRect(
           4 * rectInset,4 * rectInset,
           clipRectRight - 4 * rectInset,
           clipRectBottom - 4 * rectInset,
            Region.Op.DIFFERENCE
       )
   } else {
       canvas.clipOutRect(
           4 * rectInset,4 * rectInset,
           clipRectRight - 4 * rectInset,
           clipRectBottom - 4 * rectInset
       )
   }
   drawClippedRectangle(canvas)
   canvas.restore()
}
```

- 이와 유사한 방식으로 다른 메소드 역시 채워줌

```kotlin
private fun drawCircularClippingExample(canvas: Canvas) {

   canvas.save()
   canvas.translate(columnOne, rowTwo)
   // Clears any lines and curves from the path but unlike reset(),
   // keeps the internal data structure for faster reuse.
   path.rewind()
   path.addCircle(
       circleRadius,clipRectBottom - circleRadius,
       circleRadius,Path.Direction.CCW
   )
   // The method clipPath(path, Region.Op.DIFFERENCE) was deprecated in
   // API level 26. The recommended alternative method is
   // clipOutPath(Path), which is currently available in
   // API level 26 and higher.
   if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
       canvas.clipPath(path, Region.Op.DIFFERENCE)
   } else {
       canvas.clipOutPath(path)
   }
   drawClippedRectangle(canvas)
   canvas.restore()
}

private fun drawIntersectionClippingExample(canvas: Canvas) {
   canvas.save()
   canvas.translate(columnTwo,rowTwo)
   canvas.clipRect(
       clipRectLeft,clipRectTop,
       clipRectRight - smallRectOffset,
       clipRectBottom - smallRectOffset
   )
   // The method clipRect(float, float, float, float, Region.Op
   // .INTERSECT) was deprecated in API level 26. The recommended
   // alternative method is clipRect(float, float, float, float), which
   // is currently available in API level 26 and higher.
   if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
       canvas.clipRect(
           clipRectLeft + smallRectOffset,
           clipRectTop + smallRectOffset,
           clipRectRight,clipRectBottom,
           Region.Op.INTERSECT
       )
   } else {
       canvas.clipRect(
           clipRectLeft + smallRectOffset,
           clipRectTop + smallRectOffset,
           clipRectRight,clipRectBottom
       )
   }
   drawClippedRectangle(canvas)
   canvas.restore()
}

....
```

- 여기서 살짝 다른 부분은 이제 Clipping shape을 둥글게 하는 것인데 그러기 위해서 아래와 같이 해당 모양에 대해서 미리 선언하고 초기화해야함

```kotlin
private var rectF = RectF(
   rectInset,
   rectInset,
   clipRectRight - rectInset,
   clipRectBottom - rectInset
)
```

- 그러면 아래와 같이 추가로 적용가능함

```kotlin
private fun drawRoundedRectangleClippingExample(canvas: Canvas) {
   canvas.save()
   canvas.translate(columnTwo,rowThree)
   path.rewind()
   path.addRoundRect(
       rectF,clipRectRight / 4,
       clipRectRight / 4, Path.Direction.CCW
   )
   canvas.clipPath(path)
   drawClippedRectangle(canvas)
   canvas.restore()
}
```
```kotlin
private fun drawOutsideClippingExample(canvas: Canvas) {
   canvas.save()
   canvas.translate(columnOne,rowFour)
   canvas.clipRect(2 * rectInset,2 * rectInset,
       clipRectRight - 2 * rectInset,
       clipRectBottom - 2 * rectInset)
   drawClippedRectangle(canvas)
   canvas.restore()
}
```

- text를 그리는 것은 다른 모양을 그리는 방식과 크게 다르지 않음

```kotlin
private fun drawTranslatedTextExample(canvas: Canvas) {
   canvas.save()
   paint.color = Color.GREEN
   // Align the RIGHT side of the text with the origin.
   paint.textAlign = Paint.Align.LEFT
   // Apply transformation to canvas.
   canvas.translate(columnTwo,textRow)
   // Draw text.
   canvas.drawText(context.getString(R.string.translated),
       clipRectLeft,clipRectTop,paint)
   canvas.restore()
}
```
```kotlin
private fun drawSkewedTextExample(canvas: Canvas) {
   canvas.save()
   paint.color = Color.YELLOW
   paint.textAlign = Paint.Align.RIGHT
   // Position text.
   canvas.translate(columnTwo, textRow)
   // Apply skew transformation.
   canvas.skew(0.2f, 0.3f)
   canvas.drawText(context.getString(R.string.skewed),
       clipRectLeft, clipRectTop, paint)
   canvas.restore()
}
```

### quickReject
- `quickReject` `Canvas` 메소드는 모든 transformation이 적용된 이후 현재 보이는 regions에 특정 rectangle과 path가 완전히 외부에 있는 것이 무엇인지 확인해주게 해주는 메소드임

- `quickReject`는 가능한 빨리 필요로 하고 더 복잡한 drwaing을 설계할 때 매우 유용함

- `quickReject`가 있다면, 어떤 객체를 모두 그릴지 안 그릴지 그리고 별도의 특별한 로직을 쓸 필요없게끔 판단할 수 있게 도움을 줌

- `quickReject` 메소드는 rectangle과 path가 화면에 더의상 보이지 않는다면 `true`를 리턴함, 부분적인 overlap이 있다면 계속해서 스스로 체크해봐야함

- `EdgeType`은 근처의 가까운 pixel을 위해 `AA(Antialiased)` 혹은 `BW(Black-White)`임

  - `AA(Antialiased)`는 rounding-out한 edges만을 다룸, 왜냐하면 그것들은 antialiased 되어 있기 때문임

  - `BW(Black-White)`는 오직 근처 pixel 범위의 rounding만을 다룸

- 다양한 버전의 `quickReject`가 존재함, 이는 공식문서를 보면 좋음

- 만약 복잡한 것을 그리게 된다면 이 함수는 빠르게 어떤 모양이 clipping region에 벗어나 있는지와 어떤 것을 추가로 계산하고 그려야하는지 알려줌 왜냐하면 그것들은 부분적으로나 완전히 clipping region에 있기 때문임

- 이를 위해서 아래와 같이 좌표와 추가 row를 아래와 같이 추가함

```kotlin
private val rejectRow = rowFour + rectInset + 2*clipRectBottom
```

- 그리고 추가로 함수를 정의함

```kotlin
private fun drawQuickRejectExample(canvas: Canvas) {
   val inClipRectangle = RectF(clipRectRight / 2,
       clipRectBottom / 2,
       clipRectRight * 2,
       clipRectBottom * 2)

   val notInClipRectangle = RectF(RectF(clipRectRight+1,
       clipRectBottom+1,
       clipRectRight * 2,
       clipRectBottom * 2))

   canvas.save()
   canvas.translate(columnOne, rejectRow)
   canvas.clipRect(
       clipRectLeft,clipRectTop,
       clipRectRight,clipRectBottom
   )
   if (canvas.quickReject(
           inClipRectangle, Canvas.EdgeType.AA)) {
       canvas.drawColor(Color.WHITE)
   }
   else {
       canvas.drawColor(Color.BLACK)
       canvas.drawRect(inClipRectangle, paint
       )
   }
       canvas.restore()
}
```

- 그리고 `onDraw` 상에 추가한 뒤 테스트를 함

