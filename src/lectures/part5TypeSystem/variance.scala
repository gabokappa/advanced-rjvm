package lectures.part5TypeSystem

object variance extends App {

  trait Animal
  class Dog extends Animal
  class Cat extends Animal
  class Crocodile extends Animal

  // what is variance, is the problem of "inheritance" - type substition of generics

  class Cage[T]
  // covariance Cat can inherit from Animal you go from the generic to the specific
  class CCage[+T]
  val ccage: CCage[Animal] = new CCage[Cat]

  // invariance has to be the same type so the below code doesn't compile
  class ICage[T]
//  val icage: CCage[Animal] = new ICage[Cat] // this doesn't compile

  // contravariance put a minus sign near contra

  class XCage[-T] // the type substitution works in the opposite way. Replace the specific (Cat) for a generic (Animal) if the right-handside value can contain a Cat then it can be a Cat.
  val xcage: XCage[Cat] = new XCage[Animal]

  class InvariantCage[T](val animal: T) // invariant

  // covariant positions
  class CovariantCage[+T](val animal: T) // the val: animal is in a COVARIANT POSITION the generic type of vals. Covariant positions also invariant types

//  class ContraVariantCage[-T](val animal: T) // the compiler complaints because contravriant type is occurng in the covariant position of val animal: T
  /*
  val catCage: XCage[Cat] = new XCage[Animal](new Crocodile) This is why it can't work
   */

  // class CovariantVariableCage[+T](var animal: T) // types of vars here are called in CONTRAVARIANT POSITION why it doesn't compile.

  /* hy this cn't work
  val ccage: CCage[Animal] = new CCage[Cat](new Cat)
  ccage.animal = new Crocodile
   */

//  class ContravariantVariableCage[-T](var animal: T) //also the variable here is in a COVARIANT POSITION

  // The only acceptable type is Inavariant!

  class InvariantVariableCage[T](var animal: T)

//  trait AnotherCovariantCage[+T] {
//    def addAnimal(animal: T) // method argument is in a CONTRAVARIANT POSITION
//  }

  /*
  it doesnt compile
  val ccage: CCage[Animal] = new CCage[Dog]
  ccage.add(new Cat) so the compiler prevents from adding methods which have a covariant type
     */

  class AnotherContraVariantCage[-T] {
    def addAnimal(animal: T) = true
  }
  val acc: AnotherContraVariantCage[Cat] = new AnotherContraVariantCage[Animal]
  acc.addAnimal(new Cat)
  class Kitty extends Cat
  acc.addAnimal(new Kitty)


  class MyList[+A] {
    def add[B >: A](element: B): MyList[B] = new MyList[B] // this is widening the type
  }

  val emptyList = new MyList[Kitty]
  val animals = emptyList.add(new Kitty) // this returns a list of Kitty
  val moreAnimals = animals.add(new Cat) // this returns a list of Cat as Kitty extends Cat
  val evenMoreAnimals = moreAnimals.add(new Dog) // because Dog is an Animal. Animal is the next lowest ancestor of both Cat and Dog because of the [B >: A] in the add method this is now returning a list of Animals
  // The goal of this is that all the elements have a common Type. This is the reason for Type collections. The compiler widens the type to keep the property true

  // METHODS ARGUMENTS ARE IN CONTRAVARIANT POSITION

  // return types

  class PetShop[-T] {
    // def get(isItaPuppy: Boolean): T // METHOD RETURN TYPES ARE IN COVARIANT POSITION to solve this problem need to return a new type which is a sub-type of -T

    def get[S <: T](isItaPuppy: Boolean, defaultAnimal: S): S = defaultAnimal
  }

  val shop: PetShop[Dog] = new PetShop[Animal]
  // val evilCat = shop.get(true, new Cat) ilegal Call
  class TerraNova extends Dog
  val bigFurry = shop.get(true, new TerraNova)

  /*
  PAY ATTENTION
  - method arguments are in CONTRAVARIANT position
  - return types are in COVARIANT position
   */

  /*
   EXERCISE parking application

  1) Invariant, covariant, contravariant
   Parking[T](things List[T])
   park(vehicle: T)
   impound(vehicles: List[T])
   checkVehicles(conditions: String): List[T]

   2) Use someone else's API: IList[T]

   3) Parking = monad!
   - flatMap effort
  */

  class Vehicle
  class Bike extends Vehicle
  class Car extends Vehicle
  class IList[T]

  // INVARIANT PARKING

  class IParking[T](vehicles: List[T]) {
    def park(vehicle: T): IParking[T] = ???
    def impound(vehicles: List[T]): IParking[T] = ???
    def checkVehicles(conditions: String): List[T] = ???

    def flatMap[S](f: T => IParking[S]): IParking[S] = ???
  }

// COVARIANT PARKING

  class CParking[+T](vehicles: List[T]) {
    def park[S >: T](vehicle: S): CParking[S] = ???
    def impound[S >: T](vehicles: List[S]): CParking[S] = ???
    def checkVehicles(conditions: String): List[T] = ???

    def flatMap[S](f: T => CParking[S]): CParking[S] = ???

  }

  // CONTRAVARIANT PARKING
  class XParking[-T](vehicles: List[T]) {
    def park(vehicle: T): XParking[T] = ???
    def impound(vehicles: List[T]): XParking[T] = ???
    def checkVehicles[S <: T](conditions: String): List[S] = ???

    def flatMap[R <: T, S](f: R => XParking[S]): XParking[S] = ???

  }
/* Rules
- use covariance = COLLECTION OF THINGS
- use contravariance - GROUP OF ACTIONS
 */

  // 2 use someone else's API should be covariant and contravariant options

  // COVARIANT PARKING

  class CParking2[+T](vehicles: List[T]) {
    def park[S >: T](vehicle: S): CParking2[S] = ???
    def impound[S >: T](vehicles: IList[S]): CParking2[S] = ???
    def checkVehicles[S >: T](conditions: String): IList[S] = ???
  }

  // CONTRAVARIANT PARKING
  class XParking2[-T](vehicles: IList[T]) {
    def park(vehicle: T): XParking2[T] = ???
    def impound[S <: T](vehicles: IList[S]): XParking2[S] = ???
    def checkVehicles[S <: T](conditions: String): List[S] = ???

  }

  // 3 flatmap



}
