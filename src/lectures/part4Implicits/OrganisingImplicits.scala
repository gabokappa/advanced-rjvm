package lectures.part4Implicits

object OrganisingImplicits extends App {

  implicit val reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _) // a comparison function is introduced and now the list is sorted in reverse order. This implicit val takes precedent over the other.
  println(List(1, 2, 6, 4, 5, 9, 8).sorted) // Scala has already an implicit ordering for Integers (hover over sorted)

  // Scala looks for the ordering in the package scala.Predef which is automatically imported. But you can override the above by introducing the implicit ordering

  /*
  Implicits (potentially used as implicit parameters)
  -val/var
  -object
  -accessor methods = defs with no parentheses. for example the implicit val reverserOrdering could be written as def reverseOrdering, but it can be def reverseOrdering()
  - can only be defined within a class, object or trait not TOP LEVEL
   */

  // Exercise -1
  case class Person(name: String, age: Int)

  // my version: implicit val personOrdering: Ordering[Person] = Ordering.fromLessThan(_.name < _.name)

  val persons = List (
    Person("Steve", 30),
    Person("Amy", 22),
    Person("John", 66)
  )
object Person {
  implicit val alphabeticOrdering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0) // how to compare strings
}
  println(persons.sorted)

/*
Implicit scope (priority in this list is top to bottom)
- normal scope = LOCAL SCOPE where we write our code (highest priority)
- imported scope (imported into the doc like we do with libraries etc)
- companion objects of all types involved in the method signature (take example above with the below sorted definition as example)
  - List ( the compiler looks for implicitis ordering in here)
  - Ordering ( the companion object)
  - all the types of involved = A or any supertyp. So in the example here if we move the implicit val as part of a companion object of the Person as Person is involved in the list the compiler can see the implicit. If it is in another unrelated object then it can't see that.

 */

  // def sorted[B >: A](implicit ord: Ordering[B]): List[B] This is what the sorting does in the example above which for our case is a List



}
