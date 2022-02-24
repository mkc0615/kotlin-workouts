# Kotlin Ch.6 : 코틀린 타입 시스템

# 6.1 널 가능성

- nullpointerexception을 피할 수 있도록 실행시점이 아닌 컴파일 시점에 이 문제를 발견할 수 있도록 해서 널을 미리 감지하고 예외의 가능성을 줄일 수 있도록 하는 것.
- 널이 될 수 있는 값의 표기법과 코틀리 이 제공하는 도구가 어떻게 그 값을 처리하는지 살펴봄. 그후 널이 될 수 있는 타입 측면에서 코틀린과 자바를 어떻게 함께 사용할지 본다.
- 12 recipe for using the optional 검색해보고 참고!

## 6.1.1 널이 될 수 있는 타입

- 코틀린에서는 널이 될 수 있는 타입을 명시적으로 지원하는데, 이는 프로그램 안의 프로퍼티나 변수에 널을 허용한다는 의미이다.
- 널이 될 수 있는 변수를 메서드를 통해 호출 하면 발생하는 NullPointerException이 방지하기 위해 애초에 변수의 호출을 금지하는 것이다.

```kotlin
fun strLen(s:String) = s.length
>>> strLen(null)

// 해당 함수에 널이 인자로 들어갈 경우 컴파일 시 오류가 발생
```

- 여기서 s 는 타입이 String이며 코틀린에서는 항상 s가 String의 인스턴스여야 한다는 뜻으로 컴파일러는 널이 될 수 있는 값을 strLen에게 못 넘기게 막는다. 널이 될 수 있는 값도 받기 위해서는 다음과 같이 ?로 명시해야한다.

```kotlin
fun strLenSafe(s:String?) = ...
```

- 어떤 타입이든 이름 뒤에 물음표가 붙으면 그 타입의 변수나 널 참조 저장의 가능을 의미.
- 널이 될 수 있는 타입의 변수가 있으면 사용가능한 연산이 제한된다. 그 예로 널이 될 수 있는 값을 널이 될 수 없는 타입의 파라미터를 받는 함수에 전달하는 것은 불가능하다.
- 그래도 일단 널과 비교하고 나면 컴파일러는 그 사실을 기억하고 확실한 영역에 한해서 해당 값을 널이 될 수 없는 타입의 값과 같이 취급하고 사용할 수 있다. 아래 예시는 컴파일 이 된다.

```kotlin
fun strLenSafe(s:String): Int = 
	if(s!=null) s.length else 0
```

## 6.1.2 타입의 의미

- 책에 따르면 wiki says : 타입은 분류이며 어떤 값들이 가능한지와 그 타입에 대해 수행할 수 있는 연산의 종류를 결정한다고 정의한다.
- 예를 들어 64비트 부동소수점 수를 칭하는 double 타입의 변수가 있으면 이에 대해 일반 수학 연산을 수행할 수 있고, 모든 일반 수학 연산 함수도 적용 가능하다. 해당 변수에 대한 연산을 컴파일러는 통과시키고 그 연산이 성공적으로 실행되리라고 확신을 할 수 있다.
- 자바는 String 타입과 같이 널도 들어갈 수 있는 종류의 값이 사용할 수 있는 연산이 많지 않다. 널 여부를 추가로 확인하지 않으면 어떤 연산이 사용가능한지 알수가 없다.
- 즉 널 확인을 생략하거나 하면 예외 발생이 일어날 수밖에 없다!
- 코틀린에서는 널이 될 수 있는 타입과 널이 될 수 없는 타입을 구분하여 이를 명확히 하고, 컴파일 시점에 검사를 하여 실행 시점에 예외를 발생 시킬 수 있는 연산을 판단하여 이 연산을 아예 금지시킨다.

## 6.1.3 안전한 호출 연산자 : ?

- 해당 연산자는 널 검사와 메소드 호출을 한번의 연산으로 수행한다.
- 값이 널이 아니면 일반 메서드 호출과 같이 작동하고, 널이면 호출을 무시하고 널을 리턴한다.
- 안전한 호출의 결과 타입도 널이 될 수 있는 타입이라는 점을 유의해야한다.
- 프로퍼티를 읽거나 쓸 때도 안전한 호출을 사용할 수 있다.

```kotlin
fun printAllcaps(s:String?) {
	val allCaps : String? = s?.toUpperCase()
	println(allCaps)
}

// 프로퍼티를 읽거나 쓸 때도 안전한 호출을 사용할 수 있다.
class Employee(val name: String, val manager:Employee?)

fun managerName(employee: Employee) : String? = employee.manager?.name

>>> val ceo = Employee("Da Boss", null)
>>> val dev = Employee("Bob Smith", ceo)
>>> println(manageName(dev))
Da Boss
>>> println(manageName(ceo))
null
```

- 객체 그래프에서 널이 될 수 있는 중간 객체가 여럿 있다면 한 식 안에 안전한 호출을 연쇄해서 함께 한줄로 원하는 정보를 가져올 수도 있다.

```kotlin
class Address(val streetAddress: String, val zipCode: Int, 
							val city:String, val country:String)

class Company(val name: String, val address: Address?)

class Person(val name: String, val company: Company?)

fun Person.countryName(): String {
	val country = this.company?.address?.country // <- like this!
	return if (country != null) country else "Unknown"
}

>>> val person = Person("Min", null)
>>> println(person.countryName())
Unknown

```

## 6.1.4 엘비스 연산자: ?:

- 해당 연산자는 널 대신 사용할 디폴트 값을 지정해준다.

```kotlin
fun foo(s : String){
	val t: String = s ?: ""
}
```

