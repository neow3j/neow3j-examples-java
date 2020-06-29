package io.neow3j.examples.jsonrpc;

import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.methods.response.NeoBlockCount;
import io.neow3j.protocol.http.HttpService;

import java.io.IOException;

public class GetLatestBlockIndex {

    public static void main(String[] args) throws IOException {
        Neow3j neow3j = Neow3j.build(new HttpService("http:localhost:40332"));

        NeoBlockCount blockCountReq = neow3j.getBlockCount().send();

        System.out.println("\n####################");
        System.out.println("Latest block index: " + blockCountReq.getBlockIndex());
        System.out.println("####################");
    }
}
