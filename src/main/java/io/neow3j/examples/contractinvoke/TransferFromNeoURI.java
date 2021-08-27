package io.neow3j.examples.contractinvoke;

import io.neow3j.contract.NeoURI;
import io.neow3j.protocol.core.response.NeoSendRawTransaction;

import static io.neow3j.examples.Constants.ALICE;
import static io.neow3j.examples.Constants.GENESIS;
import static io.neow3j.examples.Constants.NEOW3J;
import static io.neow3j.examples.Utils.trackSentTransaction;

public class TransferFromNeoURI {

    public static void main(String[] args) throws Throwable {

        NeoSendRawTransaction response =
                NeoURI.fromURI("neo:NUrPrFLETzoe7N2FLi2dqTvLwc9L2Em84K?asset=neo&amount=1")
                        .neow3j(NEOW3J)
                        .buildTransferFrom(ALICE)
                        .sign()
                        .send();

        trackSentTransaction(response);
    }
}
