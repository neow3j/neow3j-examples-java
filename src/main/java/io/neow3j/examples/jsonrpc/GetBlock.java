package io.neow3j.examples.jsonrpc;

import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.BlockParameterIndex;
import io.neow3j.protocol.core.methods.response.NeoGetBlock;
import io.neow3j.protocol.http.HttpService;

import java.io.IOException;

public class GetBlock {

    public static void main(String[] args) throws IOException {

        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:30333"));

        NeoGetBlock neoGetBlock = neow3j
                .getBlock(
                        new BlockParameterIndex(2007L),
                        true)
                .send();

        System.out.println("Block: " + neoGetBlock.getBlock());
    }

}


