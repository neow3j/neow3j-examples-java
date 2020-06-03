package io.neow3j.examples.keys;

import io.neow3j.crypto.ECKeyPair;
import io.neow3j.crypto.WIF;
import io.neow3j.utils.Numeric;

public class CreateKeyPairFromWIF {

    public static void main(String[] args) {
        ECKeyPair ecKeyPair = ECKeyPair.create(WIF.getPrivateKeyFromWIF("Kx9xMQVipBYAAjSxYEoZVatdVQfhYHbMFWSYPinSgAVd1d4Qgbpf"));

        System.out.println("\n####################");
        System.out.println("Private Key (BigInteger): " + ecKeyPair.getPrivateKey());
        System.out.println("Public Key (BigInteger): " + ecKeyPair.getPublicKey());
        System.out.println("Private Key (Hex): " + Numeric.toHexStringNoPrefix(ecKeyPair.getPrivateKey()));
        System.out.println("Public Key (Hex): " + Numeric.toHexStringNoPrefix(ecKeyPair.getPublicKey()));
        System.out.println("####################");
    }
}
