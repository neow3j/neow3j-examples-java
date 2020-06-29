# neow3j Examples

This git repo contains four Java projects with some examples on how to use [neow3j](https://github.com/neow3j/neow3j). Three project are using the current release (2.4.0) of neow3j that interacts with Neo2 and the `neo3-examples` uses a snapshot (3.0.1-SNAPSHOT) that interacts with the upcoming Neo3 net.

You can run a Neo net locally e.g. Neo2 [here](https://github.com/axlabs/neo2-privatenet-openwallet-docker) or Neo3 [here](https://github.com/axlabs/neo3-privatenet-docker).

## Requirements

- Java 1.8
- (local running Neo net)

## Clone the Project

```
$ git clone https://github.com/neow3j/neow3j-examples-java.git
```

## Open the project in IntelliJ

Go to `File` -> `New` -> `Project from Existing Sources` -> and select the project you want to use -> e.g. neo3-examples directory, i.e., where a `build.gradle` is located.

In the "Import Project" window, select "Gradle" and press "Next".

Then, in the next window, mark the "Use auto-import" checkbox. Leave the
radio box "Use default Gradle wrapper (recommended)" selected. The "Gradle JVM"
option should show your local Java 1.8 JDK. Press "Finish" to finalize the
project import.

#### For the `neo2-examples` and `neo3-examples` projects:
Once the project is opened in IntelliJ, go to `src/main/java` directory.
There you can find the `io.neow3j.examples` package with other sub-packages in it.
That's where the examples are located.

For each example, a `main()` method is found. If you want to run
the example, you can select the `main()` method, right click on it,
and select "Run".

**or**

*to run the examples on the command line,* first, go to the root of the project (e.g. `/neo3-examples`) and build the project:
```
$ ./gradlew clean build
```

Then, you can execute single examples with the following command:
```
$ java -cp build/libs/examples-1.0-SNAPSHOT.jar <CLASS_NAME>
```

where `<CLASS_NAME>` is the package + the class name.

For example, to run the `CreateKeyPair` class:
```
$ java -cp build/libs/examples-1.0-SNAPSHOT-all.jar io.neow3j.examples.keys.CreateKeyPair
```

**Hint:** If you are using the neo3-privatenet-docker from AxLabs for the neo3-examples, first run the `OpenWallet.java` file in the jsonrpc package.

#### For the `neo2-examples-spring-boot` project:
Open the package `io.neow3j.example.springboot`. That's where the demoApplication file is located. Right-click on it and click on `Run 'DemoApplication'` to run the application.

**or**

*to run the springboot application on the command line,* first, go to the root of the project (`/neo2-examples-spring-boot`) and build the whole project:
```
./gradlew clean build
```

Then start the Demo Application
```
./gradlew bootRun
```

Open a new terminal window or tab and there you can make API calls for the two mappings that are implemented in this example project.
* Get the balance of an address (i.e. here: AK2nJJpJr6o664CWJKi1QRXjqeic2zRp8y):
```
curl --request GET 'http://127.0.0.1:8080/balance?address=AK2nJJpJr6o664CWJKi1QRXjqeic2zRp8y'
```

* Make a payment of 10 neo to an address (i.e. here: AJmjUqf1jDenxYpuNS4i2NxD9FQYieDpBF):
```
curl --request POST 'http://127.0.0.1:8080/pay?amount=10&address=AJmjUqf1jDenxYpuNS4i2NxD9FQYieDpBF'
```
(The wallet created in the application contains the private key for the address AK2nJJpJr6o664CWJKi1QRXjqeic2zRp8y. The payments are done from this address.)

## Contact

For questions, issues, or suggestions, please create an issue [here](https://github.com/neow3j/neow3j/issues).
