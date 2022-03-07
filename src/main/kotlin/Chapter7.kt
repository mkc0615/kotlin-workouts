class Chapter7 {

    data class Example(val x : Int, val y: Int)

    var ex1 = Example(2,5)
    var ex2 = Example(3,8)


    fun exampleCheck(ex1 : Example, ex2 : Example) {
        println(ex1 == ex2)
    }

}