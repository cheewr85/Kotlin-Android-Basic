### Explore the main() function
- Kotlin에선 return으로 무언가 존재해야 하지만 `main()` 함수 같은 경우 return이 필요없는 `Kotlin.Unit` 타입을 리턴함 즉, 아무것도 return하지 않을 때 굳이 특정 지을 필요가 없다는 것임

- `args`에 값을 넣어서 처리하는 것은 자바와 유사함

### Learn why (almost) everything has a value
- `Kotlin.Unit`하에서 모든것을 나태내고 그것이 값이 될 수 있음, 예시를 보면 아래와 같음
```kotlin
// Will assign kotlin.Unit
val isUnit = println("This is an expression")
println(isUnit)
```

- 그리고 조건문을 달아서 아래와 같이도 쓸 수 있음, 이렇게 쓰면 그 결과는 조건문에 맞는 `true/false` 값이 나옴
```kotlin
val temperature = 10
val isHot = if (temperature > 50) true else false
println(isHot)
```

- String template 역시 사용 가능
```kotlin
val temperature = 10
val message = "The water temperature is ${ if (temperature > 50) "too warm" else "OK" }."
println(message)
```

- 단, 반복문은 위와 같이 성립할 수 있음, 확실한 값이라는 것을 정의 내리기 어렵기 때문에

### Learn more about functions
- 함수 작성시, 아래와 같이 서로 다른 타입을 바탕으로 작성할 수 있음
```kotlin
import java.util.*

fun feedTheFish() {
    val day = randomDay()
    val food = "pellets"
    println ("Today is $day and the fish eat $food")
}

fun randomDay() : String {
    val week = arrayOf("Monday", "TuesDay", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    return week[Random().nextInt(week.size)]
}

fun main(args: Array<String>) {
    feedTheFish()
}
```

- `when`문도 활용할 수 있는데, 이 `when`문은 `switch`문과 유사하지만 다른 점은 분기별로 자동으로 `break`를 해주는 점임

- 그래서 위의 함수에 추가해서 적용해보면
```kotlin
import java.util.*

fun fishFood (day : String) : String {
    var food = ""
    when(day) {
        "Monday" -> food = "flakes"
        "Wednesday" -> food = "redworms"
        "Thursday" -> food = "granules"
        "Friday" -> food = "mosquitoes"
        "Sunday" -> food = "plankton"
        else -> food = "nothing"
    }
    return food
}

fun feedTheFish() {
    val day = randomDay()
    val food = fishFood(day)
    println ("Today is $day and the fish eat $food")
}

fun randomDay() : String {
    val week = arrayOf("Monday", "TuesDay", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    return week[Random().nextInt(week.size)]
}

fun main(args: Array<String>) {
    feedTheFish()
}
```

### Explore default values and compact functions
- 함수를 간결하고 읽기 쉽게 줄일 수 있음, 그리고 파라미터에 디폴트 값을 넣어서 값이 전달되기 전에 쓸 수 있음

- 아래와 같이 쓰면 값이 전달되기전 `fast`가 쓰이지만 파라미터를 넘기면 해당 파라미터 값이 넘어오게 됨

```kotlin
fun swim(speed: String = "fast") {
   println("swimming $speed")
}
```
```kotlin
swim()   // uses default speed
swim("slow")   // positional argument
swim(speed="turtle-like")   // named parameter
```

- 이를 생각해서 아래와 같이 디폴트 값을 설정해서 `when`을 써서 이 값이 변하면 `true`를 반환함, 여기서 `day`의 경우 `Sunday`여야 `true`를 반환함, 그리고 여기서 `day`는 필수로 받아야하는 파라미터 값임
```kotlin
fun shouldChangeWater (day: String, temperature: Int = 22, dirty: Int = 20): Boolean {
    return when {
        temperature > 30 -> true
        dirty > 30 -> true
        day == "Sunday" ->  true
        else -> false
    }
}
```
```kotlin
fun feedTheFish() {
    val day = randomDay()
    val food = fishFood(day)
    println ("Today is $day and the fish eat $food")
    println("Change water: ${shouldChangeWater(day)}")
}
```

- 여기서 코틀린은 위와 같은 조건에 따른 것을 체크함에 있어서 그 variable에 대해서 복잡한 것을 간결하게 줄인 버전으로 compact functions을 쓸 수 있음 single-expression functions이라고도 함, 이는 코틀린에서 자주 쓰이는 패턴임

