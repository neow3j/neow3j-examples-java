# neow3j Examples

This repository contains examples for [neow3j](https://github.com/neow3j/neow3j). Its goal is to give you as a blockchain developer a better understanding on how to use neow3j to develop dApps and smart contracts for Neo. Each folder in this repository contains a Gradle project. The prefix in the folder name designates if the contained examples are based on neow3j for Neo2 or neow3j for Neo3. 
The Neo2 examples are based on neow3j 2.4.0 and the Neo3 examples are based on neow3j 3.+ (always the latest version).

## Requirements

- Java 1.8
- A Neo network, e.g., a privatenet, which you can get [here](https://github.com/axlabs/neo2-privatenet-openwallet-docker) for Neo2 or [here](https://github.com/axlabs/neo3-privatenet-docker) for Neo3.

## Usage Instructions

Clone the repository:

```
$ git clone https://github.com/neow3j/neow3j-examples-java.git
```

Open one of the Gradle projects in your favorite IDE or editor. Each example in the project has its own main method an can be executed independetly. The names of the packages and classes hint what the example is about. The code comments give more information.

You can run the examples either with the built-in funtionality of your IDE or the command line. To run the examples via the command line, first, go to the root of the project (e.g. `/neo3-examples`) and build the project:

```
$ ./gradlew clean build
```

Then, you can execute each example separately with the following command:

```
$ java -cp build/libs/examples-1.0-SNAPSHOT.jar <CLASS_NAME>
```

Where `<CLASS_NAME>` is the fully qualified class name of the example you wish to run.

For example, to run the `CreateKeyPair` class:

```
$ java -cp build/libs/examples-1.0-SNAPSHOT-all.jar io.neow3j.examples.keys.CreateKeyPair
```

**Spring Boot Example**

To run the Spring boot Example in *neo2-examples-spring-boot*, either use the built-in features of your IDE to run Spring Boot applications or use the command line as follows. 

Run the following command in the root of the *neo2-examples-spring-boot* project.

```
./gradlew bootRun
```

Open a new terminal window or tab and from where you can make two API calls implemented by this demo application.

- Get the balance of an address (i.e. here: AK2nJJpJr6o664CWJKi1QRXjqeic2zRp8y):

```
curl --request GET 'http://127.0.0.1:8080/balance?address=AK2nJJpJr6o664CWJKi1QRXjqeic2zRp8y'
```

- Make a payment of 10 neo to an address (i.e. here: AJmjUqf1jDenxYpuNS4i2NxD9FQYieDpBF):

```
curl --request POST 'http://127.0.0.1:8080/pay?amount=10&address=AJmjUqf1jDenxYpuNS4i2NxD9FQYieDpBF'
```

(The wallet created in the application contains the private key for the address AK2nJJpJr6o664CWJKi1QRXjqeic2zRp8y. The payments are done from this address.)

## Contact

For questions, issues, or suggestions, please create an issue [here](https://github.com/neow3j/neow3j/issues).
