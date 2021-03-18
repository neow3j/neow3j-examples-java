package io.neow3j.examples.contractinvoke;

import static io.neow3j.contract.ContractParameter.hash160;
import static io.neow3j.contract.ContractParameter.integer;
import static io.neow3j.contract.ContractParameter.string;
import io.neow3j.contract.GasToken;
import io.neow3j.contract.Hash160;
import io.neow3j.contract.Hash256;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.methods.response.NeoSendRawTransaction;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.transaction.Signer;
import io.neow3j.transaction.Transaction;
import io.neow3j.transaction.Witness;
import io.neow3j.utils.Await;
import io.neow3j.wallet.Account;
import io.neow3j.wallet.Wallet;

/*
 * This example shows how to transfer tokens from a smart contract. The contract used for this 
 * example is the `OnVerificationContract`. This contract has a `verify` method that is called when 
 * we try to send tokens away from it. 
 * 
 * For this example to work the `OnVerificationContract` has to be deployed first and GAS needs to
 * be sent to it.
 */
public class TransferFromContract {

    public static void main(String[] args) throws Throwable {

        // Set up the connection to the neo-node
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:40332"));

        // Setup the account that is the owner of the OnVerificationContract. It is needed to sign 
        // the transaction that transfers GAS away from the contract.
        Account a = Account.fromWIF("L3kCZj6QbFPwbsVhxnB8nUERDy4mhCSrWJew4u5Qh5QmGMfnCTda");
        Wallet w = Wallet.withAccounts(a);

        // GAS holding contract (`OnVerificationContract`).
        Hash160 contract = new Hash160("5dca8617f3db7ee6b65788e2941c9bd9ff1a2ef2");

        GasToken gas = new GasToken(neow3j);
        Transaction tx = gas
            .invokeFunction("transfer", hash160(contract), hash160(a.getScriptHash()), integer(100), 
                string("a GAS transfer"))
            .wallet(w)
            .signers( // The contract owner and the contract are both required here.
                Signer.calledByEntry(a.getScriptHash()), 
                Signer.calledByEntry(contract))
            .getUnsignedTransaction();

        // The contract owner needs to sign the transaction.
        tx.addWitness(Witness.create(tx.getHashData(), a.getECKeyPair()));
        // A witness is also needed for the contract signer, but it is empty.
        tx.addWitness(new Witness(new byte[0], new byte[0]));

        NeoSendRawTransaction response = tx.send();
        System.out.println("####################\n");
        if (response.hasError()) {
            System.out.printf("The neo-node responded with the error message '%s'.%n",
                    response.getError());
        } else {
            Hash256 txHash = response.getSendRawTransaction().getHash();
            System.out.printf("Successfully transmitted the transaction with hash '%s'.%n", txHash);
            Await.waitUntilTransactionIsExecuted(txHash, neow3j);
            System.out.println("Tx: " + neow3j.getTransaction(txHash).send().getTransaction().toString());
            System.out.println("\n####################");
        }
    }
}
