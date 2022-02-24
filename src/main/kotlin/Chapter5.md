# Kotlin Ch.5 : 람다로 프로그래밍

In this Chapter :

- 람다 식(Lambda Expression)는 다른 함수에 넘길 수 있는 작은 코드 조각/ 동작을 뜻.

- 표준 라이브러리 함수에 람다를 넘기는 방식으로 컬렉션 처리를 위주로 예시로 잡음

- 자바 라이브러리와 함께 사용하는 방법

- 수신 객체 지정 람다

## 5.1  람다 식과 멤버 참조

### 5.1.1 람다 소개 : 코드 블록을 함수 인자로 넘기기

- 책에서는 몇 가지 예시로 코틀린 람다를 통해 얼마나 코드가 단순해지는 지를 보여 준다.
- 자바에서 무명 내부 클래스로 코드를 함수에 넘기거나 변수에 저장하던 것을 쉽게 활용할 수 있도록 해준다.
- 루프를 쓰던 코드도 라이브러리 함수와 멤버참조를 통해 단순화

```java
/* 자바에서 무명 내부 클래스로 구현 */
button.setOnClickListener(new OnClickListener(){ //<- 무명내부클래스 선언하는 부분
	@Override
	public void onClick(View view){
		// Action when clicked
	}
}
```

```kotlin
/* 람다로 구현 */
button.setOnClickListener { // Action when clicked }
// 중괄호 내부에 수행할 코드를 넣으면 끝.
```

### 5.1.2 람다와 컬렉션

- 라이브러리가 반복되는 패턴이 많은 컬렉션을 다루는 경우, 람다 없이는 좋은 코드를 제공하기 힘들다.
- 예시에는 인물 리스트에서 연장자를 찾는 경우를 보여주고 코드를 비교한다.

```kotlin
// 데이터 클래스 ( 예시에는 인물 )
data class Person(val name : String, val age : Int)

// 데이터 클래스로 구성된 리스트 ( 예시에는 인물 리스트 )
val people = listOf(Person("Alice", 29), Person("Bob", 31))

/* 코드를 직접 작성시 */
fun findTheOldest(people : List<Person>){
    var maxAge = 0
    var theOldest : Person? = null

    for(person in people){ 
        if(person.age > maxAge) {
            maxAge = person.age
            theOldest = person
        }
    }
    println(theOldest)
}

/* 람다를 활용하여 컬렉션 검색 후 출력 */
println(people.maxByOrNull{ it.age })

/* 함수나 프로퍼티의 반환 역할을 하는 람다는 멤버참조로 대체가 가능 */
println(people.maxByOrNull(Person::age))
```

- 예시에서 라이브러리를 통해 검색하는 경우, 비교에 필요한 인자를 반환하는 함수를 중괄호에 넣는다.
- 이와 같이 함수나 프로퍼티를 반환하는 역할을 하는 람다는 멤버 참조로 대치가 가능하다.

### 5.1.3 람다 식의 문법

{ x: Int , y: Int → x + y }

- 코틀린 람다식은 항상 중괄호로 감싼다.
- 화살표를 기준으로 왼쪽에 인자목록, 오른쪽에는 람다 본문이 들어가며, 인자 목록은 괄호에 넣지 않는다.
- 람다 식을 변수에 저장하고 일반 함수와 같이 사용도 가능하며, 람다 식을 직접 호출해도 되는데,
    
    람다를 만들자마자 바로 호출하느니 람다 본문을 직접 실행하는게 낫다.
    
- 블록을 감싼 코드를 실행할 때는 run이라는 라이브러리 함수를 사용. 이는 인자로 받은 람다를 실행해주는
    
    함수이다.
    
- 실행 시점에 코틀린 람다 호출에는 아무 부가 비용이 들지 않으며, 프로그램의 기본 구성요소와 비슷한 성능을 낸다. 그 이유는 8.2절 인라인 함수에서 설명.

- 위에 인물검색 예시에서 단순화되어 있는 형식이 되기까지 과정은 다음과 같다.

```kotlin
people.maxByOrNull({p: Person -> p.age})
```

