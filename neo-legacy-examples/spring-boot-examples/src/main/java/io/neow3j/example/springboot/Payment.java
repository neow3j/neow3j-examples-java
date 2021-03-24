package io.neow3j.example.springboot;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import io.neow3j.protocol.exceptions.ErrorResponseException;
import io.neow3j.wallet.AssetTransfer;
import io.reactivex.disposables.Disposable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.neow3j.model.types.NEOAsset;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.BlockParameterName;
import io.neow3j.protocol.core.methods.response.Transaction;
import io.neow3j.protocol.core.methods.response.TransactionOutput;
import io.neow3j.wallet.Account;
import io.neow3j.wallet.Wallet;

/**
 * Payment
 */
@RestController
public class Payment {

    private final Neow3j neow3j;
    private final Wallet wallet;

    @Autowired
    public Payment(Neow3j neow3j, Wallet wallet) {
        this.neow3j = neow3j;
        this.wallet = wallet;
    }

    @GetMapping("/balance")
    public ResponseEntity<String> balance(@RequestParam(value = "address", required = false) String address) throws IOException, ErrorResponseException {
        if (address == null) {
            address = "AK2nJJpJr6o664CWJKi1QRXjqeic2zRp8y";
        }
        Account account = Account.fromAddress(address).build();
        account.updateAssetBalances(neow3j);

        String NeoBalance = account.getBalances().getAssetBalance(NEOAsset.HASH_ID).getAmount().toString();

        System.out.println("Neo balance of address " + address + ": " + NeoBalance);

        return ResponseEntity.ok().body("Neo: " + NeoBalance);
    }

    @PostMapping("/pay")
    public ResponseEntity<String> pay(@RequestParam(value = "amount", required = false) BigDecimal amount,
                                      @RequestParam(value = "address", required = false) String address) throws IOException, ErrorResponseException {
        if (amount.signum() == -1) {
            System.out.println("Negative amounts cannot be transferred.");
            return ResponseEntity.badRequest().body("Only non-negative amounts are accepted.");
        }
        if (address == null) {
            Account recipient = generateNewAccount();
            address = recipient.getAddress();
        }

        Account fromAccount = Account.fromWIF("KxDgvEKzgSBPPfuVfw67oPQBSjidEiqTHURKSDL1R7yGaGYAeYnr").build();
        fromAccount.updateAssetBalances(this.neow3j);

        System.out.println("Payment: " + amount + "Neo to address: " + address);

        AssetTransfer transfer = new AssetTransfer.Builder(neow3j)
                .account(fromAccount)
                .output(NEOAsset.HASH_ID, String.valueOf(amount), address)
                .networkFee(0.001)
                .build()
                .sign()
                .send();

        wallet.addAccount(fromAccount);
        monitorPayment(address);
        return ResponseEntity.ok().body(address + " " + amount);
    }

    public Account generateNewAccount() {
        return Account.createAccount();
    }

    public void monitorPayment(String addressToMonitor) {
        new Thread(() -> {
            Disposable subscribe = neow3j.catchUpToLatestAndSubscribeToNewBlocksObservable(BlockParameterName.LATEST, true)
                    .doOnComplete(() -> System.out.println("Complete!"))
                    .doOnDispose(() -> System.out.println("Stopped monitoring payment to address=" + addressToMonitor))
                    .doOnSubscribe((var) -> System.out.println("Subscribed."))
                    .doOnTerminate(() -> System.out.println("Catch up is terminated."))
                    .subscribe((block) -> {
                        List<Transaction> transactions = block.getBlock().getTransactions();
                        transactions.forEach((tx) -> {
                            List<TransactionOutput> outputs = tx.getOutputs();
                            outputs.forEach((output) -> {
                                if (output.getAddress().equals(addressToMonitor)
                                        && output.getAssetId().contains(NEOAsset.HASH_ID)) {
                                    System.out.println(String.format("NEO payment detected: amount=%s to=%s",
                                            output.getValue(), output.getAddress()));
                                }
                            });
                        });
                    });
            try {
                Thread.sleep(60000 * 5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            subscribe.dispose();
        }).start();
    }
}
