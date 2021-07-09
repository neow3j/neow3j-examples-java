package io.neow3j.examples.contractinvoke;

import static io.neow3j.examples.Constants.ALICE;
import static io.neow3j.examples.Constants.NEOW3J;
import static io.neow3j.examples.Constants.WALLET;

import io.neow3j.contract.GasToken;
import static io.neow3j.transaction.AccountSigner.calledByEntry;
import io.neow3j.types.Hash256;
import io.neow3j.contract.NeoToken;
import io.neow3j.crypto.ECKeyPair.ECPublicKey;
import io.neow3j.utils.Await;

import java.math.BigInteger;

public class VoteForValidator {

    public static void main(String[] args) throws Throwable {

        NeoToken neoToken = new NeoToken(NEOW3J);
        GasToken gasToken = new GasToken(NEOW3J);

        // The voting account needs NEO because only NEO holders can participate in governance.
        Hash256 txHash = neoToken
                .transfer(
                        WALLET,
                        ALICE.getScriptHash(),
                        new BigInteger("100000")
                )
                .sign()
                .send()
                .getSendRawTransaction()
                .getHash();
        Await.waitUntilTransactionIsExecuted(txHash, NEOW3J);

        System.out.println("Voting account funded with Neo.");

        // The voting account needs GAS to pay for transactions.
        txHash = gasToken.transfer(WALLET, ALICE.getScriptHash(), new BigInteger("100000"))
                .sign()
                .send()
                .getSendRawTransaction().getHash();
        Await.waitUntilTransactionIsExecuted(txHash, NEOW3J);

        System.out.println("Voting account funded with Gas.");

        // The entity for which we will vote needs to be registered as a candidate to receive votes.
        ECPublicKey candidateKey = ALICE.getECKeyPair().getPublicKey();
        txHash = neoToken.registerCandidate(candidateKey)
                .signers(calledByEntry(ALICE.getScriptHash()))
                .wallet(WALLET)
                .sign()
                .send()
                .getSendRawTransaction().getHash();
        Await.waitUntilTransactionIsExecuted(txHash, NEOW3J);

        System.out.println("Candidate registered!");

        txHash = neoToken.vote(ALICE, candidateKey)
                .signers(calledByEntry(ALICE.getScriptHash()))
                .wallet(WALLET)
                .sign()
                .send()
                .getSendRawTransaction().getHash();
        Await.waitUntilTransactionIsExecuted(txHash, NEOW3J);

        System.out.println("Voted!");
        System.out.println("Voted validators are:");

        NEOW3J.getNextBlockValidators().send().getResult().forEach(v -> System.out.println(
                "Pubkey: " + v.getPublicKey() + ", votes: " + v.getVotes()));

    }
}
