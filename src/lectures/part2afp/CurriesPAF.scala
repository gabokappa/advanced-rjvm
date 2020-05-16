package lectures.part2afp

object CurriesPAF extends App {

  // curried functions. the below is a function that takes an Int and returns another function that takes an Int and returns an Int
  val superAdder: Int => Int => Int =
    x => y => x + y

  // auxillary functions
  val add3 = superAdder(3) // this is a function Int => Int = y => 3 + y
  println(s"printing add3 on it's own $add3")
  println(s"passing a number so add3(5) ${add3(5)}")
  println(s"this should also return eight: ${superAdder(3)(5)}") // curried function as it receive multiple parameter lists

  // a method!
  def curriedAdder (x: Int)(y: Int): Int = x + y // curried method
      // to use the with one parameter list
  val add4: Int => Int = curriedAdder(4) // this returns the function remaining after the first parameter list so (y: Int): Int = 4 + y. This doesn't work without type annotation.
  // try removing the type as the compile gets confused. The compiler figures out I want the remainder of the function
  // add4 is a FUNCTION VALUE!. A method converted into a function value. We want to use function value in HOFs because we can't use defs.

  // this is atually lifting. When we call a method need to pass all the parameter lists.

  // lifting = ETA-EXPANSION
  // function != methods (jvm limitation)

  def inc(x: Int): Int = x + 1
  List(1, 2, 3).map(inc) // the compiler actually converts this to .map(x => inc(x))

  // Partial function aplications

  val add5 = curriedAdder(5) _ // The underscore tells the compiler to do an ETA expansion for me and turn curriedAdder into a function value after you apply the first parameter list.
  // this becomes Int => Int function the add5

  // EXERCISE
  val simpleAddFunction = (x: Int, y: Int) => x + y
  def simpleAddMethod(x: Int, y: Int): Int = x + y
  def curriedAddMethod(x: Int)(y: Int): Int = x + y


  // define an add7 function value whose implementation is add7: Int => Int = y => 7 + y
  // as many differenet implementation of add7 using the above three functions.
val add7One = simpleAddFunction(7, _)
  println(s"add7One ${add7One(2)}")

  val add7Two = x => simpleAddMethod(7, x)
  println(add7Two(2))

  val add7Three = curriedAddMethod(7) _ // this is Partially applied function
  println(s"curry it ${add7Three(2)}")

  val add7Four = (x: Int) => simpleAddFunction(7, x)
  println(add7Four(3)) // simplest solution

  val add7_2 = simpleAddFunction.curried(7) // .curried creates a curried version of the function
  println(add7_2(3))
  val add7_4 = curriedAddMethod(7)(_) // Partially applied function is alternative syntax. This alternative syntax works with non curried functions

val add7_5 = simpleAddMethod(7, _: Int) // alternative syntax for turning methods into function values
  print(add7_5(3)) // this returns y => simpleAddMethod(7, y)

  val add7_6 = simpleAddFunction(7, _: Int) // this forces the compiles to turn a method or function into a function value



  // underscores are powerful
  def concatenator(a: String, b: String, c: String) = a + b + c

  val insertName = concatenator("Hello, I'm ", _, " how are you?") // can add type to the underscore so _: String
  println(insertName("Gabriel")) // the above turns into a function value x: String => concatenator(hello, x, how are you)

  val fillInTheBlanks = concatenator("Hello, ", _, _) // (x, y) => concatenator("Hello, ", x, y)
  println(fillInTheBlanks("Gabriel", " eat")) // each of these parameters are injected into the underscores above.

  // IMPORTANT DISTINCTION PARTIALLY APPLIED FUNCTIONS ARE SUPPOSED TO PASS IN PARAMETERS WHENEVER A VALUE IS AVAILABLE. PARTIAL FUNCTIONS is just the partial implementation

  // Exercises
  /* 1 Process a list of numbers and return their string representations with different formats. Use the %4.2f, %8.6f and %14.12f with a curried formatter function
  apply to a list of numbers the different formatting as a higher order function.
*/
  def curriedFormatter(s:String)(number: Double): String = s.format(number)
  val numbers = List(Math.PI, Math.E, 1, 9.8, 1.3e-12)

  val simpleFormat = curriedFormatter("%4.2f") _ // this tells the compiler to lift this method (the def curriedFormatter) into a function value
  val seriousFormat = curriedFormatter("%8.6f") _
  val preciseFormat = curriedFormatter("%14.12f") _
  // the above shows how curried functions like the curriedFOrmatter can be easily used by partially applied functions like the ones above

  println(numbers.map(simpleFormat))
  println(numbers.map(preciseFormat))
  println(numbers.map(seriousFormat))

  println(numbers.map(curriedFormatter("%14.12f"))) // this could be written as numbers.map(curriedFormatter("%14.12f") _) the underscore indicating that each
  // number in the list is the "number" parameter in the def curriedFormatter. The compiler does an eta-expansion

  /*
  2) Difference between
  -functions vs methods
  - parameters: by-name vs 0-lambda

   */


  // println("%4.2f".format(Math.PI)) // formatting functions

  def byName(n: => Int) = n + 1 // byName method should receive byName parameter
  def byFunction(f: () => Int) = f() + 1 // this is a zero lambda. And empty function that returns an Int

  def method: Int = 42 // accessor methods without parenthesis
  def parenMethod(): Int = 42 // proper methods with parenthesis

  /* calling byName and byFunction
  - int
  - method
  -parenMethod
  -lambda
  -PAF (partially applied function)
   */

  byName(23) // this is a by-name evaluated expression
  byName(method) // this ok because the method will be evaluated to its call which is 42
  byName(parenMethod()) // here byName uses the function itself parenMethod
  byName(parenMethod) // beware parenMethod is actually called here this is equivalent to byName(parenMethod()) by name here uses the value of parenMethod in which case 42, not the function itselt
 // byName(() => 42) This doesn't compile because byName argument of value type is not the same as a function parameter
  //but
  byName((() => 42)()) // here we are supplying the lambda () => 42, but we have to immediatly call it as you can see by (() => 42)() that works as it turns the lambda into a value
  // byName(parenMethod _) can't use a partially applied function like parenMethod. parenMethod _ expression is a function value is not a substitute for a byName parameter

  // byFunction(45) can't use this as byFunction expects a lambda
  // byFunction(method) the parameterless method is not ok because method here is evaluated to its value 42 it doesn't pass the function itself. Parameterless methods without parenthesis
  // like accessors or proper methods with parentheses they cannot be passed to higher order functions. No ETA-expansion here.

  byFunction(parenMethod) // the compier does ETA expansion for us
  byFunction(() => 46) // this is a lambda a function value
  byFunction(parenMethod _) // the underscore here isn't necessary









}
