package io.neow3j.examples.jsonrpc;

import io.neow3j.contract.NeoToken;
import io.neow3j.protocol.core.methods.response.Transaction;
import io.neow3j.utils.Await;

import java.io.IOException;

import static io.neow3j.examples.Constants.NEOW3J;

public class SendNeoToAddress {

    public static void main(String[] args) throws IOException {

        String assetId = NeoToken.SCRIPT_HASH.toString();
        String toAddress = "NWcx4EfYdfqn5jNjDz8AHE6hWtWdUGDdmy";

        Transaction tx = NEOW3J
                .sendToAddress(assetId, toAddress, "2400")
                .send()
                .getSendToAddress();
        Await.waitUntilTransactionIsExecuted(tx.getHash(), NEOW3J);

        System.out.println("\n####################");
        System.out.println("Tx:    " + tx.getHash());
        System.out.println("Asset: " + assetId);
        System.out.println("From:  " + tx.getSender());
        System.out.println("To:    " + toAddress);
        System.out.println("####################");
    }
}
