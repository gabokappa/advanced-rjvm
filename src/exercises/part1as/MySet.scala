package exercises.part1as

trait MySet[A] extends (A => Boolean) {
  /*
  Create/ implement a functional set

  implement an apply method even if it is in the Boolean trait above

  create

   */
  // MySet extends a Boolean which has an apply mehtod. But I need to implement the apply method in the trait itself

  def apply(elem: A): Boolean =
    contains(elem)

  // the apply method is the general contract of MySet. The apply method will only tell if the element is in the set and return true or false
  // as the apply method is implemented in this trait it doesn't have to included in the classes below.

  def contains(elem: A): Boolean
  def +(elem: A): MySet[A]
  def ++(anotherSet: MySet[A]): MySet[A] // union operator
  def map[B](f: A => B): MySet[B]
  def flatMap[B](f: A => MySet[B]): MySet[B]
  def filter(predicate: A => Boolean): MySet[A]
  def foreach(f: A => Unit): Unit
  def -(elem: A): MySet[A]
  def --(anotherSet: MySet[A]): MySet[A]
  def &(anotherSet: MySet[A]): MySet[A]

}

// the emoty could be a singleton class extending MySet[Nothing] only if we made the trait covariant, so MySet[+A]/

class EmptySet[A] extends MySet[A] {
  def contains(elem: A): Boolean = false

  def +(elem: A): MySet[A] = new NonEmptySet[A](elem, this)

  def ++(anotherSet: MySet[A]): MySet[A] = anotherSet

  def map[B](f: A => B): MySet[B] = new EmptySet[B]

  def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B]

  def filter(predicate: A => Boolean): MySet[A] = this

  def foreach(f: A => Unit): Unit = () // this is the unit value


}

class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A] {

  def contains(elem: A): Boolean =
    elem == head || tail.contains(elem)

  // + below is making the assumption that you don't want to add an element if it already exists in MySet, hence the if statement. This is an add function
  def +(elem: A): MySet[A] =
    if (this.contains(elem)) this
    else new NonEmptySet[A](elem, this)

  def ++(anotherSet: MySet[A]): MySet[A] =
    tail ++ anotherSet + head

  //  tail.++(anotherSet.+(head))

  /*
 The ++ method recursivley calls the ++ on the tail
 As an example if my set is [1, 2, 3] and i want to ++ [4, 5] =
 the tail of the first list [2, 3] ++ [4, 5] + 1 which is recursive call on the tail so it becomes =
 [3] ++ [4, 5] + 1 + 2 =
 [] ++ [4, 5] + 1 + 2 + 3 =
 the concatenation at this point is the ++ for an EmptySet so it just returns the "anotherSet"
 [4, 5] + 1 + 2 + 3 = [4, 5, 1, 2, 3]

  */

  def map[B](f: A => B): MySet[B] = (tail.map(f)) + f(head) // this could be (tail map f) + f(head)
  def flatMap[B](f: A => MySet[B]): MySet[B] = (tail flatMap f) ++ f(head) // tail.flatMap(f) ++ f(head)

  def filter(predicate: A => Boolean): MySet[A] = {
    val filteredTail = tail filter predicate
    if (predicate(head)) filteredTail + head
    else filteredTail
    //    if(predicate(head)) new NonEmptySet[A](head, tail.filter(predicate))
    //    else tail.filter(predicate)
  }

  def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)

  }


}

object MySet {

  /*
  When you say val s = MySet(1,2,3) that actually calls the buildSet with the seq(1,2,3) and an empty set so it becomes the recursive call to the below
  buildSet(seq(1,2,3), []) =
  buildSet(seq(2,3), [] + 1) =
  buildSet(seq(3), [1] + 2) =
  buildSet(seq(), [1, 2] + 3) =
  because valSeq is now empty
  [1, 2, 3]

   */

  def apply[A](values: A*): MySet[A] = {
    def buildSet(valSeq: Seq[A], acc: MySet[A]): MySet[A] =
      if (valSeq.isEmpty) acc
      else buildSet(valSeq.tail, acc + valSeq.head)

    buildSet(values.toSeq, new EmptySet[A])
  }
}

object MySetPlayground extends App {

  val s = MySet(1,2,3,4)
  s foreach println // these print in reverse order it isnt a sequence but a set.
  // the above tells me the apply method works and so is the + method we defined as apply relies on it
println("second example")
  s + 5 foreach println
  println("third example")
  s + 5 ++ MySet(-1, -2) foreach println
  println("fourth example doesn't add an extra 5")
  s + 5 + 5 ++ MySet(-1, -2) foreach println
  println("fifth example testing map")
  s + 5 + 5 ++ MySet(-1, -2) map (x => x * 10) foreach println
  println("sixth example testing flatMap")
  s + 5 + 5 ++ MySet(-1, -2) flatMap (x => MySet(x, x * 10)) foreach println
  println("seventh example testing filter")
  s + 5 + 5 ++ MySet(-1, -2) flatMap (x => MySet(x, x * 10)) filter (_ % 2 == 0) foreach println

}