### Learn about annotations
- 어노테이션은 코드에 metadata를 붙이는 것과 컴파일러가 읽어 코드나 로직을 생성하는데 사용하는 것임, 그러고 많은 프레임워크에서 사용함

- 이 어노테이션은 프레임워크 말고도 기본적으로 컴파일이전에 쓰는 방식으로 활용이 가능함, 대부분의 상황에서 사용이 가능하고 몇몇 어노테이션은 인수도 가질 수 있음

- 그리고 본인만의 어노테이션 역시 만들 수 있음

- 아래와 같이 어노테이션을 만들고 간단하게 붙여서 추가할 수 있음
```kotlin
annotation class ImAPlant
```
```kotlin
@ImAPlant class Plant{
    ...
}
```

- 그리고 이를 확인할 수 있음
```kotlin
fun testAnnotations() {
    val plantObject = Plant::class
    for (a in plantObject.annotations) {
        println(a.annotationClass.simpleName)
    }
}
```

- 그리고 해당 어노테이션을 찾을 수도 있음
```kotlin
fun testAnnotations() {
    val plantObject = Plant::class
    val myAnnotationObject = plantObject.findAnnotation<ImAPlant>()
    println(myAnnotationObject)
}
```

- 또한 어노테이션을 getter, setter의 타겟으로 둘 수 있음
```kotlin
annotation class ImAPlant

@Target(AnnotationTarget.PROPERTY_GETTER)
annotation class OnGet
@Target(AnnotationTarget.PROPERTY_SETTER)
annotation class OnSet

@ImAPlant class Plant {
    @get:OnGet
    val isGrowing: Boolean = true

    @set:OnSet
    var needsFood: Boolean = false
}
```

- 이러한 어노테이션은 런타임과 컴파일 시점에서 확인을 하는데 있어서 라이브러리를 만드는데 매우 효과적임, 하지만 이런 어노테이션은 위와 같이 만드는 것보다 직접 사용하는 경우가 일반적임

------

### Learn about labeled breaks
- 흐름을 제어하는데 있어서 다양한 방법이 있는데 흔히 `return`을 통해서 했지만 `break`역시 `for`문에서는 `return`과 같은 역할로 쓸 수 있음

- 여기서 추가적으로 label을 붙임으로써 중첩 for문에서 더 효과적으로 쓸 수 있음 아래와 같이

- 아래의 경우 label break로 외부 loop가 내부 loop로 부터 멈추게 됨

```kotlin
fun labels() {
    outerLoop@ for (i in 1..100) {
         print("$i ")
         for (j in 1..100) {
             if (i > 10) break@outerLoop  // breaks to outer loop
        }
    }
}

fun main() {
    labels()
}
```

- `continue`도 있는데 이는 `break`와는 달리 다음 반복으로 진행하는 것임(무시하고)

------

### Create simple lambdas
- lambdas는 이름이 없는 익명함수임, 이를 활용해서 변수를 할당하고 메소드에 인수로 넘겨줄 수 있음

- 간단한 lambda는 아래와 같이 만들 수 있음

```kotlin
val waterFilter = { dirty: Int -> dirty / 2 }
 waterFilter(30)
res0: kotlin.Int = 15
```

- 그리고 filter lambda도 만들 수 있음, 여기서 `it`은 현재 아이템을 참조하는 것으로 쓸 수 있음

```kotlin
data class Fish(val name: String)
 val myFish = listOf(Fish("Flipper"), Fish("Moby Dick"), Fish("Dory"))
 myFish.filter { it.name.contains("i") }
res1: kotlin.collections.List<Line_1.Fish> = [Fish(name=Flipper), Fish(name=Moby Dick)]
```

- 이를 응용해서 String으로 붙이는 로직도 만들 수 있음

```kotlin
myFish.filter { it.name.contains("i")}.joinToString(", ") { it.name }
⇒ res4: kotlin.String = Flipper, Moby Dick
```

-----

### Write a higher-order function
- 함수에 lambda나 다른 함수를 인자로 넘기는 것을 통해서 higher-order function을 만들 수 있음, 이는 람다를 확장하여서 적용시킬 수 있음

- 여기서 이를 적용하기 위해서 `with()`을 사용할 것임, 이는 하나 그 이상의 객체나 property의 참조를 더 쉬운 방식으로 쓸 수 있게 해 줌

- 아래와 같이 쓰므로, 람다를 통해서 제공할 객체를 구체화하고 실제 고차함수를 사용하는 것임

