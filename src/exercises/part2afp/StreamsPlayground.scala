package exercises.part2afp

abstract class MyStream[+A] {

  def isEmpty: Boolean
  def head: A
  def tail: MyStream[A]

  def #::[B >: A](element: B): MyStream[B] // prepend operator
  def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] // concatenate two streams

  def foreach(f: A => Unit): Unit
  def map[B](f: A => B): MyStream[B]
  def flatMap[B](f: A => MyStream[B]): MyStream[B]
  def filter(predicate: A => Boolean): MyStream[A]

  def take(n: Int): MyStream[A] // takes the first n elements out of this stream. A finite stream
  def takeAsList(n: Int): List[A] = take(n).toList()

  /*
  [1 2 3].toList([]) =
  [2 3].toList([1]) =
  [3].toList([2 1]) =
  [].toList([3 2 1]) // at this stage toList calls on the empty list it reverses the accumulator
  = [1 2 3]
   */

  final def toList[B >: A](acc: List[B] = Nil): List[B] =
    if (isEmpty) acc.reverse
    else tail.toList(head :: acc)

}

object EmptyStream extends MyStream[Nothing] {

  def isEmpty: Boolean = true
  def head: Nothing = throw new NoSuchElementException
  def tail: MyStream[Nothing] = throw new NoSuchElementException

  def #::[B >: Nothing](element: B): MyStream[B] = new Cons(element, this)// prepend operator
  def ++[B >: Nothing](anotherStream: => MyStream[B]): MyStream[B] = anotherStream// concatenate two streams

  def foreach(f: Nothing => Unit): Unit = () // this just returns the unit as there is no element for which we can apply foreach
  def map[B](f: Nothing=> B): MyStream[B] = this
  def flatMap[B](f: Nothing => MyStream[B]): MyStream[B] = this
  def filter(predicate: Nothing => Boolean): MyStream[Nothing] = this

  def take(n: Int): MyStream[Nothing] = this // takes the first n elements out of this stream. A finite stream
}
// the tail here (t1) is a by-name
class Cons[+A](hd: A, t1: => MyStream[A]) extends MyStream[A] {
  def isEmpty: Boolean = false

  override val head: A = hd // overriding as a val as might need it more than once throughout the implementation. Evaluate it as a value so it can be used throughout the entire body
  override lazy val tail: MyStream[A] = t1 // combines call by name and lazy val to make this a CALL BY NEED

  /*
  val s = new Cons(1, EmptyStream) the empty stream is lazily evaluated so it is only evaluated when needed
  val prepended = 1 #:: s = new Cons(1, s) but s will still be lazily evaluated, whatever is at the tail of s will remain lazily evaluated.
   */

  def #::[B >: A](element: B): MyStream[B] = new Cons(element, this)   // prepend operator

  // Below the tail will be lazily evaluated and only evaluated by need. the concatenation operater still retains the lazily evaluation
  def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] = new Cons(head, tail ++ anotherStream) // concatenate two streams need to add a call by Name to anotherStream  => MyStream[B] to avoid stackoverflow

  def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }

  /*
  s = New Cons(1, ?)
  mapped = s.map(_ + 1) = new Cons( 1 + 1, s.tail.map(_ + 1))
  the tail of the new Cons (s.tail.map(_ + 1)) will not be evaluated unless you use mapped.tail later on which will force the evaluation.
  Which in turn will evaluated the s.tail section which up until evaluation it was lazily evaluated.
   */

  def map[B](f: A => B): MyStream[B] = new Cons(f(head), tail.map(f)) // preserves lazy evaluation. the "tail.map(f) is under call-by-name so when it is needed it will be evaluated and it will force the evaluation of tail
  def flatMap[B](f: A => MyStream[B]): MyStream[B] = f(head) ++ tail.flatMap(f) // preserves lazy evaluation

  def filter(predicate: A => Boolean): MyStream[A] =
    if(predicate(head)) new Cons(head, tail.filter(predicate)) // the tail.filter(predicate) is a by name expression only evaluated if called later in the expression. it preserves the lazy evaluation as tail is as lazy val
    else tail.filter(predicate) // this will force the evaluation of the first element of the tail (the head of the tail) but the rest of the stream is lazily evaluated.

  def take(n: Int): MyStream[A] =  // takes the first n elements out of this stream. A finite stream
  if(n <= 0) EmptyStream
  else if (n == 1) new Cons(head, EmptyStream)
  else new Cons(head, tail.take(n - 1)) // this still preserves lazy evaluation


}


object MyStream {
  def from[A](start: A)(generator: A => A): MyStream[A] =
    new Cons(start, MyStream.from(generator(start))(generator)) // from called recursively in a lazily evaluated expresison
}


object StreamsPlayground extends App {

  val naturals = MyStream.from(1)(_ + 1) // this doesn't crash when compiled because of lazy val
  println(naturals.head)
  println(naturals.tail.head)
  println(naturals.tail.tail.head)

  val startFrom0 = 0 #:: naturals // naturals.#::(0)
  println(startFrom0.head)

  startFrom0.take(10001).foreach(println)

  // map flatMap

  println(startFrom0.map( _ * 2).take(100).toList())
  println(startFrom0.flatMap(x => new Cons(x, new Cons(x + 1, EmptyStream))).take(10).toList())
  println(startFrom0.filter(_ < 10).toList())

}