- 정식으로 작성한 람다는 중괄호 안에 들어가고 이를 통해 maxByOrNull 함수에 인자를 넘긴다.
- 람다 내에서 인자는 인물 클래스이고, 그 안에 age를 반환한다.

```kotlin
people.maxByOrNull() { p:Person -> p:age }
```

- 관습상 함수 호출시 맨 뒤에 인자가 람다식이면 괄호 밖으로 뺄 수 있다.
- 인자가 여럿인 함수의 경우 그대로 괄호를 안에 두어 인자임을 분명히 하는 것도 가능. 그리고 항상 함수의 인자들 중 마지막 인자가 람다인 경우에만 뺄 수 있다는 걸 명심.
- 또한 람다가 어떤 함수의 유일한 인자이며 괄호 뒤에 람다를 쓴 경우에는 호출 시 빈 괄호도 생략 가능.

```kotlin
people.maxByOrNull { p:Person -> p.age }
```

- 로컬변수처럼 컴파일러는 람다 파라미터의 타입 추론이 가능
- 람다의 파라미터가 하나 뿐이고 타입을 컴파일러가 추론 가능하면 디폴트 파라미터명 it을 사용

```kotlin
people.maxByOrNull { it.age }
```

- it 파라미터명은 자동생성되는데, 만약 람다 안에 람다가 중첩되는 경우에는 둘 다 디폴트 파라미터를 it으로 지정되어 파악이 어려워질 수 있다. 이런 경우에는 각 파라미터명을 명시하는 것이 더 좋다.
- 만약 람다를 변수에 저장할 경우에는 파라미터 타입을 명시해야된다.
- 여러줄로 이루어진 람다식의 경우 제일 마지막에 있는 식이 반환 결과값이 된다.
- 3장에서의 예시를 이와 같이 정리하는 예시

```kotlin
val names = people.joinToString ( separator = " ", transform={p:Person -> p.name} )

val names2 = people.joinToString(" ") { p:Person -> p.name }
```

### 5.1.4 현재 영역에 있는 변수에 접근

- 람다를 함수 안에서 정의하면 함수의 파라미터뿐 아니라 로컬 변수까지 람다에서 모두 사용 가능

```kotlin
fun printMessagesWithPrefix(messages: Collection<String>, prefix: String) {
	messages.forEach {
		println("$prefix $it")
	}
}

>>> val errors = listOf("403 Forbidden", "404 Not Found")
>>> printMessagesWithPrefix(errors, "Error: ")
>>> 403 Forbidden 
>>> 404 Not Found
```

- 코틀린 람다 안에서 파이널 변수가 아닌 변수에 접근이 가능하다는 점이 중요.
- 람다 안에 바깥 변수를 변경할 수도 있다. 다음은 그 예시.

```kotlin
fun printProblemCounts(responses: Collection<String>){

	var clientErrors = 0
	var serverErrors = 0

	responses.forEach{
		if(it.startsWith("4")){
			clientErrors++		
		} else if {
			serverErrors++
		}
	}
	println("$clientErrors client erros, $serverErrors server errors")
}
```

- 이와 같이 람다 외부의 변수를 람다 안에서 사용하는 경우 그 변수를 람다가 포획 했다고 부른다.
- 보통 함수 안에 정의된 로컬 변수의 생명주기는 함수가 반환되면 끝나는데, 자신의 로컬변수를 포횐한 람다를 반환하거나 다른 변수에 저장하면 로컬변수의 생명주기와 함수의 생명주기가 달라질 수도 있다.
- 람다의 본문 코드는 함수가 끝난 뒤에도 포획한 변수를 읽거나 쓸 수 있는데, 이는 파이널 변수가 아닌 변수를 포획한 경우에는 이를 특별한 래퍼로 감싸서 나중에 변경하거나 읽을 수 있게 하고 그 참조를 람다 코드와 함께 저장하기 때문이다.
- 파이널 변수를 포획한 경우에는 람다 코드를 변수 값과 함께 저장한다.

- 람다를 다른 비동기적으로 실행 되는 코드로 활용하는 경우에는 함수 호출이 끝난 다음에 로컬 변수가 변경될 수도 있다. 다음은 그 예시.

