## Benefits of Kotlin
- 코틀린의 특징으론 명료함, 간결함, 코드 안정성을 꼽을 수 있음

### Robust Code
- 프로그래머로 하여금 이런 특징을 확실하게 하기 위해서 읽기 쉽고 효율적이고 유용하고 컨트롤하기 수월하게끔 하게 짤 수 있음

- 대표적으로 nullable과 non-nullable 데이터 타입을 구분하고, 타입에 강하게 결속되어 있고, 람다식, 코루틴, properties등 더 적은 코드에서 더 적은 버그가 생기게 도와줌

### Concise, readable Code
- 매우 간결하고 getter, setter같은 부분도 제거하고 쓸 수 있게 설계됨

- 예를 들어 자바 코드에서 아래와 같은 코드가
```java
public class Aquarium {

   private int mTemperature;

   public Aquarium() { }

   public int getTemperature() {
       return mTemperature;
   }

   public void setTemperature(int mTemperature) {
       this.mTemperature = mTemperature;
   }

   @Override
   public String toString() {
       return "Aquarium{" +
               "mTemperature=" + mTemperature +
               '}';
   }
}
```

- 코틀린에선 아래와 같이 간결하게 쓸 수 있음
```kotlin
data class Aquarium (var temperature: Int = 0)
```

- 막연하게 이런 간결성을 추구하는 것이 아닌 어느정도 충분한 수준의 읽을 수 있을 수준은 유지하면서 간결성을 유지함

### Interoperatable with Java
- 자바와 코틀린 코드 자체가 같이 컴파일이 가능하고 자바 라이브러리 사용도 유지할 수 있음

- 그리고 지금 존재하는 자바 프로그램에 코틀린을 추가가능함

- 자바에서 코틀린으로 마이그레이션 하고 싶을 때 이를 변환해주는 툴도 존재함
