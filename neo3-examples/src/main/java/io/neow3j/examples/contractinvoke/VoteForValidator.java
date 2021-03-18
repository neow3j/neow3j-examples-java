package io.neow3j.examples.contractinvoke;

import static io.neow3j.transaction.Signer.calledByEntry;
import static java.util.Collections.singletonList;

import io.neow3j.contract.GasToken;
import io.neow3j.contract.Hash256;
import io.neow3j.contract.NeoToken;
import io.neow3j.crypto.ECKeyPair.ECPublicKey;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.utils.Await;
import io.neow3j.wallet.Account;
import io.neow3j.wallet.Wallet;

import java.math.BigDecimal;

public class VoteForValidator {

    public static void main(String[] args) throws Throwable {
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:40332"));

        // The account client1 is used as the voter.
        Account client1 = Account.fromWIF("L3gSLs2CSRYss1zoTmSB9hYAxqimn7Br5yDomH8FDb6NDsupeRVK");
        Wallet voteWallet = Wallet.withAccounts(client1);

        // Alice account is registered as candidate and then the client1 votes for it to become a validator.
        Account defaultAccount = Account.fromWIF("L24Qst64zASL2aLEKdJtRLnbnTbqpcRNWkWJ3yhDh2CLUtLdwYK2");

        // The account committee is the multi-sig account derived from the defaultAccount account.
        // It holds the initial funds of Neo and Gas and is needed to fund the client1 account.
        Account committee = Account.createMultiSigAccount(singletonList(defaultAccount.getECKeyPair().getPublicKey()), 1);
        Wallet wallet = Wallet.withAccounts(committee, defaultAccount);

        NeoToken neoToken = new NeoToken(neow3j);
        GasToken gasToken = new GasToken(neow3j);

        // The voting account needs NEO because only NEO holders can participate in governance.
        Hash256 txHash = neoToken.transfer(wallet, client1.getAddress(), new BigDecimal("100000"))
            .sign()
            .send()
            .getSendRawTransaction().getHash();
        Await.waitUntilTransactionIsExecuted(txHash, neow3j);

        System.out.println("Voting account funded with Neo.");

        // The voting account needs GAS to pay for transactions.
        txHash = gasToken.transfer(wallet, client1.getAddress(), new BigDecimal("100000"))
            .sign()
            .send()
            .getSendRawTransaction().getHash();
        Await.waitUntilTransactionIsExecuted(txHash, neow3j);

        System.out.println("Voting account funded with Gas.");

        // The entity for which we will vote needs to be registered as a candidate to receive votes.
        ECPublicKey candidateKey = defaultAccount.getECKeyPair().getPublicKey();
        txHash = neoToken.registerCandidate(candidateKey)
            .signers(calledByEntry(defaultAccount.getScriptHash()))
            .wallet(wallet)
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
