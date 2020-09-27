package lectures.part5TypeSystem

object StructuralTypes extends App {

  // structural types

  type JavaCloseable = java.io.Closeable //kind of things that can close

  class HipsterCloseable {
    def close(): Unit = println("yeah yeah I'm closing")
    def closeSilently(): Unit = println("not making a sound")
  }

  type UnifiedCloseabe = {
  def close(): Unit
  } // THIS IS CALLED A STRUCTURAL TYPE (they have variable and methods inside)

  def closeQuietly(unifiedCloseabe: UnifiedCloseabe): Unit = unifiedCloseabe.close()

  closeQuietly(new JavaCloseable {
    override def close(): Unit = ???
  })
  closeQuietly(new HipsterCloseable)

  // TYPE REFINEMENTS directly below is a type refinemnet

  type AdvancedCloseable = JavaCloseable { // so here advanced closeable is a JavaCloseable but we're adding (enriching) a closeSilently method
  def closeSilently(): Unit
  }

  class AdvancedJavaCloseable extends JavaCloseable {
    override def close(): Unit = println("java closes")
    def closeSilently(): Unit = println("Java closes silently")
  }

  def closeShh(advCloseable: AdvancedCloseable): Unit = advCloseable.closeSilently()

  closeShh(new AdvancedJavaCloseable)
  // closeShh(new HipsterCloseable) This doesn't work despite having the right methods, because HipsterCloseable although having all the methods does not originate from JavaCloseable

  // using structural types as standalone types

  def altClose(closeable: { def close(): Unit }): Unit = closeable.close() // the param here is it's own type, which we aliased as UnifiedCloseable

  // type-checking => duck typing

  type SoundMaker = {
  def makeSound(): Unit
  }

  class Dog {
    def makeSound(): Unit = println("bark")
  }

  class Car {
    def makeSound(): Unit = println("car noise")
  }

  // From a structural standpoint Car and Dog conform to the structure defined by SoundMaker so I can do the below

  // This is called static duck typing where the structure of the type on the right-hand side conforms to the structure of the left-hand side.
  // the Scala compiler does the duck test at compile time.
  val dog: SoundMaker = new Dog
  val car: SoundMaker = new Car

  // CAVEAT: this is based on reflection. Reflective calls have a big impact on performance.

  /*
  Exercises
  1 . is f compatible with a CBL (cons based list) and with a Human? Yes it is compatible with both
   */

  trait CBL[+T] {
    def head: T
    def tail: CBL[T]
  }

  class Human {
    def head: Brain = new Brain
  }

  class Brain {
    override def toString: String = "Brain"
  }

  def f[T](somethingWithAHead: { def head: T}): Unit = println(somethingWithAHead.head)


/*
2. Is the headEqualizer compatible with CBL and a Human
 */

  object HeadEqualizer {
    type Headable[T] = { def head: T }
    def ===[T](a: Headable[T], b: Headable[T]): Boolean = a.head == b.head
  }

}
