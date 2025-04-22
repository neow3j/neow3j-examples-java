package io.neow3j.examples.contractinvoke;

import io.neow3j.contract.SmartContract;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.response.NeoSendRawTransaction;
import io.neow3j.transaction.TransactionBuilder;
import io.neow3j.types.Hash160;

import static io.neow3j.examples.Constants.ALICE;
import static io.neow3j.examples.Constants.neow3jTestnet;
import static io.neow3j.examples.Utils.trackSentTransaction;
import static io.neow3j.transaction.AccountSigner.none;
import static io.neow3j.types.ContractParameter.integer;
import static io.neow3j.types.ContractParameter.string;

public class OracleMakeRequest {

    // The neow3j instance used in this example.
    static final Neow3j neow3j = neow3jTestnet();

    public static void main(String[] args) throws Throwable {
        // The contract CallOracleContract is deployed on testnet with the following address.
        Hash160 contractHash = new Hash160("0x4bf831c0fe505c2bf69c6269181cf712645433ce");
        SmartContract oracleExampleContract = new SmartContract(contractHash, neow3j);

        TransactionBuilder b = oracleExampleContract.invokeFunction("request",
                string("https://jsonplaceholder.typicode.com/users"), // the url
                string("$[*].email"), // the filter -> here: get the email of all users
                integer(10000000) // the gas for the oracle request
        );
        NeoSendRawTransaction response = b.signers(none(ALICE))
                .sign()
                .send();

        trackSentTransaction(response, neow3j);
    }

}
