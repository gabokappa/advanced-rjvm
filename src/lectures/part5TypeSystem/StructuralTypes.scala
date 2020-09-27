package lectures.part5TypeSystem

object StructuralTypes extends App {

  // structural types

  type JavaCloseable = java.io.Closeable //kind of things that can close

  class HipsterCloseable {
    def close(): Unit = println("yeah yeah I'm closing")
    def closeSilently(): Unit = println("not making a sound")
  }

  // def closeQuietly(closeable: )

  type UnifiedCloseabe = {
  def close(): Unit
  } // THIS IS CALLED A STRUCTURAL TYPE

  def closeQuietly(unifiedCloseabe: UnifiedCloseabe): Unit = unifiedCloseabe.close()

  closeQuietly(new JavaCloseable {
    override def close(): Unit = ???
  })
  closeQuietly(new HipsterCloseable)

  // TYPE REFINEMENTS

  type AdvancedCloseable = JavaCloseable {
  def closeSilently(): Unit
  }

  class AdvancedJavaCloseable extends JavaCloseable {
    override def close(): Unit = println("java closes")
    def closeSilently(): Unit = println("Java closes silently")
  }

  def closeShh(advCloseable: AdvancedCloseable): Unit = advCloseable.closeSilently()

  closeShh(new AdvancedCloseable)
  // closeShh(new HipsterCloseable) This doesn't work despite having the right methods

  // using structural types as standalone types

}
