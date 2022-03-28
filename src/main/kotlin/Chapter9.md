## 9.1 제네릭 타입 파라미터

- 제네릭스를 통해 타입 파라미터를 받는 타입을 정의 할 수 있다. 제네릭 타입의 인스턴스를 만들기 위해서는 타입 파라미터를 타입 인자로 치환해야한다.

```kotlin
// 코틀린 컴파일러는 보통 타입과 마찬가지로 타입인자도 추론 가능
val authors = listOf("Dmitry", "Svetlana")

// 변수의 타입 지정
val readers: MutableList<String> = mutableListOf()

// 변수를 만드는 함수의 타입 인자를 지정
val readers = mutableListOf<String>()
```

- 위 예시처럼 빈 리스트를 만들어야 한다면 타입인자를 명시해야 한다.

### 9.1.1 제네릭 함수와 프로퍼티

- 아래와 같은 제네릭 함수는 모든 리스트를 다룰 수 있다. 이와 같은 제네릭 함수를 호출할 때는 반드시 구체적인 타입으로 타입 인자를 넘겨야 한다.

```kotlin
fun <T> List<T>.slice(indices: IntRange) : List<T>
// 맨앞 <T>는 타입 선언
// 파라미터와 반환 시에도 타입 파라미터 T 를 명시

>>> val letters = ('a'..'z').toList()
>>> println(letters.slice<Char>(0..2)) // 타입 인자를 명시적으로 지정한다. 
[a, b, c]
>>> println(letters.slice(10..13)) // 컴파일러는 여기서 T가 Char라는 사실을 추론한다. 
[k, l, m, n]
```

- 아래와 같이 제네릭 고차 함수를 호출할 수 있다.

```kotlin
val authors = listOf("Dmitry", "Svetlana")
val readers = mutableListOf<String>(/*...*/)
fun <T> List<T>.filter(predicate: (T) -> Boolean): List<T>
>>> readers.filter(it !in authors)
```

- 람다 파라미터에 대해 자동으로 만들어지는 it 변수의 타입은 T 제네릭 타입.
- 컴파일러는 filter가 List<T> 타입의 리스트에 대해 호출할 수 있다는 사실과 filter의 수신객체인 readers의 타입이 List<String>이라는 사실을 알고 그로부터 T가 String이라는 사실을 추론한다.
- 클래스나 인터페이스 안에 정의된 메서드, 확장 함수 또는 최상위 함수에서 타입 파라미터를 선언할 수 있다.
- 제네릭 함수를 정의할 때와 같이 제네릭 확장 프로퍼티를 선언 가능.

    ```kotlin
    val <T> List<T>.penultimate: T  // 모든 리스트 타입에 제네릭 확장 프로퍼티를 사용할 수 있다.
        get() = this[size - 2]
    
    >>> println(listOf(1, 2, 3, 4).penultimate) // 타입 파라미터 T는 Int로 추론된다.
    3
    ```

- 확장프로퍼티만 제네릭하게 만들수 있다.
  - 일반 프로퍼티는 타입 파라미터를 가질 수 없는데 클래스 프로퍼티에 여러 타입의 값을 저장할  없으므로 제네릭한 일반 프로퍼티는 말이 안되기 때문. 일반 프로퍼티를 제네릭하게 정의하면 오류가 발생.

### 9.1.2 제네릭 클래스 선언

- 코트린에서도 타입 파라미터를 꺽쇠 괄호 안에 넣고 클래스 이름 뒤에 붙여 제네릭 클래스를 선언하게 된다. 타입 파라미터를 이름 뒤에 붙이고 클래스의 본문 안에서 타입 파라미터를 다른 일반 타입처럼 쓸 수 있다.
- 제네릭 클래스를 확장하는 클래스를 정의하려면 기반 타입의 제네릭 파라미터에 대해 타입 인자를 지정해야 한다.

```kotlin
interface List<T> { // List 인터페이스에 T라는 타입 파라미터를 정의한다.
    operator fun get(index: Int): T // 인터페이스 안에서 T를 일반 타입처럼 사용할 수 있다.
}

class StringList: List<String> {
    override fun get(index: Int): String = ...
}

class ArrayList<T>: List<T> {
    override fun get(index: Int): T = ...
}
```

