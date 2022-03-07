# Kotlin Ch.7 : 연산자 오버로딩과 기타 관례

- 어떤 언어 기능과 미리 정해진 이름의 함수를 연결해주는 기법을 코틀린에서는 ‘관례’라고 부른다.
- 코틀린은 언어기능을 타입에 의존하지 않고 관례에 의존한다.
    - 그 이유는 기존 자바 클래스를 코틀린 언어에 적용하기 위해서 라고 한다.

# 7.1 산술 연산자 오버로딩

## 7.1.1 이항 산술 연산 오버로딩

- 아래에 포인트 클래스에 대해서 + 연산을 다음과 같이 구현할 수 있다.

```kotlin
data class Point(val x:Int, val y:Int)
```

```kotlin
data class Point(val x:Int, val y:Int){
	
	// operator 키워드를 붙이고 plus 라는 이름의 연산자 함수를 정의 
	operator fun plus(other:Point):Point{
		
		return Point(x + other.x, y + other.y)

	}

```

- 연산자를 오버로딩하는 경우 operator 키워드가 항상 붙어야 하며, 이를 지키지 않고 관례에 쓰이는 함수명으로 우연히 함수를 작성했을 경우에는 발생하는 오류를 통해 이를 찾아서 고칠 수 있다.
- [ a + b → a.plus(b) ] 와 같이 + 연산자는 plus 함수 호출로 컴파일이 된다.
- 연산자를 다음과 같이 확장함수로 정의 할 수도 있다.

```kotlin
operator fun Point.plus(other: Point) : Point {

	return Point(x + other.x, y+other.y)

}
```

- 코틀린에서는 프로그래머가 직접 연산자를 만들어 사용할 수 없고, 언어에서 미리 정해둔 연산자만 오버로딩할 수 있고 관례에 따르기 위해 클래스에서 정의해야하는 이름이 연산자 별로 정해져있다.
    - a * b : times
    - a / b : div
    - a % b : mod / rem(1.1 부터)
    - a + b : plus
    - a - b : minus
- 연산자의 우선 순위는 표준 숫자타입에 대한 연산자 우선순위와 같다.
- 연산자를 정의할 때 두 피연산자가 꼭 같은 타입일 필요는 없다.

```kotlin
operator fun Point.times(scale : Double) : Point {
	return Point((x * scale).toInt(), (y * scale).toInt())
}
```

- 코틀린의 자동으로 교환 법칙을 지원하지는 않기 때문에 scale * x 의 경우 이에 대응하는 함수를 더 정의해줘야된다.
- 연산자 함수의 반환 타입이 꼭 두 피연산자 중 하나와 일치해야하는 것도 아니다.

```kotlin
operator fun Char.times(count : Int) : String{
	return toString().repeat(count)
}

>>> println("a" * 3)
aaa
```

- 이와 같은 결과 타입 조합도 완전히 합법적인 연산자 오버로딩이다.
- 일반 함수와 마찬가지로 operator 함수도 오버로딩할 수 있다. 따라서 이름은 같지만 파라미터 타입이 서로 다른 연산자 함수를 여럿 만들 수 있다.
- 코틀린은 표준 숫자 타입에 대해 비트 연산자를 정의하지 않는다. 대신 중위 연산자 표기법을 지원하는 일반 함수를 사용해 비트 연산을 수행한다.
    - 코틀린에서 비트 연산을 수행하는 함수 목록
        - shl : 왼쪽 시프트(자바 <<)
        - shr : 오른쪽 시프트 (부호 비트 유지, 자바 >>)
        - ushr : 오른쪽 시프트 (0으로 부호 비트 설정, 자바 >>>)
        - and : 비트 곱 (자바 &)
        - or : 비트 합 (자바 |)
        - xor : 비트 배타 합 (자바 ^)
        - inv : 비트 반전 (자바 ~)

## 7.1.2 복합 대입 연산자 오버로딩

- 대입과 산술 연산을 하나로 합친 복합 대입연산자에도 위와 같은 오버로딩이 자동으로 지원된다.

```kotlin
>>> var point = Point(1, 2)
>>> point += Point(3, 4)
>>> println(point)
Point(x=4, y=6)

// point = point + point(3, 4) 와 같다고 보면 됨
```

- 변수가 변경 가능한 경우에만 복합 대입 연산자를 사용할 수 있다.
- 경우에 따라 += 연산이 객체에 대한 참조를 다른 참조로 바꾸기보다 원래 객체의 내부 상태를 변경하게 만들고 싶을 때가 있다. 변경 가능한 컬렉션에 원소를 추가하는 경우가 대표적인 예다.

```kotlin
>>> val numbers = ArrayList<Int>();
>>> numbers += 42;
>>> println(numbers[0])
42
```

- 반환 타입이 Unit인 plusAssign 함수를 정의하면 코틀린은 += 연산자에 그 함수를 사용한다.
    - 다른 복합 대입 연산자 함수도 비슷하게 minusAssign, timesAssign 등의 이름을 사용
- 코틀린 표준 라이브러리는 변경 가능한 컬렉션에 대해 plusAssign을 정의하며 위에 예제를 다음과 같이 작성할 수 있다

```kotlin
operator fun <T> MutableCollection<T>.plusAssign(element : T) {
	this.add(element)
}
```

