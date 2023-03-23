package io.neow3j.examples.jsonrpc;

import io.neow3j.contract.GasToken;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.response.NeoApplicationLog;
import io.neow3j.protocol.core.response.NeoBlock;
import io.neow3j.protocol.core.response.Transaction;
import io.neow3j.types.Hash160;
import io.neow3j.types.Hash256;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static io.neow3j.examples.Constants.NEOW3J_PRIVATENET;

public class SubscribeToContractNotifications {

    // The neow3j instance used in this example.
    static final Neow3j neow3j = NEOW3J_PRIVATENET;

    public static void main(String[] args) throws IOException {

        // The contract hash to track.
        Hash160 contractHash = GasToken.SCRIPT_HASH;

        neow3j.subscribeToNewBlocksObservable(true).subscribe((blockReqResult) -> {
            NeoBlock block = blockReqResult.getBlock();
            System.out.println("#######################################");
            System.out.println("Block Index: " + block.getIndex());

            List<Hash256> transactionsWithNotification = new ArrayList<>();
            for (Transaction t : block.getTransactions()) {
                List<NeoApplicationLog.Execution> execs =
                        neow3j.getApplicationLog(t.getHash()).send().getApplicationLog().getExecutions();

                boolean notificationFound = execs.stream().anyMatch(
                        e -> e.getNotifications().stream().anyMatch(n -> n.getContract().equals(contractHash)));
                if (notificationFound) {
                    transactionsWithNotification.add(t.getHash());
                }
            }
            if (transactionsWithNotification.isEmpty()) {
                System.out.println("No contract notification found.");
            } else {
                System.out.println("Contract notification found in transactions: " + transactionsWithNotification);
            }
        });
    }

}
