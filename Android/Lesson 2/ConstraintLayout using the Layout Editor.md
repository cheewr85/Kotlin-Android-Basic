### Constraint
- `ConstraintLayout`은 자식 View에 대해서 위치와 크기에 대해서 유연하게 제공해줌

- 이런 방식은 중첩된 ViewGroup으로 설계 하는 것보다 단일하게 크고 복잡한 구조를 수월하게 처리할 수 있음

- `Constraint`는 UI 요소들 사이에 연결 혹은 정렬을 할 수 있게 해줌

- 이 Constraint는 하나, 그 이상의 View 혹은 부모 레이아웃, 보이지 않는 가이드라인에 연결되고 정렬될 수 있음

- 이는 constraint를 하나 이상의 Vertical 혹은 Horizontal constarint를 정의해서 위치를 정할 수 있음

### Use Layout Editor to build a ConstraintLayout
- Autoconnect 옵션을 통해서 Constarint를 수동 혹은 자동으로 생기게 설정할 수 있음

- View Inspector는 ConstraintLayout 인 경우만 보여지는데 여기서 constraint와 관련된 속성을 보고 조정할 수 있음

- Constraint bias는 horizontal과 vertical 축에 따라서 View를 배치하는 것임, 이를 조절해서 축에 따라서 위치를 변경할 수 있음

- 그리고 margin도 추가해서 constraint 기준으로 설정할 수 있음

- 뿐만 아니라 constraint를 통해서 Wrap Content, Fixed, Match Constraint 등 constraint 기준으로 View를 확장하고 고정하고 조절할 수 있음

### Style the TextView
- 이전에 했던 font 추가와 Text를 style을 뽑아서 처리하는 것을 똑같이 시행

### Add a second TextView and add constraints
- TextView를 추가하고 Constraint를 BoxOne에 맞춰서 설정해서 위치시킴

### Create a chain of TextView views
- vertical하게 horizontal하게 정렬을 할 수 있음 chain으로 묶어서

- `Chain`은 constraint가 서로 상호적으로 각각 link되어 있는 것을 의미함, chain에 있는 View들은 서로 vertical하게 horizontal하게 나타날 수 있음, 아래와 같이

![one](/Android/img/three.png)

- Chain에서 첫번째 View를 `head of the chain`이라고 함, 이 chain에 따라서 위치와 control등이 분산되서 적용될 수 있음

![one](/Android/img/four.png)

- `Chain Style`도 정할 수 있는데, 이는 Chain된 View들이 서로 퍼져 있거나 정렬되어 있거나 나타나 질 수 있음

- 이는 chain style을 추가하거나 weight를 추가하거나 view의 bias를 줄 수 있음

- `Spread`는 View들이 각각 마진을 고려하여 이용 가능한 공간에 할당된 기본값임

![one](/Android/img/five.png)

- `Spread inside`는 첫번째와 마지막 View가 chain 끝에 parent에 붙어있는 형태임 남은 View는 사용가능한 공간에 고르게 분포됨

![one](/Android/img/six.png)

- `Packed`는 View들이 마진을 고려하여 함께 묶여있는 것임, 이 chain 전체를 bias를 줌으로써 위치를 조정할 수 있음(head view를 조절함으로써 bias에 따라 조정가능함)

![one](/Android/img/seven.png)

- `Weighted`는 View들은 `layout_constraintHorizontal_weight`나 `layout_constraintVertical_weight`의 요소로 모든 공간을 차지하게 조정될 수 있음

- 아래와 같이 A는 weight 1, B,C는 weight 2 A의 두 배의 공간을 할당하게 처리할 수 있음

![one](/Android/img/eight.png)

- 위의 chain style은 어떻게든 추가해서 처리할 수 있음

### Add clickHandlers to the text views
- 모든 view들은 resource ID가 존재함, view의 id를 담는 것임

### Add a baseline constraint
- Baseline Constraint는 View의 text에 대한 baseline을 다른 View의 text에 대한 baseline으로 정렬함

- 만약 폰트 사이즈가 다르면 text를 포함한 View를 정렬하는데 어려움이 있을 수 있음 이때 Baseline constraint를 유용하게 쓸 수 있음

- 이것을 직접 설정하거나 혹은 `layout_constraintBaseline_toBaselineOf`로 조정할 수 있음

- xml 상에서 `tools`로 써 있는 설정은 실제 앱을 실행하면 무시되는 속성들임, Constraint 설정을 하지 않으면 `tools:layout_editor_absoluteY`등이 나타나는데 이는 해당 View의 위치를 잡아주는 것임(Constraint가 없더라도 위치를 보고 View를 보기 위해서)

