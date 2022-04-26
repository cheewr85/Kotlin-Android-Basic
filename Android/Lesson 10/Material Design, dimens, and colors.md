### Add a floating action button(FAB)
- 유저가 FAB를 누를 때 GDG의 list를 볼 수 있게 상호작용할 것임

- 이 FAB를 추가하기 위해서는 기존의 layout에선 `ConstraintLayout`에 있게됨, 이러면 모든 content위의 floating 하는 상태가 아니라 그저 `ConstarintLayout`에 일부가 됨

- 이 때 `CoordinatorLayout`을 활용할 수 있음, `CoordinatorLayout`을 View를 서로 쌓을 수 있게 해주는 View Group임

- `ScrollView`가 전체화면을 차지해야 하고 FAB는 화면 하단 가장자리 근처에 떠 있어야 함

- 그리고 `ScrollView`는 `NestedScrollView`로 변경해야함, Coordinator layout이 scrolling을 알기 위해서 `NestedScrollView`를 사용해야 다른 view를 scroll 할 수 있게 처리할 수 있음

- 그 다음 `FloatingActionButton`을 `CoordinatorLayout`에서 `NestedScrollView`밑에 추가함

```xml
    <androidx.coordinatorlayout.widget.CoordinatorLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <androidx.core.widget.NestedScrollView
            android:layout_width="match_parent"
            android:layout_height="match_parent">

            <androidx.constraintlayout.widget.ConstraintLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:clipToPadding="true"
                android:paddingBottom="16dp"
                tools:context=".home.HomeFragment">

                <ImageView
                    android:id="@+id/image"
                    android:layout_width="0dp"
                    android:layout_height="0dp"
                    android:scaleType="centerCrop"
                    app:layout_constraintDimensionRatio="4:3"
                    app:layout_constraintEnd_toEndOf="parent"
                    app:layout_constraintStart_toStartOf="parent"
                    app:layout_constraintTop_toTopOf="parent"
                    app:srcCompat="@drawable/behind" />

                <TextView
                    android:id="@+id/title"
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="16dp"
                    android:fontFamily="@font/lobster_two"
                    android:text="@string/about_google_developer_groups"
                    android:textAppearance="@style/TextAppearance.Title"
                    app:layout_constraintEnd_toStartOf="@+id/end_guideline"
                    app:layout_constraintStart_toStartOf="@+id/start_guideline"
                    app:layout_constraintTop_toBottomOf="@id/image" />

                <TextView
                    android:id="@+id/intro_text"
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="8dp"
                    android:text="@string/gdg_description_long"
                    app:layout_constraintEnd_toStartOf="@+id/end_guideline"
                    app:layout_constraintStart_toStartOf="@+id/start_guideline"
                    app:layout_constraintTop_toBottomOf="@id/title" />

                <TextView
                    android:id="@+id/subtitle"
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="13dp"
                    android:text="@string/gdgs_are"
                    android:textAllCaps="true"
                    android:textAppearance="@style/TextAppearance.Subtitle"
                    android:textStyle="bold"
                    app:layout_constraintEnd_toStartOf="@id/end_guideline"
                    app:layout_constraintStart_toEndOf="@id/start_guideline"
                    app:layout_constraintTop_toBottomOf="@+id/intro_text" />

                <TextView
                    android:id="@+id/description"
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="8dp"
                    android:text="@string/gdg_description_bullets"
                    app:layout_constraintEnd_toStartOf="@+id/end_guideline"
                    app:layout_constraintStart_toStartOf="@+id/start_guideline"
                    app:layout_constraintTop_toBottomOf="@id/subtitle" />

                <ImageView
                    android:id="@+id/end_image"
                    android:layout_width="0dp"
                    android:layout_height="0dp"
                    android:layout_marginTop="16dp"
                    android:scaleType="centerCrop"
                    app:layout_constraintBottom_toBottomOf="parent"
                    app:layout_constraintDimensionRatio="3:1.5"
                    app:layout_constraintEnd_toEndOf="parent"
                    app:layout_constraintStart_toStartOf="parent"
                    app:layout_constraintTop_toBottomOf="@id/description"
                    app:srcCompat="@drawable/wtm" />

                <androidx.constraintlayout.widget.Guideline
                    android:id="@+id/start_guideline"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    app:layout_constraintGuide_begin="16dp" />

                <androidx.constraintlayout.widget.Guideline
                    android:id="@+id/end_guideline"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    app:layout_constraintGuide_end="26dp" />
            </androidx.constraintlayout.widget.ConstraintLayout>
        </androidx.core.widget.NestedScrollView>

        <com.google.android.material.floatingactionbutton.FloatingActionButton
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"/>
    </androidx.coordinatorlayout.widget.CoordinatorLayout>
```

