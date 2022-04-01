### Learn about pairs and triples
- Pairs와 Triples는 2 ~ 3개의 generic item으로 미리 data class를 만드는 것임

- 이것은 함수가 둘 이상의 값을 반환할 때 유용하게 쓸 수 있음

- Pairs의 경우 아래와 같이 `to`를 사용해서 두 개의 문자열을 묶을 수 있음 각각의 값은 `.first`, `.second`로 참조함

```kotlin
val equipment = "fish net" to "catching fish"
println("${equipment.first} used for ${equipment.second}")
```
```kotlin
⇒ fish net used for catching fish
```

- Triples의 경우 `Triple()`을 통해서 만들 수 있음 이를 아래와 같이 변환도 가능하고 위에서 설명한대로 `.first`, `.second`, `.third`등 각각 값을 참조할 수 있음

```kotlin
val numbers = Triple(6, 9, 42)
 println(numbers.toString())
 println(numbers.toList())
```

- 그리고 단순히 같은 타입이 아니어도 다른 다양한 타입이 될 수 있음 또한 아래와 같이 다른 pair나 triple도 활용 가능함

```kotlin
val equipment2 = ("fish net" to "catching fish") to "equipment"
 println("${equipment2.first} is ${equipment2.second}\n")
 println("${equipment2.first.second}")
```

- 그리고 이를 각각 부분을 destructing 할 수 있음 적절한 변수를 할당을 시켜줄 수 있음을 의미함(pair, triple 다 가능함)

```kotlin
val equipment = "fish net" to "catching fish"
val (tool, use) = equipment
println("$tool is used for $use")
```

```kotlin
val numbers = Triple(6, 9, 42)
val (n1, n2, n3) = numbers
println("$n1 $n2 $n3")
```

- 이 부분은 이전에 data class에서와 동일하게 작용하는 것임

------

### Comprehend collections
- 이전에 List와 Mutable List등 몇 개의 Collections을 보았는데 코틀린은 이 Collections에 유용한 함수를 몇 가지 제공함(더 많은 자세한 내용은 공식문서로)

![one](/Kotlin/img/four.png)

- 위의 함수 말고도 모든 요소를 더하는 `sum()`함수도 있음

```kotlin
val list = listOf(1, 5, 3, 4)
println(list.sum())

// ⇒ 13
```

- 하지만 만약 여기서 String으로 이루어진 list의 경우 `sum()`을 하면 에러가 생김

- 이 경우에는 무엇을 더할지 명확하지 않아서 그러는 것이므로 `.sumBy()` 람다식을 통해서 String에서 무엇을 더할지 구체화 할 수 있음

- 아래와 같이 문자 길이를 더할 수 있음(람다의 디폴트는 `it`이며 아래의 경우 리스트의 각 요소를 가리킴)
```kotlin
val list2 = listOf("a", "bbb", "cc")
println(list2.sumBy { it.length })

// ⇒ 6
```

- 이외에도 `.`을 통해서 list로 매우 다양한 것을 할 수 있음을 알 수 있음

- 하나 더 알아본다면 `listIterator()`를 통해서 `for`문을 통해서 각각의 모든 요소를 공백을 바탕으로 아래와 같이 출력 가능함
```kotlin
val list2 = listOf("a", "bbb", "cc")
for (s in list2.listIterator()) {
    println("$s ")
}

// ⇒ a bbb cc
```

- 그리고 하나 더 알아볼 Collection은 HashMap임 `hashMapOf()`를 통해 무엇이든 map을 할 수 있음

- Hashmap은 key-value 쌍으로 이뤄진 일종의 list pair 같은 것임 

- 아래와 같이 Hashmap을 선언하고 증상을 key로 병명을 value로 정할 수 있음 그리고 이를 증상 key를 바탕으로 아래와 같이 가져올 수 있음

```kotlin
val cures = hashMapOf("white spots" to "Ich", "red sores" to "hole disease")
```
```kotlin
println(cures.get("white spots")) // ⇒ Ich
println(cures["red sores"]) // ⇒ hole disease
```

- 만약 map에 존재하지 않는 값을 찾으려고 하면 `null`을 반환함, 여기서 만약 해당 값이 없을 경우 `getOrDefault()`로 아래와 같이 쓸 수 있음
```kotlin
println(cures.getOrDefault("bloating", "sorry, I don't know")) // ⇒ sorry, I don't know
```

- 만약 여기서 return이 필요한 경우에는 `getOrElse()`를 써서 아래와 같이 쓸 수 있음, 아래는 단순히 String을 return 하였지만 그 이상의 다른 코드로 더 쓸 수 있음

```kotlin
println(cures.getOrElse("bloating") {"No cure for this"}) // ⇒ No cure for this
```

