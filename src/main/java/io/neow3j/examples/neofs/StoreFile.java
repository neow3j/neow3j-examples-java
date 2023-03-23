package io.neow3j.examples.neofs;

import io.neow3j.neofs.sdk.NeoFSClient;
import io.neow3j.wallet.Account;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.neow3j.examples.Constants.ALICE;
import static io.neow3j.examples.Constants.NEOFS_GRPC_ENDPOINT_TESTNET;

public class StoreFile {

    public static void main(String[] args) throws Throwable {
        Account ownerAccount = ALICE;

        // Load the shared library file based on your architecture, e.g., linux-amd64.
        NeoFSClient neofsClient = NeoFSClient.loadAndInitialize(ownerAccount, NEOFS_GRPC_ENDPOINT_TESTNET);

        // Todo: Create a simple container in "CreateContainer.java" and paste its containerId here.
        String containerId = "";

        // Read the file bytes you want to store in NeoFS and create an object in your container.
        String filename = "neow3j-neo3.png";
        String neow3jImage = Paths.get(filename).toString();
        File neow3jFile = new File(StoreFile.class.getClassLoader().getResource(neow3jImage).toURI());
        byte[] fileBytes = Files.readAllBytes(neow3jFile.toPath());

        // If you want to store a String, you can encode it into a sequence of bytes, e.g., using the UTF_8 charset.
//        String stringToStoreInObject = "hello world!";
//        byte[] fileBytes = stringToStoreInObject.getBytes(StandardCharsets.UTF_8);

        // There's no await function yet that checks if the container has persisted yet. Coming soon!
        Thread.sleep(20000);

        // Create an object with the file bytes
        String objectId = neofsClient.createObject(containerId, fileBytes, ownerAccount);

        System.out.printf("Object '%s' was created in the container '%s'.%n", objectId, containerId);
    }

}
