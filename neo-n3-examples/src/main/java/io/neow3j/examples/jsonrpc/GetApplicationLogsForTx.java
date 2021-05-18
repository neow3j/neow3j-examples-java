package io.neow3j.examples.jsonrpc;

import static io.neow3j.examples.Constants.NEOW3J;

import java.io.IOException;

import io.neow3j.types.Hash256;
import io.neow3j.protocol.core.response.NeoGetApplicationLog;

public class GetApplicationLogsForTx {

    public static void main(String[] args) throws IOException {

        NeoGetApplicationLog al = NEOW3J
                .getApplicationLog(new Hash256("92fb5decb03055cea8edbeb2c1c1c2ae0e9b5aa23df24922867e9210a358e560"))
                .send();

        System.out.println("\n####################\n");
        System.out.println(al.getApplicationLog());
        System.out.println("\n####################");

    }

}
