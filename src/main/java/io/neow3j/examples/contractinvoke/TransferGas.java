package io.neow3j.examples.contractinvoke;

import static io.neow3j.examples.Constants.ALICE;
import static io.neow3j.examples.Constants.BOB;
import static io.neow3j.examples.Constants.NEOW3J;
import static io.neow3j.examples.Utils.trackSentTransaction;

import java.math.BigDecimal;

import io.neow3j.contract.GasToken;
import io.neow3j.protocol.core.response.NeoSendRawTransaction;
import io.neow3j.transaction.AccountSigner;
import io.neow3j.transaction.Transaction;
import io.neow3j.transaction.TransactionBuilder;
import io.neow3j.wallet.Account;

public class TransferGas {

    public static void main(String[] args) throws Throwable {

        // Setup the GasToken class with a node connection for further calls to the contract.
        GasToken gasToken = new GasToken(NEOW3J);

        // The transfer method will add the wallets default account as the signer and use that
        // accounts tokens to cover the transfer amount.
        NeoSendRawTransaction response = gasToken
                // The amount can be passed directly in fractions, e.g., BigInteger("150000000").
                // But here we use the method `toFractions()` providing that takes the number of
                // decimals of the specific token (here GasToken) into account. There is also a
                // static method `Token.toFractions(...)` for manual setting of the decimal number.
                .transfer(ALICE, BOB.getScriptHash(), gasToken.toFractions(new BigDecimal("1.5")))
                .sign() // Signs the transaction with the account that was configured as the signer.
                .send(); // Sends the transaction to the neo-node.

        trackSentTransaction(response);
    }
}
