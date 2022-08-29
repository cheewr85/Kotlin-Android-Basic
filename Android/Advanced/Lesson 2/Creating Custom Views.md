### Intro
- Android에서는 `View`의 다양한 하위 클래스를 제공해주는데 만약 이러한 하위 클래스에서 당신의 요구를 충족하는 것이 없다면 Custom View라는 `View`의 하위 클래스를 만들 수 있음

- 그렇게 하기 위해서 현재 존재하는 `View`의 하위 클래스(`Button` 혹은 `EditText`) 혹은 `View`를 상속받은 클래스 자체를 만들 수 있음

- `View`를 직접적으로 extend 함으로써 `View`를 그리는데 활용하는 `onDraw` 함수를 오버라이딩하여 어떠한 사이즈던 형태던 상호작용하는 UI 요소를 만들 수 있음

- 이 Custom View를 만든 뒤, `TextView`와 `Button`을 만들듯이 레이아웃에 직접 추가할 수 있음

### Understanding custom views
- Views는 app UI의 기본적인 구성요소임, `View`는 UI widgets이라고 불리는 매우 많은 하위 클래스를 가지고 있음

- `Button`이나 `TextView`는 `View` 클래스를 상속받은 하위 클래스임

- 여기서 이 중 하나를 상속받을 것임, custom view는 부모의 모습과 동작을 상속받고 당신이 원하는 방향으로 보여지게하거나 동작을 오버라이딩할 수 있음

- 예를 들어 `EditText`를 상속받은 custom view를 확장하여 처음에는 `EditText`처럼 동작하게 할 수 있지만, 보여지는 것을 커스텀할 수 있음

- 이처럼 어떠한 `View`의 하위 클래스든 상속받을 수 있음, 그리고 이 custom view에 대해서 XML의 요소로써도 사용이 가능함

- 만약 아예 나만의 custom view를 만들기 위해서 `View` 클래스 자체를 상속받아서 `View`에서 정의하는 모습이나 동작에 대해 정의한 함수를 오버라이딩 할 수 있음

- 이렇게 되면 스크린에서 보여지는 형태와 사이즈등 모든 UI요소로 그려지는 것들에 대해서 순수하게 사용자가 custom해서 만들고 싶은대로 만들 수 있음

- custom view를 만드는데 일반적인 방법은 아래와 같음

   - `View`를 확장한 custom view 클래스를 만들거나, `View`의 하위 클래스를 상속받음(`Button`이나 `EditText`)

   - 만약 기존에 존재하는 `View`의 하위 클래스를 상속받는다면, 바꾸고 싶은 형태나 동작만을 오버라이딩하면 됨

   - `View` 클래스를 상속받는다면, 새로운 클래스의 `onDraw`나 `onMeasure`같은 `View`의 메소드를 오버라이딩함으로써 custom view의 형태나 모습을 조절하는데 그릴 수 있음

   - UI를 추가하거나 필요하면 custom view를 다시 그릴 수 있음

   - XML layout 상에서 UI widget으로써 사용할 수 있음, 그리고 이를 별도로 정의해서 처리할 수 있음

### Create a custom view
- 우선 기본적인 View를 추가함

- 그리고 새로운 Kotlin 클래스를 만들고 `View`를 상속받음, 그리고 아래와 같이 `View` 생성자를 추가함, 해당 어노테이션을 통해서 Kotlin 컴파일러가 해당 함수에 대한 디폴트 파라미터를 생성하도록 하게끔 함

```kotlin
class DialView @JvmOverloads constructor(
   context: Context,
   attrs: AttributeSet? = null,
   defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
```

- 그리고 상단에 fan speed에 관해서 나타내는 `enum` 클래스를 만듬, 이는 실제 String이 아니므로 `Int` 타입으로 진행함

```kotlin
private enum class FanSpeed(val label: Int) {
   OFF(R.string.fan_off),
   LOW(R.string.fan_low),
   MEDIUM(R.string.fan_medium),
   HIGH(R.string.fan_high);
}
```

- 그리고 하단에 dial indicator와 label에 대해 그리는 것의 일부로 쓸 상수를 추가함

```kotlin
private const val RADIUS_OFFSET_LABEL = 30      
private const val RADIUS_OFFSET_INDICATOR = -35
```

- 그 이후에 `DialView` 클래스 안에 custom view를 그리기 위한 다양한 변수를 정의함, 이 때 `PointF`를 import 받아야함

- 이 때 `radius`는 원의 현재 radius를 나타내는 것으로 view가 screen에 그려질 때 설정되는 값임