- 하위 클래스에서 상위 클래스에 정의된 함수를 오버라이드 하는 경우 타입 인자를 구체적인 타입으로 치환한다.
- 클래스가 자기 자신을 타입인자로 참조할 수도 있다. 아래 예시에서 String 클래스는 제네릭 Comparable 인터페이스를 구현하면서 그 인터페이스 타입 파라미터 T로 String 자신을 지정

```kotlin
interface Comparable<T> {
    fun compareTo(other: T): Int
}
class String: Comparable<String> {
    override fun compareTo(other: String): Int = /*...*/
}
```

### 9.1.3 타입 파라미터 제약

- 타입 파라미터의 제약은 클래스나 함수에 사용할 있는 타입 인자를 제한하는 기능으로, 어떤 타입을 제네릭 타입의 파라미터에 대한 상한으로 지정하면 그 제네릭 타입을 인스턴스화 할 때 사용하는 타입 인자는 반드시 그 상한 타입이거나 그 상한 타입의 하위 타입이어야 한다.
- 제약을 가하려면 타입 파라미터 이름 뒤에 :를 표시하고 그 뒤에 상한 타입을 적는다.

```kotlin
// fun <타입 파라미터: 상한> List<T>.sum(): T
fun <T: Number> List<T>.sum(): T

/* java */
<T extends Number> T sum(List<T> list)
```

- 타입 파라미터 T에 대한 상한을 정하고 나면 T 타입의 값을 그 상한 타입의 값으로 취급할 수 있다.

```kotlin
fun <T : Number> oneHalf(value: T): Double { // Number를 타입 파라미터 상한으로 정한다. 
    return value.toDouble() / 2.0 // Number 클래스에 정의된 메소드를 호출한다. 
}

>>> println(oneHalf(3))
1.5
```

- 타입 파라미터를 제약하는 함수를 아래와 같이 선언할 수 있다. T의 상한 타입은 Comparable<String> 이고 String 이 Comparable<String>을 확장하므로 String은 max 함수에 적합한 타입 인자이다.

```kotlin
// 이 함수들의 인자들은 비교 가능해야함
fun <T: Comparable<T>> max(first: T, second: T): T {
    return if (first > second) first else second
}
>>> println(max("kotlin", "java"))
kotlin
```

- 타입 파라미터에 대해 둘 이상의 제약을 가하는 경우는 아래와 같다. 타입인자가 CharSequence와 Appendable 인터페이스를 반드시 구현해야한다는 점을 표현하여 데이터에 접근하는 연산인 endWith와 데이터를 변환하는 연산 append를 T 타입의 값에게 수행할 수 있게 한다.

```kotlin
fun <T> ensureTrailingPeriod(seq: T) where T: CharSequence, T: Appendable {
    if (!seq.endsWith('.')) {
        seq.append('.')
    }
}

>>> val helloWorld = StringBuilder("Hello World")
>>> ensureTrailingPeriod(helloWorld)
>>> println(helloWorld)
Hello World.
```

### 9.1.4 타입 파라미터를 널이 될 수 없는 타입으로 한정

- 제네릭 클래스나 함수를 정의하고 그 타입을 인스턴스화 할 때는 null 이 될 수 있는 타입을 포함하는 어떤 타입으로 타입 인자를 지정해도 타입 파라미터를 치환할 수 있다.
- 아무런 상한을 정하지 않은 타입 파라미터는 결과적으로 Any?를 상한으로 정한 파라미터와 같다. null 가능성을 제외한 아무런 제약도 필요 없다면 Any?대신 Any를 상한으로 사용하면 된다.

## 9.2 실행 시 제네릭스의 동작 : 소거된 타입 파라미터와 실체화된 타입 파라미터

- JVM의 제네릭스는 보통 타입 소거를 사용해 구현된다. 이는 실행 시점에 제네릭 클래스의 인스턴스에 타입 인자 정보가 들어있지 않다는 뜻.

### 9.2.1 실행 시점의 제네릭: 타입 검사와 캐스트

