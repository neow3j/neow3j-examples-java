package io.neow3j.examples;

import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.types.Hash160;
import io.neow3j.wallet.Account;
import io.neow3j.wallet.Wallet;

import static java.util.Arrays.asList;

public class Constants {

    public static final HttpService privateNetHttpService = new HttpService("http://127.0.0.1:40332", true);
    public static final HttpService testnetHttpService = new HttpService("http://seed2t5.neo.org:20332", true);
    public static final HttpService mainnetHttpService = new HttpService("http://seed2.neo.org:10332", true);

    // This sets up the connection to a Neo node of a private network.
    public static Neow3j neow3jPrivatenet() {
        return Neow3j.build(privateNetHttpService);
    }

    // This sets up the connection to a testnet Neo node.
    public static Neow3j neow3jTestnet() {
        Neow3j neow3j = Neow3j.build(testnetHttpService);
        neow3j.setNNSResolver(TESTNET_NNS_CONTRACT_HASH);
        return neow3j;
    }

    // This sets up the connection to a mainnet Neo node.
    public static Neow3j neow3jMainnet() {
        return Neow3j.build(mainnetHttpService);
    }

    // This is Alice's account which is also available on the pre-configured private network.
    public static Account ALICE = Account.fromWIF("L1eV34wPoj9weqhGijdDLtVQzUpWGHszXXpdU9dPuh2nRFFzFa7E");

    // This is Bob's account which is also available on the pre-configured private network.
    public static Account BOB = Account.fromWIF("L3cNMQUSrvUrHx1MzacwHiUeCWzqK2MLt5fPvJj9mz6L2rzYZpok");

    // The genesis account holds all NEO and GAS at the beginning of a new Neo blockchain. The genesis account is a
    // multi-sig account. In our case it is made up of only one account, namely, Alice's account.
    public static Account GENESIS = Account.createMultiSigAccount(asList(ALICE.getECKeyPair().getPublicKey()), 1);

    public static Wallet WALLET = Wallet.withAccounts(ALICE, GENESIS, BOB);

    public static final Hash160 TESTNET_NNS_CONTRACT_HASH = new Hash160("0xd4dbd72c8965b8f12c14d37ad57ddd91ee1d98cb");

}
