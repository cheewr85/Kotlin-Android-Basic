### Add support for right-to-left(RTL) languages
- LTR과 RTL의 차이는 보여지는 컨텐츠의 방향임

- LTR에서 RTL로 UI 방향이 바뀌면 이를 mirroring이라고 함, mirroring은 대부분의 screen, text, text field icon layout 등 모든 것에 영향을 줌

- clock, phone numbers나 direction이 없는 icon playback controls, chart, graph등은 mirroring 되지 않음

- 세계적으로 RTL로 text를 읽는 곳이 꽤 되기 때문에 이를 알아둘 필요가 있음

- 먼저 manifest에서 `application` 단위에서 아래와 같이 추가함

```xml
<application
        ...
        android:supportsRtl="true">
```

- 그리고 layout에서 `Locale for preview`를 통해 `Preview Right to Left`를 보고 RTL을 볼 수 있음

- 에뮬레이터에 강제로 RTL 적용을 한 뒤 보면 짤린 것을 알 수 있음

- 즉, 이는 Constraint가 변경이 되는 것이 반영이 안되는 것인데 여기서 단순히 `Left`, `Right`를 쓰게 될 경우 RTL을 사용할 경우 제대로 반영이 안됨

- 그렇기 때문에 `Start`, `End`를 쓰는 것이 나음, 이는 text의 start와 end를 현재 언어의 text의 방향에 맞게 적절하게 적용하는 것임

- `list_item`을 `Left`, `Right` 값을 수정을 함, 그러면 RTL을 해도 깨지는 현상이 발생하지 않음

```xml
<ImageView
            android:id="@+id/gdg_image"
            android:layout_width="50dp"
            android:layout_height="76dp"
            android:layout_marginStart="@dimen/spacing_normal"
            android:layout_marginTop="26dp"
            android:layout_marginBottom="26dp"
            android:contentDescription="TODO"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:srcCompat="@drawable/ic_gdg" />

        <TextView
            android:id="@+id/chapter_name"
            android:layout_width="0dp"
            android:layout_height="0dp"
            android:layout_marginStart="@dimen/spacing_normal"
            android:layout_marginEnd="@dimen/spacing_normal"
            android:gravity="center_vertical"
            android:text="@{chapter.name}"
            android:textAppearance="?textAppearanceHeadline6"
            app:layout_constraintBottom_toBottomOf="@+id/gdg_image"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toEndOf="@+id/gdg_image"
            app:layout_constraintTop_toTopOf="@+id/gdg_image"
            app:layout_constraintVertical_chainStyle="packed"
            tools:text="GDG Mountain View is really long so it will wrap in tools" />

```

- 그리고 icon에 대해서는 `automirrored`를 제거해서 제대로 나오게끔 처리함

- 그래서 되도록 `Left`, `Right` 대신 `Start`, `End` 쓰는것을 권장함

- 여기서 직접 바꿀 수도 있고 처음부터 그렇게 안 쓸 수 있지만 Android에서는 `Refactor > Add RTL support where possible`을 누르면 `Left/Right`를 `Start/End`로 되도록 처리해줌

- 추가로 `res/values-es`에서 이 es 폴더는 다른 언어에 대한 폴더를 의미함 즉, 다른 지역에 언어도 추가해서 활용할 수 있음

### Scan for accessibility
- Accessibility Scanner app을 통해 app accessible을 확인할 수 있음

- 이를 통해서 target device에 대해서 보여지는 screen에 더 좋은 접근성을 제공해주기 위해서 개선사항을 추천해줌 

- 에뮬레이터에서 Play Store에서 Accessibility Scanner by Google LLC를 통해서 쓰려는 앱을 실험할 수 있음, 그리고 기본 세팅을 하고 앱을 실행시키면 그와 관련해서 SnapShot을 찍어서 체크된 요소에 대해서 권장사항을 알려줌

- 그리고 사진 말고도 세부사항에 대해서 알아갈 수 있음 아래와 같이

![one](/Android/img/fourtythree.png)

### Design for TalkBack
- Accessibility Suite 앱은 앱을 더 접근성 있도록 도와주는 tools도 포함함, TalkBack 같은 tool

- TalkBack은 청각, 촉각 및 음성 피드백을 주는 스크린 리더로 사용자가 눈을 사용하지 않고 기기의 content를 소비하거나 안내받게 할 수 있음

