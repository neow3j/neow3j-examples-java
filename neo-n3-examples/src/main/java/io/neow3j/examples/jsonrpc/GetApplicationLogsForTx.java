package io.neow3j.examples.jsonrpc;

import static io.neow3j.examples.Constants.NEOW3J;
import java.io.IOException;
import io.neow3j.contract.Hash256;
import io.neow3j.protocol.core.methods.response.NeoGetApplicationLog;

public class GetApplicationLogsForTx {

    public static void main(String[] args) throws IOException {

        NeoGetApplicationLog al = NEOW3J
                .getApplicationLog(new Hash256("f8ceb2a490f5412185f2a8899f8e38c7272db4a84076c5257dc71d356e516e72"))
                .send();

        System.out.println("\n####################");
        System.out.println("Application log: ");
        System.out.println(al.getApplicationLog());
        System.out.println("\n####################");

    }

}
