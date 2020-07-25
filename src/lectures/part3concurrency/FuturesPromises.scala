package lectures.part3concurrency

import scala.collection.SortedSet
import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Random, Success}
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
    val names = Map (
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

  val aDefaultProfile = SocialNetwork.fetchProfile("unknown").recover {
    case e: Throwable=> Profile("fb.id.0-dummy", "Solo")
  }

  val aFetchedProfileNoMatterWhat = SocialNetwork.fetchProfile("unknown").recoverWith {
    case e: Throwable => SocialNetwork.fetchProfile("fb.id.3-gabriel")
  }

  val fallbackResult = SocialNetwork.fetchProfile("unknown").fallbackTo(SocialNetwork.fetchProfile("fb.id.3-gabriel"))


  // lets pretend we have an online banking app
  case class User(name: String)
  case class Transaction(sender: String, receiver: String, amount: Double, status: String)

  object BankingApp {
    val name = "Banking App"

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
    // the below is the promis being fulfilled
    promise.success(42)
    println("[producer] done")
  })

  producer.start()
  Thread.sleep(1000)

}
