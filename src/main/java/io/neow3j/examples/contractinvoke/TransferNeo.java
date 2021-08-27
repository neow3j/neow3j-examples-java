package io.neow3j.examples.contractinvoke;

import io.neow3j.contract.NeoToken;
import io.neow3j.protocol.core.response.NeoSendRawTransaction;

import java.math.BigInteger;

import static io.neow3j.examples.Constants.ALICE;
import static io.neow3j.examples.Constants.GENESIS;
import static io.neow3j.examples.Constants.NEOW3J;
import static io.neow3j.examples.Utils.trackSentTransaction;

public class TransferNeo {

    public static void main(String[] args) throws Throwable {
        // Setup the NeoToken class with a node connection for further calls to the contract.
        NeoToken neoToken = new NeoToken(NEOW3J);

        // The transfer method will add the wallets default account as the signer and use that
        // accounts tokens to cover the transfer amount.
        NeoSendRawTransaction response = neoToken.transfer(
                        GENESIS, ALICE.getScriptHash(), new BigInteger("1000"))
                .getUnsignedTransaction()
                // Because GENESIS is a multi-sig account that holds all the NEO on the
                // neo-express instance we use for these examples, we can't use `sign()` but need
                // to provide the witness manually. ALICE holds the necessary private key since
                // GENESIS is a multi-sig only made up of one account.
                .addMultiSigWitness(GENESIS.getVerificationScript(), ALICE)
                .send(); // Sends the transaction to the neo-node.

        trackSentTransaction(response);
    }
}
