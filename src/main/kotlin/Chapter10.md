# 10장. 애노테이션과 리플렉션

# 10.1 애노테이션 선언과 적용

- 어떤 함수를 호출하기 위해서는 그 함수가 정의된 클래스의 이름과 함수 이름, 파라미터 이름 등을 알아야 한다.
- Annotation 과 Reflection 을 사용하면 그런 제약을 벗어나서 미리 알지 못하는 임의의 클래스를 다룰 수 있다.
- Annotation은 라이브러리가 요구하는 의미를 클래스에게 부여 가능
- Reflection은 실행 시점에 컴파일러 내부 구조를 분석

## 10.1.1 애노테이션 적용

- 코틀린에는 자바와 같은 방법으로 애노테이션을 적용할 수 있는데, 적용하려면 대상 앞에 애노테이션을 붙이면 된다.
- 예제에서는 @Deprecated 을 보여주는 이는 자바와 같은 의미를 갖지만 코틀린에서는 replaceWith 파라미터를 통해 옛 버전을 대신할 수 있는 패턴을 제시한다. API 사용자는 그 패턴을 보고 지원을 종료할 aPI 기능을 더 쉽게 새 버전으로 포팅 가능.
- 아래 예제는 사용 금지를 설명하는 메시지와 대체할 패턴을 지정한다.

```kotlin
@Deprecated(“Use removeAt(index) instead.”, ReplaceWith(“removeAt(index)”))
fun remove(index: Int) { … }
```

- 애노테이션의 인자로는 원시 타입의 값, 문자열, enum, 클래스 참조, 다른 애노테이션 클래스, 그리고 지금까지 말한 요소들로 이뤄진 배열이 들어갈 수 있는데, 인자를 지정하는 문법은 자바와 약간 다르다.
    - 클래스를 애노테이션 인자로 지정시 @MyAnnotation(MyClass :: class)처럼 :: class 를 클래스명 뒤에 넣어야 한다.
    - 다른 애노테이션을 인자로 지정할 때는 인자로 들어가는 애노테이션 명 앞에 @를 넣지 말아야 한다. 그 예로 위 예시에서 ReplaceWith는 애노테이션인데 인자로 들어가므로 @를 사용하지 않는다.
    - 배열을 인자로 지정하려면 @RequestMapping(path=arrayOf(”/foo”, ”/bar”)) 와 같이 arrayOf 함수를 사용한다. 자바에서 선언한 애노테이션 클래스를 사용한다면 value 라는 이름의 파라미터가 필요에 따라 자동으로 가변 길이 인자로  변환된다. 따라서 그런 경우에는 @JavaAnnotationWithArrayVal(”abc”, “foo”, “bar”)처럼 arrayOf 함수를 쓰지 않아도 된다.
- 프로퍼티를 애노테이션 인자로 사용하려면 그 앞에 const 변경자를 붙여야 하고 컴퍼일러는 const가 붙은 프로퍼티를 컴파일 시점 상수로 취급한다. 아래 예시에서 이를 사용한다.
- const가 붙은 프로퍼티는 파일의 맨 위나 object 안에 선언해야 하며 원시 타입이나 String으로 초기화해야한다.

```kotlin
const val TEST_TIMEOUT=100L
@Test(timeout=TEST_TIMEOUT) fun testMethod() {…}
```

## 10.1.2 애노테이션 대상

- 자바 또는 코틀린에 선언된 애노테이션을 통해 프로퍼티에 애노테이션을 붙이는 경우에는 사용시점대상(user-site target) 선언으로 애노테이션을 붙일 요소를 정할 수 있다. 그 예로 아래는 getter에 애노테이션을 붙이고 싶을 때 선언하는 예시이다.

```kotlin
// 사용시점대상 : 애노테이션 이름
@get:MyAnnotation
val temp = Temp()
```

- 자바에 선언된 애노테이션을 사용해서 프로퍼티에 붙이는 경우, 위와 같이 기본적으로 프로퍼티의 필드에 그 애노테이션을 붙인다. 하지만 코틀린으로 애노테이션을 선언하면 프로퍼티에 직접 적용할 수 있는 애노테이션을 만들 수 있다.

```kotlin
class HashTempFolder {

	@get : Rule
	val folder = TemporaryFolder()
	@Test
	fun testUsingTempFolder(){
		val createdfile = folder.newFile("myfile.txt")
		val createdfolder = folder.newFolder("subfolder")
		...
	}

}
```