- 널 검사 후 널이 아니면 ?: 기준 좌항 그대로 결과로 하고, 널일 경우에는 우항을 사용한다.
- 안전한 호출 연산자와 함께 사용하면 객체가 널인 경우에 대비하여 값을 지정할 수 있다.

```kotlin
fun strLenSafe(s: String?):Int = s?.length?:0
```

- 6.1.3 마지막 예시의 countryName도 아래와 같이 고칠 수 있다.

```kotlin
fun Person.countryName() = company?.address?.country?:"Unknown"
```

- 코틀린에서는 return이나 throw 등의 연산도 식이기 때문에 엘비스 연산자에서 이를 넣을 수도 있는데, 이런 패턴은 함수의 전제 조건을 검사하는 경우 유용하게 쓸 수 있다.

```kotlin
/* 위 6.1.3 마지막 예시에서 이어짐 */

fun printShippingLabel(person: Person){
	val address = person.company?.address
		?: throw IllegalArgumentException("No address")
	with (address){
		println(streetAddress)
		println("$zipCode $city, $ country")
	}
}
```

## 6.1.5 안전한 캐스트: as?

- 자바의 instanceOf 검사 대신 코르린에서는 더 안전한 타입 캐스트 연산자를 사용한다.
- as 연산자로 대상값을 지정한 타입으로 바꿀 수 없으면 ClassCastException을 발생시킨다.
- as? 연산자는 어떤 값을 지정한 타입으로 캐스트 하는데 변환이 불가능하면 null을 반환한다.
- 일반적인 패턴으로는 캐스트를 수행한 뒤에 엘비스 연산자를 사용한다.

```kotlin
class Person(val firstName: String, val lastName: String){
	override fun equals(o:Any?):Boolean {
		val otherPerson = o as? Person ?: return false
		return otherPerson.firstName == firstName && 
						otherPerson.lastName == lastName
	}
	
	override fun hashCode(): Int = 
		firstName.hashCode() * 37 + lastName.hashCode()
}
```

- 이 패턴을 사용하면 인자로 받은 값이 원하는 타입인지 검사하며 캐스트 하고, 타입이 맞지 않으면 false 반환까지 쉽게 할 수 있다.

## 6.1.6 널 아님 단언: !!

- 하지만 때로는 널 처리 지원을 활용하는 대신 직접 컴파일러에게 어떤 값이 널이 아니라는 사실을 알려주고 싶은 경우가 있다.
- 널 아님 단언 !! 을 사용하면 어떤 값이든 널이 될 수 없는 타입으로 강제로 바꿀 수 있다.

```kotlin
fun ignoreNulls(s: String?){
	val sNotNull: String = s!!
	println(sNotNull.length)
}

// null을 인자로 넘기면 npe를 일으킨다.
>>> ignoreNulls(null)
Exception in thread "main" kotlin.KotlinNullPointerException
at <...>.ignoreNulls(07_NotnullAssertions.kt:2)
```

- 위 예시와 같이 !!을 사용하여 예외가 발생할 경우 널 값을 사용하는 코드가 아니라 단언문이 위치한 곳을 가리킨다.
- !! 기호를 쓴 이유도 일부러 더 나은 방법을 찾아보라는 의도에서 만들어졌다고 한다.
- 하지만 이 널 아님 단언문이 더 나은 해법인 경우가 있는데,
    
    어떤 함수가 널 검사를 한 후 다른 함수를 호출한다고 했을 때, 컴파일러는 호출된 함수 안에서 안전하게 그 값을 사용할 수 있는지 인식할 수 없다. 
    
    만약 호출된 함수가 언제나 널이 아닌 값을 전달 받는다는 사실을 분명히 표현하면 널 검사를 다시 수행하지 않도록 하고 싶을 때 사용한다.
    
- 스윙과 같은 ui 프레임워크에서 주로 이와 같은 일이 발생한다. 액션 클래스 안에는 그 액션의 상태를 변경하는 메서드와 실제 액션을 수행하는 메서드가 있는데, 상태를 변경하는 메서드 안에서 검사하는 조건을 만족하지 않으면 그 뒤에 수행 메서드를 호출할 수 없다. 이런 점을 컴파일러는 알 방법이 없다.

```kotlin
class CopyRowAction(val list:JList<String>):AbstractAction(){

	override fun isEnabled():Boolean = 
		list.selectedValue != null
	
	override fun actonPerformed(e: ActionEvent){
		val value = list.selectedValue!!
		// value를 클립보드로 복사
	}

}
```

- 이 경우에 !!를 사용하지 않으려면 val value = list.selectedValue ?: return 처럼 널이 될 수 없는 타입이 되도록 해야한다. 그런데 이와 같이 사용하면 list.selectedValue에서 널이 나올 경우, 함수가 조기 종료되므로 함수의 나머지 본문에서는 value가 항상 아니게 되어버린다. 위에 isEnabled와 중복이지만 나중을 위해 미리 보호 장치를 해두는 개념.
- 또한 !! 연산자는 어떤 파일의 몇 번재 줄에서 예외가 발생하는지 알려주고 어떤 식인지는 알려주지 않으니 단언물을 한줄에 쓰는 것은 피하는게 좋다.

```kotlin
person.company!!.address!!.country //<- 이러지 말자
```

## 6.1.7 let 함수

- 만약 널이 될 수 있는 값을 널이 될 수 없는 값만 인자로 받는 함수에 넘기려 하면 컴파일러는 호출을 허용하지 않는다. 이에 코틀린 언어는 표준 라이브러리에 도움이 될 수 있는 let 함수가 있다.
- 안전한 호출 연산자와 함께 사용하면 원하는 식을 평가해서 결과가 널인지 검사한 후 그 결과를 변수에 넣는 작업을 간단한 식을 사용해 한꺼번에 처리할 수 있다.
- kotlin 공식 레퍼런스에서 function selection을 확인해보면 좋다!

