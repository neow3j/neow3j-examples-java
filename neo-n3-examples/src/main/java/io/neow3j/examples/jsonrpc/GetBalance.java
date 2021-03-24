package io.neow3j.examples.jsonrpc;

import static io.neow3j.examples.Constants.NEOW3J;
import static java.util.Collections.singletonList;
import io.neow3j.contract.FungibleToken;
import io.neow3j.contract.Hash160;
import io.neow3j.wallet.Account;

public class GetBalance {

    public static void main(String[] args) throws Throwable {

        // Setup a wrapper to invoke the contract.
        FungibleToken contract = new FungibleToken(
            new Hash160("0xef4073a0f2b305a38ec4050e4d3d28bc40ea63f5"), NEOW3J);

        Account a = Account.fromWIF("L24Qst64zASL2aLEKdJtRLnbnTbqpcRNWkWJ3yhDh2CLUtLdwYK2");
        Account committee = Account.createMultiSigAccount(singletonList(a.getECKeyPair().getPublicKey()), 1);
        System.out.println("\n####################");
        System.out.println("NEO balance of the default account is " + contract.getBalanceOf(committee.getScriptHash()).intValue());

        Hash160 receiver = new Hash160("d6c712eb53b1a130f59fd4e5864bdac27458a509");
        System.out.println("NEO balance of the receiver account is " + contract.getBalanceOf(receiver).intValue());
        System.out.println("####################");
    }
}
