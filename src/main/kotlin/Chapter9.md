# Kotlin Ch.9.  제네릭스

## 9.1 제네릭 타입 파라미터

9.1.1 제네릭 함수와 프로퍼티

```kotlin
>>> val letters = ('a'..'z').toList()
>>> println(letters.slice<Char>(0..2)) // 타입 인자를 명시적으로 지정한다. 
[a, b, c]
>>> println(letters.slice(10..13)) // 컴파일러는 여기서 T가 Char라는 사실을 추론한다. 
[k, l, m, n]
```

```kotlin
fun <T : Number> oneHalf(value: T): Double { // Number를 타입 파라미터 상한으로 정한다. 
    return value.toDouble() / 2.0 // Number 클래스에 정의된 메소드를 호출한다. 
}

>>> println(oneHalf(3))
1.5
```

```kotlin
class Processor<T> {
		fun process(value: T) {
				value?.hashCode() // "value"는 널이 될 수 있다. 따라서 안전한 호출을 사용해야 한다. 
		}
}

class Processor<T : Any> {
		fun process(value: T) {
				value.hashCode()
		}
}
```

9.1.2 제네릭 클래스 선언

9.1.3 타입 파라미터 제약

9.1.4 타입 파라미터를 널이 될 수 없는 타입으로 한정

## 9.2 실행 시 제네릭스의 동작 : 소거된 타입 파라미터와 실체화된 타입 파라미터

9.2.1 실행 시점의 제네릭: 타입 검사와 캐스트

```kotlin
val list1: List<String> = listOf("a", "b")
val list2: List<Int> = listOf(1, 2, 3)

fun printSum(c: Collection<*>) {
    val intList = c as? List<Int>                 
            ?: throw IllegalArgumentException("List is expected")
    println(intList.sum())
}
>>> printSum(listOf(1, 2, 3))               
6

>>> printSum(setOf(1, 2, 3))                  
IllegalArgumentException: List is expected
>>> printSum(listOf("a", "b", "c"))          
ClassCastException: String cannot be cast to Number
```

9.2.2 실체화한 타입 파라미터를 사용한 함수 선언

9.2.3 실체화한 타입 파라미터로 클래스 참조 대신

```kotlin
fun printSum(c: Collection<Int>) {
    if (c is List<Int>) {           
        println(c.sum())
    }
}
>>> printSum(listOf(1, 2, 3))
6
```

9.2.4 실체화한 타입 파라미터의 제약

## 9.3 변성 : 제네릭과 하위 타입

9.3.1 변성이 있는 이유: 인자를 함수에 넘기기

```kotlin
>>> val strings = mutableListOf("abc", "bac")
>>> addAnswer(strings) // 이 줄이 컴파일된다면.                 
>>> println(strings.maxBy { it.length })  
ClassCastException: Integer cannot be cast to String // 실행 시점에 예외가 발생할 것이다.
```

9.3.2 클래스, 타입, 하위 타입

9.3.3 공변성: 하위 타입 관계를 유지

```kotlin
interface Producer<out T> {  // 클래스가 T에 대해 공변적이라고 선언한다. 
    fun produce(): T
}
```

```kotlin
open class Animal {
    fun feed() { ... }
}
class Herd<T : Animal> {  // 이 타입 파라미터를 무공변성으로 지정한다. 
    val size: Int get() = ...
    operator fun get(i: Int): T { ... }
}
fun feedAll(animals: Herd<Animal>) {
    for (i in 0 until animals.size) {
        animals[i].feed()
    }
}

// 사용자 코드가 고양이 무리를 만들어서 관리한다. 
class Cat : Animal() {   
    fun cleanLitter() { ... }
}
fun takeCareOfCats(cats: Herd<Cat>) {
    for (i in 0 until cats.size) {
        cats[i].cleanLitter()
        // feedAll(cats)           
    }
}
```

```kotlin
class Herd<out T : Animal> {  
   ...
}
fun takeCareOfCats(cats: Herd<Cat>) {
    for (i in 0 until cats.size) {
        cats[i].cleanLitter()
    }
    feedAll(cats)  
}
```

9.3.4 반공변성: 뒤집힌 하위 타입 관계

9.3.5 사용 지점 변성 : 타입이 언급되는 지점에서 변성 지정

9.3.6 스타 프로젝션 : 타입 인자 대신 * 사용