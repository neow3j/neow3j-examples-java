package io.neow3j.examples.jsonrpc;

import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.methods.response.NeoBlock;
import io.neow3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;

public class GetBlock {

    public static void main(String[] args) throws IOException {
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:40332"));

        NeoBlock block = neow3j.getBlock(new BigInteger("2"), true)
                .send()
                .getBlock();

        System.out.println("\n####################");
        System.out.println("block: " + block);
        System.out.println("####################");
    }
}
