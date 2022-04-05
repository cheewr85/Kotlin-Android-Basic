### Choose the root layout to use LinearLayout
- ViewGroup은 다른 View와 ViewGroup을 자식뷰로 포함할 수 있는 View임, 최상단에 위치해서 계층적으로 구성할 수 있음

### Add a TextView using Layout Editor
- `setContentView`를 통해서 특정 `Activity` 파일을 연결 할 수 있음

- `R`클래스는 앱의 모든 요소에 대한 정의된 자동생성되는 클래스로 모든 요소를 참조할 수 있음

### Style your TextView
- 어떠한 View, Element이던 간에 아래와 같은 View에 대한 공간에 대해서 파악할 수 있음

![one](/Android/img/one.png)

- 그리고 각각의 Padding과 Margin 값에 대해서 조절할 수 있음

- `Right/left`와 `Start/End`로 쓰이는 경우가 있는데 타겟 버전이 17이상이ㅣ면 `Start/End`를 쓰고 그 이하면 `Right/Left`를 쓰는 것이 나음

- 이 부분은 LTR, RTL flow로 되던간에 홤녀의 오른쪽과 왼쪽 사이드를 각각 `Right`, `Left`로 쓰지만 `Start/End`도 그 흐름대로 쓰임, 알고 있으면 됨

- `dp`단위는 다른 밀도에서 스크린에 같은 크기로 UI가 보이길 원한다면 해당 단위를 쓰면 됨 하지만 텍스트 사이즈는 `sp`를 권장함

- `style`은 View의 형태와 디자인에 대해서 모아져 있는 것으로 font color, font size, background color, padding 등 다양한 요소들이 존재함

- 그리고 특정 View에 대해서 style을 뽑아서 다른 View에도 해당 style을 재사용할 수 있음, 이를 통해서 다양한 View에 대한 통일성을 유지할 수 있고 한 곳의 공통 요소를 묶어 둘 수 있음

- 아래와 같이 View의 특정 요소에 대해서 뽑아서 style로 저장할 수 있음, 추출을 했다면 원래 View에도 style로써 들어가 있음

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>

    <style name="NameStyle">
        <item name="android:layout_marginTop">@dimen/layout_margin</item>
        <item name="android:fontFamily">@font/roboto</item>
        <item name="android:paddingTop">@dimen/small_padding</item>
        <item name="android:textColor">@color/black</item>
        <item name="android:textSize">@dimen/text_size</item>
    </style>
</resources>
```
```xml
<TextView
        android:id="@+id/name_text"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="@string/name"
        android:textAlignment="center"
        style="@style/NameStyle" />
```

### Add an ImageView
- `ImageView`는 `PNG,JPG,GIF,WebP Files`와 같은 `Bitmap` 요소를 보여줄 수 있거나 혹은 `Drawable`에 있는 `Vector Drawing`도 보여줄 수 있음

- `contentDescription`을 통해서 screen reader가 user에게 image를 설명해줌

### Add a ScrollView
- `ScrollView`는 계층적으로 구성된 View를 스크롤 할 수 있게 해주는 ViewGroup임

- `ScrollView`는 오직 하나의 다른 View, ViewGroup을 자식으로 둘 수 있음, 일반적으로 `LinearLayout`을 둠

![one](/Android/img/two.png)

