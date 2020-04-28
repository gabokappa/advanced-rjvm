package exercises.part1as

object PartialFunctionEx extends App {

  val aManualFussyFunction = new PartialFunction[Int, Int] {

    override def apply(v1: Int): Int = v1 match {
      case 1 => 42
      case 2 => 65
      case 5 => 999
    }

    override def isDefinedAt(x: Int): Boolean =
      x == 1 || x == 2 || x == 5

    // the above are required methods for a partial function. These are the charateristic methods of a partial function Scala provide the syntactic sugar for this
  }

  // the below line could be used as val chattybot: String => String = { ....and the rest

  val chattybot: PartialFunction[String, String] = {
    case "hello" => "Hi, my name is HAL"
    case "goodbye" => "You will never leave"
    case "call for help" => "I am all the help you need"
    case _ => "Speak normal please"
  }

// old line  scala.io.Source.stdin.getLines().foreach(line => println("chatbot says: " + chattybot(line)))

  scala.io.Source.stdin.getLines().map(chattybot).foreach(x => println(s"Computer says: $x"))

}
