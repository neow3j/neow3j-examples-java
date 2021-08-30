package io.neow3j.examples.contractinvoke;

import io.neow3j.contract.GasToken;
import io.neow3j.protocol.core.response.NeoSendRawTransaction;
import io.neow3j.transaction.AccountSigner;
import io.neow3j.transaction.ContractSigner;
import io.neow3j.transaction.Transaction;
import io.neow3j.transaction.Witness;
import io.neow3j.types.Hash160;

import java.math.BigInteger;

import static io.neow3j.examples.Constants.ALICE;
import static io.neow3j.examples.Constants.BOB;
import static io.neow3j.examples.Constants.NEOW3J;
import static io.neow3j.examples.Utils.trackSentTransaction;
import static io.neow3j.types.ContractParameter.hash160;
import static io.neow3j.types.ContractParameter.integer;
import static io.neow3j.types.ContractParameter.string;

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
        Hash160 contract = new Hash160("0xe7c27a246c701755574134aaa094b4fd5c79f78a");

        GasToken gas = new GasToken(NEOW3J);
        NeoSendRawTransaction response = gas.transfer(
                        contract,
                        BOB.getScriptHash(),
                        new BigInteger("100"))
                .signers( // The contract owner (Alice) and the contract are both required here.
                        AccountSigner.calledByEntry(ALICE),
                        ContractSigner.calledByEntry(contract, string("hello, world!")))
                .sign() // Adds a witness for Alice and one for the contract including the
                        // parameter ("hello, world!") that should be passed to the verify method.
                .send();

        trackSentTransaction(response);
    }
}
