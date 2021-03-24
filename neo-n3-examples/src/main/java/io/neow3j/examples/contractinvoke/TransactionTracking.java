package io.neow3j.examples.contractinvoke;

import static io.neow3j.wallet.Account.createMultiSigAccount;
import static java.util.Collections.singletonList;

import io.neow3j.contract.NeoToken;
import io.neow3j.contract.Hash160;
import io.neow3j.contract.TransactionBuilder;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.methods.response.NeoApplicationLog;
import io.neow3j.protocol.core.methods.response.NeoSendRawTransaction;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.transaction.Transaction;
import io.neow3j.wallet.Account;
import io.neow3j.wallet.Wallet;

import java.math.BigDecimal;

public class TransactionTracking {

    public static void main(String[] args) throws Throwable {
        Neow3j neow = Neow3j.build(new HttpService("http://localhost:40332"));

        Account account = Account.fromWIF("L24Qst64zASL2aLEKdJtRLnbnTbqpcRNWkWJ3yhDh2CLUtLdwYK2");
        Account multiSigAccount = createMultiSigAccount(singletonList(account.getECKeyPair().getPublicKey()), 1);

        Wallet wallet = Wallet.withAccounts(multiSigAccount, account);

        // ScriptHash: fb2b60c9ea35be51abf741981e7c4954eedf50c3
        // Address: NdihqSLYTf1B1WYuzhM52MNqvCNPJKLZaz
        Hash160 to = new Hash160("fb2b60c9ea35be51abf741981e7c4954eedf50c3");
        BigDecimal amount = new BigDecimal("1");

        TransactionBuilder b = new NeoToken(neow).transferFromDefaultAccount(wallet, to, amount);
        Transaction tx = b.sign();
        NeoSendRawTransaction send = tx.send();

        if (send.getError() == null) {
            System.out.println("Transaction sent successfully - starting tracking.");
            // track the transaction and print the application log.
            tx.track().subscribe(blockIndex -> {
                System.out.println("\n####################");
                NeoApplicationLog log = tx.getApplicationLog();
                String state = log.getExecutions().get(0).getState();
                System.out.printf("Found the transaction on block %s. It exited with state %s.\n", blockIndex, state);
                System.out.println(log.toString());
                System.out.println("####################");
                neow.shutdown();
            });
        }
    }
}
