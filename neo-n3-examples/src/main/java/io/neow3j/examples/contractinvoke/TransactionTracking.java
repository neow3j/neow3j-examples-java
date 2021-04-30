package io.neow3j.examples.contractinvoke;

import static io.neow3j.examples.Constants.BOB;
import static io.neow3j.examples.Constants.NEOW3J;
import static io.neow3j.examples.Constants.WALLET;
import java.math.BigInteger;

import io.neow3j.contract.NeoToken;
import io.neow3j.contract.TransactionBuilder;
import io.neow3j.model.types.NeoVMStateType;
import io.neow3j.protocol.core.methods.response.NeoApplicationLog;
import io.neow3j.protocol.core.methods.response.NeoSendRawTransaction;
import io.neow3j.transaction.Transaction;

public class TransactionTracking {

    public static void main(String[] args) throws Throwable {

        TransactionBuilder b = new NeoToken(NEOW3J)
                .transferFromDefaultAccount(WALLET, BOB.getScriptHash(), new BigInteger("1"));

        Transaction tx = b.sign();
        NeoSendRawTransaction send = tx.send();

        if (send.getError() == null) {
            System.out.println("Transaction sent successfully - starting tracking.");
            // track the transaction and print the application log.
            tx.track().subscribe(blockIndex -> {
                System.out.println("\n####################");
                NeoApplicationLog log = tx.getApplicationLog();
                NeoVMStateType state = log.getExecutions().get(0).getState();
                System.out.printf("Found the transaction on block %s. It exited with state %s.\n", blockIndex, state);
                System.out.println(log);
                System.out.println("####################");
                NEOW3J.shutdown();
            });
        }
    }
}
