package io.neow3j.examples.jsonrpc;

import static io.neow3j.examples.Constants.BOB;
import static io.neow3j.examples.Constants.NEOW3J;

import java.io.IOException;
import java.math.BigInteger;

import io.neow3j.contract.Hash160;
import io.neow3j.contract.NeoToken;
import io.neow3j.protocol.core.methods.response.NeoOpenWallet;
import io.neow3j.protocol.core.methods.response.Transaction;

public class SendNeoToAddress {

    public static void main(String[] args) throws IOException {
        NeoOpenWallet resp = NEOW3J.openWallet("Alice.wallet.json", "neo").send();
        if (resp.hasError()) {
            System.out.println("Couldn't open wallet. Error message was: '"
                    + resp.getError().getMessage() + "'");
            return;
        }

        Hash160 assetId = NeoToken.SCRIPT_HASH;
        Transaction tx = NEOW3J.sendToAddress(assetId, BOB.getScriptHash(), new BigInteger("2"))
                .send()
                .getSendToAddress();

        System.out.println("\n####################");
        System.out.println("Issued transaction with hash " + tx.getHash().toString());
        System.out.println("####################");
    }
}

