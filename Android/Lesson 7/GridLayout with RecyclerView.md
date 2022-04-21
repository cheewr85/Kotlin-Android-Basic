## Layouts and LayoutManagers
- 이전 챕터에서 `RecyclerView`에 `LinearLayoutManager`로 설정했음, 별도의 커스텀하지 않아서 vertical list로 나옴

- `LinearLayoutManager`는 매우 흔한 패턴이고 horizontal하게나 vertical하게 둘 다 지원이 가능함, `LinearLayoutManager`를 통해 horizontal하게 나타내게 할 수 있음

### GridLayout
- 또 하나로 사용자에게 많은 데이터를 보여주고 싶을 때 사용하는 것은 `GridLayout`을 사용함, `GridLayoutManager`로 설정하면 grid 형태로 스크롤 할 수 있게 데이터를 보여줌

![one](/Android/img/thirtythree.png)

- 디자인 관점에서 `GridLayout`은 사진이 있는 탐색앱 안에서 list로써 icon과 image를 나타내는데 최적의 디자인임

- 예시앱에서 이 icon을 grid 형태로 크게 보여줄 수 있음

### How GridLayout lays out items
- `GridLayout`은 items을 grid의 row와 column으로 배치함, vertical scrolling을 기본값으로 row에 있는 각 아이템을 하나의 span이라고 함

- 이따금씩 아이템이 여러개의 span을 차지하는 경우도 있음 아래의 사진과 같이 하나의 span은 하나의 column의 길이와 동등하게 가짐

- 처음 2개의 사진은 각 row는 3개의 span으로 이루어짐 기본으로 `GridLayoutManager`는 하나의 span의 하나의 아이템을 둠 그리고 span count에 도달하면 다음 줄로 감

- 기본으로 하나의 아이템은 하나의 span이지만 아이템을 더 크게 하여 span을 다 차지할 수 있음(3번째 사진)

![one](/Android/img/thirtyfour.png)

---------

### Implement GridLayout
- 이전에 `RecyclerView`에서 설정한 `layoutManager`를 삭제함

```xml
app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager
```

- 그리고 `SleepTrackerFragment.kt`에서 `onCreateView()`에서 `return` 이전에 `GridLayoutManager`를 생성함

```xml
// GridLayout 설정함
        val manager = GridLayoutManager(activity, 3, GridLayoutManager.VERTICAL, false)
        // 그리고 layoutManger를 위에서 설정한 GridLayout으로 바꿈
        binding.sleepList.layoutManager = manager
```

- 그리고 마무리로 `list_item_sleep_night.xml`에서 GridLayout에 맞게 item에 대한 view를 수정함

```xml
<ImageView
   android:id="@+id/quality_image"
   android:layout_width="@dimen/icon_size"
   android:layout_height="60dp"
   android:layout_marginTop="8dp"
   android:layout_marginBottom="8dp"
   app:layout_constraintBottom_toBottomOf="parent"
   app:layout_constraintEnd_toEndOf="parent"
   app:layout_constraintStart_toStartOf="parent"
   app:layout_constraintTop_toTopOf="parent"
   tools:srcCompat="@drawable/ic_sleep_5"
   app:sleepImage="@{sleep}"/>

<TextView
   android:id="@+id/quality_string"
   android:layout_width="0dp"
   android:layout_height="20dp"
   android:layout_marginEnd="16dp"
   android:textAlignment="center"
   app:layout_constraintBottom_toBottomOf="parent"
   app:layout_constraintEnd_toEndOf="parent"
   app:layout_constraintHorizontal_bias="0.0"
   app:layout_constraintStart_toStartOf="parent"
   app:layout_constraintTop_toBottomOf="@+id/quality_image"
   app:sleepQualityString="@{sleep}"
   tools:text="Excellent!"/>
``` 

- 마지막으로 `GridLayout`에 대해서 다양한 커스텀을 할 수 있음, span을 아래와 같이 1로 주던가, 혹은 10으로도 줄 수 있음

```kotlin
val manager = GridLayoutManager(activity, 1)
```

- 아니면 5로 제한하고 `Horizontal`로 바꿀 수도 있음

```kotlin
val manager = GridLayoutManager(activity, 5, GridLayoutManager.HORIZONTAL, false)
```

- 이처럼 커스터마이징이 용이함