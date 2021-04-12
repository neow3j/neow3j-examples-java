package io.neow3j.examples.jsonrpc;

import static io.neow3j.examples.Constants.BOB;
import static io.neow3j.examples.Constants.NEOW3J;
import java.io.IOException;
import java.math.BigInteger;
import io.neow3j.contract.Hash160;
import io.neow3j.contract.NeoToken;

public class SendNeoToAddress {

    public static void main(String[] args) throws IOException {

        Hash160 neoToken = NeoToken.SCRIPT_HASH;
        Hash160 receiver = BOB.getScriptHash();

        NEOW3J.sendToAddress(neoToken, receiver, new BigInteger("2"))
                .send()
                .getSendToAddress();

        System.out.println("\n####################");
        System.out.println("Transaction issued.");
        System.out.println("####################");
    }
}
