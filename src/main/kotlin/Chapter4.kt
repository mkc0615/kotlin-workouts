import java.awt.Window
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.Serializable

/* 클래스 걔층 정의
* 인터페이스에 프로퍼티 선언이 들어갈 수 있다.
* 기본적으로 final 이며 public 이다.
* 중첩클래스는 기본적으로 내부 클래스가 아니다. 즉 중첩클래스는 외부클래스에 대해 참조가 없다.
* 생성자를 간단하게, 또는 완전하게 작성 가능하다. 프로퍼티도 접근자를 직접 정의할수도 있다.
* data 클래스를 정의하면 컴파일러가 알아서 유용한 메서드를 만들어준다.
* 클래스와 인스턴스를 동시에 선언하는 object 키워드
*/

// 인터페이스는 아래와 같이 선언
interface clickable{
    fun click();
    fun showOff() = println("this is also clickable!")
}

interface focusable {
    fun setfocus(b: Boolean) = println("I ${if (b) "got" else "lost"} focus")
    fun showOff() = println("this is clickable")
}

// 클래스는 아래와 같이 선언, 인터페이스는 아래와 같이 사용
// 하지만 자바와 달리 override를 꼭 사용해야한다. -> 실수로 상위에 다른 메서드를 오버라이드 하는 경우를 방지
// 위에 작성한것처럼 인터페이스 내의 메서드도 default 구현을 할 수 있다.
// 하지만 자바와 함께 사용할때는 코틀린의 디폴트 구현을 사용할 수가 없다!
class Chapter4 : clickable, focusable {

    override fun click() = println("this is clickable")

    // 두 인터페이스를 구현하는데, 겹치는 메서드를 호출할 경우 아래와 같이 메서드를 다시 정의하지 않으면 컴파일 에러가 발생한다.
    override fun showOff() {

        // 자바와 달리 super 메서드를 사용할 때는 아래와 같이 한다.
        super<clickable>.showOff()
        super<focusable>.showOff()
    }
}


// 코틀린에서는 클래스와 메서드가 기본적으로는 final이다.
// 이는 하위 클래스에서 이를 작성자의 의도와 다르게 사용하다가 나중에 예기치 않게 동작이 바뀔 수 있다는 걱정 때문.
// 하지만 이점으로 클래스가 final 이라면 val로 선언된 프로퍼티는 모두 스마트 캐스트에 사용가능하다.
// 어떤 클래스의 상속을 허용하려면 open 변경자를 사용한다
open class RichButton : clickable{ // <- 이 클래스는 열려있음. 상속 가능

    fun disable(){} // <- 기본적으로 final. 오버라이드 불가

    open fun animate(){} // <- 오버라이드 가능

    override fun click(){} // <- 오버라이드를 한번 한 함수는 open 상태이다. 앞에 final을 붙이면 오버라이드 불가
}

// 추상 클래스도 abstract 키워드로 선언할 수 있다. 추상 클래스는 당연히 하위 클래스에서 추상 멤버들을 오버라이드 해야만 인스턴스 생성이 가능하다.
// 그러므로 자연스레 추상 멤버는 모두 open 상태이기에 해당 키워드를 붙일 필요 없음.
abstract class Animated{

    abstract fun animate() // <- 오버라이드 해야됨

    open fun stopAnimating(){} // <- 비추상함수의 경우 open으로 오버라이드 허용 가능

    fun animateTwice(){} // <- 기본적으로는 여기서도 final
}


// 가시성 변경자는 기본적으로 public 이다.
// 코틀린에서는 internal, protected, private, public의 가시성 변경자가 있다.
// internal은 같은 모듈 내에서만 볼 수 있다는 뜻인데, 여기서 모듈을 한번에 컴파일되는 파일의 집합을 칭함.
// protected는 같은 패키지 내라도 접근 불가, 무조건 해당 클래스와 그를 상속한 클래스에서만 볼 수 있다.
// 코틀린에서는 최상위 선언에 대해서 private 가시성이 허용된다.
internal open class TalkativeButton : focusable{
    private fun yell() = println("Hey!")
    protected fun whisper() = println("can we talk over there")
}
/* 아래와 같은 경우 클래스와 메서드 모두에서 에러 발생!
    // internal 타입을 노출 할 수 없음. 해당 클래스를 internal로 바꾸거나, TalkativeButton 이 public 이 되야 가능하다.
    fun TalkativeButton.giveSpeech(){
        yell() -> private 타입 호출 불가
        whisper() -> protected 타입 호출 불가
    }
*/
// 지난주에 봤듯이 확장함수에서는 private과 protected 멤버에 접근 불가하다.
// 가시성 변경자는 컴파일된 자바 바이트코드안에서도 유지되는데, private의 경우 자바의 package-private과 같이 컴파일되며
// internal의 경우 public으로 컴파일이 되어서 코틀린에서는 접근불가가 자바에서는 접근 가능이 되는 경우가 발생하긴 한다.
// 하지만 내부 오버라이드 방지와 internal 클래스를 모듈 내부에서 사용하는 일을 막기 위해 internal의 멤버들의 이름을 모두 mangle 시킨다고 함.


