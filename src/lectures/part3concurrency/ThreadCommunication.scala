package lectures.part3concurrency

import scala.collection.mutable
import scala.util.Random

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
      println("[consumer] waiting...")
      container.synchronized { // locks it in
        container.wait() // this makes it waiting, the producer thread will release the lock
      }
      // at this point the container must have a value, the only person that can wake up the consumer from this value is the producer
      println("[consumer] I have consumed " + container.get)
    })

    val producer = new Thread(() => {
      println("[producer] working")
      Thread.sleep(2000)
      val value = 42

      container.synchronized {
        println("[producer] produced " + value)
        container.set(value)
        container.notify()
      }
    })
    consumer.start()
    producer.start()

  }

  // smartProdCons()

  /*
  producer -> [ ?, ?, ?] - > consumer
  Both the producer and consumer may block eachother.
   */

  def prodConsLargeBuffer(): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 3

    val consumer = new Thread(() => {
      val random = new Random()

      while(true) {
        buffer.synchronized {
          if (buffer.isEmpty) {
            println("[consumer] buffer empty, waiting...")
            buffer.wait()
          }
          // there must be at least ONE value in the buffer
          val x = buffer.dequeue()
          println("[consumer] consumed " + x)

          buffer.notify() // this wakes up the producer thread to wakeup as a value has been consumed
        }
        Thread.sleep(random.nextInt(250))
      }
    })

    val producer = new Thread(() => {
      val random = new Random()
      var i = 0

      while(true) {
        buffer.synchronized {
          if (buffer.size == capacity) {
            println("producer buffer is full, waiting...")
            buffer.wait() // waiting for the consumer to wake me up
          }
          // there must be at least ONE empty space in the buffer
          println("producer producing " + i)
          buffer.enqueue(i)

          buffer.notify() // this tells the consumer thread to wake up. as now it has added a value

          i += 1
        }

        Thread.sleep(random.nextInt(250))
      }
    })
    consumer.start()
    producer.start()
  }

//  prodConsLargeBuffer()

  /*
  prod-cons level 3. Have a limited capacity buffer, but have multiples producers and multiple consumers acting on the same buffer.
  producer1 -> [ ? ? ? ?] -> consumer1
  producer2 -> --^    ^___ consumer2
   */

  class Consumer(id: Int, buffer: mutable.Queue[Int]) extends Thread {
    override def run(): Unit = {
      val random = new Random()

      while(true) {
        buffer.synchronized {
          while (buffer.isEmpty) {
            println(s"[consumer $id] buffer empty, waiting...")
            buffer.wait()
          }
          // there must be at least ONE value in the buffer
          val x = buffer.dequeue()
          println(s"[consumer $id] consumed " + x)

          buffer.notify() // this wakes up either a Cosumer thread or porducer thread
        }
        Thread.sleep(random.nextInt(500))
      }
    }
  }

  class Producer(id: Int, buffer: mutable.Queue[Int], capacity: Int) extends Thread {
    override def run(): Unit = {
      val random = new Random()
      var i = 0

      while (true) {
        buffer.synchronized {
          while (buffer.size == capacity) {
            println(s"producer$id buffer is full, waiting...")
            buffer.wait() // waiting for the consumer to wake me up
          }
          // there must be at least ONE empty space in the buffer
          println(s"producer$id producing " + i)
          buffer.enqueue(i)

          buffer.notify() // this tells the consumer or producer thread to wake up

          i += 1
        }

        Thread.sleep(random.nextInt(500))
      }
    }
  }

  def multiProdCons(nConsumers: Int, nProducers: Int): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 3

    (1 to nConsumers).foreach(i => new Consumer(i, buffer).start())
    (1 to nProducers).foreach(i => new Producer(i, buffer, capacity).start())
  }

  multiProdCons(3, 3)

}