- 자바와 마찬가지로 코틀린 제네릭 타입 인자 정보는 런타임에 지워진다.
  - 제네릭 클래스 인스턴스가 그 인스턴스를 생성할 때 타입 인자에 대한 정보를 유지 하지 않음.
- 아래 예시에서 두 리스트를 컴파일러는 서로 다른 타입으로 인식하지만 실행 시점에 그 둘은 완전히 같은 타입의 객체가 된다. 컴파일러가 타입 인자를 알고 올바른 타입의 값만 각 리스트에 넣도록 보장해주기 때문에 List의 원소 타입을 추론할 수 있다.
- 타입 인자를 따로 저장하지 않기 때문에 실행 시점에 타입인자를 검사할 수 없다는 타입 소거의 단점이 있다.
- 아래 예시에서 실행 시점에 어떤 값이 List인지는 확실히 알 수 있지만, 그게 String 리스트인지, 다른 타입의 리스트인지는 알 수 없음.

```kotlin
val list1: List<String> = listOf("a", "b")
val list2: List<Int> = listOf(1, 2, 3)

>>> if (value is List<String>) {...}
ERROR: Cannot check for instance of erased type
```

- 어떤 값이 집합이나 맵이 아닌 리스트라는 사실을 스타 프로젝션을 이용해 체크할 수 있다. 타입 파라미터가 2개 이상이라면 모든 타입 파라미터에 *을 포함시켜야 한다.

```kotlin
if (value is List<*>) {...}
```

- 아래 예시처럼 as나 as? 캐스팅에도 여전히 제네릭 타입을 사용할 수 있지만, 기저 클래스는 같지만 타입인자가 다른 타입으로 캐스팅을 해도 여전히 캐스팅에 성공한다.
- 아래 두번째 파트는 어떤 값이 List<Int>인지 검사할 수는 없으므로 IllegalArgumentException이 발생하지 않고 sum은 Number 타입의 값을 리스트에서 가져와 더하려 시도해도 String을 Number 로 사용하려고 하기 때문에 ClassCastException이 발생한다.

```kotlin
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

- 타입 정보가 주어진 경우에는 is 검사를 수행 가능.
- 컴파일 시점에 c 컬렉션이 Int값을 저장한다는 사실이 알려져 있으니 c가 List<Int>인지 검사할 수 없다.

```kotlin
fun printSum(c: Collection<Int>) {
    if (c is List<Int>) {           
        println(c.sum())
    }
}
>>> printSum(listOf(1, 2, 3))
6
```

### 9.2.2 실체화한 타입 파라미터를 사용한 함수 선언

- 코틀린의 제네릭 타입의 타입 인자 정보는 실행시점에 지워진다. 따라서 제네릭 클래스의 인스턴스가 있어도 그 인스턴스를 만들 때 필요한 타입 인자를 알아낼 수 없다. 제네릭 함수의 타입 인자도 제네릭 함수가 호출되도 그 함수의 본문에서는 호출 시 쓰인 타입 인자를 알 수 없다.
- 인라인 함수의 타입 파라미터는 실체화되므로 실행 시점에 인라인 함수의 타입인자를 알 수 있다.

```kotlin
>>> fun <T> isA(value: Any) = value is T
Error: Cannot check for instance of erased type: T

