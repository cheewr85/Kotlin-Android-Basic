### Learn about visibility modifiers
- `public`, `private` 같은 부분을 볼 수 없는데 코틀린은 기본적으로 `public`이기 때문임 이는 즉 클래스, 메소드, 멤버, Properties 등 어디서든 접근할 수 있다는 것을 의미함

- 기본적으로 알고 있는 모든 요소들이 아래와 같은 modifiers로 정할 수 있음

   - `public`은 어디서든 접근할 수 있는 클래스 외부에서도 접근가능함을 의미함, 기본 디폴트로 `public`으로 되어있음

   - `internal`은 오직 `module`안에서만 보이는 것을 의미함 `module`은 코틀린 파일들의 집합으로 컴파일이 같이됨(라이브러리나 application 같이)

   - `private`는 클래스 내에서만 보이는 것을 의미함(이를 사용하기 위해선 함수를 통해서 작업해야함)

   - `protected`는 `private`과 비슷하지만 하위 클래스에서 보일 수 있음(상속받은)

- 멤버나 클래스 안에서의 Properties들은 기본적으로 `public`임 만약 `var`로 정의하면 변경 가능하고 읽고 쓰기가 가능하고 `val`을 썼다면 초기화 후 오직 읽기만 가능함

- 만약 property를 읽고 쓸 수 있지만 외부에선 오직 읽게만 하고 싶다면 getter를 `public`으로 두고 setter를 `private`으로 둘 수 있음

```kotlin
var volume: Int
    get() = width * height * length / 1000
    private set(value) {
        height = (value * 1000) / (width * length)
    }
```

------

### Learn about subclasses and inheritance
- 상속받고 하위 클래스 개념 역시 다른 언어와 유사하지만 코틀린은 살짝 다른 부분이 있는데 기본적으로 하위 클래스가 될 수 없음 즉, properties, 멤버 변수가 하위 클래스로 오버라이딩 될 수 없는 것을 의미함

- 이를 허용하기 위해서 반드시 `open`키워드를 사용해서 오버라이딩 될 수 있게 처리해야함, 이 키워드는 클래스 인터페이스의 세부사항을 실수로 적용시켜 노출되는 것을 방지함

- 아래와 같이 쓰고 활용할 수 있음

```kotlin
package example.myapp

open class Aquarium(open var length: Int = 100, open var width: Int = 20, open var height: Int = 40) {

    init{
        println("aquarium initializing")
    }

    open var volume: Int
        get() = width * height * length / 1000  // 1000 cm^3 = 1 l
        set(value) {
            height = (value * 1000) / (width * length)
        }

    open val shape = "rectangle";

    open var water: Double = 0.0
        get() = volume * 0.9

    constructor(numberOfFish: Int) : this() {
        // 2,000 cm^3 per fish + extra room so water doesn't spill
        val tank = numberOfFish * 2000 * 1.1

        // calculate the height needed
        height = (tank / (length * width)).toInt()
    }


    fun printSize() {
        println(shape)
        println("Width: $width cm " +
                "Length: $length cm " +
                "Height: $height cm ")
        // 1 l = 1000 cm^3
        println("Volume: $volume 1 Water: $water l (${water/volume*100.0}% full")
    }

}


```

```kotlin
package example.myapp

fun buildAquarium() {
    val aquarium6 = Aquarium(length = 25, width = 25, height = 40)
    aquarium6.printSize()

}

fun main() {
    buildAquarium()
}
```

- 그리고 이를 활용하여 하위 클래스는 만들 수 있는데 `TowerTank`는 `Aquarium`을 상속받고 해당 클래스만의 생성자 파라미터를 별도로 정의해줘야함, 여기서 `height`를 오버라이딩 하고 `diamter`가 추가됨

- 아래와 같이 만들 수 있음(여기서 포인트는 오버라이딩 하면 해당 형태를 getter,setter 등 그대로 가져와서 적용한 부분도 있지만 그 값 자체를 다시 재정의해서 활용한 부분도 있음)

```kotlin
package example.myapp

import java.lang.Math.PI

open class Aquarium(open var length: Int = 100, open var width: Int = 20, open var height: Int = 40) {

    init{
        println("aquarium initializing")
    }

    open var volume: Int
        get() = width * height * length / 1000  // 1000 cm^3 = 1 l
        set(value) {
            height = (value * 1000) / (width * length)
        }

    open val shape = "rectangle";

    open var water: Double = 0.0
        get() = volume * 0.9

    constructor(numberOfFish: Int) : this() {
        // 2,000 cm^3 per fish + extra room so water doesn't spill
        val tank = numberOfFish * 2000 * 1.1

        // calculate the height needed
        height = (tank / (length * width)).toInt()
    }


    fun printSize() {
        println(shape)
        println("Width: $width cm " +
                "Length: $length cm " +
                "Height: $height cm ")
        // 1 l = 1000 cm^3
        println("Volume: $volume 1 Water: $water l (${water/volume*100.0}% full")
    }

}

class TowerTank(override var height: Int, var diameter: Int): Aquarium(height = height, width = diameter, length = diameter) {
    override var volume: Int
        get() = (width/2 * length/2 * height / 1000 * PI).toInt()
        set(value) {
            height = ((value * 1000 / PI) / (width/2 * length/2)).toInt()
        }

    override var water = volume * 0.8
    override val shape = "cylinder"
    
    
}

```

```kotlin
package example.myapp

fun buildAquarium() {
    val myAquarium = Aquarium(width = 25, length = 25, height = 40)
    myAquarium.printSize()
    val myTower = TowerTank(diameter = 25, height = 40)
    myTower.printSize()

}

fun main() {
    buildAquarium()
}
```
