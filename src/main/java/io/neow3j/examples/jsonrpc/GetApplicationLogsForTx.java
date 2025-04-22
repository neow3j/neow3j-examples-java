package io.neow3j.examples.jsonrpc;

import java.io.IOException;

import io.neow3j.protocol.Neow3j;
import io.neow3j.types.Hash256;
import io.neow3j.protocol.core.response.NeoGetApplicationLog;

import static io.neow3j.examples.Constants.neow3jPrivatenet;

public class GetApplicationLogsForTx {

    // The neow3j instance used in this example.
    static final Neow3j neow3j = neow3jPrivatenet();

    public static void main(String[] args) throws IOException {

        Hash256 txHash = new Hash256("92fb5decb03055cea8edbeb2c1c1c2ae0e9b5aa23df24922867e9210a358e560");
        NeoGetApplicationLog logResponse = neow3j.getApplicationLog(txHash).send();

        System.out.println("\n####################\n");
        System.out.println(logResponse.getApplicationLog());
        System.out.println("\n####################");
    }

}
