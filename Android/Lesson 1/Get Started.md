### Explore Android Studio
- `app-java` 폴더 하위의 domain package 명으로 3개의 하위폴더가 존재하는데 이는 app package에 있는 모든 파일을 포함함

- 그리고 `androidTest`와 `test`라고 써 있는 폴더는 Unit Test와 같은 테스트를 위한 폴더임

- `generatedJava` 폴더의 경우, 앱을 빌드할 경우, 안드로이드 스튜디오가 생성하는 파일을 가지고 있음, 해당 폴더에 있는 내용을 건드리면, 앱을 rebuild 하는 상황에서 오버라이딩되어서 세팅이 바뀔 수 있음, 하지만 이 폴더의 경우 디버깅할 경우 볼 필요가 있을 수 있음

- `manifest`의 경우, 앱에 대한 중요한 정보를 Android System에 알려줌, 그리고 앱이 필요로 하는 권한에 대해서도 정의를 해 둠

### Explore the Gradle Scripts folder
- Gradle의 경우, 앱의 프로젝트 구조, configuration, dependecies 같은 것을 빌드 자동화 처리하는 시스템임

- 컴파일하고 앱을 실행하는 순간, Gradle build에 대한 정보를 볼 수 있고 APK가 설치된 것에 대한 정보를 볼 수 있음

- APK는 설치한 모바일앱과 구분하기 위해 Android OS가 사용하는 패키지 파일 포맷임

- `build.gradle(Project: Project Name)`에서는 project를 구성하는데 있어서 필요한 모든 공통의 모듈에 대한 configuration options이 들어가 있음, 이 파일에서는 Gradle repositories와 모든 프로젝트 모듈의 공통된 dependencies를 정의함, 각각의 프로젝트는 고유의 Gradle build file이 존재함

- `build.gradle(Module:app)`에서는 각각의 모듈에 대해서 해당 파일이 존재함, 이 모듈 레벨에서는 각각의 모듈에 대한 빌드 세팅을 구성할 수 있게 해 줌, 여기선 app-level build configuration을 변경할 때 자주 쓰게됨, 예를 들어 SDK level을 바꾼다던지 새로운 dependencies를 추가하던지 할 때 활용을 함

### Summary
- 프로젝트에 새로운 dependencies를 추가하거나 dependency 버전을 변경할 때 `build.gradle(Module:app)`파일에서 변경할 수 있음

- feature, components, permissions을 추가할 땐 `AndroidManifest.xml`에서 추가할 수 있음