### Explore generic classes
- Generic type을 통해서 클래스를 generic 하게 만들 수 있고 그렇게 해서 클래스를 더 유연하게 만들 수 있음

- 예를 들어 `MyList`라는 클래스가 있는데 item을 가지고 있는 list인데 generics으로 만들지 않는다면 각 타입에 따라서 각각 `MyList`버전을 적용시켜야함

- 이 때 generic을 활용해서 어떤 객체의 타입도 적용시킬 수 있음, 이를 적용 시키기 위해서 `<T>`를 통해서 클래스를 정의할 수 있음

- `T` 참조는 일반 타입처럼 쓸 수 있음, return 타입으로도 쓸 수 있고 파라미터로도 쓸 수 있음

```kotlin
class MyList<T> {
    fun get(pos: Int): T {
        TODO("implement")
    }
    fun addItem(item: T) {}
}
```

- 먼저 이를 테스트 하기 위해 새로운 패키지에서 계층 구조를 만들기 위해서 `open`으로 상속받을 수 있게 만듬

- 그리고 이를 상속받은 하위 클래스들과 각 함수를 정의해서 만듬

```kotlin
package example.myapp.generics

open class WaterSupply(var needsProcessing: Boolean)

class TapWater : WaterSupply(true) {
    fun addChemicalCleaners() {
       needsProcessing = false
    }
}

class FishStoreWater : WaterSupply(false)

class LakeWater : WaterSupply(true) {
    fun filter() {
        needsProcessing = false
    }
}
```

- 그 다음 서로 다른 타입의 water supplies를 적용시키기 위한 generic class를 만듬

```kotlin
class Aquarium<T>(val waterSupply: T)
```

- 그리고 이를 활용해서 위에서 계층 구조로 만든 클래스를 `<>`을 사용해서 타입으로 특정지어서 아래와 같이 쓸 수 있음

- 이렇게 하면 타입 캐스팅을 하지 않고 함수를 불러올 수 있음

```kotlin
fun genericsExample() {
    val aquarium = Aquarium<TapWater>(TapWater())
    aquarium.waterSupply.addChemicalCleaners()
}
```

- 하지만 인스턴스 생성시 코틀린은 타입추론을 할 수 있기 때문에 굳이 `TapWater`를 두 번 쓰지 않더라도 알아서 해당 타입을 알고 처리를 함

- 이를 확인하기 위해서 출력해보면 아래와 같음

```kotlin
fun genericsExample() {
    val aquarium = Aquarium<TapWater>(TapWater())
    println("water needs processing: ${aquarium.waterSupply.needsProcessing}")
    aquarium.waterSupply.addChemicalCleaners()
    println("water needs processing: ${aquarium.waterSupply.needsProcessing}")
}

fun main() {
    genericsExample()
}

/*
출력 결과
⇒ water needs processing: true
water needs processing: false
*/
```

- Generic은 결국 무엇이든 보낼 수 있는 것인데 가끔 문제가 생길 때가 있음

- 이는 위에서 이 타입을 `WaterSupply`로 뒀지만 아래와 같이 string이든, null이어도 그 값이 들어가서 적용됨

```kotlin
fun genericsExample() {
    val aquarium2 = Aquarium("string")
    println(aquarium2.waterSupply)
}
```
```kotlin
fun genericsExample() {
    val aquarium3 = Aquarium(null)
    if (aquarium3.waterSupply == null) {
        println("waterSupply is null")
    }
}
```

- 이런식으로 `null`까지 들어갈 수 있는 이유는 기본적으로 `T`는 nullable한 `Any?`로 나타나기 때문에 어떤 타입이어도 가능하게 되기 때문임
```kotlin
class Aquarium<T: Any?>(val waterSupply: T)
```

- `null`을 허용하지 않고 싶다면 명시적으로 `Any`타입을 가르키고 `?`를 제거하면 됨
```kotlin
class Aquarium<T: Any>(val waterSupply: T)
```

- 이 `Any`는 generic constraint로 어떤 타입도 `T`에 들어갈 수 있다는 것을 의미함

