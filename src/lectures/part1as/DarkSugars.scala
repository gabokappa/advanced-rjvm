package lectures.part1as

import scala.util.Try

object DarkSugars extends App {

  // syntax sugar #1: methods with single param

  def singleArg(arg: Int): String = s"$arg little dogs..."

  val desc = singleArg {
    // complex code block
    42
  }
  println(desc)

  val aTryInstance = Try { // this style of code is used a lot in practice because it is simlar to jave try
    None
  }
  println(List(1, 2, 3).map { x =>
    x + 1 + x + 1
  })

  // syntax sugar #2 - instances of traits with a single method can be reduced to lambdas these are the Single Abstract Method

  trait Action {
    def act(x: Int): Int
  }

  val anInstance: Action = new Action {
    override def act(x: Int): Int = x + 1
  }

  // the above would be the same as

  val anInstance2: Action = (x: Int) => x + 2 // magic here, the compiler realises this is a function 1 type that can be attributed to the act function in the trait
  // lambda abstract type of conversion. They are used a lot in concurrency is an example of instatiating traits with Runnables.
      // Runnables are instances of a trait (or java interface) that can be passed on to threads

  val aThread = new Thread(new Runnable {
    override def run(): Unit = println("I'm running")
  }) // this is a valid thread creations

  // below is another valid thread creation using syntactic sugar

  val aSweeterThread = new Thread(() => println("sweet syntax for runnable")) // this is a lambda that takes no parameters because the run method above doesn't take params





}
