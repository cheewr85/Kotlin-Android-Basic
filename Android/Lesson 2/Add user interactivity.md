### Add an EditText for text input
- `TextView`의 하위 클래스인 `EditText`를 통해서 사용자가 text를 입력하고 수정하고 스크린에 보여주게 할 수 있음

### Style your EditText
- `inputType` 성질을 통해서 `EditText`에 입력할 타입에 대해서 구체화 시킬 수 있음, 그에 따라 적절한 키보드 역시 띄어

### Add a button and style it
- `Button`은 사용자가 누르는 행동을 취할 수 있고 text, icon, text와 icon 둘 다 있을 수 있음

- 여기서 현재 버튼 색깔이 변경이 안되는데 이는 테마가 `MaterialComponents`여서 그럼 이 테마는 Background를 자체적으로 갖고 있어서 변경되지 않는 것임

- 그래서 이 부분을 해결하기 위해서 AppCompatButton으로 바꿔주던가 Theme 부분을 `Theme.AppCompat`으로 바꿔주면 됨

### Add a TextView to display the nickname
- Visibility 설정으로 View를 안보이게 할 수 있는데 `Invisible`은 보이지 않지만 여전히 공간을 차지하는 것이고 gone은 아예 View를 안보이게 하면서 공간도 차지하지 않는 차이가 있음

### Add a click listener to the DONE button
- Button을 눌렀을 때 이벤트 처리를 하기 위해서 리스너를 만들 수 있음

- 여기서 그냥 버튼을 불러와서 `.setOnClickListener`를 하고 onClick을 오버라이딩해서 익명함수로 쓸 수 있는 방법이 있는데, 이는 코드가 지저분해질 수 있음

- 그래서 별도의 이벤트 처리 함수를 만들고 이를 xml에 추가하는 것 혹은 `setOnClickListener`로 추가할 수 있음

```xml
<Button
   android:id="@+id/done_button"
   android:text="@string/done"
  
   android:onClick="clickHandlerFunction"/>
```
```kotlin
myButton.setOnClickListener {
   clickHanderFunction(it)
}
```

### Add a click listener to the nickname TextView
- TextView에서도 이렇게 클릭 리스너를 달아서 이벤트 처리를 할 수 있음

