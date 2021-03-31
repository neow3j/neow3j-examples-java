package io.neow3j.examples.jsonrpc;

import static io.neow3j.examples.Constants.ALICE;
import static io.neow3j.examples.Constants.NEOW3J;

import io.neow3j.contract.FungibleToken;
import io.neow3j.contract.Hash160;

public class GetBalance {

    public static void main(String[] args) throws Throwable {

        // Setup a wrapper to invoke the contract.
        FungibleToken token = new FungibleToken(
            new Hash160("0xef4073a0f2b305a38ec4050e4d3d28bc40ea63f5"), NEOW3J);

        System.out.println("\n####################");

        int balance = token.getBalanceOf(ALICE.getScriptHash()).intValue();

        String symbol = token.getSymbol();

        System.out.println(symbol + " balance of the Alice's account is " + balance);
        System.out.println("####################");
    }
}