- 이론적으로 +=을 plus와 plusAssign 양쪽으로 컴파일할 수 있다. 그래서 어떤 클래스에서 이 함수를 둘다 정의하고 +=에 사용하고 있다면 오류를 보고한다.
- 일반 연산자를 사용하거나, var을 val로 바꿔서 plusAssign 쪽에서 적용이 불가능하게 만들수도 있지만, 가장 좋은 방법은 일관성 있게 plus 와 plusAssign 함수를 동시에 정의하지 않는 것이 좋다.
- += 연산자는 a = a.plus(b) 아니면  a.plusAssign(b) 와같은 함수 호출로 번역된다.
- 코틀린 표준 라이브러리는 컬렉션에 대해 +와 - 는 새로운 컬렉션을 반환하고 += 와 -= 연산자는 항상 변경이 가능한 컬렉션에 적용해 메모리에 있는 객체 상태를 변화시킨다.
- 또한 읽기 전용 컬렉션에서 += 와 -=는 변경을 적용한 복사본을 반환하기 때문에 var로 선언한 변수가 가리키는 읽기전용 컬렉션에 만 적용이 가능하다.
- 피연산자로는 개별 원소를 사용하거나 원소 타입이 일치하는 다른 컬렉션을 사용할 수 있다.

```kotlin
>>> val list = arrayListOf(1, 2)
>>> list += 3
>>> val newList = list + listOf(4, 5)

>>> println(list)
[1, 2, 3]

>>> println(newList)
[1, 2, 3, 4, 5]
```

## 7.1.3 단항 연산자 오버로딩

- 단항 연산자는 하나의 값에만 작용하는 연산자이다. 이항연산자와 마찬가지로 오버로딩을 하는 절차는 같이 미리 정해진 이름의 함수를 선언하고 operator로 표시하면 된다.

```kotlin
operator fun Point.unaryMinus(): Point{

	return Point(-x, -y)

}
```

- 단항 연산자를 오버로딩하기 위해 사용하는 함수는 인자를 취하지 않는다.
- 코틀린에서 오버로딩할 수 있는 모든 단항 연산자는 다음과 같다.
    - +a : unaryPlus
    - -a : unaryMinus
    - !a : not
    - ++a, a++ : inc
    - -- a, a -- : dec
- inc 나 dec 함수를 정의해 오버로딩을 하는 경우 컴파일러는 일반적인 값에 대한 전위와 호위 증가 감사 연산자와 같은 의미를 제공한다.

```kotlin
operator fun BigDecimal.inc() = this.BigDecimal.ONE

>>> var bd = BigDecimal.ZERO
>>> println(bd++)
0
>>> println(++bd)
2
```

- 전위와 후위 연산을 처리하기 위해 별다른 처리를 해주지 않아도 제대로 증가 연산자가 작동한다.

# 7.2 비교 연산자 오버로딩

- 코틀린은 모든 객체에 대해 비교 연산도 수행할 수 있는데, 자바에서 equals 나 compareTo를 호출하여 사용하는 것과 달리 == 비교 연산자를 직접 사용 가능하다.

## 7.2.1 동등성 연산자 : equals

- == 연산자 호출을 equals 메서드 호출로 컴파일 하는 것은 지금까지 설명한 관례를 적용한 것과 같다.
- != 연산자 호출도 equals 호출로 컴파일되며, 뒤집은 비교결과를 반환하는 것이다.
- == 와 != 연산자는 내부에서 인자가 널인지 검사를 하므로 널이 될 수 있느 값에도 적용할 수 있다.
    - [ a == b ] → [ a?.equals(b) ?: (b == null) ]

```kotlin
class Point(val x: Int, val y: Int){
	override fun equals(obj:Any?) : Boolean {
		if (obj === this) return true
		if (obj !is Point) return false
		return obj.x == x && obj.y == y
	}	
}

>>> println(Point(10, 20) == Point(10, 20))
true
>>> println(Point(10, 20) != Point(5, 5))
true
>>> println(null == Point(1, 2))
false
```

- data 클래스에서는 이미 생성되어있는 equals를 직접 구현한다면 위와 같을 것.
- 식별자 비교 연산자(===)는 자바 == 연산자와 같아서 두 피연산자가 서로 같은 객체를 가리키는지를 비교한다. equals를 구현할때는 === 연산자를 사용해 자기 자신과의 비교를 최적화하는 경우가 많으며, 이를 오버로딩 할 수는 없다.
- equals 함수에 override가 붙어있는 이유는 이 함수가 다른 연산자 오버로딩 관례와 달리 이미 Any에 정의된 메서드이기 때문이며, operator 키워드도 상위 클래스의 지정이 적용되어 여기서는 붙이지 않는다.

## 7.2.2 순서 연산자 : compareTo

- 자바의 Comparable 인터페이스를 구현하면 정렬, 최대값, 최소값 등을 비교하는 클래스를 구현할 때 사용된다. 여기에 들어있는 compareTo 메서드는 한 객체와 다른 객체의 크기를 비교해 정수로 나타내는데  > 와 < 등의 연산자로는 원시타입의 값만 비교할 수 있으며, 다른 모든 타입의 값에는 element1.compareTo(element2)를 명시적으로 사용해야한다.
- 코틀린에서는  비교 연산자들로 compareTo 호출로 컴파일해서 이를 사용할 수 있게 해준다.

```kotlin
Class Person (
		val firstName: String, val lastName: String
) : Comparable<Person> {
	override fun compareTo(other:Person) : Int {
		return compareValuesBy(this, other, Person::lastName, Person::firstName)
	}
}

>>> val p1 = Person("Alice", "Smith")
>>> val p2 = Person("Bob", "Johnson")
>>> println(p1 < p2)
false
```

