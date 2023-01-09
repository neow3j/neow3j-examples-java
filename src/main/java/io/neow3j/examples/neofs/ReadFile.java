package io.neow3j.examples.neofs;

import io.neow3j.neofs.sdk.NeoFSClient;
import io.neow3j.wallet.Account;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.neow3j.examples.Constants.ALICE;
import static io.neow3j.examples.Constants.NEOFS_GRPC_ENDPOINT_TESTNET;

public class ReadFile {

    public static void main(String[] args) throws Exception {
        Account ownerAccount = ALICE;

        // Load the shared library file based on your architecture, e.g., linux-amd64.
        NeoFSClient neofsClient = NeoFSClient.loadAndInitialize(ownerAccount, NEOFS_GRPC_ENDPOINT_TESTNET);

        // Todo: Paste the containerId here.
        String containerId = "";
        // Todo: Paste the objectId here.
        String objectId = "";

        byte[] readBytes = neofsClient.readObject(containerId, objectId, ownerAccount);

        // Recreates the read bytes into a new file in the resource folder.
        // Todo: Change the filename based on what type of object was stored in NeoFS.
        String filename2 = "read-from-neofs.png";
        Path newFile = Paths.get("src", "main", "resources", filename2).toFile().toPath();
        Files.write(newFile, readBytes);

        // Now check the /resources folder for a file called "read-from-neofs.png".

        // If you stored a String (UTF_8) and not a file, you can just encode the read byte array.
//        String storedString = new String(readBytes);
//        System.out.printf("Object value is '%s'.%n", storedString);
    }

}