// 코틀린에서는 클래스 안에 클래스를 선언할 경우 이는 기본적으로 중첩 클래스이다.
// 코틀린에서는 기본적으로 명시적으로 요청하지 않는 한, 바깥쪽 클래스 인스턴스에 대해 중첩 클래스가 접근 권한이 없다.
// 책에서는 직렬화를 예시로 아래와 같이 설명함
interface State : Serializable

interface View {
    fun getCurrentState(): State
    fun restoreState(state : State){}
}

// 코틀린에서는 이와 같은 구현이 가능한데 기본적으로 ButtonState 클래스는 중첩 클래스로 외부 클래스의 멤버에 접근이 안되기 때문에
// 상위 Button 클래스는 직렬화가 가능해진다.
open class Button : View {
    override fun getCurrentState(): State = ButtonState()
    override fun restoreState(state: State) {}
    class ButtonState : State {}
}
// 자바에서였다면 클래스 안에 선언된 ButtonState 클래스가 기본적으로 내부 클래스이기 때문에 외부 클래스의 참조가 기본적으로 포함된다.
// serializable가 포함 안된 ButtonState 클래스는 직렬화가 불가해져 전체적으로 에러가 발생한다.

// 바깥 클래스를 참조하는 내부 클래스를 선언하고 싶으면 inner 키워드를 앞에 붙이면 된다. 외부 멤버를 부를 때는 아래와 같이 구현한다.
class Outer{
    var message = "from outer space"
    inner class Inner{
        fun getOuterReference() : String = this@Outer.message // -> 외부 클래스의 message를 호출
    }
}


// sealed 클래스를 통해 해당 클래스를 상속한 클래스의 정의를 제한할 수 있다.
sealed class Expr {
    class Num(val value : Int) : Expr()
    class Sum(val left : Expr, val right : Expr) : Expr()
}
// sealed 클래스의 하위 클래스는 모두 open 타입이다.
fun eval(e : Expr) : Int =
    when (e) {
        is Expr.Num -> e.value
        is Expr.Sum -> eval(e.right) + eval(e.left)
    }
// 위에 Expr 클래스는 private 생성자를 가지므로 클래스 내부에서만 호출 가능하다.


// 코틀린에서는 선언할 때 주 생성자와 부 생성자를 정의한다.
// 주 생성자는 클래스를 초기화 할때 사용하며 클래스 본문 밖에 정의
// 부 생성자는 클래스 본문 안에서 정의

// 생성자 파라미터를 지정하고 그 생성자 파라미터에 의해 초기화되는 프로퍼티를 정의하는 두 가지 목적
class UserEx(val nickname : String) // 괄호 안 부분이 주 생성자

// contructor 키워드는 주 or 부 생성자를 정의할 때 사용.
// init 키워드는 초기화 블록을 시작하는데, 이는 클래스가 인스턴스화 될때 실행될 초기화 코드가 들어간다.
class User constructor(_nickname : String){

    val nickname : String

    init {
        nickname=_nickname
    }
}
// 하지만 프로퍼티 선언에 init을 포함시키고 별다른 annotation 이나 가시성 변경자가 없으면 contructor 키워드도 생략이 가능하다.
class UserFin(_nickname : String){
    val nickname = _nickname
}
// 이걸 위에 UserEx 선언처럼 본문의 val 키워드를 파라미터에 붙여서 파라미터로 프로퍼티를 바로 초기화하고 정의할 수도 있게 된다.
// 함수 때랑 마찬가지로 생성자 프로퍼티에도 디폴트 값을 줄 수 있다.
class UserAct(val nickname:String, val isSubscribed:Boolean = true)

