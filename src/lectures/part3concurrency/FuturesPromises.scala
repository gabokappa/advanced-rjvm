package lectures.part3concurrency

import scala.collection.SortedSet
import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Random, Success, Try}
import scala.concurrent.duration._

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
  aFuture.onComplete { // this is used fro side effects
    case Success(meaningOfLife) => println(s"the meaning of life $meaningOfLife")
    case Failure(ex) => println((s"This fialed with $ex"))
  } // this will be called by SOME thread, we don't know which thread computes this
  Thread.sleep(3000)

  // mini social network

  case class Profile(id: String, name: String) {
    def poke(anotherProfile: Profile) =
      println(s"${this.name} poking ${anotherProfile.name}")
  }

  object SocialNetwork {
    // "database" of profiles held as a map
    val names = Map(
      "fb.id.1-zuck" -> "Mark",
      "fb.id.2-bill" -> "Bill",
      "fb.id.3-gabriel" -> "Gabriel"
    )

    val friends = Map(
      "fb.id.1-zuck" -> "fb.id.2-bill"
    )

    val random = new Random()

    // API consisting of two methodds

    def fetchProfile(id: String): Future[Profile] = Future {
      Thread.sleep(random.nextInt(300))
      Profile(id, names(id))
    }

    def fetchBestFriend(profile: Profile): Future[Profile] = Future {
      Thread.sleep(random.nextInt(400))
      val bfId = friends(profile.id)
      Profile(bfId, names(bfId))
    }
  }

  // in the client application we want Mark to poke build.

  val mark = SocialNetwork.fetchProfile("fb.id.1-zuck")
  mark.onComplete {
    case Success(markProfile) => {
      val bill = SocialNetwork.fetchBestFriend(markProfile)
      bill.onComplete {
        case Success(billProfile) => markProfile.poke(billProfile)
        case Failure(ex) => ex.printStackTrace()
      }
    }
    case Failure(e) => e.printStackTrace()
  }

  Thread.sleep(1000)

  // functional composition of futures map, flatMap, filter

  val nameOnTheWall = mark.map(profile => profile.name) // this returns a different kind of future so a future of String instead of the original future of profile

  val marksBestFriend = mark.flatMap(profile => SocialNetwork.fetchBestFriend(profile)) // can return another future within the flatMap. This returns a future of profile within a furure of profile

  val zucksBesFriendRestricted = marksBestFriend.filter(profile => profile.name.startsWith("Z"))

  // can write for-fomprehensions instead

  for {
    mark <- SocialNetwork.fetchProfile("fb.id.1-zuck")
    bill <- SocialNetwork.fetchBestFriend(mark)
  } mark.poke(bill)

  Thread.sleep(1000)

  // fallbacks

  val aDefaultProfile = SocialNetwork.fetchProfile("unknown").recover { // this recovers with a dummy profile
    case e: Throwable => Profile("fb.id.0-dummy", "Solo")
  }

  val aFetchedProfileNoMatterWhat = SocialNetwork.fetchProfile("unknown").recoverWith { // this goes and fetch another profile, we use this to return an argument that we know almost for sure that doesn't throw an exception
    case e: Throwable => SocialNetwork.fetchProfile("fb.id.3-gabriel")
  }

  val fallbackResult = SocialNetwork.fetchProfile("unknown").fallbackTo(SocialNetwork.fetchProfile("fb.id.3-gabriel")) // this creates a new future, if the first succeeds then it is fine, the fallback is used in case of the failure of the first future. If the second future also fails the exception returns in the one of the first failure


  // lets pretend we have an online banking app
  case class User(name: String)

  case class Transaction(sender: String, receiver: String, amount: Double, status: String)

  object BankingApp {
    val name = "The Banking App"

    def fetchUser(name: String): Future[User] = Future {
      // simulate long computation
      Thread.sleep(500)
      User(name)
    }

    def createTransaction(user: User, merchantName: String, amount: Double): Future[Transaction] = Future {
      // simulate some processes
      Thread.sleep(1000)
      Transaction(user.name, merchantName, amount, "SUCCESS")
    }

    def purchase(username: String, item: String, merchantName: String, cost: Double): String = {
      // fetch the user from the DB, create a transaction, WAIT for the transaction to finish
      val transactionStatusFuture = for {
        user <- fetchUser(username)
        transaction <- createTransaction(user, merchantName, cost)
      } yield transaction.status

      Await.result(transactionStatusFuture, 2.seconds) // implicits conversions -> pimp my library // the awaits means this call will block until all the futures are completed.
    }
  }

  println(BankingApp.purchase("Gabo", "iPhone 12 Pro", "Store", 2000))

  // promises

  val promise = Promise[Int]() // this call the apply method to the apply method on the Promise companion object this is a controlloer over the future
  val future = promise.future // "future" is under the management/control of the promise

  // thread 1 - "consumer"
  future.onComplete {
    case Success(r) => println("[consumer] I've received the " + r)
  }

  // thread 2 - "producer"
  val producer = new Thread(() => {
    println("[producer] crunching numbers...")
    Thread.sleep(500)
    // the below is the promise being fulfilled
    promise.success(42)
    println("[producer] done")
  })

  producer.start()
  Thread.sleep(1000)

  /*
  1) Write a future that returns a value immediatley
  2) Write inSequence(future[a], future[b]) this runs future b after future a has been completed. So in sequence.
  3) Return a future containing the earliest value returned by two futures
  first(fa, fb) => new future with the first value of the two futures

  4) last(fa,fb) the opposite of the above return the last value
  5) retryUntil(action: () => Future[T], condition: T => Boolean): Future[T] Run an action until the condition is met and then return the first instance that fulfills the condition

   */

  // 1 fulfill immediately
  def fulfillImmediatley[T](value: T): Future[T] = Future(value) // the value is already computed

  // 2 insequence

  def inSequence[A, B](first: Future[A], second: Future[B]): Future[B] = {
    first.flatMap(_ => second) // this returns the second future, making sure the first future has finished.
  }

  // 3 first out of two futures (PROMISES EMPLOYED HERE)
  def first[A](fa: Future[A], fb: Future[A]): Future[A] = {
    val promise = Promise[A] // this is the controller of a Future[A]

    // the below needs to be wrapped in a try because if one promise succeeds say fa. then fb when it goes to succeed it throws an exception because it has already succeeded.

    // helper method

    //    def tryComplete(promise: Promise[A], result: Try[A]) = result match {
    //      case Success(r) => try {
    //        promise.success(r)
    //      } catch {
    //        case _ =>
    //      }
    //      case Failure(t) => try {
    //        promise.failure(t)
    //      } catch {
    //        case _ =>
    //      }
    //    }
    //
    //    fa.onComplete(result => tryComplete(promise, result)) // fb. uses the "shorter" version using underscore instead of result variable
    //
    //    fb.onComplete(tryComplete(promise, _))

    // All the above can be shortened without using the helper method as one already exists in the promise. The difference is that it returns a boolean

    fa.onComplete(promise.tryComplete(_)) // same as below. The method is lifted into a function value
    fb.onComplete(promise.tryComplete)

    promise.future // this will hold the first finished promise out of fa or fb.
  }

    // 4 - the last of the two futures

    def last[A](fa: Future[A], fb: Future[A]): Future[A] = {
        // going to use two promises. 1 promise which both futures try to complete
      // the one that arrives last and fails the first promise, will complete a second promise

      val bothPromise = Promise[A]
      val lastPromise = Promise[A]
      val checkAndComplete = (result: Try[A]) =>
        if(!bothPromise.tryComplete(result))
          lastPromise.complete(result)

      fa.onComplete(checkAndComplete)
      fb.onComplete(checkAndComplete)

      lastPromise.future
    }

  val fast = Future {
    Thread.sleep(100)
    42
  }

  val slow = Future {
    Thread.sleep(200)
    45
  }

  first(fast, slow).foreach(f => println("FIRST: " + f)) // this should be 42
  last(fast, slow).foreach(l => println("LAST: " + l)) // this should be 45

  Thread.sleep(1000)

  // retry until
  def retryUntil[A](action: () => Future[A], condition: A => Boolean): Future[A] =
    action()  // Triggering the action returns a future.
    .filter(condition) // here, the result from action() either passes the condition or it fails
    .recoverWith { // if the above fails it retries again
      case _ => retryUntil(action, condition)
    }

  val random = new Random()
  val action = () => Future {
    Thread.sleep(100)
    val nextValue = random.nextInt(100)
    println("generated " + nextValue)
    nextValue
  }

  retryUntil(action, (x: Int) => x < 10).foreach(result => println("settled at " + result))
  Thread.sleep(10000)

}
