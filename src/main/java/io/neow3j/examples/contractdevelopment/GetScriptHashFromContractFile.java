package io.neow3j.examples.contractdevelopment;

import static io.neow3j.examples.Constants.ALICE;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import io.neow3j.types.Hash160;
import io.neow3j.contract.NefFile;
import io.neow3j.contract.SmartContract;
import io.neow3j.serialization.exceptions.DeserializationException;
import io.neow3j.protocol.ObjectMapperFactory;
import io.neow3j.protocol.core.response.ContractManifest;
import io.neow3j.utils.Numeric;

public class GetScriptHashFromContractFile {

    public static void main(String[] args) throws DeserializationException, IOException {

        // NEF:
        File contractNefFile = Paths.get("build", "neow3j", "FungibleToken.nef").toFile();
        NefFile nefFile = NefFile.readFromFile(contractNefFile);
        // Manifest file:
        File contractManifestFile = Paths.get("build", "neow3j", "FungibleToken.manifest.json").toFile();
        ContractManifest manifest;
        try (FileInputStream s = new FileInputStream(contractManifestFile)) {
            manifest = ObjectMapperFactory.getObjectMapper().readValue(s, ContractManifest.class);
        }

        // Get and print the contract hash. The deploying account is part of the contract's hash.
        Hash160 contractHash = SmartContract.calcContractHash(ALICE.getScriptHash(),
                nefFile.getCheckSumAsInteger(), manifest.getName());
        System.out.println("Contract Hash: " + contractHash);
        System.out.println("Contract Address: " + contractHash.toAddress());
        System.out.println("Contract Script: " + Numeric.toHexString(nefFile.getScript()));
    }

}
