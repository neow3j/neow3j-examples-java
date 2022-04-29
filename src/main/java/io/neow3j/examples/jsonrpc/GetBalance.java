package io.neow3j.examples.jsonrpc;

import java.math.BigInteger;

import io.neow3j.contract.FungibleToken;
import io.neow3j.types.Hash160;
import io.neow3j.contract.NeoToken;

import static io.neow3j.examples.Constants.ALICE;
import static io.neow3j.examples.Constants.NEOW3J;

public class GetBalance {

    public static void main(String[] args) throws Throwable {

        Hash160 contractHash = NeoToken.SCRIPT_HASH;

        // Set up a wrapper to invoke the contract.
        FungibleToken token = new FungibleToken(contractHash, NEOW3J);

        System.out.println("\n####################");

        BigInteger balance = token.getBalanceOf(ALICE.getScriptHash());

        String symbol = token.getSymbol();

        System.out.println(symbol + " balance of the Alice's account is " + balance.toString());
        System.out.println("####################");
    }

}
