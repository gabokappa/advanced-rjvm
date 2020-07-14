package lectures.part3concurrency

import java.util.concurrent.Executors

object Intro extends App {

  /*
  The below is from Java to exlplain what the Thread is. Theinerface will be treated as a trait in Scala
  interface Runnable {
  public void run()
  }
   */
  // JVM threads
  val aThread = new Thread(new Runnable {
    override def run(): Unit = println("Running in parallel") // this piece of code instantiates a thread object with a runnable object, which has a method run
  })

//  aThread.start() //  this gives the signal to the JVM to start a JVM thread .this runs in parallel. There is a difference with the thread instance we use and the actual JVM thread where the parallel code actually takes place
  // this creates a JVM thread which runs on top of an operating sytem thread. This gets executed i na seperate jvm threads on top of the thread that evaluates this code

  // to start a thread in parallel, you call the start method on the thread NOT the run method on the runnable

  aThread.join() // this blocks until aThread running. This s how you make sure a thread finished running before you continue with another computation.

  val threadHello = new Thread(() => (1 to 5).foreach(_  => println("hello")))
  val threadGoodbye = new Thread(() => (1 to 5).foreach(_ => println("goodbye")))

//  threadHello.start()
//  threadGoodbye.start()

  // these return different results. Different runs produce different results because they are in a multi-threaded environment

  // executors. Re-use threads by creating a threead pool and using executors
  val pool = Executors.newFixedThreadPool(10) // this creates a pool of 10 threads
  pool.execute(() => println("something in the thread pool")) // Can pass in the runnable println. This runnable will get executed by one of the threads in the pool
  // but don't need to care about starting and stopping threads.

//  pool.execute(() => {
//    Thread.sleep(1000)
//    println("done after 1 second")
//  })

//  pool.execute(() => {
//    Thread.sleep(1000)
//    println("almost done")
//    Thread.sleep(1000)
//    println("done after 2 seconds")
//  })

  //if i want to shut down all the threads it means no more actions can be submitted
  pool.shutdown() // it means it no longer accepts any more actions so those which are sleeping contine to operate
  // pool.execute(() => println("should not appear")) // this throws an exception in the calling thread

  // pool.shutdownNow() // this interupts the sleeping threads currently running in the pool and these throw and exception

//  println(pool.isShutdown)

  def runInParallel = {
    var x = 0

    val thread1 = new Thread(() => {
      x = 1
    })

    val thread2 = new Thread(() => {
      x = 2
    })

    thread1.start()
    thread2.start()
    println(x)
  }

 // for (_ <- 1 to 100) runInParallel
  // this is called a race condition as two variables are trying to set the same value at the same time.

  class BankAccount(@volatile var amount: Int) {
    override def toString: String = "" + amount
  }

  def buy(account: BankAccount, thing: String, price: Int) = {
    account.amount -= price
//    println("I've bought " + thing)
//    println("my account is now " + account)
  }

//  for (_ <- 1 to 10000) {
//    val account = new BankAccount(50000)
//    val thread1 = new Thread(() => buy(account, "shoes", 3000))
//    val thread2 = new Thread(() => buy(account, "iPhone 13", 4000))
//
//    thread1.start()
//    thread2.start()
//    Thread.sleep(10)
//    if (account.amount != 43000) println(("TOLD YOU! " + account.amount))
//    //println()
//  }

  /*
  thread1 (shoes): 50000
  - account = 50000 - 3000 = 47000
  thread2 (iphone): 50000
   - account = 50000 - 4000 = 46000 overwrites the memory of account.amount

   HOW DO WE ADDRESS RACE CONDITIONS?

   option #1: use the synchronized() // synchronizing on the cirtical "thing" that will be modified.
   */

  def buySafe(account: BankAccount, thing: String, price: Int) =
    account.synchronized { // passing a block syntactic sugar for ()
      // no two threads can evaluate the expression being passed as a parameter here at the same time.
      account.amount -= price
      println("I've bought " + thing)
      println("my account is now " + account)
    }

  // synchronising is the more powerful and more used one because it allows you to put in more expressions in the synchronised block

  // option #2: is to use @volatile. So its an anotation that all reads and writes to the var are synchronised. You have to annotate the var amount with @volatile. Check the amount in the class BankAccount

  /*
  Exercises:
  1) Construct 50 "inception" threads

  Thread1 -> thread 2 -> thread 3 ...
  pintln("hello from thread #3\") print these greetings in reverse order

   */


  def inceptionThreads(maxThreads: Int, i: Int = 1): Thread = new Thread(() => {
    if (i < maxThreads) {
      val newThread = inceptionThreads(maxThreads, i + 1)
      newThread.start()
      newThread.join()
    }
    println(s"Hello from thread $i")
  })

  inceptionThreads(50).start()


  /*
  Exercise 2)
   */

  var x = 0
  val threads = (1 to 100).map(_ => new Thread(() => x += 1)) // each thread will try and increment the value of the thread
  threads.foreach(_.start())

  // what is the biggest value possible for x? is it 100? Biggest possible value for x is if they happen sequentially so it is 100.
  // what is the smallest value for x? and 0? The answer is 1. So in theory all of them could try and read the variable at the same time. And all of them attempt to write back x = 1.

  threads.foreach(_.join())
  println(x)

  /*

  Exercise 3

  the sleep fallacy
   */

  var message = ""
  val awesomeThread = new Thread(() => {
    Thread.sleep(1000)
    message = "Scala is awesome"
  })

  message = "Scala sucks"
  awesomeThread.start()
  Thread.sleep(1001)
  println(message)

  // what is the value of message, is it guaranteed, why or why not? almost always is Scala is awesome, but not guaranteed.

  /*
  This could happen instead
   (main thread)
  message = "Scala sucks"
  awesomeTHread.start()(
  sleep() - relieves execution this relieves a thread to the CPU to execute something at the CPU's discretion
  (awesome thread)
  sleep() - relieves exection
  (OS gives the CPU to some important thread which takes the CPU more than 2 seconds)
  At this point when the OS returns the CPU to the thread, bot h threads finished sleeping, so the CPU can go on the MAIN thread which prints scala sucks.

   */

  // How do we fix the above? synchronising only works with concurrent modifcations so when two threads are attempted to access the same thing at the same time. Here we have a sequential issue
  // we need to implement the .join so wait for the awesome thread to finish to join.

  /*
  message = "Scala sucks"
  awesomeThread.start()
  Thread.sleep(1001)
  awesomeThread.join() // This tells it to wait for the awesome thread to join. This solves the sleeping fallacy thing.
  println(message)

   */


}
