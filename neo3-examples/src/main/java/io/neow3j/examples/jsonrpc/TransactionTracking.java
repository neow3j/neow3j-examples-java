package io.neow3j.examples.jsonrpc;

import io.neow3j.contract.NeoToken;
import io.neow3j.contract.ScriptHash;
import io.neow3j.contract.TransactionBuilder;
import io.neow3j.model.NeoConfig;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.methods.response.NeoApplicationLog;
import io.neow3j.protocol.core.methods.response.NeoSendRawTransaction;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.transaction.Transaction;
import io.neow3j.wallet.Account;
import io.neow3j.wallet.Wallet;

import java.math.BigDecimal;
import java.util.Arrays;

public class TransactionTracking {

    public static void main(String[] args) throws Throwable {
        NeoConfig.setMagicNumber(new byte[]{0x01, 0x03, 0x00, 0x0}); // Magic number 769
        Neow3j neow = Neow3j.build(new HttpService("http://localhost:40332"));

        Account account = Account.fromWIF("L3kCZj6QbFPwbsVhxnB8nUERDy4mhCSrWJew4u5Qh5QmGMfnCTda");
        Account multiSigAccount = Account.createMultiSigAccount(
                Arrays.asList(account.getECKeyPair().getPublicKey()), 1);

        Wallet wallet = Wallet.withAccounts(multiSigAccount, account);

        ScriptHash to = new ScriptHash("f7014e6d52fe8f94f7c57acd8cfb875b4ac2a1c6");
        BigDecimal amount = new BigDecimal("1");

        TransactionBuilder b = new NeoToken(neow).transferFromDefaultAccount(wallet, to, amount);
        Transaction tx = b.sign();
        NeoSendRawTransaction send = tx.send();
        send.getSendRawTransaction();

        if (send.getError() == null) {
            System.out.println("Transaction sent successfully - starting tracking.");
            // track the transaction and print the application log.
            tx.track().subscribe(blockIndex -> {
                System.out.println("\n####################");
                NeoApplicationLog log = tx.getApplicationLog();
                System.out.printf("Found tx on block %s." +
                        " Tx exited with state %s.", blockIndex, log.getState());
                System.out.println(log.toString());
                System.out.println("####################");
                neow.shutdown();
            });
        }
    }
}