- 함수가 단일한 결과를 리턴할 때 함수의 구체적 내용을 `=`뒤에 `{}`과 `return`을 제외하고 아래와 같이 쓸 수 있음
```kotlin
fun isTooHot(temperature: Int) = temperature > 30

fun isDirty(dirty: Int) = dirty > 30

fun isSunday(day: String) = day == "Sunday"
```
```kotlin
fun shouldChangeWater (day: String, temperature: Int = 22, dirty: Int = 20): Boolean {
    return when {
        isTooHot(temperature) -> true
        isDirty(dirty) -> true
        isSunday(day) -> true
        else  -> false
    }
}
```

- 여기서 디폴트 값은 값이 아니고 아래와 같이 또 다른 함수가 올 수 있음
```kotlin
fun shouldChangeWater (day: String, temperature: Int = 22, dirty: Int = getDirtySensorReading()): Boolean {

```

### Get started with filters
- Filter는 조건에 따라서 list의 값을 찾아오기 쉽게 하는 방법임

- 아래와 같이 어떤 list가 있다고 할 때 filter를 통해서 첫 문자가 `p`로 시작한 값을 쉽게 불러올 수 있음
```kotlin
val decorations = listOf ("rock", "pagoda", "plastic plant", "alligator", "flowerpot")

fun main() {
    println( decorations.filter {it[0] == 'p'})
}
```

- 그리고 코틀린에선 이 filter가 eager 즉, 수행할 연산을 미루지 않고 바로 처리하거나 lazy 지금 하지도 않은 연산을 최대한 뒤로 미루고, 어쩔 수 없이 연산이 필요한 순간에 연산을 수행하는 방식 이 두 가지를 선택할 수 있음

- filter를 lazy로 만들기 위해선 `Sequence`를 사용해야함, 이를 비교하는 코드를 보면 아래와 같음

```kotlin
fun main(args: Array<String>) {
    val decorations = listOf ("rock", "pagoda", "plastic plant", "alligator", "flowerpot")

    // eager, creates a new list
    val eager = decorations.filter { it [0] == 'p' }
    println("eager: $eager")

    // lazy, will wait until asked to evaluate
    val filtered = decorations.asSequence().filter { it[0] == 'p' }
    println("filtered: $filtered")

    // force evaluation of the lazy list
    val newList = filtered.toList()
    println("new list: $newList")

    val lazyMap = decorations.asSequence().map {
        println("access: $it")
        it
    }

    println("lazy: $lazyMap")
    println("-----")
    println("first: ${lazyMap.first()}")
    println("-----")
    println("all: ${lazyMap.toList()}")

    val lazyMap2 = decorations.asSequence().filter {it[0] == 'p'}.map {
        println("access: $it")
        it
    }
    println("-----")
    println("filtered: ${lazyMap2.toList()}")
}
```

- 즉, 바로 출력해서 가져오는 것이 아닌 우선 `Sequence`에서 해당 값을 알고 필터를 적용하고 `Sequence`에 접근할 때 필터가 적용되어 그 값이 return 되는 것임

### Get Started with lambdas and higher-order functions
- Lambdas의 경우 함수를 정의할 때 이름이 없이 정의하고, 람다식이 데이터를 넘겨줄 수 있는 부분이 아주 유용하게 쓰일 수 있음 

- lambda를 다른 함수에 넘김으로써 고차원 함수를 만들 수 있음, 위에서 filter에서 `p`값이 있는지 확인하는 부분에서 이 람다식을 활용한 것임

- lambda 역시도 파라미터를 가지고 있는 이때 `->`를 통해서 파라미터를 가르키고 나타냄, 값을 할당하면 함수처럼 활용이 가능함
```kotlin
var dirtyLevel = 20
val waterFilter = { dirty : Int -> dirty / 2}
println(waterFilter(dirtyLevel))
```

- 이러한 코틀린 함수 표현은 람다 문법과 연관되어 있음, 아래와 같이 정확하게 값을 선언해서 쓸 수도 있음
```kotlin
val waterFilter: (Int) -> Int = { dirty -> dirty / 2 }
```

- 고차원 함수를 응용한다는 것은 한 함수의 argument 값에 다른 함수를 넣는 것을 의미함
```kotlin
fun updateDirty(dirty: Int, operation: (Int) -> Int): Int {
   return operation(dirty)
}
```

- 이를 그대로 넘겨서 아래와 같이 함수를 호출할 수 있음
```kotlin
val waterFilter: (Int) -> Int = { dirty -> dirty / 2 }
println(updateDirty(30, waterFilter))
```

- 여기서 굳이 람다만 넘기지 않고 일반 함수도 넘길 수 있음 이때 일반함수를 넘기는 것을 나타내기 위해 `::`을 써줘야함
```kotlin
fun increaseDirty( start: Int ) = start + 1

println(updateDirty(15, ::increaseDirty))
```