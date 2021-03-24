package io.neow3j.examples.contractinvoke;

import static io.neow3j.examples.Constants.NEOW3J;
import static io.neow3j.examples.Constants.WALLET;
import io.neow3j.contract.Hash256;
import io.neow3j.contract.NeoURI;
import io.neow3j.contract.TransactionBuilder;
import io.neow3j.protocol.core.methods.response.NeoSendRawTransaction;
import io.neow3j.utils.Await;

public class TransferFromNeoURI {

    public static void main(String[] args) throws Throwable {

        TransactionBuilder b = NeoURI.fromURI("neo:NUrPrFLETzoe7N2FLi2dqTvLwc9L2Em84K?asset=neo&amount=1")
                .neow3j(NEOW3J)
                .wallet(WALLET)
                .buildTransfer();
        NeoSendRawTransaction response = b.sign().send();

        System.out.println("####################\n");
        if (response.hasError()) {
            System.out.printf("The neo-node responded with the error message '%s'.%n", response.getError());
        } else {
            Hash256 txHash = response.getSendRawTransaction().getHash();
            System.out.printf("Successfully transmitted the transaction with hash '%s'.%n", txHash);
            Await.waitUntilTransactionIsExecuted(txHash, NEOW3J);
            System.out.println("Tx: " + NEOW3J.getTransaction(txHash).send().getTransaction().toString());
            System.out.println("\n####################");
        }
    }
}
