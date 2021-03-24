package io.neow3j.examples.contractinvoke;

import static io.neow3j.examples.Constants.BOB;
import static io.neow3j.examples.Constants.NEOW3J;
import static io.neow3j.examples.Constants.WALLET;
import java.math.BigDecimal;
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
        NeoSendRawTransaction response = gasToken.transfer(WALLET, BOB.getAddress(), BigDecimal.valueOf(10000L))
                .sign() // Signs the transaction with the account that was configured as the signer.
                .send(); // Sends the transaction to the neo-node.

        System.out.println("####################\n");
        if (response.hasError()) {
            System.out.printf("The neo-node responded with the error message '%s'.%n",
                    response.getError());
        } else {
            Hash256 txHash = response.getSendRawTransaction().getHash();
            System.out.printf("Successfully transmitted the transaction with hash '%s'.%n", txHash);
            Await.waitUntilTransactionIsExecuted(txHash, NEOW3J);
            System.out.println("Tx: " + NEOW3J.getTransaction(txHash).send().getTransaction().toString());
            System.out.println("\n####################");
        }
    }
}
