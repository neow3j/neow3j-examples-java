package io.neow3j.examples;

import io.neow3j.contract.Hash256;
import io.neow3j.protocol.core.methods.response.NeoSendRawTransaction;
import io.neow3j.utils.Await;

import java.io.IOException;

import static io.neow3j.examples.Constants.NEOW3J;

public class Utils {

    public static void trackSentTransaction(NeoSendRawTransaction response) throws IOException {
        System.out.println("####################\n");
        if (response.hasError()) {
            System.out.printf("The neo-node responded with the error message '%s'.%n",
                    response.getError());
        } else {
            Hash256 txHash = response.getSendRawTransaction().getHash();
            System.out.printf("Successfully transmitted the transaction with hash '%s'.%n", txHash);
            Await.waitUntilTransactionIsExecuted(txHash, NEOW3J);
            System.out.println(
                    "Tx: " + NEOW3J.getTransaction(txHash).send().getTransaction().toString());
            System.out.println("\n####################");
        }
    }
}
