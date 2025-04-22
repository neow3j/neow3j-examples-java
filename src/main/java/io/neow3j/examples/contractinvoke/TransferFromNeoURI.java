package io.neow3j.examples.contractinvoke;

import io.neow3j.contract.NeoURI;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.response.NeoSendRawTransaction;

import static io.neow3j.examples.Constants.ALICE;
import static io.neow3j.examples.Constants.neow3jPrivatenet;
import static io.neow3j.examples.Utils.trackSentTransaction;

public class TransferFromNeoURI {

    // The neow3j instance used in this example.
    static final Neow3j neow3j = neow3jPrivatenet();

    public static void main(String[] args) throws Throwable {

        NeoSendRawTransaction response =
                NeoURI.fromURI("neo:NUrPrFLETzoe7N2FLi2dqTvLwc9L2Em84K?asset=neo&amount=1")
                        .neow3j(neow3j)
                        .buildTransferFrom(ALICE)
                        .sign()
                        .send();

        trackSentTransaction(response, neow3j);
    }

}
