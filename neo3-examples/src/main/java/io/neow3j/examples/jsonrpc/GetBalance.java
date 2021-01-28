package io.neow3j.examples.jsonrpc;

import io.neow3j.contract.Nep17Token;
import io.neow3j.contract.ScriptHash;
import io.neow3j.model.NeoConfig;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.wallet.Account;

public class GetBalance {

    public static void main(String[] args) throws Throwable {
        // Set the magic number according to the Neo network's configuration.
        NeoConfig.setMagicNumber(new byte[]{0x01, 0x03, 0x00, 0x0}); // Magic number 769

        // Set up the connection to the neo-node.
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:40332"));

        // Setup a wrapper to invoke the contract.
        Nep17Token contract = new Nep17Token(
            new ScriptHash("3e1c7c20b1ddbb998b1048061e7665c426b85b14"), neow3j);

        Account account = Account.fromWIF("L3kCZj6QbFPwbsVhxnB8nUERDy4mhCSrWJew4u5Qh5QmGMfnCTda");
        System.out.println("\n####################");
        System.out.println("BCT balance of main-account is " + contract.getBalanceOf(account.getScriptHash()).intValue());

        ScriptHash receiver = new ScriptHash("d6c712eb53b1a130f59fd4e5864bdac27458a509");
        System.out.println("BCT balance of receiver account is " + contract.getBalanceOf(receiver).intValue());
        System.out.println("####################");
    }
}