inline fun <reified T> isA(value: Any) = value is T
>>> println(isA<String>("abc"))
true
>>> println(isA<String>(123))
false
```

- isA함수를 인라인 함수로 만들고 타입 파라미터를 reified로 지정하면 value의 타입이 T의 인스턴스인지 실행 시점에 검사 가능.
- 실체화한 타입 파라미터의 활용은 아래와 같이 가능. filterIsInstance의 타입 인자로 String을 지정해 함수의 반환타입을 List<String>으로 추론할 수 있게 된다.
- 타입 인자를 실행 시점에 알 수 있고, filterIsInstance는 그 타입 인자를 사용해 리스트의 원소 중에 인자와 타입이 일치하는 원소만을 추려낼 수 있다.

```kotlin
// 인자로 받은 컬렉션의 원소 중에서
// 타입 인자로 지정한 클래스의 인스턴스만을 모아서 만든
// 리스트를 반환하는 filterIsInstance
>>> val items = listOf("one", 2, "three")
>>> println(items.filterIsInstance<String>())
[one, three]
```

- 인라인함수에서는 이전 장에서 설명한대로, 컴파일러는 인라인의 함수의 본문을 구현한 바이트 코드를 그 함수가 호출되는 모든 시점에 삽입한다. 컴파일러는 실체화한 타입인자를 사용해서 인라인 함수를 호출하는 각 부분의 정확한 타입 인자를 알 수 있고, 타입인자로 쓰인 구체적인 클래스를 참조하는 바이트코드를 생성해서 삽입할 수 있다.
  - 자바에서는 reified 타입 파라미터를 사용하는 인라인 함수를 호출할 수 없다! 인라인도 자바에서는 다른 보통 함수처럼 호출하기 때문에

### 9.2.3 실체화한 타입 파라미터로 클래스 참조 대신

- java.lang.Class 타입 인자를 파라미터로 받는 API에 대한 코틀린 어댑터를 구축한 경우 실체화한 타입 파라미터를 자주 사용한다.
- 자바표준 api인 ServiceLoader를 사용해 서비스를 읽어들이려면 아래와 같이 호출하는데, ::class.java 구문은 코틀린 클래스에 대응하는 java.lang.Class 참조를 얻는 압법을 보여준다.
- Service::class.java 라는 코드는 Service.class라는 자바 코드와 같다.

```kotlin
val serviceImpl = ServiceLoader.load(Service::class.java)

// 위를 구체화한 타입 파라미터로 작성
val serviceImpl = loadService<Service>()

// 함수를 아래와 같이 정의 가능
inline fun <reified T> loadService(){
	return ServiceLoader.load(T::class.java)
}
```

### 9.2.4 실체화한 타입 파라미터의 제약

- 실체화한 타입 파라미터를 사용할 수 있는 경우
  - 타입 검사와 캐스팅(is, !is, as, as?)
  - 코틀린 리플렉션 API( 10장 설명 예정 )
  - 코틀린 타입에 대응하는 java.lang.Class를 얻기(::class.java)
  - 다른 함수를 호출 할 때 타입 인자로 사용
- 실체화한 타입의 파라미터가 하지 못하는 경우
  - 타입 파라미터 클래스의 인스턴스 생성.
  - 타입 파라미터 클래스의 동반 객체 메서드 호출
  - 실체화한 타입 파라미터를 요구하는 함수를 호출하면서 실체화하지 않은 타입 파라미터로 받은 타입을 타입 인자로 넘기기
  - 클래스, 프로퍼티, 인라인 함수가 아닌 함수의 타입 파라미터를 reified로 지정
    - 실체화한 타입 파라미터를 인라인 한 경우에만 사용할 수 있으므로 실체화한 타입 파라미터를 사용하는 함수는 자신에게 전달되는 모든 람다와 함께 인라이닝이 된다.
  - 람다 내부에서 타입 파라미터를 사용하는 방식에 따라서는 noinline 변경자를 함수 타입 파라미터에 붙여 인라이닝을 금지할 수 있음.

## 9.3 변성 : 제네릭과 하위 타입

- 변성은 List<String>와 List<Any>와 같이 기저 타입이 같고 인자가 다른 여러 타입이 서로 어떤 관계가 있는 설명하는 개념이다.

### 9.3.1 변성이 있는 이유: 인자를 함수에 넘기기

- MutableList<Any>가 필요한 곳에 MutableList<String>을 넘기면 안된다.

```kotlin
fun addAnswer(list: MutableList<Any>) {
    list.add(42)
}
>>> val strings = mutableListOf("abc", "bac")
>>> addAnswer(strings)
>>> println(strings.maxBy { it.length }) // 실행 시점에 예외가 발생한다.
ClassCastException: Integer cannot be cast to String
```

### 9.3.2 클래스, 타입, 하위 타입

- 제네릭 클래스가 아닌 클래스에서는 클래스 이름을 바로 타입으로 쓸 수 있고, 올바른 타입을 얻으려면 제네릭 타입의 타입 파라미터를 구체적인 타입 인자로 바꿔줘야 한다.
- 어떤 타입A의 값이 필요한 모든 장소에 어떤 타입 B의 값을 넣어도 아무 문제가 없다면 B는 타입 A의 하위 타입니다.(B가 A보다 구체적)
- 상위 타입은 하위 타입의 반대다. 하위 타입은 하위 클래스와 근본적으로 같다.
- 제네릭 타입을 인스턴스화 할 때 타입 인자로 서로 다른 타입이 들어가면 인스턴스 타입 사이의 하위 타입 관계가 성립하지 않으면 그 제네릭 타입을 무공변이라고 한다.

### 9.3.3 공변성: 하위 타입 관계를 유지

- 앞에 자주 쓴 코틀린의 List 인터페이스는 읽기 전용 컬렉션을 표현하는데, A가 B의 하위 타입이면 List<A>는 List<B>의 하위 타입이다. 그런 클래스나 인터페이스를 공변적이라고 한다.
- Producer<T>를 예로 공변성 클래스를 설명하는데 A가 B의 하위 타입일 때 Producer<A>가 Producer<B>의 하위 타입이면 공변적이며 이를 하위 타입 관계가 유지된다고 함.

```kotlin
interface Producer<out T> {  // 클래스가 T에 대해 공변적이라고 선언한다. 
    fun produce(): T
}
```

- 코틀린에서 제네릭 클래스가 타입 파라미터에 대해 공변적임을 표시하려면 타입 파라미터 이름 앞에 out을 넣어야 한다.
- 클래스의 타입 파라미터를 공변적으로 만들면 함수 정의에 사용한 파라미터 타입과 타입 인자의 타입이 명확히 일치하지 않더라도 그 클래스의 인스턴스를 함수 인자나 반환값으로 사용할 수 있다.
- T가 함수 반환 타입에 쓰인다면 T는 아웃 위치에 있고, T가 함수 파라미터 타입에 쓰인다면 T는 in 위치에 있다. 이런 경우 파라미터 타입 T의 값은 일단 소비 된다.
- 클래스 타입 파라미터 T 앞에 out 키워드를 붙이면 클래스 안에서 T를 사용하는 메서드가 아웃 위치에서만 T를 사용하게 허용하고 in 위치에서는 T를 사용 못하게 한다.

```kotlin
open class Animal {
    fun feed() { ... }
}
class Herd<T : Animal> {  // 이 타입 파라미터를 무공변성으로 지정한다. 
    val size: Int get() = ...
    operator fun get(i: Int): T { ... }
}

