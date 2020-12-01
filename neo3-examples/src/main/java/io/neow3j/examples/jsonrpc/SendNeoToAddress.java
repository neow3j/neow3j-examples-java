package io.neow3j.examples.jsonrpc;

import io.neow3j.contract.NeoToken;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.methods.response.Transaction;
import io.neow3j.protocol.http.HttpService;

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

        System.out.println("\n####################");
        System.out.println("tx: " + tx.getHash());
        System.out.println("asset: " + assetId);
        System.out.println("from: " + tx.getSender());
        System.out.println("to: " + toAddress);
        System.out.println("####################");
    }
}
