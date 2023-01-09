package io.neow3j.examples.neofs;

import io.neow3j.neofs.sdk.NeoFSClient;
import io.neow3j.wallet.Account;

import static io.neow3j.examples.Constants.ALICE;
import static io.neow3j.examples.Constants.NEOFS_GRPC_ENDPOINT_TESTNET;

public class DeleteFile {

    public static void main(String[] args) throws Exception {
        Account ownerAccount = ALICE;

        // Load the shared library file based on your architecture, e.g., linux-amd64.
        NeoFSClient neofsClient = NeoFSClient.loadAndInitialize(ownerAccount, NEOFS_GRPC_ENDPOINT_TESTNET);

        // Todo: Paste the containerId here.
        String containerId = "";
        // Todo: Paste the objectId here.
        String objectId = "";

        // Delete the object from the container. A tombstone object is created that marks the object to be deleted.
        String tombstoneId = neofsClient.deleteObject(containerId, objectId, ownerAccount);
        System.out.printf("Tombstone object '%s' was created in the container '%s'.%n", tombstoneId, containerId);
    }

}
