package io.neow3j.examples.contractdevelopment;

import io.neow3j.compiler.CompilationUnit;
import io.neow3j.compiler.Compiler;
import io.neow3j.contract.ContractManagement;
import io.neow3j.contract.Hash160;
import io.neow3j.contract.SmartContract;
import io.neow3j.examples.contractdevelopment.contracts.NonFungibleToken;
import io.neow3j.protocol.core.methods.response.NeoSendRawTransaction;

import java.nio.file.Path;
import java.nio.file.Paths;

import static io.neow3j.contract.ContractUtils.writeContractManifestFile;
import static io.neow3j.contract.ContractUtils.writeNefFile;
import static io.neow3j.examples.Constants.*;
import static io.neow3j.transaction.Signer.global;

// Shows how a smart contract can be compiled programmatically and then deployed on a local
// Neo blockchain.
public class CompileAndDeploy {

    public static void main(String[] args) throws Throwable {

        // Compile the NonFungibleToken contract and construct a SmartContract object from it.
        CompilationUnit res = new Compiler().compile(NonFungibleToken.class.getCanonicalName());

        // Write contract (compiled, NEF) to the disk
        Path buildNeow3jPath = Paths.get("build", "neow3j");
        buildNeow3jPath.toFile().mkdirs();
        writeNefFile(res.getNefFile(), res.getManifest().getName(), buildNeow3jPath);

        // Write manifest to the disk
        writeContractManifestFile(res.getManifest(), buildNeow3jPath);

        // Deploy the contract's NEF and manifest. This creates, signs and send a transaction to
        // the neo-node.
        NeoSendRawTransaction response = new ContractManagement(NEOW3J)
                .deploy(res.getNefFile(), res.getManifest())
                .signers(global(ALICE))
                .wallet(WALLET)
                .sign()
                .send();

        if (response.hasError()) {
            System.out.printf("Deployment was not successful. Error message from neo-node was: "
                    + "'%s'\n", response.getError().getMessage());
        } else {
            Hash160 contractHash = SmartContract.getContractHash(
                    ALICE.getScriptHash(), res.getNefFile().getCheckSumAsInteger(),
                    res.getManifest().getName());
            System.out.println("Script hash of the deployed contract: " + contractHash);
            System.out.println("Contract Address: " + contractHash.toAddress());
        }
    }

}
