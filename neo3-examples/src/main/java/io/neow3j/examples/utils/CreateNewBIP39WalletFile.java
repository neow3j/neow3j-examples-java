package io.neow3j.examples.utils;

import io.neow3j.contract.ScriptHash;
import io.neow3j.crypto.exceptions.CipherException;
import io.neow3j.wallet.Bip39Account;
import io.neow3j.wallet.Wallet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CreateNewBIP39WalletFile {

    public static void main(String[] args) throws IOException, CipherException {
        Path tempFile = Files.createTempFile("wallet-dir-prefix-test", ".wallet");

        Wallet w = Wallet.createWallet();
        Bip39Account a = Bip39Account.createAccount("myPassw0rd!@#");
        w.addAccounts(a);
        w.encryptAllAccounts("password-to-encrypt");
        w.saveNEP6Wallet(tempFile.toFile());

        String mnemonicWords = a.getMnemonic();

        System.out.println("\n####################");
        System.out.println("mnemonic words: " + mnemonicWords);
        System.out.println("####################");
    }
}
