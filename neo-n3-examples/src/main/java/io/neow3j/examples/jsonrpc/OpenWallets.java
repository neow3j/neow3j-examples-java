package io.neow3j.examples.jsonrpc;

import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.http.HttpService;

import java.io.IOException;

public class OpenWallets {

    public static void main(String[] args) throws IOException {
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:40332"));
        Boolean consensusWalletOpen = neow3j.openWallet("wallet.json", "neo").send().getOpenWallet();

        System.out.println("\n####################");

        // Open consensus wallet
        if (consensusWalletOpen) {
            System.out.println("Consensus wallet opened successfully!");
        } else {
            System.out.println("Consensus wallet could not be opened - check your wallet file and password!\n");
        }

        // Open wallet of client 1
        neow3j = Neow3j.build(new HttpService("http://localhost:10332"));
        Boolean client1WalletOpen = neow3j.openWallet("wallet.json", "neo").send().getOpenWallet();
        if (client1WalletOpen) {
            System.out.println("Client 1 wallet opened successfully!");
        } else {
            System.out.println("Client 1 wallet could not be opened - check your wallet file and password!");
        }

        // Open wallet of client 2
        neow3j = Neow3j.build(new HttpService("http://localhost:20332"));
        Boolean client2WalletOpen = neow3j.openWallet("wallet.json", "neo").send().getOpenWallet();
        if (client2WalletOpen) {
            System.out.println("Client 2 wallet opened successfully!");
        } else {
            System.out.println("Client 2 wallet could not be opened - check your wallet file and password!");
        }

        System.out.println("####################");
    }
}
