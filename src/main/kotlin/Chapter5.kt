
val people = listOf(PersonCls("Alice", 29), PersonCls("Bob", 31))

data class PersonCls(val name : String, val age : Int)

fun findTheOldest(people : List<PersonCls>){
    var maxAge = 0
    var theOldest : PersonCls? = null

    for(person in people){
        if(person.age > maxAge) {
            maxAge = person.age
            theOldest = person
        }
    }
    println(theOldest)
}

val names = people.joinToString ( separator = " ", transform={p:PersonCls -> p.name} )

fun printMessagesWithPrefix(messages: Collection<String>, prefix: String) {
    messages.forEach {
        println("$prefix $it")
    }
}

val canBeInClub30 = { p:PersonCls -> p.age <= 30 }

val peopleList = listOf(PersonCls("Alice", 29), PersonCls("Bob", 31), PersonCls("Carol", 31))


val numList = listOf(1,2,3,4)

val resultList = numList.asSequence()
.map{ print("map($it) "); it * it}
.filter{ print("filter($it) "); it % 2 == 0 }
// .toList()

val errors = listOf("403 Forbidden", "404 Not Found")

class Chapter5 {

    fun workout(){

        findTheOldest(people)

        println(people.maxByOrNull{ it.age })

        println(people.maxByOrNull(PersonCls::age))

        printMessagesWithPrefix(errors, "Error: ")

        println(resultList)

    }
}