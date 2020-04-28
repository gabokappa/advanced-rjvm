package lectures.part2afp

object PartialFunctions extends App {

  val aFunction = (x: Int) => x + 1 // Function1[Int, Int] or the sugared type Int => Int. Any Int can be passed to this function and it will return a result

  // below we restrict the type of Integers that can go in but this is very clunky way of doing it

  val aFussyFunction = (x: Int) =>
    if (x == 1) 42
  else if (x == 2) 56
  else if (x == 5) 999
  else throw new FunctionNotApplicableException

  class FunctionNotApplicableException extends RuntimeException

  // less clunky version ... this will throw a match error if it throw a matching exception

  val aNicerFussyFunction = (x: Int) => x match {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  }

  // anicerFussyFunction is an implementation from the domain { 1, 2, 5 } => Int this is a partial function from Int to Int (the above) as { 1,2 ,5) is a subset of the Int

  // a shorthand notation for writing a partial function is below. Is called a partial function literal and can only be assigned to a partial function. the above is a proper function and cannot be assigned to a partial function as it is a full function

  val aPartialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  } // the section in between the curly braces above is called a partial function value and equivalent to the section above

// partial function can be called and used like any value

  println(aPartialFunction(2)) // partial functions are based on pattern matching.

  // partial Function utilities

  println(aPartialFunction.isDefinedAt(67)) // this can test whether my partial function is applicable to the argument 67. Can it be run with 67. This returns false

  // partial functions can be lifted to total functions returning options

  val lifted = aPartialFunction.lift // this will turn Int => Int to a total function from Int => Option[Int]
  println(lifted(2)) // returns Some(56) as case matcher above
  print(lifted(98)) // returns None

  // can chain partial functions

  val pfChain = aPartialFunction.orElse[Int, Int] {
    case 45 => 67
  } // or else takes another partial function as an element which is case 45 => 67

  println(s"\nlook at this ${pfChain(45)}")

  val aTotalFunction: Int => Int = {
    case 1 => 99
  }

  // partial functions are a subtype of total function and can be supplied as an element in the above.
  // a side effect is that HOFs accept partial functions as well.

  val aMappedList = List(1,2,3).map {
    case 1 => 42
    case 2 => 78
    case 3 => 1000
  } // this is an acceptable expression but if there isn't a case covered it will crash.

  println(aMappedList)

  /*
  Exercises

  1- construct a PF instance yourself (anonymous class)
  2 - chatbot as a partial function (PF)
   */

  scala.io.Source.stdin.getLines().foreach(line => println("you said: " + line))

  /// partial functions are things that can take as parameters a subset of a given domain
  // oartial function literals that run pn patter matching
    // utilities for PFs are isDefinedAt, lift and orElse



}
