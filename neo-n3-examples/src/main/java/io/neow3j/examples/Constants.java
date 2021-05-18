package io.neow3j.examples;

import static java.util.Collections.singletonList;

import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.wallet.Account;
import io.neow3j.wallet.Wallet;

public class Constants {

    // This sets up the connection to the neo-node of our private network.
    public static Neow3j NEOW3J = Neow3j.build(new HttpService("http://127.0.0.1:40332"));

    public static Neow3j NEOW3J_TESTNET =
            Neow3j.build(new HttpService("http://seed2t.neo.org:20332"));

    // This is Alice' account which is also available on the pre-configured private network.
    public static Account ALICE =
            Account.fromWIF("L1eV34wPoj9weqhGijdDLtVQzUpWGHszXXpdU9dPuh2nRFFzFa7E");

    // This is Bob's account which is also available on the pre-configured private network.
    public static Account BOB =
            Account.fromWIF("L3cNMQUSrvUrHx1MzacwHiUeCWzqK2MLt5fPvJj9mz6L2rzYZpok");

    // The genesis account holds all NEO and GAS at the beginning of a new Neo blockchain. The
    // genesis account is a multi-sig account. In our case it is made up of only one account,
    // namely, Alice' account.
    public static Account GENESIS = Account.createMultiSigAccount(
            singletonList(ALICE.getECKeyPair().getPublicKey()), 1);

    public static Wallet WALLET = Wallet.withAccounts(ALICE, GENESIS, BOB);

}
