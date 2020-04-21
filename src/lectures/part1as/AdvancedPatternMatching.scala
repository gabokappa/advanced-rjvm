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


  // infix patterns

  case class Or[A, B](a: A, b: B) // this is similar to Either. Being a case class it has a companion object with its method unapply
  val either = Or(2, "two")

  val humanDescription = either match {
    case number Or string => s"$number another way of doing it with $string" // This the infix way and is same as case matcher below
    case Or(number, string) => s"$number and $string"
  }
// the infix only makes sense when you have two parameters so you can do the above
  println(humanDescription)

 // decomposing sequences

  val vararg = numbers match {
    case List(1, _*) => "starting with 1" // this is pattern matching for a list that starts with 1 and could have many other elements
      // the original list in numbers is List(1). But above is pattenr matching against that whole list.
  }

  // unapply sequence

  abstract class MyList[+A] {
    def head: A = ???
    def tail: MyList[A] = ???
  }
  case object Empty extends MyList[Nothing]
  case class Cons[+A](override val head: A, override val tail: MyList[A]) extends MyList[A]

  // need to define an unapply sequence method on an object

  object MyList {
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
      if (list == Empty) Some(Seq.empty)
      else unapplySeq(list.tail).map(list.head +: _)
    // calling this recursively in the else, you need to map and addthe head because tail returns a MyList and you need an Option of Seq A as what needs to be returned.
    // it turrns MyList[A] into Option[Seq[A]]
  }

  // whenever I instatiate MyList it still has access to the list mentioned in the previous example

  val myList: MyList[Int] = Cons(1, Cons(2, Cons(3, Empty)))
  val decomposed = myList match {
    case MyList(1, 2, _*) => "staring with 1, 2"
    case _ => "something else"
  }

  // in order to do the above type pattern matching, the object's unapplySeq method needs to return an Option of a sequence as is defined by MyList's unapplySeq method.

  // The first pattern in the case match MyList has to have an unapply or unapplySeq method. The compiler expects an unapply seq as I put down MyList(1, 2, _*) - with the wildcard at the end
  // The compiler finds the unapply method for the MyList object and knows it takes a MyList[A] in our case MyList[Int] and knows it turns into an Option[Seq[A]] - which is what we wrote
  // so that match at runtime the values in the pattern matched MyLost(1, 2, _*) are going to get matched on the sequenced returned by the unapplyseq passing in the Cons(1, Cons( 2, Cons(3, Empty)))

  println(decomposed)


  // custom return types for unapply - the return type you use for unapply o unapplySeq(as above) doesn't have to be an option it just needs to have two defined methods

  // these are: isEmpty: Boolean, get: something

  abstract class Wrapper[T] {
    def isEmpty: Boolean
    def get: T
  }

  object PersonWrapper {
    def unapply(person: Person): Wrapper[String] = new Wrapper[String] {
      def isEmpty = false
      def get: String = person.name
    }
  }

// having defined PersonWrapper with that unapply method I can use the person in pattern matching

println(bob match {
  case PersonWrapper(n) => s"This person's name is $n"
  case _ => "An Alien"
})


}
