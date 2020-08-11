package lectures.part4Implicits

import java.util.Date

object JSONSerialization extends App {

  /*
  Users, posts, feeds
  Seriaize to JSON to pass to the front end or in between micro services
   */

  case class User(name: String, age: Int, email: String)
  case class Post(content: String, createdAt: Date)
  case class Feed(user: User, posts: List[Post])

  /*
  1 -Create intermediate data types: Int, String, List, Date we json serializeble for these data types
  2 -create type classes for conversion to intermediate data types
  3 - serialize these intermediate data types ot json
   */

  sealed trait JSONValue { // intermediate data type
    def stringify: String
  }

  final case class JSONString(value: String) extends JSONValue {
    def stringify: String = "\"" + value + "\""
  }

  final case class JSONNumber(value: Int) extends JSONValue {
    def stringify: String = value.toString

  }

  final case class JSONArray(values: List[JSONValue]) extends JSONValue {
    def stringify: String = values.map(_.stringify).mkString("[", ",", "]")

  }

  final case class JSONObject(values: Map[String, JSONValue]) extends JSONValue {

    /*
   an example of this
   {
   name: "John"
   friends: [...]
    */

    def stringify: String = values.map {
      case (key, value) => "\"" + key + "\":" + value.stringify
    }
      .mkString("{", ",", "}")
  }

  val data = JSONObject(Map(
    "user" -> JSONString("Gabriel"),
    "posts" -> JSONArray(List(
      JSONString("Scala is good"),
      JSONNumber(453)
    ))
  ))

  println(data.stringify)

  // 2 type class to convert the three things into json values

  /*
  There are three things we need for type classes.
  2.1- the type classes themselves
  2.2- type class instances (implicit)
  2.3 - (this is the conversion route - pimp my library) - methods to use the typ class instances
   */

  // 2.1 -type class
  trait JSONConverter[T] {
    def convert(value: T): JSONValue
  }

  // 2.2 type class instances

  // these are for existing data types
  implicit object StringConverter extends JSONConverter[String] {
    def convert(value: String): JSONValue = JSONString(value)
  }

  implicit object NumberConverter extends JSONConverter[Int] {
    def convert(value: Int): JSONValue =JSONNumber(value)
  }

  // custom data types

  implicit object UserConverter extends JSONConverter[User] {
    def convert(user: User): JSONObject = JSONObject(Map(
      "name" -> JSONString(user.name),
      "age" -> JSONNumber(user.age),
      "email" -> JSONString(user.email)
    ))
  }

  // 2 call on stringify on result

}
