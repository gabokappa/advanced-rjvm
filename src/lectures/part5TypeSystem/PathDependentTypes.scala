package lectures.part5TypeSystem

object PathDependentTypes extends App {

  class Outer {
    class Inner
    object InnerObject
    type InnerType

    def print(i: Inner) = println(i)
    def printGeneral(i: Outer#Inner) = println(i)
  }

  def aMethod: Int = {
  class HelperClass
    type HelperType = String
    2
  }

  // per-instance can't instantiate the inner class directly. eg val inner = new Outer.Inner OR val inner = new Inner
  val o = new Outer
  val inner = new o.Inner // o.Inner is it's own type. To reference and inner type you need an outer instance and each outer instance is different

  val oo = new Outer
  // THIS DOESN'T COMPILE val otherInner: oo.Inner = new o.Inner

  o.print(inner)

  // these are path dependent types. All inner types have a common super type

  // Outer#Inner
  o.printGeneral(inner)
  oo.printGeneral(inner) // this works because the o.Inner type is a sub-type of the more general type Outer#Inner

  /* Exercise
  DB keyed by Int or String but mayber others
   */

  trait ItemLike {
    type Key
  }

  trait Item[K] extends ItemLike {
    type Key = K
  }
  trait IntItem extends Item[Int]
  trait StringItem extends Item[String]

  def get[ItemType <: ItemLike](key: ItemType#Key): ItemType = ???

  get[IntItem](42) // ok
  get[StringItem]("home") // ok
 // get[IntItem]("scala") // NOTHING)

}