- 그리고 여기서 무조건 `WaterSupply`의 타입으로 보내고 싶다면 아래와 같이 `Any`대신해서 더 구체적으로 generic constraint로 해당 타입을 명시해주는게 좋음

```kotlin
class Aquarium<T: WaterSupply>(val waterSupply: T)
```

- 여기서 `check()`함수를 통해서 코드가 예상한대로 동작하는지 확인할 수 있음, 이 함수는 코틀린에 기본 라이브러리 함수로 만약 `false`가 나오면 `IllegalStateException`을 던짐

```kotlin
class Aquarium<T: WaterSupply>(val waterSupply: T) {
    fun addWater() {
        check(!waterSupply.needsProcessing) { "water supply needs processing first" }
        println("adding water from $waterSupply")
    }    
}
```

- 여기서 아래와 같이 쓰면 예외가 발생함 filter 처리를 먼저 해줘야 하기 때문에

```kotlin
fun genericsExample() {
    val aquarium4 = Aquarium(LakeWater())
    aquarium4.addWater()
}
```

- 이 `check()`함수를 통해 봤기 때문에 이를 filter처리르 하고 다시 돌리면 문제가 없어짐
```kotlin
fun genericsExample() {
    val aquarium4 = Aquarium(LakeWater())
    aquarium4.waterSupply.filter()
    aquarium4.addWater()
}
```

- 이런식으로 `check()`를 통해 generic처리를 더 확실히 할 수 있음

--------

### Learn about in and out types
- `in`타입은 오직 반환되지 않은 클래스로만 들어갈 수 있고 `out`타입은 클래스에서만 반환될 수 있는 타입임

- 앞서 본 예시에서 생성자를 제외하고 `T`를 매개변수로 사용하는 경우가 없음

- 이때 `out`타입을 통해서 타입이 사용하는데 안전한 것인지에 대해서 알려줄 수 있음 유사하게 `in`타입은 반환되지 않는 generic 타입으로 오직 메소드 안에서만 쓸 수 있게함

- 이는 코틀린에서 코드 안전성을 위해서 추가 체크를 하기 때문임

- 이 `in`과 `out`은 코틀린의 타입 시스템이기 때문임

- 위의 부분에 대해서 클래스와 메소드를 아래와 같이 수정함
```kotlin
class Aquarium<out T: WaterSupply>(val waterSupply: T) {

}
```

- 그리고 아래와 같이 메소드도 활용이 가능한데 이는 `out`타입을 썼기 때문에 안전성의 문제가 생기지 않음
```kotlin
fun addItemTo(aquarium: Aquarium<WaterSupply>) = println("item added")

fun genericsExample() {
    val aquarium = Aquarium(TapWater())
    addItemTo(aquarium)
}
```

- 여기서 만약 `out`타입을 없애면 바로 에러가 뜸, 해당 타입에 대해서 보장이 확실히 되지 않기 때문임

- `in`타입 역시도 비슷하지만 반환되지 않는 함수에 generic 타입으로 들어갈 수 있음 만약 이를 반환하려고 한다면 에러가 뜸

- 아래와 같이 인터페이스에서 정의를 할 수 있음, 이는 generic `T`를 `WaterSupply`로 제한을 하게 함

```kotlin
interface Cleaner<in T: WaterSupply> {
    fun clean(waterSupply: T)
}
```

- 그리고 이 인터페이스를 implement하여서 해당 타입을 넣어서 쓸 수 있음

```kotlin
class TapWaterCleaner : Cleaner<TapWater> {
    override fun clean(waterSupply: TapWater) =   waterSupply.addChemicalCleaners()
}
```

- 그리고 앞서 본 `Aquarium` 클래스에 이를 추가해서 쓸 수 있음

```kotlin
class Aquarium<out T: WaterSupply>(val waterSupply: T) {
    fun addWater(cleaner: Cleaner<T>) {
        if (waterSupply.needsProcessing) {
            cleaner.clean(waterSupply)
        }
        println("water added")
    }
}
```