- 위 예시에 정의한 객체의 Comparable 인터페이스를 코틀린 뿐 아니라 자바 쪽의 컬렉션 정렬 메서드 등에서도 사용할 수 있다.
- 또한 Comparable의 compareTo에도 operator 변경자가 붙어있으므로 오버라이딩 함수에는 키워드를 붙일 필요가 없다.
- compareValuesBy는 두 객체와 여러 비교 함수를 인자로 받고 첫번째 비교 함수에 두 객체를 넘겨 두 객체가 같지 않다는 결과가 나오면 그 결과값을 즉시 반환하고, 두 객체가 같다는 결과가 나오면 두 번째 비교함수를 통해 두 객체를 비교한다. 그리고 대소를 알려주는 0 이 아닌 값이 처음 나올 때까지 인자로 받은 함수를 차례로 호출해 두 값을 비교하며, 모든 함수가 0을 반환하면 0을 반환한다. 각 비교 함수는 람다나 프로퍼티/메서드 참조 일수도 있다.
    - 구현은 아래와 같이
    
    ```kotlin
    fun <T> compareValuesBy( a: T, b: T, vararg selectors:(T) -> Comparable(*)?) : Int
    ```
    
    - 필드를 직접 비교하면 코드는 조금 더 복잡해지지만 비교 속도는 훨씬 빠르다
- Compareable 인터페이스를 구현하는 모든 자바 클래스를 코틀린에서는 간결한 연산자 구문으로 비교할 수 있고 자바 클래스에 대해 사용하기 위해 특별히 확장 메서드를 만들거나 할 필요는 없다.

# 7.3 컬렉션과 범위에 대해 쓸 수 있는 관례

## 7.3.1 인덱스로 원소에 접근 : get 과 set

- 코틀린에서는 인덱스 연산자로 원소를 읽는 연산은 get 연산자 메서드로 변환되고, 원소를 사용하는 연산은 set 연산자 메서드로 변환된다.
- Map과 MutableMap 인터페이스에는 그 두 메서드가 이미 들어있다. 상단 예시에 사용한 Point 클래스에 이런 메서드를 추가해보면 다음과 같다.

```kotlin
operator fun Point.get(index: Int{	
	return when(Index){	
		0 -> x
		1 -> y
		else -> throw IndexoutOfBoundsException("Invalid coordinate $Index")
	}
}

>>> val p = Point(10, 20)
>>> println(p[1])
20
```

- get 메서드를 만들고 operator 변경자를 붙이면, Point 타입에 사용하는 경우 []는 get 메서드로 변환된다.
- get 메서드에는 Int가 아닌 타입도 들어갈 수 있는데 대표적으로 Map의 키 타입과 같은 임의의 타입이 될 수도 있다. 또 여러 파라미터를 사용하는 get을 정의할 수도 있다.
    - 예시로 2차원 행렬이나 배열을 표현하는 클래스에 operator fun get(rowIndex: Int, colIndex: Int)를 정의하면 matrix[row, col]로 그 메서드를 호출할 수도 있다.
    - 컬렉션 클래스가 다양한 키 타입을 지원해야 한다면 다양한 파라미터 타입에 대해 오버로딩한 get 메서드를 여럿 정의할 수도 있다.
- 인덱스에 해당하는 컬렉션 원소를 쓰고 싶을 때는 set이라는 이름의 함수를 정의하면 된다. 불변 클래스에는 set을 쓰는 의미가 없고 변경이 가능한 클래스에서 사용.

```kotlin
data class MutablePoint(var x: Int, var y:Int)

operator fun MutablePoint.set(index: Int){
	when(index){

		0 -> x = value
		1 -> y = vale
		else ->
			throw IndexoutOfBoundsException("Invalid coordinate $index")
	}
}

>>> val p = MutablePoint(10, 20)
>>> p[1] = 42
>>> println(p)
MutablePoint(x=10, y=42)
```

- set은 받은 마지막 파라미터 값을 대입문의 우항에 넣고, 나머지 파라미터 값은 인덱스 연산자에 넣는다.

## 7.3.2 in 관례

- 컬렉션이 지원하는 다른 연산자인 in은 객체가 컬렉션에 들어있는지 검사한다.
- in 연산자가 대응하는 함수는 contains다.

```kotlin
data class Rectangle(val upperLeft:Point, val lowerRight:point)

operator fun Rectangle.contains(p:Point) : Boolean {

	return p.x in upperLeft.x until lowerRight.x &&
		p.y in upperLeft.y until lowerRight.y
}

>>> val rect = Rectangle(Point(10, 20), Point(50, 50))
>>> println(Point(20, 30) in rect)
true
>>> println(Point(5, 5) in rect)
false
```

- in 의 우항에 있는 객체는 contains 메서드의 수신 객체가 되고, 좌항에 있는 객체는 인자로 전달된다.
- ‘열린 범위’는 끝 값을 포함하지 않는 범위를 말한다. [ 10 ..  20 ]
    - 10 until 20으로 만드는 열린 범위는 10이상 19이하인 범위로 20은 포함되지 않는다.

## 7.3.3 rangeTo 관례

- 범위를 만들려면 .. 구문을 사용하는데 이 연산자는 rangeTo 함수를 간략하게 표현하는 방법이다.
    - [ start..end ] → [ start.rangeTo(end) ]
