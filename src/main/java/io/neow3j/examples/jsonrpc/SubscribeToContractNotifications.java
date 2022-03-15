package io.neow3j.examples.jsonrpc;

import io.neow3j.contract.GasToken;
import io.neow3j.protocol.core.response.NeoApplicationLog;
import io.neow3j.protocol.core.response.NeoBlock;
import io.neow3j.protocol.core.response.Transaction;
import io.neow3j.types.Hash160;
import io.neow3j.types.Hash256;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.neow3j.examples.Constants.NEOW3J;

public class SubscribeToContractNotifications {

    public static void main(String[] args) throws IOException {

        // The contract hash to track.
        Hash160 contractHash = GasToken.SCRIPT_HASH;

        NEOW3J.subscribeToNewBlocksObservable(true)
                .subscribe((blockReqResult) -> {
                    NeoBlock block = blockReqResult.getBlock();
                    System.out.println("#######################################");
                    System.out.println("Block Index: " + block.getIndex());

                    List<Hash256> transactionsWithNotification = new ArrayList<>();
                    for (Transaction t : block.getTransactions()) {
                        List<NeoApplicationLog.Execution> execs =
                                NEOW3J.getApplicationLog(t.getHash()).send().getApplicationLog().getExecutions();
                        List<NeoApplicationLog.Execution.Notification> contractNotifications = new ArrayList<>();
                        for (NeoApplicationLog.Execution e : execs) {
                            contractNotifications = e.getNotifications().stream()
                                    .filter(n -> n.getContract().equals(contractHash))
                                    .collect(Collectors.toList());
                        }
                        if (!contractNotifications.isEmpty()) {
                            transactionsWithNotification.add(t.getHash());
                        }
                    }
                    if (transactionsWithNotification.isEmpty()) {
                        System.out.println("No contract notification found.");
                    } else {
                        System.out.println(
                                "Contract notification found in transactions: " + transactionsWithNotification);
                    }
                });
    }
}