```kotlin
// 널이 될 수 있는 타입을 넘길 수 없는 함수
fun sendEmailTo(email:String) { ... }

>>> val email : String? = ...
>>> sendEmailTo(email)
ERROR <- 널이 될 수 있는 타입을 인자로 넘기면 에러 발생

if(email != null) sendEmailTo(email) // <- 이와 같이 검사를 먼저 수행해야한다.
```

- 위에 예시를 let을 사용하여 인자를 전달 할 수 있다.
- 해당 함수는 자신의 수신객체를 인자로 전달 받은 람다에게 넘긴다. 쉽게 말하면, 인자가 널이 아닌 경우에만 let이 호출된다는 의미이다.

```kotlin
email?.let{email -> sendEmailTo(email)}

// it을 사용하여 더 간단하게
email?.let{ sendEmailTo(it) }

// 널을 넘기면 아무 일도 안 일어난다
>>> email = null
>>> email?.let{sendEmailTo(it)}
// 결과 없음
```

- let을 쓰면 긴 식의 결과를 저장하는 변수를 따로 만들 필요가 없어서 간결하게 작성이 가능하다.

```kotlin
val person: Person? = getTheBestPerson()
if(person != null) sendEmailto(person.email)

// 아래와 같이 바꿀 수 있음
getTheBestPerson()?.let{ sendEmailTo(it.email) }
```

- 여러 값이 널인지 검사해야할 경우 let호출을 중첩해서 처리할 수 있지만 코드가 복잡해져서 일반적인 if를 사용해 모든 값을 한꺼번에 검사하는게 낫다.

## 6.1.8 나중에 초기화할 프로퍼티

- 코틀린에서는 일반적으로 생성자에서 모든 프로퍼티를 초기화 해야되고, 프로퍼티 타입이 널이 될 수 없는 타입이면 반드시 널이 아닌 값으로 그 프로퍼티를 초기화해야한다.
- 그런데! 실제로는 생성자 안에서 널이 아닌 값으로 초기화할 방법이 없는 경우도 있다. 또한 객체 인스턴스를 일단 생성하고 나중에 초기화하는 프레임워크들도 많다고 한다.
- 이런 경우 해당 프로퍼티에 널이 될 수 있는 타입을 사용해야하고, 그렇게되면 결국 !!를 쓰거나 모든 프로퍼티 접근에 대해 널 검사를 넣어야된다. like below...

```kotlin
class MyService{
	fun performAction(): String = "foo"
}

class MyTest {
	private var myService: MyService? = null // 널이 될 수 있는 타입으로 선언
	
	@Before fun setUp(){
		myService = MyService() // 여기서 진짜 초기값을 지정
	}
	
	@Test fun testAction(){
		Assert.assertEquals("foo", myService!!.performAction()) // 널 가능성을 신경써야함
	}
}
```

- 프로퍼티를 여러번 써야될 경우 위에 코드는 더 복잡해질 것이다.
- 이를 해결하기 위해 코틀린에서는 lateinit 변경자를 붙여서 프로퍼티를 나중에 초기화 할 수 있다.

```kotlin
class MyTest {
	private lateinit var myService: MyService 
	
	@Before fun setUp(){
		myService = MyService() 
	}
	
	@Test fun testAction(){
		Assert.assertEquals("foo", myService.performAction()) 
	}
}
```

- val 프로퍼티는 final필드로 컴파일되고 생성자 안에서 반드시 초기화해야 되기 때문에, 나중에 초기화하는 프로퍼티는 항상 var이어야 한다.
- 나중에 초기화하는 프로퍼티는 널이 될 수 없는 타입이라고 해도 더 이상 생성자 안에서 초기화할 필요가 없다. 만약 그 프로퍼티를 초기화하기 전에 접근하려하면 프로퍼티를 초기화하지 않았다는 예외를 발생시켜 어디서 문제가 발생했는지 볼 수 있기 때문에 npe보다 낫다.
- lateinit 프로퍼티를 의존관계 주입 프레임워크와 함께 사용하는 경우가 많다. 이 경우 lateinit 프로퍼티의 값을 DI 프레임워크가 외부에서 설정해주는데, 다양한 자바 프레임워크와의 호환성을 위해 코틀린은 lateinit가 지정한 프로퍼티와 가시성이 똑같은 필드를 생성해준다.

## 6.1.9 널이 될 수 있는 타입 확장

- 널이 될 수 있는 타입에 대한 확장 함수를 정의하면 어떤 메서드를 호출 하기 전에 확장함수가 널을 검사하고 처리해줘서 수신객체 역할을 하는 변수가 널이 될 수 없다고 보장해준다.
- 동적 디스패치는 객체의 동적 타입에 따라 적절한 메서드를 호출해주는 방식을 말하고, 직접 디스패치는 컴파일러가 컴파일 시점에 어떤 메서드를 호출될지 결정해서 코드를 생성하는 방식이다.
- 일반 멤버 호출은 객체 인스턴스를 통해 디스패치되므로 그 인스턴스가 널인지 여부를 검사하지 않는다. 즉 확장함수를 통해서만 위와 같은 처리가 가능하다.
- 예시로 String? 의 isNullOrEmpty 나 isNullOrBlank 메서드가 있다.

```kotlin
// 안전한 호출 없이도 널이 될 수 있는 수신 객체 타입에 대해 선언된 확장함수 호출 가능
fun verifyUserInput(Input:String?){
	if (input.isNullOrBlank()){
		println("Please fill in the required fields")
	}
}
```

