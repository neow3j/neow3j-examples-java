# neow3j Examples for Neo N3

This project provides Java examples for the neow3j SDK and devpack based on Neo N3.

It contains examples for the following use cases:

- Smart Contract Development
- Smart Contract Compilation and Deployment
- JSON RPC calls to interact with the Neo node
- Credentials and Wallet related examples

## Instructions

We recommend using [*Virtual Studio Code*](https://code.visualstudio.com/) (VSCode) for trying
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