package io.neow3j.examples.utils;

import io.neow3j.crypto.exceptions.CipherException;
import io.neow3j.wallet.Bip39Account;
import io.neow3j.wallet.Wallet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CreateNewBIP39WalletFile {

    public static void main(String[] args) throws IOException, CipherException {
        Path tempFile = Files.createTempFile("wallet-dir-prefix-test", ".wallet");

        Bip39Account a = Bip39Account.create("myPassw0rd!@#");
        Wallet w = Wallet.withAccounts(a);
        w.encryptAllAccounts("password-to-encrypt");
        w.saveNEP6Wallet(tempFile.toFile());

        String mnemonicWords = a.getMnemonic();

        System.out.println("\n####################");
        System.out.println("Mnemonic words: " + mnemonicWords);
        System.out.println("####################");
    }
}
