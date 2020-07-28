package lectures.part4Implicits

object ImplicitsIntro extends App {

  val pair = "Daniel" -> "555" // The first operand is the instance to which the method is and the right-hand-side is the argument. But there isn't an arrow method for String or INT
  // the arrow (cmd + click on it) Creates an ArrowAssoc instance out of "Daniel" and it calls the arrow method on it. Which you can see has an implicit and converts it into a tuple
  val intPair = 1 -> 2

  case class Person(name: String) {
    def greet = s"Hi, my name is $name!"
  }

  implicit def fromStringToPerson(str: String): Person = Person(str)
  // the implicit keyword allows the below to work, as in call the method greet on "Peter" even though it is of a Person class.
  // It looks for ANYTHING that can turn a string into the greet method. Objects, values or methods. It find the Person class, so it converts the String into a Person instance to call greet on it.

  println("Peter".greet) // the compiler matches this code and re-writes as println(fromStringToPerson("Peter).greet)
  // the compiler assumes that there is only one version of the code that matches. If more than one multiple it breaks

  // TODO if the below is enabled it breaks the above implicit as there is a conflicting implicit

//  class A {
//    def greet: Int = 2
//  }
//
//  implicit def fromStringToA(str: String): A = new A
//

// implicit parametrs!
  def increment(x: Int)(implicit amount: Int): Int = x + amount
  implicit val defaultAmount = 10

  // can call increment with just one value and the defaultAmount will be passed through as an implicit as the second paramater "amount"
  increment(2)
  // THIS IS NOT AS DEFAULT ARG. The implicit is found by the compiler based on its search scope

}
