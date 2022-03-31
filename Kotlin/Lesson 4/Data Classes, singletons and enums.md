### Create a data class
- data class는 다른 언어에서 `struct`와 유사한 개념임 즉, 어느 데이터를 잡고 있기 위해 존재하는 것임

- 하지만 data class 객체는 여전히 객체임, 코틀린에서는 이런 data class 객체는 출력하고 복사하는 등의 몇 가지 이점이 존재함

- 예시로 아래와 같이 data class를 만들고 함수를 통해 인스턴스화 하고 출력할 수 있음, 그리고 main에서 바로 사용이 가능함

```kotlin
package example.myapp.decor

data class Decoration(val rocks: String) {
}

fun makeDecorations() {
    val decoration1 = Decoration("granite")
    println(decoration1)
}
```

```kotlin
fun main () {
    makeDecorations()
}
```

- 여기서 아래와 같이 함수를 만들고 비교를 해보면 다음과 같음을 알 수 있

```kotlin
package example.myapp.decor

data class Decoration(val rocks: String) {
}

fun makeDecorations() {
    val decoration1 = Decoration("granite")
    println(decoration1)

    val decoration2 = Decoration("slate")
    println(decoration2)

    val decoration3 = Decoration("slate")
    println(decoration3)

    println (decoration1.equals(decoration2))
    println (decoration3.equals(decoration2))
}
```
```kotlin
fun main () {
    makeDecorations()
}
```
```kotlin
⇒ Decoration(rocks=granite)
Decoration(rocks=slate)
Decoration(rocks=slate)
false
true
```

- 여기서 데이터 클래스 객체에 대해 `==`는 코틀린에서는 `equals()`메소드와 동일함 즉, `==` 통해서도 같은 객체를 참조하는지 확인할 수 있음

- 복사 역시 해당 객체에 대한 참조가 복사되는 것임 새 객체를 복사하기 위해선 `copy()`메소드를 사용하면 됨

- 단, `copy()`, `equals()`, 기타 데이터 클래스에서는 오직 처음 생성자로 정의된 참조 properties에만 사용할 수 있음

- 각각의 property에 대해서 한 번에 할당할 수 있는데 이를 destructuring이라고 함, 아래와 같이 쓸 수 있음

```kotlin
// Here is a data class with 3 properties.
data class Decoration2(val rocks: String, val wood: String, val diver: String){
}

fun makeDecorations() {
    val d5 = Decoration2("crystal", "wood", "diver")
    println(d5)

// Assign all properties to variables.
    val (rock, wood, diver) = d5
    println(rock)
    println(wood)
    println(diver)
}

```

- 여기서 만약 해당 properties가 필요 없다면 `_`을 사용해서 생략할 수 있음
```kotlin
    val (rock, _, diver) = d5
```

-------

### Learn about singletons, enums, and sealed classes
- `object` 키워드를 통해서 모든 인스턴스가 동일하게 오직 하나의 인스턴스인 singleton으로 만들 수 있음

```kotlin
object GoldColor : FishColor {
   override val color = "gold"
}
```

- 그리고 enum 클래스에 대해서도 지원이 가능한데 다른 언어에서와 같이 이름을 가지고 무언가를 열거하고 불러올 수 있음

- 여기서 `enum`키워드를 사용해서 정의하고 쓸 수 있음, 이 `enum` 역시도 singletons과 비슷함, 아래와 같이 property를 얻어올 수도 있음

```kotlin
enum class Direction(val degrees: Int) {
    NORTH(0), SOUTH(180), EAST(90), WEST(270)
}

fun main() {
    println(Direction.EAST.name)
    println(Direction.EAST.ordinal)
    println(Direction.EAST.degrees)
}
```

- `sealed class`는 상속을 받을 수 있지만 오직 선언된 파일 안에서만 가능함

- 즉 이 부분은 같은 파일에서만 상속관계가 성립됨을 알면 됨, 아래와 같이 쓸 수 있음

```kotlin
sealed class Seal
class SeaLion : Seal()
class Walrus : Seal()

fun matchSeal(seal: Seal): String {
   return when(seal) {
       is Walrus -> "walrus"
       is SeaLion -> "sea lion"
   }
}
```

