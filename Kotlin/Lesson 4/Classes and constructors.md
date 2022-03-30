### Create a class
- 새로운 패키지를 만들고 아래와 같이 위에서 언급한 `Aquarium` 클래스를 만들 수 있음
```kotlin
package example.myapp

class Aquarium {
    var width: Int = 20
    var height: Int = 40
    var length: Int = 100
}
```

- 코틀린은 자동으로 `Aquarium`에서 정의한 Properties에 대해서 getter와 setter를 만들어줌 그래서 해당 properties에 바로 접근할 수 있음

- 그리고 main 파일을 만들어서 인스턴스를 만들어 볼 것임 여기선 클래스가 정의되지 않음, 아래와 같이 간단하게 `Aquarium()`을 하는것으로 생성자가 되어 해당 인스턴스를 만들 수 있음

```kotlin
package example.myapp

fun buildAquarium() {
    val myAquarium = Aquarium()
}

fun main() {
    buildAquarium()
}
```

- method를 만들어 properties를 출력하고 이를 테스트 해볼 수 있고 값에 대해서도 정의해서 처리할 수 있음

```kotlin
package example.myapp

class Aquarium {
    var width: Int = 20
    var height: Int = 40
    var length: Int = 100

    fun printSize() {
        println("Width: $width cm " + "Length: $length cm " + "Height: $height cm ")
    }
}
```

```kotlin
package example.myapp

fun buildAquarium() {
    val myAquarium = Aquarium()
    myAquarium.printSize()
    myAquarium.height = 60
    myAquarium.printSize()
}

fun main() {
    buildAquarium()
}
```

----------

### Add class constructors
- `Aquarium` 클래스에 constructor를 만들 수 있음, 이는 다른 언어에서 생성자를 정의하는 방식과 유사함

- 그 방식은 아래와 같음

```kotlin
package example.myapp

class Aquarium(length: Int = 100, width: Int = 20, height: Int = 40) {
    var width: Int = width
    var height: Int = height
    var length: Int = length

    
}
```

- 여기서 코틀린에 맞게 더 줄여서 쓸 수 있음, 아래와 같이 `var`, `val`을 쓰는것만으로 코틀린은 getter랑 setter를 자동으로 만들어줌

```kotlin
package example.myapp

class Aquarium(var length: Int = 100, var width: Int = 20, var height: Int = 40) {
    
}
```

- 그러므로 Main에서 쓸 때 이 값에 대해서 생성자에서 만들지 않으면 디폴트 값이 들어가고 그게 아니면 직접 값을 커스텀할 수 있음(모든 값을 하든 일부를 하든 선택할 수 있음)

```kotlin
package example.myapp

fun buildAquarium() {
    val aquarium1 = Aquarium()
    aquarium1.printSize()
    // default height and length
    val aquarium2 = Aquarium(width = 25)
    aquarium2.printSize()
    // default width
    val aquarium3 = Aquarium(height = 35, length = 110)
    aquarium3.printSize()
    // everything custom
    val aquarium4 = Aquarium(width = 25, height = 35, length = 110)
    aquarium4.printSize()
}

fun main() {
    buildAquarium()
}
```

- 위의 예시는 단순하게 properties를 선언하고 할당한 것이고 만약 생성자에 초기화 코드가 필요하다면 `init`을 사용해서 초기화 코드를 정할 수 있음

- 아래와 같이 쓸 수 있음

```kotlin
package example.myapp

class Aquarium(var length: Int = 100, var width: Int = 20, var height: Int = 40) {

    init{
        println("aquarium initializing")
    }
    
    init{
        // 1 liter = 1000cm^3
        println("Volume: ${width * length * height / 1000} l")
    }
    
    
}


```