```kotlin
/* 이 코드는 제대로 작동 안됨 */
fun tryToCountButtonClicks(button: Button): Int{

	var clicks = 0
	button.onClick{ clicks++ }
	return clicks

}
```

- 여기서 onClick은 비동기적으로 사용된다. 즉, 해당 함수가 return clicks를 한 뒤에 onClick 핸들러가 호출이 되기 때문에 clicks의 변경된 값을 찾지 못하여 그대로 0을 return하는 것.
- 제대로 구현이 될려면 clicks 를 함수 밖에 선언해서 나중에도 변경된 값을 볼 수 있도록 해야한다.

### 5.1.5 멤버 참조

- 멤버 참조란 함수 자체를 값으로 바꾸어 사용하는 것.
- 멤버 참조는 프로퍼티나 메서드를 단 하나만 호출하는 함수 값을 만들어준다.

```kotlin
// 간략하게 표현하기 전
val getAgeOriginal = { person: Person -> person.age }

// 멤버참조로 간략하게 표현
val getAge = Person::age
```

- 참조 대상이 함수이든 프로퍼티이든 멤버 참조 뒤에는 괄호를 넣으면 안됨.
- 멤버 참조는 그 멤버를 호출하는 람다와 같은 타입이다 (에타 변환 : 함수 f 와 {x→f(x)}를 바꿔 사용).
    
    아래와 같이 케이스들을 바꿔 쓸 수 있다.
    

```kotlin
people.maxByOrNull(Person::age)
people.maxByOrNull { p -> p.age }
people.maxByOrNull { it.age }
```

- 최상위에 선언된 함수나 프로퍼티를 참조할 수도 있다. 이때는 앞에 클래스명을 생략한다.

```kotlin
fun salute() = println("salute!")

>>> run(::salute)
Salute!
```

- 람다가 인자가 여럿인 경우 다른 함수한테 작업을 위임하는 경우에는 람다를 정의하지 않고 직접 위임 함수에 대한 참조를 제공할 수 있다.

```kotlin
val action = { person: Person, message: String ->
	sendEmail(person, message)
}

val nextAction = ::sendEmail
```

- 생성자 참조를 사용하면 클래스 생성 작업을 연기하거나 저장해 둘 수 있다.
- 생성자 참조는 :: 뒤에 클래스명을 넣으면 만들 수 있다.

```kotlin
data class Person(val name: String, val age: Int)

>>> val createPerson = ::Person
>>> val p = createPerson("Alice", 29)
>>> println(p)
```

- 확장 함수도 멤버 함수와 똑같은 방식으로 참조가 가능하다.
- 다음 예시에서 isAdult는 확장함수이며, 인스턴스멤버 호출 구문을 쓸 수 있는 것처럼, 멤버 참조 구문으로 확장함수에 대한 참조를 얻을 수 있다.

```kotlin
fun Person.isAdult() = age >= 21
val predicate = Person::isAdult
```

## 5.2 컬렉션 함수형 API

- 여기서는 컬렉션을 다루는 코틀린 표준 라이브러리를 몇 개 다룬다.

### 5.2.1 필수적인 함수 : filter 와 map

- filter 함수는 컬렉션을 이터레이션하면서 주어진 람다에 각 원소를 넘겨 람다가 true를 반환하는 원소만 모음.
- filter 함수는 조건에 맞지 않는 원소를 제거할 뿐, 변환은 불가능하다.

```kotlin
/* 예시 1 */
>>> val numList = listOf(1, 2, 3, 4)

>>> println(list.filter { it % 2 == 0 })

/* 예시 2 */
>>> val people = listOf(Person("Alice", 29), Person("Bob", 31))

>>> println(people.filter { it.age > 30 })
```

- map 함수는 람다를 컬렉션의 각 원소에 적용한 결과를 모아서 새 컬렉션을 만들어 반환.

```kotlin
/* 예시 1 */
>>> val numList = listOf(1, 2, 3, 4)

>>> println(list.map{ it * it })

/* 예시 2 */
>>> val people = listOf(Person("Alice", 29), Person("Bob", 31))

>>> println(people.map { it.name })
>>> println(people.map(Person::name)) // -> 멤버참조 활용
```

