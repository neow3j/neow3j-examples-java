package io.neow3j.examples.utils;

import io.neow3j.crypto.ECKeyPair;
import io.neow3j.crypto.Wallet;
import io.neow3j.crypto.WalletFile;
import io.neow3j.crypto.WalletUtils;
import io.neow3j.crypto.exceptions.CipherException;
import io.neow3j.utils.Numeric;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class LoadWallet {

    private final static String MY_PASSWORD = "myPassw0rd!@#";

    public static void main(String[] args) throws Exception {

        // create a new standard wallet file -- just for testing
        Path walletFilePathOnDisk = createNewStandardWalletFile();

        // based on the path returned above, load the wallet file as an object
        WalletFile walletFile = WalletUtils.loadWalletFile(walletFilePathOnDisk.toString());
        // load the first account of such wallet file (it could have multiple ones)
        WalletFile.Account account = walletFile.getAccounts().stream().findFirst().orElseThrow(() -> new Exception("Account not found."));

        // "decrypt"or "unlock" the account based on the password, and get the raw key pairs
        ECKeyPair ecKeyPair = Wallet.decryptStandard(MY_PASSWORD, walletFile, account);

        // print the key pairs info
        System.out.println("Private Key (BigInteger): " + ecKeyPair.getPrivateKey());
        System.out.println("Public Key (BigInteger): " + ecKeyPair.getPublicKey());
        System.out.println("Private Key (Hex): " + Numeric.toHexStringNoPrefix(ecKeyPair.getPrivateKey()));
        System.out.println("Public Key (Hex): " + Numeric.toHexStringNoPrefix(ecKeyPair.getPublicKey()));

    }

    private static Path createNewStandardWalletFile() throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, CipherException {
        Path tempDirectory = Files.createTempDirectory("wallet-dir-prefix-test");
        String fileName = WalletUtils.generateNewWalletFile(MY_PASSWORD, tempDirectory.toFile());
        return Paths.get(tempDirectory.toString(), fileName);
    }

}
