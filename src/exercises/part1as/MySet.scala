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

  // Exercise 1
  def contains(elem: A): Boolean
  def +(elem: A): MySet[A]
  def ++(anotherSet: MySet[A]): MySet[A] // union operator
  def map[B](f: A => B): MySet[B]
  def flatMap[B](f: A => MySet[B]): MySet[B]
  def filter(predicate: A => Boolean): MySet[A]
  def foreach(f: A => Unit): Unit

  // Exercise 2
  def -(elem: A): MySet[A]
  def --(anotherSet: MySet[A]): MySet[A] //difference
  def &(anotherSet: MySet[A]): MySet[A] // intersection

  // Exercise 3 - implement a unary_! is a negation of a set. If you have a set[1,2,3] this methods returns everything BUT set[1,2,3]
  // set[1,2,3] => negating this could lead to an infinite number of possible return values.
  def unary_! : MySet[A] // make sure you leave a space between the ! and : so it doesn't include the : as part of the method name


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
  def -(elem: A): MySet[A] = this
  def --(anotherSet: MySet[A]): MySet[A] = this
  def &(anotherSet: MySet[A]): MySet[A] = this

  //Exercise 3
  // unary! for an empty set. If you negate an empty set that will lead to creating a set that has every single element of type A
  def unary_! : MySet[A] = new PropertyBasedSet[A](_ => true)

}

  class AllInclusiveSet[A] extends MySet[A] {
    override def contains(elem: A): Boolean = true
    override def +(elem: A): MySet[A] = this
    override def ++(anotherSet: MySet[A]): MySet[A] = this

    // naturals = if you have an allinlusive set of integers, so all the natural numbers
    // naturals.map(x => x %3) => ??? (what does it give us?). The answer is if we map all the naturals we only get [0, 1, 2]
    override def map[B](f: A => B): MySet[B] = ???
    override def flatMap[B](f: A => MySet[B]): MySet[B] = ???

    override def filter(predicate: A => Boolean): MySet[A] = ??? // introduce the concept of a property based set. That mean all elements of type A that satisfy the predicate which could be infinite

    override def foreach(f: A => Unit): Unit = ???

    override def -(elem: A): MySet[A] = ???

    override def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)

    override def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet)

    override def unary_! : MySet[A] = new EmptySet[A]

    // all inclusive sets are infinite, and it requires the introduction of a property based set.
  }


// This will denote all the elements of type A that satisfy the property. The function of the property returns true if they are of type A
// this is more flexible
// { x in A | property(x) } <= mathematical
class PropertyBasedSet[A](property: A => Boolean) extends MySet[A] {

  def contains(elem: A): Boolean = property(elem)

  // Tis is for below { x in A | property(x) * my comment :if (property(elem))* } + element = { x in A | property(x) || x == element }
  def +(elem: A): MySet[A] =
    new PropertyBasedSet[A](x => property(x) || x == elem)

  // union operator.
  // { x in A | property(x) ++ set => {x in A | property(x) || the other set contains x }
  def ++(anotherSet: MySet[A]): MySet[A] =
    new PropertyBasedSet[A](x => property(x) || anotherSet(x)) // does anotherSet contain X. Remember apply is essentially checking whether it contains the elem

  // Can't really map or flatMap a PropertyBasedSet because we don't know what we're going to obtain. If you map over a infinite set with a function you don't know if it is finite or not.
  def map[B](f: A => B): MySet[B] = politelyFail
  def flatMap[B](f: A => MySet[B]): MySet[B] = politelyFail
  def foreach(f: A => Unit): Unit = politelyFail

  def filter(predicate: A => Boolean): MySet[A] = new PropertyBasedSet[A](x => property(x) && predicate(x))
  def -(elem: A): MySet[A] = filter(x => x != elem) // for any element x, x is not equal to any element I pass as a parameter.
  def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)
  def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet) // intersection. Remember anotherSet apply method is effectively a contains function. so anotherSet can be passed into filter as it is A => Boolean

  // for example if you the property for PropertyBasedSet to return all even numbers the unary! will return all the numbers for which the property of eveness does NOT hold for the numbers so you get all the odd numbers
  def unary_! : MySet[A] = new PropertyBasedSet[A](x => !property(x)) // for every x the opposite of property(x) holds

  def politelyFail = throw new IllegalArgumentException("Really deep rabbit hole")


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
 As an example if my set is [1, 2, 3] and I want to ++ [4, 5] =
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

    def -(elem: A): MySet[A] =
      if (head == elem) tail
      else tail - elem + head //recursively calling the - (minus) on tail


    def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet) // this was originally = filter(x => !anotherSet.contains(x))

    // as with & method contains is the same as apply this can be refactored as

    // = filter(x => !anotherSet(x))

    // and -- can be modified with implementing a new unary operator so the answer is = filter(!anotherSet) need to define the unary operator for MySet

    def &(anotherSet: MySet[A]): MySet[A] = filter(x => anotherSet.contains(x)) // intersecting and filtering are practically the same thing because they are functional.
    // basically if the other set has the same element in it creates a new MySet with the elements that are in both sets

  // filtering all the elements in the initial set on the condition that the other set contains that element
    /* this can be refactored because contain is the contract for the set. Apply and contains in this are the same. The set is functional, and how wr defined contains (in the trait as being) apply,
    so we can just use apply. So we can just call filter on another set as we're using the apply on anotherSet which checks if it contains something.

    so & could be = filter(anotherSet) // I presume the x => is implicit? Another set is a function.

    More so intersecting & is the same thing as filter

     */

    // Exercise 3
    def unary_! : MySet[A] = new PropertyBasedSet[A](x => !this.contains(x))

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

  val negative = !s  // this is re-wirtten as s.unary! = all the naturals not equals to 1,2,3,4
  println(negative(2)) // false here means that 2 is NOT in the negative
  println(negative(5)) // true here means that 5 IS in the negative set, i.e not in the original set.

  val negativeEven = negative.filter( _ % 2 == 0)
  println(s"5 here is false because 5 although not in s variable it isn't even:  ${negativeEven(5)}")

  val negativeEvenPlus5 = negativeEven + 5 // all the even numbers > 4 plus the number 5.
println(negativeEvenPlus5(5))

  val randoList = negativeEven ++ MySet(15, 6, 10)
  println(randoList(15))

  val removeit = randoList -- MySet(6, 10)
  println(removeit(6))
}