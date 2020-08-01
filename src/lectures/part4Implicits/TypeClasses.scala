package lectures.part4Implicits

object TypeClasses extends App {

  trait HTMLWritable {
    def toHTML: String
  }

  case class User(name: String, age: Int, email: String) extends HTMLWritable {
    override def toHTML: String = s"<div>$name ($age yo) <a href=$email/> </div>"
  }

  User("John", 33, "john@testemail.com").toHTML

  /*
  ISSUES WITH THE ABOVE

  1) This only works for the types WE write. So for other types like java standard dates or other types in libraries we would need to write conversion to other types which isnt pretty
  2) This is only one implementation out of many options. How do we treat this when a user logs in or out
   */

  // Another option would be to use pattern match
  // option -2
  object HTMLSerializerPM {
    def serialiseToHtml(value: Any) = value match {
      case User(n, a ,e) =>
     // case java.util.Date =>
      case _ =>
    }
  }

  // isues with option 2

  /*
  1) we lost type safety as the value can be anything
  2) We have to modify the code all the time. Whenever we add a data structure or something else we want to render on the page
  3) This is still one implementation
   */


  // BETTER DESIGN create a trait

  trait HTMLSerializer[T] {
    def serialize(value: T): String
  }

  implicit object UserSerializer extends HTMLSerializer[User] {
    def serialize(user: User): String = s"<div>${user.name} (${user.age} yo) <a href=${user.email}/> </div>"
  }

  // this the proper implementation of user serialiser
  val john = User("John", 32, "john@tesemail.com")
  println(UserSerializer.serialize(john))

  // 1 we can define serializers for other types even for types we haven't written. e.g below

  import java.util.Date
  object DateSerializer extends HTMLSerializer[Date] {
    override def serialize(date: Date): String = s"<div>${date.toString()}/> </div>"
  }

  // 2 - we can define MULTIPLE serializers for a certain type. The example here is String, the same as the original object UserSerializer

  object PartialUserSerializer extends HTMLSerializer[User] {
    override def serialize(user: User): String = s"<div>${user.name}> </div>"
  }

  // TYPE CLASS this is what HTMLSerializer is. A type calls specficies a set of operations that can be applied to a given type (in this case def serialize that need to be overwritten
  // Anything extends this TYPE CLASS needs to implement the operatuin(serialize).
  // The object(e.g PartialUserSerializer) that extends the TYPE CLASS(HTMLSerializer) these (PartialUserSerializer) are call type class instances. They are singleton objects and don't need to be instantiatied
  // A normal class describes a method and properties of a particular type (e.g String) type checkers use this info when compiling

  // TypeClass describes a collection of properties and methods a type must have in order to belong to that type class. SO if a type belongs to a ordering type class (so Person belongs to a ordering class) it is known the instance belongs to that and can implement that

  //TYPE CLASS example

  trait MyTypeClassTemplate[T] {
    def action(value: T): String
  }

  object MyTypeClassTemplate {
    def apply[T](implicit instance: MyTypeClassTemplate[T]) = instance
    // as this surfaces the the implicit this means we have access to all the methods in the trair above?
  }

  /*
  Implemenet an equal type class that compares two values
   */

  trait Equal[T] {
    def apply(a: T, b: T): Boolean
  }

  implicit object NameEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name
  }

  object FullEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = (a.name == b.name && a.email == b.email)
  }


  // part 2

  object HTMLSerializer {
    def serialize[T](value: T)(implicit serializer: HTMLSerializer[T]): String =
      serializer.serialize(value)

    // TODO adding apply method a better design to have a factory method apply here

    def apply[T](implicit serializer: HTMLSerializer[T]) = serializer
  }

  // TODO the original code below
//  object IntSerializer extends HTMLSerializer[Int] {
//    override def serialize(value: Int): String = s"<div style: color=blue>$value</div>"
//  }
//
//  println(HTMLSerializer.serialize(42)(IntSerializer))

// TODO the implicit below with the IntSerializer implicit so no need to use it below

    implicit object IntSerializer extends HTMLSerializer[Int] {
      override def serialize(value: Int): String = s"<div style: color=blue>$value</div>"
    }

    println(HTMLSerializer.serialize(42))
  // below need to make the UserSerializer above implicit

  println(HTMLSerializer.serialize(john))
  println(HTMLSerializer[User].serialize(john)) //as we declare the given type in HTMLSerializer[User] we have access to entire type class interface so other methods as well.

  /*
  Exercise: implement the type class pattern for the Equality type class.
   */

  object Equal {
    def apply[T](a: T, b: T)(implicit equalizer: Equal[T]): Boolean =
      equalizer.apply(a, b)
  }

  val anotherJohn = User("John", 45, "johnny2@test.com")
  println(Equal.apply(john, anotherJohn))
  // below is the same. The below is known as AD-HOC polymorphism. If two distinct unrelated types have qualizers implemented we can call Equal regardles of their type.
  // based on the types passed to the Equal the compiler fetches the right type.
  println(Equal(john, anotherJohn))


}
