package io.neow3j.examples.jsonrpc;

import java.io.IOException;

import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.methods.response.NeoGetNep17Balances;
import io.neow3j.protocol.http.HttpService;

public class GetBalance {
    
    public static void main(String[] args) throws IOException {
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:40332"));

        NeoGetNep17Balances balance = neow3j
            .getNep17Balances("NZNos2WqTbu5oCgyfss9kUJgBXJqhuYAaj").send();

        System.out.println("\n####################");
        System.out.println("Balance: " + balance.getBalances().getBalances());
        System.out.println("######################");
    }

}
