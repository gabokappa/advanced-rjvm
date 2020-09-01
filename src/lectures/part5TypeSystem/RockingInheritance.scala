package lectures.part5TypeSystem

object RockingInheritance extends App {

  // convenience

  trait Writer[T] {
    def write(value: T): Unit
  }

  trait Closeable {
    def close(status: Int): Unit
  }

  trait GenericStream[T] {
    // some emthods
    def foreach(f: T => Unit): Unit
  }

  // stream below is its own type with all the other traits mixed in. Mixed in a specific type as a parameter to a method
  def processStream[T](stream: GenericStream[T] with Writer[T] with Closeable): Unit = {
    stream.foreach(println)
    stream.close(0)
  }

  // related to the inheritance and its the diamond problem

  trait Animal {
    def name: String
  }

  trait Lion extends Animal { override def name: String = "leon" }

  trait Tiger extends Animal { override def name: String = "tigre" }

  // as trait the Mutant below inherits name from Lion and Tiger. If this were a class you're forced to override the name method.
  class Mutant extends Lion with Tiger {
  // remove this line to see what Mutant inherits. Diamond problem is because mutant is the intersection of Lion and Tiger which are dissendents from Animal
    // override def name: String = "ALIEN"
  }

  val m = new Mutant
  println(m.name)

  /*
  Mutant
  extends Animal with { override def name: String = "leon" } // fetches animal and overrides it with Lion
  l with { override def name: String = "tigre" } //  and overides with tiger THE LAST OVERRIDE GETS PICKED!!!!
   */

  // The Super problem + type linearization. Super accesses the parent member or trait.

  trait Cold {
    def print: Unit = println("Cold")
  }

  trait Green extends Cold {
    override def print: Unit = {
      println("Green")
      println(super.print)
    }
  }

  trait Blue extends Cold {
    override def print: Unit = {
      println("Blue")
      println(super.print)
    }
  }

  class Red {
    def print: Unit = println("Red")
  }

  class White extends Red with Green with Blue {
    override def print: Unit = {
      println("white")
      super.print
    }
  }

  val colour = new White
  colour.print
}