### Add a fragment
- Fragment는 Activity에서 UI의 일부를 담당하거나 특정 행동을 나타낼 수 있음

- 하나의 Activity에 여러개의 Fragment를 묶어서 여러창의 UI를 만들 수 있고 다양한 Activity에서 하나의 Fragment를 재사용할 수 있음

- Fragment는 마치 sub-activity 같은 역할을 함

- Fragment는 그 자체의 생명주기와 input 이벤트를 받을 수 있고 Activity가 실행중일 때 Fragment를 삭제하고 추가할 수 있으며 Kotlin 클래스로 정의하고 UI 역시 XML 레이아웃 파일로 사용 가능함

- 여기서 Fragment를 생성할 때 그냥 기본 `Blank` Fragment를 생성하고 `onCreateView`만 남겨두고 나머지는 다 삭제한 상태로 내가 원하는대로 커스텀 할 수 있음