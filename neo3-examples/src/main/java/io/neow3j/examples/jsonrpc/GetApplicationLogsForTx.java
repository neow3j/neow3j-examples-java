package io.neow3j.examples.jsonrpc;

import java.io.IOException;
import java.util.Arrays;

import io.neow3j.model.NeoConfig;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.methods.response.NeoGetApplicationLog;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.wallet.Account;
import io.neow3j.wallet.Wallet;

public class GetApplicationLogsForTx {

    public static void main(String[] args) throws IOException {
        // Set up the connection to the neo-node
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:40332"));

        NeoGetApplicationLog al = neow3j
            .getApplicationLog("f8ceb2a490f5412185f2a8899f8e38c7272db4a84076c5257dc71d356e516e72")
            .send();

        System.out.println("\n####################");
        System.out.println("Application log: ");
        System.out.println(al.getApplicationLog());
        System.out.println("\n####################");
            
    }

}
