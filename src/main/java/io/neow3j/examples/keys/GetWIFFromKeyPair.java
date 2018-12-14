package io.neow3j.examples.keys;

import io.neow3j.crypto.Credentials;
import io.neow3j.crypto.ECKeyPair;
import io.neow3j.crypto.Keys;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class GetWIFFromKeyPair {

    public static void main(String[] args) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        ECKeyPair ecKeyPair = Keys.createEcKeyPair();
        // String wif = ecKeyPair.exportAsWIF();
        // System.out.println("WIF: " + wif);
    }

}
