package io.neow3j.examples.jsonrpc;

import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.methods.response.Transaction;
import io.neow3j.protocol.http.HttpService;

import java.io.IOException;

public class SendNeoToAddress {

    public static void main(String[] args) throws IOException {
        Neow3j neow3j = Neow3j.build(new HttpService("http:localhost:40332"));

        String assetId = "9bde8f209c88dd0e7ca3bf0af0f476cdd8207789";
        String toAddress = "Aa1rZbE1k8fXTwzaxxsPRtJYPwhDQjWRFZ";
        Transaction sendToAddress = neow3j.sendToAddress(assetId,
                toAddress,
                "2400").send().getSendToAddress();

        System.out.println("\n####################");
        System.out.println("tx: " + sendToAddress.getHash());
        System.out.println("asset: " + assetId);
        System.out.println("from: " + sendToAddress.getSender());
        System.out.println("to: " + toAddress);
        System.out.println("####################");
    }
}