- 두 함수를 다음 예시처럼 연쇄시킬 수도 있다.

```kotlin
>>> val people = listOf(Person("Alice", 29), Person("Bob", 31))

/* 30세 이상인 사람의 이름 */
people.filter{it.age}.map(Person::name)

/* 최고 연장자의 이름 */
people.filter{ it.age == people.maxByOrNull(Person::age)!!.age }
// 위 예시는 목록에서 최대값을 구하는 작업을 반복하기에 좋지 않다.
// 아래와 같이 최대값을 한번만 계산하도록 하는게 낫다.
val maxAge = people.maxByOrNull(Person::age)!!.age
people.filter{ it.age == maxAge }
```

- 이런 함수들의 사용은 맵(hashmap과 같은 데이터 구조로써의 map)에서도 활용이 가능하다.
- 맵에서 활용할 때 key와 value를 처리하는 함수가 따로 있다. key는 filterKeys, mapKeys가 있고, value에는 filterValues, mapValues를 사용한다.

```kotlin
>>> val numbers = mapOf(0 to "zero", 1 to "one")
>>> println(numbers.mapValues{ it.value.toUpperCase() })
```

### 5.2.2 all, any, count, find : 컬렉션에 술어 적용

- 코틀린에는 all 과 any 처럼 컬렉션의 모든 원소가 어떤 조건을 만족하는지, 혹은 어떤 조건을 만족하는 원소가 있는지 판단하는 연산들이 있다.
- count는 조건을 만족하는 원소의 개수를 반환하고, find는 조건을 만족하는 첫번째 원소를 반환한다.

```kotlin
val canBeInClub30 = { p:Person -> p.age >= 30 }

>>> val people = listOf(Person("Alice", 29), Person("Bob", 31))

// 모든 원소가 만족하는지 확인은 all 함수
>>> println(people.all(canBeInClub30))

// 술어를 만족하는 원소가 하나라도 있는지 확인은 any 함수
>>> println(people.any(canBeInClub30))

// 술어를 만족하는 원소의 개수를 찾을 땐 count 함수
>>> println(people.count(canBeInClub30))

// 술어를 만족하는 원소를 하나 찾고 싶을 경우에는 find 함수
>>> println(people.find(canBeInClub30))
```

- !all 은 any와 같고, !any는 all과 같음. 가독성을 위해서 가능하면 쓰지 말자.
- count와 size를 적절히 사용할 것. size의 경우 조건을 만족하는 모든 원소가 들어가는 중간 컬렉션을 생성한다. count의 경우에는 조건에 만족하는 원소를 저장하지 않고 개수 만을 추적하기 때문에 훨씬 효율적이다.
- find는 가장 먼저 조건에 만족하는 원소를 반환하며 없을 경우에는 null을 반환한다. 이는 firstOrNull과도 같기 때문에 명확성을 위해서 이를 사용해도 좋다.

### 5.2.3 groupBy : 리스트를 여러 그룹으로 이뤄진 맵으로 변경

- 컬렉션 내의 원소를 특성에 따라 여러 그룹을 나누고 싶을 때 사용하는 groupBy 함수

```kotlin
>>> val people = listOf(Person("Alice", 29), Person("Bob", 31), Person("Carol", 31))

println(people.groupBy{ it.age })
```

- 이 같은 경우 원소를 구분하는 특성을 키로 하고 값은 각 그룹이 들어가는 map으로 결과가 반환된다.

### 5.2.4 flatMap과 flatten : 중첩된 컬렉션 안의 원소 처리

- flatMap 함수는 인자로 주어진 람다를 컬렉션의 모든 객체에 적용하고 결과로 얻어지는 여러 리스트를 하나로 모아서 반환한다.

```kotlin
/* 예시 1 */
class Book(val title: String, val authors: List<String>)

>>> val books = listOf( Book("Thursday Next", listOf("Jasper Fforde")),
												Book("Mort", listOf("Terry Pratchett")),
												Book("Good Omens", listOf("Terry Pratchett", "Neil Gaiman")))

println(books.flatMap{it.authors}.toSet()) // -> toSet은 중복도 제거해줌

/* 예시 2 */
>>> val strings = listOf("abc", "def")
>>> println(strings.flatMap{ it.toList() })
```

