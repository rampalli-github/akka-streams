package sample

import akka.actor.ActorSystem
import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.typesafe.config.ConfigFactory
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object SampleStreams extends App{

  implicit val system = ActorSystem("Test",ConfigFactory.load())

  implicit val materializer = ActorFlowMaterializer()

  def streamIterator = Stream.from(1).iterator

  iterateSrc(new IteratorWrapper(streamIterator))

  def iterateSrc(iter: Iterator[Int]): Unit = {
    val src = Source(() => iter)

    val future = src.take(10).runWith(Sink.foreach(f => println("Int: "+f)))

    future.onComplete {
      case Success(x) => println("Complete")
      case Failure(t) => println(t.getMessage)
    }
  }
}

class IteratorWrapper[T](iter: Iterator[T]) extends Iterator[T]{

  val internalIterator = Stream.from(1).iterator

  override def hasNext: Boolean = {
    println("HasNext Called: "+internalIterator.next())
    iter.hasNext
  }

  override def next(): T = iter.next()
}
