package io.neow3j.examples;

import io.neow3j.contract.GasToken;
import io.neow3j.contract.NeoToken;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.types.Hash160;
import io.neow3j.wallet.Account;
import io.neow3j.wallet.Wallet;

import static java.util.Arrays.asList;

public class Constants {

    // This sets up the connection to the Neo node of our private network or the testnet respectively.
    public static Neow3j NEOW3J_PRIVATENET = Neow3j.build(new HttpService("http://127.0.0.1:40332", true));
    public static Neow3j NEOW3J_TESTNET = Neow3j.build(new HttpService("http://seed2t5.neo.org:20332", true));

    // This is Alice's account which is also available on the pre-configured private network.
    public static Account ALICE = Account.fromWIF("L1eV34wPoj9weqhGijdDLtVQzUpWGHszXXpdU9dPuh2nRFFzFa7E");

    // This is Bob's account which is also available on the pre-configured private network.
    public static Account BOB = Account.fromWIF("L3cNMQUSrvUrHx1MzacwHiUeCWzqK2MLt5fPvJj9mz6L2rzYZpok");

    // The genesis account holds all NEO and GAS at the beginning of a new Neo blockchain. The genesis account is a
    // multi-sig account. In our case it is made up of only one account, namely, Alice' account.
    public static Account GENESIS = Account.createMultiSigAccount(asList(ALICE.getECKeyPair().getPublicKey()), 1);

    public static Wallet WALLET = Wallet.withAccounts(ALICE, GENESIS, BOB);

    public static GasToken gasToken = new GasToken(NEOW3J_PRIVATENET);
    public static NeoToken neoToken = new NeoToken(NEOW3J_PRIVATENET);
    public static final Hash160 TESTNET_NNS_CONTRACT_HASH = new Hash160("0xd4dbd72c8965b8f12c14d37ad57ddd91ee1d98cb");

    // Set NNS resolver for testnet
    static {
        NEOW3J_TESTNET.setNNSResolver(TESTNET_NNS_CONTRACT_HASH);
    }

}
