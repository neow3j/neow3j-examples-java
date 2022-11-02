package io.neow3j.examples.jsonrpc;

import java.io.IOException;
import java.math.BigInteger;

import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.response.NeoBlock;

import static io.neow3j.examples.Constants.NEOW3J_PRIVATENET;

public class GetBlock {

    // The neow3j instance used in this example.
    static final Neow3j neow3j = NEOW3J_PRIVATENET;

    public static void main(String[] args) throws IOException {

        NeoBlock block = neow3j.getBlock(new BigInteger("2"), true)
                .send()
                .getBlock();

        System.out.println("\n####################");
        System.out.println("block: " + block);
        System.out.println("####################");
    }

}
