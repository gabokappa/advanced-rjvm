package lectures.part4Implicits

import java.util.Date

object JSONSerialization extends App {

  /*
  Users, posts, feeds
  Seriaize to JSON
   */

  case class User(name: String, age: Int, email: String)
  case class Post(content: String, createdAt: Date)
  case class Feed(user: User, posts: List[Post])

  /*
  1 -Create intermediate data types: Int, String, List, Date
  2 -create type classes for conversion to intermediate data types
  3 - serialize these intermedite data types ot json
   */

}
