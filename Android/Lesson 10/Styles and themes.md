## Android's styling system
- Android는 app의 모든 view에 대해서 보여지는 것을 컨트롤 할 수 있게 하기 위해서 풍부한 styling system을 지원함

- themes, styles, styling의 영향을 주는 view attribute등 사용할 수 있음

- 아래의 사진은 각 스타일링의 우선순위를 요약한 것임, 즉 아래의 diagram은 system에 의해 styling 방식이 적용되는 순서를 아래에서 위로 나타냄

- 예를 들어 만약 theme에서 text size를 설정하고 view attribute에서 text size를 설정했다면 view attribute가 theme styling을 오버라이딩 즉, view attribute 속성으로 적용이 됨

![one](/Android/img/fourtytwo.png)

### View attributes
- 각각 view에 명시적으로 속성을 설정할 수 있음, 재사용할 수 있는 요소가 아님

- 모든 요소는 style이나 theme을 통해서 설정하여 사용할 수 있음

- margin, padding, constraint같은 사용자 지정 또는 일회성 디자인에 사용함

### Styles
- font size나 color 같이 재사용 가능한 styling 정보의 집합체를 만들기 위해 style을 사용함

- 앱 전체에 사용되는 공통 디자인의 작은 set을 선언할 때 좋음

- 기본 스타일을 재정의하여 몇 몇 view의 style을 적용함, 예를 들어 스타일을 사용하여 일관된 스타일의 헤더 또는 버튼 세트를 만듬

### Default style
- Android system에서 제공해주는 default styling임

### Themes
- 전체 앱을 위한 color를 정의하기 위해 theme을 사용

- 전체 앱을 위한 기본 font를 설정하기 위해 theme을 사용

- text views나 radio buttons 같은 모든 views에 적용

- 전체 앱에 일관되게 적용할 수 있는 속성을 구성하는 데 사용

### TextApperance
- `fontFamily` 같은 오직 text 속성을 위한 styling

- Android는 view의 styles를 지정할 때, 커스텀 할 수 있는 themes, styles, attributes의 조합으로 적용함

- Attributes는 항상 style이나 theme에 지정된 모든 항목을 재정의함, 그리고 style은 항상 theme에 지정된 모든 항목을 재정의함

----------

### Use attributes for styling
- `textSize`를 `24sp`로 속성을 변경함

- `sp`는 scale-independent pixels로 이는 유저가 장치 설정에서 설정한 pixel 밀도와 font-size 기본 설정에 따라 크기가 조정됨

- Android는 text를 그릴 때 화면에 표시되어야 하는 text의 크기를 파악함, text-size는 `sp`를 사용하는게 좋음

- 그리고 `textColor`를 aRGB 값인 `#FF555555`로 설정함

- aRGB는 색상의 알파 투명도 red, green, blue 값을 표현함, aRGB는 16진수로 각각 색깔 요소가 00부터 FF까지 있음

- `#(alpha)(red)(green)(blue)`, `(00-FF)(00-FF)(00-FF)(00-FF)`

- `title` 뿐 아니라 `subtitle`도 그에 맞게 설정함

### Use themes and downloadable fonts
- 앱에서 글꼴을 사용할 때 필요한 글꼴 파일을 APK의 일부로 제공할 수 있음, 이 솔루션은 간단하지만 일반적으로 앱을 다운로드하고 설치하는 데 시간이 오래 걸리므로 권장하지 않음

- Android에서는 앱이 Downloadable Fonts API를 사용하여 런타임에 글꼴을 다운로드할 수 있음, 앱이 기기의 다른 앱과 동일한 글꼴을 사용하는 경우 Android는 글꼴을 한 번만 다운로드하여 기기의 저장 공간을 절약함 

- 이 때 Downloadable Font를 사용하여 Theme를 사용하는 앱의 모든 View 글꼴을 설정할 수 있음

- 여기서 design 탭에서 textview를 고르고 `fontFamily` 속성을 찾아서 설정에 More Fonts를 통해서 `lobster` 폰트를 찾아서 Create downloadable font를 통해 처리함

- 그럼 Manifest 파일에서 `<meta-data>`태그에서 `name`, `resource` 속성을 통해 `preloaded_fonts`로 이름을 정하고 이 태그는 Google Play Services에 앱이 downloaded fonts를 사용할 수 있게 말해줌