- `fanSpeed`는 현재 fan의 속도로 `FanSpeed` enum 의 값 중 하나이고 디폴트는 `OFF`임

- `postPosition`은 X,Y point로 screen에서의 다양한 view의 요소들을 그리는데 사용됨

```kotlin
private var radius = 0.0f                   // Radius of the circle.
private var fanSpeed = FanSpeed.OFF         // The active selection.
// position variable which will be used to draw label and indicator circle position
private val pointPosition: PointF = PointF(0.0f, 0.0f)
```

- 이 value들은 view가 실제로 그려지기 전에 실제 그려지는 과정을 더 빨리하게 하기 위해 초기화되고 만들어짐

- 그리고 `DialView`에 `Paint` 객체를 초기화함, 사전에 아래와 같이 정의할 경우 그리는 과정을 더 빠르게 하게끔 도와줌

```kotlin
private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
   style = Paint.Style.FILL
   textAlign = Paint.Align.CENTER
   textSize = 55.0f
   typeface = Typeface.create( "", Typeface.BOLD)
}
```

- 그리고 string 값을 추가함

```xml
<string name="fan_off">off</string>
<string name="fan_low">1</string>
<string name="fan_medium">2</string>
<string name="fan_high">3</string>
```

### Drawing custom views
- custom view를 만들게 되면, 이를 그릴 필요가 있는데, `EditText`와 같이 `View`의 하위 클래스를 상속받으면 그 View 자체의 모습과 표현이 화면에 그려짐, 결과적으로 그래서 view를 직접 그릴 필요가 없는 것임

- 이 때 부모 메소드를 오버라이딩해서 당신의 view로 커스텀할 수 있음

- 만약 아예 view를 새로 만든다면, 화면을 새로고침할 때마다 전체 view를 그릴 필요가 있고 이 때 `View`의 메소드를 오버라이딩해서 그릴 수 있음

- 적절한 custom view를 그리기 위해서 `View`를 상속받고

   - `onSizeChanged` 메소드를 통해 처음 나타낼 때, 그리고 매번 View가 사이즈가 변할 때 view의 사이즈를 계산하기 위해서 해당 메소드를 오버라이딩함

   - `onDraw` 메소드를 통해 `Paint` 객체로 스타일을 할 수 있는 `Canvas` 객체를 활용하여 custom view를 그릴 수 있음

   - `invalidate`메소드를 통해서 유저의 상호작용에 view가 변화해서 무효화하는지 확인하고 `onDraw`를 강제호출하여 view를 다시 그리게 함

- `onDraw` 메소드는 화면을 새로고침할 때마다 매번 호출을 하고 1초에 몇번씩 호출될 수도 있음

- 성능적인 측면과 시각적인 뭉개짐을 방지하기 위해서 `onDraw`를 가능한 적게 일하게 해야함, 그리고 `onDraw`를 할당하지 않는게 좋음 자칫 잘못하면 시각적으로 더디는 상황이 발생할 수 있음

- `Canvas`와 `Paint` 클래스는 그리는데 있어서 많은 지름길을 제공해줌

   - text를 그리기 위해 `drawText`를 활용함, typeface를 특정하기 위해서 `setTypeface`를 호출함, `setColor`를 통해 text color를 정함

   - 원초적인 shape을 그리기 위해 `drawRect`와 `drawOval`, `drawArc`를 사용함, 형태가 채워지든 간략해지든 둘 다 `setStyle`을 호출함

   - bitmap을 그리기 위해 `drawBitmap`을 호출함

### Draw the custom view and add it to the layout
- 앞서 custom view에 대한 기본적인 설정 이후 이제 `onSizeChanged`메소드를 오버라이딩하여 custom view의 사이즈를 계산함

- 해당 메소드는 view의 크기가 변하는 어느때던 호출이 됨, layout이 만들어져 그려질 처음에도 호출이 됨

- `onSizeChanged`를 오버라이딩하여 위치, 차원, custom view size와 연관된 모든 값을 계산함, 매번 그릴 때마다 다시 계산하지 않고 할 수 있음

- 아래와 같이 현재 circle element를 계산할 수 있음(min은 kotlin.math.min import 해야함)

```kotlin
override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
   radius = (min(width, height) / 2.0 * 0.8).toFloat()
}
```

- 그리고 `computeXYForSpeed`라는 `PointF`의 확장함수를 정의하고 text label과 current indicator, 주어진 FanSpeed 위치와 raidus를 위한 X,Y 좌표를 계산함(`onDraw`에서 사용함)

