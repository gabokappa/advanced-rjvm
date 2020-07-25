package lectures.part3concurrency

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object FuturesPromises extends App {

  def calculateMeaningOfLife: Int = {
    Thread.sleep(2000)
    42
  }

  val aFuture = Future {
    calculateMeaningOfLife // this creates a future object/instance by calling the apply method of the companion object of the future trait and inside here I pass the expression I want to delegate to another thread
  } // (global) passed by the compiler

  println(aFuture.value) // The value returns an Option[Try[Int]]
  println("Waiting on the future")
  aFuture.onComplete(t => t match {
    case Success(meaningOfLife) => println(s"the meaning of life $meaningOfLife")
    case Failure(ex) => println((s"This fialed with $ex"))
  })
Thread.sleep(3000)
}