- 위와 같이 반환하는 리스트에 특별한 효과를 줄 필요 없이 합치고 중복 제거만 하는 것이라면 flatten 함수를 써도 된다. flatMap은 다른 효과를 줄 경우 사용.

## 5.3 지연 계산(lazy) 컬렉션 연산

- 앞에 filter과 map과 같은 컬렉션 함수를 연쇄하여 사용시 매 단계마다 계산의 중간 결과를 새로운 컬렉션에 임시로 담는데 이를 즉시(eagerly) 생성이라고 한다.
- Sequence를 사용하면 중간 임시 컬렉션을 사용하지 않고 연산을 연쇄할 수 있다.

```kotlin
people.asSequence()
	.map(Person::name)
	.filter{it.canBeInClub30}
	.toList()
```

- 지연 계산은 sequence 인터페이스에서 시작되는데, 이는 단지 한번에 하나씩 열거 될 수 있는 원소의 시퀀스를 표현할 뿐이다. 해당 인터페이스 안에는 iterator 라는 하나의 메서드만 있고 이로부터 원소 값을 얻는다.
- 이를 사용하는 강점은 해당 원소가 계산이 되는 시점에 연산을 모두 연쇄적으로 처리하는데 있다. 중간에 저장을 하는 과정이 없기 때문에 효율적이다.
- 마지막에는 시퀀스를 컬렉션으로 전환해준다.

### 5.3.1 시퀀스 연산 실행 : 중간 연산과 최종 연산

sequence.map{...}.filter{...}.toList()

- 위와 같이 시퀀스를 사용하는 것에 대해 자세한 내용을 여기서 다룬다.
- 우선 시퀀스에 대한 연산은 중간연산과 최종연산으로 나뉜다.
- 위 코드에서 toList 파트를 최종연산, 그 사이의 연산을 중간연산이라고 한다.
- 중간연산은 최초 시퀀스로부터 반환되는 다른 시퀀스로 최초의 원소를 어떻게 변환할지에 대한 방법만을 가지고 있다고 보면된다. 궁극적인 결과값을 갖고 있는 것은 아니다.
- 궁긍적인 결과값을 반환하는 것은 최종연산에서 수행한다. 중간연산 시퀀스가 가지고 있는 변환 방법을 지연해서 가지고 있다가 결과가 필요한 시점에 연산을 수행하는 것이다.
- 아래 예시를 마지막 toList()를 포함했을 때와 안했을 때 돌려보면 그 차이를 볼 수 있다. 포함 안하면 시퀀스 위치 정보만 출력된다.

```kotlin
>>> listOf(1,2,3,4).asSequence()
			.map{ print("map($it) "); it * it}
			.filter{ print("filter($it) "); it % 2 == 0 }
			.toList()
```

- find를 사용한 다음 예시로는 즉시계산과 지연계산의 연산 차이를 볼 수 있다. (책 226페이지 그림 참고)

```kotlin
>>> println(listOf(1,2,3,4).asSequence().map{ it * it }.find{ it > 3 })
```

- 이와 같은 코드를 컬렉션에서 수행(즉시계산)할 경우에는 먼저 매핑이 완료된 리스트가 만들어지고 find를 수행하는 반면, 시퀀스를 사용한 경우(지연계산) 하나의 원소를 처음부터 끝까지 연산을 하기 때문에 find가 처음으로 조건에 만족하는 결과를 찾을 경우 더 이상 연산을 하지 않는다.
- 이와 같은 경우로 인해 지연계산을 할 경우 전체 리스트의 원소들이 연산과정을 거치지 않을 수도 있다.

- 중간연산의 순서에 따라 성능이 달라지기도 한다. 다음 예시는 map과 filter를 바꾸어서 연산했을 때 수행되는 변환의 횟수가 다르다. (책 227페이지 그림 참고)

```kotlin
// map 다음에 filter 수행
>>> println(people.asSequence().map(Person::name).filter{it.length < 4}.toList())

// filter 다음에 map 수행
>> println(people.asSequence().filter{it.length < 4}.map(Person::name).toList())
```

