package lectures.part2afp

object Monads extends App {

  // our own Try monad below
  trait Attempt[+A] {
    def flatMap[B](f: A => Attempt[B]): Attempt[B]
  }
  object Attempt {
    def apply[A](a: => A): Attempt[A] =
      try{
        Success(a)
      } catch {
        case e: Throwable => Fail(e)
      }
  }

  case class Success[+A](value: A) extends Attempt[A] {
    def flatMap[B](f: A => Attempt[B]): Attempt[B] =
      try{
        f(value)
      } catch {
        case e: Throwable => Fail(e)
      }
  }
  case class Fail(e: Throwable) extends  Attempt[Nothing] {
    def flatMap[B](f: Nothing => Attempt[B]): Attempt[B] = this
  }

  /*
  left-identity
  unit.flatMap(f) = f(x)
  Attempt(x).flatMap(f) = f(x) // this is the Success case!
  Success(x).flatMap(f) = f(x) // proved

  right-identity
  attempt.flatMap(unit) = attempt
  Success(x).flatMap(x => Attempt(x)) = Attempt(x) = Success(x)
  Fail(_).flatMap(....) = Fail(e) // this returns a failure with the throwable


  associativity
  attempt.flatMap(f).flatMap(g) == attempt.flatMap(x => f(x).flatMap(g) //
  Fail(e).flatMap(f).flatMap(g) = Fail(e) returns the same failure object
  Fail(e).flatMap(x => f(x).flatMap(g)) = Fail(e)

//THE BELOW IS THE LEFT_HANDSIDE OF THE EQUALITY IN LINE 43
  Success(v).flatMap(f).flatMap(g) =
  f(v).flatMap(g) OR Fail(e)

//THE BELOW IS THE RIGHT_HANDSIDE OF THE EQUALITY IN LINE 43
  Success(v).flatMap(x => f(x).flatMap(g)) =
  f(v).flatMap(g) OR Fail(e)

   */

//  val attempt = Attempt {
//    throw new RuntimeException("My own Monad")
//  }
//  println(attempt)

  /*
  1) EXERCISE: implement a Lazy[T] monad = computation which will only be executed when it's needed.
  unit/apply for a companion object of a lazy trait
  flatMap

  2) Monads = unit + flatMap. An alternavie definisition is = unit + map + flatten
  given a Monad[T] {

  def flatMap[B](f: T => Monad[B]): Monad[B] = .....implemented

  def map[B](f: T => B): Monad[B] = ???
  def flatten(m: Monad[Monad[T]]): Monad[T] = ???

     */

  // EXERCISE 1 implement a Lazy[T] monad = computation which will only be executed when needed.
  // unit/apply for a Monad
  // flatMap

  // EXERCISE 2 (see

  // implement Monads = unit + flatMap
  // implement Monads = unit + map + flatten (sometimes Monads are like this)

  // the below takes a value by name, which prevents the value from being evaluated when the lazy object is being constructed.
  class Lazy[+A](value: => A) {
    // call by need i.e create a private lazy val to equate to value
    private lazy val internalValue = value
    def use: A = internalValue
    // the f: (=> A) means that flatMap receives f lazily by-name so it delays the evaluation.
  def flatMap[B](f: (=> A) => Lazy[B]): Lazy[B] = f(internalValue)

  }

  object Lazy {
    def apply[A](value: => A): Lazy[A] = new Lazy(value)

  }

  val LazyInstance = Lazy {
    println("Today is sunny")
    42
  }
//  println(LazyInstance.use) // had to create the methof use, in this instance should see the lyric and the value printed out

  val flatMappedInstance = LazyInstance.flatMap(x => Lazy {
    10 * x
  })

  val flatMappedInstance2 = LazyInstance.flatMap(x => Lazy {
    10 * x
  })
flatMappedInstance.use // this forces the evaluation of the LazyInstance in this original variable. Because of the call by need in the Lazy class this only evaluates once not again in the following line
  flatMappedInstance2.use

  /* left-identity
  unit.flatMap(f) = f(v)
  Lazy(v).flatMap(f) = f(v)

  right-identity
  l.flatMap(unit) = l
  Lazy(v).flatMap(x => Lazy(x)) = Lazy(v)

  associativity: l.flatMap(f).flatMap(g) = l.flatMap(x => f(x).flatMap(g))
  Lazy(v).flatMap(f).flatMap(g) = f(v).flatMap(g)
  Lazy(v).flatMap(x => f(x).flatMap(g)) = f(v).flatMap(g)

   */

  // 2: map and flatten in terms of flatMap

  /*

    Monad[T] { // List


  def map[B](f: T => B): Monad[B] = flatMap(x => unit(f(x)) The unit here constructs a monad based on the values given of type B, so the end result is a Monad[B]

  def flatten(m: Monad[Monad[T]]): Monad[T] = m.flatMap((x: Monad[T]) => x)
}
  List(1,2,3).map(_ * 2) = List(1,2,3).flatMap(x => List(x * 2))
  LIst(List(1, 2), List(3, 4)).flatten = List(List(1, 2), List(3, 4)).flatMap(x => x) = List(1, 2, 3, 4)

   */



}
