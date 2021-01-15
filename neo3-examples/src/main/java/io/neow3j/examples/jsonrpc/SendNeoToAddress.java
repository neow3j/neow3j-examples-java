package io.neow3j.examples.jsonrpc;

import io.neow3j.contract.NeoToken;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.methods.response.Transaction;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.utils.Await;

import java.io.IOException;

public class SendNeoToAddress {

    public static void main(String[] args) throws IOException {
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:40332"));

        String assetId = NeoToken.SCRIPT_HASH.toString();
        String toAddress = "NWcx4EfYdfqn5jNjDz8AHE6hWtWdUGDdmy";

        Transaction tx = neow3j
                .sendToAddress(assetId, toAddress, "2400")
                .send()
                .getSendToAddress();
        Await.waitUntilTransactionIsExecuted(tx.getHash(), neow3j);

        System.out.println("\n####################");
        System.out.println("Tx:    " + tx.getHash());
        System.out.println("Asset: " + assetId);
        System.out.println("From:  " + tx.getSender());
        System.out.println("To:    " + toAddress);
        System.out.println("####################");
    }
}
