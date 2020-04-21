package exercises.part1as

object PatternMatching extends App {

  // usually when you create singleton objects for unapply methods for conditions these objects are named in lower case

  object even {
    def unapply(arg: Int): Option[Boolean] =
      if (arg % 2 == 0) Some(true)
      else None
  }

  object singleDigit {
    def unapply(arg: Int): Boolean = arg < 10 && arg > -10
  }

  val n: Int = 8
  val mathProperty = n match {
    case singleDigit() => "single digit"
    case even(_) => "an even number"
    case _ => "no property"
  }

  println(mathProperty)

  // in terms of style note how singleDigit doesn't use the option just uses a boolean
  // quick way to write tests is by writing singleton objects with unapply with a boolean
  // although this might get a bit verbose with multiple objects



}
