package lectures.part4Implicits

object PimpMyLibrary extends App {

  // 2.isPrime
// TODO implicit classes can ONLY take one value
  implicit class RichInt(val value: Int) extends AnyVal {
    def isEven: Boolean = value % 2 == 0
    def sqrt: Double = Math.sqrt(value)

    def times(function: () => Unit): Unit = {
      def timesAux(n: Int): Unit =
        if (n <= 0) ()
        else {
          function()
          timesAux(n - 1)
        }
      timesAux(value)
    }

    def *[T](list: List[T]): List[T] = {
      def concatenate(n: Int): List[T] =
        if(n <= 0) List()
        else concatenate(n -1) ++ list // concatenate the list
      concatenate(value)
    }
  }

  implicit class RicherInt(richInt: RichInt) {
    def isOdd: Boolean = richInt.value % 2 != 0
  }

  new RichInt(42).sqrt
  // we can also declare this as the below is the same as the above. This is called type enrichment because we made an implicit class

  42.isEven // the compiler re-writes this as new RichInt(42).isEven. This is called type enrichment

  // below is a type enrichment
  1 to 10

  import scala.concurrent.duration._
  3.seconds

  // compiler doesn't do multiple implicit searches
  //42.isOdd

  /*
  Enrich the String class
  - asInt
  - encrypt method
  "John" -> Lqjp

  keep enriching the Int class
  -times (function) 3.times(something)
  -  * multiple taht takes a list as argument
   */

  implicit class RichString(string: String) {
    def asInt: Int = Integer.valueOf(string) // this returns a java.lang.Integer which Java converts to Int
    def encrypt(cypherDistance: Int): String = string.map(c => (c + cypherDistance).asInstanceOf[Char])
  }
println("3".asInt + 4)
  println("John".encrypt(2))


  // testing more out

  3.times(() => println("Hello Gabriel"))
  println(4 * List(1, 2))

  // "3' / 4
  implicit def stringToInt(string: String): Int = Integer.valueOf(string)
  println("6" / 2) // this gets re-written to stringToIn("6") / 2

  // TODO The below is an equivalent to an implicit class. Similar to declaring implicit class RichAltInt(value: Int)
  class RichAltInt(value: Int)
  implicit def enrich(value: Int): RichAltInt = new RichAltInt(value) // implicit through methods are discouraged

  // danger zone

  implicit def intToBoolean(i: Int): Boolean = i == i

  /* if (n) do something
  else do something else
   */

  val aConditionedValue = if (3) "OK" else "Something wrong"
  println(aConditionedValue) // this prints something wrong if there is a bug with an implicit methods and libraries.

  // TODO BEST PRACTICE
  /*
  - re-cap on video
   */


}
