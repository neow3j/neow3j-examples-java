package io.neow3j.examples.jsonrpc;

import java.io.IOException;
import java.util.Arrays;

import io.neow3j.model.NeoConfig;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.methods.response.NeoGetApplicationLog;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.wallet.Account;
import io.neow3j.wallet.Wallet;

public class GetApplicationLogsForTx {

    public static void main(String[] args) throws IOException {
        // Set the magic number according to the Neo network's configuration. It is used when
        // signing transactions.
        NeoConfig.setMagicNumber(new byte[]{0x01, 0x03, 0x00, 0x0}); // Magic number 769

        // Set up the connection to the neo-node
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:40332"));
        // Setup an account and wallet that are used as the sender and for signing the transaction
        // Make sure that the account has a sufficient GAS and NEO balance for payment and fees.
        Account a = Account.fromWIF("L3kCZj6QbFPwbsVhxnB8nUERDy4mhCSrWJew4u5Qh5QmGMfnCTda");
        Account multiSigAccount = Account.createMultiSigAccount(
                Arrays.asList(a.getECKeyPair().getPublicKey()), 1);
        Wallet w = Wallet.withAccounts(a, multiSigAccount);

        NeoGetApplicationLog al = neow3j
            .getApplicationLog("f8ceb2a490f5412185f2a8899f8e38c7272db4a84076c5257dc71d356e516e72")
            .send();

        System.out.println("\n####################");
        System.out.println("Application log: ");
        System.out.println(al.getApplicationLog());
        System.out.println("\n####################");
            
    }

}
