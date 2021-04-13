package io.neow3j.examples.contractinvoke;

import static io.neow3j.examples.Constants.BOB;
import static io.neow3j.examples.Constants.NEOW3J;
import static io.neow3j.examples.Constants.WALLET;
import static io.neow3j.examples.Utils.trackSentTransaction;

import java.math.BigDecimal;
import java.math.BigInteger;

import io.neow3j.contract.Hash256;
import io.neow3j.contract.NeoToken;
import io.neow3j.protocol.core.methods.response.NeoSendRawTransaction;
import io.neow3j.utils.Await;

public class TransferNeo {

    public static void main(String[] args) throws Throwable {
        
        // Setup the NeoToken class with a node connection for further calls to the contract.
        NeoToken neoToken = new NeoToken(NEOW3J);

        // The transfer method will add the wallets default account as the signer and use that
        // accounts tokens to cover the transfer amount.
        NeoSendRawTransaction response = neoToken.transfer(WALLET, BOB.getScriptHash(), BigInteger.ONE)
                .sign() // Signs the transaction with the account that was configured as the signer.
                .send(); // Sends the transaction to the neo-node.

        trackSentTransaction(response);
    }
}
