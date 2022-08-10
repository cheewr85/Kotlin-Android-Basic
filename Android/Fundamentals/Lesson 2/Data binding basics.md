### Use data binding to eliminate findViewById()
- `findViewById()`는 View를 참조해서 가져오는 것이지만, 매번 이렇게 View를 찾아오는 것은 간단한 앱에서는 괜찮지만 앱이 복잡해지면 상당히 비용이 드는 작업임

- 만약 View가 계층적으로 복잡하게 되어 있다면 매번 View를 불러오는데 있어서 매번 비용이 들면서 불러오는 것임

- 이러한 부분을 해결하기 위해서 `Binding`이라는 객체를 통해 모든 앱에 적용시켜서 쓸 수 있음, 이를 Data binding이라고 함

- 앱이 만들어질 때 한 번 binding 객체를 생성하면 View에 접근하고 다른 데이터여도 binding 객체를 통해서 접근할 수 있음

- 즉, 앞서 말한대로 계층적인 View를 순회하지 않고 데이터를 찾으려고 순회하지 않아도 됨

![one](/Android/img/nine.png)

- 이 Data binding을 사용함으로써 코드가 더 짧고 읽기 쉬워지고 `findViewById()`를 통한 것보다 수월하게 처리가능함

- 그리고 Data와 View가 철저하게 분리되어 있음, 그리고 Android System이 각 View에 대해서만 순회를 함 유저가 앱과 상호작용할 때 일어남

- 접근하고자 하는 앱에 대해서 타입 안정성을 얻을 수 있음

- data binding 사용을 위해서 `build.gradle (Module: app)` 단위의 아래의 코드 추가를 해야함

```kotlin
dataBinding {
        enabled = true
    }
```

- 위처럼해서 sync까지 완료됐다면 data binding을 쓰기 위해서 xml 레이아웃을 `<layout>`태그로 감싸줘야함

- 이렇게 하면 root class는 더 이상 ViewGroup이 아니고 ViewGroup가 View를 포함한 layout이 되는 것임

- 이 때 binding 객체가 layout과 그 안에 있는 View를 알 수 있게됨

```xml
<layout>
   <LinearLayout ... >
   ...
   </LinearLayout>
</layout>
```

- 그리고 layout 태그 안에 view properties를 추가해줘야함

```xml
<layout xmlns:android="http://schemas.android.com/apk/res/android"
   xmlns:app="http://schemas.android.com/apk/res-auto">
```

- 위와 같이 layout을 추가하고 에러가 없이 잘 실행됐다면 이를 binding 객체를 통해서 main activity의 view를 참조하고 access 할 수 있음

- `onCreate()` 최상위에 써두는데 이 변수는 `binding`으로 부르고 기본적으로 이름과 컴파일러가 불러오는 것은 layout file에 기반으로 불러와서 처리함 즉, `activity_main + Binding` 이런식의 조합으로

- 실제로는 아래와 같이 쓰임
```kotlin
private lateinit var binding: ActivityMainBinding
```

- 이번엔 `setContentView` 함수에 대해서 변경할 수 있음, 기존에는 직접 레이아웃 파일을 참조하였지만 이번에는 binding을 활용하여서 처리함

- MainActivity에 `activity_main`과 연관된 `DataBindingUtil` 클래스로부터 `setContentView()`를 사용함, `DataBindingUtil`은 View에 대한 일부 Data binding 설정을 처리함

- `setContentView()`는 아래와 같이 바꿀 수 있음

```kotlin
//        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
```

- 이제 여기서 `findViewById()`로 하는 모든 호출을 binding 객체로 바꿀 수 있음

- binding 객체가 만들어지면 컴파일러는 레이아웃에 있는 View의 Id로부터 binding 객체의 View의 이름을 형성함 이는 Camel case로 변환해서 나타남

- 예를 들어 `done_button`은 `doneButton`으로 binding 객체가 생긴다는 것임

- 그러므로 여기서 모든 `findViewById()`로 불러온 것을 바꿀 수 있음

- 가령 `findViewById<Button>(R.id.done_button)`을 `binding.doneButton`으로 바꿀 수 있음

### Use data binding to display data
- Data binding을 사용함으로써 data class는 바로 view를 이용할 수 있음

- 이런 것은 복잡한 케이스에서 더더욱 활용하기 좋음

- 원래는 `TextView`에서의 string 설정은 `string.xml`에서 불러왔지만 이를 data class에서 data를 참조하는 것으로 바꿀 수 있음

- `layout` 태그와 `LinearLayout` 태그 사이에 `data` 태그를 집어넣음 이 `data`태그는 여기서 View를 data와 연결을 함

- 여기서 데이터 클래스에 대한 참조를 하는 variable을 선언할 수 있음

- `MyName`이라는 데이터 클래스에 대해서 아래와 같이 `variable`태그를 통해 해당 데이터 클래스를 불러올 수 있음
```kotlin
package com.example.aboutme

data class MyName(var name: String = "", var nickname: String = "")
```
```xml
<data>
        <variable
            name="myName"
            type="com.example.aboutme.MyName" />
    </data>
```

- 이렇게 하면 이제 `@string/name`을 `@={}`로 대체할 수 있음 이는 `myName` 클래스에서 정의된 데이터를 바로 참조할 수 있는 것을 의미함

```xml
android:text="@={myName.name}"
```

- 즉, String 값을 직접써서 참조하는게 아닌 data 클래스에 값을 통해서 직접 그 data를 View에 연결하는 것임 이는 해당 data class에서 데이터를 통신해서 불러오던 변수를 만들어서 쓰던 어디선가 name이 생성되는 순간 그 값이 바로 View에 연결됨을 의미함

- 이를 구현하기 위해서 `MainActivity.kt`에서 `MyName` 데이터 클래스의 인수의 변수를 할당함

```kotlin
// myName 클래스 인스턴스 만들어 변수 할당
    private val myName: MyName = MyName("Aleks Haecky")
```

- 그러면 이 변수가 어떻게 생성되었던 어떻게든 어디든 생성된 변수에 대해서 `binding` 객체를 통해서 해당 View에 있는 data 태그에서 존재한 `myName`에 변수할당 가능

```kotlin
binding.myName = myName
```

- 동일하게 TextView에 대해서 아래와 같이 정의해서 쓸 수 있음
```xml
android:text="@={myName.nickname}"
```

- 위의 TextView에서 설정한 `myName.nickname`에 대해서도 MainActivity에서 myName 변수에 대해서 설정을 함
```kotlin
myName?.nickname = nicknameEdit.text.toString()
```

- 그리고 이 이후에 UI 역시 새로운 데이터로 갱신되어야 하는데 그러기 위해서 `invalidateAll()`을 추가하여 업데이트 하도록 함

```kotlin
binding.apply {
   myName?.nickname = nicknameEdit.text.toString()
   invalidateAll()
   ...
}
```