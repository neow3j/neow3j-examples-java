package io.neow3j.examples.jsonrpc;

import io.neow3j.contract.FungibleToken;
import io.neow3j.contract.ScriptHash;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.wallet.Account;

public class GetBalance {

    public static void main(String[] args) throws Throwable {
        // Set up the connection to the neo-node.
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:40332"));

        // Setup a wrapper to invoke the contract.
        FungibleToken contract = new FungibleToken(
            new ScriptHash("3e1c7c20b1ddbb998b1048061e7665c426b85b14"), neow3j);

        Account account = Account.fromWIF("L3kCZj6QbFPwbsVhxnB8nUERDy4mhCSrWJew4u5Qh5QmGMfnCTda");
        System.out.println("\n####################");
        System.out.println("BCT balance of main-account is " + contract.getBalanceOf(account.getScriptHash()).intValue());

        ScriptHash receiver = new ScriptHash("d6c712eb53b1a130f59fd4e5864bdac27458a509");
        System.out.println("BCT balance of receiver account is " + contract.getBalanceOf(receiver).intValue());
        System.out.println("####################");
    }
}