- 두 코드를 실행하면 위에 코드는 매핑 단계까지는 모든 시퀀스가 수행되고 필터에서는 두 개의 시퀀스만 수행되는 반면, 두번째 코드에서는 중간 필터 술어에 참인 원소들만 마지막까지 연산이 수행된다.

### 5.3.2 시퀀스 만들기

- 지금까지 컬렉션에 대해 asSequence() 메서드를 호출해서 시퀀스로 만들었는데, 다른 방법으로는 generateSequence 함수를 사용할 수 있다.

```kotlin
>>> val naturalNums = generateSequence(0){it + 1}

>>> val numsTo100 = naturalNums.takeWhile{ it <= 100 }

>>> println(numsTo100.sum())
```

- naturalNums와 numsTo100은 모두 시퀀스이며 마지막에 최종연산인 sum()가 수행될 때까지는 계산이 이루어지지 않는다.(지연계산)
- 일반적인 사용법으로 어떤 객체가 자신과 같은 타입으로 조상들이 이루어져 있을 때, 그 객체의 조상으로 이뤄진 시퀀스를 만들어내고 거기에서 어떤 특성을 찾는 용례가 있다. 그 예시로 아래와 같이 파일 상위 디렉터리에서 숨김 속성을 가진 파일을 검사하는 경우

```kotlin
fun File.isInsideHiddenDirectory() =
		generateSequence(this) { it.parentFile }.any{ it.isHidden }

>>> val file = File("/Users/someFile/.HiddenDir/a.txt")
>>> println(file.isInsideHiddenDirectory())
```

## 5.4 자바 함수형 인터페이스 활용

- 여기서는 코틀린 람다를 자바 api에 활용하는 방법을 다룬다.
- 책은 5장 맨 처음에 나왔던 아래 코드로 하나씩 설명한다.

```kotlin
button.setOnClickListener{ /* 클릭 시 수행 동작 */ }
```

- 자바에서 Button 클래스는 setOnClickListener라는 메서드를 통해 버튼의 listener를 설정하는데, 이때 이 메서드의 파라미터는 onClickListener이다.

```kotlin
public class Button {
	
	public void setOnClickListener(onClickListener l){...}

}
```

- 여기서 이 OnClickListener 인터페이스는 onClick이라는 메서드만 선언되어있다.

```kotlin
public interface OnClickListener {
	void onClick(View v);
}
```

- 자바 8 이전에는 setOnClickListener 메서드에 이 파라미터를 넘기기 위해 무명 클래스의 인스턴스를 만들어야 했다.

```kotlin
button.setOnClickListener(new OnClickListener() {
	
	@Override
	public void onClick(View v) {
		...
	}
}
```

- 그런데 코틀린에서는 앞서 배운 것처럼 무명클래스 인스턴스 대신 람다를 넘길 수 있다.

```kotlin
button.setOnClickListener{ view -> ... }
```

- 이때 자바의 메서드에서 코틀린 람다의 파라미터가 사용될 수 있는 이유는 위에 언급한 OnClickListener의 추상 메서드가 하나 밖에 없기 때문이다. 그리고 이런 인터페이스를 함수형 인터페이스 or SAM(Single Abstract Method) 인터페이스라고 부른다.
- 자바에는 Runnable, Callable 과 같은 함수형 인터페이스와 이를 활요하는 메서드가 많은데 코틀린에서는 이와 같은 자바에서 함수형 인터페이스를 인자로 삼는 메서드를 호출할 때 람다를 넘길 수 있게 해주어 코드를 깔끔하게 해준다.
- 물론, 코틀린에는 함수 타입이라는게 존재하기 때문에 함수를 인자로 받을 필요가 있는 함수는 함수형 인터페이스가 아니라 함수 타입을 인자로 사용해야 한다. 그리고 코틀린 컴파일러도 코틀린 람다를 함수형 인터페이스로 변환해주지 않는다는 사실을 기억.

### 5.4.1 자바 메소드에 람다를 인자로 전달

- 위에 설명과 같이 함수형 인터페이스를 인자로 원하는 자바 메소드에 코틀린 람다를 전달하는 예시.

