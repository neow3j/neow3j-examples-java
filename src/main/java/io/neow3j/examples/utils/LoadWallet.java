package io.neow3j.examples.utils;

public class LoadWallet {

//    private final static String MY_PASSWORD = "myPassw0rd!@#";
//
//    public static void main(String[] args) throws Exception {
//
//        // create a new standard wallet file -- just for testing
//        Path walletFilePathOnDisk = createNewStandardWalletFile();
//
//        // based on the path returned above, load the wallet file as an object
//        WalletFile walletFile = WalletUtils.loadWalletFile(walletFilePathOnDisk.toString());
//        // load the first account of such wallet file (it could have multiple ones)
//        WalletFile.Account account = walletFile.getAccounts().stream().findFirst().orElseThrow(() -> new Exception("Account not found."));
//
//        // "decrypt"or "unlock" the account based on the password, and get the raw key pairs
//        ECKeyPair ecKeyPair = Wallet.decryptStandard(MY_PASSWORD, walletFile, account);
//
//        // print the key pairs info
//        System.out.println("Private Key (BigInteger): " + ecKeyPair.getPrivateKey());
//        System.out.println("Public Key (BigInteger): " + ecKeyPair.getPublicKey());
//        System.out.println("Private Key (Hex): " + Numeric.toHexStringNoPrefix(ecKeyPair.getPrivateKey()));
//        System.out.println("Public Key (Hex): " + Numeric.toHexStringNoPrefix(ecKeyPair.getPublicKey()));
//
//    }
//
//    private static Path createNewStandardWalletFile() throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, CipherException {
//        Path tempDirectory = Files.createTempDirectory("wallet-dir-prefix-test");
//        String fileName = WalletUtils.generateNewWalletFile(MY_PASSWORD, tempDirectory.toFile());
//        return Paths.get(tempDirectory.toString(), fileName);
//    }

}
