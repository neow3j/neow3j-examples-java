package io.neow3j.examples.transactions;

import io.neow3j.crypto.transaction.RawScript;
import io.neow3j.crypto.transaction.RawTransactionInput;
import io.neow3j.crypto.transaction.RawTransactionOutput;
import io.neow3j.crypto.transaction.RawVerificationScript;
import io.neow3j.model.types.GASAsset;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.methods.response.NeoSendRawTransaction;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.transaction.ClaimTransaction;
import io.neow3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;

public class CreateClaimTransaction {

    public static void main(String[] args) throws IOException {
        String claimableTxId = "4ba4d1f1acf7c6648ced8824aa2cd3e8f836f59e7071340e0c440d099a508cff";
        int idx = 0;
        String receivingAdr = "AK2nJJpJr6o664CWJKi1QRXjqeic2zRp8y";
        byte[] invocationScript = Numeric.hexStringToByteArray("400c40efd5f4a37b09fb8dca3e9cd6486c1b2d46c0319ac216c348f546ff44bb5fc3a328a43f2f49c9b2aa4cb1ce3f40327fd8403966e117745eb5c1266614f7d4");

        BigInteger publicKey = Numeric.toBigIntNoPrefix("031a6c6fbbdf02ca351745fa86b9ba5a9452d785ac4f7fc2b7548ca2a46c4fcf4a");
        ClaimTransaction signedTx = new ClaimTransaction.Builder().output(new RawTransactionOutput(GASAsset.HASH_ID, "7264", receivingAdr))
                .claim(new RawTransactionInput(claimableTxId, idx))
                .script(new RawScript(invocationScript, RawVerificationScript.fromPublicKey(publicKey).getScript()))
                .build();

        byte[] rawTxSignedArray = signedTx.toArray();
        System.out.println("rawTransactionHexString: " + Numeric.toHexStringNoPrefix(rawTxSignedArray));

//        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:30333"));
//        NeoSendRawTransaction sendRawTx = neow3j.sendRawTransaction(Numeric.toHexStringNoPrefix(rawTxSignedArray)).send();
//        System.out.println("Result: " + sendRawTx.getResult());
    }

}
