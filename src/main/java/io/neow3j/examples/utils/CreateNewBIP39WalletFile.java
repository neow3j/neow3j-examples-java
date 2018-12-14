package io.neow3j.examples.utils;

import io.neow3j.crypto.Bip39Wallet;
import io.neow3j.crypto.WalletUtils;
import io.neow3j.crypto.exceptions.CipherException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class CreateNewBIP39WalletFile {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, CipherException {
        Path tempDirectory = Files.createTempDirectory("wallet-dir-prefix-test");

        Bip39Wallet bip39Wallet = WalletUtils.generateBip39Wallet("myPassw0rd!@#", tempDirectory.toFile());

        String mnemonicWords = bip39Wallet.getMnemonic();
        System.out.println("mnemonic words: " + mnemonicWords);
    }

}
