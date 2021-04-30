package io.neow3j.examples.contractinvoke;

import static io.neow3j.examples.Constants.ALICE;
import static io.neow3j.examples.Constants.NEOW3J;
import static io.neow3j.examples.Constants.WALLET;
import static io.neow3j.examples.Utils.trackSentTransaction;

import java.math.BigDecimal;
import java.math.BigInteger;

import io.neow3j.contract.GasToken;
import io.neow3j.contract.Token;
import io.neow3j.protocol.core.methods.response.NeoSendRawTransaction;

public class TransferGas {

    public static void main(String[] args) throws Throwable {

        // Setup the GasToken class with a node connection for further calls to the contract.
        GasToken gasToken = new GasToken(NEOW3J);

        // The transfer method will add the wallets default account as the signer and use that
        // accounts tokens to cover the transfer amount.
        NeoSendRawTransaction response = gasToken
                .transfer(
                        WALLET, // the wallet to use for the transfer
                        ALICE.getScriptHash(), // the recipient
                        gasToken.toFractions(new BigDecimal("10.5")) // the transfer amount
                        // the amount can also be passed as a fraction value directly:
//                        new BigInteger("1050000000")

                        // or by calling the static method `Token.toFractions()` providing the
                        // number of decimal numbers:
//                        Token.toFractions(new BigDecimal("10.5"), 8)
                )
                .sign() // Signs the transaction with the account that was configured as the signer.
                .send(); // Sends the transaction to the neo-node.

        trackSentTransaction(response);
    }
}
