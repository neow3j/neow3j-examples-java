package io.neow3j.examples.transactions;

import io.neow3j.crypto.ECKeyPair;
import io.neow3j.crypto.Sign;
import io.neow3j.crypto.WIF;
import io.neow3j.crypto.transaction.RawScript;
import io.neow3j.crypto.transaction.RawTransaction;
import io.neow3j.crypto.transaction.RawTransactionInput;
import io.neow3j.crypto.transaction.RawTransactionOutput;
import io.neow3j.model.types.NEOAsset;
import io.neow3j.transaction.ContractTransaction;
import io.neow3j.utils.Keys;
import io.neow3j.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class CreateContractTransactionMultiSig {

    //    public static void main(String[] args) {
    //
    //        ECKeyPair ecKeyPair1 = ECKeyPair.create(WIF.getPrivateKeyFromWIF("Kx9xMQVipBYAAjSxYEoZVatdVQfhYHbMFWSYPinSgAVd1d4Qgbpf"));
    //        ECKeyPair ecKeyPair2 = ECKeyPair.create(WIF.getPrivateKeyFromWIF("KzbKux44feMetfqdA5Cze9FNAkydRmphoFKnK5TGDdEQ8Nv1poXV"));
    //        ECKeyPair ecKeyPair3 = ECKeyPair.create(WIF.getPrivateKeyFromWIF("L3hxLFUsNDmkzW6QoLH2PGc2DqGG5Kj1gCVwmr7duWJ9FReYWnjU"));
    //
    //        String multiSigAddress = Keys.getMultiSigAddress(2, ecKeyPair1.getPublicKey(), ecKeyPair2.getPublicKey(), ecKeyPair3
    //                .getPublicKey());
    //
    //        RawVerificationScript verificationScript = Keys.getVerificationScriptFromPublicKey(2, ecKeyPair1.getPublicKey(), ecKeyPair2
    //                .getPublicKey(), ecKeyPair3.getPublicKey());
    //
    //        ContractTransaction rawTx = new ContractTransaction.Builder()
    //                .input(new RawTransactionInput("9feac4774eb0f01ab5d6817c713144b7c020b98f257c30b1105062d434e6f254", 0))
    //                .outputs(
    //                        Arrays.asList(
    //                                new RawTransactionOutput(NEOAsset.HASH_ID, "100.0", "AK2nJJpJr6o664CWJKi1QRXjqeic2zRp8y"),
    //                                new RawTransactionOutput(NEOAsset.HASH_ID, "900.0", multiSigAddress)
    //                        )
    //                )
    //                .build();
    //
    //        // serialize the base raw transaction
    //        // Important: without scripts!
    //        byte[] rawTxUnsignedArray = rawTx.toArray();
    //
    //        // add 2 signatures out of the 3 possible -- order here is important!
    //        List<RawInvocationScript> rawInvocationScriptList = new ArrayList<>();
    //        rawInvocationScriptList.add(new RawInvocationScript(Sign.signMessage(rawTxUnsignedArray, ecKeyPair1)));
    //        rawInvocationScriptList.add(new RawInvocationScript(Sign.signMessage(rawTxUnsignedArray, ecKeyPair2)));
    //
    //        // give the invocation and verification script to the raw transaction:
    //        rawTx.addScript(rawInvocationScriptList, verificationScript);
    //
    //        byte[] rawTxSignedArray = rawTx.toArray();
    //        String rawTransactionHexString = Numeric.toHexStringNoPrefix(rawTxSignedArray);
    //        System.out.println("rawTransactionHexString: " + rawTransactionHexString);
    //
    //    }

    public static void main(String[] args) {
        ECKeyPair ecKeyPair1 = ECKeyPair.create(WIF.getPrivateKeyFromWIF("Kx9xMQVipBYAAjSxYEoZVatdVQfhYHbMFWSYPinSgAVd1d4Qgbpf"));
        ECKeyPair ecKeyPair2 = ECKeyPair.create(WIF.getPrivateKeyFromWIF("KzbKux44feMetfqdA5Cze9FNAkydRmphoFKnK5TGDdEQ8Nv1poXV"));
        ECKeyPair ecKeyPair3 = ECKeyPair.create(WIF.getPrivateKeyFromWIF("L3hxLFUsNDmkzW6QoLH2PGc2DqGG5Kj1gCVwmr7duWJ9FReYWnjU"));
        ECKeyPair ecKeyPair4 = ECKeyPair.create(WIF.getPrivateKeyFromWIF("Kwc1zMAmYHYQ77jZuopaEgL7FejdyDRxd9jpPQJQFkUH39MQpab9"));
        ECKeyPair ecKeyPair5 = ECKeyPair.create(WIF.getPrivateKeyFromWIF("L4LefFC58PPsPyacCLwGJ7RhuPZLnfLdQUiwNyW6gu11JkSRyx73"));
        ECKeyPair ecKeyPair6 = ECKeyPair.create(WIF.getPrivateKeyFromWIF("L4ToFoNfJR1XwzSEHVQonxjmLfoRW91oVLZ5hGGLakkkKUMHanWC"));
        ECKeyPair ecKeyPair7 = ECKeyPair.create(WIF.getPrivateKeyFromWIF("L1RirHWwQcwEcsUVmF6m4s9wUB8zufUXEHrHmcvuP4cuNTXA1nSM"));
        ECKeyPair ecKeyPair8 = ECKeyPair.create(WIF.getPrivateKeyFromWIF("L1CcDgYLRT1VzGQEzHATyHwPoYHwGTDdD4ugJYESYfGehZ6AtnHw"));
        ECKeyPair ecKeyPair9 = ECKeyPair.create(WIF.getPrivateKeyFromWIF("L2xUnihSNBTh8jrYzLyrxMfFwDbPp1HvDe92r7HxsZQ8rL4NktfE"));
        ECKeyPair ecKeyPair10 = ECKeyPair.create(WIF.getPrivateKeyFromWIF("KyRPzRthggPA3SSRTBgma3VyjKhS3yhSM2jsKVrLRfVkFP5g5bQi"));

        List<BigInteger> publicKeys = new ArrayList<>();
        publicKeys.add(ecKeyPair1.getPublicKey());
        publicKeys.add(ecKeyPair2.getPublicKey());
        publicKeys.add(ecKeyPair3.getPublicKey());
        publicKeys.add(ecKeyPair4.getPublicKey());
        publicKeys.add(ecKeyPair5.getPublicKey());
        publicKeys.add(ecKeyPair6.getPublicKey());
        publicKeys.add(ecKeyPair7.getPublicKey());
        publicKeys.add(ecKeyPair8.getPublicKey());
        publicKeys.add(ecKeyPair9.getPublicKey());
        publicKeys.add(ecKeyPair10.getPublicKey());

        // the multiSig address should be "AJqgaX57U9ua5WxBKfA27wbfEgtR8HwER3"
        String multiSigAddress = Keys.getMultiSigAddress(7, publicKeys);

        RawTransaction tUnsigned = new ContractTransaction.Builder().input(new RawTransactionInput("9feac4774eb0f01ab5d6817c713144b7c020b98f257c30b1105062d434e6f254", 0))
                .output(new RawTransactionOutput(NEOAsset.HASH_ID, "100.0", "AK2nJJpJr6o664CWJKi1QRXjqeic2zRp8y"))
                .output(new RawTransactionOutput(NEOAsset.HASH_ID, "900.0", multiSigAddress))
                .build();

        byte[] tUnsignedArray = tUnsigned.toArrayWithoutScripts();

        List<Sign.SignatureData> sigs = new ArrayList<>();
        sigs.add(Sign.signMessage(tUnsignedArray, ecKeyPair1));
        sigs.add(Sign.signMessage(tUnsignedArray, ecKeyPair2));
        sigs.add(Sign.signMessage(tUnsignedArray, ecKeyPair3));
        sigs.add(Sign.signMessage(tUnsignedArray, ecKeyPair4));
        sigs.add(Sign.signMessage(tUnsignedArray, ecKeyPair5));
        sigs.add(Sign.signMessage(tUnsignedArray, ecKeyPair6));
        sigs.add(Sign.signMessage(tUnsignedArray, ecKeyPair7));
        tUnsigned.addScript(RawScript.createMultiSigWitness(7, sigs, publicKeys));

        byte[] rawTxSignedArray = tUnsigned.toArray();
        String rawTransactionHexString = Numeric.toHexStringNoPrefix(rawTxSignedArray);
        System.out.println("rawTransactionHexString: " + rawTransactionHexString);
    }

}
