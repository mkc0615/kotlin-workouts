
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

class Chapter5 {

    fun workout(){

        findTheOldest(people)

        println(people.maxByOrNull{ it.age })

        println(people.maxByOrNull(PersonCls::age))

    }
}