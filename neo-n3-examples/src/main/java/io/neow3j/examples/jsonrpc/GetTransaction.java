package io.neow3j.examples.jsonrpc;

import io.neow3j.types.Hash256;
import io.neow3j.protocol.core.response.Transaction;

import java.io.IOException;

import static io.neow3j.examples.Constants.NEOW3J;

public class GetTransaction {

    public static void main(String[] args) throws IOException {

        Hash256 txId = new Hash256("b76bcfd718b2099a43e09b72e624a1e0d789958d7c8ea86c75912ba379fe21a9");
        Transaction tx = NEOW3J.getTransaction(txId)
                .send()
                .getTransaction();

        System.out.println("\n####################");
        if (tx == null) {
            System.out.println("No transaction found with tx id = " + txId + ".");
        } else {
            System.out.println("tx: " + tx);
        }
        System.out.println("####################");
    }
}
