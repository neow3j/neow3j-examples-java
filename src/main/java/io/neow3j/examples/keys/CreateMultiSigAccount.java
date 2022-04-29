package io.neow3j.examples.keys;

import java.util.Arrays;
import io.neow3j.crypto.ECKeyPair;
import io.neow3j.utils.Numeric;
import io.neow3j.wallet.Account;

import static io.neow3j.examples.Constants.ALICE;
import static io.neow3j.examples.Constants.BOB;

public class CreateMultiSigAccount {

    public static void main(String[] args) {
        ECKeyPair aliceKeyPair = ALICE.getECKeyPair();

        ECKeyPair bobKeyPair = BOB.getECKeyPair();

        int signingThreshold = 2;

        Account multiSigAcc = Account.createMultiSigAccount(
            Arrays.asList(aliceKeyPair.getPublicKey(), bobKeyPair.getPublicKey()), signingThreshold);

        System.out.println("\n####################");
        System.out.println("Multi-sig account address: " + multiSigAcc.getAddress());
        System.out.println("Multi-sig account verification script: ");
        System.out.println(Numeric.toHexStringNoPrefix(multiSigAcc.getVerificationScript().getScript()));
        System.out.println("####################\n");
    }

}
