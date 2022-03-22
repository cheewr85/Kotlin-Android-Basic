### Operators and Types
- 사칙연산 및 다양한 숫자 타입등 활용(Int, Long, Double 등)

```kotlin
1+1
res1: kotlin.Int = 2

53-3
res2: kotlin.Int = 50

50/10
res3: kotlin.Int = 5

1.0/2.0
res4: kotlin.Double = 0.5

2.0*3.5
res5: kotlin.Double = 7.0

6*50
res6: kotlin.Int = 300

6.0*50.0
res7: kotlin.Double = 300.0

6.0*50
res8: kotlin.Double = 300.0

2.times(3)
res9: kotlin.Int = 6

3.5.plus(4)
res10: kotlin.Double = 7.5

2.4.div(2)
res11: kotlin.Double = 1.2
```

- 코틀린은 암묵적으로 숫자 타입 사이의 변환을 해주지 않기 때문에, 직접 값을 할당해줘야함(암묵적인 변환이 에러를 일으키는 경우가 있어서 코틀린은 그렇게 하지 않음)

- 그래서 항상 서로 다른 타입의 값을 캐스팅을 통해 할당해줘야함(선언을 하고 .to(Type)으로 값을 변환해줘야함)

- 아래와 같이 할당을 하면 에러가 남

```kotlin
val b2: Byte = 1
 println(b2)
1

val i1: Int = b2
error: type mismatch: inferred type is Byte but Int was expected
val i1: Int = b2
              ^

val i2: String = b2
error: type mismatch: inferred type is Byte but String was expected
val i2: String = b2
                 ^

val i3: Double = b2
error: type mismatch: inferred type is Byte but Double was expected
val i3: Double = b2
                 ^
```

- 그래서 아래와 같이 캐스팅을 해줘야함

```kotlin
val i4: Int = b2.toInt()
 println(i4)
1

val i5: String = b2.toString()
 println(i5)
1

val i6: Double = b2.toDouble()
 println(i6)
1.0
```

- 그리고 만약 그 값이 길다면 `_`를 통해서 숫자를 나타내는 것을 코틀린은 허용함

```kotlin
val oneMillion = 1_000_000
val socialSecurityNumber = 999_99_9999L
val hexBytes = 0xFF_EC_DE_5E
val bytes = 0b11010010_01101001_10010100_10010010
```

- 코틀린은 타입을 정하지 않아도 타입에 강한 언어라서 컴파일러가 타입을 정하는데 개입하기 때문에 굳이 명시적으로 선언하지 않아도 됨

- `val`타입의 경우 값이 한 번 할당되면 다시 재할당 시킬 수 없고, `var`타입은 나중에 그 값을 바꿀 수 있음

```kotlin
var fish = 1
 fish = 2
 val aquarium = 1
 aquarium = 2
error: val cannot be reassigned
aquarium = 2
```

- 타입 또한 정하게 된다면 이 타입을 바꿀 순 없음

- String 역시 자바에서처럼 `"`은 문자열을 `'`은 문자를 `+`연산으로 문자를 붙일 수 있는 등 똑같음

- 여기서 `$variable`을 통해서 해당 텍스트를 variable로 바꿔서 쓸 수 있음, 그리고 연산 결과값으로써도 표현 가능함

```kotlin
val numberOfFish = 5
 val numberOfPlants = 12
 "I have $numberOfFish fish" + " and $numberOfPlants plants"
res0: kotlin.String = I have 5 fish and 12 plants

"I have ${numberOfFish + numberOfPlants} fish and plants"
res1: kotlin.String = I have 17 fish and plants
```

### Conditions and booleans
- 다른 프로그래밍 언어와 마찬가지로 작용을 함 비교 연산도 동일함(`<`,`==`,`>`,...등)

- if, else의 경우 아래와 같이 다양하게 활용가능함, 그리고 해당 범위에 있는지 등과 기본적인 체크하는 경우도 마찬가지로

```kotlin
val numberOfFish = 50
 val numberOfPlants = 23
 if (numberOfFish > numberOfPlants) {
     println("Good ratio!")
 } else {
     println("Unhealthy ratio")
 }
Good ratio!

val fish = 50
 if (fish in 1..100) {
     println(fish)
 }
50

if (numberOfFish == 0) {
     println("Empty tank")
 } else if (numberOfFish < 40) {
     println("Got fish!")
 } else {
     println("That's a lot of fish!")
 }
That's a lot of fish!
```

- when문도 있는데 이는 다른 언어에서 switch문의 역할을 함

```kotlin
when (numberOfFish) {
     0 -> println("Empty tank")
     in 1..39 -> println("Got fish!")
     else -> println("That's a lot of fish!")
 }
That's a lot of fish!
```

### nullability
- 기본적으로 코틀린은 var에 대해서는 null이 될 수 없음

```kotlin
var rocks: Int = null
error: null can not be a value of a non-null type Int
```

- `?`연산을 씀으로써, 해당 값이 null이 될 수 있음을 알리고 null을 할당시킬 수 있음

```kotlin
var marbles: Int? = null
```

- 여기서 이런 특징을 고려하여 기본적으로 만약 null인지 확인하는 것은 아래와 같이 조건문을 통해서 진행했음

