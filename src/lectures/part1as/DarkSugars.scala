package lectures.part1as

import scala.util.Try

object DarkSugars extends App {

  // syntax sugar #1: methods with single param

  def singleArg(arg: Int): String = s"$arg little dogs..."

  val desc = singleArg {
    // complex code block
    42
  }
  println(desc)

  val aTryInstance = Try { // this style of code is used a lot in practice because it is simlar to jave try
    None
  }
  println(List(1, 2, 3).map { x =>
    x + 1 + x + 1
  })

  // syntax sugar #2 - instances of traits with a single method can be reduced to lambdas these are the Single Abstract Method

  trait Action {
    def act(x: Int): Int
  }

  val anInstance: Action = new Action {
    override def act(x: Int): Int = x + 1
  }

  // the above would be the same as

  val anInstance2: Action = (x: Int) => x + 2 // magic here, the compiler realises this is a function 1 type that can be attributed to the act function in the trait
  // lambda abstract type of conversion. They are used a lot in concurrency is an example of instatiating traits with Runnables.
      // Runnables are instances of a trait (or java interface) that can be passed on to threads

  val aThread = new Thread(new Runnable {
    override def run(): Unit = println("I'm running")
  }) // this is a valid thread creations

  // below is another valid thread creation using syntactic sugar

  val aSweeterThread = new Thread(() => println("sweet syntax for runnable")) // this is a lambda that takes no parameters because the run method above doesn't take params
  // this instantiates a runnable

  // this pattern works with classes that have some members implemented but only have one method unimplemented

  abstract class AnAbstractType {
    def implemented: Int = 23
    def f(a: Int): Unit
  }

  // the below is actually implemented the unapplied method above def f.
  val anAbstractInstance: AnAbstractType = (a: Int) => println("sweet")


  // syntax sugar #3 the :: and #:: methods

  val prependedList = 2 :: List(3, 4)
  // this gets re-written as List(3,4).::(2).
  // scala spec: the last character decides the associativity of the method. So if it ends in a column it is a right associated. if it is not it is left associated.
1 :: 2 :: 3 :: List(4, 5)
  List(4,5).::(3).::(2).::(1)

  // #:: is the prepend method for streams

  class MyStream[T] {
    def -->:(value: T): MyStream[T] = this // can add an actual implementation here
  }

  val myStream = 1 -->: 2 -->: 3 -->: new MyStream[Int]

  // syntax sugar #4: multi-word method naming

  class TeenWolf(name: String) {
    def `and then said`(gossip: String) = println(s"$name said $gossip") // this is a method composed of three words using back ticks
  }

  val mike = new TeenWolf("Mike")
  mike `and then said` "Scala is so sweet"

  // syntax sugar #5: infix types

  class Composite[A, B]
  val composite: Composite[Int, String] = ???

  // or We can also infix generic types

  val composite2: Int Composite String = ???

  class -->[A, B]
  val towards: Int --> String = ???
  // the compilers composes the --> Int is A type and String is B type
  // type names are permissive type names


  // # syntax sugar update method much like apply()

  val anArray = Array(1, 2, 3)
  anArray(2) = 7 // this gets rewritten to anArray.update(2, 7) first the index of the array element and the second param what we want to change.
  // this is used a lot in mutable collections.
  // remember apply() AND update()!

  // syntax sugar#7 setters for mutable containers

  class Mutable {
    private var internalMember: Int = 0 // private for OO encapsulation
    def member = internalMember // this is the getter
    def member_=(value: Int): Unit =
      internalMember = value // this is the setter. In this example the setter and getter are in close relationship
  }
  // create a mutable container

  val aMutableContainer = new Mutable
  aMutableContainer.member = 42 // the scala compiler re-writes as aMutableContainer.member_= (42). This only happens because I set the the name of the getter and setter as member

}
