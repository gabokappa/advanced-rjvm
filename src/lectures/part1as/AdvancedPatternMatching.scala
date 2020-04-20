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
    def unapply(person: Person): Option[(String, Int)] =
      if (person.age < 21) None
      else Some((person.name, person.age))

     def unapply(age: Int): Option[String] =
       Some(if (age < 21) "minor" else "major")

  }

  val bob = new Person("Bob", 25)
  val greeting = bob match {
    case Person(n, a) => s"Hi, my name is $n and I'm $a years old"
  }

  println(greeting)

  /* Although Person not a case class because we created a companion object with a method that returns a Option(String, Int) we can pattern match
  because of the unapply method.

  When the runtime happens it goes through different steps. As it reads the match case Person(n, a) it looks for a method unapply in an object called person.
  If unapply returns something else this would work for example if unapply was

  object Person {
    def unapply(person: Person): Option[(String, Int)] =
    if (person.age < 26) None
    else Some((person.name, person.age))
  }

  The above will cause a pattern match error. The class doesn't even have to have the same name as the object, as long as the pattern match is done against the objects
  name.
   */


  val legalStatus = bob.age match {
    case Person(status) => s"Legal age is $status"
  }

  // the above works as a pattern match because bob.age matches the other unapply method in the Person object that takes Int as an argument and it returns a Some with a string

  println(legalStatus)

  /*
  Exercise.
   */

  val n: Int = 45
  val mathProperty = n match {
    case x if x < 10 => "single digit"
    case x if x % 2 == 0 => "an even number"
    case _ => "no property"
  }



}
