- MotionLayout은 ConstraintLayout을 기반으로 함

- 이를 통해 다양한 animate를 진행할 수 있음, 이미 선언한 XML과 ConstraintLayout을 기반으로 진행이 가능함

- 이를 좀 더 자세히 보면 아래와 같음

   - UI에서 유저가 변화하는 것을 자연스럽게 볼 수 있음, animating의 state 사이를 볼 수 있음

   - 중요한 UI 요소에 대해서 animations을 통해 attention을 그릴 수 있음

   - 좀 더 효과적은 motion 처리를 할 수 있음

### Creating animations with MotionLayout
- `MotionLayout`은 `ConstraintLayout`의 하위 클래스임, 그래서 `MotionLayout` tag에서 모든 view를 특정해서 animation을 설정할 수 있음

- `MotionScene`은 `MotionLayout`의 animation을 설명하는 XML 파일임

- `Transition`은 `MotionScene`의 일부로써 animation duration과 trigger와 view를 어떻게 움직일지에 대한 내용이 들어있음

- `ConstraintSet`은 transition의 constraints의 start와 end를 정의함

- `MotionLayout`을 추가하기 위해 기존의 `ConstraintLayout`에서 추가할 수 있음

- xml 파일 상에서 design 부분에서 `Convert to MotionLayout`을 누른다면 기존의 XML이 아래와 같이 `MotionLayout`으로 변환됨

```xml
<!-- initial code -->
<androidx.constraintlayout.widget.ConstraintLayout
       ...
       android:layout_width="match_parent"
       android:layout_height="match_parent"
       >

   <ImageView
           android:id="@+id/red_star"
           ...
   />

</androidx.constraintlayout.motion.widget.MotionLayout>
```
```xml
<!-- explore motion:layoutDescription="@xml/activity_step1_scene" -->
<androidx.constraintlayout.motion.widget.MotionLayout
       ...
       motion:layoutDescription="@xml/activity_step1_scene">
```

- 여기서 motion scene이 `MotionLayout`에서 animation을 가르키는 부분임, 그리고 xml에서 design을 설정하듯이 Motion Editor가 존재함

![MotionEditor](https://codelabs.developers.google.com/codelabs/motion-layout?hl=ko#2)

- 위의 링크에서 들어가면 볼 수 있듯이 Motion Editor를 통해서 `start`와 `end`사이의 transition을 선택하거나 `ConstraintSet`에서의 `start`를 설정하는 등 볼 수 있고 선택한 `ConstraintSet`과 해당 속성을 볼 수 있음

### 일시정지
- 현재 튜토리얼 상태가 원활하지 못함, 내용적인 이해도는 튜토리얼 참고하면 좋긴함

![CodeLab](https://codelabs.developers.google.com/codelabs/motion-layout?hl=ko#2)

- 버전도 맞지 않고 읽는데 계속 실습도 안되고 이전 프로젝트를 리뷰하고 정리한다 생각하면 좋을 것 같아서 따로 씀

- 결국 MotionLayout은 ConstraintLayout 기반으로 다양한 Animation을 적용하는데 이를 Motion Editor로 더 직관적으로 볼 수 있게 하고 설정도 직접 간단하게 할 수 있게함, 굳이 앞서 한 방식과 같이 Animation 설정을 할 것이 아니라

- 그래서 이를 클릭시 움직이게끔 그리고 drag해서도 움직이게끔 철저하게 MotionScence에서 설정한대로 transition과 UI와 등등을 처리할 수 있게함

- 그리고 path역시 KeyPosition 등으로 설정하고 복합적인 효과를 넣어서 처리할 수 있음

- 이를 통해 다양한 경로설정 뿐 아니라 다양한 효과 즉 앞서 한 animation도 추가적으로 줄 수 있음

![공식문서](https://developer.android.com/training/constraint-layout?hl=ko)

- 위의 문서를 참조해서 재정리 필요

![예제 프로젝트](https://github.com/cheewr85/SampleProject/tree/master/UpperIntermediate/OTT)

- 실제 다양한 실습은 직접 위의 프로젝트처럼 다 해봤으므로 복습하면서 문서 참조