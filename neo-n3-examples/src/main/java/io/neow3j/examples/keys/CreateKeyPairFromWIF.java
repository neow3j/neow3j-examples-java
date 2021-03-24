package io.neow3j.examples.keys;

import io.neow3j.crypto.ECKeyPair;
import io.neow3j.utils.Numeric;
import io.neow3j.wallet.Account;

import java.math.BigInteger;
import java.util.Arrays;

import static io.neow3j.examples.Constants.ALICE;

public class CreateKeyPairFromWIF {

    public static void main(String[] args) {
        ECKeyPair ecKeyPair = ALICE.getECKeyPair();
        BigInteger privKeyBigInt = ecKeyPair.getPrivateKey().getInt();
        BigInteger pubKeyBigInt = new BigInteger(1, ecKeyPair.getPublicKey().getEncoded(true));

        Account multiSigAcc = Account.createMultiSigAccount(Arrays.asList(ecKeyPair.getPublicKey()), 1);
        System.out.println(Numeric.toHexStringNoPrefix(multiSigAcc.getVerificationScript().getScript()));
        System.out.println(multiSigAcc.getAddress());
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
