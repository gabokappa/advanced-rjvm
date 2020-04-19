package lectures.part1as

object Recap extends App {

  val aCondition: Boolean = false
  val aConditionedVal = if (aCondition) 42 else 65

  // know instructions vs expressions
  // instructions are more part of imperative language

  // In Scala we build expressions on top of other expressions a characteristic of functional languages

  // the below has an infered type
  val aCodeBlock = {
    if (aCondition) 54
    56 // the value of this code block is the value of it's last expression so in this case is 56
  }



  // Unit only do side effects, they are equivalent to void. They don't return anything meaningful, but good for side effects

  val theUnit = println("hello scala")

  // functions
  def aFunction(x: Int): Int = x + 1

  // recursion: stack and tail

  // tail recursive function, don't use additional stack frames when calling things recursively
  def factorial(n: Int, acc: Int): Int =
    if (n <= 0) acc
    else factorial(n -1, n * acc)

  // object-orientation declare classes

  class Animal
  class Dog extends Animal
  val aDog: Animal = new Dog // object orientated polymorphism by subtyping

  // abstract data type
  trait Carnivore {
    def eat(a: Animal): Unit
  }

  class Crocodile extends Animal with Carnivore {
    override def eat(a: Animal): Unit = println("crunch")
  }

  // method notations to used the .notation (dot notation)

  val aCroc = new Crocodile
  aCroc.eat(aDog)
  aCroc eat aDog // natural language infix notation
  1 + 2 == 1.+(2)

  // anonymous classes

  val aCarnivore = new Carnivore { // the compile creates an anonymous class by extending Carnivore overrding the method and assigns a new instance of anonymous class to the variable
    override def eat(a: Animal): Unit = println("roar!")
  }

// generics
  abstract class MyList[+A] // this is covariant

  //singleton objects and companions

  object MyList // this is a apair with the above

  // case class


  case class Person(name: String, age: Int)

  // exceptions and try/catch/finally

 // val throwsException = throw new RuntimeException // the type for this is Nothing as it cannot be substantiated. Nothing is the type of nothingness in Scala

  val aPotentialFailure = try {
    throw new RuntimeException
  } catch {
    case e: Exception => "I caught an exception"
  } finally {
    println("some logs")
  }

// packaging and imports

  // functional programming
  // functions are instances of classes with an apply method

  val incrementer = new Function1[Int, Int] {
    override def apply(v1: Int): Int = v1 + 1
  }

  // these can be called as a function

  incrementer(1)

  // first class syntactic sugar. For lambdas

  val anonymousIncrementer = (x: Int) => x + 1
  println(anonymousIncrementer(4))
println(List(1, 2, 3).map(anonymousIncrementer))
// map , flatMap, filter

  // for-comprehension - this is syntactic sugar for a chain of maps and flatMaps

  val pairs = for {
    num <- List(1,2,3) // could add filters by adding an if guard here.
    cha <- List("a", "b", "c")
  } yield num + "-" + cha // this would do a cross pairing of the two lists for every element in the list
  // a chain of maps and flatmaps
println(pairs)

  // Scala collections: Seqs, Array, Lists, Vectors, Maps, Tuples
  // also did Options and Tyr

  val anOption = Some(2) // is this a Monad?

  // pattern matching

  val x = 2
  val order = x match {
    case 1 => "first"
    case 2 => "second"
    case _ => "Don't care"
  }

  val bob = Person("Bob", 22)
  val greeting = bob match {
    case Person(n, _) => s"Hi, my name is $n"
  }

  // all the patterns

}
