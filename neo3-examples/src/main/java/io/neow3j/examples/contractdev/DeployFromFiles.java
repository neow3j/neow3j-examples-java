package io.neow3j.examples.contractdev;

import io.neow3j.contract.ContractManagement;
import io.neow3j.contract.NefFile;
import io.neow3j.contract.ScriptHash;
import io.neow3j.contract.SmartContract;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.ObjectMapperFactory;
import io.neow3j.protocol.core.methods.response.ContractManifest;
import io.neow3j.protocol.core.methods.response.NeoSendRawTransaction;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.transaction.Signer;
import io.neow3j.wallet.Account;
import io.neow3j.wallet.Wallet;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;

// Shows how to read a smart contract's files from the disk and deployed it on through a local
// neo-node.
public class DeployFromFiles {

    public static void main(String[] args) throws Throwable {

        // Set up the connection to the neo-node
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:40332"));

        // Setup an account and wallet for signing the transaction. Make sure that the account has a
        // sufficient GAS balance to pay for the deployment.
        Account a = Account.fromWIF("L3kCZj6QbFPwbsVhxnB8nUERDy4mhCSrWJew4u5Qh5QmGMfnCTda");
        Wallet w = Wallet.withAccounts(a);

        // Retrieve the contract files:

        String contractName = "BongoCatToken";
        // NEF file:
        File contractNefFile = Paths.get("build", "neow3j", contractName + ".nef").toFile();
        NefFile nefFile = NefFile.readFromFile(contractNefFile);
        // Manifest file:
        File contractManifestFile = Paths.get("build", "neow3j", contractName + ".manifest.json").toFile();
        ContractManifest manifest;
        try (FileInputStream s = new FileInputStream(contractManifestFile)) {
            manifest = ObjectMapperFactory.getObjectMapper().readValue(s, ContractManifest.class);
        }

        // Deploy the contract's NEF and manifest. This creates, signs and send a transaction to
        // the neo-node.
        NeoSendRawTransaction response = new ContractManagement(neow3j)
                .deploy(nefFile, manifest)
                .signers(Signer.global(a.getScriptHash()))
                .wallet(w)
                .sign()
                .send();

        if (response.hasError()) {
            System.out.printf("Deployment was not successful. Error message from neo-node "
                    + "was: '%s'\n", response.getError().getMessage());
        } else {
            System.out.printf("The contract was deployed in transaction %s\n", 
                    response.getSendRawTransaction().getHash());
            ScriptHash contractHash = SmartContract.getContractHash(
                    a.getScriptHash(), nefFile.getCheckSumAsInteger(), manifest.getName());
            System.out.printf("Script hash of the deployed contract: %s\n", contractHash.toString());
            System.out.printf("Contract Address: %s\n", contractHash.toAddress());
        }
    }

}
