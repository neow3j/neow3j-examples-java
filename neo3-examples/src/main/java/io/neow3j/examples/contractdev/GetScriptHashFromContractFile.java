package io.neow3j.examples.contractdev;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import io.neow3j.contract.NefFile;
import io.neow3j.contract.ScriptHash;
import io.neow3j.contract.SmartContract;
import io.neow3j.io.exceptions.DeserializationException;
import io.neow3j.model.NeoConfig;
import io.neow3j.utils.Numeric;
import io.neow3j.wallet.Account;

public class GetScriptHashFromContractFile {
    public static void main(String[] args) throws DeserializationException, IOException {
        
        // Set the magic number according to the Neo network's configuration. It is used when
        // signing transactions.
        NeoConfig.setMagicNumber(new byte[]{0x01, 0x03, 0x00, 0x0}); // Magic number 769

        // Setup the account that was used to deploy the Contract loaded below. It is required to derive the contract's hash.
        Account account = Account.fromWIF("L3kCZj6QbFPwbsVhxnB8nUERDy4mhCSrWJew4u5Qh5QmGMfnCTda");

        // Retrieve the contract files.
        File contractNefFile = Paths.get("build", "neow3j", "BongoCatToken.nef").toFile();
        NefFile nefFile = NefFile.readFromFile(contractNefFile);

        // Get and print the contract hash
        ScriptHash contractHash = SmartContract.getContractHash(account.getScriptHash(), nefFile.getScript());
        System.out.println("Contract Hash: " + contractHash);
        System.out.println("Contract Address: " + contractHash.toAddress());
        System.out.println("Contract Script: " + Numeric.toHexString(nefFile.getScript()));
    }
}
