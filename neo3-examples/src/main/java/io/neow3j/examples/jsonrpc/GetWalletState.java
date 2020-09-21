package io.neow3j.examples.jsonrpc;

import io.neow3j.contract.GasToken;
import io.neow3j.contract.NeoToken;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.methods.response.NeoAddress;
import io.neow3j.protocol.http.HttpService;

import java.io.IOException;
import java.util.List;

public class GetWalletState {

    public static void main(String[] args) throws IOException {
        // The consensus node runs on port 40332
        // To display the client 1 wallet state, change the port to 10332
        // To display the client 2 wallet state, change the port to 20332
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:40332"));

        List<NeoAddress> addresses = neow3j.listAddress()
                .send()
                .getAddresses();

        System.out.println("\n####################");
        System.out.println("Nr of addresses in your wallet: " + addresses.size());
        if (!addresses.isEmpty()) {
            String neoBalance = neow3j.getWalletBalance(NeoToken.SCRIPT_HASH.toString())
                    .send()
                    .getWalletBalance().getBalance();

            String gasBalance = neow3j.getWalletBalance(GasToken.SCRIPT_HASH.toString())
                    .send()
                    .getWalletBalance().getBalance();

            System.out.println("State of your first address: " + addresses.get(0).toString());
            System.out.println("Neo balance of your wallet: " + neoBalance);
            System.out.println("Gas balance of your wallet: " + gasBalance);
        }
        System.out.println("####################");
    }
}