```

- 타입 파라미터 T에 붙은 out 키워드의 의미는 다음과 같이 정리
  - 공변성 : 하위 타입 관계가 유지
  - 사용제한 : T를 아웃 위치에서만 사용 가능

### 9.3.4 반공변성: 뒤집힌 하위 타입 관계

- 반공변 클래스의 경우 공변 클래스와 반대의 하위 타입 관계를 갖는다.

```kotlin
interface Comparator<in T> {
    fun compare(e1: T, e2: T): Int {...}    // T를 in 위치에 사용
}
```

- 위 예시의 인터페이스의 메서드는 T 타입의 값을 소비하기만 한다. 타입 B가 타입 A의 하위 타입인 경우 Consumer<A>가 Consumer<B>의 하위 타입인 관계가 성립하면 제네릭 클래스 Consumer<T>는 타입 인자 T에 대해 반공변이다.

### 9.3.5 사용 지점 변성 : 타입이 언급되는 지점에서 변성 지정

- 클래스를 선언하면서 벼성을 지정하는 방식을 선언 지점 변성이라고 함.
- 타입 파라미터가 있는 타입을 사용할 때마다 해당 타입 파라미터를 하위 타입이나 상위 타입 중 어떤 타입으로 대치할 수 있는지 명시하는 방식을 사용 지점 변성이라고 함.
- 클래스 안에서 어떤 타입 파라미터가 공변적이거나 반공변적인지 선언할 수 없는 경우에도 특정 타입 파라미터가 나타나는 지점에서 변성을 정할 수 있다.

```kotlin
fun <T> copyData(source: MutableList<T>, destination: MutableList<T>) {
    for (item in source) {
        destination.add(item)
    }
}
```

- 위 예시의 함수는 컬렉션의 원소를 다른 컬렉션으로 복사한다. 두 컬렉션 모두 무공변 타입이지만 원본 컬렉션에서는 읽기만 하고 대상 컬렉션에선느 쓰기만 한다. 이 경우 두 컬렉션의 원소타입이 정확히 일치할 필요가 없다.
- 위 예시를 여러 다른 리스트 타입에 대해 작동하게 아래처럼 만들 수 있다. 두 타입 파라미터는 원본과 대상 리스트의 우너소 타입을 표현한다.

```kotlin
fun <T: R, R> copyData(source: MutableList<T>, // source 원소 타입은 destination 원소 타입의 하위 타입이어야 한다.
                       destination: MutableList<R>) {
    for (item in source) {
        destination.add(item)
    }
}
>>> val ints = mutableListOf(1, 2, 3)
>>> val anyItems = mutableListOf<Any>()
>>> copyData(ints, anyItems)    // Int가 Any의 하위 타입이므로 이 함수를 호출할 수 있다.
>>> println(anyItems)
[1, 2, 3]
```

- 함수의 구현이 아웃 위치나 인 위치에 있는 타입 파라미터를 사용하는 메서드만 호출한다면 그런 정보를 바탕으로 함수 정의 시 타입 파라미터에 변성 변경자를 추가할 수 있다.

```kotlin
// 아웃-프로젝션 타입 파라미터를 사용하는 데이터 복사 함수
fun <T> copyData(source: MutableList<out T>,    // out 키워드를 타입을 사용하는 위치 앞에
                 destination: MutableList<T>) { // 붙이면 T 타입을 in 위치에 사용하는
                                                // 메소드를 호출하지 않는다는 뜻이다.
    for (item in source) {
        destination.add(item)
    }
}