- 사용 지점 대상을 지정할 때 지원하는 대상 목록은 다음과 같다.
    - property : 프로퍼티 전체. 자바에서 선언된 애노테이션에는 이 사용 지점 대상을 사용할 수 없다.
    - field : 프로퍼티에 의해 생성되는 필드
    - get : 프로퍼티 게터
    - set : 프로퍼티 세터
    - receiver : 확장 함수나 프로퍼티의 수신 객체 파라미터
    - param : 생성자 파라미터
    - setparam : 세터 파라미터
    - delagate : 위임 프로퍼티의 위임 인스턴스를 담아둔 필드
    - file : 파일 안에 선언된 최상위 함수와 프로퍼티를 담아두는 클래스

```kotlin
fun test(list: List<*>){

    @Suppress("UNCHECKED_CAST")
    val strings = list as List<String>
}
```

- 자바 API를 애노테이션으로 제어하기
    - 코틀린은 코틀린으로 선언한 내용을 자바 바이트코드로 컴파일하는 방법과 코틀린 선언을 자바에 노출하는 방법을 제어하기 위한 애노테이션을 제공한다.
        - @JvmName은 코틀린 선언이 만들어내는 자바 필드나 메서드 이름을 변경
        - @JvmStatic을 메서드, 객체 선언, 동반 객체에 적용하면 그 요소가 자바 정적 메서드로 노출
        - @JvmOverloads를 사용하면 디폴트 파라미터 값이 있는 함수에 대해 컴파일러가 자동으로 오버로딩한 함수를 생성해준다.
        - @JvmField 를 프로퍼티에 사용하면 게터나 세터가 없는 공개된 자바 필드로 프로퍼티를 노출시킨다.

## 10.1.3 애노테이션을 활용한 JSON 직렬화 제어

- 직렬화는 객체를 저장장치에 저장하거나 네트워크를 통해 전송하기 위해 텍스트나 이진 형식으로 변환하는 것.
- 역직렬화는 그 반대 과정으로 텍스트나 이진 형식으로 저장된 데이터로부터 원래의 객체를 만들어냄.

```kotlin
data class Person(val name: String, val age: Int)

>>> val person = Person("Alice", 29)
>>> println(serialize(person))
{"age":29, "name":"Alice"}

>>> val json = """{"name":"Alice", "age":29}"""
>>> println(deserialize<Person>(json))
Person(name=Alice, age=29)
```

- 위 예시에서 Person의 인스턴스를 serialize 함수에 전달하면 JSON표현이 담긴 문자열을 돌려 받는데, 키와 값 쌍으로 이뤄진 객체를 표현한다. JSON에는 객체의 타입이 저장되지 않기 떄문에 JSON 데이터로부터 인스턴스를 만들려면 타입 인자로 클래스를 명시해야 한다.
- 애노테이션을 활용해 객체를 직렬화하거나 역직렬화하는 방법을 제어할 수 있다. 객체를 JSON으로 직렬호할 때 제이키드 라이브러리는 기본적으로 모든 프로퍼티를 직렬화하며 프로퍼티 이름을 키로 사용한다.
    - @JsonExclude 애노테이션을 사용하면 직렬화나 역직렬화 시 그 프로퍼티를 무시할 수 있다.
    - @JsonName 애노테이션을 사용하면 프로퍼티를 표현하는 키 값 쌍의 키로 프로퍼티 이름 대신 애노테이션이 지정한 이름을 쓰게 할 수 있다.

```kotlin
data class Person(
		@JsonName("alias") val firstName: String,
		@JsonExclude val age: Int? = null
)
```

- firstName 프로퍼티를 JSON으로 저장할 때 사용하는 키를 변경하기 위해 @JsonName 애노테이션을 사용하고, age 프로퍼티를 직렬화나 역직렬화에 사용하지 않기 위해 @JsonExclude 애노테이션을 사용
- 직렬화 대상에서 제외할 age 프로퍼티에는 반드시 디폴트 값을 지정해야 하며, 지정하지 않으면 인스턴스를 새로 만들 수 없다.

## 10.1.4 애노테이션 선언

- 제이키드 애노테이션 중에서 @JsonExclude는 아무 파라미터도 없는 가장 단순한 애노테이션인데 일반 클래스와 선언이 비슷하다.

```kotlin
annotation class JsonExclude
```

