# neow3j Examples

This git repo contains two Java projects with some examples on how to use [neow3j](https://github.com/neow3j/neow3j). One project is using the current release (2.4.0) of neow3j that interacts with Neo2 and the other uses a snapshot (3.0.1-SNAPSHOT) that interacts with the upcoming Neo3 net.

You can run a Neo net locally e.g. Neo2 [here](https://github.com/axlabs/neo2-privatenet-openwallet-docker) or Neo3 [here](https://github.com/axlabs/neo3-privatenet-docker).

## Requirements

- Java 1.8
- (local running Neo net)

## Clone the Project

```
$ git clone https://github.com/neow3j/neow3j-examples-java.git
```

## Open the project in IntelliJ

Go to `File` -> `New` -> `Project from Existing Sources` -> and select the project you want to use -> the neo2-examples or neo3-examples directory, i.e., where a `build.gradle` is located.

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

**Hint:** If you are using the neo3-privatenet-docker from AxLabs for the neo3-examples, first run the `OpenWallet.java` file in the jsonrpc package.

## Running examples on the command line

To use the following commands, go to the root of the project you want to use (e.g. `{your_path_to_this_repo}/neo2-examples/`).

First, build the project:

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
