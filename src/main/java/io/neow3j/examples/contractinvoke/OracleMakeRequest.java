package io.neow3j.examples.contractinvoke;

import io.neow3j.contract.SmartContract;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.response.NeoSendRawTransaction;
import io.neow3j.transaction.TransactionBuilder;
import io.neow3j.types.Hash160;

import static io.neow3j.examples.Constants.ALICE;
import static io.neow3j.examples.Constants.NEOW3J_TESTNET;
import static io.neow3j.examples.Utils.trackSentTransaction;
import static io.neow3j.transaction.AccountSigner.none;
import static io.neow3j.types.ContractParameter.string;

public class OracleMakeRequest {

    // The neow3j instance used in this example.
    static final Neow3j neow3j = NEOW3J_TESTNET;

    public static void main(String[] args) throws Throwable {
        // The contract CallOracleContract is deployed on testnet with the following address.
        Hash160 contractHash = new Hash160("f1922e6e590acfb52e52fa1c0d689c52ebe9a8e2");
        SmartContract oracleExampleContract = new SmartContract(contractHash, neow3j);

        TransactionBuilder b = oracleExampleContract.invokeFunction("request",
                string("https://jsonplaceholder.typicode.com/users"), // the url
                string("$[*].email")); // the filter -> here: get the email of all users
        NeoSendRawTransaction response = b.signers(none(ALICE))
                .sign()
                .send();

        System.out.println("Request transaction hash: " + response.getSendRawTransaction().getHash());
        trackSentTransaction(response, neow3j);
    }

}
