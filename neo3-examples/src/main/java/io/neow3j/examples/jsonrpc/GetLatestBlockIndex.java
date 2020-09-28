package io.neow3j.examples.jsonrpc;

import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;

public class GetLatestBlockIndex {

    public static void main(String[] args) throws IOException {
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:40332"));

        BigInteger blockCount = neow3j.getBlockCount()
                .send()
                .getBlockIndex();

        System.out.println("\n####################");
        System.out.println("Latest block index: " + blockCount);
        System.out.println("####################");
    }
}
