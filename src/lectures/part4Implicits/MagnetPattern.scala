package lectures.part4Implicits

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object MagnetPattern extends App {

  // deals with method overloading

  class P2PRequest
  class P2PResponse
  class Serializer[T]

  trait Actor {
    def recieve(statusCode: Int): Int
    def recieve(request: P2PRequest): Int
    def recieve(response: P2PResponse): Int
    // TODO def receive[T](message: T)(implicit serializer: Serializer[T]) // this is the same as the below
    def receive[T : Serializer](message: T): Int
    def receive[T: Serializer](message: T, statusCode: Int): Int
    def receive(future: Future[P2PRequest]): Int
    // LOTS OF OVERLOADS THIS HAS PROBLEMS
  }

  /*
  These are problems
  1 - type erasure (clashing of methods
  2 - lifting doesn't work for all overloads // can't write val receiveFV = receive _ (the compiler doesn't know about the underscore
  3 - code duplication the logic for most of teh receive methods is simillar
  4 - type inference and default arguments. The compiler won't know which default arguments

  The above can be re-written
   */

  trait MessageMagnet[Result] {
    def apply(): Result
  }

  def receive[R](magnet: MessageMagnet[R]): R = magnet()

  implicit class FromP2PRequest(request: P2PRequest) extends MessageMagnet[Int] {
    def apply(): Int = {
      // logic for handling p2p request
      println("Handling P2P request")
      42
    }
  }

  implicit class FromP2PResponse(request: P2PResponse) extends MessageMagnet[Int] {
    def apply(): Int = {
      // logic for handling p2p response
      println("Handling P2P response *")
      24
    }
  }

  receive(new P2PRequest)
  receive(new P2PResponse)

  /* THE MAGNET PATTER
  1 - no more type erasure problems
   */
  implicit class FromResponseFuture(future: Future[P2PResponse]) extends MessageMagnet[Int] {
    override def apply(): Int = 2
  }

  implicit class FromRequestFuture(future: Future[P2PRequest]) extends MessageMagnet[Int] {
    override def apply(): Int = 3
  }

  println(receive(Future(new P2PResponse)))
  println(receive(Future(new P2PRequest)))

  // the compiler looks for implicit conversions before the types are raised

  /*
  2 -lifting works
   */

  trait MathLib {
    def add1(x: Int): Int  = x + 1
    def add1(s: String):Int = s.toInt + 1
    // add1 overloads
  }

  // "magnetize" the above the re-written below
  trait AddMagnet {
    def apply(): Int
  }
  def add1(magnet: AddMagnet): Int = magnet()

  implicit class AddInt(x: Int) extends AddMagnet {
    override def apply(): Int = x + 1
  }

  implicit class AddString(s: String) extends AddMagnet {
    override def apply(): Int = s.toInt + 1
  }

  val addFV = add1 _

  println(addFV(1))
  println(addFV("3"))

  // val receiveFV = receive _ this doesn't work

  /*
  Drawbacks
  1 - verbose
  2 - difficult to read
  3 - you can't name or place default arguments, it has to receive somethibng.
  4- call by name doesn't work correctly (exercise why it doesn't w=work with the magnet pattern. think of side effects.

   */

  class Hanlder {
    def handle(s: => String) ={
    println(s)
      println(s)
    }
  }

  trait HandleMagnet {
    def apply(): Unit
  }

  def handle(magnet: HandleMagnet) = magnet()

  implicit class StringHandle(s: => String) extends HandleMagnet {
    override def apply(): Unit = {
      println(s)
      println(s)
    }
  }

  def sideEffectMethod(): String = {
    println("Hello Scala")
    "funny"
  }

  handle(sideEffectMethod())
  handle {
    println("Hello Scala")
    new StringHandle("funny")

    // you can see from the aboe it only prints Hello scala once, but it does "funny"
  }


}