- 하지만 애노테이션 클래스는 선언이나 식과 관련하여 메타데이터의 구조를 정의하기 때문에 내부에 아무 코드도 없을 때가 있다. 그 이유로 컴파일러는 애노테이션 클래스에서 본문을 정의하지 못하게 막는다.
- 파라미터가 잇는 애노테이션을 정의하려면 애노테이션 클래스의 주 생성자에 파라미터를 선언해야함

```kotlin
annotation class JsonName(val name: String)
```

- 일반 클래스의 주 생성자 선언 구문을 똑같이 사용하지만 애노테이션 클래스에서는 모든 파라미터 앞에 val을 붙어야한다.

```kotlin
// 자바
public @interface JsonName{
    String value();
}
```

- 코틀린 애노테이션에서는 name이라는 프로퍼티를 사용하지만 자바 애노테이션에서는 value라는 메서드를 쓴다. 자바에서는 어떤 애노테이션을 적용할 때 value를 제외한 모든 애트리뷰트에는 이름을 명시해야함
- 코틀린에서는 일반적인 생성자 호출과 동일하다. 따라서 인자의 이름에 명시하기 위해 이름 붙인 인자를 사용할 수 있고 이름을 생략할 수도 있다.
- 자바에서 선언한 애노테이션을 코틀린에서 사용할 경우, value를 제외한 모든 인자에 이름을 붙여서 사용해야 한다.

## 10.1.5 메타애노테이션 : 애노테이션을 처리하는 방법 제어

- 애노테이션 클래스에 적용할 수 있는 애노테이션을 메타 애노테이션이라고 한다.
- 메타 애노테이션의 경우, 컴파일러가 애노테이션을 처리하는 방법을 제어한다. 그 예로 의존관계 주입 라이브러리들이 메타애노테이션을 사용해 주입 가능한 타입이 동일한 여러 객체를 식별한다.
- 그 예로 아래 @Target 애노테이션은 선언된 애노테이션에 적용할 수 있는 요소 유형을 정의한다.

```kotlin
@Target(AnnotationTarget.PROPERTY)
annotation class JsonExclude
```

- 애노테이션 클래스에 대해 구체적인 @Target을 지정하지 않으면 모든 선언에 적용될 수 있는 애노테이션이 된다. 하지만 제이키드 라이브러리는 프로퍼티 애노테이션만을 사용하므로 애노테이션 클래스에 @Target을 꼭 지정해야 한다.
- 애노테이션이 붙을 수 잇는 대상이 정의된 enum은 AnnotationTarget이다. 그 안에는 클래스, 파일, 프로퍼티, 프로퍼티 접근자, 타입, 식 등에 대한 이넘 정의가 들어있다. 필요하면 둘 이상의 대상을 한꺼번에 선언할 수도 있다.
- 매타애노테이션을 직접 만ㄷ르어야 된다면 ANNOTATION_CLASS를 대상으로 지정하면 된다.

```kotlin
// 메타에노테이션을 통한 메타애노테이션 클래스 정의
@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class BindingAnnotation

// 위에서 정의한 메타에노테이션을 이용해 애노테이션 클래스 정의
@BindingAnnotation
annotation class MyBinding
```

- 대상을 PROPERTY로 지정한 애노테이션을 자바 코드에서 사용할 수 없는데 자바에서 그런 애노테이션을 사용하려면 AnnotationTarget.FIELD를 두번째 대상으로 추가해야 한다. 그렇게 하면 애노테이션을 코틀린 프로퍼티와 자바 필드에 적용할 수 있다.
- @Retention 애노테이션
    - 정의한 애노테이션 클래스를 소스 수준에서 유지하고, .class파일 저장, 실행 시점에 리플렉션을 사용해 접근할 수 있게 할지 지정.
    - 자바 컴파일러는 기본적으로 애노테이션을 .class 파일에 저장하고, 런타임에 사용할 수 없도록 한다.
    - 대부분의 애노테이션을 런타임에도 사용을 필요로 해서 디폴트로 코틀린에서는 @Retention을 RUNTIME으로 지정한다.

## 10.1.6 애노테이션 파라미터로 클래스 사용

- 어떤 클래스를 선언 메타데이터로 참조할 수 있는 기능이 필요할 때 클래스 참조를 파라미터로 하는 애노테이션 클래스를 선언하여 사용할수 있다.
- 제이키드 라이브러리에 있는 @DeserializeInterface는 인터페이스 타입인 프로퍼티에 대한 역직렬화를 제어할 때 쓰는 애노테이션으로 인터페이스의 인스턴스를 직접 만들 수는 없으니 역직렬화 시 어떤 클래스를 사용해 인터페이스를 구현할지 지정할 수 있어야 한다.