- 그리고 FAB의 style을 추가해서 처리함

```xml
<com.google.android.material.floatingactionbutton.FloatingActionButton
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="bottom|end"
            android:layout_margin="16dp"
            app:srcCompat="@drawable/ic_gdg"
            />
```

- 그리고 이 FAB의 click listener 처리를 추가함

- 클릭 이벤트 처리를 위해 아래와 같이 data binding을 추가하고 viewModel을 연결함

```xml
<variable
   name="viewModel"
   type="com.example.android.gdgfinder.home.HomeViewModel"/>

   ....

android:onClick="@{() -> viewModel.onFabClicked()}"
```

- `onFabClicked`에선 navigation이 `LiveData`로 처리됨

- 그리고 `HomeFragment`에서 viewModel을 연결하고 그 안에서 설정한 `LiveData` 값 기준으로 observe도 추가함

```kotlin
        viewModel.navigateToSearch.observe(viewLifecycleOwner,
            Observer<Boolean> { navigate ->
                if(navigate) {
                    val navController = findNavController()
                    navController.navigate(R.id.action_homeFragment_to_gdgListFragment)
                    viewModel.onNavigatedToSearch()
               }
             })
```

### Use styling in a world of Material Design
- 대부분의 Material Design components 사용하기 위해서 theme attribute를 사용함

- Theme attributes는 app의 primary color 같은 다양한 styling 정보의 가리키는 변수임

- `MaterialComponents` theme을 위한 theme attribute요소를 특정 지음으로써 앱의 styling을 간편하게 할 수 있음

- color나 font에 대한 값은 모든 widget의 적용될 것이고 그래서 design과 branding의 일관성을 유지할 수 있음

- 이를 사용하기 위해 기존의 `@style/TextAppearance.Title`을 theme attribute 사용을 위한 `?attr/textAppearanceHeadline5`으로 변경함

```xml
<TextView
       android:id="@+id/title"
       style="?attr/textAppearanceHeadline5"
```

- `TextAppearance`는 theme보다 styling 속성에서 밑에 있음 `TextAppearance`는 text styling을 적용할 어떤 view에도 있는 속성임

- style과 같지 않지만, text가 어떻게 보일지에 대해서 정의를 함, Material Design components에 있는 text style 모두 `textAppearance`를 써야함, 그렇게 해야 정의한 theme attribute에 우선순위를 줄 수 있음(위의 처럼 style만 써버리면 font 반영이 안되어 있음)

- `subtitle`의 경우 `textAppearanceHeadline6`를 쓸 것인데 size가 20sp로 되어 있음 그래서 이를 `styles`에서 재정의해서 수정할 것임

```xml
<style name="TextAppearance.CustomHeadline6" parent="TextAppearance.MaterialComponents.Headline6">
        <item name="android:textSize">18sp</item>
        <item name="textAppearanceHeadline6">@style/TextAppearance.CustomHeadline6</item>
    </style>
```

- 그리고 `subtitle`에 반영함

### Change the toolbar theme
- toolbar만 다른 theme으로 바꾸고 싶을 수 있는데 이 때 theme overlay를 사용함

- `Theme`은 전체 앱의 전역 theme을 설정하는데 씀, `ThemeOverlay`는 toolbar 같은 특정 view의 theme를 오버라이딩 할 때 씀

- Theme overlays는 케이크 위에 아이싱을 하듯이 현재 존재하는 theme에 대해 overlay하기 위해 디자인 된 가벼운 theme임 

- 앱의 일부를 바꾸려고 할 때 유용하게 쓸 수 있음, 예를 들어 toolbar는 어둡게 하지만 나머지 screen은 밝게 하는 것과 같이

