package io.neow3j.examples.android;

import android.os.AsyncTask;
import android.widget.TextView;
import io.neow3j.model.types.NEOAsset;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.BlockParameterIndex;
import io.neow3j.protocol.core.BlockParameterName;
import io.neow3j.protocol.exceptions.ErrorResponseException;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.utils.Numeric;
import io.neow3j.wallet.Account;
import io.neow3j.wallet.AssetTransfer;

import java.io.IOException;
import java.math.BigInteger;

public class Monitor extends AsyncTask<TextView, Void, String> {

    public static String txId;

    protected String doInBackground(TextView... textView) {
        try {
            Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:30333"));

            BigInteger latestBlockIndex;

            System.out.println(neow3j.getVersion().send().getVersion().getUserAgent());

            latestBlockIndex = neow3j.getBlockCount().send().getBlockIndex();
            System.out.println("Latest block: " + latestBlockIndex);

            Account fromAccount = Account.fromWIF("KxDgvEKzgSBPPfuVfw67oPQBSjidEiqTHURKSDL1R7yGaGYAeYnr").build();
            printBalance(neow3j, fromAccount);

            neow3j.catchUpToLatestBlockObservable(new BlockParameterIndex(latestBlockIndex.subtract(BigInteger.valueOf(10L))), true)
                    .doOnCompleted(() -> {
                        System.out.println("PAST BLOCKS - Completed.");
                    })
                    .subscribe((block) -> {
                        System.out.println("PAST BLOCKS - block received: " + block.getBlock().getIndex());
                    });

            neow3j.catchUpToLatestAndSubscribeToNewBlocksObservable(BlockParameterName.LATEST, true)
                    .subscribe((block) -> {
                        System.out.println("LATEST BLOCKS - block received: " + block.getBlock().getIndex());
                        block.getResult().getTransactions().stream().forEach(t -> {
                            String txIdWithoutPrefix = Numeric.cleanHexPrefix(t.getTransactionId());

                            t.getOutputs().stream().forEach(o -> {
                                if (o.getAddress() != null
                                        && o.getAddress().equals(fromAccount.getAddress())
                                        && o.getAssetId().contains(NEOAsset.HASH_ID)) {
                                    System.out.println("Detected: Tx (" + txIdWithoutPrefix + ") with output to (" + fromAccount.getAddress() + ")");
                                }
                            });

                            printBalance(neow3j, fromAccount);
                        });
                    });
        } catch (Exception e) {
            System.out.println(e);
        }

        return "a";
    }

    private void printBalance(Neow3j neow3j, Account fromAccount) {
        try {
            fromAccount.updateAssetBalances(neow3j);
            System.out.println("Balance: " + fromAccount.getBalances().getAssetBalance(NEOAsset.HASH_ID).getAmount());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ErrorResponseException e) {
            e.printStackTrace();
        }
    }
}