- 그리고 `mutableListOf`처럼 `mutableMapOf` 역시 쓸 수 있음, 이는 mutable map에 아이템을 넣고 삭제할 수 있음 즉 변경 가능한 것임

```kotlin
val inventory = mutableMapOf("fish net" to 1)
inventory.put("tank scrubber", 3)
println(inventory.toString())
inventory.remove("fish net")
println(inventory.toString())

// ⇒ {fish net=1, tank scrubber=3}{tank scrubber=3}
```

- 마지막으로 Immutable collections의 경우 여러 스레드가 같은 collection을 건드릴 때 문제가 생기는 환경에서 유용하게 쓸 수 있음

-------

### Study Constants
- `const val`를 통해서 숫자 상수를 만들 수 있음, 여기서 `const val` 역시 숫자가 할당되고 변경이 불가능함

- 이때 `val`과 차이점은 `const val`의 경우 컴파일을 할 때 값이 결정되고 `val`의 경우 프로그램 실행 동안 값이 결정되는 것임

- 즉, 이 경우에 아래와 같이 함수를 통해서 `val`은 값을 할당할 수 있지만 `const val`의 경우는 불가능함

```kotlin
val value1 = complexFunctionCall() // OK
const val CONSTANT1 = complexFunctionCall() // NOT ok
```

- 그리고 `const val`은 오직 top-level에서만 작용함, 아래와 같이 `object`로 싱글톤을 선언했을 땐 `const val`로 사용이 가능함

```kotlin
object Constants {
    const val CONSTANT2 = "object constant"
}
val foo = Constants.CONSTANT2
```

- 코틀린에서는 클래스 레벨의 상수의 개념이 없음

- 그래서 클래스에서 상수를 정의하기 위해선 `companion` 키워드로 `companion object`를 선언해야함, 이는 기본적으로 클래스 내부에 싱글톤 object를 말함

```kotlin
class MyClass {
    companion object {
        const val CONSTANT3 = "constant in companion"
    }
}
```

- `companion object`와 일반 `object`의 차이는 `companion object`의 경우 객체가 생성될 때 포함하고 있는 클래스의 정적 생성자를 통해서 초기화 됨 반면, 일반 `object`는 해당 object의 처음 접근할 때 뒤늦게 초기화 됨 즉, 처음 사용될 때 쓰임

------

### Write extensions
- 코틀린에서는 기존의 클래스의 Utility 함수에 대해 확장해서 쓰는데 있어서 편리함을 제공함

- Extension functions에서는 소스코드에 접근하지 않아도 현재 존재하는 클래스의 함수를 추가하도록 할 수 있게함

- 직접적으로 해당 클래스를 수정하지 않더라도 `.`을 통해서 해당 클래스 객체의 함수를 호출할 수 있음

- 아래와 같이 Extension function을 쓰고 활용할 수 있음

```kotlin
fun String.hasSpaces(): Boolean {
    val found = this.find { it == ' ' }
    return found != null
}
println("Does it have spaces?".hasSpaces())
```

- 그리고 이 Extension function을 간소화해서 쓸 수 있음, 아래와 같이
```kotlin
fun String.hasSpaces() = find { it == ' ' } != null
```

- 이 Extension function은 오직 public API만 접근할 수 있음 `private` 같은 경우는 접근할 수 없음

```kotlin
class AquariumPlant(val color: String, private val size: Int)

fun AquariumPlant.isRed() = color == "red"    // OK
fun AquariumPlant.isBig() = size > 50         // gives error
```

- Property 역시 Extension function으로 쓸 수 있음, 비슷한 로직으로 쓸 수 있고 `.`을 통해서 접근해서 처리할 수 있음

```kotlin
val AquariumPlant.isGreen: Boolean
   get() = color == "green"

aquariumPlant.isGreen
```

- 클래스는 또한 receiver를 쓸 수 있는 이는 해당 클래스를 nullable 하게 만들 수 있음, 이를 쓴다면 `this`는 `null`이 될 수 있음

- 만약 호출하는 곳에서 extension method를 nullable variables로 받고 싶어한다면 nullable receiver를 쓸 수 있음 혹은 default behavior를 두기 위해서 `null`을 적용할 수 있음

- 여기서 아래와 같이 처리할 수 있음, 이때 아무것도 출력되지 않고 처리가 안됨, `null`로써 존재하기 때문에(`?.apply.`를 통해서 코드를 실행하기 전에 `null` 체크를 할 수 있음)

```kotlin
fun AquariumPlant?.pull() {
   this?.apply {
       println("removing $this")
   }
}

val plant: AquariumPlant? = null
plant.pull()
```