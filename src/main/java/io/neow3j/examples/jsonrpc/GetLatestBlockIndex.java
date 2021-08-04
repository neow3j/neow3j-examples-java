package io.neow3j.examples.jsonrpc;

import java.io.IOException;
import java.math.BigInteger;

import static io.neow3j.examples.Constants.NEOW3J;

public class GetLatestBlockIndex {

    public static void main(String[] args) throws IOException {

        BigInteger blockCount = NEOW3J.getBlockCount()
                .send()
                .getBlockCount();

        // Note: The RPC getblockcount includes the genesis block with index 0.
        BigInteger latestBlockIndex = blockCount.subtract(BigInteger.ONE);

        System.out.println("\n####################");
        System.out.println("Latest block index: " + latestBlockIndex);
        System.out.println("####################");
    }
}
