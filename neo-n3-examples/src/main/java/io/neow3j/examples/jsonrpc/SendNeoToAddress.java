package io.neow3j.examples.jsonrpc;

import io.neow3j.contract.NeoToken;

import java.io.IOException;

import static io.neow3j.examples.Constants.BOB;
import static io.neow3j.examples.Constants.NEOW3J;

public class SendNeoToAddress {

    public static void main(String[] args) throws IOException {

        String assetId = NeoToken.SCRIPT_HASH.toString();
        String toAddress = BOB.getAddress();

        NEOW3J.sendToAddress(assetId, toAddress, "2")
                .send()
                .getSendToAddress();

        System.out.println("\n####################");
        System.out.println("Transaction issued.");
        System.out.println("####################");
    }
}