- rangeTo 함수는 범위를 반환하는데 이는 아무 클래스에나 정의 가능하지만, 어떤 클래스가 Comparable 인터페이스를 구현한다면 rangeTo를 정의할 필요가 없다.
- 코틀린 표준 라이브러리로 비교 가능한 원소로 이뤄진 범위를 쉽게 만들 수 있고 모든 Comparable 객체에 대해 적용 가능한 rangeTo 함수가 들어있다.

```kotlin
operator fun <T:Comparable<T>> T.rangeTo(that:T) : ClosedRange<T>
```

- 위 함수는 범위를 반환하며 어떤 원소가 그 범위 안에 들어있는지 in을 통해 검사 가능하다.

```kotlin
>>> val now = LocalDate.now()
>>> val vacation = now..now.plusDays(10)
>>> println(now.plusWeeks(1) in vacation)
true
```

- 위 예시에서 rangeTo 함수는 LocalDate의 멤버가 아니며, 위에 설명한대로 Comparable에 대한 확장 함수이다.
- rangeTo 연산자는 다른 산술 연산자보다 우선순위가 낮지만 괄호로 묶어서 사용하는게 혼동을 줄인다.

```kotlin
>>> val n = 9
>>> println(0 .. (n+1))
0 .. 10
```

- 0 .. n.forEach{ } 와 같은 식은 컴파일이 될 수 없다. 범위 연산자는 우선 순위가 낮아서 범위의 메서드를 호출하면 범위를 괄호로 둘러싸야 한다.

```kotlin
>>> (0..n).forEach { print(it) }
```

## 7.3.4 for 루프를 위한 iterator 관례

- 코틀린의 for루프는 위의 범위 검사와 같이 in 연산자를 사용한다.
    - 하지만 이 경우에 for(x in list){ ... } 와 같은 문장은 list.iterator()를 호출해서 이터레이터를 얻은 후 자바와 마찬가지로 그 이터레이터에 대해 hasNext와 next 호출을 반복하는 식으로 변환된다.
    - 코틀린에서는 이 또한 관례로, iterator 메서드를 확장 함수로 정의할 수 있다. 이 덕분에 자바와는 달리 문자열에 대한 for 루프가 가능한다. 코틀린 표준 라이브러리는 String의 상위 클래스인 CharSequence에 대한 iterator 확장함수를 제공한다.

```kotlin
operator fun CharSequence.iterator():CharIterator
>>> for(c in "abc"){ }
```

- 클래스 안에 직접 iterator 메서드를 구현할 수도 있다.

```kotlin
operator fun ClosedRange<LocalDate>.iterator(): Iterator<LocalDate> = 
	object: Iterator<LocalDate> {
		var current = start
		
		override fun hasNext() =
			current <= endInclusive
	
		override fun next() = current.apply{
			current = plusDays(1)
		}
	}

>>> val newYear = LocaDate.ofYearDay(2017, 1)
>>> val daysOff = newYear.minusDays(1)..newYear
>>> for(dayOff in daysOff){ println(dayOff) }
2016-12-31
2017-01-01
```

- 여기서 rangeTo 라이브러리 함수는 ClosedRange의 인스턴스를 반환한다. 코드에서 이에 대한 확장 함수 iterator를 정의했기 때문에 LocalDate의 범위 객체를 for 루프에 사용할 수 있다.

# 7.4 구조 분해 선언과 component 함수

- ‘구조 분해’를 사용하면 복합적인 값을 분해해서 여러 다른 변수를 한꺼번에 초기화할 수 있다.

```kotlin
>>> val p = Point(10, 20)
>>> val (x, y) = p
>>> println(x)
10
>>> println(y)
20
```

- 구조 분해 선언은 일반 변수 선언과 비슷해보이지만 =의 좌변에 여러 변수를 괄호로 묶었다는 점이 다르다.
- 구조분해 선언 내부에서 사용되는 관례는 각 변수를 초기화하기 위해 componentN이라는 함수를 호출한다. N은 구조 분해 선언에 있는 변수 위치에 따라 붙는 번호이며 아래와 같이 컴파일된다.
    - [ val (a, b) = p ] → [ val a = p.component1() , val b = p.component2() ]
- data 클래스의 주 생성자에 들어있는 프로퍼티에 대해서는 컴파일러가 자동으로 componentN 함수를 만들어준다. 구현 되는 모습은 아래와 같다.

```kotlin
class Point(val x: Int, val y:Int){

	operator fun component1() = x
	operator fun component2() = y

}
```

- 구조 분해 선언은 함수에서 여러 값을 반환할 때 유용한데, 여러 값을 한꺼번에 반환해야하는 함수가 있으면 반환해야할 모든 값이 들어갈 데이터 클래스를 정의하고, 함수의 반환 타입을 그 데이터 클래스로 바꾼다.
- 구조분해 선언 구문을 사용하면 이런 함수가 반환하는 값을 쉽게 풀어서 여러 변수에 넣을 수 있다.

```kotlin
data class NameComponents(val name: String, val extension: String)

fun splitFilename(fullName: String) : NameComponents {

	val result = fullName.split('.', limit =2)
	return NameComponents((result[0], result[1])

}

>>> val (name, ext) = splitFilename("example.kt")
>>> println(name)
example
>>> println(ext)
kt
```

