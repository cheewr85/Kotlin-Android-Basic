### Explore the activity and layout files
- `AppCompatActivity`는 `Activity`의 하위 클래스로 안드로이드 기능의 모든 부분을 지원하는 클래스임, 많은 수의 기계와 유저가 사용하길 원한다면 항상 `AppCompatActivity`를 사용해야함

- Activity에선 생성자를 통해 객체를 초기화 하는 방식 대신, 미리 선언된 메소드 즉, 라이프사이클에 맞는 생명주기 메소드에서 Activity를 설정함

- `onCreate`에선 액티비티와 관련된 레이아웃을 연결하고 inflate함

- `setContentView`에서 레이아웃 레퍼런스를 integer 레퍼런스로 불러옴, `R`클래스는 앱의 모든 자산을 포함함, 요소들을(res 디렉토리에 있는것을 포함해서)

- 처음 세팅이 된 `R.layout.activity_main`의 경우, `R`클래스를 불러 `layout`폴더에 `activity_main.xml`의 레이아웃 파일을 참조하는 것임

- 이외에도 `R`클래스를 통해서 image, string, layout file element등 다양한 요소를 참조할 수 있음

- `ViewGroup`은 다른 `View`를 가지고 있으면서 화면에 `View`의 위치를 정할 수 있는 container 역할을 함

- 모든 `View`와 `ViewGroup`은 레이아웃 등에 `View`를 계층적으로 구성할 수 있음, 최상위 `View`에선 `View`와 `View Group`을 담을 수 있음

### Add a button
- Context 객체는 현재 Android OS 상태에 대한 정보를 가져와 통신할 수 있도록 해주는 객체임

- 그래서 `this`를 써서 현재 액티비티에 대해서 통신을 해서 Toast 메시지를 띄워주는 것임