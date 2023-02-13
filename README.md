# neow3j Examples

This repository contains Java examples for [neow3j](https://github.com/neow3j/neow3j) for the Neo N3 and the Neo Legacy blockchain.
Its goal is to give you a better understanding on how to use neow3j to develop dApps and smart contracts for Neo.

The **master** branch contains neow3j examples for the **Neo N3** blockchain.

If you seek examples for the **Neo Legacy** blockchain, you can find those in the according branches: `neo-legacy/examples`,
`neo-legacy/android-examples` and `neo-legacy/spring-boot-examples`.

## Requirements

At least Java 8 is required.

Additionally, many examples will require a Neo network. We
recommend using [*Neo Express*](https://github.com/neo-project/neo-express) for that purpose. It
is integrated into the [*Neo Blockchain Toolkit*](https://marketplace.visualstudio.com/items?itemName=ngd-seattle.neo-blockchain-toolkit)
that is available on the VSCode Extension Marketplace. 

All examples are using [*Gradle*](https://gradle.org/) as the build tool and provide a Gradle
wrapper. So, you don't necessarily need Gradle installed on your machine.

## Neo N3 examples

The project in the master branch provides Java examples for the neow3j SDK and devpack based on Neo N3.

It contains examples for the following use cases:

- Smart Contract Development
- Smart Contract Compilation and Deployment
- JSON RPC calls to interact with the Neo node
- Credentials and Wallet related examples

**Visit [neow3j.io](https://neow3j.io) for more information and technical documentation.**

**Important:** The provided example contracts are not audited nor tested and merely provide example implementations 
about how to use neow3j for contract development. When developing contracts for production on mainnet, make sure to 
extensively test them and desirably obtain an audit for them.

## Instructions

We recommend using [*Visual Studio Code*](https://code.visualstudio.com/) (VSCode) for trying
these examples. You can then make use of the [*Neo Blockchain
Toolkit*](https://marketplace.visualstudio.com/items?itemName=ngd-seattle.neo-blockchain-toolkit)
which gives you the best developer experience with Neo. Checkout their [Quickstart
Guide](https://github.com/neo-project/neo-blockchain-toolkit/blob/master/quickstart.md) for how
to set it up.

With VSCode you should install the [Java Extension
Pack](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) and the
[Gradle Extension
Pack](https://marketplace.visualstudio.com/items?itemName=richardwillis.vscode-gradle-extension-pack) for Java and Gradle support.

These examples are accompanied by code tours. To make use of them install the VSCode [*Code
Tours*](https://marketplace.visualstudio.com/items?itemName=vsls-contrib.codetour) extension.

Once you have VSCode setup, clone the whole examples repository and open this directory with
VSCode. Now you can build the project with `./gradlew build`. Some examples don't depend on a
running Neo network, those can be executed immediately, e.g., via the `Run` buttons above the
`main` methods. All other examples will require a running Neo network. Setting this up with the
help of the *Neo Blockchain Toolkit* is explained in the code tours.

## Contact

For questions, issues, or suggestions, please create an issue [here](https://github.com/neow3j/neow3j-examples-java/issues).
