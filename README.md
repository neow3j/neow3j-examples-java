# neow3j Examples

This git repo contains a Java project with some examples on how to use [neow3j](https://github.com/neow3j/neow3j).

## Requirements

* Java 1.8

## Clone the Project

```
$ git clone https://github.com/neow3j/neow3j-examples.git
```

## Open the project in IntelliJ

Go to `File` -> `New` -> `Project from Existing Sources` -> and select the
directory you cloned the `neow3j-examples` repo, i.e., where the `build.gradle` is located.

In the "Import Project" window, select "Gradle" and press "Next".

Then, in the next window, mark the "Use auto-import" checkbox. Leave the
radio box "Use default Gradle wrapper (recommended)" selected. The "Gradle JVM"
option should show your local Java 1.8 JDK. Press "Finish" to finalize the
project import.

Once the project is opened in IntelliJ, go to `src/main/java` directory.
There you can find the `io.neow3j.examples` package with other sub-packages in it.
That's where the examples are located.

For each example, a `main()` method is found. If you want to run
the example, you can select the `main()` method, right click on it,
and select "Run".

## Running examples on the command line

First, build the whole project:

```
$ ./gradlew clean build
```

Then, you can execute single examples with the following command:

```
$ java -cp build/libs/examples-1.0-SNAPSHOT.jar <CLASS_NAME>
```

where `<CLASS_NAME>` is the package + the class name.

For example, to run the `CreateKeyPair` class, then run:

```
$ java -cp build/libs/examples-1.0-SNAPSHOT-all.jar io.neow3j.examples.keys.CreateKeyPair
```

## Contact

For questions, issues, or suggestions, please create an issue [here](https://github.com/neow3j/neow3j/issues).
