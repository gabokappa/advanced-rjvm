package lectures.part4Implicits

import java.{util => ju}

object ScalaJavaConversion extends App {

import collection.JavaConverters._
  val javaSet: ju.Set[Int] = new ju.HashSet[Int]()
  (1 to 5).foreach(javaSet.add)
  println(javaSet)

  val scalaSet = javaSet.asScala

  /*
  There are bunch of vonverters interators iterable, ju.List - scala.mutable.Buffer
  ju.Set - scala.collection.mutable.Set
  ju.Ma[ - scala.collection.mutable.Map

   */

  import collection.mutable._
  val numbersBuffer = ArrayBuffer[Int](1, 2, 3)
  val juNumbersBuffer = numbersBuffer.asJava

  println(juNumbersBuffer.asScala eq numbersBuffer) // these references are equal these conversions give back the same object

  val numbers = List(1, 2, 3)
  val juNumbers = numbers.asJava
  val backToScala = juNumbers.asScala
  println(backToScala eq numbers) // will get false
  println(backToScala == numbers) // deep equals

  // juNumbers.add(7) // this is an unsupported reference as

  /*
  Scala to Java Optional -Option
   */

  class ToScala[T](value: => T) {
    def asScala: T = value
  }

  implicit def asScalaOptional[T](o: ju.Optional[T]): ToScala[Option[T]] = new ToScala[Option[T]](
    if (o.isPresent) Some(o.get) else None
  )

  val juOptional: ju.Optional[Int] = ju.Optional.of(2)
  val scalaOption = juOptional.asScala
  println(scalaOption)

}
