package io.neow3j.examples.contractinvoke;

import static java.util.Collections.singletonList;

import io.neow3j.contract.Hash160;
import io.neow3j.contract.Hash256;
import io.neow3j.contract.NeoToken;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.methods.response.NeoSendRawTransaction;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.utils.Await;
import io.neow3j.wallet.Account;
import io.neow3j.wallet.Wallet;

import java.math.BigDecimal;

public class TransferNeo {

    public static void main(String[] args) throws Throwable {
        // Set up the connection to the neo-node
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:40332"));

        // Setup an account and wallet that are used as the sender and for signing the transaction
        // Make sure that the account has a sufficient GAS and NEO balance for payment and fees.
        Account a = Account.fromWIF("L24Qst64zASL2aLEKdJtRLnbnTbqpcRNWkWJ3yhDh2CLUtLdwYK2");
        Account multiSigAccount = Account.createMultiSigAccount(
                singletonList(a.getECKeyPair().getPublicKey()), 1);
        Wallet w = Wallet.withAccounts(multiSigAccount, a);

        // Receiver
        Hash160 receiver = Hash160.fromAddress("NZNos2WqTbu5oCgyfss9kUJgBXJqhuYAaj");

        // Setup the NeoToken class with a node connection for further calls to the contract.
        NeoToken neo = new NeoToken(neow3j);

        // The transfer method will add the wallets default account as the signer and use that
        // accounts tokens to cover the transfer amount.
        NeoSendRawTransaction response = neo.transfer(w, receiver, BigDecimal.ONE)
                .sign() // Signs the transaction with the account that was configured as the signer.
                .send(); // Sends the transaction to the neo-node.

        System.out.println("####################\n");
        if (response.hasError()) {
            System.out.printf("The neo-node responded with the error message '%s'.%n",
                    response.getError());
        } else {
            Hash256 txHash = response.getSendRawTransaction().getHash();
            System.out.printf("Successfully transmitted the transaction with hash '%s'.%n", txHash);
            Await.waitUntilTransactionIsExecuted(txHash, neow3j);
            System.out.println("Tx: " + neow3j.getTransaction(txHash).send().getTransaction().toString());
            System.out.println("\n####################");
        }
    }
}
