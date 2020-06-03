package io.neow3j.examples.jsonrpc;

import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.BlockParameterName;
import io.neow3j.protocol.http.HttpService;

public class JustSubscribeToNewBlocks {

    public static void main(String[] args) {
        Neow3j neow3j = Neow3j.build(new HttpService("http://seed1.ngd.network:10332"));

        neow3j.catchUpToLatestAndSubscribeToNewBlocksObservable(BlockParameterName.LATEST, true)
                .subscribe((blockReqResult) -> {
                    System.out.println("#######################################");
                    System.out.println("blockIndex: " + blockReqResult.getBlock().getIndex());
                    System.out.println("hashId: " + blockReqResult.getBlock().getHash());
                    System.out.println("confirmations: " + blockReqResult.getBlock().getConfirmations());
                    System.out.println("transactions: " + blockReqResult.getBlock().getTransactions());
                });
    }

}
