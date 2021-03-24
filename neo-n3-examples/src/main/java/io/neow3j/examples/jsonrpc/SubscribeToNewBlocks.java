package io.neow3j.examples.jsonrpc;

import java.io.IOException;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.http.HttpService;

public class SubscribeToNewBlocks {

    public static void main(String[] args) throws IOException {
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:40332"));

        neow3j.subscribeToNewBlocksObservable(true)
                .subscribe((blockReqResult) -> {
                    System.out.println("#######################################");
                    System.out.println("Block Index:     " + blockReqResult.getBlock().getIndex());
                    System.out.println("Block Hash:      " + blockReqResult.getBlock().getHash());
                    System.out.println("Prev Block Hash: " + blockReqResult.getBlock().getPrevBlockHash());
                    System.out.println("Next Consensus:  " + blockReqResult.getBlock().getNextConsensus());
                    System.out.println("Transactions:    " + blockReqResult.getBlock().getTransactions());
                });
    }
}