- isNullOrBlank는 널을 명시적으로 검사해서 널인 경우 true를 반환하고, 널이 아닌 경우 isBlank를 호출한다. 여기서 isBlank는 널이 아닌 문자열 타입의 값에 대해서만 호출 가능하다.

```kotlin
fun String?.isNullOrBlank():Boolean = this == null || this.isBlank()
// 널이 될 수 있는 스트링을 확장한다. 여기서 두번째 this는 스마트 캐스트가 적용
```

- 널이 될 수 있는 타입에 대한 확장을 정의하면 널이 될 수 있는 값에 대해 그 확장함수를 호출 할 수 있다. 함수 내부에서 this는 널이 될 수 있고 명시적으로 널 여부를 검사해야한다.
- 자바에서는 메서드 안의 this는 그 메서드가 호출된 수신 객체를 가리키므로 항상 널이 아니다. 하지만 코틀린에서는 this가 널이 될 수 있다.

- 앞에 let 함수의 경우에도 널이 될 수 있는 타입의 값에 대해 호출을 할 수 있지만 this가 널인지는 검사하지 않는다. 그렇기 때문에 널이 될 수 있는 타입의 값에 대해 안전한 호출을 사용하지 않고 let을 호출하면 람다의 인자는 널이 될 수 있는 타입으로 추론된다.
- 따라서 let을 사용할 때 수신객체가 널이 아닌지 검사하고 싶다면 예전에 살펴본
    
    person?.let{ sendEmailTo(it) } 처럼 반드시 안전한 안전한 호출 연산인 ?. 을 사용해야 한다.
    
- 직접 확장 함수를 작성해야 한다면 그 확장함수가 널이 될 수 있는 타입에 대해 정의를 할지 여부를 정해야하는데, 처음에는 널이 될 수 없는 타입에 대한 확장함수를 정의하고, 나중에 널이 될 수 있는 타입에 대해 널 처리를 하도록 바꾸어도 된다.
- 코틀린에서 s.isNullOrBlank()와 같은 어떤 확장함수를 쓰고 있으면서 검사 없이 사용한다고해서, 이 s가 널이 될 수 없는 타입이 되는건 아니다! 확장함수를 쓰고 있다면 널이 될 수 있는 타입의 변수를 쓰고 있을 수도 있는 것이다.

## 6.1.10 타입 파라미터의 널 가능성

- 코틀린에서는 함수나 클래스의 모든 타입 파라미터가 기본적으로는 널이 될 수 있기 때문에 널이 될 수 있는 타입을 포함하는 어떤 타입이라도 타입 파라미터를 대신할 수 있다. 즉, 타입 파라미터 T를 클래스나 함수 안에 타입이름으로 사용하면 이름 끝에 물음표가 없어도 널이 될 수 있는 타입이다.
- 가능하면 무조건 널 아닌 타입으로 쓸수 있도록 하는게 좋다!

```kotlin
fun <T> printHashCode(t:T){
	println(t?.hashCode()) // <- t는 널이 될 수 있다! 그러니 안전한 호출을 써야함.
}
>>> printHashCode(null)
null
```

- 타입 파라미터 T에 대해 추론한 타입은 널이 될 수 있는 Any? 타입이다. 만약 타입 파라미터가 널이 아님을 확실하게 하려면 널이 될 수 없는 타입 상한을 지정해야 한다. 그럴 경우 널이 될 수 있는 값을 거부하게 된다.

```kotlin
fun <T:Any> printHashCode(t:T){
	println(t?.hashCode())
}
>>> printHashCode(null)
Error -> type parameter bound for t is not satisfied
```

- 타입 파라미터는 널이 될 수 있는 타입을 표시하려면 반드시 물음표를 이름 뒤에 붙여야 한다는 규칙의 유일한 예외에 해당된다.

## 6.1.11 널 가능성과 자바

- 코틀린과 달리 자바는 널 가능성을 지원하지 않는다. 상호운용성을 위해 첫째로는 자바 코드에도 애노테이션으로 표시된 널 가능 정보가 있으며 코틀린에서 이 정보를 활용한다.
    - @Nullable String 은 코틀린에서 String?과 같고, @NotNull String 은 코틀린 쪽에서 String과 같다.
- 두번째로 애노테이션이 없을 경우 자바의 타입은 코틀린에서 플랫폼 타입이 된다.
- 플랫폼 타입은 코틀린에서 널 관련 정보를 알 수 없는 타입을 말하며, 널이 될 수 있는 타입과 없는 타입으로도 처리가 가능하며 모든 책임을 작성자에게 전가한다.

```kotlin
/* 자바 */
public class Person {

	private final String name;
	
	public Person(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}
}
```

- 위와 같은 자바 코드에서 코틀린 컴파일러는 String의 타입의 널 가능성에 대해 전혀 모른다.
- 코틀린 컴파일러는 public 가시성인 코틀린 함수의 널이 아닌 타입인 파라미터와 수신객체에 대한 널 검사를 추가해준다. public 가시성 함수에 널 값을 사용하면 즉시 예외가 발생하는데 이런 파라미터 값 검사는 함수 내부에서 파라미터를 사용하는 시점이 아니라 함수 호출 시점에서 이루어지기 때문에 잘못된 인자로 함수를 호출해도 그 인자가 여러 함수에 전달되기 전에 예를 발생시켜 그 원인을 파악하기 쉬워진다.

```kotlin
// 널 검사 없이 자바 클래스 접근하기
fun yellAt(person:Person){

	println(person.name.toUpperCase()+"!!!")

}

>>> yellAt(Person(null))
// 에러 발생 -> Npe가 아니라 수신객체로 널을 받을 수 없다는 예외가 발생

// 널 검사를 통해 자바 클래스 접근하기
fun yellAtSafe(person: Persion){
	println(person.name ?: "Anyone").toUpperCase()+"!!!")
}
>>> yellAt(Person(null))
Anyone!!!
```

