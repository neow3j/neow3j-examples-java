package io.neow3j.examples.jsonrpc;

import java.io.IOException;
import java.math.BigInteger;

import static io.neow3j.examples.Constants.NEOW3J;

public class GetLatestBlockIndex {

    public static void main(String[] args) throws IOException {

        BigInteger blockCount = NEOW3J.getBlockCount()
                .send()
                .getBlockIndex();

        System.out.println("\n####################");
        System.out.println("Latest block index: " + blockCount);
        System.out.println("####################");
    }
}
