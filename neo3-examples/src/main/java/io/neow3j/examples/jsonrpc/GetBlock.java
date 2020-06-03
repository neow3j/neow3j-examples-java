package io.neow3j.examples.jsonrpc;

import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.methods.response.NeoBlock;
import io.neow3j.protocol.http.HttpService;

import java.io.IOException;

public class GetBlock {

    public static void main(String[] args) throws IOException {
        Neow3j neow3j = Neow3j.build(new HttpService("http:localhost:40332"));

        NeoBlock block = neow3j.getBlock("0x51480d0fb7af31f14c96ff017ed3465a191ba9f28f188518e6f9df545b12d905", true)
                .send()
                .getBlock();

        System.out.println("\n####################");
        System.out.println("block: " + block);
        System.out.println("####################");
    }
}
