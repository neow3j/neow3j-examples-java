# neow3j Examples for Neo3

This project provides Java examples for the latest version of the neow3j SDK and devpack based on Neo3.

It contains examples for the following use cases:

- Smart Contract Development
- Smart Contract Compilation and Deployment
- JSON RPC calls to interact with the Neo node
- Credentials and Wallet related examples

## Instructions

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

💪 A Neo blockchain is now running and the `main-account` is funded. The environment is ready to run
the Java examples found in this repository.


### Compile and deploy a smart contract (With the Visual Devtracker)

1. Example Java smart contracts examples are located in the
   `io.neow3j.examples.contractdev.contracts` package. To compile a contract, you can use the Gradle
   plugin that is shipped with neow3j. The `neow3jCompiler` section in the `build.gradle` file in
   the project root specifies which contract is to be compiled by the plugin. By default the
   `BongoCatToken` is set. Exchange the fully qualified class name with any other example contract
   you are interesed in.

2. To execute the compilation use one of the following options:
   1. Open the VSCode command pallette, search for "Task: Run Task" and run the "compile-contract"
      task.
   2. Run the following command from the project root.
       ```
       ./gradlew newo3jCompile
       ```
   In either way the output should show "Compilation succeeded!" and the paths to the produced
   files.
   
3. Switch to the Devtracker view and right click the `default.neo-express` blockchain that you
   started in the last section. Choose "Deploy contract" from the context menu. Choose the
   `main-account` in the appearing command pallette. Then choose the contract you compiled in the
   previous step. Any contract for which a NEF file is found in the project will show up.

After the last step the transaction hash of the deployment transaction should show up in a pop up
and the contract will have been deployed.



## Contact

For questions, issues, or suggestions, please create an issue [here](https://github.com/neow3j/neow3j/issues).
