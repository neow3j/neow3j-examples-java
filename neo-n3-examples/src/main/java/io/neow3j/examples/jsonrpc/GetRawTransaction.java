package io.neow3j.examples.jsonrpc;

import io.neow3j.contract.Hash256;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.http.HttpService;

import java.io.IOException;

public class GetRawTransaction {

    public static void main(String[] args) throws IOException {
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:40332"));

        Hash256 txId = new Hash256("b76bcfd718b2099a43e09b72e624a1e0d789958d7c8ea86c75912ba379fe21a9");
        String rawTransaction = neow3j.getRawTransaction(txId)
                .send()
                .getRawTransaction();

        System.out.println("\n####################");
        System.out.println("Raw Tx: " + rawTransaction);
        System.out.println("####################");
    }
}