```kotlin
interface Company {
    val name: String
}

data class CompanyImpl(override val name: String): Company

data class Person(
    val name: String,
    @DeserializeInterface(CompanyImpl::class) company: Company // 일반적으로 클래스를 참조하기위해  ::class 를 뒷붙힌다.
)
```

- 직렬화된 person인스턴스를 역직렬화하는 과정에서 company 프로퍼티를 표현한 JSON을 읽으면 제이키드는 그 프로퍼티 값에 해당하는 JSON을 역직렬화하면서 CompanyImple의 인스턴스를 생성하고 Person 인스턴스의 company 프로퍼티에 생성한다.

```kotlin
annotation class DeserializeInterface(val targetClass: KClass<out Any>)
```

- KClass는 자바 java.lang.Class 타입과 같은 역할을 하는 코틀린 타입이다.
- 코틀린 클래스에 대한 참조를 저장할 때 이 타입을 사용하는데, KClass의 타입 파라미터는 이 KClass의 인스턴스가 가리키는 코틀린 타입을 지정한다.

## 10.1.7 애노테이션 파라미터로 제네릭 클래스 받기

- 기본적으로 제이키드는 원시타입이 아닌 프로퍼티를 중첩된 객체로 직렬화한다. 이런 기본 동작을 변경하고 싶으면 값을 직렬화하는 로직을 직접 제공하면 된다.
- @CustomSerializer 애노테이션은 커스텀 직렬화 클래스에 대한 참조를 인자로 받는다. 인자로 쓰이는 커스텀 직렬화 클래스는 ValueSerializer<T> 인터페이스를 구현해야 한다.

```kotlin
interface ValueSerializer<T> {
		// 직렬화 
    fun toJsonValue(value: T): Any?
    // 역직렬화
		fun fromJsonValue(jsonValue:Any?):T
}
```

- 예시로 날짜를 직렬화 하는 경우가 나오는데, ValueSerializer<Date>를 구현하는 DateSerializer를 만들어서 인자로 애노테이션에 넘겨서 사용하는 걸 아래와 같이 보여준다.

```kotlin
data class Person(
    val name: String,
    @CustomSerializer (DateSerializer::class) val birthDate: Date
)
```

- 위의 @CustomSerializer을 작성하려고 하면, ValueSerializer 클래스는 제네릭 클래스라서 타입 파라미터가 있다. 따라서 타입을 참조하려면 항상 타입 인자를 제공해야한다. 하지만 이 애노테이션이 어떤 타입에 대해 쓰일지 전혀 알 수 없으므로 여기서는 스타 프로젝션을 아래와 같이 사용할 수 있다.

```kotlin
annotation class CustomSerializer(
    val serializerClass: KClass<out ValueSerializer<*>>)
```

- 위에 serializerClass파라미터의 타입을 나누어 보면
    - <out ValueSerializer<*>> 에서 DateSerialzer::class 는 올바른 인자로 받아들이지만 Date::class는 거부한다.
    - out은 ValueSerializer::class뿐 아니라 ValueSerializer를 구현하는 모든 클래스를 받아들인다.
    - <*> 부분에서 ValueSerializer를 사용해 어떤 타입의 값이든 직렬활할 수 있게 허용한다.

# 10.2 리플렉션 : 실행 시점에 코틀린 객체 내부 관찰

- 리플렉션은 실행시점에 객체의 프로퍼티와 메서드에 접근할 수 있게 해주는 방법이다.
- 일반적으로 객체의 메서드나 프로퍼티에 접근할 때는 프로그램 안에 구체적인 선언 있는 메서드나 프로퍼티의 이름을 사용하며, 컴파일러는 그런 이름이 실제로 가리키는 선언을 컴파일 시점에 찾아내서 선언이 실제 존재함을 보장해준다.
- 타입과 관계 없이 객체를 다뤄야 하는 경우나 객체가 제공하는 메서드나 프로퍼티 이름을 오직 실행 시점에서만 알 수 있는 경우가 존재하는데 그 예로 나오는 것이 JSON 직렬화 라이브러리다.
- 직렬화 라이브러리는 어떤 객체든 JSON으로 변환이 가능해야하고 실행 시점이 되기 전까지는 라이브러리가 직렬화할 프로퍼티나 클래스에 대한 정보를 알 수 없고 이럴 때 리플렉션을 사용한다.
- 코틀린에서 리플렉션을 사용하려면 서로 다른 두 API를 사용해야함
    - 첫번째는 자바에서 제공하는 java.lang.reflect 패키지로, 자바 리플렉션 API가 필요한 이유는 코틀린 클래스는 일반 자바 바이트 코드로 컴파일 되기 때문이다. 자바 리플렉션 API는 코틀린 클래스를 컴파일한 바이트코드로 완벽히 지원한다.
    - 두번째는 코틀린에서 제공하는 kotlin.reflect 에서 제공하는 API로 자바에서는 없는 프로퍼티나 널이 될 수 있는 타입과 같은 코틀린 고유 개념에 대한 리플렉션을 제공한다.

