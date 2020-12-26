# appcodeStudentTask
This repository houses the code that I wrote to prove JetBrains what I can do.

## introduction

I'd like to thank TornadoFx for allowing my (not so great) frontend code to be much more readable and Dagger 2 for their great 
compile time dependency injection implementation.

I'm Wout Werkman, at the time of writing, a 23-year-old grad student... Who am I kidding, let's talk code!

### conventions and methods

Please note that the conventions I chose are not based on a strong opinion.
I am very willing to receive feedback and change my style for the better.

Every package outside the `view` package was written using the TDD doctrine. Test names must **only** show behaviour,
test code shows how **only the named** behaviour is asserted. There is no strict application of 
AAA (Arrange, act, assert), instead tested behaviour must **always** be a small scope.

Every package (but `view`) has an interface explaining the behaviour of the package content, and the implementations 
are named after how the interface is implemented. Then there also is a `Component` which exposes the necessary
behaviour for DI.

## installation

Preconditions of running this code are the following:
 - [IntelliJ IDEA](https://www.jetbrains.com/idea/download) is installed (Confirmed on 2020.3 and 2020.1)
 - [kotlinc](https://kotlinlang.org/docs/tutorials/command-line.html) is installed (Its version will determine the executed script version)
 - Kotlin can be executed using the `kotlinc` command. (`kotlinc -version` should work)
 - Swift can be executed using the `/usr/bin/env swift` command. (`/usr/bin/env swift -version` should work)

If one of the languages does not work, the code will compile, and the app will run. However, trying to execute a language
without its dependencies correctly configured will result in an unhandled exception

Below the steps to run the tests and the code are given.
 - Open IntelliJ and pull this repository
 - Build the project.
   - If you get a JavaFx version error such as `JavaFXPlugin has been compiled by a more recent version of the Java Runtime`,
     this means that you must select a newer JDK for gradle. Go to `Preferences | Build, Execution, Deployment | Build Tools | Gradle`
     and select Gradle JVM JDK version 11 or higher (verified with 11 and 15)
 - Run all tests.
    - If the tests can't run, go to `Preferences | Build, Execution, Deployment | Build Tools | Gradle` and select
      run tests using: `IntelliJ IDEA`
    - If any the script executor tests fail, there might be no access to
 - Run the `Main` method using IntelliJ IDEA's default Kotlin runner. TornadoFx runner is **NOT** guaranteed to work
