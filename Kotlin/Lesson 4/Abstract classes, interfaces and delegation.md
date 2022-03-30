### Compare abstract classes and interfaces
- 가끔은 몇 몇 클래스들 사이에 공유하는 공통된 함수는 properties를 정의하고 싶을 때가 있음, 코틀린에서는 2가지 방법을 제공하는데 `interface`와 `abstract class`임

- 이번 예시로는 `AquariumFish` 클래스에서 모든 fish의 공통인 properties를 만들고 모든 fish의 공통 행동을 `FishAction` 인터페이스에 정의할 것임

- abstract class나 interface나 인스턴스화를 시킬 수 없음 즉, 그 자체의 타입으로 객체 생성이 안됨

- Abstract class는 생성자를 가지고 있고 Interface는 생성자 로직이나 상태를 저장하는 것을 가질 수 없음

- 그리고 Abstract class는 기본이 `open`이어서 이 키워드를 써줄 필요 없음 대신 `abstract` 키워드를 써줌 이는 하위 클래스에 주어지면 반드시 implement 하라는 의미임

- 이는 아래와 같이 적용해서 쓸 수 있음 

```kotlin
package example.myapp

abstract class AquariumFish {
    abstract val color: String
}

class Shark: AquariumFish() {
    override val color = "gray"
}

class Plecostomus: AquariumFish() {
    override val color = "gold"
}
```

```kotlin
package example.myapp

fun makeFish() {
    val shark = Shark()
    val pleco = Plecostomus()

    println("Shark: ${shark.color}")
    println("Plecostomus: ${pleco.color}")
}

fun main () {
    makeFish()
}
```

- 즉, 도식화해서 생각하면 아래와 같은 구조로 활용된 것임

![one](/Kotlin/img/one.png)

- 인터페이스도 역시 아래와 같이 만들 수 있음

```kotlin
package example.myapp

abstract class AquariumFish {
    abstract val color: String
}

class Shark: AquariumFish(), FishAction {
    override val color = "gray"
    override fun eat() {
        println("hunt and eat fish")
    }
}

class Plecostomus: AquariumFish(), FishAction {
    override val color = "gold"
    override fun eat() {
        println("eat algae")
    }
}

interface FishAction {
    fun eat()
}
```

```kotlin
package example.myapp

fun makeFish() {
    val shark = Shark()
    val pleco = Plecostomus()
    println("Shark: ${shark.color}")
    shark.eat()
    println("Plecostomus: ${pleco.color}")
    pleco.eat()
}

fun main () {
    makeFish()
}
```

- 이 역시 도식화하면 아래와 같음

![one](/Kotlin/img/two.png)

- 여기서 둘 다 간단하고 연관된 클래스에 대해서 더 쉽게 유지보수하게끔 제공을 함 하지만 interface를 통해서 해당 클래스의 기능을 확장하고 모으는데 더 낫고 수월하지만 abstract class의 경우 상속과 오직 하나만 받아야 한다는 부분이 있다는 것을 염두해두면 좋음

--------

### Use interface delegation
- 이 인터페이스 위임 방식은 곧, 연관성 없는 클래스에서 인터페이스를 사용해서 필요한 기능을 추가하고 기능적으로 인스턴스를 활용해서 적용할 수 있음을 의미함

- 이를 아래와 같이 예시로 본다면 다음과 같음(즉, 상속이 아닌 인터페이스를 활용해서 정의를 )

```kotlin
package example.myapp

class Shark: FishColor, FishAction {
    override val color = "gray"
    override fun eat() {
        println("hunt and eat fish")
    }
}

class Plecostomus: FishColor, FishAction {
    override val color = "gold"
    override fun eat() {
        println("eat algae")
    }
}

interface FishAction {
    fun eat()
}

interface FishColor {
    val color: String
}
```

- 여기서 GoldColor의 경우 무조건 GoldColor인 `gold`인데 이를 여러 개의 인스턴스를 만들 필요가 없음 그래서 이를 오직 하나의 인스턴스만을 만들 수 있게 정의할 수 있는데 이는 `object`키워드를 사용하여서 싱글톤으로 만들 수 있음 이렇게 만들면 이 인스턴스는 오직 하나만 만들어지고 클래스에서 이 인스턴스를 참조할 때 해당 인스턴스만을 참조하는 것임

- 아래와 같이 쓸 수 있음

```kotlin
package example.myapp

class Plecostomus: FishAction,FishColor by GoldColor {
    override fun eat() {
        println("eat algae")
    }
}

interface FishAction {
    fun eat()
}

interface FishColor {
    val color: String
}

object GoldColor : FishColor {
    override val color = "gold"

}
```

- 여기서 이 값을 아예 생성자를 통해서 디폴트로도 정할 수 있음

```kotlin
package example.myapp

class Plecostomus(fishColor: FishColor = GoldColor): FishAction, FishColor by fishColor {
    override fun eat() {
        println("eat algae")
    }
}

interface FishAction {
    fun eat()
}

interface FishColor {
    val color: String
}

object GoldColor : FishColor {
    override val color = "gold"

}
```

- 그리고 FishAction에서도 똑같이 이를 적용할 수 있음 이는 기존의 `eat`함수를 인터페이스에 위임해서 파라미터만 넘겨주면 되는 것임

```kotlin
package example.myapp

class Shark: FishColor, FishAction {
    override val color = "gray"
    override fun eat() {
        println("hunt and eat fish")
    }
}

class Plecostomus(fishColor: FishColor = GoldColor): FishAction by PrintingFishAction("eat algae"), FishColor by fishColor {
    
}

interface FishAction {
    fun eat()
}

interface FishColor {
    val color: String
}

object GoldColor : FishColor {
    override val color = "gold"

}

class PrintingFishAction(val food: String) : FishAction {
    override fun eat() {
        println(food)
    }

}
```

- 이를 비교해보면 둘 다 위임을 하였지만 아래의 도식화와 같이 다른 차이가 있음

![one](/Kotlin/img/three.png)

- 이것은 추상 클래스를 사용해서 상속받고 처리하는 식으로 생각을 할 수 있지만 위와 같이 인터페이스 위임을 적극 활용하여서 많은 클래스를 상속받는 형태로 처리하는 것보다 더 쉽게 생각하는 바를 달성할 수 있음
