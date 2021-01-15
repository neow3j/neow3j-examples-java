package io.neow3j.examples.contract_invocation;

import io.neow3j.contract.NeoToken;
import io.neow3j.contract.NeoURI;
import io.neow3j.contract.ScriptHash;
import io.neow3j.model.NeoConfig;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.methods.response.NeoSendRawTransaction;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.utils.Await;
import io.neow3j.wallet.Account;
import io.neow3j.wallet.Wallet;

import java.util.Arrays;

public class BuildNeoURIAndTransfer {

    public static void main(String[] args) throws Throwable {
        // Set the magic number according to the Neo network's configuration. It is used when
        // signing transactions.
        NeoConfig.setMagicNumber(new byte[]{0x01, 0x03, 0x00, 0x0}); // Magic number 769

        // Set up the connection to the neo-node
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:40332"));
        Account a = Account.fromWIF("L3kCZj6QbFPwbsVhxnB8nUERDy4mhCSrWJew4u5Qh5QmGMfnCTda");
        Account multiSigAccount = Account.createMultiSigAccount(
                Arrays.asList(a.getECKeyPair().getPublicKey()), 1);
        Wallet w = Wallet.withAccounts(multiSigAccount, a);
        ScriptHash receiver = ScriptHash.fromAddress("NLnyLtep7jwyq1qhNPkwXbJpurC4jUT8ke");

        NeoURI uri = new NeoURI(neow3j)
                .wallet(w)
                .amount(22)
                .asset(NeoToken.SCRIPT_HASH)
                .toAddress(receiver);
        String uriAsString = uri.buildURI().getURIAsString();

        NeoSendRawTransaction response = uri.buildTransfer()
                .sign()
                .send();

        System.out.println("####################\n");
        if (response.hasError()) {
            System.out.printf("The neo-node responded with the error message '%s'.%n",
                    response.getError());
        } else {
            System.out.println("NEP-9 URI: '" + uriAsString + "'\n");
            String txHash = response.getSendRawTransaction().getHash();
            System.out.println("Successfully transmitted the transaction with hash " + txHash + ".");
            Await.waitUntilTransactionIsExecuted(txHash, neow3j);
            System.out.println("Tx: " + neow3j.getTransaction(txHash).send().getTransaction().toString());
            System.out.println("\n####################");
        }
    }
}
