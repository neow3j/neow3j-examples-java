package io.neow3j.examples.jsonrpc;

import static io.neow3j.examples.Constants.NEOW3J;
import java.io.IOException;
import java.math.BigInteger;
import io.neow3j.protocol.core.response.NeoBlock;

public class GetBlock {

    public static void main(String[] args) throws IOException {

        NeoBlock block = NEOW3J.getBlock(new BigInteger("2"), true)
                .send()
                .getBlock();

        System.out.println("\n####################");
        System.out.println("block: " + block);
        System.out.println("####################");
    }
}
