package exercises

import lectures.part4Implicits.TypeClasses.User

object EqualityPlayground extends App {

  trait Equal[T] {
    def apply(a: T, b: T): Boolean
  }

  implicit object NameEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name
  }

  object FullEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = (a.name == b.name && a.email == b.email)
  }

  object Equal {
    def apply[T](a: T, b: T)(implicit equalizer: Equal[T]): Boolean =
      equalizer.apply(a, b)
  }

  val john = User("John", 32, "john@tesemail.com")
  val anotherJohn = User("John", 45, "johnny2@test.com")
  println(Equal.apply(john, anotherJohn))
  // below is the same. The below is known as AD-HOC polymorphism. If two distinct unrelated types have equalizers implemented we can call Equal regardless of their type.
  // based on the types passed to the Equal the compiler fetches the right type.
  println(Equal(john, anotherJohn))

  /*
  Improve equal type class with an implicit conversion class
  get two methods
  ===(anothervalue: T)
  !==(anotherValue: T the opposite of the above
   */

  implicit class TypeSafeEqual[T](value: T) {
    def ===(other: T)(implicit equalizer: Equal[T]): Boolean = equalizer.apply(value, other)
    def !==(other: T)(implicit equalizer: Equal[T]): Boolean = ! equalizer.apply(value, other)
  }
println(john === anotherJohn)
  /*
  These are the steps the compiler does
  it re-wirtes the expression as john.===(anotherJohn) it finds an implicit class TypeSafeEqual and wraps it around a new instance of equal user
  new TypeSafeEqual[User](john).===(anotherJohn) // this method (the ===) takes an implicit equalizer which it takes from above from the implicit NameEquality
  new TypeSafeEqual[User](john).===(anotherJohn)(NameEquality)
   */

  /*
  THIS IS TYPE SAFE you can't === john === 43 for example because different types
   */

}