- 배열이나 컬렉션에도 componentN함수가 있음을 안다면 이 예제를 더 개선할 수 있다. 크기가 정해진 컬렉션을 다루는 경우 구조분해가 특히 더 유용하다.

```kotlin
data class NameComponents(val name: String, val extension: String)

fun splitFilename(fullName: String) : NameComponents {

	val (name, extension) = fullName.split('.', limit = 2)
	return NameComponents(name, extension)
	
}
```

- componentN을 무한하게 선언할 수는 없지만, 코틀린 표준 라이브러리에서는 맨 앞의 다섯 원소에 대한 componentN을 제공한다. 컬렉션의 크기를 벗어나는 위치의 원소에 대한 구조 분해 선언을 사용하면 실행시점에 IndexOutOfBoundsException 등의 예외가 발생한다.
- 반면 여섯개 이상의 변수를 사용하는 구조 분해를 컬렉션에 대해 사용하면 component6 등에 의한 컴파일 오류가 발생한다.
    - error: destructuring declaration initializer of type List<Int> must have a ‘component6()’ function

## 7.4.1 구조 분해 선언과 루프

- 함수 본문 내의 선언문 뿐 아니라 변수 선언이 들어갈 수 있는 장소라면 어디든 구조 분해 선언을 사용할 수 있으며 루프 안에서도 가능하다.
- 맵의 원소에 대해 이터레이션할 때 구조 분해 선언이 유용하다.

```kotlin
fun printEntries(map: Map<String, String>){

	for((key, value) in map){
		println("$key -> $value")
	}
}

>>> val map = mapOf("Oracle" to "Java", "JetBrains" to "Kotlin")
>>> printEntries(map)
Oracle -> Java
JetBrains -> Kotlin
```

- 위 예시에서는 여기서 지금까지 설명한 두 가지의 관례가 사용된다.
    - 하나는 객체를 이터레이션하는 관례로, 코틀린 표준 라이브러리에는 맵에 대한 확장함수로 iterator가 들어있다. 이는 맵 원소에 대한 이터레이터를 반환한다.
    - 다른 하나는 구조 분해 선언으로 Map.Entry에 대한 확장 함수로 component1과 component2를 제공한다. 앞으로 루프는 이런 확장함수를 사용하는 다음 코드와 같다.

```kotlin
for(entry in map.entries){
	val key = entry.component1()
	val value = entry.component2()
	...
}
```

- 이와 같이 코틀린 관례를 사용할 때는 확장함수가 중요한 역할을 한다.

# 7.5 프로퍼티 접근자 로직 재활용 : 위임 프로퍼티

- 위임 프로퍼티를 사용하면 값을 뒷받침하는 필드에 단순히 저장하는 것보다 더 복잡한 방식으로 작동하는 프로퍼티를 쉽게 구현할 수 있고 그 과정에서 접근자 로직을 매번 재구현할 필요도 없다.
- 프로퍼티는 위임을 통해 자신의 값을 필드가 아니라 데이터베이스 테이블이나 브라우저 세션, 맵 등에 저장할 수 있다. 위임은 객체가 직접 작업을 수행하지 않고 다른 도우미 객체가 그 작업을 처리하게 맡기는 디자인 패턴을 말하는데 이 도우미 객체를 ‘위임 객체’라고 부른다.

## 7.5.1 위임 프로퍼티 소개

- 위임 프로퍼티의 일반적인 문법은 다음과 같다.

```kotlin
class Foo {
	var p: Type by Delegate()
}
```

- p 프로퍼티는 접근자 로직을 Delegate 클래스의 인스턴스인 위임객체로 위임하는데, by 뒤에 있는 식을 계산해서 위임에 쓰일 객체를 얻는 것이다. 프로퍼티 위임 객체가 따라야하는 관례를 따르는 모든 객체는 위임에 사용할 수 있다.

 

```kotlin
// 위임 객체를 사용하는 클래스
class Foo {
	private val delegate = Delegate()

	var p: Type
	set(value: Type) = delegate.setValue(..., value)
	get() = delegate.getValue(...)

}

// getValue와 setValue를 메서드를 제공하는 Delegate 클래스
class Delegate {
	operator fun getValue(...){ ... }
	operator fun setValue(...){ ... }
}

>>> val foo = Foo()
>>> val oldValue = foo.p // 이때 내부에서 getValue를 호출
>>> foo.p = newValue // 이때 내부에서 setValue를 호출
```

- 위와 같이 위임 관례를 따르는 Delegate 클래스는 getValue와 setValue 메서드를 제공해야하고 관례를 사용하는 다른 경우와 마찬가지로 이 두 메서드는 멤버 혹은 확장 메서드 일 수 있다.
- foo.p를 일반 프로퍼티 처럼 사용하고 있지만 실제로는 Delegate에게 게터나 세터를 위임하였고 거기로부터 메서드를 호출한다.

## 7.5.2 위임 프로퍼티 사용 : by lazy()를 사용한 프로퍼티 초기화 지연

- 코틀린 라이브러리는 프로퍼티 위임을 사용해 프로퍼티 초기화를 지연시켜 줄 수 있다.
- 지연 초기화는 객체의 일부분을 초기화하지 않고 남겨두었다가 실제로 그 부분의 값이 필요할 때 초기화하여 쓰는 패턴이다. 초기화 과정에 자원을 많이 사용하거나 객체를 사용할 때마다 꼭 초기화를 하지 않아도 되는 프로퍼티에 대해 지연 초기화 패턴을 쓸 수 있다.
- 아래 예시에서는 person 클래스에서 이메일 목록이라는 프로퍼티의 값을 최초로 사용할 때 단 한번만 이메일을 데이터베이스에서 가져오는 함수가 있다고 가정한다.

