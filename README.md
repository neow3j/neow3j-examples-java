# neow3j - Examples for Neo Legacy

This project contains example usages of neow3j with the Neo Legacy blockchain.

It contains:

- Examples of JSON RPC calls to interact with the Neo node
- ECC key related examples
- Transaction related examples with specific NEP-5 transfers
- And several Wallet and Account related examples


## Instructions

You can run the examples either via the built-in funtionality of your IDE or the command line.
To run the examples via the command line, first, go to the root of the project and build the
project:

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
