### Add navigation components to the project
- Navigation component를 사용하기 위해서 `build.gradle`에서 의존성을 추가해줘야함

- [공식문서](https://developer.android.com/guide/navigation/navigation-getting-started?hl=ko)

- 그리고 navigation에 대해서 쭉 만들면 됨

### Create the NavHostFragment
- 단순하게 xml만 파일을 만들면 아무것도 나타나지 않는데 여기서 navigation graph의 host로써의 fragment를 설정해줘야함(주로 `NavHostFragment`로 지음)

- 유저가 정의된 Navigation을 통해 이동할 때 navigation host fragment가 필요한 fragment를 swap을 해 줌

- 그리고 Fragment는 적절한 Fragment 백스택을 관리하고 만듬

### Add fragments to the navigation graph
- Navigation Graph에 Fragment를 서로 연결해서 처리할 수 있음

- 그리고 Navigation Editor 내부에서 아래와 같이 Fragment를 추가할 수도 그리고 서로 연결해서 진행할 수도 있음

![one](/Android/img/ten.png)

### Add conditional navigation
- conditional navigation의 경우, 유저가 특정 context에서만 이용가능하도록 하는 navigation임

- 이 부분은 유저가 로그인했는지에 따라서 다른 flow가 생길 때 사용함

- 예시 앱은 유저각 정답을 맞췄냐에 따라서 navigation을 다르게 할 것임

- 문제를 푸는 로직으로 이미 구현되어 있고 여기서 이에 맞춰서 틀리면 게임 오버 화면이 그리고 맞으면 바로 게임 성공 화면이 나옴

- 여기서 Back 버튼을 누르면 어떤 상태던지간에 질문 Fragment로 돌아감 왜냐하면 애초에 그 쪽으로 연결을 설정했으므로 바로 그 쪽으로 돌아가는 것임(이상적인 것은 타이틀 화면으로 돌아가야함)

### Change the Back button's destination
- 안드로이드 시스템은 유저가 어디 상태에 있는지 계속 확인함

- 그래서 안드로이드에 있는 기본 뒤로가기 버튼을 누르면 백스택에 있는 최상단에 위치로 넘어감

- 기본적으로 이 백스택의 최상단은 유저가 최근 본 화면으로 넘어감, 하지만 현재 예시앱에선 navigation이 이 부분을 관리함

- 즉, 예시앱에선 `GameOverFragment`, `GameWonFragment` 화면에서 뒤로가기 버튼을 누른다면, `GameFragment`로 돌아가게됨, 하지만 여기서 이 백스택의 action을 수정하여 `TitleFragment`로 가게 할 수 있음

- 이 백스택 관리를 연결된 프래그먼트에서 `pop behavior`을 설정함으로써 조정할 수 있음

- `popUpTo`을 통해서 주어진 위치에 연결되어서 갈 수 있음 백스택에만 존재한다면 이 연결을 이어서 설정할 수 있음

- `popUpToInclusive`가 `false`라면 해당 위치로 가지만 백스택은 그대로 남겨두고 `true`를 한다면 해당 위치로 가고 백스택을 다 제거를 함

- 그래서 `popUpToInclusive`를 `true`로 두고 `popUpTo`를 시작점으로 설정하면 뒤로가기 버튼을 누르면 백스택이 초기화되고 다시 시작점으로 돌아오게 설정할 수 있음

- `popUpToInclusive`를 `true`로 두고 예시앱을 쓰면 `popUpTo`가 `GameFragment`로 되어 있지만 `true`로 설정되서 백스택이 다 사라지므로 `TitleFragment`가 나오게 됨

- 그리고 예시 앱에서 실패와 성공시 버튼이 있는데 이 부분의 Navigation을 `GameFragment`로 둠, 그리고 `popUpTo`를 `titleFragment`로 하고 `false`로 둠, 백스택에선 남아있어야 하므로, 그리고 버튼 리스너 등록을 함

### Add an Up button in the app bar
- app bar는 action bar라고도 부르는데 앱의 브랜딩과 아이덴티티를 보여줌

- app bar의 색깔도 설정할 수 있고 또 option menu 같은 친숙한 navigation에 접근하게 해 줌, 이 부분은 흔히 안드로이드에 기본으로 보이는 옵션 버튼임

- 이 app bar에 대해서 string이 각 화면마다 다르게 표현하게 할 수 있음

- 그리고 기존에는 안드로이드 시스템에서 인식하는 뒤로가기 버튼을 활용했지만 on-screen Up button 역시 나타낼 수 있음(타이틀 화면 제외하고)

- Up button은 화면사이의 계층적 구조에 따라서 형성됨, 앱을 벗어나게끔 유도되진 않음

- 이 Up Button은 `NavigationUI` 라이브러리에서 사용할 수 있음 여기에는 top app bar, navigation drawer, bottom navigation등이 포함되어 있음

- 이 부분은 Fragment를 가지고 있는 Activity에서 `onCreate`에서 navigation controller 객체를 불러오고 app bar에 아래와 같이 연결한 뒤
```kotlin
val navController = this.findNavController(R.id.myNavHostFragment)
NavigationUI.setupActionBarWithNavController(this, navController)
```

- 그리고 `onSupportNavigateUp`을 오버라이딩해서 아래의 함수를 불러 return하면 Up Button이 생김

```kotlin
override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return navController.navigateUp()
    }
```

### Add an options menu
- 다양한 타입의 메뉴가 존재하는데 그 중 전형적인 Options menu를 씀, 이 역시 Fragment로 처리할 수 있음

- Menu Resource Type을 통해서 Navigation처럼 XML을 꾸밀 수 있음

![one](/Android/img/eleven.png)

- 여기서 코드로 Option Menu를 추가해줄 수 있음

### Add the navigation drawer
- Navigation Drawer는 스크린의 가장자리에서 슬라이드하듯이 들어오는 곳임, 여기엔 주로 헤더랑 메뉴가 달림

- 이 Navigation Drawer는 평상시에는 숨겨져 있지만 유저가 스와이프 하거나 버튼을 눌렀을 경우 나타나게됨

- 예시에서의 Navigation Drawer는 2개의 메뉴 아이템을 가지고 있음, 이 컴포넌트는 Material 라이브러리에 있음

- Drawer 역시 Menu Resource File로 만듬, 그리고 options menu처럼 menu item을 2개 추가함 각각 About과 Rule에 관한것임

- 이 Drawable을 쓰기 위해서 아래와 같이 최상단의 위치해줘야함 그리고 아래와 같이 만든 DrawerMenu를 추가해서 쓸 수 있음

```xml
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">

    <androidx.drawerlayout.widget.DrawerLayout
        android:id="@+id/drawerLayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:orientation="vertical">

            <!--이전에 Fragment를 직접 불러왔지만 NavHostFragment로 변경함-->
            <!--여기서 res폴더의 navigation.xml도 연결해줌-->
            <!--그리고 디폴트 nav로 설정해서 뒤로가기 버튼시 반응함-->
            <fragment
                android:id="@+id/myNavHostFragment"
                android:name="androidx.navigation.fragment.NavHostFragment"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                app:navGraph="@navigation/navigation"
                app:defaultNavHost="true"/>


        </LinearLayout>
        <!--앞서 만든 navdrawer를 사용하기 위해 해당 메뉴 xml 추가-->
        <com.google.android.material.navigation.NavigationView
            android:id="@+id/navView"
            android:layout_width="wrap_content"
            android:layout_height="match_parent"
            android:layout_gravity="start"
            app:headerLayout="@layout/nav_header"
            app:menu="@menu/navdrawer_menu"/>
    </androidx.drawerlayout.widget.DrawerLayout>

</layout>

```

