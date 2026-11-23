package io.neow3j.examples.contractinvoke;

import java.math.BigInteger;

import io.neow3j.contract.NeoToken;
import io.neow3j.protocol.Neow3j;
import io.neow3j.transaction.TransactionBuilder;
import io.neow3j.transaction.TransactionSubmission;
import io.neow3j.types.NeoVMStateType;
import io.neow3j.protocol.core.response.NeoApplicationLog;
import io.neow3j.transaction.Transaction;

import static io.neow3j.examples.Constants.ALICE;
import static io.neow3j.examples.Constants.BOB;
import static io.neow3j.examples.Constants.neow3jPrivatenet;

public class TransactionTracking {

    // The neow3j instance used in this example.
    static final Neow3j neow3j = neow3jPrivatenet();

    public static void main(String[] args) throws Throwable {
        TransactionBuilder b = new NeoToken(neow3j).transfer(ALICE, BOB.getScriptHash(), new BigInteger("1"));
        Transaction tx = b.sign();
        TransactionSubmission txSubmission = tx.send();

        if (txSubmission.getError() == null) {
            System.out.println("Transaction sent successfully - starting tracking.");
            // track the transaction and print the application log.
            txSubmission.track().subscribe(blockIndex -> {
                System.out.println("\n####################");
                NeoApplicationLog log = txSubmission.getApplicationLog();
                NeoVMStateType state = log.getExecutions().get(0).getState();
                System.out.printf("Found the transaction on block %s. It exited with state %s.\n", blockIndex, state);
                System.out.println(log);
                System.out.println("####################");
                neow3j.shutdown();
            });
        }
    }

}