- theme overlay를 view에 적용하고 overlay는 해당 view에 적용되고 그 자식들에게도 적용이 됨, 이렇게 적용하기 위해서 원하는 theme을 view 계층구조에서 root view에 적용해야함

- 여기서 계층구조에 있는 view에서 overlay theme으로 정의한 특정 속성을 사용하기 위해서는 view의 attribute의 set을 해줘야함 `?attr/valuename`으로

- 실제 적용을 해 볼 것

- `MaterialComponents` theme에는 light screen의 dark toolbar 옵션이 없음, 이 때 theme에서 toolbar만 바꿀 수 있음, toolbar의 `Dark` theme을 적용시키는 것

- 이것은 `MaterialComponents`에서 toolbar를 overlay 하는 것으로 가능함

- 아래와 같이 `Toolbar`에 style과 background를 줘서 수정함, `ThemeOverlay`이므로 수정 가능

```xml
<androidx.appcompat.widget.Toolbar
                android:id="@+id/toolbar"
                android:layout_width="match_parent"
                android:layout_height="?attr/actionBarSize"
                android:background="?attr/colorPrimaryDark"
                android:theme="@style/ThemeOverlay.MaterialComponents.Dark.ActionBar">
```

- 여기서 추가로 그 안의 이미지가 이제 text가 눈에 띄지 않음, 여기서 새로운 이미지를 만들거나 새로운 이미지를 만들지 않고 `ImageView`의 tint를 설정할 수 있음

- `tint` 설정시 `ImageView` 전체가 특정 색깔로 tinted 됨 즉, 그에 맞게 새로운 이미지가 아니라 색깔을 맞출 수 있음

- 그래서 아래와 같이 `ImageView`는 좀 더 잘 보이게 밝게 세팅할 수가 있음
```xml
android:tint="?attr/colorOnPrimary"
```

### Use dimensions
- 잘 짜여진 앱의 경우, 일관된 느낌과 보기를 제공함, 모든 화면에 같은 색감과 비슷한 레이아웃을 볼 수 있음

- 이것이 앱이 예쁘게 보이지 않을 수도 있지만, 유저가 screen에 이해하고 상호작용하는데 더 쉽게 해 줄 수 있음

- Dimens는 앱을 위해 재사용 가능한 측정 도구 중 하나임, margin, height, padding은 dp를 쓰고 font size는 sp를 씀

- 여기서 일관된 margin을 정하기 위해 `dimen`을 정의할 것임

- 16dp를 `@dimen` 값에 추출해서 아래와 같이 동일하게 적용시킬 수 있음, Guideline의 값이 다 다르기 때문에

```xml
<androidx.constraintlayout.widget.Guideline
       android:id="@+id/end_grid"
       app:layout_constraintGuide_end="@dimen/spacing_normal"
```

- 그 외에 16dp로 설정된 값을 모두 다 위의 값으로 변경 가능함

### Use colors
- https://material.io/tools/color/

- 위의 사이트에서 컴포넌트 별로 색깔 배합을 볼 수 있음, 그리고 이를 Export해서 `colors.xml`로 다운받아서 쓸 수 있음

- 그러면 그걸 그대로 프로젝트에 저장하고 `styles.xml`에서 `AppTheme`을 아래와 같이 color를 추가하고 수정할 수 있음

```xml
<item name="colorPrimary">@color/primaryColor</item>
<item name="colorPrimaryDark">@color/primaryDarkColor</item>
<item name="colorPrimaryVariant">@color/primaryLightColor</item>
<item name="colorOnPrimary">@color/primaryTextColor</item>
<item name="colorSecondary">@color/secondaryColor</item>
<item name="colorSecondaryVariant">@color/secondaryDarkColor</item>
<item name="colorOnSecondary">@color/secondaryTextColor</item>
```

- 그리고 `ImageView`에 대해서 `colorOnSecondary`로 수정함

```xml
<ImageView
                    android:id="@+id/hero_image"
                    android:layout_width="150dp"
                    android:layout_height="match_parent"
                    android:contentDescription="@string/app_name"
                    app:srcCompat="@drawable/logo"
                    app:tint="?attr/colorOnSecondary" />
```