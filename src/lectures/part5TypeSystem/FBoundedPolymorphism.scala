package lectures.part5TypeSystem

class FBoundedPolymorphism extends App {

  // How to force a method in the super type to accept a current type

//  trait Animal {
//    def breed: List[Animal]
//  }
//
//  class Cat extends Animal {
//    override def breed: List [Animal] = ??? // List[Cat]!!
//  }
//
//  class Dog extends Animal {
//    override def breed: List [Animal] = ??? // List[Dog]!!
//  }

  // How do we make the compiler to force the correct version So that each override returns a specific list, enforced by the compiler, not manually introduced by the user

//  trait Animal {
//    def breed: List[Animal]
//  }
//
//  // Solution 1
//  // The below works because List is covariant and Cat and Dog extends Animal
//  class Cat extends Animal {
//    override def breed: List [Cat] = ??? // List[Cat]!!
//  }
//
//  class Dog extends Animal {
//    override def breed: List [Cat] = ??? // List[Dog]!!
//  }

  // SOLUTION 2 - F Bounded Poymorphism

//  trait Animal[A <: Animal[A]] { // recursive type upper bound. This is called F-Bounded Polymorphism
//    def breed: List[Animal[A]]
//  }
//
//  class Cat extends Animal[Cat] {
//    override def breed: List [Animal[Cat]] = ??? // List[Cat]!!
//  }
//
//  class Dog extends Animal[Dog] {
//    override def breed: List [Animal[Dog]] = ??? // List[Dog]!!
//  }
//
//  trait Entity[E <: Entity[E]] // used a lot in databases, used in object-relational mapping
//  class Person extends Comparable[Person] { // Another example of F-Bounded Polymorphism
//    override def compareTo(o: Person): Int = ???
//  }
//
//  class Crocodile extends Animal[Dog] {
//    override def breed: List[Animal[Dog]] = ??? // HOW TO GET THE COMPILER TO ENFORCE THAT THE CLASS IM DEFINING AND THE TYPE A IM ANNOTATING WITH ARE THE SAME?
//  }

  // Solution 3 - FBP (F-Bounded Polymorphism) in conjuction with self types

//  trait Animal[A <: Animal[A]] { self: A =>
//    def breed: List[Animal[A]]
//  }
//
//  class Cat extends Animal[Cat] {
//    override def breed: List [Animal[Cat]] = ??? // List[Cat]!!
//  }
//
//  class Dog extends Animal[Dog] {
//    override def breed: List [Animal[Dog]] = ??? // List[Dog]!!
//  }
//
//  trait Fish extends Animal[Fish]
//
//  class Shark extends Fish {
//    override def breed: List[Animal[Fish]] = List(new Cod) // here is the fundamental limitation to FBP because Cod is a fish, this is allowed
//  }
//
//  class Cod extends Fish {
//    override def breed: List[Animal[Fish]] = ???
//  }

  // Solution 4 type classes

//  trait Animal
//  trait CanBreed[A] {
//    def breed(a: A): List[A] // we enforce the method signature directly from the type parameter
//  }
//
//  class Dog extends Animal
//  object Dog {
//    implicit object DogsCanBreed extends CanBreed[Dog] {
//      def breed(a: Dog): List[Dog] = List()
//    }
//  }
//
//  implicit class CanBreedOps[A](animal: A) {
//    def breed(implicit canBreed: CanBreed[A]): List[A] =
//      canBreed.breed(animal)
//  }
//
//  val dog = new Dog
//  dog.breed // This returns a list of dogs breed is an implicit method
  /*
  new canBreedOps[Dog](dog).breed(Dog.DogsCanBreed // breed takes an implicit paramater CanBreed, which is the implicit object in Dog? DogCanBreed

  implicit value to pass to breed is Dog.DogsCanBreed

   */

  // THIS IS WRONG

//  class Cat extends Animal
//  object Cat {
//    implicit object CatsCanBreed extends CanBreed[Dog] {
//      def breed(a: Dog): List[Dog] = List()
//    }
//  }
//
//  val cat = new Cat
//  cat.breed

  // Solution 5

  trait Animal[A] { // pure type classes. Animal is the type class itself
    def breed(a: A): List[A]
  }

  class Dog
  object Dog {
    implicit object DogAnimal extends Animal[Dog] {
      override def breed(a: Dog): List[Dog] = List()
    }
  }

  implicit class AnimalOps[A](animal: A) {
    def breed(implicit animalTypeClassInstance: Animal[A]): List[A] =
      animalTypeClassInstance.breed(animal)
  }

  val dog = new Dog
  dog.breed

}