// 클래스의 인스턴스를 만들 때는 생성자를 직접 호출하면된다. 아래는 그 다양한 예시.
val thisPerson = UserAct("thisDude")
val thisPerson2 = UserAct("thisGuy", false)
val thisPerson3 = UserAct("thisOne", isSubscribed = false)


// 기반클래스가 있는 경우에는 주 생성자에서 기반 클래스의 생성자를 호출할 수도 있다.
open class UserBase(val nickname : String){ }
class TwitterUser(nickname : String) : UserBase(nickname){ }
// 만약 클래스 정의할 때 생성자를 정의하지 않았을 경우 컴파일러가 자동으로 파라미터가 없는 생성자를 만들어준다.
// 이런 클래스를 상속하는 하위 클래스는 반드시 이 생성자를 호출해야 한다. 인터페이스는 생성자가 없으니 괄호가 필요 없음.
class RadioButton : Button()

// 외부에서 클래스를 인스턴스화하지 못하게 하고 싶으면 모든 생성자를 private으로 바꿀 수 있다.
class Secretive private constructor(){}

// 부 생성자의 경우 다양하게 인스턴스를 초기화할 수 있도록 해준다.
open class ViewSec {
    constructor(ctx: String){
    }

    constructor(ctx: String, attr: Button){
    }
}
// 클래스 확장하면서 부생성자를 똑같이 정의 가능한데 super 키워드를 통해 대응하는 상위 클래스 생성자를 호출한다.
class MyButton : ViewSec {
    constructor(ctx : String)
    : super(ctx){

    }

    // 다음과 같이 위 생성자를 this 키워드를 통해 자신의 다른 생성자를 호출할 수도 있다.
    /*
        constructor(ctx : String) : this(ctx, myStyle)
    */

    constructor(ctx : String, attr : Button)
    :super(ctx, attr){
    }
}

// 프로퍼티 : 다른 곳에 저장된 값을 가져와 저장 & 커스텀 접근자를 통해 계산 후 저장하는 방법이 있다.
interface UserInterface {
    val nickname : String
}

class PrivateUser (override val nickname: String) : UserInterface // 주 생성자의 프로퍼티 사용하여 값을 저장

class SubscribingUser(val email : String) : UserInterface {
    override val nickname : String
    get() = email.substringBefore('@') // 커스텀 getter를 사용하여 값을 계산
}
/*
class MetaUser(val accountId: Int) : UserInterface {
    override val nickname = getMetaName(accountId) // 초기화 식이라고 가정
}
*/

// 인터페이스에 getter setter가 있는 프로퍼티를 선언하는 것도 가능하지만, 인터페이스이므로 뒷받침하는 필드의 참조는 불가하다.
interface userEmailInterface{
    val email : String
    val nicknameEml : String
        get() = email.substringBefore('@')
}

// 위 두 가지 프로퍼티 정의 방법을 통해 값을 저장하면서 로직을 수행하는 프로퍼티 정의의 예시
class UserList(val name :  String){
    var address : String = "unspecified"
        set(value: String){
            println("""
                Address was changed for $name:
                "$field" -> "$value".""".trimIndent()) // 여기서 field를 통해 setter가 뒷받침하는 필드에 접근 -> getter였으면 읽기만 가능
        field = value
        }
}

// 접근자를 변경하고 싶을 때는 get set 앞에 가시상 변경자를 추가하면 됨
class LengthCounter{
    var counter : Int = 0
        private set
    fun addWord(word : String){
        counter += word.length
    }
}



// 모든 클래스가 정의해야 하는 메소드 몇 가지
class Client(val name : String, val postalCode : Int){


    // toString -> 보기 좋은 형식으로 오버라이드
    override fun toString() = "Client(name=$name, postalCode=$postalCode)"

    // equals
    override fun equals(other: Any?): Boolean{
        if(other == null || other !is Client)
            return false
        return name == other.name && postalCode == other.postalCode
    }

    // hashcode
    override fun hashCode() : Int = name.hashCode() * 31 + postalCode

    // copy
    fun copy(name:String = this.name,
    postalCode: Int = this.postalCode) = Client(name, postalCode)
}
// 위 메서드를 data 클래스로 선언하는 것만으로 자동 생성된다.
// 추가적으로 메서드를 복사하고 일부 프로퍼티를 변경할 수 있게 해주는 copy 메서드도 여기 포함
data class ClientData(val name : String, val postalCode: Int)



