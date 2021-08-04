package io.neow3j.examples.contractinvoke;

import static io.neow3j.examples.Constants.NEOW3J;
import static io.neow3j.examples.Constants.WALLET;
import static io.neow3j.examples.Utils.trackSentTransaction;

import io.neow3j.contract.NeoURI;
import io.neow3j.transaction.TransactionBuilder;
import io.neow3j.protocol.core.response.NeoSendRawTransaction;

public class TransferFromNeoURI {

    public static void main(String[] args) throws Throwable {

        TransactionBuilder b =
                NeoURI.fromURI("neo:NUrPrFLETzoe7N2FLi2dqTvLwc9L2Em84K?asset=neo&amount=1")
                        .neow3j(NEOW3J)
                        .wallet(WALLET)
                        .buildTransfer();
        NeoSendRawTransaction response = b.sign().send();

        trackSentTransaction(response);
    }
}
