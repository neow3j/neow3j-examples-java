package io.neow3j.examples.jsonrpc;

import io.neow3j.contract.Hash160;
import io.neow3j.contract.NeoToken;

import java.io.IOException;
import java.math.BigInteger;

import static io.neow3j.examples.Constants.BOB;
import static io.neow3j.examples.Constants.NEOW3J;

public class SendNeoToAddress {

    public static void main(String[] args) throws IOException {

        Hash160 assetId = NeoToken.SCRIPT_HASH;

        NEOW3J.sendToAddress(assetId, BOB.getScriptHash(), new BigInteger("2"))
                .send()
                .getSendToAddress();

        System.out.println("\n####################");
        System.out.println("Transaction issued.");
        System.out.println("####################");
    }
}

