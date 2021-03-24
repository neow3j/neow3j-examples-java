package io.neow3j.examples.keys;

import io.neow3j.crypto.ECKeyPair;
import io.neow3j.crypto.WIF;
import io.neow3j.utils.Numeric;

import java.math.BigInteger;

public class CreateKeyPairFromWIF {

    public static void main(String[] args) {
        ECKeyPair ecKeyPair = ECKeyPair.create(WIF.getPrivateKeyFromWIF("L3kCZj6QbFPwbsVhxnB8nUERDy4mhCSrWJew4u5Qh5QmGMfnCTda"));
        BigInteger privKeyBigInt = ecKeyPair.getPrivateKey().getInt();
        BigInteger pubKeyBigInt = new BigInteger(1, ecKeyPair.getPublicKey().getEncoded(true));

        System.out.println("\n####################");
        System.out.println("Private Key");
        System.out.println("BigInteger: " + privKeyBigInt);
        System.out.println("Hex:        " + Numeric.toHexStringNoPrefix(privKeyBigInt));
        System.out.println();
        System.out.println("Public Key");
        System.out.println("BigInteger: " + pubKeyBigInt);
        System.out.println("Hex:        " + Numeric.toHexStringNoPrefix(pubKeyBigInt));
        System.out.println("####################\n");
    }
}
