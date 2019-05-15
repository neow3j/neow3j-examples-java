package io.neow3j.examples.transactions;

import io.neow3j.crypto.Claim;
import io.neow3j.crypto.ECKeyPair;
import io.neow3j.crypto.Keys;
import io.neow3j.crypto.Sign;
import io.neow3j.crypto.WIF;
import io.neow3j.crypto.transaction.ClaimTransaction;
import io.neow3j.crypto.transaction.RawInvocationScript;
import io.neow3j.crypto.transaction.RawTransaction;
import io.neow3j.crypto.transaction.RawTransactionInput;
import io.neow3j.crypto.transaction.RawTransactionOutput;
import io.neow3j.crypto.transaction.RawVerificationScript;
import io.neow3j.model.types.GASAsset;
import io.neow3j.model.types.NEOAsset;
import io.neow3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateClaimTransaction {

    public static void main(String[] args) {
        String wif = "KxDgvEKzgSBPPfuVfw67oPQBSjidEiqTHURKSDL1R7yGaGYAeYnr";
        ECKeyPair ecKeyPair = ECKeyPair.create(WIF.getPrivateKeyFromWIF(wif));
        String adr = Keys.getAddress(ecKeyPair.getPublicKey()); // "AK2nJJpJr6o664CWJKi1QRXjqeic2zRp8y"

        // The transaction output (ID and index) from which the claimable GAS is generated.
        String txId = "4ba4d1f1acf7c6648ced8824aa2cd3e8f836f59e7071340e0c440d099a508cff";
        int index = 0;
        // The claimable GAS amount.
        String claimValue = "7264";
        RawTransactionOutput output = new RawTransactionOutput(0, GASAsset.HASH_ID, claimValue, adr);
        RawTransactionInput input = new RawTransactionInput(txId, index);
        ClaimTransaction tx = new ClaimTransaction(null, Arrays.asList(output), Arrays.asList(input), null);

        // serialize the base raw transaction
        byte[] rawTxUnsignedArray = tx.toArray();

        // Create the Invocation Script
        List<RawInvocationScript> rawInvocationScriptList = Arrays.asList(
                new RawInvocationScript(Sign.signMessage(rawTxUnsignedArray, ecKeyPair)));

        // Create the Verification Script
        byte[] publicKeyByteArray = Numeric.hexStringToByteArray(
                Numeric.toHexStringNoPrefix(ecKeyPair.getPublicKey()));
        RawVerificationScript verificationScript = Keys.getVerificationScriptFromPublicKey(publicKeyByteArray);

        // give the invocation and verification script to the raw transaction:
        tx.addScript(rawInvocationScriptList, verificationScript);

        byte[] rawTxSignedArray = tx.toArray();
        String rawTransactionHexString = Numeric.toHexStringNoPrefix(rawTxSignedArray);
        System.out.println("rawTransactionHexString: " + rawTransactionHexString);

    }

}
