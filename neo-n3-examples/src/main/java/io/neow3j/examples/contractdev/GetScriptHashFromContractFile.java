package io.neow3j.examples.contractdev;

import io.neow3j.protocol.ObjectMapperFactory;
import io.neow3j.protocol.core.methods.response.ContractManifest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;

import io.neow3j.contract.NefFile;
import io.neow3j.contract.Hash160;
import io.neow3j.contract.SmartContract;
import io.neow3j.io.exceptions.DeserializationException;
import io.neow3j.utils.Numeric;
import io.neow3j.wallet.Account;

public class GetScriptHashFromContractFile {

    public static void main(String[] args) throws DeserializationException, IOException {

        // Setup the account that was used to deploy the Contract loaded below.
        // It is required to derive the contract's hash.
        Account account = Account.fromWIF("L3kCZj6QbFPwbsVhxnB8nUERDy4mhCSrWJew4u5Qh5QmGMfnCTda");

        // Retrieve the contract files.

        // NEF:
        File contractNefFile = Paths.get("build", "neow3j",
                "BongoCatToken.nef").toFile();
        NefFile nefFile = NefFile.readFromFile(contractNefFile);
        // Manifest file:
        File contractManifestFile = Paths.get("build", "neow3j",
                "BongoCatToken.manifest.json").toFile();
        ContractManifest manifest;
        try (FileInputStream s = new FileInputStream(contractManifestFile)) {
            manifest = ObjectMapperFactory.getObjectMapper().readValue(s, ContractManifest.class);
        }


        // Get and print the contract hash
        Hash160 contractHash = SmartContract.getContractHash(account.getScriptHash(),
                nefFile.getCheckSumAsInteger(), manifest.getName());
        System.out.println("Contract Hash: " + contractHash);
        System.out.println("Contract Address: " + contractHash.toAddress());
        System.out.println("Contract Script: " + Numeric.toHexString(nefFile.getScript()));
    }

}