```kotlin
/* 자바 */
void postponeComputation(int delay, Runnable computation);

/* 코틀린 */
postponeComputation(1000){println(42)}
```

- Runnable 타입의 파라미터의 자리에 들어간 코틀린 람다를 컴파일러는 자동으로 무명 클래스와 그 인스턴스를 만들어준다. 그리고 이때 무명 클래스의 유일한 추상메서드를 구현할 때 그 본문에 람다 본문을 사용한다.
- 아래와 같이 무명객체를 명시적으로 만들어서 사용할 수도 있지만, 이 경우에는 메서드를 호출 할때마다 새로운 객체가 생성된다. 람다의 경우에는 인스턴스를 하나만 생성하고 이를 반복 사용한다.

```kotlin
postponeComputation(1000, object : Runnable {

	override fun run(){
		println(42)
	}
})
```

- 명시적인 object 선언을 사용하고 람다와 같은 코드는 그래서 다음과 같다. 이 경우에는 람다의 경우처럼 Runnable 인스턴스를 한번만 만들고 이를 저장하여 메서드 호출 때마다 이 인스턴스를 재활용한다.

```kotlin
val runnable = Runnable { println(42) }

fun handleComputation(){

	postponeComputation(1000, runnable)

}
```

- 그런데! 만약에 주변 영역의 변수를 포획하여 사용하는 람다의 경우에는 같은 인스턴스를 재활용해서 쓸 수가 없다. 컴파일러는 이 경우 매번 포획한 변수와 새로운 인스턴스를 생성하기 때문이다.
- 컬렉션을 확장한 메서드에 람다를 넘기는 경우에 코틀린은 위와 같은 방식으로 함수형 인터페이스와 그 무명 클래스와 인스턴스를 생성해주지 않는다.
- 대부분 코틀린의 확장함수들에는 inline 표시가 되어있는데 코틀린 inline으로 표시된 코틀린 함수에서 람다를 넘기면 마찬가지로 아무런 무명 클래스가 생성되지 않는다.(이에 대해 8.2에서 설명예정)

### 5.4.2 SAM 생성자 : 람다를 함수형 인터페이스로 명시적으로 변경

- 바로 위에 설명한 람다와 함수형 인터페이스 사이의 변환은 컴파일러에서 자동으로 이루어지는데, 때로는 이를 수동으로 변환해야하는 경우가 있다.
- 이때는 함수형 인터페이스의 인스턴스를 생성해주는 SAM 생성자를 사용한다. 반환하고픈 람다를 SAM 생성자로 감싸서 사용을 하는데, 그 생성자의 이름은 함수형 인터페이스의 이름과 같다.

 

```kotlin
fun createAllDoneRunnable() : Runnable{

	return Runnable{ println("All Done!") }

}

>>> createAllDoneRunnable().run()
```

- SAM 생성자는 함수형 인터페이스 내의 유일한 추상 메서드의 본문에 사용할 람다 만을 인자로 받아서 이를 구현하는 클래스의 인스턴스를 반환한다.
- 변수에 이를 저장해야 될 경우에도 SAM생성자를 쓸 수 있다. 이렇게 하면 아래 예시처럼 같은 인스턴스를 활용하여 다양한 케이스에 적절한 결과를 보여주는 동작도 수행할 수 있다.

```kotlin
val listener = OnClickListener { view ->

	val text = when(view.id){
	
		R.id.button1 -> "First Button"
		R.id.button2 -> "Second Button"
		else -> "Unknown Button"

	}
	toast(text)

}

button1.setOnClickListener(listener)
button2.setOnClickListener(listener)
```

- 함수형 인터페이스를 요구하는 메서드를 호출 할 때 대부분의 SAM 변환을 컴파일러가 자동으로 수행하는데, 가끔 오버로드한 메서드 중에 어떤 타입의 메서드를 선택해 람다를 변환해 넘겨줘야될지 모르는 경우가 생길 수도 있다. 이때는 명시적으로 SAM 생성자를 적용하여 오류를 피할 수도 있다.

## 5.5 수신 객체 지정 람다 : with와 apply