```kotlin
class Email { ... }

fun loadEmails(person: Person) : List<Email>{

	println("${person.name}의 이메일을 가져옴")

	return listOf( ... )

}
```

- 아래는 이메일을 불러오기 전에는 null을 저장하고, 불러온 다음에는 이메일 리스트를 저장하는 _emails 프로퍼티를 추가해서 지연 초기화를 구현한 클래스이다.

```kotlin
class Person(val name: String){
	// emails 의 위임객체
	private var _emails : List<Email>? = null
	 
	val emails: List<Email>
		get() {
			
			if(_emails == null){ // 최초 접근 시 이메일을 가져온다.
				_emails = loadEmails(this)
			}
			return _emails!! // 저장해둔 데이터가 있으면 그 데이터를 반환
		}
}

>>> val p = Person("Alice")
>>> p.emails
Load emails for Alice
>>> p.emails // <- 최초로 이메일을 읽을 때 한번만 이메일을 가져오기 때문에 문구가 안뜬다.
```

- 여기서 _emails 라는 프로퍼티에 값을 저장하고 다른 프로퍼티인 emails는 해당 이를 읽어오는 연산을 제공하며 _emails는 널이 될 수 있는 타입인 반면 emails는 널이 될 수 없는 타입이므로 프로퍼티는 두 개를 사용한다. 이런 기법을 뒷받침하는 프로퍼티(Backing Property)라고 한다.
- 그런데! 상기 방식은 지연 초기화 해야하는 프로퍼티가 많아지면 코드도 많아질 것이고, 스레드 안전하지 않아서 언제나 제대로 작동한다고 말할 수도 없다.
- 코틀린에서는 위임 프로퍼티를 사용하여 데이터를 저장할 때는 뒷받침하는 프로퍼티와 값이 오직 한번만 초기화됨을 보장하는 게터 로직을 함께 캡슐화한다. 예제와 같은 경우를 위한 위임 객체를 반환하는 표준 라이브러리 함수가 바로 lazy이다.

```kotlin
class Person(val name: String){

	val emails by lazy { loadEmails(this) }

}
```

- lazy 함수는 크틀린 관례에 맞는 시그니처의 getValue 메서드가 들어있는 객체를 반환하여 lazy를 by 키워드와 함께 사용해 위임 프로퍼티를 만들 수 있다. lazy 함수의 인자는 값을 초기화할 때 호출할 람다라고 보면 된다.
- lazy 함수는 기본적으로 스레드 안전하며, 필요에 따라 동기화에 사용할 락을 이 함수에 전달 할 수도 있고, 다중 스레드 환경에서 사용하지 않을 프로퍼티를 위해 이 함수가 동기화를 하지 못하게 막을 수도 있다.

## 7.5.3 위임 프로퍼티 구현

- 책에서는 위임 프로퍼티 구현의 예시로 객체 프로퍼티가 바뀔 때마다 리스너에게 변경을 통지하는 자바의PropertyChangeSupport과 PropertyChangeEvent 와 같은 클래스와 같은 기능을 코틀린에서 구현하고 이를 리펙토링하는 것을 보여준다.
- PropertyChangeSupport 클래스는 리스너의 목록을 관리하고, PropertyChangeEvent 클래스 이벤트가 들어오면 목록의 모든 리스너에게 이벤트를 통지한다.
- 자바 빈 클래스의 필드에 PropertyChangeSupport 인스턴스를 저장하고 프로퍼티 변경시 그 인스턴스에게 처리를 위임하는 형식으로 이런 통지 기능을 주로 구현한다. 필드를 모든 클래스에 추가하지는 않기 위해 이 인스턴스를 changeSupport라는 필드에 저장하고 프로퍼티 변경 리스너를 추적해주는 도우미 클래스를 만든다. 리스너 지원이 필요한 클래스는 이 도우미 클래스를 확장해서 changeSupport에 접근이 가능하다.

```kotlin
open class PropertyChangeAware {
	protected val changeSupport = PropertyChangeSupport(this)

	fun addPropertyChangeListener(listener : PropertyChangeListener){
		changeSupport.addPropertyChangeListener(listener)
	}

	fun removePropertyChangeListener(listener:PropertyChangeListener){
		changeSupport.removePropertyChangeListener(listener)
	}
}
```

- 이제 이를 사용할 Person 클래스를 작성한다. 읽기 전용 프로퍼티와 변경 가능한 프로퍼티 둘을 정의한다. 이 클래스에서 이 변경 가능한 프로퍼티가 바뀌면 그 사실을 리스너에게 통지할 것이다.

```kotlin
class Person (
		val name: String, age: Int, salary: Int
) : PropertyChangeAware(){

	var age : Int = age
		set(newValue){
			val oldValue = field //-> 뒷받침하는 필드에 접근할 때 'field'식별자를 사용한다.
			field = newValue
			changeSupport.firePropertyChange(
				"age", oldValue, newValue)
		}

	var salary : Int = salary
		set(newValue) {
			val oldvalue = field
			field = newValue
			changeSupport.firePropertyChange(
				"salary", oldValue, newValue)
		}	

}

>>> val p = Person("Dmitry", 34, 2000)
>>> p.addProperyChangeListener(
			PropertyChangeListener { event ->
				println("Property ${event.propertyName} changed " +
					"from ${event.oldValue} to ${event.newValue}")
			}
		)
```

