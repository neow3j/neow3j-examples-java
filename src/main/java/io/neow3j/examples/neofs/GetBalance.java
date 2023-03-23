package io.neow3j.examples.neofs;

import io.neow3j.neofs.sdk.NeoFSClient;
import io.neow3j.wallet.Account;

import static io.neow3j.examples.Constants.ALICE;
import static io.neow3j.examples.Constants.NEOFS_GRPC_ENDPOINT_TESTNET;

public class GetBalance {

    public static void main(String[] args) throws Exception {
        Account account = ALICE;

        // Load the shared library file based on your architecture, e.g., linux-amd64.
        NeoFSClient neofsClient = NeoFSClient.loadAndInitialize(account, NEOFS_GRPC_ENDPOINT_TESTNET);

        // Get the NeoFS balance of the account.
        neo.fs.v2.accounting.Types.Decimal balance = neofsClient.getBalance(account.getECKeyPair().getPublicKey());
        System.out.printf("NeoFS balance of '%s':%n", account.getAddress());
        System.out.println(balance);
    }

}
