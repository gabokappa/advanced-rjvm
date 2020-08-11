package lectures.part4Implicits

  trait MyTypeClassTemplate[T] {
    def action(value: T): String
  }

  object MyTypeClassTemplate {
    def apply[T](implicit instance: MyTypeClassTemplate[T]) = instance
    // as this surfaces the implicit this means we have access to all the methods in the trait above?
  }