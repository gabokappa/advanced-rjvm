package lectures.part3concurrency

object ThreadCommunication extends App {

  /*
  The producer-consumer problem
  producer -> [ x ] sets the value in the container
  [ x ] -> consumer which sole purpose is to extract what is in the container. The issue is these operate in parallel and run at the same time
  we don't when eachother have finished working. We usually have to force the consumer to wait until the producer finishes the job.

  This is often a case that we have to wait for something. Most problems are producer -> consumer problem

   */

  class SimpleContainer {
    private var value: Int = 0

    def isEmtpy: Boolean = value == 0
    def set(newValue: Int) = value = newValue // this is the setter or the producing method
    def get = { // a getter method this is the consuming method
      val result = value
      value = 0
      result
    }
  }

  def naiveProdCons(): Unit = {
    val container = new SimpleContainer
    val consumer = new Thread(() => {
      println("[consumer] waiting...")
      while (container.isEmtpy) {
        println("[consumer] actively waiting...")
      }

      println("[consumer] has consumed " + container.get)
    })

    val producer = new Thread(() => {
      println("[producer] computing...")
      Thread.sleep(500)
      val value = 42
      println("[producer] I have produced this value: " + value)
      container.set(value)
    })

    consumer.start()
    producer.start()

  }

 // naiveProdCons() this just creating busy waiting

  // wait and notify

  // using wait and waiting on the calling monitor suspends the thread indefinitely
  // notify signals to one of the sleeping threads that they may continue but have no control which thread but they hold the lock
  // notifyAll notifies all the threads. THESE ARE ONLY USED IN SYNchRONISED expression


  def smartProdCons(): Unit = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("[consumer waiting...")
      container.synchronized { // locks it in
        container.wait() // this makes it waiting, the producer thread will release the lock
      }
      // at this point the container must have a value, the only person that can wake up from this calue is the producer
      println("[consumer] I have consumer " + container.get)
    })

    val producer = new Thread(() => {
      println("[producer] working")
      Thread.sleep(2000)
      val value = 42

      container.synchronized {
        println("producer producing" + value)
        container.set(value)
        container.notify()
      }
    })
    consumer.start()
    producer.start()

  }

  smartProdCons()

}