- 그리고 이를 응용해서 아래와 같이 활용이 가능함

```kotlin
fun genericsExample() {
    val cleaner = TapWaterCleaner()
    val aquarium = Aquarium(TapWater())
    aquarium.addWater(cleaner)
}
```

- 이러한 `in`, `out`타입을 통해서 코틀린은 generics을 좀 더 코드 상에서 안정적으로 쓸 수 있게끔 해 줌

-------

### Find out about generic functions
- generic 함수를 쓰는 것은 generic 타입의 클래스를 인수로 갖는 함수에서 쓰기 좋음

- 여기서 파라미터로 generic type을 넣고 함수도 generic type으로 정의를 함

```kotlin
fun <T: WaterSupply> isWaterClean(aquarium: Aquarium<T>) {
   println("aquarium water is clean: ${!aquarium.waterSupply.needsProcessing}")
}
```

- 그리고 함수 호출시에 타입 역시 확실히 해서 아래와 같이 사용 가능함

```kotlin
fun genericsExample() {
    val aquarium = Aquarium(TapWater())
    isWaterClean<TapWater>(aquarium)
}
```

- 하지만 타입추론을 통해서 `Aquarium`을 알기 때문에 굳이 `<>`를 쓸 필요는 없음

```kotlin
fun genericsExample() {
    val aquarium = Aquarium(TapWater())
    isWaterClean(aquarium)
}
```

- generic type을 가지고 있더라도 타입을 확인하기 위한 용도로써 또 사용할 수 있음

- 이때 generic type을 쓰는데 `R`을 쓰는 것은 이미 `T`가 사용됐기 때문임

```kotlin
fun <R: WaterSupply> hasWaterSupplyOfType() = waterSupply is R
```

- 하지만 이렇게 `is`를 통해서 generic을 또 사용할 경우 타입에 대해서 reified인지 real인지 함수로써 사용될 것이라고 알려줘야 하는데 이때 `fun` 키워드에 `inline`을 사용해야하고 `R` 타입엔 `reified`를 써줘야함

```kotlin
inline fun <reified R: WaterSupply> hasWaterSupplyOfType() = waterSupply is R
```

- generic type은 오직 컴파일 타입에서만 사용이 가능하고 실제 타입으로 변환이 됨 이 generic type을 런타임시에도 유지하기 위해서 함수를 `inline`으로 타입을 `reified`로 써줘야함

- 타입이 reified 되면 normal 타입으로 사용할 수 있음 만약 이를 써주지 않는다면 코틀린은 `is`체크를 할 수 없음

- 그리고 아래와 같이 확인이 가능해짐

```koltin
fun genericsExample() {
    val aquarium = Aquarium(TapWater())
    println(aquarium.hasWaterSupplyOfType<TapWater>())   // true
}
```

- 그리고 이를 extension function에도 사용이 가능함

- 아래와 같이 extension function을 정의해서 사용할 수 있음

```kotlin
inline fun <reified T: WaterSupply> WaterSupply.isOfType() = this is T
```
```kotlin
fun genericsExample() {
    val aquarium = Aquarium(TapWater())
    println(aquarium.waterSupply.isOfType<TapWater>())  
}
```

- 위의 extension function에서는 `Aquarium`인 한 타입이 중요하지 않음 그래서 이 때 star-projection을 통해서 다양한 매칭을 특정짓기 쉬운 방법으로 사용할 수 있음, 이를 사용함으로써 안정성이 보장됨

```kotlin
inline fun <reified R: WaterSupply> Aquarium<*>.hasWaterSupplyOfType() = waterSupply is R
```
```kotlin
fun genericsExample() {
    val aquarium = Aquarium(TapWater())
    println(aquarium.hasWaterSupplyOfType<TapWater>())
}
```

- 결국 `reified`와 `inline`에 대해서 런타임에서 코틀린이 알 수 있게 하기 위해서 generic type에서 활용함을 알 수 있음, 이 부분을 잘 생각하고 generic type을 활용하면 좋음

