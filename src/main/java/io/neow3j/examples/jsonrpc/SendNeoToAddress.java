package io.neow3j.examples.jsonrpc;

import static io.neow3j.examples.Constants.BOB;
import static io.neow3j.examples.Constants.NEOW3J;

import java.io.IOException;
import java.math.BigInteger;

import io.neow3j.protocol.core.response.NeoSendToAddress;
import io.neow3j.protocol.exceptions.ErrorResponseException;
import io.neow3j.types.Hash160;
import io.neow3j.contract.NeoToken;
import io.neow3j.protocol.core.response.NeoOpenWallet;

public class SendNeoToAddress {

    public static void main(String[] args) throws IOException, ErrorResponseException {

        // This example requires that the wallet of Alice's account is open on the node. Thus,
        // first the wallet is opened with the `openwallet` RPC method.
        NeoOpenWallet resp = NEOW3J.openWallet("Alice.wallet.json", "neo").send();
        if (resp.hasError()) {
            System.out.println("Couldn't open wallet. Error message was: '"
                    + resp.getError().getMessage() + "'");
            return;
        }

        // Then the `sendtoaddress` RPC method is used to transfer NEO from the opened wallet.
        // This requires that Alice's account owns some NEO.
        Hash160 assetId = NeoToken.SCRIPT_HASH;
        NeoSendToAddress response = NEOW3J.sendToAddress(
                assetId, BOB.getScriptHash(), new BigInteger("2")).send();

        if (response.hasError()) {
            System.out.println("Call resulted in an error with message:");
            System.out.println("\"" + response.getError().getMessage() + "\"");
            return;
        }

        System.out.println("\n####################");
        System.out.println("Issued transaction with hash " + response.getSendToAddress().getHash()
                .toString());
        System.out.println("####################");
    }
}

