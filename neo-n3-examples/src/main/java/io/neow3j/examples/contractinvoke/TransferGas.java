package io.neow3j.examples.contractinvoke;

import static io.neow3j.examples.Constants.BOB;
import static io.neow3j.examples.Constants.NEOW3J;
import static io.neow3j.examples.Constants.WALLET;
import static io.neow3j.examples.Utils.trackSentTransaction;

import java.math.BigDecimal;
import java.math.BigInteger;

import io.neow3j.contract.GasToken;
import io.neow3j.contract.Hash256;
import io.neow3j.protocol.core.methods.response.NeoSendRawTransaction;
import io.neow3j.utils.Await;

public class TransferGas {

    public static void main(String[] args) throws Throwable {

        // Setup the GasToken class with a node connection for further calls to the contract.
        GasToken gasToken = new GasToken(NEOW3J);

        // The transfer method will add the wallets default account as the signer and use that
        // accounts tokens to cover the transfer amount.
        NeoSendRawTransaction response = gasToken
                .transfer(
                        WALLET, // the wallet to use for the transfer
                        BOB.getScriptHash(), // the recipient
//                        gasToken.toFractions(new BigDecimal("10.5")) // the transfer amount
                        // the amount can also be passed as a fraction value
                        new BigInteger("10_50000000")
                ).sign() // Signs the transaction with the account that was configured as the signer.
                .send(); // Sends the transaction to the neo-node.

        trackSentTransaction(response);
    }
}
