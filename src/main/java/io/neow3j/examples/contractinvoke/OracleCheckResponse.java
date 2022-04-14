package io.neow3j.examples.contractinvoke;

import io.neow3j.contract.SmartContract;
import io.neow3j.protocol.core.response.InvocationResult;
import io.neow3j.types.Hash160;

import java.io.IOException;

import static io.neow3j.examples.Constants.NEOW3J_TESTNET;

public class OracleCheckResponse {

    public static void main(String[] args) throws IOException {
        // The contract CallOracleContract is deployed on testnet with the following address.
        Hash160 contractHash = new Hash160("ae2e396f6b082cadeb74d13d978960fa90af28ed");
        SmartContract smartContract = new SmartContract(contractHash, NEOW3J_TESTNET);

        InvocationResult result = smartContract.callInvokeFunction("getStoredUrl").getInvocationResult();
        String storedURL = result.getStack().get(0).getString();
        System.out.println("Stored URL: " + storedURL);

        result = smartContract.callInvokeFunction("getStoredResponseCode").getInvocationResult();
        String storedResponseCode = result.getStack().get(0).getString();
        System.out.println("Stored response code: " + storedResponseCode);

        result = smartContract.callInvokeFunction("getStoredResponse").getInvocationResult();
        String storedResponse = result.getStack().get(0).getString();
        System.out.println("Stored value: " + storedResponse);
    }

}
