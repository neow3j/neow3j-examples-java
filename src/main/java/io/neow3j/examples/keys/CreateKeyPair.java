package io.neow3j.examples.keys;

import io.neow3j.crypto.ECKeyPair;
import io.neow3j.utils.Numeric;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class CreateKeyPair {

    public static void main(String[] args) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        ECKeyPair ecKeyPair = ECKeyPair.createEcKeyPair();

        System.out.println("\n####################");
        System.out.println("Private Key");
        System.out.println("BigInteger: " + ecKeyPair.getPrivateKey().getInt());
        System.out.println("Hex:        " + Numeric.toHexStringNoPrefix(ecKeyPair.getPrivateKey().getInt()));
        System.out.println();
        System.out.println("Public Key");
        System.out.println("BigInteger: " + new BigInteger(1, ecKeyPair.getPublicKey().getEncoded(true)));
        System.out.println("Hex:        " + Numeric.toHexStringNoPrefix(new BigInteger(1, ecKeyPair.getPublicKey().getEncoded(true))));
        System.out.println("####################");
    }
}