```kotlin
private fun PointF.computeXYForSpeed(pos: FanSpeed, radius: Float) {
   // Angles are in radians.
   val startAngle = Math.PI * (9 / 8.0)   
   val angle = startAngle + pos.ordinal * (Math.PI / 4)
   x = (radius * cos(angle)).toFloat() + width / 2
   y = (radius * sin(angle)).toFloat() + height / 2
}
```

- 그 다음 `onDraw`메소드를 오버라이딩 함, 이는 `Canvas`와 `Paint`클래스를 스크린에서 view를 그리는데 활용하는 메소드임

- 그리고 해당 메소드 안에서 `FanSpeed`에 따라 색깔이 바뀌도록 추가함

```kotlin
// Set dial background color to green if selection not off.
paint.color = if (fanSpeed == FanSpeed.OFF) Color.GRAY else Color.GREEN
```

- 그런 다음에 dial을 그리게 추가함, 앞서 설정한 것과 크기의 경우 아래와 같이 현재 View를 쓰지만 조절을 함

```kotlin
// Draw the dial.
canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius, paint)
```

- 이 때, 또 확장함수를 통해서 원의 크기를 좀 더 줄여서 만들 수 있음(현재 fan speed를 바탕으로 indicator center의 x,y 좌표를 계산할 수 있음)

```kotlin
// Draw the indicator circle.
val markerRadius = radius + RADIUS_OFFSET_INDICATOR
pointPosition.computeXYForSpeed(fanSpeed, markerRadius)
paint.color = Color.BLACK
canvas.drawCircle(pointPosition.x, pointPosition.y, radius/12, paint)
```

- 마지막으로 dial에서의 fan speed label에 대해서 적절한 위치를 그릴 수 있음, 똑같이 `computeXYForSpeed`를 호출해서 각각 라벨의 위치를 얻어서 `pointPosition` 객체를 활용하여 할당을 피함, `drawText`를 사용함

```kotlin
// Draw the text labels.
val labelRadius = radius + RADIUS_OFFSET_LABEL
for (i in FanSpeed.values()) {
   pointPosition.computeXYForSpeed(i, labelRadius)
   val label = resources.getString(i.label)
   canvas.drawText(label, pointPosition.x, pointPosition.y, paint)
}
```

- 그리고 xml 파일상에서 아래와 같이 `ImageView` 대신해서 추가할 수 있음

```xml
<com.example.android.customfancontroller.DialView
       android:id="@+id/dialView"
       android:layout_width="@dimen/fan_dimen"
       android:layout_height="@dimen/fan_dimen"
       app:layout_constraintTop_toBottomOf="@+id/customViewLabel"
       app:layout_constraintLeft_toLeftOf="parent"
       app:layout_constraintRight_toRightOf="parent"
       android:layout_marginLeft="@dimen/default_margin"
       android:layout_marginRight="@dimen/default_margin"
       android:layout_marginTop="@dimen/default_margin" />
```

### Add view interactivity
- 유저가 view를 탭할 때 action을 수행하기 위해서 custom view를 설정할 수 있음

- 각각의 탭은 off-1-2-3에서 다시 off까지로 선택한 indicator로 갈 수 있게 움직여야함

- 만약 selection이 1이 상이면 배경색도 gray에서 green으로 바꿔야함

- custom view를 클릭 가능하게 하기 위해서

   - view의 `isClickable` 속성을 `true`로 만듬, 이 속성을 통해 custom view가 click이 가능하게 함

   - view가 클릭될 때, 동작을 수행하기 위해서 `performClick`을 implement함

   - Android System에게 view를 다시 그리게 할 때 `onDraw`를 호출하도록 `invalidate` 메소드를 호출함

- 일반적으로 Android view에서는 `OnClickListener`를 implement했지만 custom view에서는 `performClick`을 implement함

- 먼저 enum class에서 아래와 같이 변화가 적용되는 확장함수를 씀

```kotlin
private enum class FanSpeed(val label: Int) {
   OFF(R.string.fan_off),
   LOW(R.string.fan_low),
   MEDIUM(R.string.fan_medium),
   HIGH(R.string.fan_high);

   fun next() = when (this) {
       OFF -> LOW
       LOW -> MEDIUM
       MEDIUM -> HIGH
       HIGH -> OFF
   }
}
```

- 그리고 `DialView`에서 `init`으로 `isClickable`을 true로 만듬

```kotlin
init {
   isClickable = true
}
```

- 그 밑에 `performClick`메소드를 둠, 맨 처음에는 `onClickListener`를 호출하지만 그 다음에서 `next` 메소드를 통해서 view content가 바뀌게 됨, 그리고 `invalidate`를 통해서 View를 다시 그리게 됨