- 이 코드는 field 키워드를 사용해서 age와 salary 프로퍼티를 뒷받침하는 필드에 접근 하는 방법을 보여준다. 세터는 중복이 많이 보이는데, 프로퍼티의 값을 저장하고 필요에 따라 통지를 보내주는 클래스를 추출하면 다음과 같이 코드가 정리된다. 이 코드는 실제로 위임이 작동하는 방식과 비슷하다.

```kotlin
class ObservableProperty(
	val propName : String, var propValue : Int,
	val changeSupport : PropertyChangeSupport
){
	fun getValue() : Int = propValue
	fun setValue(newValue : Int) {
		val oldValue = propValue
		propValue = newValue
		changeSupport.firePropertyChange(propName, oldValue, newValue)
	}
}

class Person (
		val name: String, age: Int, salary: Int
) : PropertyChangeAware(){
	
	val _age = ObservableProperty("age", age, changeSupport)
	var age : Int
		get() = _age.getValue()
		set(value){ _age.setValue(value) }

	val _salary = ObservableProperty("salary", salary, changeSupport)
	var salary : Int
		get() = _salary.getValue()
		set(value){ _salary.setValue(value) }

}
```

- 프로퍼티 값을 저장하고, 값이 바뀌면 자동으로 변경 통지를 전달해주는 클래스를 만들었으며, 로직의 중복을 제거했다. 하지만 각 프로퍼티마다 ObservableProperty를 만들어야 되고 게터와 세터에서 해당 클래스로 작업을 위임하는 분비 코드가 아직 많이 필요하다. 이 부분을 코틀린 위임 프로퍼티 기능을 통해 없앨 수 있다.
- 그전에 ObservableProperty 에 있는 두 메서드의 시그니처를 코틀린의 관례에 맞게 수정해야한다.

```kotlin
class ObservableProperty(
	var propValue : Int, val changeSupport : PropertyChangeSupport
){
	operator fun getValue(p:Person, prop:KProperty<*>): Int = propValue
	operator fun setValue(p:Person, prop:KProperty<*>, newValue: Int){
		val oldValue = propValue
		propValue = newValue
		changeSupport.firePropertyChange(prop.name, oldValue, newValue)
	}
}
```