```kotlin
var fishFoodTreats = 6
 if (fishFoodTreats != null) {
     fishFoodTreats = fishFoodTreats.dec()
 }
```

- 이를 코틀린에선 아래와 같이 개선할 수 있음

```kotlin
var fishFoodTreats = 6
fishFoodTreats = fishFoodTreats?.dec()
```

- 추가로 `?:`를 통해서 null 테스트를 묶을 수 있음, 이는 null이 아닌 경우 dec를 진행하고 만약 null이면 0의 값을 할당하게 됨(이를 Elvis operator라고도 함)
```kotlin
fishFoodTreats = fishFoodTreats?.dec() ?: 0
```

- 여기서 코틀린은 아예 null을 못 쓰게 하긴 하지만 그럼에도 `NullPointerExceptions`을 통해서 예외 처리를 하고 싶다면 아래와 같이 처리할 수 있음

```kotlin
var s: String? = null
 val len = s!!.length
java.lang.NullPointerException
```

- 이때 `!!`은 null이 아님을 나타내는 연산자임(그렇게 추천하는 방식은 아님)

### Explore arrays, lists, and loops
- list의 경우 다른 언어와 크게 다르지 않음 `listOf`로 만들 수 있음, 여기서 `listOf`을 쓰면 해당 list를 바꿀 수 없음

```kotlin
val school = listOf("mackerel", "trout", "halibut")
 println(school)
[mackerel, trout, halibut]
```

- `mutableListOf`의 경우 값을 변경할 수 있고 삭제 가능함, 정상적으로 삭제된 경우 `true`가 나옴

```kotlin
val myList = mutableListOf("tuna", "salmon", "shark")
 myList.remove("shark")
res1: kotlin.Boolean = true
```

- arrays라는 것이 코틀린에는 존재하는데 다른 자료구조가 변경가능하고 변경이 불가능한 버전이 나뉘어져 있지만 `Array`의 경우 한 번 만들면 크기가 고정되고 추가하고 제거도 안되고 변경 가능한 버전이 없음, 단 복사는 가능함

- `val`, `var`둘 다 동일하게씀

- 출력을 위해서 아래와 같이 `toString`사용

```kotlin
val school = arrayOf("shark", "salmon", "minnow")
 println(java.util.Arrays.toString(school))
[shark, salmon, minnow]
```

- 그리고 타입에 있어서도 값과 연관된 타입으로 정의하지 않아도 되서 여러가지가 섞여 있을 수 있음

```kotlin
val mix = arrayOf("fish", 2)
```

- 또한 이와 반대로 아예 하나의 타입으로만 정의해서 만들 수 있음

```kotlin
val numbers = intArrayOf(1,2,3)
```

- 아래와 같은 연산도 가능함(`+`로 array를 붙임)

```kotlin
val numbers = intArrayOf(1,2,3)
 val numbers3 = intArrayOf(4,5,6)
 val foo2 = numbers3 + numbers
 println(foo2[5])
3
```

- 이런 list와 array에 대해서 같이 결합된 형태로도 제공이 가능함, array를 출력하려고 하지 않으면 주소값을 출력함

```kotlin
val numbers = intArrayOf(1, 2, 3)
 val oceans = listOf("Atlantic", "Pacific")
 val oddList = listOf(numbers, oceans, "salmon")
 println(oddList)
[[I@1e8cb0a9, [Atlantic, Pacific], salmon]
```

- 여기서 array 초기화의 경우도 0으로 초기화하는 방식말고 코드로 초기화 할 수 있음

- 초기화 코드를 `{}`를 사용할 수 있음, `it`의 경우 array index를 참조하고 0부터 시작을 함

```kotlin
val array = Array (5) { it * 2 }
 println(java.util.Arrays.toString(array))
[0, 2, 4, 6, 8]
```

- 반복문의 경우 iterate를 해서 아래와 같이 출력이 가능함

```kotlin
val school = arrayOf("shark", "salmon", "minnow")
 for (element in school) {
     print(element + " ")
 }
shark salmon minnow 
```

- 코틀린에서는 값과 인덱스를 동시에 반복문을 통해서 나타낼 수 있음

```kotlin
for ((index, element) in school.withIndex()) {
     println("Item at $index is $element\n")
 }
Item at 0 is shark
Item at 1 is salmon
Item at 2 is minnow
```

- 다른 사이즈와 범위를 활용할 수 있음, 그리고 범위를 숫자, 문자 알파벳으로도 정의할 수 있음, 그리고 `downTo`를 통해서 거꾸로도 가능함

```kotlin
for (i in 1..5) print(i)
12345

for (i in 5 downTo 1) print(i)
54321

for (i in 3..6 step 2) print(i)
35

for (i in 'd' .. 'g') print(i)
defg
```

- 다른 언어와 마찬가지로 `while`, `do..while`문이 있고 `++`, `--` 연산 가능함, 코틀린은 또한 `repeat`반복도 있음

```kotlin
var bubbles = 0
 while (bubbles < 50) {
     bubbles++
 }
 println("$bubbles bubbles in the water\n")
 
 do {
     bubbles--
 } while (bubbles > 50)
 println("$bubbles bubbles in the water\n")
 
 repeat(2) {
     println("A fish is swimming\n")
 }
50 bubbles in the water
49 bubbles in the water
A fish is swimming
A fish is swimming
```