## 10.2.1 코틀린 리플렉션 API : KClass, KCallable, KFunction, KProperty

- KClass는 자바의 java.lang.Class 에 해당하는 클래스로 클래스 안에 있는 모든 선언을 열거하고 각 선언에 접근하거나 클래스의 상위 클래스를 얻는 등의 작업이 가능하다.
- MyClass::class 라는 식으로 KClass의 인스턴스를 얻을 수 있다. 실행 시점에 객체의 클래스를 얻기 위해서는 객체의 javaClass 프로퍼티를 사용해 객체의 자바 클래스를 얻어야 하고, javaClass는 java.lang.Object.getClass()와 동일하다. 자바 클래스를 얻었다면 .kotlin 확장 프로퍼티를 통해 자바에서 코틀린 리플렉션 API로 옮겨올 수 있다.

```kotlin
class Person(val name: String, val age: Int)
>>> import kotlin.reflect.full.* //memberProperties 확장함수 임포트
>>> val person = Person("Alice", 29)
>>> val kClass = person.javaClass.kotlin
>>> println(kClass.simpleName)
Person
>>> kClass.memberProperties.forEach{ println(it.name) }
age
name
```

- 위 예제는 클래스 이름과 그 클래스에 들어있는 프로퍼티 이름을 출력하고 memberProperties를 통해 클래스와 모든 조상 클래스 내부에 정의된 비확장 프로퍼티를 모두 가져온다.
- Kclass 선언을 찾아보면 내부에 사용할 수 있는 다양한 메서드를 볼 수 있다.

```kotlin
interface KClass<T : Any>{

	val simpleName: String?
	val qualifiedName: String?
	val members: Collection<KCallable<*>>
	val constructors: Collection<KFunction<T>>
	val nestedClasses: Collection<KClass<*>>
	...
}
```

- 해당 클래스의 모든 멤버 목록이 KCallable 인스턴스의 컬렉션이라는 사실을 알 수 있는데, KCallable은 함수와 프로퍼티를 아우르는 공통 상위 인터페이스이다. 그 내부에는 call 메서드가 존재하며 이 call을 사용하면 함수나 프로퍼티의 게터 호출이 가능하다.

```kotlin
interface KCallable<out R> {
		fun call(vararg args: Any?): R
		...
}
```

- call을 사용할 때는 함수 인자를 vararg리스트로 전달한다. 다음 예시를 통해 리플렉션이 call을 사용해 함수를 호출할 수 있음을 보여준다.

```kotlin
fun foo(x: Int) = println(x)
>>> val kFunction = ::foo
>>> kFunction.call(42)
42
```

