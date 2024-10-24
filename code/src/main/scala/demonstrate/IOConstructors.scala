package demonstrate

import cats.effect.*

object IOConstructors extends IOApp.Simple {
    // Create a counter to help us see when evaluation happens
    var counter = 0

    // This function has a side effect of incrementing the counter
    def getNext(): Int = {
        counter += 1
        counter
    }

    // Create two IOs, one with IO.apply and one with IO.pure
    val effectfulIO = IO(getNext()) // Evaluates getNext() each time the IO runs
    val pureIO = IO.pure(getNext()) // Evaluates getNext() once when creating the IO

    // A program that demonstrates the difference between IO and IO.pure
    val run = for {
        // First round
        _ <- IO.println("First round:")
        v1 <- effectfulIO
        _ <- IO.println(s"effectfulIO value: $v1")
        v2 <- pureIO
        _ <- IO.println(s"pureIO value: $v2")

        // Second round
        _ <- IO.println("\nSecond round:")
        v3 <- effectfulIO
        _ <- IO.println(s"effectfulIO value: $v3")
        v4 <- pureIO
        _ <- IO.println(s"pureIO value: $v4")
    } yield ()
}
