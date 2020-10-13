package lectures.part5TypeSystem

object SelfTypes extends App {

  // self type is a way of requiring a type to be mixed in

  trait Instrumentalist {
    def play(): Unit
  }

  trait Singer { self: Instrumentalist => // This enforces that a Singer has to play an Instrument (the trait above) This the marker that forces implementation oof Instrumentalist. This is NOT a lambda syntax
    // the self construct is independent from the rest of the implementation or API.

    def sing(): Unit
  }

  class LeadSinger extends Singer with Instrumentalist { // this follows the constraint enforced by the "trait Singer { self: Instrumentalist =>" construct
    // The marker in the Singer trait enforces whoever implements the Singer trait they need to enforce the Instrumentalist trait.
    override def play(): Unit = ???

    override def sing(): Unit = ???
  }
// TODO the below doesn't work because it's constrained by the self implementation in Singer, so this would not compile

//  class Vocalist extends Singer {
//    override def sing(): Unit = ???
//  }

  val jamesH = new Singer with Instrumentalist {
    override def play(): Unit = ???
    override def sing(): Unit = ???
  }

  class Guitarist extends Instrumentalist {
    override def play(): Unit = println("Something guitar cool")
  }

  val ericC = new Guitarist with Singer {
    override def sing(): Unit = ??? // This is fine because Guitarist fulfills the constraint set out in the trait Singer, as Guitarist extends Instrumentalist
  }

  // we don't simply do trait Singer extends Instrumentalist, because we want to mantain the separation of two concepts (Singer and Instrumentalist), but for our specific use case here we want to force the constraint

  // self types are often compared to inheritance

  class A
  class B extends A // here all instances of B must be an A. B IS AN A

  trait T
  trait S { self: T =>
  } // we're saying here that S REQUIRES T. NOT that S is na A

  // self-types are used in cake pattern (in Java named "dependency injection". A layer of abstraction.

  // depency injection example below

  class Component {
    // API
  }

  class ComponentA extends Component
  class ComponentB extends Component // either of these two classes can be injected as the component parameter in DependentComponent. This is injected at run-time
  class DependentComponent(val component: Component)

  // In dependency injection the framework makes sure that the values are injected at run time.


// CAKE PATTERN
  trait ScalaComponent {
  //API

  def action(x: Int): String
}
  trait ScalaDependentComponent {
    self: ScalaComponent => // whoever implements the ScalaDependentComponenet trait must also implement the trait for the ScalaComponent
def dependentAction(x: Int): String = action(x) + "this rocks!" // this lets you call on action method as the compiler knows you need to implement ScalaComponent
  }

  trait ScalaApplication { self: ScalaDependentComponent =>
  }

  // layer 1 example - small components
  trait Picture extends ScalaComponent
  trait Stats extends ScalaComponent // each of these components that have their own API

  // layer 2 - compose
  trait Profile extends ScalaDependentComponent with Picture
  trait Analytics extends ScalaDependentComponent with Stats

  // layer 3
  trait AnalyticsApp extends ScalaApplication with Analytics

  // CAKE PATTERN the values of the dpeendencies are checked at compile time NOT runtime like dependency injection

  // cyclical dependencies

  // these won't compile because they are cyclically dependent, but with self types below they are possible
//  class X extends Y
//  class Y extends X

  trait X { self: Y => }
  trait Y { self: X => } // we're saying these are unrelated concepts, but they go hand in hand. Whoever implents X needs to implement Y.

}
