# neow3j Examples

This repository contains Java examples for [neow3j](https://github.com/neow3j/neow3j) for the Neo N3 and the Neo Legacy blockchain.
Its goal is to give you a better understanding on how to use neow3j to develop dApps and smart contracts for Neo.

The **master** branch contains neow3j examples for the **Neo N3** blockchain.

If you seek examples for the **Neo Legacy** blockchain, you can find those in the according branches: `neo-legacy/examples`,
`neo-legacy/android-examples` and `neo-legacy/spring-boot-examples`.

## Neo N3 examples

The project in the master branch provides Java examples for the neow3j SDK and devpack based on Neo N3.

It contains examples for the following use cases:

- Smart Contract Development
- Smart Contract Compilation and Deployment
- JSON RPC calls to interact with the Neo node
- Credentials and Wallet related examples

**Visit [neow3j.io](https://neow3j.io) for more information and technical documentation.**

> **Important:** The provided example contracts are not audited nor tested and merely provide example implementations
> about how to use neow3j for contract development. When developing contracts for production on mainnet, make sure to
> extensively test them and desirably obtain an audit for them.

## Development Options

You can get started in three different ways described more closely below:
- GitHub Codespaces
- Local Development
  - Run locally using a [dev container](https://containers.dev/)
  - Run locally in your machine's environment

## GitHub Codespaces

Create a codespace on the `Code` button on top of this repo's landing page. It will take a while until the codespace
is loaded and all extensions are installed. Once that process is done you'll end up on a VSCode editor in your browser
and are ready to run the examples. We've configured the repo to start a Codespace with all necessary tools
pre-configured.

<img width="430" alt="image" src="https://github.com/neow3j/neow3j-examples-java/assets/53603111/e4c3d3b6-ee9f-44fb-ae72-ab9353e3e662">

By default GitHub Codespaces opens a VSCode isntance in the browser, but, you can configure GitHub such that it opens  a
local VSCode instance connected to the codespace. This requires the GitHub Codespaces
[extension](https://marketplace.visualstudio.com/items?itemName=GitHub.codespaces) to be installed (and the
devcontainers extension, see below).

## Local Development

With the following instructions you can develop on your local machine. If you want to use dev containers, there are less requirements,
since a pre-configured Docker image is used that configures all required VSCode extensions.
See [here](https://code.visualstudio.com/docs/devcontainers/containers) for more in-depth information about it.

First, clone the repository:

```bash
git clone https://github.com/neow3j/neow3j-examples-java.git
cd neow3j-examples-java
```

Now follow the next steps below for developing with dev containers, or further below without dev containers.

### Developing with Dev Containers

You will need [*Visual Studio Code*](https://code.visualstudio.com/) (VSCode), the 
[Dev Containers](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers) VSCode
extension, and [Docker](https://www.docker.com/)

1. Login to the GitHub container repository through your terminal.
    ```bash
    docker login ghcr.io
    ``` 
    It will ask you for your Docker login credential. If you don't have an account with Docker, you can use your GitHub
    username with a personal access code from GitHub. See the GitHub docs for more info on personal access tokens.

3. Go to the command palette in VSCode (`View` -> `Command Palette`) and execute `>Dev Containers: Rebuild and Reopen in Container`.
   This will reload your VSCode IDE and install all necessary extensions in the container. This might take a while.

4. Now you can [run the examples](#run-examples).

### Developing in your local Environment

You will need JDK 8 or higher.

We recommend using [*Visual Studio Code*](https://code.visualstudio.com/) (VSCode) for trying these examples.
You can then make use of the [*Neo Blockchain Toolkit*](https://marketplace.visualstudio.com/items?itemName=ngd-seattle.neo-blockchain-toolkit)
which gives you the best developer experience with Neo. You can checkout their
[Quickstart Guide](https://github.com/neo-project/neo-blockchain-toolkit/blob/master/quickstart.md) for how to set it up.

You should install the [Java Extension Pack](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) and the
[Gradle Extension Pack](https://marketplace.visualstudio.com/items?itemName=richardwillis.vscode-gradle-extension-pack)
for Java and Gradle support. All examples are using [*Gradle*](https://gradle.org/) as the build tool and provide a
Gradle wrapper. So, you don't necessarily need Gradle installed on your machine.

Many examples will require a Neo network. We recommend using [*Neo Express*](https://github.com/neo-project/neo-express) for
that purpose. It is integrated into the [*Neo Blockchain Toolkit*](https://marketplace.visualstudio.com/items?itemName=ngd-seattle.neo-blockchain-toolkit)
that is available on the VSCode Extension Marketplace.

These examples are accompanied by code tours. To make use of them install the VSCode [*Code
Tours*](https://marketplace.visualstudio.com/items?itemName=vsls-contrib.codetour) extension.

## Run Examples

Finally, open the directory (where you cloned the repo in) with VSCode. Now you can build the project:

```bash
./gradlew build
```

Some examples don't depend on a running Neo network, those can be executed immediately, e.g., via the `Run` buttons above the
`main` methods. All other examples will require a running Neo network. Setting this up with the help of the *Neo Blockchain Toolkit*
is explained in the code tours.

## Contact

For questions, issues, or suggestions, please create an issue [here](https://github.com/neow3j/neow3j-examples-java/issues).
