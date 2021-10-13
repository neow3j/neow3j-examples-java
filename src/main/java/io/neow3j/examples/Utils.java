package io.neow3j.examples;

import io.neow3j.types.Hash256;
import io.neow3j.protocol.core.response.NeoSendRawTransaction;
import io.neow3j.utils.Await;

import java.io.IOException;

import static io.neow3j.examples.Constants.NEOW3J;

public class Utils {

    public static void trackSentTransaction(NeoSendRawTransaction response) throws IOException {
        System.out.println("####################\n");
        if (response.hasError()) {
            System.out.printf("The neo-node responded with the error message '%s'.%n",
                    response.getError().getMessage());
        } else {
            Hash256 txHash = response.getSendRawTransaction().getHash();
            System.out.printf("Successfully transmitted the transaction with hash '%s'.%n", txHash);
            System.out.println("Waiting until transaction is persisted in a block...");
            Await.waitUntilTransactionIsExecuted(txHash, NEOW3J);
            System.out.println(NEOW3J.getTransaction(txHash).send().getTransaction());
            // To check the transaction's status, you can check its application log.
            // -> see the example `GetApplicationLogsForTx.java`
            System.out.println("\n####################");
        }
    }
}
