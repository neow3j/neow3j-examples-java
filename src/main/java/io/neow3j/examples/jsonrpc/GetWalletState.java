package io.neow3j.examples.jsonrpc;

import io.neow3j.contract.GasToken;
import io.neow3j.contract.NeoToken;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.response.NeoAddress;
import io.neow3j.protocol.core.response.NeoOpenWallet;

import java.io.IOException;
import java.util.List;

import static io.neow3j.examples.Constants.NEOW3J_PRIVATENET;

public class GetWalletState {

    // The neow3j instance used in this example.
    static final Neow3j neow3j = NEOW3J_PRIVATENET;

    public static void main(String[] args) throws IOException {

        NeoOpenWallet resp = neow3j.openWallet("Alice.wallet.json", "neo").send();
        if (resp.hasError()) {
            System.out.println("Couldn't open wallet. Error message was: '" + resp.getError().getMessage() + "'");
            return;
        }

        List<NeoAddress> addresses = neow3j.listAddress()
                .send()
                .getAddresses();

        System.out.println("\n####################");

        if (addresses != null) {
            System.out.println("Nr of addresses in your wallet: " + addresses.size());
            if (!addresses.isEmpty()) {
                String neoBalance = neow3j.getWalletBalance(NeoToken.SCRIPT_HASH)
                        .send()
                        .getWalletBalance().getBalance();

                String gasBalance = neow3j.getWalletBalance(GasToken.SCRIPT_HASH)
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
