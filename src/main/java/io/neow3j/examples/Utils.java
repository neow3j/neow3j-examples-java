package io.neow3j.examples;

import io.neow3j.compiler.DebugInfo;
import io.neow3j.protocol.ObjectMapperFactory;
import io.neow3j.types.Hash256;
import io.neow3j.protocol.core.response.NeoSendRawTransaction;
import io.neow3j.utils.Await;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static io.neow3j.examples.Constants.NEOW3J;

public class Utils {

    public static void trackSentTransaction(NeoSendRawTransaction response) throws IOException {

        System.out.println("####################");
        if (response.hasError()) {
            System.out.printf("The neo-node responded with the error message '%s'.%n",
                    response.getError().getMessage());
        } else {
            Hash256 txHash = response.getSendRawTransaction().getHash();
            System.out.printf("Successfully transmitted the transaction with hash '%s'.%n", txHash);
            System.out.println("Waiting until transaction is persisted in a block...");
            Await.waitUntilTransactionIsExecuted(txHash, NEOW3J);
            System.out.println(NEOW3J.getTransaction(txHash).send().getTransaction());
            // To check the transaction's status, you can check its application log.
            // -> see the example `GetApplicationLogsForTx.java`
            System.out.println("####################");
        }
    }

    public static void writeDebugFile(Path outDir, String contractName) throws IOException {
        contractName = "io.neow3j.examples.contractdevelopment.contracts." + contractName;

        File tmpFile = File.createTempFile("contract", ".debug.json");
        tmpFile.deleteOnExit();
        DebugInfo debugInfo = new DebugInfo();
        try (FileOutputStream s = new FileOutputStream(tmpFile)) {
            ObjectMapperFactory.getObjectMapper().writeValue(s, debugInfo);
        }
        // Then put it into a ZIP archive.
        String zipName = contractName + ".nefdbgnfo";
        String zipEntryName = contractName + ".debug.json";
        File zipOutputFile = Paths.get(outDir.toString(), zipName).toFile();
        try (ZipOutputStream s = new ZipOutputStream(Files.newOutputStream(zipOutputFile.toPath()))) {
            s.putNextEntry(new ZipEntry(zipEntryName));
            byte[] bytes = ObjectMapperFactory.getObjectMapper().writeValueAsBytes(debugInfo);
            s.write(bytes);
            s.closeEntry();
        }
    }

}
