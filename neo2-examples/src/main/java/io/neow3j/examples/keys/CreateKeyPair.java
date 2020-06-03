package io.neow3j.examples.keys;

import io.neow3j.crypto.ECKeyPair;
import io.neow3j.utils.Numeric;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class CreateKeyPair {

    public static void main(String[] args) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        ECKeyPair ecKeyPair = ECKeyPair.createEcKeyPair();

        System.out.println("\n####################");
        System.out.println("Private Key (BigInteger): " + ecKeyPair.getPrivateKey());
        System.out.println("Public Key (BigInteger): " + ecKeyPair.getPublicKey());
        System.out.println("Private Key (Hex): " + Numeric.toHexStringNoPrefix(ecKeyPair.getPrivateKey()));
        System.out.println("Public Key (Hex): " + Numeric.toHexStringNoPrefix(ecKeyPair.getPublicKey()));
        System.out.println("####################");
    }
}
