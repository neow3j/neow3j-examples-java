package io.neow3j.examples.contract_invocation;

import io.neow3j.contract.NeoToken;
import io.neow3j.contract.ScriptHash;
import io.neow3j.model.NeoConfig;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.methods.response.NeoSendRawTransaction;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.wallet.Account;
import io.neow3j.wallet.Wallet;
import java.math.BigDecimal;

public class TransferNeo {

    public static void main(String[] args) throws Throwable {
        NeoConfig.setAddressVersion((byte) 0x35);
        // Set the magic number according to the Neo network's configuration. It is used when
        // signing transactions.
        NeoConfig.setMagicNumber(new byte[]{0x01, 0x03, 0x00, 0x0}); // Magic number 769

        // Set up the connection to the neo-node
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:40332"));
        // Setup an account and wallet that are used as the sender and for signing the transaction
        // Make sure that the account has a sufficient GAS and NEO balance for payment and fees.
        Account a = Account.fromWIF("L3kCZj6QbFPwbsVhxnB8nUERDy4mhCSrWJew4u5Qh5QmGMfnCTda");
        Wallet w = Wallet.withAccounts(a);
        ScriptHash receiver = ScriptHash.fromAddress("NLnyLtep7jwyq1qhNPkwXbJpurC4jUT8ke");

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
        }
        System.out.printf("Successfully transmitted the transaction with hash '%s'.%n",
                response.getSendRawTransaction().getHash());
        System.out.println("\n####################");
    }
}
