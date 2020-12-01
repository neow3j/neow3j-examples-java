package io.neow3j.examples.keys;

import io.neow3j.crypto.ECKeyPair;
import io.neow3j.crypto.WIF;
import io.neow3j.utils.Numeric;

import java.math.BigInteger;

public class CreateKeyPairFromWIF {

    public static void main(String[] args) {
        ECKeyPair ecKeyPair = ECKeyPair.create(WIF.getPrivateKeyFromWIF("L3kCZj6QbFPwbsVhxnB8nUERDy4mhCSrWJew4u5Qh5QmGMfnCTda"));

        System.out.println("\n####################");
        System.out.println("Private Key (BigInteger): " + ecKeyPair.getPrivateKey().getInt());
        System.out.println("Public Key (BigInteger): " + new BigInteger(1, ecKeyPair.getPublicKey().getEncoded(true)));
        System.out.println("Private Key (Hex): " + Numeric.toHexStringNoPrefix(ecKeyPair.getPrivateKey().getInt()));
        System.out.println("Public Key (Hex): " + Numeric.toHexStringNoPrefix(new BigInteger(1, ecKeyPair.getPublicKey().getEncoded(true))));
        System.out.println("####################");
    }
}
