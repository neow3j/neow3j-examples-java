package io.neow3j.examples.jsonrpc;

import io.neow3j.types.Hash256;

import java.io.IOException;

import static io.neow3j.examples.Constants.NEOW3J;

public class GetRawTransaction {

    public static void main(String[] args) throws IOException {

        Hash256 txId = new Hash256("b76bcfd718b2099a43e09b72e624a1e0d789958d7c8ea86c75912ba379fe21a9");
        String rawTransaction = NEOW3J.getRawTransaction(txId)
                .send()
                .getRawTransaction();

        System.out.println("\n####################");
        System.out.println("Raw Tx: " + rawTransaction);
        System.out.println("####################");
    }
}
