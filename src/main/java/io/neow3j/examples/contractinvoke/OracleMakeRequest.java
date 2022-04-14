package io.neow3j.examples.contractinvoke;

import io.neow3j.contract.SmartContract;
import io.neow3j.transaction.TransactionBuilder;
import io.neow3j.types.Hash160;
import io.neow3j.types.Hash256;

import java.math.BigDecimal;
import java.math.BigInteger;

import static io.neow3j.contract.Token.toFractions;
import static io.neow3j.examples.Constants.ALICE;
import static io.neow3j.examples.Constants.NEOW3J_TESTNET;
import static io.neow3j.transaction.AccountSigner.none;
import static io.neow3j.types.ContractParameter.integer;
import static io.neow3j.types.ContractParameter.string;

public class OracleMakeRequest {

    public static void main(String[] args) throws Throwable {
        // The contract CallOracleContract is deployed on testnet with the following address.
        Hash160 contractHash = new Hash160("ae2e396f6b082cadeb74d13d978960fa90af28ed");
        SmartContract smartContract = new SmartContract(contractHash, NEOW3J_TESTNET);

        BigInteger gasForResponse = toFractions(BigDecimal.ONE, 8);
        TransactionBuilder b = smartContract.invokeFunction("request",
                // Change the index 1 to 2 (i.e., 'todos/2') in order to see whether the contract storage was actually
                // updated.
                string("https://jsonplaceholder.typicode.com/todos/1"),
                string(""), // the filter
                integer(gasForResponse)); // the amount of GAS provided for creating the response tx
        Hash256 hash = b.signers(none(ALICE))
                .sign()
                .send()
                .getSendRawTransaction()
                .getHash();

        System.out.println("Request transaction hash: " + hash);
    }

}