```kotlin
package example.myapp.example

data class Fish (var name: String)

fun fishExamples() {
    val fish = Fish("splashy")  // all lowercase
    with (fish.name) {
        println(capitalize())
    }
}
fun main() {
    fishExamples()
}
```

- 이런 고차함수를 보기 위해서 직접 이를 작성해서 적용할 수 있음, 아래와 같이 작성하면 이는 고차함수를 적용한 것과 동일한 방식으로 진행이 됨

```kotlin
fun myWith(name: String, block: String.() -> Unit) {
    name.block()
}

fun fishExamples() {
    val fish = Fish("splashy")  // all lowercase
    myWith (fish.name) {
        println(capitalize())
    }
}
```

- `with()`외에도 이와 유사한 람다 확장이 존재함

- 그 중 `run()`의 경우 모든 타입에서 확장을 할 수 있는데 람다를 인자로 받아 람다를 실행한 결과를 return을 함, 위의 예시를 활용하여 쓰면 아래와 같음

- 아래는 단지 `name`을 return 하지만 이외에도 변수를 할당하거나 출력도 할 수 있음

```kotlin
fish.run {
   name
}
```

- `apply()`의 경우 `run()`과 유사한데 람다의 결과 대신에서 바뀐 객체에 대한 것을 적용시켜서 반환을 함, 이는 새로운 객체를 생성할 때 메소드를 호출할 때 유용함

```kotlin
val fish2 = Fish(name = "splashy").apply {
     name = "sharky"
}
println(fish2.name)
```

- `let()`의 경우 `apply()`와 유사한데 바뀐 부분이 있는 객체의 복사본을 반환함, 이는 연속적으로 같이 묶어서 쓰는데도 유용함

- 즉, 아래와 같이 `fish`를 얻어온 후 추가적인 작업을 덧붙여서 처리할 수 있음

```kotlin
println(fish.let { it.name.capitalize()}
.let{it + "fish"}
.let{it.length}
.let{it + 31})
```

- 하지만 이는 위에서 말했듯이 복사본으로 반환하는 것이어서 원본은 똑같이 보존되어 있음

```kotlin
println(fish.let { it.name.capitalize()}
    .let{it + "fish"}
    .let{it.length}
    .let{it + 31})
println(fish)

⇒ 42
Fish(name=splashy)
```

-----

### Inline functions
- 람다와 고차함수는 유용한 개념이지만 알아둘 것이 람다는 객체이고 람다 표현식은 `Function` 인터페이스의 인스턴스이며 이는 `Object`의 subtype임

- 여기서 `Function` 인터페이스에는 `invoke`함수가 있고 이는 람다 표현식 호출을 오버라이딩 할 수 있음

```kotlin
// actually creates an object that looks like this
myWith(fish.name, object : Function1<String, Unit> {
    override fun invoke(name: String) {
        name.capitalize()
    }
})
```

- 평소에는 큰 문제가 되지 않지만 만약 위의 예시처럼 직접 `myWith()`와 같은 상황을 만든다면 이야기가 다름 오버헤드가 발생할 수 있음

- 이를 해결하기 위해서 코틀린은 `inline`을 제공함, 이러면 컴파일 시점에서 소스코드를 변환해서 처리하는 것을 의미함

```kotlin
inline myWith(fish.name) {
    capitalize()
}
```
```kotlin
// with myWith() inline, this becomes
fish.name.capitalize()
```

- 하지만 이는 어디까지 오버헤드나 그런 부분 처리에 대한 참조사항인 것이지 너무 복잡하게 생각할 필요는 없음

------

### Learn about Single Abstract Methods
- Single Abstract Method의 의미는 하나의 메소드만을 가진 인터페이스를 의미함

- 이는 자바에서 쓰여진 API에서 흔한 케이스라 SAM이라고 부름(예를 들어서 `run`추상 메소드가 하나뿐인 `Runnable`과 `call()`추상 메소드가 하나뿐임 `Callable`이 있음)

- 즉, 이는 쉽게 말해서 이를 인스턴스화하고 한 줄의 코드로 SAM을 호출하고 오버라이딩 할 수 있음을 의미함

```kotlin
package example;

public class JavaRun {
    public static void runNow(Runnable runnable) {
        runnable.run();
    }
}
```
```kotlin
fun runExample() {
    JavaRun.runNow {
        println("Last parameter is a lambda as a Runnable")
    }
}
```