- 위 예제의 두번째 코드에서처럼 널 값을 제대로 처리하면 실행 시점에 예외가 발생하지 않는다.
- 자바 api는 애노테이션을 잘 안쓰기 때문에 자바 메서드 문서를 자세히 살펴보고 널을 반환할지 알아 낸 다음 널을 반환하는 메서드에 대한 검사를 추가해야한다.
- 모든 자바 타입을 널이 될 수 있는 타입으로 다루면 더 안전할 수 있지만, 널이 될 수 없는 값에 대해서도 불필요한 검사를 해야되기 때문에 코틀린은 실용적 차원에서 개발자에게 그 책임을 넘겼다.
- 코틀린에서 플랫폼 타입을 선언하는 건 불가능하다. 코틀린 컴파일러는 String!과 같은 방법으로 표현하지만 코틀린에서 이를 쓸수는 없다.

- 코틀린에서는 자바 메서드를 오버라이드 할때 그 메서드의 파라미터와 반환 타입을 널이 될 수 있는 타입으로 선언할지 없는 타입으로 선언할지 결정해야하는데 코틀린 컴파일러는 여러 다른 널 가능성으로 구현한 케이스를 모두 받아들인다.
- 구현 메서드를 다른 코틀린 코드가 호출 할 수 있으므로 코틀린 컴파일러는 널이 될 수 없는 타입으로 선언된 모든 파라미터에 대해 널이 아님을 검사하는 단언문을 만들어 주어 널 값을 넘기면 예외를 발생 시킨다.

# 6.2 코틀린의 원시 타입

- 코틀린은 원시타입과 래퍼 타입을 구분하지 않는다.

## 6.2.1 원시 타입: Int, Boolean

- 자바는 원시타입과 참조타입을 구분하는데, 원시타입에 대해 메서드를 호출하거나 컬렉션에는 이를 담을 수 없다. 특별한 래퍼 타입으로 원시 타입 값을 감싸서 활용한다.
- 하지만 코틀린에서는 이 둘을 구분하지 않고 항상 같은 타입을 사용하며, 원시 타입의 값에 대해 메서드를 호출 할 수도 있다.
- 그렇다고 코틀린이 항상 타입에 따라 객체로 사용하는 것은 아니며, 대부분의 경우에는 자바 타입으로 컴파일을 시키고, 이런 컴파일이 불가능한 경우는 컬렉션과 같은 제네릭 클래스를 사용할 뿐이다.
- Int와 같은 코틀린 타입에는 널 참조가 들어갈 수 없기 때문에 상응하는 자바 원시 타입으로 컴파일이 되고, 반대로 자바 원시타입의 값은 널이 될 수 없으므로 코틀린에서도 널이될 수 없는 타입으로 취급된다.

 

## 6.2.2 널이 될 수 있는 원시 타입: Int?, Boolean? 등

- null 참조를 자바의 참조타입의 변수에만 대입할수 있기 때문에, 널이 될 수 있는 코틀린 타입은 자바 래퍼 타입으로 컴파일된다.

```kotlin
data class Person(val name:String, val age:Int? = null){
	fun isOlderThan(other:Person):Boolean?{
		if(age == null || other.age == null)
			return null

		return age > other.age
		
	}
}

>>> println(Person("Sam", 35).isOlderThan(Person("Amy", 42)))
false

>>> println(Person("Sam", 35).isOlderThan(Person("Jane")))
null
```

- 위 예시처럼 널이 될 가능성이 있으면 Int? 타입의 값을 직접 비교할 수 없다. 모두 널이 아닌지 검사한 후 컴파일러는 두 값을 일반적인 값으로 다룬다. 변수나 프로퍼티가 널이 들어갈 수 있는지만 확인해서 적절한 타입을 찾으면 된다!
- 제네릭 클래스의 경우에는 래퍼 타입을 사용한다. 어떤 클래스의 타입인자로 원시타입을 넘기면 코틀린은 그 타입에 대한 박스 타입을 사용한다.
    - 이렇게 컴파일 되는 이유는 자바 가상머신은 타입인자로 원시타입을 허용하지 않는다. 자바나 코틀린 모두에서 제네릭 클래스는 항상 박스타입을 사용해야 한다. 원시타입으로 이뤄진 대규모 컬렉션을 효율적으로 저장해야한다면 원시타입으로 이뤄진 효율적인 컬렉션을 제공하는 서드파티 라이브러리를 쓰거나 배열을 사용해야 한다.

## 6.2.3 숫자 변환

- 코틀린은 한 타입의 숫자를 다른 타입의 숫자로 자동 변환하지 않고, 결과 타입이 허용하는 숫자의 범위가 원래 타입의 넓은 범위에 있어도 자동 변환은 불가능하다.

```kotlin
val i = 1
val l : Long = i // -> 컴파일 오류 발생한다.

val i = 1
val l : Long = i.toLong() // -> 문제 없음
```

- 코틀린은 모든 원시 타입에 대한 변환 함수를 제공한다. 함수 이름은 to+타입() 형식으로 양방향 변환 함수가 모두 제공된다.
    - 이는 혼란을 막기 위해 타입 변환을 명시하는 것으로 특히 박스 타입을 비교할 때 그렇다. 두 박스 타입 간의 equals 메서드는 그 안에 들어있는 값이 아니라 박스타입 객체를 비교하는데 이는 명시적으로 같은 타입의 값을 만든 후 비교한다.
