package io.neow3j.examples.utils;

import io.neow3j.crypto.exceptions.CipherException;
import io.neow3j.wallet.Wallet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class CreateNewStandardWalletFile {

    public static void main(String[] args) throws IOException, CipherException {
        Path tempFile = Files.createTempFile("wallet-dir-prefix-test", ".wallet");

        Wallet.createGenericWallet("myPassw0rd!@#", tempFile.toFile());

        System.out.println("Wallet file path: " + tempFile.toAbsolutePath());
    }

}
