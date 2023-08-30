package io.neow3j.examples.contractinvoke;

import io.neow3j.contract.SmartContract;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.stackitem.StackItem;
import io.neow3j.types.Hash160;

import java.io.IOException;
import java.util.List;

import static io.neow3j.examples.Constants.NEOW3J_TESTNET;
import static java.util.Arrays.asList;

public class OracleCheckResponse {

    // The neow3j instance used in this example.
    static final Neow3j neow3j = NEOW3J_TESTNET;

    public static void main(String[] args) throws IOException {
        // The OracleExampleContract is deployed on testnet with the following address.
        Hash160 contractHash = new Hash160("f1922e6e590acfb52e52fa1c0d689c52ebe9a8e2");
        SmartContract oracleExampleContract = new SmartContract(contractHash, neow3j);

        List<StackItem> list = oracleExampleContract.callInvokeFunction("getResponse", asList())
                .getInvocationResult().getFirstStackItem().getList();

        System.out.println("######################");
        System.out.println("URL:                     " + list.get(0).getString());
        System.out.println("Response Code (decimal): " + list.get(1).getInteger());
        System.out.println("Response:                " + list.get(2).getString());
        System.out.println("######################");
    }

}
