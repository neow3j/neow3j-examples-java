package io.neow3j.examples.utils;

import io.neow3j.crypto.WalletUtils;
import io.neow3j.crypto.exceptions.CipherException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class CreateNewStandardWalletFile {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, CipherException {
        Path tempDirectory = Files.createTempDirectory("wallet-dir-prefix-test");

        String fileName = WalletUtils.generateNewWalletFile("myPassw0rd!@#", tempDirectory.toFile());

        System.out.println("Wallet file path: " + Paths.get(tempDirectory.toString(), fileName));
    }

}
