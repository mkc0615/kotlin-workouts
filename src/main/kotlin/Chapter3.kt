
// 이것들의 정체는 아래 3장 Function 을 보시오.
var opCount = 0
fun performOperation(){
    opCount++
}
fun reportOperationCount(times : Int = 0){
    var thisRound : Int = times
    while (thisRound>0) {
        performOperation()
        thisRound--
    }
    println("Operation performed $opCount times cumulatively")
}
const val UNIX_LINE_SEPERATOR = "/n"


class Chapter3 {

    // === 3장. 함수의 정의와 호출 === //
    fun chapter3Func(){
        // 자바코드와의 상호작용을 위해 코틀린은 자체 컬렉션을 제공하지 않는다.
        val set = hashSetOf(1, 7, 53); // hashset 정의
        val list = arrayListOf(1, 7, 53); // arraylist 정의
        val map = hashMapOf(1 to "one", 7 to "seven", 53 to "fifty-three") // hashmap 정의
        println("in java : getClass() / in kotlin : "+set.javaClass)

        // BUT 더 많은 기능을 제공한다. 그 예시 중 하나가 바로 다음과 같은 마지막 원소 가져오기 함수
        println(set.maxOrNull())
        println(list.last())

        // joinToString -> 책에서는 요 함수를 차츰 코틀린답게 바꿔나가면서 설명한다.
        // -- 먼저 각 파라미터에 디폴트 값을 더했고
        // -- 그 뒤에는 함수를 Collection<T> 확장함수로 선언하면서 파라미터 중 컬렉션이 사라짐
        fun <T> Collection<T>.newJoinToString (
            separator : String = ", ",
            prefix : String = "",
            postfix : String = "") : String {
            val result = StringBuilder(prefix)
            for((index, element) in this.withIndex()){
                if(index > 0) result.append(separator)
                result.append(element)
            }
            result.append(postfix)
            return result.toString()
        }

        // 우선 자바와 달리 코틀린에서는 함수 호출 시 parameter가 어떤 것에 해당하는지 표기할 수가 있다.
        list.newJoinToString(separator = " ", prefix = " ", postfix = ".")

        // 자바에서는 메서드명은 같으나 파라미터 타입이 다른 메서드들에 대해 오버로딩을 많이 하게 되는데
        // 코틀린에서는 위에 함수처럼 선언시 각 파라미터에 default 값을 지정해주면서 이를 많이 줄일 수 있다.
        // 그리고 그 덕분에 코틀린에서는 함수 호출시 파라미터를 생략해도 호출이 가능하다.
        list.newJoinToString(", ", "", "")
        list.newJoinToString()
        list.newJoinToString("; ")

        // 자바에서는 정적 클래스를 주로 util 함수들과 같이 보관용으로 사용한다.
        // 메서드를 항상 어떤 특정 클래스에 두어야하거나, 비슷한 연산과 역할을 하는 클래스가 여럿 생기기도 마련이다.
        // 코틀린에서는 메서드를 최상위에 위치, 즉 모든 클래스 밖에 선언함으로써 사용하며, 타 패키지에서 필요할 경우
        // import하여 클래스명을 명시할 필요 없이 사용할 수 있다. 프로퍼티도 이와 같이 선언할 수 있으며,
        // 그 예시로 아래 함수는 관련 메서드와 프로퍼티는 이 함수 밖에 선언했다.
        reportOperationCount(5)

        // 상수도 이처럼 최상위에 선언하기 좋다. 대신에 다른 최상위의 프로퍼티들 처럼 getter setter가 발생하는데
        // 상수에게 있어서 이는 부자연스럽다. 그래서 자바에서 public static final과 같은 표현이 const
        UNIX_LINE_SEPERATOR
        // 다만 const 는 원시타입과 String 에만 지정 가능!

        // 이번 챕터 제일 중요한 부분이 바로 확장함수와 확장 프로퍼티!!!
        // 자바 라이브러리를 재작성하지 않고 코틀린이 제공하는 여러 기능 사용할 수 있도록하는 것이 확장 함수
        // 아래는 문자열의 마지막 문자를 가져오는 함수와 그 실행 예시
        // fun String.lastChar():Char = this.get(this.length -1)

        fun String.lastCharFunc():Char = get(length -1)
        println("Kotlin".lastCharFunc())

        // Kotlin이라는 문자열은 수신객체이며 그 객체의 타입은 obviously String
        // 위에 함수는 스트링이라는 클래스에 해당 함수를 추가, 즉 확장시킨 셈이다. 자신을 가리키는 this는 생략이 가능
        // 하지만 이것이 본래 스트링 클래스, 즉 수신객체 클래스의 캡슐화를 깨는 것은 아니다.
        // 스트링 클래스 안에 있는 private, protected 메서드들을 이 확장함수는 여전히 사용할 수가 없다.

        // 물론 확장함수도 선언 후 사용하기 위해서는 아래처럼 import를 해야한다. strings라는 package에 정의했다고 가정했을 경우
        // import strings.lastChar as last 와 같이 가져오며 as 를 통해 따로 이름을 붙여서 쓰는게 중복을 막고 명칭도 짧아져서 쓰기 좋다.
        // char c = StringUtilKt.lastChar("java")와 같이 StringUtil.kt 파일에 정의했다 가정하고 java에서도 사용 가능하다.

        // 확장 함수는 어디까지나 정적 메소드 호출에 대한 문법적인 편의이므로 더 구체적인 타입을 수신 객체 타입으로 지정 가능하다.
        // 위에 함수를 스트링을 가진 컬렉션만을 위한 함수를 만들고 정의하는데 쓸수 있다.
        fun Collection<String>.joinStrings(seperator: String = ", ",
                                           prefix: String ="",
                                           postfix: String="") = newJoinToString(seperator, prefix, postfix)
        println(listOf("one", "two", "eight").joinStrings()) // 이같은 경우 당연히 다른 객체가 들어간 리스트에서 사용 시에는 에러가 발생한다.
        // 그리고 확장함수는 클래스의 일부가 아니기에 그 하위 클래스에서 오버라이딩을 할 수 없다.

        // 위 예시를 조금 변경하여 이번에는 확장 프로퍼티를 정의하고 선언해본다.
        // -> IDE 상으로 String 클래스에 확장 프로퍼티를 더할 수는 없게 되어 있어서 여기서는 주석으로만 표기한다.
        // 확장프로퍼티를 사용할때는 다음과 같이 getter setter 를 명시적으로 사용
        // val String.lastCharFunc : Char
        //      get() = get(length - 1)
        // StringUtilKt.getLastCharProp("java")

        // 이처럼 확장함수를 이용하여 코틀린 라이브러리는 last()나 joinToStrings 와 같이 더 다양한 기능들을 제공하게 된 것
        // 컬렉션 표준 라이브러리의 함수 몇 가지를 바탕으로 코틀린의 다른 특징들을 볼 수 있다.

        // << 가변 인자 함수 : 인자의 개수가 달라질 수 있는 함수 정의 >>
        // 인자의 수가 달라질 수 있는 함수의 경우 다음과 같이 파라미터 앞에 vararg 를 붙여 선언한다.
        // fun arrayOf<T> (vararg values : T) : Array<T>{ ... }
        // 또한 자바에서는 배열을 그대로 넘길 수 있지만, 코틀린에서는 배열을 명시적으로 하나씩 꺼내어 전달해야되는데,
        // *를 앞에 붙여서 전달하면 스프레드 연산자를 이를 해결해준다
        val thisList = arrayOf("asdf", "qwer", "zxcv")
        fun lister(args: Array<String>){
            val list = listOf("args: ", *args)
            println(list)
        }
        lister(thisList)

        // << 중위호출 >>
        // 이를 설명하기 위해서는 mapOf 함수를 사용한다.
        val thisMap = mapOf(1 to "one", 7 to "seven", 53 to "fifty-three") // hashmap 정의
        // 여기서 to는 확장 함수이다. 중위호추른 특별한 방식으로 이와 같이 메서드를 호출하는 방식인데,
        // 수식객체와 유일한 메서드 인자 사이에 띄어쓰기를 하고 메서드 명을 넣는 방법이다. 이와 같은 메서드를 선언할 때는 앞에 infix 를 붙이면 된다
        // to 메서드의 선언 예시 -> infix fun Any.to(other:Any) = Pair(this, other)

        // << 구조분해 선언 >>
        // 구조분해는 배열이나 객체의 속성을 분해서 그 값을 변수에 담을 수 있게 하는 표현식
        // 위 예시의 Pair 내용으로 두 변수를 초기화할 수 있는데,
        // val (number, name) = 1 to "one 처럼 작성하여 다시 number 과 name 으로 분해를 한 것이라 볼 수 있음.
        // 앞서 본 newJoinToString 에서 썼던 withIndex 로 루프를 통해 아래처럼 구조분해도 가능하다.
        for((index, element) in list.withIndex()){
            println("$index: $element")
        }
        // 앞에 mapOf 선언문을 보면 알 수 있듯이 to의 수신객체는 제네릭
        // -> fun <K, V> thisMapOf(vararg values : Pair<K, V>) : Map<K, V>{ ... }
        // 이와 같은 기능들을 통해 코틀린은 일반적인 함수들을 간결하게 호출을 한다! 특별한 문법이 따로 있는 것이 아님!

        // 정규표현식 활용 ( 3중 따옴표 )
        // 자바의 split 과 달리 코틀린은 split 할 파라미터를 Regex, 즉 정규표현식으로 받을 수도 있다.
        println("12.345-6.A".split(".", "-"))
        // 또한 3중 따옴표( """ ) 로 묶은 정규표현식은 이스케이프를 사용할 필요가 없어진다. 아래 경로 파싱 함수를 참고할 것
        fun parsePath(path: String){
            val regex = """(.+)/(.+)\.(.+)""".toRegex()
            val matchResult = regex.matchEntire(path)
            if(matchResult != null){
                val (directory, filename, extension) = matchResult.destructured
                println("Dir: $directory, name:$filename, ext:$extension")
            }
        }
        parsePath("C://home/projects/kotlin/thisFile.kt")
        // 3중 따옴표 안에서는 탭과 같은 들여쓰기를 포함시키고 줄바꿈도 그대로 사용이 가능하다. 들여쓰기의 경우에는 .으로 들여쓰기한 부분을 표현은 해줘야함.
        // 문자열 템플렛도 사용은 가능하지만 이스케이프 사용이 불가하기 때문에 $의 경우는 따로 표기를 해줘야한다.
        val price = """${'$'}99.99"""
        println(price)

        // Refactoring : 로컬 함수와 확장함수로 코드 다듬기
        // 반복되는 부분을 로컬 함수로 줄일 수 있다.
        class User(val id: Int, val name:String, val address: String)
        fun saveUser(user: User){
            fun validate(value: String, fieldName: String){
                if(value.isEmpty()){
                    throw IllegalArgumentException(
                        "Cannot save user ${user.id} : "+
                                "empty $fieldName"
                    )
                }
                validate(user.name, "Name")
                validate(user.address,"Address")
            }
        }
        // validate 라는 로컬함수를 바깥으로 꺼내서 User 클래스의 확장함수로 바꾸어 사용해도 된다.

        // @jvmUtils 를 사용하여 자바에서도 코틀린 코드를 사용할 수 있다.
    }

}