// 상속에서 발생하는 문제를 해결하기 위해 클래스들이 기본적으로 final 로 지정되는데 종종 상속을 허용하지 않는 클래스에
// 새로운 동작을 추가해야할 때 데코레이터 패턴을 사용한다. 이는 기존 상속 안되는 클래스와 같은 인터페이스를 가진 상속을 허용하는 데코레이터 클래스를 만들고
// 기존 클래스를 데코레이터 내부에 필드로 유지하는 것이다. 원래는 많은 준비코드가 필요하지만 코틀린에서는 이를 by 키워드로 해당 인터페이스에 대한
// 구현을 다른 객체에게 위임 중이라고 명시할수가 있다.
class CountingSet<T>(
    val innerSet : MutableCollection<T> = HashSet<T>()
) : MutableCollection<T> by innerSet {

    var objectsAdded = 0
    override fun add(element : T) : Boolean {
        objectsAdded++
        return innerSet.add(element)
    }

    override fun addAll(c : Collection<T>) : Boolean {
        objectsAdded += c.size
        return innerSet.addAll(c)
    }
}

val cset = CountingSet<Int>()


//* 싱글턴 -> 인스턴스 하나만 사용하는 클래스
// object 키워드로 클래스 정의와 인스턴스 생성, 변수 저장까지 모두 수행한다.
// 객체 선언 안에 프로퍼티, 메서드, 초기화 블록 등이 모두 들어갈 수 있지만 생성자는 들어갈 수 없음. -> 싱글턴은 선언문 위치에서 즉시 만들어지기 때문에
object Payroll{
    val allEmployees = arrayListOf<User>()
    fun calculateSalary(){
        for(user in allEmployees){
            println("add calculations here")
        }
    }
}
// 객체 선언시 클래스나 인터페이스 선언도 가능하다.
// 클래스 안에 객체를 선언할 수도 있는데 이 경우도 인스턴스는 하나이다.

//* 동반객체
// 클래스 안에 객체 중에 companion 키워드를 붙이면 이를 동반 객체로 만들수 있는데,
// 이는 private 멤버와 팩토리 패턴을 부르고 구현하기 좋다. 위에 예제를 이에 맞게 변경한 것이 예시.
class UserComp private constructor(val nickname : String) {
    companion object {
        fun newSubscribingUser(email : String) = UserAct(email.substringBefore("@"))

        /*
        fun newMetaUser(accountId: Int) = User(getMetaName(accountId))
        */
    }
}

// 동반 객체를 일반 클래스처럼 이름을 붙이거나, 인터페이스를 상속하거나, 동반 객체 안에 확장함수와 프로퍼티를 정의할 수 있다.
// 따로 이름을 정하지 않을 경우 디폴트로 Companion으로 정해진다.
class Person(val name : String) { // 객체 to json으로 만드는 경우의 예시
    companion object Loader {
        fun fromJSON(jsonText : String) : UserAct = UserAct("john") // 예시
    }
}

// json to 객체 예시에는 인터페이스를 사용하여 설명.
interface JSONFactory<T>{
    fun fromJSON(jsonText : String) : T
}

class PersonWithItf(val name : String) {
    companion object : JSONFactory<Person> {
        override fun fromJSON(jsonText: String): Person = Person("jane")
    }
}

// 확장함수를 추가할수도 있다.
fun Person.Loader.newIntFunction(json: String) : Int {
    return 0;
}
val p = Person.newIntFunction("anyJson")

//* 무명 내부 클래스도 object 키워드로 작성한다.
/*
window.addMouseListener (
    object : MouseAdapter() {
        override fun mouseClicked(e: MouseEvent) {}

        override fun mouseEntered(e: MouseEvent?) {}
    }
)
*/
// 앞에 'val listener = ' 등으로 이름을 붙일 수도 있다.

// 자바에서처럼 객체 식 안의 코드는 그 식이 포함된 함수의 변수에 접근이 가능하다.
fun countClicks(window : Window){
    var clickCount = 0
    window.addMouseListener(object : MouseAdapter(){
        override fun mouseClicked(e : MouseEvent) {
            clickCount ++
        }
    })
}
