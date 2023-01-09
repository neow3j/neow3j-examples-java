package io.neow3j.examples.neofs;

import io.neow3j.neofs.sdk.BasicACL;
import io.neow3j.neofs.sdk.NeoFSClient;
import io.neow3j.neofs.sdk.NeoFSHelper;
import io.neow3j.wallet.Account;
import neo.fs.v2.container.Types;

import java.util.Date;

import static io.neow3j.examples.Constants.ALICE;
import static io.neow3j.examples.Constants.NEOFS_GRPC_ENDPOINT_TESTNET;

public class CreateContainer {

    public static void main(String[] args) throws Exception {
        Account ownerAccount = ALICE;

        // Load the shared library file based on your architecture, e.g., linux-amd64.
        NeoFSClient neofsClient = NeoFSClient.loadAndInitialize(ownerAccount, NEOFS_GRPC_ENDPOINT_TESTNET);

        // Create a simple container
        Types.Container simpleContainer = CreateContainer.createSimpleContainer(ownerAccount);
        String containerId = neofsClient.createContainer(simpleContainer);
        System.out.printf("Container '%s' was created.%n", containerId);
    }

    /**
     * Creates a fully-public container without extended access control, one replica and no backups.
     *
     * @param ownerAccount the owner of the container.
     * @return a container instance.
     */
    protected static Types.Container createSimpleContainer(Account ownerAccount) {
        neo.fs.v2.refs.Types.Version version = NeoFSHelper.createVersion();
        BasicACL basicAccessControl = BasicACL.PUBLIC_BASIC_NAME;
        int containerBackupFactor = 0;
        int nrReplicas = 1;
        Types.Container.Attribute attribute = Types.Container.Attribute.newBuilder()
                .setKey("CreatedAt")
                .setValue(new Date().toString())
                .build();

        return Types.Container.newBuilder()
                .setVersion(version)
                .setNonce(NeoFSHelper.createNonce())
                .setOwnerId(NeoFSHelper.createOwnerId(ownerAccount))
                .setBasicAcl(basicAccessControl.value())
                .setPlacementPolicy(neo.fs.v2.netmap.Types.PlacementPolicy.newBuilder()
                        .setContainerBackupFactor(containerBackupFactor)
                        .addReplicas(neo.fs.v2.netmap.Types.Replica.newBuilder()
                                .setCount(nrReplicas)
                                .build())
                        .build())
                .addAttributes(attribute)
                .build();
    }

}