- ::foo 식의 값 타입은 리플렉션 API에 있는 KFunction클래스의 인스턴스이며 이 함수참조가 가리키는 함수를 호출하면 [KCallable.call](http://KCallable.call) 메서드를 호출한다.
- 만약 인자가 맞지 않으면 IllegalArgumentException이 날 것이다.
- ::foo의 타입 KFunction1<Int, Unit>에는 파라미터와 반환 값 타입 정보가 들어있다. KFunction1 인터페이스를 통해 함수를 호출하려면 invoke메서드를 사용해야 한다.
- invoke는 정해진 개수의 인자만을 받아들이며 인자 타입은 KFunction1 제네릭 인터페이스의 첫번째 타입 파라미터와 같다.

```kotlin
import kotlin.reflect.KFunction2
fun sum(x:Int, y:Int) = x + y
>>> val kFunction: KFunction2<Int, Int, Int> = : sum
>>> println(kFunction.invoke(1, 2) + kFunction(3, 4))
10
>>> kFunction(1)
ERROR : No value passed for parameter p2
```

- kFunction의 invoke 메서드를 호출할 때는 인자 개수나 타입이 맞아 떨어지지 않으면 컴파일이 안 되기 때문에 KFunction의 인자 타입과 반환타입을 모두 다 안다면 invoke메서드를 호출하는게 낫다. call 메서드는 모든 타입의 함수에 적용할 수 있는 일반적인 메서드지만 타입 안정성을 보장해주지는 않는다.
- kProperty의 call 메서드를 호출할 수도 있는데 이는 프로퍼티의 게터를 호출한다.
- 하지만 프로퍼티 인터페이스는 프로퍼티 값을 얻는 더 좋은 방법으로 get 메서드를 제공한다. 이에 접근하려면 프로퍼티가 선언된 방법에 따라 올바른 인터페이스를 사용해야 한다.
- 최상위 프로퍼티는 KProperty0 인터페이스의 인스턴스로 표현되고 인자가 없는 get 메서드가 있다. 멤버프로퍼티는 KProperty1 인스턴스로 표현되고 인자가 한 개인 get 메서드가 들어있다. 멤버 프로퍼티는 어떤 객체에 속해 있는 프로퍼티이므로 멤버 프로퍼티의 값을 가져오려면 get 메서드에게 프로퍼티를 얻고자 하는 객체 인스턴스를 넘겨야 한다.
- 

## 10.2.2 리플렉션을 사용한 객체 직렬화 구현

- 직렬화함수의 기능을 살펴보면 기본적으로 객체의 모든 프로퍼티를 직렬화한다고 볼 수 있다.

```kotlin
private fun StringBuilder.serializeObject(obj: Any) {
    val kClass = obj.javaClass.kotlin // 객체의 KClass를 얻는다.                  
    val properties = kClass.memberProperties // 클래스의 모든 프로퍼티를 얻는다.         
    properties.joinToStringBuilder(
            this, prefix = "{", postfix = "}") { prop ->
        serializeString(prop.name) // 프로퍼티 이름을 얻는다.                     
        append(": ")
        serializePropertyValue(prop.get(obj)) // 프로퍼티 값을 얻는다.         
    }
}
```

- 위 예시는 각 프로퍼티를 차례로 결과는 JSON 형태로 표현되도록 직렬화 한다.
- joinToStringBuilder는 프로퍼티를 콤마로 구분해주고 제이키드 함수인 serializeString은 JSON 명세에 따라 특수문자를 이스케이프해주고, serializePropertyValue는 어떤 값이 원시타입, 문자열, 컬렉션, 중첩된 객체 중 어떤 것인지 판단하고 그에 따라 값을 적절히 직렬화한다.
- 어떤 객체의 클래스에 정의된 모든 프로퍼티를 열거하기 때문에 정확히 각 프로퍼티가 어떤 타입인지 알 수 없다. 따라서 prop 변수의 타입은 KProperty1<Any, *>이며, prop.get(obj) 메서드 호출은 Any 타입의 값을 반환한다. Any클래스의 어떤 프로퍼티가 들어오든 처리한다.
- 수신 객체 타입을 컴파일 시점에 검사할 방법이 없지만, 이 코드에서는 어떤 프로퍼티의 get에 넘기는 객체갑 ㅏ로 그 프로퍼티를 얻어온 객체이기 때문에 항상 프로퍼티 값이 제대로 반환된다.

## 10.2.3 애노테이션을 활용한 직렬화 제어

- JSON 직렬화 과정을 제어하는 과정 중에서 @JsonExclude를 사용하여 특정 필드들을 제외하고 싶을 경우가 있다. 아래 코드는 @JsonExclude로 애노테이션한 프로퍼티를 제외시킨다.

```kotlin
private fun StringBuilder.serializeObject(obj: Any) {
    val kClass = obj.javaClass.kotlin
		val properties = kClass.memberProperties

		properties.joinToStringBuilder(
			this, prefix="{", postfix="}"){
			prop -> serializeString(prop.name)
			append(": ")
			serializePropertyValue(prop.get(obj))
		}
}
```

## 10.2.4 JSON  파싱과 객체 역직렬화

- API는 직렬화와 마찬가지로 함수 하나로 이뤄져 있다.

```kotlin
inline fun<refied T:Any> deserialize(json:String): T
```

- 아래는 위 함수의 사용방법이다.

```kotlin
data class Author(val name: String)
data class Book(val title:String, val author:Author)
>>> val json = """{"title":"Catch-22", "author":{"name":"J.Heller"}}"""
>>> val book = deserialize<Book>(json)
>>> println(book)
Book(title=Catch-22, author=Author(name=J.Heller))
```

- 역직렬화할 객체의 타입을 실체화한 타입 파라미터로 deserialize 함수에 넘겨서 새로운 객체 인스턴스를 얻는다.
- JSON 문자열 입력을 파싱하고, 리플렉션을 사용해 객체의 내부에 접근해서 새로운 객체와 프로퍼티를 생성하는 과정 때문에 역직렬화는 직렬화보다 어렵다.
- 제이키드의 JSON 역직렬화기는 흔히 쓰는 방법을 따라 3단계로 구현돼 있다.
    - 첫번째 단계는 어휘 분석기로 lexer라고 부른다.
    - 두번째는 문법 분석기로 parser라고 부른다.
    - 마지막은 파싱환 결과로 객체를 생성하는 역직렬화 컴포넌트 이다.
- 어휘분석기는 여러 문자로 이루어진 입력 문자열을 토큰 리스트로 변환
- 토큰의 종류는 2가지
    - 문자 토큰은 문자를 표현하며 JSON 문법에서 중요한 의미가 있다.
        - 콤마, 콜론, 중괄호, 각괄호가 문자 토큰에 해당
    - 값 토큰은 문자열, 수, 불리언값, null 상수를 말한다.
- 문법분석기는 토큰의 리스트를 구조화된 표현으로 변환한다. 제이키드에서 파서는 JSON의 상위 구조를 이해하고 토큰을 JSON에서 지원하는 의미 단위로 변환하는 일을 한다. 의미 단위로는 키와 값쌍과 배열이 이에 해당.

## 10.2.5 최종 역직렬화 단계 : callBy(), 리플렉션을 사용해 객체 만들기

- 최종 결과인 객체 인스턴스를 생성하고 생성자 파라미터 정보를 캐시하는 classInfo 클래스가 있는데 이는 앞서 나온 ObjectSeed 안에서 쓰인다.
- 앞서 나온 KCallable.call은 디폴트 파라미터 값을 지원하지 않는다는 한계가 있고, 제이키드에서 역직렬화 시 생성해야 하는 객체에 디폴트 생성자 파라미터 값이 있고 제이키드가 그런 디폴트 값을 사용할 수 없다면 JSON에서 관련 프포티를 꼭 지정하지 않아도 된다. 따라서 여기는 디폴트 파라미터 값을 지원하는 KCallable.callBy를 사용해야 한다.

```kotlin
interface KCallable<out R>{
		fun callBy(args:Map<KParameter, Any?) : R
}
```

- 이 메서드는 파라미터와 이에 해당하는 값을 연결해주는 맵을 인자로 받는다. 인자로 받은 맵에서 파라미터를 찾을 수 없는데, 파라미터 디폴트 값이 정의돼 있다면 그 디폴트 값을 사용한다.
- 여기서 타입이 제대로 처리하기 위해 args 맵에 들어있는 각 값의 타입이 생성자의 파라미터 아비과 일치해야 한다. 타입 변환 에는 커스텀 직렬화에 사용했던 ValueSerializer 인스턴스를 똑같이 사용한다. 프로퍼티에 @CustomSerializer 애노테이션이 없다면 프로퍼티 타입에 따라 표준 구현을 불러와 사용

```kotlin
fun serializerForType(type: Type) : ValueSerializer<out:Any?>? = 
		when(type){
			Byte::class.java -> ByteSerializer
			Int::class.java -> IntSerializer
			Boolean::class.java -> BooleanSerializer
			...
			else -> null		
}
```

- 아래 예시와 같이 타입별로 ValueSerializer구현은 필요한 타입 검사나 변환을 수행한다.

```kotlin
object BooelanSerialzer : ValueSerializer<Boolean>{

		override fun fromJsonValue(jsonValue : Any?): Boolean{
				if(jsonValue !is Boolean) throw JKidException("Boolean expected")
				return jsonValue
		}
		
		override fun toJsonValue(value: Boolean) = value
}
```

- callBy 메서드에 생성자 파라미터와 그 값을 연결해주는 맵을 넘기면 객체의 주 생성자를 호출 할 수 있다. 위 메커니즘을 통해 생성자를 호출할 때 사용하는 맵에 들어가는 값이 생성자 파라미터 정의의 타입과 일치하게 만들고 이제 API를 호출한다.
- ClassInfoCache는 리플렉션 연산 비용을 줄이는 클래스이다. 직렬화와 역직렬화에 사용하는 애노테이션들이 파라미터가 아니라 프로퍼티에 적용된다는 사실을 기억할 것.
- 객체를 역직렬화할 때는 반대로 프로퍼티가 아니라 생성자 파라미터를 다뤄야 한다, 즉, 애노테이션을 꺼내려면 파라미터에 해당하는 프로퍼티를 찾아야 한다. 맵을 하나씩 읽고 검색을 하면 성능이 저하되니 클래스 별로 한번만 검색을 수행하고 검색 결과를 캐시에 넣어준다.

```kotlin
class ClassInfoCache{
		private val cacheData = mutableMapOf<KClass<*>, ClassInfo<*>>()
		@Suppress("UNCHECKED_CAST")
		operator fun<T : Any> get(cls:KClass<T>): ClassInfo<T>=
			cacheData.getOrPut(cls){ ClassInfo(cls) } as ClassInfo<T>
}
```

- 맵에 값을 저장할 때는 타입 정보가 사라지지만, 맵에서 돌려 받는 값의 타입은 ClassInfo<T>의 타입 인자가 항상 올바른 값이 되게 get메서드 구현이 보장된다.
- getOrPut을 사용하는 부분을 보면 cls에 대한 항목이 cacheData맵에 있다면 그 항목을 반환한다. 그 항목이 없으면 전달 받은 람다를 호출해서 키에 대한 값을 계산하고 계산한 결과 값을 맵에 저장한 다음에 반환한다.
- ClassInfo 클래스는 대상 클래스의 새 인스턴스를 만들고 필요한 정보를 캐시에 둔다.

```kotlin
class ClassInfo<T:Any>(cls:KClass<T>){

		private val constructor = cls.primaryConstructor!!
		private val jsonNameToParam = hashMapOf<String, KParameter>()
		private val paramToSerializer = 
				hashMapOf<String, Class<out Any>?>()
		init{
				constructor.parameters.forEach{ cacheDataForParameter(cls, it) }
		}
		fun getConstructorParameter(propertyName: String) : KParameter = 
			jsonNameToParam[propertyName]!!!
		fun deserializeConstructorArgument(
				param:KParameter, value:Any?):Any?{
			val serializer = paramToSerializer[param]
			if(serializer != null) return serializer.fromJSsonValue(value)
			validateArgumentType(param, value)
			return value
		}

		fun createInstance(arguments:Map<KParameter, Any?>):T{
				ensureAllParametersPresent(arguments)
				return constructor.callBy(arguments)
		}

		...
}
```

- 초기화시 이 코드는 각 생성자 파라미터에 해당하는 프로퍼티를 찾아서 애노테이션을 가져온다.
- 데이터는 세 가지 맵에 저장한다.
    - jsonNameToParam은 JSON 파일의 각 키에 해당하는 파라미터를 저장
    - jsonNameToDeserializedClass는 @DeserializeInterface애노테이션 인자로 지정한 클래스를 저장
    - ClassInfo는 프로퍼티 이름으로 생성자 파라미터를 제공할 수 있으며 생성자를 호출하는 코드는 그 파라미터를 파라미터와 생성자 인자를 연결하는 맵의 키로 사용.
- cachDataForParameter, validateArgumentType, ensureAllParametersPresent 함수는 이 클래스에 정의된 비공개 함수로 코드는 직접 찾아볼 것
- 필수 파라미터가 모두 있는지 검증하는 ensureAllParametersPresent함수는 다음과 같다.

```kotlin
private fun ensureAllParametersPresent(arguments:Map<KParameter, Any?){
		for(param in constructor.parameters){
				if(arguments[param] == null && !param.isOptional && !param.type.isMarkedNullable){
						throw JKidException("Missing value for parameter ${param.name}")
				}
		}
}
```

- 파라미터에 디폴트 값이 있다면 param.isOptional이 true이다. 따라서 그런 프라미터에 대한 인자가 인자 맵에 없어도 아무 문제가 없으나 파라미터가 널이 될 수 잇는 값이라면 디폴트 파라미터 값으로 null을 사용한다. 그 두 경우가 모두 아니면 예외를 발생시킨다.
- 리플렉션 캐시를 사용하면 역직렬화 과정을 제어하는 애노테이션을 찾응 과정을 JSON 데이터에서 발견한 모든 프로퍼티에 대해 반복할 필요 없이 프로퍼티 이름별로 단 한번만 수행할 수 있다.