- custom view에서는 어떠한 이유라도 view가 변하게 되면 `invalidate`를 호출해줘야함

```kotlin
override fun performClick(): Boolean {
   if (super.performClick()) return true

   fanSpeed = fanSpeed.next()
   contentDescription = resources.getString(fanSpeed.label)
  
   invalidate()
   return true
}
```

### Use custom attributes with your custom view
- 이 때, `DialView`에서 dial position에 따라서 서로 다른 색깔을 설정하게 할 수 있음

- 아래와 같이 values의 `attrs`를 추가해서 `declare-styleable`을 통해서 `color`에 대해서 각각 설정을 다르게 해 줄 수 있음

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
       <declare-styleable name="DialView">
           <attr name="fanColor1" format="color" />
           <attr name="fanColor2" format="color" />
           <attr name="fanColor3" format="color" />
       </declare-styleable>
</resources>
```

- 해당 attributes에 대해서 `DialView` 아래와 같이 xml 상에서 추가할 수 있음

```xml
<com.example.customfancontroller.DialView
        android:id="@+id/dialView"
        android:layout_width="@dimen/fan_dimen"
        android:layout_height="@dimen/fan_dimen"
        app:layout_constraintTop_toBottomOf="@+id/customViewLabel"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        android:layout_marginLeft="@dimen/default_margin"
        android:layout_marginRight="@dimen/default_margin"
        android:layout_marginTop="@dimen/default_margin"
        app:fanColor1="#FFEB3B"
        app:fanColor2="#CDDC39"
        app:fanColor3="#009688"/>