// in 프로젝션 타입 파라미터를 사용하는 데이터 복사 함수
fun <T> copyData(source: MutableList<T>,
                 destination: MutableList<in T>) {  // 원본 리스트 원소 타입의 상위 타입을
                                                    // 대상 리스트 원소 타입으로 허용한다.
    for (item in source) {
        destination.add(item)
    }                     
}
```

### 9.3.6 스타 프로젝션 : 타입 인자 대신 * 사용

- 제네릭 타입 인자 정보가 없는 경우를 표현하기 위해 스타 프로젝션을 사용. 예로 원소 타입이 알려지지 않는 리스트는 List<*>라는 구문으로 표현이 가능하다.
- Mutable<*>는 MutableList<Any?>와 같지 않다.
  - Mutable<*>는 어떤 정해진 구체적인 타입의 원소만을 담는다.
  - MutableList<Any?>는 모든 타입의 원소를 담을 수 있다.
- 컴파일러는 MutableList<*>를 아웃 프로젝션 타입으로 인식한다.

```kotlin
>>> val list: MutableList<Any?> = mutableListOf('a', 1, "qwe")
>>> val chars = mutableListOf('a', 'b', 'c')
>>> val unknownElements: MutableList<*> =                
...         if (Random().nextBoolean()) list else chars
>>> unknownElements.add(42) // 컴파일러는 이 메소드 호출을 금지한다.                              
Error: Out-projected type 'MutableList<*>' prohibits
the use of 'fun add(element: E): Boolean'
>>> println(unknownElements.first()) // 원소를 가져와도 안전하다. first()는 Any? 타입의 원소를 반환한다. 
a
```

- 타입 파라미터를 시그니처에서 전혀 언급하지 않거나 데이터를 읽기는 하지만 그 타입에는 관심이 없는 경우와 같이 타입 인자 정보가 중요하지 않을 때도 스타 프로젝션 구문을 사용할 수 있다.

```kotlin
un printFirst(list: List<*>) {  // 모든 리스트를 인자로 받을 수 있다. 
    if (list.isNotEmpty()) { // isNotEmpty()에서는 제네릭 타입 파라미터를 사용하지 않는다. 
        println(list.first()) // first()는 이제 Any?를 반환하지만 여기서는 그 타입만으로 충분하다. 
    }
}
>>> printFirst(listOf("Svetlana", "Dmitry"))
Svetlana
```