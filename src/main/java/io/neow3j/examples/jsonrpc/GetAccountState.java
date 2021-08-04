package io.neow3j.examples.jsonrpc;

import io.neow3j.model.types.GASAsset;
import io.neow3j.model.types.NEOAsset;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.methods.response.NeoGetAccountState;
import io.neow3j.protocol.http.HttpService;

import java.io.IOException;

public class GetAccountState {

    public static void main(String[] args) throws IOException {
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:30333"));

        NeoGetAccountState getAccountState = neow3j.getAccountState("AK2nJJpJr6o664CWJKi1QRXjqeic2zRp8y").send();

        getAccountState.getAccountState().getBalances().stream().forEach((balance) -> {

            System.out.println("\n####################");
            System.out.println("Asset Address/Name: " + convertToNameIfKnown(balance.getAssetAddress()));
            System.out.println("Balance: " + balance.getValue());
        });
    }

    public static String convertToNameIfKnown(String address) {
        if (address.contains(NEOAsset.HASH_ID)) {
            return NEOAsset.NAME;
        } else if (address.contains(GASAsset.HASH_ID)) {
            return GASAsset.NAME;
        }
        return address;
    }
}