- 이는 단순히 맹인만을 위한 것이 아니라 시각적인 불편함이 있는 사람이나 심지어 눈을 쉬고 싶은 사람들에게도 제공을 함

- 그래서 모두에게 접근성을 높일 수 있음

- 먼저 Accessibility Suite앱을 다운로드 함, 똑같이 사용 설정을 함

- 여기서 예시앱에 들어가서 이미지를 눌러도 반응이 없음, 그래서 `contentDescription`을 아래와 같이 추가할 수 있음

```xml
android:contentDescription="@string/stage_image_description"
```

- 그러면 이미지 클릭시 목소리가 들림, 뿐 만 아니라 `add_gdg_fragment`의 `EditText`에서 `hint`를 통해서 이런 컨텐츠를 제공할 수 있음, 각각 추가함, TextView에도 추가 가능

```xml
android:hint="@string/your_name_label"

android:hint="@string/email_label"

android:hint="@string/city_label"

android:hint="@string/country_label"

android:hint="@string/region_label"

...

android:contentDescription="@string/motivation" 
```

- 그 뿐 아니라 이 content를 grouping으로 다룰 수 있음, 관련된 content에 대해서 grouping을 통해서 알려주는 것임 즉, 별도의 유저가 정보를 찾기 위한 행동을 하지 않아도 설명을 할 수 있는 것임

- `LabelTextWhy`와 `EditTextWhy`를 `LinearLayout`으로 묶을 수 있음

- 버튼의 label은 현재 OK로 되어 있는데 이 때 이 label이 버튼이 submit이 된 이후로 동적으로 바뀔 수 있게 다른 label과 description을 live region을 사용해서 처리할 수 있음

- live region은 user가 view가 바뀐 것을 알아야 하는 경우에 대한 접근성 서비스를 가르킴

- 예를 들어서 패스워드 실패나 네트워크 에러일 때 유저에게 알려주는 것은 앱의 접근성을 더 높이는 방식임, 이 앱에선 submit button이 상태를 바꿨다고 알려주게 할 것임

- 이를 위해 아래의 속성을 설정함

```xml
android:accessibilityLiveRegion="polite"
```

- 여기서 다양한 옵션값을 설정할 수 있는데 이 기준은 변화가 얼마나 중요한지에 따라 다름, 만약 중요한 경우라면 유저를 interrupt 할 지 여부를 고름

- `assertive`의 경우 접근성 서비스가 view가 바뀌자마자 알려주고 말을 해 줌, `none`의 경우 바뀐 것에 말이 없고, `polite`의 경우 변화를 알려주지만 때를 기다림

- 그 다음 Fragment에서 `SnackBar` 작업이 끝난 후 아래와 같이 새로운 content 설명과 button의 text를 바꿈

```kotlin
viewModel.showSnackBarEvent.observe(this, Observer {
            if (it == true) { // Observed state is true.
                Snackbar.make(
                    activity!!.findViewById(android.R.id.content),
                    getString(R.string.application_submitted),
                    Snackbar.LENGTH_SHORT // How long to display the message.
                ).show()
                viewModel.doneShowingSnackbar()
                // live region 이후 설명과 text 바꿈
                binding.button.contentDescription = getString(R.string.submitted)
                binding.button.text = getString(R.string.done)
            }
        })
```

- 마지막으로 button의 속성을 건드림

### Use chips to filter regions
- `Chips`는 attribute, text, entity, action을 나타내는 compact element임, 그것은 유저에게 정보를 입력하고 선택을 하게하고 content를 필터링하고 action을 시작하게끔 할 수 있음

- `Chip` widget은 모든 layout과 draw logic을 포함하는 `ChipDrawable`로 감쌈, 추가 logic은 touch, mouse, keyboard, 접근성 탐색도 있음

- 기본 Chip과 close icon은 별도의 논리적 하위 view로 여겨지고 그 자체의 탐색 behavior와 상태를 포함함

- `Chip`은 drawable을 사용함, Android drawable은 image, shape, screen의 animation을 그리게 하고 그것은 고정 사이즈와 동적 사이즈를 가짐

- image를 drawable로 사용할 수 있음, 그리고 생각하는 무엇이든 vector drwaings을 통해서 그릴 수 있음, 그리고 resizable drawable인 9-patch drawable도 있음