- 코드에서 동시에 여러 숫자 타입을 사용하려면 예상치 못한 동작을 피하기 위해 각 변수를 명시적으로 변환해야 한다.
- 숫자 리터럴(278페이지 참고)을 사용할 때는 보통 변환함수를 호출 필요가 없다. 이런 경우는 변수나 함수 내에서 사용시 컴파일러가 필요한 변환을 자동으로 넣어준다.

```kotlin
fun foo(l:Long) = println(l)
>>> val b:Byte = 1 // 상수 값은 적절히 해석
>>> val l = b+1L // 연산자는 각 숫자를 인자로 받을 수 있고
>>> foo(42) // 컴파일러는 알아서 Long으로 해석
42
```

- 코틀린 산술 연산자에서도 자바와 똑같이 표현 범위를 넘어가는 값이 있으면 오버플로우가 발생할 수 있다. 코틀린은 그리고 이를 검사하느라 추가 비용을 들이지 않는다.
- 문자열을 숫자로 변환할 때 코틀린 표준 라이브러리는 문자열을 원시 타입으로 변환하는 여러 함수를 제공한다. 이런 함수는 문자열의 내용을 각 원시 타입을 표기하는 문자열로 파싱한다. 파싱에 실패할 경우에는 NumberFormatException이 발생한다.

## 6.2.4 Any, Any?:

- 자바에서 클래스 계층의 최상위 타입이 Object 이듯이 코틀린에서는 Any 타입이 모든 널이 될 수 없는 타입의 조상타입이다.
- 자바에서는 참조타입만 Object를 정점으로 하는 타입 계층에 포함되고 원시타입은 그런 계층에 안 들어있지만, 코틀린에서는 Any가 Int 등의 원시 타입을 포함한 모든 타입의 조상이다.
- 코틀린에서도 원시 타입 값을 Any 타입의 변수에 대입하면 자동으로 값을 객체로 감싼다. Any는 널이 될 수 없는 타입이므로 그 변수에는 널이 들어갈 없고 널 까지 포함한 값을 변수로 선언하려면 Any?타입을 사용해야한다.
- 모든 코틀린 클래스에는 toString, equals, hashCode라는 세 메서드가 들어있는데 이는 Any 에 정의된 메서드를 상속한 것이다. 하지만 자바 Object애 있는 다른 메서드는 사용할 수 없다. 이런 메서드를 호출하고 싶으면 Object 타입으로 캐스트를 해야한다.

## 6.2.5 Unit 타입: 코틀린의 void

