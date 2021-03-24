package io.neow3j.examples.keys;

import io.neow3j.crypto.ECKeyPair;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class GetWIFFromKeyPair {

    public static void main(String[] args) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        ECKeyPair ecKeyPair = ECKeyPair.createEcKeyPair();
        String wif = ecKeyPair.exportAsWIF();

        System.out.println("\n####################");
        System.out.println("wif: " + wif);
        System.out.println("####################");
    }
}
