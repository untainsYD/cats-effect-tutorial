/*
 * Copyright 2024 Creative Scala
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package introduction

import cats.effect.*

// The goal of these exercises is to introduce some of the basics of using IO:
//
// - constructing IO
// - doing one thing after another (sequencing)
// - combining multiple independent IOs
//
// and additionally get to the fundamental design principle of IO: the
// separation between descrptions and action
object Basics extends IOApp.Simple {
    // 0. This object extends IOApp.Simple. This allows us to run the object. It
    // expects an IO with the name `run` which describes the program it will run.
    // So replace `run` below with whatever you want to see when your program is
    // run.
    // val run = IO(println("Hello!"))
    //val runPure = IO.pure(println("Hello IO pure!"))

    // 1. You can create an IO using IO(...) and IO.pure(...). (The former is the
    // apply method on the IO companion object, so can also be written as
    // IO.apply(...)
    //
    // What is the difference between these methods? Can you write a program that
    // demonstrates the difference?

    // Solution task 1
    val pureIO = IO.pure(println("Hello 1")) // evaluates println immediately
    val effectfulIO = IO(println("Hello 2")) // defers println until execution

    // IO/IO apply evaluates it each time the IO is run
    // IO.pure evaluates its argument once, and always returns the same value, and IO.pure guarantees error handling

    // 2. flatMap allows us to do one thing after another, where the thing we do
    // second depends on the result of the thing we do first.
    //
    // IO.realTime gives the time since the start of the epoch. Using flatMap
    // write a program that gets the time and prints it on the console.

    // Solution task 2
    // map:    (A => B) => IO[B] - map transforms the value inside the IO without adding another layer of IO
    // flatMap: (A => IO[B]) => IO[B] - flatMap transforms the value into another IO and flattens the result
    val runMap = IO.realTime.map(time => s"map: current time since epoch: $time")
    val runFlatMap = IO.realTime.flatMap(time => IO.println(s"flatMap: current time since epoch: $time"))

    // More examples
    val simpleStringTransformation = IO("simple transformation").map(str => str.toUpperCase) // simple transformation
    val simpleTimeTransformation = IO.realTime.map(time => s"The time is $time") // transforms time to a string

    val effectStringTransformation = IO("effect producing transformation").flatMap(str => IO(str.toUpperCase))
    val effectTimeTransformation = IO.realTime.flatMap(time => IO.println(s"The time is $time"))

    // 3. mapN allows us to combine two or more IOs that don't depend on each
    // other. Here's an example:
    //
    // (IO(1), IO(2)).mapN((x, y) => x + y)
    //
    // When this program is run, are the IOs always evaluated in a particular
    // order (e.g. left to right) or is it non-deterministic?

    // 4. The following program attempts to add logging before and after an IO
    // runs. Does it do this correctly?
    // def log[A](io: IO[A]): Unit = {
    //     println("Starting the IO")

    //     val _ = io

    //     println("Ending the IO")
    // }

    // 5. Write a method that adds logging before and after an IO
    // Here's an example of how to use it:
    //
    // log(IO.realTime).flatMap(time => IO.println(s"The time is $time"))

    // 6. What do the *> and <* methods do? Could you use them in the logging
    // method you just wrote?

    val run = for {
        // Testing pureIO vs IO/IO apply
        _ <- IO.println("\n=== Testing pureIO vs effectfulIO ===")
        _ <- effectfulIO
        _ <- pureIO
        _ <- effectfulIO
        _ <- pureIO

        // Testing map vs flatMap with realTime
        _ <- IO.println("\n=== Testing map vs flatMap with realTime ===")
        _ <- runMap
        _ <- runFlatMap

        // Testing simple transformations
        _ <- IO.println("\n=== Testing simple transformations ===")
        upperStr <- simpleStringTransformation
        _ <- IO.println(s"Simple transformation result: $upperStr")
        timeStr <- simpleTimeTransformation
        _ <- IO.println(s"Time string transformation: $timeStr")

        // Testing effect producing transformations
        _ <- IO.println("\n=== Testing effect producing transformations ===")
        upperEffect <- effectStringTransformation
        _ <- IO.println(s"Effect producing transformation result: $upperEffect")
        _ <- effectTimeTransformation
    } yield ()
}
