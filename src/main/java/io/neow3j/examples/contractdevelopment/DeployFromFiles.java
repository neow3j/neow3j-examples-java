package io.neow3j.examples.contractdevelopment;

import static io.neow3j.examples.Constants.ALICE;
import static io.neow3j.examples.Constants.NEOW3J_PRIVATENET;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import io.neow3j.contract.ContractManagement;
import io.neow3j.contract.NefFile;
import io.neow3j.contract.SmartContract;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.ObjectMapperFactory;
import io.neow3j.protocol.core.response.ContractManifest;
import io.neow3j.protocol.core.response.NeoSendRawTransaction;
import io.neow3j.transaction.AccountSigner;
import io.neow3j.types.Hash160;

// Shows how to read a smart contract's files from the disk and deployed it on through a local neo-node.
public class DeployFromFiles {

    // The neow3j instance used in this example.
    static final Neow3j neow3j = NEOW3J_PRIVATENET;

    public static void main(String[] args) throws Throwable {

        String contractName = "AxLabsToken";
        // NEF file:
        File contractNefFile = Paths.get("build", "neow3j", contractName + ".nef").toFile();
        NefFile nefFile = NefFile.readFromFile(contractNefFile);
        // Manifest file:
        File contractManifestFile = Paths.get("build", "neow3j", contractName + ".manifest.json").toFile();
        ContractManifest manifest;
        try (FileInputStream s = new FileInputStream(contractManifestFile)) {
            manifest = ObjectMapperFactory.getObjectMapper().readValue(s, ContractManifest.class);
        }

        // Deploy the contract's NEF and manifest. This creates, signs and send a transaction to the neo-node.
        NeoSendRawTransaction response = new ContractManagement(neow3j)
                .deploy(nefFile, manifest)
                .signers(AccountSigner.global(ALICE))
                .sign()
                .send();

        if (response.hasError()) {
            System.out.printf("Deployment was not successful. Error message from neo-node was: '%s'\n",
                    response.getError().getMessage());
        } else {
            System.out.printf("The contract was deployed in transaction %s\n",
                    response.getSendRawTransaction().getHash());
            Hash160 contractHash = SmartContract.calcContractHash(
                    ALICE.getScriptHash(), nefFile.getCheckSumAsInteger(), manifest.getName());
            System.out.printf("Script hash of the deployed contract: %s\n", contractHash);
            System.out.printf("Contract Address: %s\n", contractHash.toAddress());
        }
    }

}
