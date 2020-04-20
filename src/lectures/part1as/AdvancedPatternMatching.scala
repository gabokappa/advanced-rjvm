package lectures.part1as

object AdvancedPatternMatching extends App {

  val numbers = List(1)
  val description = numbers match {
    case head :: Nil => println(s"the only element is $head.")
    case _ =>
  }

  /*
  - constants
  - wildcards
  -tuples
  -some specific syntax sugar
   */

  class Person(val name: String, val age: Int)

  // define a companion
  object Person {
    def unapply(person: Person): Option[(String, Int)] = Some((person.name, person.age))
  }

  val bob = new Person("Bob", 25)
  val greeting = bob match {
    case Person(n, a) => s"Hi, my name is $n and I'm $a years old"
  }

  /* Although Person not a case class because we created a companion object with a method that returns a Option(String, Int) we can pattern match
  because of the unapply method.

  When the runtime happens it goes through different steps. As it reads the match case Person(n, a) it looks for a method unapply in an object called person

   */




}