```

- 해당 attributes를 `DialView`에서 쓰기 위해서 이를 로컬 변수에 캐싱처럼 할당해서 쓸 수 있음

```kotlin
private var fanSpeedLowColor = 0
private var fanSpeedMediumColor = 0
private var fanSpeedMaxColor = 0
```

- 그리고 `init` 블럭에서 `withStyledAttributes` 확장함수를 활용해서 색깔을 바꾸게 적용할 수 있음

```kotlin
context.withStyledAttributes(attrs, R.styleable.DialView) {
   fanSpeedLowColor = getColor(R.styleable.DialView_fanColor1, 0)
   fanSpeedMediumColor = getColor(R.styleable.DialView_fanColor2, 0)
   fanSeedMaxColor = getColor(R.styleable.DialView_fanColor3, 0)
}
```

- 그리고 상태에 따라 `onDraw`에 적용을 함

```kotlin
paint.color = when (fanSpeed) {
   FanSpeed.OFF -> Color.GRAY
   FanSpeed.LOW -> fanSpeedLowColor
   FanSpeed.MEDIUM -> fanSpeedMediumColor
   FanSpeed.HIGH -> fanSeedMaxColor
} as Int
```

### Add accessibility
- Accessibility는 사용에 어려움이 있는 사람들을 포함하여 모두에게 사용가능하게 design, implementation, testing technique의 집합체임

- 일반적인 disabilities는 blindness, low vision, color blindness, deafness or hearing loss, restricted motor skills 등 사용자의 기기 사용에 영향을 미칠 수 있음

- 그래서 앱을 개발할 경우 이러한 accessibility를 생각하고 불편함을 겪는 유저뿐만 아니라 모든 유저를 생각해서 이런 UX를 더 낫게 만들어야함

- Android는 이러한 accessibilty feature로 default로 일반적인 UI View를 제공함

- 하지만 custom view를 만들게 된다면 앞서 말한 설명과 on-screen content로써 custom view가 어떻게 accessible features를 제공할 것인지 고려해야할 필요가 있음

- 여기서 Android screen reader인 TalkBack을 활용하여 custom view에 대해서 알 수 있는 힌트와 설명을 조정할 수 있음

- TalkBack을 활성화시킨다면 user는 screen을 보지 않고 Android device와 상호작용할 수 있음, 왜냐하면 Android가 screen 요소를 그릴 것이므로, 유저의 시각적인 불편함은 사용하는 앱의 TalkBack에 의존할 것임

- Android device나 emulator에서 Settings > Accessibility > TalkBack으로 간 뒤, On/Off를 통해 TalkBack을 켤 수 있음(권한 설정 확인과 기기 비밀번호와 처음 시작시 튜토리얼이 나옴)

- 앱을 실행하고 device의 Overview 혹은 recent button으로 연다면 TalkBack에서 app의 이름을 알려주고 TextView label의 text 역시 알려줌, 하지만 `DialView`를 tap 한다면 view의 상태나 view를 탭했을 때의 활성화 된 후의 action등에 대한 정보를 알려주지 않음

- Content descriptions은 app에서의 view의 목적과 의미를 설명함, 이 label은 TalkBack과 같은 screen reader가 각 element가 정확히 기능하지 설명해줄 수 있게끔 해줌

- `ImageView`에서 `contentDescription`을 하고 TextView, EditText에서  자동으로 해당 text를 view의 content description으로 하듯이 custom fan control view 역시 view가 클릭되고 현재 fan setting을 가르킬 때 동적으로 해당 content를 설명하는 것이 필요함

- 이를 위해서 `DialView` 클래스에서 아래와 같이 함수를 추가함

```kotlin
fun updateContentDescription() {
   contentDescription = resources.getString(fanSpeed.label)
}
```

- 그리고 `init`과 `performClick` 부분에 추가를 함

```kotlin
init {
   isClickable = true
   // ...

   updateContentDescription()
}
```
```kotlin
override fun performClick(): Boolean {
   if (super.performClick()) return true
   fanSpeed = fanSpeed.next()
   updateContentDescription()
   invalidate()
   return true
}
```

- 그러면 이제 TalkBack에서 해당 설명을 확인할 수 있음

- 여기서 단순히 활성화 됐는지 여부 말고도 활성화 됐을 때 어떤 일이 일어날지에 대해서도 설정할 수 있음, 이렇게 하기 위해서 accessibility delegate의 방식으로 accessibility node info object의 view action에 대한 정보를 추가해야함

- 아래와 같이 `init`에서 `AccessibilityDelegateCompat`의 새로운 객체의 view로 delegate 할 수 있음, 그런 다음, `onInitializeAccessibilityNodeInfo()`를 오버라이딩 

```kotlin
ViewCompat.setAccessibilityDelegate(this, object : AccessibilityDelegateCompat() {
   override fun onInitializeAccessibilityNodeInfo(host: View, 
                            info: AccessibilityNodeInfoCompat) {
      super.onInitializeAccessibilityNodeInfo(host, info)

   }  
})
```

- 모든 view는 실제 layout components view와 상관이 있든 없든 accessibility node에 대한 tree를 가지고 있음

- Android accessibility service는 view에 대한 정보를 찾기 위해서 이러한 node를 navigate함

- custom view를 만들 때 accessibility를 위한 custom information 제공을 위해서 node information을 오버라이딩 할 필요가 있음

- 그래서 해당 메소드 안에서 생성자 상수를 넘겨서 기본 string을 설정함

```kotlin
ViewCompat.setAccessibilityDelegate(this, object : AccessibilityDelegateCompat() {
   override fun onInitializeAccessibilityNodeInfo(host: View, 
                            info: AccessibilityNodeInfoCompat) {
      super.onInitializeAccessibilityNodeInfo(host, info)
      val customClick = AccessibilityNodeInfoCompat.AccessibilityActionCompat(
         AccessibilityNodeInfo.ACTION_CLICK,
        "placeholder"
      )
   }  
})
```

- `AccessibilityActionCompat` 클래스는 accessibility 목적을 위한 view의 action을 나타냄, 일반적으로 클릭과 탭이지만 focus를 집중하고 잃는것, cut, copy, paste 혹은 scrolling하는 것도 포함함

- 그리고 `placeholder` string을 아래와 같이 수정할 수 있음

```kotlin
ViewCompat.setAccessibilityDelegate(this, object : AccessibilityDelegateCompat() {
   override fun onInitializeAccessibilityNodeInfo(host: View, 
                            info: AccessibilityNodeInfoCompat) {
      super.onInitializeAccessibilityNodeInfo(host, info)
      val customClick = AccessibilityNodeInfoCompat.AccessibilityActionCompat(
         AccessibilityNodeInfo.ACTION_CLICK,
        context.getString(if (fanSpeed !=  FanSpeed.HIGH) R.string.change else R.string.reset)
      )
   }  
})
```

- 그 다음, node info object에 새로운 accessibility를 추가하기 위해 아래 메소드를 써서 추가함

```kotlin
ViewCompat.setAccessibilityDelegate(this, object : AccessibilityDelegateCompat() {
   override fun onInitializeAccessibilityNodeInfo(host: View, 
                            info: AccessibilityNodeInfoCompat) {
       super.onInitializeAccessibilityNodeInfo(host, info)
       val customClick = AccessibilityNodeInfoCompat.AccessibilityActionCompat(
           AccessibilityNodeInfo.ACTION_CLICK,
           context.getString(if (fanSpeed !=  FanSpeed.HIGH) 
                                 R.string.change else R.string.reset)
       )
       info.addAction(customClick)
   }
})
```