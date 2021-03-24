package io.neow3j.examples.jsonrpc;

import io.neow3j.contract.GasToken;
import io.neow3j.contract.NeoToken;
import io.neow3j.protocol.core.methods.response.NeoAddress;
import io.neow3j.protocol.core.methods.response.NeoOpenWallet;
import java.io.IOException;
import java.util.List;

import static io.neow3j.examples.Constants.NEOW3J;

public class GetWalletState {

    public static void main(String[] args) throws IOException {

        // Note: Before you can run this example successful with the Neo Devtracker and Neo Express,
        // you will have to export Alice' wallet with the following commmand:
        // `neoxp wallet export Alice`
        // When prompted, input the password "neo".
        
        NeoOpenWallet resp = NEOW3J.openWallet("Alice.wallet.json", "neo").send();
        if (resp.hasError()) {
            System.out.println("Couldn't open wallet.");
            return;
        }

        List<NeoAddress> addresses = NEOW3J.listAddress()
                .send()
                .getAddresses();

        System.out.println("\n####################");

        if (addresses != null) {
            System.out.println("Nr of addresses in your wallet: " + addresses.size());
            if (!addresses.isEmpty()) {
                String neoBalance = NEOW3J.getWalletBalance(NeoToken.SCRIPT_HASH.toString())
                        .send()
                        .getWalletBalance().getBalance();

                String gasBalance = NEOW3J.getWalletBalance(GasToken.SCRIPT_HASH.toString())
                        .send()
                        .getWalletBalance().getBalance();

                System.out.println("State of your first address: " + addresses.get(0).toString());
                System.out.println("Neo balance of your wallet: " + neoBalance);
                System.out.println("Gas balance of your wallet: " + gasBalance);
            }
        } else {
            System.out.println("Wallet access is denied.");
        }

        System.out.println("####################");
    }
}
