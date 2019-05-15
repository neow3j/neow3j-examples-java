package io.neow3j.examples.transactions;

import io.neow3j.crypto.ECKeyPair;
import io.neow3j.crypto.Keys;
import io.neow3j.crypto.Sign;
import io.neow3j.crypto.WIF;
import io.neow3j.crypto.transaction.RawInvocationScript;
import io.neow3j.crypto.transaction.RawTransaction;
import io.neow3j.crypto.transaction.RawTransactionInput;
import io.neow3j.crypto.transaction.RawTransactionOutput;
import io.neow3j.crypto.transaction.RawVerificationScript;
import io.neow3j.model.types.NEOAsset;
import io.neow3j.utils.Numeric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateContractTransactionMultiSig {

    public static void main(String[] args) {

        ECKeyPair ecKeyPair1 = ECKeyPair.create(WIF.getPrivateKeyFromWIF("Kx9xMQVipBYAAjSxYEoZVatdVQfhYHbMFWSYPinSgAVd1d4Qgbpf"));
        ECKeyPair ecKeyPair2 = ECKeyPair.create(WIF.getPrivateKeyFromWIF("KzbKux44feMetfqdA5Cze9FNAkydRmphoFKnK5TGDdEQ8Nv1poXV"));
        ECKeyPair ecKeyPair3 = ECKeyPair.create(WIF.getPrivateKeyFromWIF("L3hxLFUsNDmkzW6QoLH2PGc2DqGG5Kj1gCVwmr7duWJ9FReYWnjU"));

        String multiSigAddress = Keys.getMultiSigAddress(
                2,
                ecKeyPair1.getPublicKey(),
                ecKeyPair2.getPublicKey(),
                ecKeyPair3.getPublicKey()
        );

        RawVerificationScript verificationScript = Keys.getVerificationScriptFromPublicKey(
                2,
                ecKeyPair1.getPublicKey(),
                ecKeyPair2.getPublicKey(),
                ecKeyPair3.getPublicKey()
        );

        RawTransaction rawTx = RawTransaction.createContractTransaction(
                null,
                Arrays.asList(
                        new RawTransactionInput("9feac4774eb0f01ab5d6817c713144b7c020b98f257c30b1105062d434e6f254", 0)
                ),
                Arrays.asList(
                        new RawTransactionOutput(0, NEOAsset.HASH_ID, "100.0", "AK2nJJpJr6o664CWJKi1QRXjqeic2zRp8y"),
                        new RawTransactionOutput(1, NEOAsset.HASH_ID, "900.0", multiSigAddress)
                )
        );

        // serialize the base raw transaction
        // Important: without scripts!
        byte[] rawTxUnsignedArray = rawTx.toArray();

        // add 2 signatures out of the 3 possible -- order here is important!
        List<RawInvocationScript> rawInvocationScriptList = new ArrayList<>();
        rawInvocationScriptList.add(new RawInvocationScript(Sign.signMessage(rawTxUnsignedArray, ecKeyPair1)));
        rawInvocationScriptList.add(new RawInvocationScript(Sign.signMessage(rawTxUnsignedArray, ecKeyPair2)));

        // give the invocation and verification script to the raw transaction:
        rawTx.addScript(rawInvocationScriptList, verificationScript);

        byte[] rawTxSignedArray = rawTx.toArray();
        String rawTransactionHexString = Numeric.toHexStringNoPrefix(rawTxSignedArray);
        System.out.println("rawTransactionHexString: " + rawTransactionHexString);

    }

}