- 위 코드는 이전과 비교해 다음과 같은 차이가 있다.
    - 코틀린 관례를 사용하는 다른 함수와 마찬가지로 getValue와 setValue 함수에도 operator 변경자가 붙는다.
    - 이 두 함수는 프로퍼티가 포함된 객체와 프로퍼티를 표현하는 객체를 파라미터로 받는다. KProperty 타입의 객체를 사용해 프로퍼티를 표현하는데 이에 대해서는 이후에 자세히 배울 예정. 지금은 [KProperty.name](http://KProperty.name) 을 통해 메서드가 처리할 프로퍼티 이름을 알 수 있다는 점만 기억할 것.
    - KProperty 인자를 통해 프로퍼티의 이름을 전달 받으므로 주 생성자에서는 name 프로퍼티를 없앤다.
- 이와 같이 최종적으로 코틀린이 제공하는 위임 프로퍼티를 사용해서 예제 코드를 작성할 수 있다.

```kotlin
class Person(
		val name: String, age: Int, salary: Int
) : PropertyChangeAware(){
	var age : Int by ObservableProperty(age, changeSupport)
	var salary : Int by ObservableProperty(salary, changeSupport)
}
```

- by 키워드를 통해 위임객체를 지정하면 이전 코드의 내용을 코틀린에서 자동으로 처리해준다.
- by 오른쪽의 위임객체는 감춰진 프로퍼티에 저장하고 있다가 주 객체의 프로퍼티를 일걱나 쓸 때마다 위임 객체의 getValue와 setValue를 호출해준다.
- 코틀린 표준 라이브러리에는 위 ObservableProperty의 역할을 해주는 비슷한 클래스가 있다. 이 클래스는 PropertyChangeSupport와 연결이 돼있지는 않다. 따라서 프로퍼티 값의 변경을 통지할 때 PropertyChangeSupport를 사용하는 방법을 알려주는 람다를 그 표준 라이브러 클래스에 넘겨야 한다. 이 예시는 아래와 같다.

```kotlin
class Person(
		val name: String, age: Int, salary: Int
) : PropertyChangeAware(){

	private val observer = {
		prop:KProperty<*>, oldValue:Int, newValue:Int ->
		changeSupport.firePropertyChange(prop,name, oldValue, newValue)
	}

	var age : Int by Delegates.observable(age, observer)
	var salary : Int by Delegates.observable(salary, observer)
}
```

- by의 오른쪽에 있는 식이 꼭 새 인스턴스를 만들 필요는 없고, 함수 호출이나 다른 프로퍼티나 다른 식도 올 수 있다. 하지만 우항에 있는 식을 계산할 결과인 객체는 컴파일러가 호출할 수 있는 올바른 타입의 getValue와 setValue를 반드시 제공해야한다. 다른 관례와 마찬가지로 이 둘은 객체 안에 정의된 메서드일수도 있고, 확장 함수 일 수도 있다.

## 7.5.4 위임 프로퍼티 컴파일 규칙

- Int 타입의 프로퍼티 뿐 아니라 프로퍼티 위임 메커니즘은 모든 타입에 두루두루 사용할 수 있다.

```kotlin
class C {
		var prop : Type by MyDelegate()
}
val c = C()
```

- 컴파일러는 MyDelegate클래스의 인스턴스를 감춰진 프로퍼티에 저장하며 그 감춰진 프로퍼티를 <delegate>라는 이름으로 부른다. 또한 컴파일러는 프로퍼티를 표현하기 위해 KProperty 타입의 객체를 사용한다. 이 객체를 <property>라고 부른다.

```kotlin
class C {

	private val <delegate> = MyDelegate()
	var prop : Type
	get () = <delegate>.getValue(this, <property>)
	set(value:Type) = <delegate>.setValue(this, <property>, value)
}
```

- 컴파일러는 모든 프로퍼티 접근자 안에 getValue 와 setValue라는 호출 코드를 생성해준다.
- 이 메커니즘을 통해 프로퍼티 값이 저장될 장소를 바꿀 수도 있고, 프로퍼티를 읽거나 쓸 때 벌어질 일을 변경할 수도 있다.

## 7.5.5 프로퍼티 값을 맵에 저장

- 자신의 프로퍼티를 동적으로 정의할 수 있는 객체를 위임 프로퍼티를 활용하여 만드는 경우가 있는데 이런 객체를 ‘확장 가능한 객체’라고 부르기도 한다.
- 예로 어떤 시스템의 경우 특별히 처리해야하는 필수정보와 사람마다 달라질 수 있는 추가정보가 있을 때 정보를 모두 맵에 저장하되 그 맵을 통해 처리하는 프로퍼티를 통해 필수 정보를 제공하는 방법이 있다.

 

```kotlin
class Person{
	// 추가정보
	private val _attributes = hashMapOf<String, String>()
	fun setAttribute(attrName:String, value:String){
		_attributes[attrName] = value
	}
	// 필수정보
	val name : String
	get() = _attributes["name"]!!

}
```

- 이와 같이 추가 데이터를 저장하기 위해 일반적인 API를 사용하고, 특정 프로퍼티를 처리하기 위해 구체적인 개별 API를 제공한다. 이를 위임 프로퍼티를 활용하여 변경할 수 잇는데, by 키워드 뒤에 맵을 직접 넣는 것이다.

```kotlin
class Person{
	// 추가정보
	private val _attributes = hashMapOf<String, String>()
	fun setAttribute(attrName:String, value:String){
		_attributes[attrName] = value
	}
	// 필수정보
	val name : String by _attributes
}
```

- 이와 같은 코드가 작동하는 이유는 표준 라이브러리 Map과 MutableMap 인터페이스에 대해 getValue와 setValue 확장함수를 제공하기 때문이다.
    - [p.name](http://p.name) 은 _attributes.getValue(p, prop)라는 호출을 대신하고 _attributes.getValue(p, prop)은 다시 _attributes[prop.name]을 통해 구현된다.

## 7.5.6 프레임워크에서 위임 프로퍼티 활용

- 객체 프로퍼티를 저장하거나 변경하는 방법을 바꿀 수 있으면 프레임워크를 개발할 때 유용하다.

```kotlin
object Users : IdTable(){ // 데이터베이스 테이블에 해당
	// 프로퍼티는 테이블의 칼럼
	val name = varchar("name", length = 50).index()
	val age = integer("age")

}
// 각 유저 인스턴스는 테이블에 들어있는 구체적인 엔티티에 해당한다
class User(id: EntityID) : Entity(id){
	
	var name : String by Users.name // 사용자 이름은 데이터베이스 name 칼럼에 들어있다.
	var age : Int by Users.age

}
```

- 데이터베이스 전체에 단 하나만 존재하는 테이블을 표현하므로  Users를 싱글턴 객체로 선언했고 프로퍼티는 테이블 칼럼을 표현한다. User의 상위 클래스인 Entity 클래스는 데이터베이스 칼럼을 엔티티의 속성 값으로 매핑이 되어있는데 각 유저의 프로퍼티 중에는 데이터베이스에서 가져온 name과 age가 있다.
- 어떤 유저 객체를 변경하면 그 객체는 변경됨(dirty) 상태로 변하고, 프레임워크는 나중에 데이터베이스에 변경내용을 반영한다.
- 각 엔티티 속성은 위임 프로퍼티며, 칼럼 객체를 위임 객체로 사용한다. 프레임워크는 칼럼 클래스 안에 getValue와 setValue 메서드를 정의한다. 이 두 메서드는 코틀린의 위임 객체 관례에 따른 시그니처 요구사항을 만족한다.

```kotlin
operator fun <T> Column<T>.getValue(o:Entity, desc:KProperty<*>):T{ 
	// DB 칼럼값 가져오기 
}

operator fun <T> Column<T>.setValue(o:Entity, desc:KProperty<*>, value:T){ 
	// DB의 값 변경하기 
}
```

- 칼럼 프로퍼티 User.name을 위임 프로퍼티(name)에 대한 위임 객체로 사용할 수 있다.
- user.age += 1 이라는 식을 코드에서 사용하면 그 식은 user.ageDelegate.setValue(user.AgeDelegate.getValue() + 1) 과 비슷한 코드로 변환되고, getValue 와 setValue 메서드는 데이터베이스에서 데이터를 가져오고 처리하는 작업을 처리한다.