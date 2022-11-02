package io.neow3j.examples.contractinvoke;

import java.math.BigDecimal;

import io.neow3j.contract.GasToken;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.response.NeoSendRawTransaction;

import static io.neow3j.examples.Constants.ALICE;
import static io.neow3j.examples.Constants.BOB;
import static io.neow3j.examples.Constants.NEOW3J_PRIVATENET;
import static io.neow3j.examples.Utils.trackSentTransaction;

public class TransferGas {

    // The neow3j instance used in this example.
    static final Neow3j neow3j = NEOW3J_PRIVATENET;

    public static void main(String[] args) throws Throwable {

        // Set up the GasToken class with a node connection for further calls to the contract.
        GasToken gasToken = new GasToken(neow3j);

        // The transfer method will add the wallets default account as the signer and use that accounts tokens to
        // cover the transfer amount.
        NeoSendRawTransaction response = gasToken
                // The amount can be passed directly in fractions, e.g., BigInteger("150000000").
                // But here we use the method `toFractions()` providing that takes the number of decimals of the
                // specific token (here GasToken) into account. There is also a static method `Token.toFractions(...)`
                // for manual setting of the decimal number.
                .transfer(ALICE, BOB.getScriptHash(), gasToken.toFractions(new BigDecimal("1.5")))
                .sign() // Signs the transaction with the account that was configured as the signer.
                .send(); // Sends the transaction to the neo-node.

        trackSentTransaction(response, neow3j);
    }

}