- 코틀린 표준 라이브러리의 with 와 apply 를 보여주는데, 이를 바탕으로 수식 객체를 명시하지 않고 람다의 본문 안에서 다른 객체의 메서드를 호출할 수 있게 하는 수신 객체지정 람다를 설명한다.

### 5.5.1 with 함수

- with 함수는 어떤 객체의 이름을 반복하지 않고 그 객체의 다양한 연산을 수행할 수 있도록 해주는 라이브러리 함수이다.

```kotlin
/* with 사용 전에는 result를 반복 사용한다 */
fun alphabet() : String {
	val result = StringBuilder()
	for(letter in 'A'...'z'){
		result.append(letter)
	}
	result.append("\nNow I know the alphabet!")
	return result.toString()
}
>>> println(alphabet())

/* with 사용 */
fun alphabet(): String {
	val stringBuilder = StringBuilder()

	return with(stringBuilder) {

		for(letter in 'A'...'z'){
			this.append(letter)
		}
		append("\nNow I know the alphabet!")
		this.toString()
	}

}
>>> println(alphabet())	
```

- 자세히보면 with는 첫번째 파라미터로 받은 객체를 두번째 파라미터로 받은 람다의 수신객체로 만들어 사용할 수 있는 함수이다.
- 마치 확장함수가 확장하는 타입의 인스턴스에 접근할 수 있던 것처럼, 수신객체 지정람다는 지정된 수신객체의 내부 멤버에 접근할 수 있는 것. 그렇다면 상단 코드를 변수를 없애고 this를 생략하여 최종적으로는 다음과 같이 리펙토링 할 수 있다.

```kotlin
fun alphabet() = with(StringBuilder()){
	for(letter in 'A'...'z'){
			append(letter)
		}
		append("\nNow I know the alphabet!")
		toString()
	}
}
```

- 이때 with의 첫번째 파라미터인 수신객체 클래스와 두번째 파라미터인 람다가 들어있는 클래스 안에 이름이 같은 메서드가 있을 경우에는 혼돈을 방지하기 위해 this@OuterClass.toString()과 같이 바깥 클래스의 메서드 여부를 붙여서 명확하게 호출할 수도 있다.

### 5.5.2 apply 함수

- with를 사용하면 람다의 결과, 즉 람다 본문 안에 마지막 식의 결과가 반환된다. 하지만 수신객체가 반환되야 하는 경우도 있을 텐데, 이때 apply를 사용한다.
- apply는 with와 거의 비슷한데 람다가 받았던 수신객체를 반환한다.

```kotlin
fun alphabet() = StringBuilder().apply{
		
		for(letter in 'A'...'z'){
			append(letter)
		}
		append("\nNow I know the alphabet!")
	
}toString()
```

- apply는 확장함수로 정의가 되어있어서 apply의 수신객체가 곧 파라미터로 받은 람다의 수신객체가 된다.
- apply는 객체의 인스턴스를 만들면서 즉시 프로퍼티 일부를 초기화 해야할 경우 유용하게 쓰인다. 아래는 그 활용 예시로 안드로이드의 TextView 컴포넌트를 만들면서 특성 일부를 설정한다.

```kotlin
fun createViewwithCustomAttributes(context:Context) = 
	TextView(context).apply{
		text = "default text"
		textSize = 20.0
		setPadding(10 , 0, 0, 0)
	}
```

- TextView 인스턴스가 만들어지고 이를 apply에 넘기면 람다 안에서 TextView가 수신객체가 된다. 그 뒤로는 TextView 안에 메서드와 프로퍼티를 호출해서 설정이 가능하다. 마지막에는 이 TextView 인스턴스가 반환된다.

- with와 apply 외로는 예시로 buildString 이라는 함수도 소개한다. 앞 알파벳 예시의 StringBuilder 객체를 생성하는 것과 toString 호출까지 알아서 해주는 메서드로 해당 예시를 더욱 단순화해준다.

```kotlin
fun alphabet() = buildString{

	for(letter in 'A'...'z'){
			append(letter)
		}
		append("\nNow I know the alphabet!")

}
```

- 후에 11장에서 영역특화언어(DSL)에 대해 다룰 때 더 많은 용례를 볼 예정.