package io.neow3j.examples.jsonrpc;

import io.neow3j.protocol.Neow3j;

import java.io.IOException;
import java.math.BigInteger;

import static io.neow3j.examples.Constants.neow3jPrivatenet;

public class GetLatestBlockIndex {

    // The neow3j instance used in this example.
    static final Neow3j neow3j = neow3jPrivatenet();

    public static void main(String[] args) throws IOException {

        BigInteger blockCount = neow3j.getBlockCount()
                .send()
                .getBlockCount();

        // Note: The RPC getblockcount includes the genesis block with index 0.
        BigInteger latestBlockIndex = blockCount.subtract(BigInteger.ONE);

        System.out.println("\n####################");
        System.out.println("Latest block index: " + latestBlockIndex);
        System.out.println("####################");
    }

}
