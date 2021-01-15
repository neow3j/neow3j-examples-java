package io.neow3j.examples.jsonrpc;

import static io.neow3j.transaction.Signer.calledByEntry;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import io.neow3j.contract.GasToken;
import io.neow3j.contract.NeoToken;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.methods.response.NeoGetNextBlockValidators.Validator;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.utils.Await;
import io.neow3j.wallet.Account;
import io.neow3j.wallet.Wallet;

public class VoteForValidator {

    public static void main(String[] args) throws Throwable {
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:40332"));
        Account client1 = Account.fromWIF("KwjpUzqHThukHZqw5zu4QLGJXessUxwcG3GinhJeBmqj4uKM4K5z");
        Wallet voteWallet = Wallet.withAccounts(client1);

        NeoToken neoToken = new NeoToken(neow3j);
        GasToken gasToken = new GasToken(neow3j);
        Account committeeAcc = Account.fromWIF("L3kCZj6QbFPwbsVhxnB8nUERDy4mhCSrWJew4u5Qh5QmGMfnCTda");
        Wallet committeeWallet = Wallet.withAccounts(committeeAcc);

        String txHash = neoToken.transfer(committeeWallet, client1.getAddress(), new BigDecimal("100000"))
            .sign()
            .send()
            .getSendRawTransaction().getHash();
        Await.waitUntilTransactionIsExecuted(txHash, neow3j);
        txHash = gasToken.transfer(committeeWallet, client1.getAddress(), new BigDecimal("100000"))
            .sign()
            .send()
            .getSendRawTransaction().getHash();
        Await.waitUntilTransactionIsExecuted(txHash, neow3j);

        txHash = neoToken.vote(client1, committeeAcc.getECKeyPair().getPublicKey())
            .signers(calledByEntry(client1.getScriptHash()))
            .wallet(voteWallet)
            .sign()
            .send()
            .getSendRawTransaction().getHash();
        Await.waitUntilTransactionIsExecuted(txHash, neow3j);

        List<Validator> validators = neow3j.getNextBlockValidators()
                .send()
                .getResult();
    }
}
