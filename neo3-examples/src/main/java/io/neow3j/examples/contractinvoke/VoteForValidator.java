package io.neow3j.examples.contractinvoke;

import static io.neow3j.transaction.Signer.calledByEntry;

import java.math.BigDecimal;
import java.util.Arrays;

import io.neow3j.contract.GasToken;
import io.neow3j.contract.NeoToken;
import io.neow3j.crypto.ECKeyPair.ECPublicKey;
import io.neow3j.model.NeoConfig;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.utils.Await;
import io.neow3j.wallet.Account;
import io.neow3j.wallet.Wallet;

public class VoteForValidator {

    public static void main(String[] args) throws Throwable {
        
        NeoConfig.setMagicNumber(new byte[]{0x01, 0x03, 0x00, 0x0}); // Magic number 769
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:40332"));

        // The account client1 is used as the voter.
        Account client1 = Account.fromWIF("KwjpUzqHThukHZqw5zu4QLGJXessUxwcG3GinhJeBmqj4uKM4K5z");
        Wallet voteWallet = Wallet.withAccounts(client1);

        // The account defaultAcc is registered as candidate and then the client1 votes for it to become a validator.
        Account defaultAcc = Account.fromWIF("L3kCZj6QbFPwbsVhxnB8nUERDy4mhCSrWJew4u5Qh5QmGMfnCTda");
        // The account committeeAcc is the multi-sig account derived from the defaultAcc account.
        // It holds the initial funds of Neo and Gas and is needed to fund the client1 account.
        Account committeeAcc = Account.createMultiSigAccount(Arrays.asList(defaultAcc.getECKeyPair().getPublicKey()), 1);
        Wallet committeeWallet = Wallet.withAccounts(committeeAcc, defaultAcc);

        NeoToken neoToken = new NeoToken(neow3j);
        GasToken gasToken = new GasToken(neow3j);

        // The voting account needs NEO because only NEO holders can participate in governance.
        String txHash = neoToken.transfer(committeeWallet, client1.getAddress(), new BigDecimal("100000"))
            .sign()
            .send()
            .getSendRawTransaction().getHash();
        Await.waitUntilTransactionIsExecuted(txHash, neow3j);

        // The voting account needs GAS to pay for transactions.
        txHash = gasToken.transfer(committeeWallet, client1.getAddress(), new BigDecimal("100000"))
            .sign()
            .send()
            .getSendRawTransaction().getHash();
        Await.waitUntilTransactionIsExecuted(txHash, neow3j);

        // The entity for which we will vote needs to be registered as a candidate to receive votes.
        ECPublicKey candidateKey = defaultAcc.getECKeyPair().getPublicKey();
        txHash = neoToken.registerCandidate(candidateKey)
            .signers(calledByEntry(defaultAcc.getScriptHash()))
            .wallet(committeeWallet)
            .sign()
            .send()
            .getSendRawTransaction().getHash();
        Await.waitUntilTransactionIsExecuted(txHash, neow3j);

        System.out.println("Candidate registered!");

        txHash = neoToken.vote(client1, candidateKey)
            .signers(calledByEntry(client1.getScriptHash()))
            .wallet(voteWallet)
            .sign()
            .send()
            .getSendRawTransaction().getHash();
        Await.waitUntilTransactionIsExecuted(txHash, neow3j);

        System.out.println("Voted!");
        System.out.println("Voted validators are:");

        neow3j.getNextBlockValidators().send().getResult()
                .forEach(v -> System.out.println(
                    "Pubkey: " + v.getPublicKey() + ", votes: " + v.getVotes()));

    }
}
