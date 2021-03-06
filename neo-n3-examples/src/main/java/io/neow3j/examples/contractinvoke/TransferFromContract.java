package io.neow3j.examples.contractinvoke;

import static io.neow3j.types.ContractParameter.hash160;
import static io.neow3j.types.ContractParameter.integer;
import static io.neow3j.types.ContractParameter.string;
import static io.neow3j.examples.Constants.ALICE;
import static io.neow3j.examples.Constants.NEOW3J;
import static io.neow3j.examples.Constants.WALLET;
import static io.neow3j.examples.Utils.trackSentTransaction;

import io.neow3j.contract.GasToken;
import io.neow3j.types.Hash160;
import io.neow3j.protocol.core.response.NeoSendRawTransaction;
import io.neow3j.transaction.Signer;
import io.neow3j.transaction.Transaction;
import io.neow3j.transaction.Witness;

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

        // GAS holding contract (`OnVerificationContract`).
        Hash160 contract = new Hash160("5dca8617f3db7ee6b65788e2941c9bd9ff1a2ef2");

        GasToken gas = new GasToken(NEOW3J);
        Transaction tx = gas
                .invokeFunction(
                        "transfer",
                        hash160(contract),
                        hash160(ALICE.getScriptHash()),
                        integer(100),
                        string("a GAS transfer")
                )
                .wallet(WALLET)
                .signers( // The contract owner and the contract are both required here.
                        Signer.calledByEntry(ALICE.getScriptHash()),
                        Signer.calledByEntry(contract)
                )
                .getUnsignedTransaction();

        // The contract owner needs to sign the transaction.
        tx.addWitness(Witness.create(tx.getHashData(), ALICE.getECKeyPair()));
        // A witness is also needed for the contract signer, but it is empty.
        tx.addWitness(new Witness(new byte[0], new byte[0]));

        NeoSendRawTransaction response = tx.send();

        trackSentTransaction(response);
    }
}