- 자바의 void와 같은 기능을 하는 것이 코틀린의 Unit 타입이다. 이는 반환 타입 선언 없이 정의한 블록이 본문인 함수와 같다.
- 코틀린에서 반환 타입이 Unit이고 그 함수가 제네릭 함수를 오버라이드 하지 않는다면, 그 함수는 내부에서 자바 void 함수로 컴파일 된다.
- Unit과 void의 차이는 Unit은 타입 인자로 쓸 수 있다는 점이다. Unit 타입에 속한 값은 단 하나 뿐이고 그 이름도 Unit이다. Unit 타입의 함수는 Unit 값을 묵시적으로 반환한다. 이 두 특성은 제네릭 파라미터를 반환하는 함수를 오버라이드하면서 반환 타입으로 Unit을 쓸 때 유용하다.
- [https://stackoverflow.com/questions/55953052/kotlin-void-vs-unit-vs-nothing](https://stackoverflow.com/questions/55953052/kotlin-void-vs-unit-vs-nothing) 참고할 것!

```kotlin
interface Processor<T>{
	fun process() : T
}

class NoResultProcessor : Processor<Unit> {
	override fun process(){
		// 업무 처리 코드
	}
}
```

- 타입 인자로 값 없음을 표현하는 문제를 자바에서 해결할 방법을 생각해보면,
    - 첫째는 별도의 인터페이스를 사용해서 값을 반환하는 경우와 값을 반환하지 않는 경우를 분리하는 방법도 있다.
    - 또 다른 방법은 java.lang.Void 타입을 특별히 사용할 수도 있다. 이런 경우에는 여전히 Void 타입에 대응할 수 있는 유일한 값인 null을 반환하기 위해 return null을 명시해야한다. 반환타입이 void가 아니므로 함수를 반환할 때 항상 return null을 사용해야 한다.
- 함수형 프로그래밍에서 전통적으로 단 하나의 인스턴스만 갖는 타입을 Unit이라고 불러왔는데 그 유일한 인스턴스의 유무가 자바의 void와의 차이라고 할수 있다.

## 6.2.6 Nothing 타입: 이 함수는 결코 정상적으로 끝나지 않는다

- 코틀린에는 결코 성공적으로 값을 돌려주는 일이 없으므로 반환값이라는 개념 자체가 의미 없는 함수가 일부 존재한다. 그 예로 fail이라는 테스트 라이브러리 함수가 있는데, 이 함수는 특별한 메시지가 들어있는 예외를 던져서 현재 테스트를 실패시킨다. 이렇게 정상적으로 끝나지 않는 함수의 상태를 알리고 표현하기 위해 Nothing이라는 반환 타입을 사용한다.
- Nothing 타입은 아무 값도 포함하지 않고 반환 타입이나 반환 타입으로 쓰일 타입 파라미터로만 쓸 수 있다.

```kotlin
val address = company.address ?: fail("No Address")
println(address.city)
```

- 컴파일러는 company.address가 널인 경우 엘비스 연산자의 우항에서 예외가 발생하는 사실을 파악하고 address의 값이 널이 아님을 추론할 수 있다.

# 6.3 컬렉션과 배열

## 6.3.1 널 가능성과 컬렉션

- 컬렉션 안에 널 값을 넣을 수 있는지 여부는 어떤 변수의 값이 널이 될 수 있는지와 같이 중요하다.

```kotlin
fun readNumbers(reader:BufferedReader):List<Int?>{
	val result = ArrayList<Int?>()
	for(line in reader.lineSequence()){
		try {
			val number = line.toInt()
			result.add(number)
		}catch(e: NumberFormatException){
			result.add(null)
		}
	}
	return result
}
```

- List<Int?> 의 경우에는 리스트 자체는 항상 널이 아니지만 리스트에 들어있는 모든 각 원소는 널이 될 수 있다. List<Int>?의 경우에는 리스트를 가리키는 변수에는 널이 들어갈 수 있지만 리스트 안에는 널이 아닌 값만 들어갈 수 있다.
- 널이 될 수 있는 값으로 이뤄진 널이 될 수 있는 리스트를 정의해야할 경우에는 List<Int?>? 로 이를 표현하는데 이런 리스트를 처리할 때는 변수에 대해 널 검사를 수행한 다음에 그 리스트에 속한 모든 원소에 대해 다시 널 검사를 수행해야 한다.

```kotlin
// 널이 될 수 있는 값으로 이뤄진 컬렉션 다루기
fun addValidNumbers(numbers:List<Int?>){

	var sumOfValidNumbers = 0
	var invalidNumbers = 0
	for(number in numbers){
		if(number != null) {
			sumOfValidNumbers += number
		} else {
			invalidNumbers++
		}
	}
	println("sum of valid numbers : $sumOfValidNumbers")
	println("invalid numbers : $invalidNumbers")
}

// filterNotNull을 사용
fun addValidNumbers(numbers:List<Int?>){
	var validNumbers = numbers.filterNotNull()
	println("sum of valid numbers : ${validNumbers.sum()}")
	println("invalid numbers : ${numbers.size - validNumbers.sum()}")
}
```

- 걸러내는 연산도 컬렉션의 타입에 영향을 끼친다. filterNotNull이 컬렉션 안에 널이 들어있지 않음을 보장해주므로 validNumbers는 List<Int> 타입이다.

## 6.3.2 읽기 전용과 변경 가능한 컬렉션

- 코틀린 컬렉션과 자바 컬렉션으로 나누는 가장 중요한 특성 하나는 코틀린에서는 컬렉션 안의 데이터 접근하는 인터페이스와 컬렉션 안의 데이터를 변경하는 인터페이스를 분리했다는 점이다.
- 컬렉션의 데이터를 수정하려면 kotlin.collections.MutableCollection 인터페이스를 사용한다. 이는 일반 인터페이스를 확장하면서 원소를 추가, 삭제, 원소를 모두 지우는 등의 메서드를 제공한다.
- 일반적으로는 읽기 전용 인터페이스를 사용하고 변환이 필요한 경우에만 변경 가능 인터페이스를 사용한다.
- 이를 구별한 이유는 프로그램에서 어떤 일이 벌어지는지를 더 쉽게 이해하기 위함이며 사용하는 인터페이스에 따라 그 함수는 컬렉션을 어떻게 다루려는지 파악이 가능하다.
- 어떤 컴포넌트의 내부 상태에 컬렉션이 포함된다면 그 컬렉션을 MutableCollection을 인자로 받는 함수에 전할 때 원본의 변경을 막기 위해 컬렉션을 복사해야 할 수도 있다. → 방어적 복사

```kotlin
fun <T> copyElements(source:Collection<T>, target:MutableCollection<T>){
	for(item in source){
		target.add(item)
	}
}
```

- 위 예제를 보면 source 컬렉션은 변경하지 않지만 target은 변경하리라는 사실을 알수 있다.
- target에 해당하는 인자로 읽기 전용 컬렉션을 당연히 넘길 수 없다. 실제 컬렉션이 변경 가능한 컬렉션인지 여부와 상관 없이 선언된 타입이 읽기 전용이면 컴파일 오류가 난다.
- 컬렉션 인터페이스를 사용할 때 읽기 전용 컬렉션이라고 해서 꼭 변경 불가능한 컬렉션일 필요는 없다. 읽기 전용 인터페이스 타입인 변수를 사용할 때 그 인터페이스를 실제로는 다른 컬렉션 인스턴스를 가리키는 경우 일 수도 있다.
- 이런 상황에서 그 이 컬렉션을 참조하는 다른 코드를 호출하거나 병렬 실행한다면 컬렉션을 사용하는 도중에 다른 컬렉션이 그 컬렉션의 내용을 변경하는 상환이 생길 수 있고 다른 오류가 발생할 수 있다. 다중 스레드 환경에서 데이터를 다루는 경우 그 데이터를 동기화하거나 동시 접근을 허용하는 데이터구조를 사용해야한다.

## 6.3.3 코틀린 컬렉션과 자바

- 모든 코틀린 컬렉션은 그에 상응하는 자바 컬렉션 인터페이스의 인스턴스이기 때문에 자바와 코틀린 사이를 오갈 때 아무 변환도 필요 없다. 또한 래퍼 클래스를 만들거나 데이터를 복사할 필요도 없다. 코틀린은 모든 자바 컬렉션의 인터페이스마다 읽기 전용과 변경 가능 인터페이스를 제공한다.
- 각 인터페이스의 기본 구조는 java.util 패키지에 잇는 자바 컬렉션 인터페이스의 구조를 그대로 옮겼고, 추가로 변경 가능한 인터페이스는 자신과 대응하는 읽기 전용 인터페이스를 확장/상속한다.
- 291 페이지 6.1 표 참고
- 자바 메서드를 호출 하되 컬렉션을 인자로 넘겨야 한다면 따로 변환하거나 복사하는 등의 추가 작업 없이 직접 컬렉션을 넘기면 된다. 예를 들어 java.util.collection을 파라미터로 받는 자바 메서드가 있다면 아무 collection이나 mutableCollection 값을 인자로 넘길 수 있다.
- 그런데 컬렉션의 변경 가능성에 대해 자바는 이를 구분하지 않음으로 코틀린에서 어떤 컬렉션으로 선언된 객체에 대해 자바 코드에서는 이를 구분하지 않고 변경할 수도 있게 된다. 코틀린 컴파일러는 자바 코드가 컬렉션에 대해 어떻게 다루는지 완전히 분석을 할 수 없기 때문에 이를 막을 수도 없다.
- 이 문제에 대해서는 개발자가 올바른 파라미터 타입을 사용할 책임을 지어야 한다.

## 6.3.4 컬렉션을 플랫폼 타입으로 다루기

- 앞에 설명한 플랫폼 타입은 컬렉션에서도 작용한다. 자바 쪽에서 선언한 컬렉션의 타입의 변수를 코틀린에서는 플랫폼 타입으로 보고 널이 될 수 있는 타입이든 없는 타입이든 허용한다. 컬렉션의 읽기 전용과 변경 가능성에 대한 것도 마찬가지이다.
- 컬렉션 타입이 시그니처에 들어간 자바 메소드 구현을 오버라이드하려는 경우 읽기 전용과 변경 가능 컬렉션의 차이가 문제가 된다. 플랫폼 타입에서 널 가능성을 다룰 때처럼 이런 경우에도 오버라이드 하려는 메서드의 자바 컬렉션 타입을 코틀린 컬렉션 타입 중 어떤걸로 표현할지 직접 결정해야한다.
    - 컬렉션이 널이 될수 있는가?
    - 컬렉션의 원소가 널이 될 수 있는가?
    - 오버라이드하는 메서드가 컬렉션을 변경할 수 있는가?
- 어떤 맥락에서 자바 인터페이스와 클래스가 사용되었는지 확인하고 어떤 작업을 수행해야 할지 검토할 것!

## 6.3.5 객체의 배열과 원시 타입의 배열

- 코틀린 배열은 타입 파라미터를 받는 클래스다. 배열의 원소 타입은 바로 그 타입 파라미터에 의해 정해진다.
- 코틀린에서 배열을 만드는 방법은 다양하다.
    - arrayOf 함수에 원소를 넘기면 배열을 만들 수 있다,
    - arrayOfNulls 함수에 정수 값을 인자로 넘기면 모든 원소가 null이고 인자로 넘긴 값과 크기가 같은 배열을 만들수 있다. 물론 우너소 타입이 널이 될 수 있는 타입인 경우에만 그 이 함수를 쓸 수 있다.
    - Array 생성자는 배열 크기와 람다를 인자로 받아서 람다를 호출해서 각 배열 원소를 초기화해준다. arrayOf를 쓰지 않고 각 원소가 널이 아닌 배열을 만들어야 하는 겨우 이 생성자를 사용

```kotlin
val letters = Array<String>(26) { i -> ('a'+i).toString() }
```

- 람다는 배열 원소의 인덱스를 인자로 받아서 배열의 해당 위치에 들어갈 원소를 반환한다.

```kotlin
val strings = listOf("a", "b", "c")

println("%s/%s/%s".format(*strings.toTypedArray()))
```

- 코틀린에서는 배열을 인자로 받는 자바 함수를 호출하거나 vararg파라미터(가변인자)를 받는 코틀린 함수를 호출하기 위해 가장 자주 배열을 만드는데, 이때 데이터가 이미 컬렉션에 들어 있으면 컬렉션을 배열로 변환해야한다. 이는 위와 같이 toTypedArray 메서드를 통해 쉽게 컬렉션을 배열로 바꿀 수 있다.
- 다른 제네릭 타입에서처럼 배열 타입의 타입 인자도 항상 객체 타입이 된다. Array<Int>와 같은 타입을 선언하면 그 배열은 박싱된 정수의 배열이다. 박싱하지 않은 원시 타입의 배열이 필요하면 그런 타입을 위한 특별할 배열 클래스를 사용해야하는데, 코틀린은 원시 타입의 배열을 표현하는 별도 클래스를 각 타입마다 하나씩 제공한다.
    - IntArray, ByteArray, BooleanArray 등과 같이 표현되는 원시타입 배열은 자바원시 타입 배열인 int[], byte[] 등으로 컴파일된다. 원시타입 배열을 만드는 방법은 다음과 같다.
        - 각 배열의 타입 생성자는 size 인자를 받아서 해당 원시 타입의 디폴트 값으로 초기화 된 size 크기의 배열을 반환한다.
        - 팩토리 함수는 여러 값을 가변 인자로 받아서 그런 값이 들어간 배열을 반환한다.
        - 크기와 람다를 인자로 받는 생성자를 사용한다.
- 박싱된 값이 들어있는 컬레션이나 배열이 있다면 변환 함수를 사용해 박싱하지 않는 값이 들어있는 배열로 변환도 가능하다.
- 코틀린 표준 라이브러리는 배열 기본 연산에 더해 컬레션에 사용할 수 있는 모든 확장 함수를 배열에도 제공한다. 5장에서 살펴본 함수들과 원시타입인 원소로 이뤄진 배열에도 그런 확장함수를 똑같이 사용할 수 있다. 그 예로 forEachIndexed 함수와 람다를 사용해서 위에 알파벳 나열 함수를 다시 작성할 수 있다.

```kotlin
fun main(args:Array<String>){

	args.forEachIndexed{ index, element -> println("Argument $index is: $element")

}
```