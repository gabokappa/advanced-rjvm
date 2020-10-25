package lectures.part5TypeSystem

object HigherKindedTypes extends App {

  // They are deep generic type with unknown paramter at the deepest level

  trait AHigherKindedType[F[_]]

  trait MyList[T] {
    def flatMap[B](f: T => B): MyList[B]
  }

  trait MyOption[T] {
    def flatMap[B](f: T => B): MyOption[B]
  }

  trait MyFuture[T] {
    def flatMap[B](f: T => B): MyFuture[B]
  }

  // combine/multiply (List(1,2) * List("a", "b") => List(1a, 1b, 2a, 2b) The concept of combine is the same through these monads.

  def multiply[A, B](listA: List[A], listB: List[B]): List[(A, B)] =
    for {
      a <- listA
      b <- listB
    } yield (a, b)

  // same can be done for option

//  def multiply[A, B](listA: Option[A], listB: Option[B]): Option[(A, B)] =
//    for {
//      a <- listA
//      b <- listB
//    } yield (a, b)

  // How do we create a common API for different concepts /monads

  // The answer is to use a Higher Kind of Type (HKT)

  trait Monad[F[_], A] { // this is a wrapper instead of the actual types, which allows us to use it in class MonadList and MonadOption
    def flatMap[B](f: A=> F[B]): F[B]
    def map[B](f: A => B): F[B]
  }

  implicit class MonadList[A](list: List[A]) extends Monad[List, A] { // the extended Monad here is an abstraction of a list of ints.
    override def flatMap[B](f: A => List[B]): List[B] = list.flatMap(f)
    override def map[B](f: A => B): List[B] = list.map(f)
  }
  // Now that we have the flatMap and map methods we cna use the multiply method. def multiply

  def multiply[F[_], A, B](implicit ma: Monad[F, A], mb: Monad[F, B]): F[(A, B)] =
    for {
      a <- ma
      b <- mb
    } yield (a, b)

  /*
  The for comprehension is re-written as
  ma.flatMap(a => mb.map(b => (a, b))) // flatMap is called on the first list(which in this example is the list of ints then map is called on the second list(which is hte list of strings)
   */

  val monadlist = new MonadList(List(1, 2, 3))
  monadlist.flatMap(x => List(x, x + 1)) // This will return a List[Int]]
  // The flatMap here does a Monad[List, Int] => List[Int]
  monadlist.map(_ * 2)
  // Monad[List, Int] => List[Int]

  println(multiply(new MonadList(List(1, 2)), new MonadList(List("a", "b"))))

  // We can re-use the wrapper API code used for list for another kind of type. For example below instead of List I'm going to do Option

  implicit class MonadOption[A](option: Option[A]) extends Monad[Option, A] { // the extended Monad here is an abstraction of a list of ints.
    override def flatMap[B](f: A => Option[B]): Option[B] = option.flatMap(f)
    override def map[B](f: A => B): Option[B] = option.map(f)
  }

  println(multiply(new MonadOption[Int](Some(2)), new MonadOption[String](Some("scala"))))

// BY making the MonadOption and MonadList implicits, and adding implicit to the multiply parameters, I can call multiply without needing to do new MonadOption or new MonadList

  println(multiply(List(2, 3), List("c", "d")))
  println(multiply(Some(4), Some("something")))
}