- 앱이 실행되고 Lobster Tow font를 요청할 떄 font provider는 만약 font가 기기에서 아직 사용 가능하지 않다면 internet으로부터 font를 다운로드함

```xml
<meta-data android:name="preloaded_fonts" android:resource="@array/preloaded_fonts"/>
```

- font와 관련된 xml 파일이 추가됨

- 여기서 `styles.xml`에서 `AppTheme`이 기본값으로 설정되어 만들어진 것을 알 수 있음, 여기서 모든 text에 위에서 다운 받은 폰트를 적용하기 위해선 이 theme을 수정해야함

- `<style>` 태그에 `parent` 요소를 보면 모든 style tag는 parent를 특정지을 수 있고 parent's 속성의 모든 것을 상속할 수 있음

- 이 `Theme`은 Android library에서 정의함

- `MaterialComponents` theme은 button이 어떻게 작동하는지부터 toolbars를 어떻게 그리는지 모든 것을 특정 지음

- 이 theme는 꽤 민감한 기본값으로 이 부분을 원하는 부분으로 커스텀할 수 있음 아래와 같은 설정은 `Actionbar`가 없는 `Light` 버전의 theme을 app이 사용함을 말함

```xml
<!-- Base application theme. -->
<style name="AppTheme" parent="Theme.MaterialComponents.Light.NoActionBar">
   <!-- Customize your theme here. -->
   <item name="colorPrimary">@color/colorPrimary</item>
   <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
   <item name="colorAccent">@color/colorAccent</item>
</style>
```

- 여기서 모든 font를 위에서 설정한 font로 쓸 수 있음

```xml
<!-- Base application theme. -->
    <style name="AppTheme" parent="Theme.MaterialComponents.Light.NoActionBar">
        <!-- Customize your theme here. -->
        <item name="colorPrimary">@color/colorPrimary</item>
        <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
        <item name="colorAccent">@color/colorAccent</item>
        <item name="android:fontFamily">@font/lobster_two</item>
        <item name="fontFamily">@font/lobster_two</item>
    </style>
```

- 여기서 테스트로 `title` textview에 대해서 `app:fontFamily`를 통해서 다른 font를 적용시킬 수 있음

### Use styles
- Theme은 default font나 primary color 같은 앱의 일반적인 theming을 적용하는데 좋음

- Attribute는 각각 스크린의 특정 지을 수 있는 특정 view의 styling이나 margin, padding, constraint 같은 layout 정보를 추가하는데 좋음

- `styles`의 경우 당신이 원하는 view의 적용할 수 있는 속성을 재사용 가능한 그룹임

- 아래와 같이 `<resource>` 태그 안에 `<style>`을 씀

- 이 style 이름을 의미적으로 정하는 것은 중요함, style에 영향을 줄 요소에 기반하지 말고 나중에 쓸 style에 기반하여 style 이름을 정해야함, 예를 들어 `Title`이라고 부르는 것 `LargeFontInGrey`라고 하지 말고

- 이렇게 지으면 이 style은 어떤 title에서도 재사용할 것임, 편의를 위해 `TextAppearance.Title`이라고 함

- 그리고 이 style은 parent가 있음, 여기서 style은 `TextAppearance.MaterialComponents.Headline6`를 extend함 이 style은 `MaterialComponents` theme의 디폴트 text style임, 그래서 아예 밑바닥부터 시작하는 대신 이 defalut style을 확장하는게 나음

- 그리고 그 안에 `textSize`와 `textColor`를 씀

```xml
<style name="TextAppearance.Title" parent="TextAppearance.MaterialComponents.Headline6">
        <item name="android:textSize">24sp</item>
        <item name="android:textColor">#555555</item>
    </style>
```

- 그리고 `SubTitle` style도 정의함

```xml
<style name="TextAppearance.Subtitle" parent="TextAppearance.Title" >
   <item name="android:textSize">18sp</item>
</style>
```

- 그리고 아래와 같이 `TextView`에 적용함

```xml
<TextView
       android:id="@+id/title"
       android:textAppearance="@style/TextAppearance.Title"
```

```xml
<TextView
       android:id="@+id/subtitle"
       android:textAppearance="@style/TextAppearance.Subtitle"
```

- 여기서 text를 조작하는 theme와 style이 모두 있는 경우 theme 안에 text properties를 원하고 style의 설정되고 상속된 것을 재정의하려하면 textAppearance 속성으로 text properties를 적용해줘야함

