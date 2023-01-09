package io.neow3j.examples.neofs;

import io.neow3j.neofs.sdk.NeoFSClient;
import io.neow3j.neofs.sdk.dto.EndpointResponse;
import io.neow3j.wallet.Account;
import neo.fs.v2.netmap.Types;

import static io.neow3j.examples.Constants.ALICE;
import static io.neow3j.examples.Constants.NEOFS_GRPC_ENDPOINT_TESTNET;

public class GetNetmapInfo {

    public static void main(String[] args) throws Exception {
        Account account = ALICE;

        // Load the shared library file based on your architecture, e.g., linux-amd64.
        NeoFSClient neofsClient = NeoFSClient.loadAndInitialize(account, NEOFS_GRPC_ENDPOINT_TESTNET);

        // Get information about the endpoint.
        EndpointResponse endpoint = neofsClient.getEndpoint();
        System.out.printf("Endpoint version:%n%s%n", endpoint.getVersion());
        System.out.printf("Endpoint node info:%n%s%n", endpoint.getNodeInfo());

        // Get information about the network.
        Types.NetworkInfo networkInfo = neofsClient.getNetworkInfo();
        System.out.printf("Network info:%n%s%n", networkInfo);
    }

}
