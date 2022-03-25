# Kotlin Ch.9.  제네릭스

## 9.1 제네릭 타입 파라미터

9.1.1 제네릭 함수와 프로퍼티

```kotlin
val authors = listOf("John", "Sveltna")

val authors: List<String> = emptyList();
val authors = emptyList<String>();
```

```kotlin
public fun <T> List<T>.slice(indices: IntRange): List<T>

val <T> List<T>.penultimate: T
	get() = this[size - 2]
```

```kotlin
fun <T : Number> List<T>.sum(): T // Kotlin
<T extend Number> T sum(List<T> list) // Java
```

```kotlin
fun <T: Comparable<T>> max(first: T, second: T): T {
	// 코틀린 컴파일러에 의해 first.compareTo(second) > 0으로 변한다.
	return if (first > second) first else second
}

println(max("kotlin", "java")) // kotlin
println(max("kotlin", 42")) // 42의 경우 첫 번째 인자의 타입 정보와 일치하지 않기 때문에 컴파일 에러
```

```kotlin
fun <T> ensureTraillingPeriod(seq: T) where T: CharSequence, T: Appendable {
	if (!seq.endsWith('.')) seq.append('.')
}
```

```kotlin
class A<T> {
	// 타입 파라미터 T에 대해 별도의 상한을 지정하지 않았기 때문에 `Nullable`
	fun process(value: T) {
		value?.hashCode()
	}
}

class B<T: Any> {
	// 타입 파라미터 T에 대해 Any로 상한을 지정하였기 때문에 NonNull
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

9.2.2 실체화한 타입 파라미터를 사용한 함수 선언

9.2.3 실체화한 타입 파라미터로 클래스 참조 대신

9.2.4 실체화한 타입 파라미터의 제약

## 9.3 변성 : 제네릭과 하위 타입

9.3.1 변성이 있는 이유: 인자를 함수에 넘기기

9.3.2 클래스, 타입, 하위 타입

9.3.3 공변성: 하위 타입 관계를 유지

9.3.4 반공변성: 뒤집힌 하위 타입 관계

9.3.5 사용 지점 변성 : 타입이 언급되는 지점에서 변성 지정

9.3.6 스타 프로젝션 : 타입 인자 대신 * 사용