package io.neow3j.examples.android;

import android.os.AsyncTask;
import android.widget.TextView;
import io.neow3j.model.types.NEOAsset;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.exceptions.ErrorResponseException;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.wallet.Account;
import io.neow3j.wallet.AssetTransfer;

import java.io.IOException;

public class Transfer extends AsyncTask<TextView, Void, String> {

    protected String doInBackground(TextView... textView) {
        try {
            Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:30333"));
            Account fromAccount = Account.fromWIF("KxDgvEKzgSBPPfuVfw67oPQBSjidEiqTHURKSDL1R7yGaGYAeYnr").build();
            updateAndPrintBalance(neow3j, fromAccount);

            AssetTransfer transfer = new AssetTransfer.Builder(neow3j).account(fromAccount)
                    .output(NEOAsset.HASH_ID, 10, "AJQ6FoaSXDFzA6wLnyZ1nFN7SGSN2oNTc3")
                    .networkFee(0.001)
                    .build()
                    .sign()
                    .send();

            String txId = transfer.getTransaction().getTxId();
            System.out.println("Transaction Id: " + txId);

            Monitor.txId = txId;

        } catch (Exception e) {
            System.out.println(e);
        }

        return "a";
    }

    private void updateAndPrintBalance(Neow3j neow3j, Account fromAccount) {
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