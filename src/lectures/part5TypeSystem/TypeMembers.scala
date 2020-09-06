package lectures.part5TypeSystem

object TypeMembers extends App {

  class Animal
  class Dog extends Animal
  class Cat extends Animal

  class AnimalCollection {
    type AnimalType // an abstract type member
    type BoundedAnimal <: Animal // this is upper bounded in Animal which must extend Animal
    type SuperBoundedAnimal >: Dog <: Animal // lower bounded by Dog, but upper bounded by Animal
    type AnimalC = Cat // this is an alias for an exsiting type. This is mostly used to help the compiler do type inference
  }

  val ac = new AnimalCollection
  val dog: ac.AnimalType = ???

  // the compiled allows to create a dod

  val pup: ac.SuperBoundedAnimal = new Dog // the SuperBoundedAnimal is some super type of dog
  val cat: ac.AnimalC = new Cat // this is fine because the compiler equates AnimalC as a Cat

  type CatAlias = Cat
  val anotherCat: CatAlias = new Cat

  // abstract type members are sometimes used in api that look simillar to generics

  // an alternative to generics

  trait MyList {
    type T
    def add(element: T): MyList
  }

  class NonEmptyList(value: Int) extends MyList {
    override type T = Int
    def add(element: Int): MyList = ???
  }

  // .type can use some values type as a type alias

  type CatsType = cat.type // this is a type alias

  // val newCat: CatsType = cat // not sure why this doesn't compile

  /*
  Exercise - enforce a type to be applicable to SOME TYPES only
   */

  // this is locked and can't be changed
  trait MList {
    type A
    def head: A
    def tail: MList
  }

  // THIS IS NOT OK as far as th exercise is concerned

  class CustomList(hd: String, tl: CustomList) extends MList {
    type A = String
    def head: String = hd
    def tail = tl
  }

  // OK

  class IntList(hd: Int, tl: IntList) extends MList {
    type A = Int
    def head = hd
    def tail = tl
  }

  // need the Number type use type member and type member constraints(bounds)

}
