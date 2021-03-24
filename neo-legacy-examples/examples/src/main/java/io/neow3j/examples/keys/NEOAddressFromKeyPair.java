package io.neow3j.examples.keys;

import io.neow3j.crypto.ECKeyPair;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class NEOAddressFromKeyPair {

    public static void main(String[] args) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        ECKeyPair ecKeyPair = ECKeyPair.createEcKeyPair();
        String neoAddress = ecKeyPair.getAddress();

        System.out.println("\n####################");
        System.out.println("NEO Address: " + neoAddress);
        System.out.println("####################\n");
    }

}
