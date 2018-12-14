package io.neow3j.examples.utils;

import io.neow3j.crypto.Bip39Wallet;
import io.neow3j.crypto.Credentials;
import io.neow3j.crypto.ECKeyPair;
import io.neow3j.crypto.WalletUtils;
import io.neow3j.crypto.exceptions.CipherException;
import io.neow3j.utils.Numeric;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LoadBIP39Wallet {

    private final static String MY_PASSWORD = "myPassw0rd!@#";

    public static void main(String[] args) throws Exception {

        // create a new BIP-39 wallet file -- just for testing
        Bip39Wallet bip39WalletFileObject = createNewBIP39WalletFile();

        // based on the password and the mnemonic, re-create the keypair for such account
        Credentials credentials = WalletUtils.loadBip39Credentials(MY_PASSWORD, bip39WalletFileObject.getMnemonic());

        // get the keypair object
        ECKeyPair ecKeyPair = credentials.getEcKeyPair();

        // print the key pairs info
        System.out.println("Private Key (BigInteger): " + ecKeyPair.getPrivateKey());
        System.out.println("Public Key (BigInteger): " + ecKeyPair.getPublicKey());
        System.out.println("Private Key (Hex): " + Numeric.toHexStringNoPrefix(ecKeyPair.getPrivateKey()));
        System.out.println("Public Key (Hex): " + Numeric.toHexStringNoPrefix(ecKeyPair.getPublicKey()));

    }

    private static Bip39Wallet createNewBIP39WalletFile() throws IOException, CipherException {
        Path tempDirectory = Files.createTempDirectory("wallet-dir-prefix-test");
        Bip39Wallet bip39Wallet = WalletUtils.generateBip39Wallet(MY_PASSWORD, tempDirectory.toFile());
        System.out.println("Wallet file path: " + Paths.get(tempDirectory.toString(), bip39Wallet.getFilename()));
        return bip39Wallet;
    }

}