- 그러면 출력값은 아래와 같이 나오게 됨(즉, 클래스를 정의할 때 그리고 생성자가 호출될 때 먼저 `init`이 실행되고 처리됨)
```kotlin
aquarium initializing
Volume: 80 l
Width: 20 cm Length: 100 cm Height: 40 cm 
aquarium initializing
Volume: 100 l
Width: 25 cm Length: 100 cm Height: 40 cm 
aquarium initializing
Volume: 77 l
Width: 20 cm Length: 110 cm Height: 35 cm 
aquarium initializing
Volume: 96 l
Width: 25 cm Length: 110 cm Height: 35 cm 
```

- 생성자를 다른 요소를 추가해서 오버로딩 할 수 있음 마치 자바에서 생성자 오버로딩 하듯이

- 단, 코틀린에선 한 클래스에서 디폴트 값과 파라미터가 있는 오직 하나의 생성자를 작성하도록 권고함 그리고 `init`이 먼저 실행이 되고 기본 생성자가 먼저 실행되는 것을 알고 있어야함

- 아래와 같이 만들 수 있음(위에서 말한 유의사항대로 두번째 생성자 전에 init이 출력이 되어 Volume은 2번 출력함)

```kotlin
package example.myapp

class Aquarium(var length: Int = 100, var width: Int = 20, var height: Int = 40) {

    init{
        println("aquarium initializing")
    }

    init{
        // 1 liter = 1000cm^3
        println("Volume: ${width * length * height / 1000} l")
    }

    constructor(numberOfFish: Int) : this() {
        // 2,000 cm^3 per fish + extra room so water doesn't spill
        val tank = numberOfFish * 2000 * 1.1

        // calculate the height needed
        height = (tank / (length * width)).toInt()
    }


    fun printSize() {
        println("Width: $width cm " +
                "Length: $length cm " +
                "Height: $height cm ")
    }

}


```

- 코틀린은 getter와 setter를 properties를 정의하면 자동으로 만들어주지만 가끔 값을 조정하거나 계산할 경우가 있으므로 getter를 명시적으로 추가할 수 있음

- 아래와 같이 추가할 수 있

```kotlin
package example.myapp

class Aquarium(var length: Int = 100, var width: Int = 20, var height: Int = 40) {

    init{
        println("aquarium initializing")
    }

    val volume: Int
        get() = width * height * length / 1000  // 1000 cm^3 = 1 l

    constructor(numberOfFish: Int) : this() {
        // 2,000 cm^3 per fish + extra room so water doesn't spill
        val tank = numberOfFish * 2000 * 1.1

        // calculate the height needed
        height = (tank / (length * width)).toInt()
    }


    fun printSize() {
        println("Width: $width cm " +
                "Length: $length cm " +
                "Height: $height cm ")
        // 1 l = 1000 cm^3
        println("Volume: $volume l")
    }

}


```

```kotlin
package example.myapp

fun buildAquarium() {
    val aquarium6 = Aquarium(numberOfFish = 29)
    aquarium6.printSize()
}

fun main() {
    buildAquarium()
}
```

- Setter 역시 이와 같이 추가할 수 있음

```kotlin
package example.myapp

class Aquarium(var length: Int = 100, var width: Int = 20, var height: Int = 40) {

    init{
        println("aquarium initializing")
    }

    var volume: Int
        get() = width * height * length / 1000  // 1000 cm^3 = 1 l
        set(value) {
            height = (value * 1000) / (width * length)
        }

    constructor(numberOfFish: Int) : this() {
        // 2,000 cm^3 per fish + extra room so water doesn't spill
        val tank = numberOfFish * 2000 * 1.1

        // calculate the height needed
        height = (tank / (length * width)).toInt()
    }


    fun printSize() {
        println("Width: $width cm " +
                "Length: $length cm " +
                "Height: $height cm ")
        // 1 l = 1000 cm^3
        println("Volume: $volume l")
    }

}


```

```kotlin
package example.myapp

fun buildAquarium() {
    val aquarium6 = Aquarium(numberOfFish = 29)
    aquarium6.printSize()
    aquarium6.volume = 70
    aquarium6.printSize()
}

fun main() {
    buildAquarium()
}
```