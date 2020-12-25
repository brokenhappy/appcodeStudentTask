# appcodeStudentTask
This repository houses the code that I wrote to prove JetBrains what I can do.
##introduction

I'd like to thank TornadoFx for allowing my (not so great) frontend code to be readable and Dagger 2 for their great 
compile time dependency injection implementation.

I'm Wout Werkman, at the time of writing, a 23-year-old grad student... Who am I kidding, lets talk code!

Every package outside the `view` package was written using the TDD doctrine. Test names must **only** show behaviour,
test code shows how **only the named** behaviour is asserted. There is no strict application of 
AAA (Arrange, act, assert), instead tested behaviour must **always** be a small scope.

Every package (but `view`) has an interface explaining the behaviour of the package content, and the implementations 
are named after how the interface is implemented. Then there also is a `Component` which exposes the necessary
behaviour.

Please note that these conventions I chose are not based on a strong opinion, this is my first time using DI on JVM, and
I am very willing to receive feedback and change my style for the better.

## installation
Preconditions of running this code are the following:
 - [IntelliJ IDEA](https://www.jetbrains.com/idea/download) is installed (Confirmed on 2020.3) 
 - [kotlinc](https://kotlinlang.org/docs/tutorials/command-line.html) is installed (Its version will determine the executed script version)
 - `kotlinc` has to be a variable. It MUST be able to be executed as a command (example: `kotlinc -version`)

Below the steps to run the tests and the code are given.
 - Open IntelliJ and pull this repository
 - Run all tests.
    - If the tests can't run, go to `Preferences | Build, Execution, Deployment | Build Tools | Gradle` and select
      run tests using: `IntelliJ IDEA`
    - If the `KotlinScriptExecutor` tests fail, the `kotlinc` variable might not work for `ProcessBuilder` commands yet.
      Rebooting the machine did the trick for me
 - Run the `Main` method using the default runner. TornadoFx runner will **NOT** work
