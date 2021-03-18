package io.neow3j.examples.jsonrpc;

import io.neow3j.contract.Hash256;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.methods.response.Transaction;
import io.neow3j.protocol.http.HttpService;

import java.io.IOException;

public class GetTransaction {

    public static void main(String[] args) throws IOException {
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:40332"));

        Hash256 txId = new Hash256("b76bcfd718b2099a43e09b72e624a1e0d789958d7c8ea86c75912ba379fe21a9");
        Transaction tx = neow3j.getTransaction(txId)
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
