package io.neow3j.examples.jsonrpc;

import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.methods.response.NeoGetTransaction;
import io.neow3j.protocol.http.HttpService;

import java.io.IOException;

public class GetTransaction {

    public static void main(String[] args) throws IOException {
        Neow3j neow3j = Neow3j.build(new HttpService("https://seed0.cityofzion.io:443"));

        NeoGetTransaction neoGetTransaction =
                neow3j.getTransaction("d5a63225e419aca0c10520708c531d36d9912eef0e12cec04e29568930cc4bf9").send();

        System.out.println("\n####################");
        System.out.println("Transaction (verbose=1): " + neoGetTransaction.getTransaction());
        System.out.println("####################\n");
    }
}
