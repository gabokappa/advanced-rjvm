package lectures.part2afp

object LazyEvaluation extends App {

  // lazy delays the value of x. Lazy values are evaluated once, but only when they are used for the first time. only evaluated on a needs basis
  // that is why this doesn't crash. if you remove the word lazy this crashes the programme
  lazy val x: Int = throw new RuntimeException

  // but once evaluated it the variable will stay assigned to that name

  lazy val y: Int =  {
    println("hello")
    42
  }
  println(s"with side effects $y")
  println(s"print again without the side effect 'hello' above as already evaluated $y") // they only evalaute once when used the first time

  // example of implications
  def sideEffectCondition: Boolean = {
    println("Boo")
    true
  }

  def simpleCondition: Boolean = false

  lazy val lazyCondition = sideEffectCondition
println(if (simpleCondition && lazyCondition) "yes" else "no") // side effect "Boo" not printed out because lazyCondition not evaluated unless it is needed, which it doesnt because the first term is false
  println(if (lazyCondition && simpleCondition) "yes" else "no") // this one does

  // in conjunction with call by name

  def byNameMethod(n: => Int): Int = n + n + n + 1
  def retrieveMagicValue ={
    println("waiting")
    Thread.sleep(1000)
    42
  }

  println(byNameMethod(retrieveMagicValue))

  // use lazy vals instead to avoid the side effects and long evaluation, so see below the subtle differences especially in the byNameMethod.
  // this is a CALL BY NEED technique below


  def byNameMethod2(n: => Int): Int = {
    // CALL BY NEED is below
    lazy val t = n // only evaluated once
    t + t + t + 1
  }
  def retrieveMagicValue2 ={
    println("waiting")
    Thread.sleep(1000)
    42
  }

  println(byNameMethod2(retrieveMagicValue2))

  // filtering with lazy vals

  def lessThan30(i: Int): Boolean = {
    println(s"$i is less than 30?)")
    i < 30
  }
  def greaterThan20(i: Int): Boolean = {
    println(s"$i is greater than 20?)")
    i > 20
  }

  val numbers = List(1, 25, 40, 5, 23)
  val lt30 = numbers.filter(lessThan30) // lesThan30 here converts to a function value // this should return the list with all numbers less than 30
  val gt20 = lt30.filter(greaterThan20)
  println(gt20)

  val lt30Lazy = numbers.withFilter(lessThan30) // withFilter uses lazy values under the hood. it i a function oon collection
  val gt20Lazy = lt30Lazy.withFilter(greaterThan20)
  println
  println(gt20Lazy) // no side effect applies, the filtering doesn't takes place, the collection is retunred
  gt20Lazy.foreach(println) // this will force printing take place. side effects and predicate are checked on a by need basis

  // for-comprehensions use withFilter with guards

  for {
    a <- List(1,2,3) if a % 2 == 0 // if guards use lazy vals
  } yield a + 1
  // same as
  List(1,2,3).withFilter(_ % 2 == 0).map(_ + 1) // wilFilter turns it into a filter monadic, map turns it back into collection, the original list representation to give us a // List[Int]

  /*
  Exercise: implement a lazily evaluated, singly linked STREAM of elements.

  MyStream.from(1)(x => x +1) = stream of natural numbers (potentially infinite)

  naturals = MyStream.from(1)(x => x +1) = stream of natural numbers (potentially infinite)

  naturals.take(100) // lazily evaluted stream of the first 100 naturals (finite stream)
  natural.foreach(println)  will crash
   */

  abstract class MyStream[+A] {
    def isEmpty: Boolean
    def head: A
    def tail: MyStream[A]

    def #::[B >: A](element: B): MyStream[B] // prepend operator
    def ++[B >: A](anotherStream: MyStream[B]): MyStream[B] // concatenate two streams

    def foreach(f: A => Unit): Unit
    def map[B](f: A => B): MyStream[B]
    def flatMap[B](f: A => MyStream[B]): MyStream[B]
    def filter(predicate: A => Boolean): MyStream[A]

    def take(n: Int): MyStream[A] // takes the first n elements out of this stream. A finite stream
    def takeAsList(n: Int): List[A]
  }

  object MyStream {
    def from[A](start: A)(generator: A => A): MyStream[A] = ???
  }








}
