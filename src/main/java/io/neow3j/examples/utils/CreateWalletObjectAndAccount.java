package io.neow3j.examples.utils;

import io.neow3j.crypto.ECKeyPair;
import io.neow3j.crypto.Keys;
import io.neow3j.crypto.Wallet;
import io.neow3j.crypto.WalletFile;
import io.neow3j.crypto.WalletUtils;
import io.neow3j.crypto.exceptions.CipherException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class CreateWalletObjectAndAccount {

    public static void main(String[] args) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, CipherException, IOException {
        // create a standard wallet file object
        WalletFile walletFileObj = Wallet.createStandardWallet();

        // create a key pair
        ECKeyPair ecKeyPair = Keys.createEcKeyPair();

        // password-protected account (NEP-2) based on a key pair
        WalletFile.Account account = Wallet.createStandardAccount("myPassw0rd!@#", ecKeyPair);

        // add the account to the standard wallet file
        walletFileObj.addAccount(account);

        Path tempDirectory = Files.createTempDirectory("wallet-dir-prefix-test");
        String fileName = WalletUtils.generateWalletFile(walletFileObj, tempDirectory.toFile());

        System.out.println("Wallet file path: " + Paths.get(tempDirectory.toString(), fileName));
    }

}
