package io.neow3j.examples.contractinvoke;

import io.neow3j.contract.NeoURI;
import io.neow3j.protocol.Neow3j;
import io.neow3j.transaction.TransactionSubmission;

import static io.neow3j.examples.Constants.ALICE;
import static io.neow3j.examples.Constants.neow3jPrivatenet;
import static io.neow3j.examples.Utils.trackTransactionSubmission;

public class TransferFromNeoURI {

    // The neow3j instance used in this example.
    static final Neow3j neow3j = neow3jPrivatenet();

    public static void main(String[] args) throws Throwable {

        TransactionSubmission txSubmission =
                NeoURI.fromURI("neo:NUrPrFLETzoe7N2FLi2dqTvLwc9L2Em84K?asset=neo&amount=1")
                        .neow3j(neow3j)
                        .buildTransferFrom(ALICE)
                        .sign()
                        .send();

        trackTransactionSubmission(txSubmission, neow3j);
    }

}