- Drawable은 view가 아니기 때문에 `ConstraintLayout`에 바로 넣을 수 없음, `ImageView`에 넣어서 써야함

- 그리고 drawable을 text view나 button의 배경으로 제공할 수 있고 text 뒤의 background를 그릴 수 있음

- 이 chip에 대해서 background와 check mark는 각각 별개의 drawable로 처리할 것임 chip을 터치하면 ripple effect가 생성되고 이를 통해서 상태의 변화의 응답을 할 것임

- GDGs의 list에 대한 chip을 추가하여 선택될 때 상태가 변화게끔 처리할 것임, chip이라는 button을 row 배열로 추가하고 각각 버튼은 GDC list를 선택한 지역에 맞게 필터링하게 해줄 것임

- 버튼 선택시, 버튼은 자신의 배경을 바꾸고 check mark를 보여줄 것임

- 먼저 `HorizontalScrollView`에 `ChipGroup`을 추가하고 `singleLine`을 `true`로 설정되어서 수평으로 line을 스크롤 할 수 있게 처리하고 `singleSelection`을 `true`로 하여 한번에 하나의 chip만 선택할 수 있게끔 함

- 그리고 `Chip`에 대한 layout을 만듬, 오직 `Chip`요소만 담고 있음

```xml
<com.google.android.material.chip.Chip
        xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:tools="http://schemas.android.com/tools"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        style="@style/Widget.MaterialComponents.Chip.Choice"
        app:chipBackgroundColor="@color/selected_highlight"
        app:checkedIconVisible="true"
        tools:checked="true"/>
```

- 그리고 `color`에 `selected_highlight`를 만듬, 여기서 `<selector>`을 써서 서로 다른 상태에 서로 다른 색깔을 제공해줄 수 있음, 각각의 상태와 연관된 색깔은 `<item>`으로 인코딩 할 수 있음

- 각각 `item`을 아래와 같이 설정함

```xml
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:color="?attr/colorPrimaryVariant" android:state_selected="true"/>
    <item android:alpha="0.18" android:color="?attr/colorOnSurface" />
    

</selector>
```

- state list는 위에서 아래로 조건문처럼 적용되기 때문에, state가 매칭되는게 없으면 default를 적용함

- 그리고 이 Chip을 만들고 필터링하는 결과를 처리하기 위해서 Fragment에 적용함, ViewModel에서 값을 보고 확인을 해서 처리함

```kotlin
// view model의 변화에 의해 region의 list가 제공될 때 chip이 바로 재생성될 수 있도록 상태를 추가함
        viewModel.regionList.observe(viewLifecycleOwner, object: Observer<List<String>> {
            override fun onChanged(data: List<String>?) {
                // null 테스트함
               data ?: return
                // null 테스트 통과시 Chip group 만듬
                val chipGroup = binding.regionList
                val inflator = LayoutInflater.from(chipGroup.context)
                // Chip Group의 자식을 가져와서 맵핑함
                val children = data.map { regionName ->
                    val chip = inflator.inflate(R.layout.region, chipGroup, false) as Chip
                    chip.text = regionName
                    chip.tag = regionName
                    // chip 클릭시 state를 재설정하고 viewModel 함수를 호출해 filter에 따라서 결과를 fetch하게 처리함
                    chip.setOnCheckedChangeListener { button, isChecked ->
                        viewModel.onFilterChanged(button.tag as String, isChecked)
                    }
                    chip
                }
                // chip group의 view를 다 없애고 children으로 선택된 chip만으로 재구성함
                chipGroup.removeAllViews()

                for (chip in children) {
                    chipGroup.addView(chip)
                }
            }

        })
```

### Support night mode
- night mode는 앱이 dark theme을 쓸 수 있도록 함 만약 device가 night mode를 허용하게 세팅이 되었다면

- night mode에선 default light background가 dark로 바뀌고 다른 element 역시 그에 맞게 바뀜

- 이를 둘 다 쓰기 위해서 `DayNight`로 해야함, 그러면 Dark Mode도 지원함

- 그 다음 다크모드를 프로그래밍적으로 처리하기 위해 아래와 같이 `MainActivity`에 씀

```kotlin
AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
```

- 그 다음 dark theme으로 커스텀하기 위해서 `-night` 폴더에 `values-night`를 만듬, 이를 추가하고 night mode를 쓰면 여기에 설정된 값으로 색상이 바뀜