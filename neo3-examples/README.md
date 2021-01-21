# neow3j Examples for Neo3

This project provides Java examples for the latest version of the neow3j SDK and devpack based on Neo3.

It contains examples for the following use cases:

- Smart Contract Development
- Smart Contract Compilation and Deployment
- JSON RPC calls to interact with the Neo node
- Credentials and Wallet related examples

## Instructions

These instructions assume that you have installed Virtual Studio Code, the [Visual
Devtracker](https://github.com/ngdenterprise/neo3-visual-tracker/releases) extension and
[neo-express](https://github.com/neo-project/neo-express/releases). The latter two need to be
compatible with the Neo3-preview4 release.

### Setup a blockchain and fund an account

1. Clone the examples repository and move into it
    ```
    git clone https://github.com/neow3j/neow3j-examples-java.git
    cd neow3j-examples-java/neo3-examples
    ```
2. Open the neo3-examples in Virtual Studio Code.
    ```
    code .
    ```
3. The project contains a neo-express configuration file that allows you to start a preconfigured
   Neo blockchain with the Neo Visual Devtracker VSCode extension: Switch to the Visual
   Devtracker in the Activity Bar and start the blockchain named `default.neo-express` by clicking
   on the play button shown when hovering over the name.

4. Transfer some GAS to the `main-account`, which is used in many examples: Right-click on the
   `default.neo-express` entry in the Devtracker and choose "Transfer assets". In the command
   palette choose GAS, the amount of GAS to transfer (e.g. 1000), the sending account (`genesis`)
   and the receicing account (`main-account`). After the last step, the transaction hash should
   be shown in a pop up message. 
   
5. Execute the same process of step 4 to transfer some NEO to the `main-account`.

A Neo blockchain is now running and the `main-account` is funded. The environment is ready to run
the Java examples found in this repository. 💪


### Compile and deploy a smart contract

1. Example Java smart contracts are located in the `io.neow3j.examples.contractdev.contracts`
   package. To compile a contract, you can use the Gradle plugin that is shipped with neow3j. The
   `neow3jCompiler` section in the `build.gradle` file in the project root specifies which contract 
   is to be compiled by the plugin. By default the `BongoCatToken` is set. Exchange the fully 
   qualified class name with any other example contract you are interesed in.

2. To execute the compilation use one of the following options:
   1. Open the VSCode command pallette, search for "Task: Run Task" and run the "compile-contract"
      task.
   2. Run the following command from the project root.
       ```
       ./gradlew newo3jCompile
       ```
   In either way the output should show "Compilation succeeded!" and the paths to the produced
   files.
   
3. To deploy the contract switch to the Devtracker view and right click the `default.neo-express`
    blockchain that you started in the last section. Choose "Deploy contract" from the context menu.
    Choose the `main-account` in the appearing command pallette. Then choose the contract you compiled
    in the previous step. Any contract for which a NEF file is found in the project will show up.

After the last step the transaction hash of the deployment transaction should show up in a pop up
and the contract will have been deployed. 🥳


### Invoking the deployed contract 

For contract invocations you could use the Visual Devtracker, but this repository is about showing
how to do things with neow3j. Therefore, we will use the `InvokeContract` class, which calls methods
of the `BonogoCatToken`.

The `InvokeContract` class connects to the running Neo blockchain and uses the `main-account` to
make invocations. Before we can invoke the contract deployed in the previous section, we need to
know its hash. This is achieved by using the contract's script and the account used for deployment
(lines 20-21). Then, three invocations are made. Head over to the file and inspect the code.


## Contact

For questions, issues, or suggestions, please create an issue [here](https://github.com/neow3j/neow3j/issues).
