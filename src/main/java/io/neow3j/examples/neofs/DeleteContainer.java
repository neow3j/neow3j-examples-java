package io.neow3j.examples.neofs;

import io.neow3j.neofs.sdk.NeoFSClient;
import io.neow3j.wallet.Account;

import static io.neow3j.examples.Constants.ALICE;
import static io.neow3j.examples.Constants.NEOFS_GRPC_ENDPOINT_TESTNET;

public class DeleteContainer {

    public static void main(String[] args) throws Exception {
        Account ownerAccount = ALICE;

        // Load the shared library file based on your architecture, e.g., linux-amd64.
        NeoFSClient neofsClient = NeoFSClient.loadAndInitialize(ownerAccount, NEOFS_GRPC_ENDPOINT_TESTNET);

        // Todo: Paste the containerId here.
        String containerId = "";

        boolean deleted = neofsClient.deleteContainer(containerId);
        if (deleted) {
            System.out.printf("Container '%s' has been deleted.%n", containerId);
        }
    }

}
