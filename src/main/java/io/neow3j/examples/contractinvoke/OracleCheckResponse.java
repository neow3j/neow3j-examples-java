package io.neow3j.examples.contractinvoke;

import io.neow3j.contract.SmartContract;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.stackitem.StackItem;
import io.neow3j.types.Hash160;

import java.io.IOException;
import java.util.List;

import static io.neow3j.examples.Constants.neow3jTestnet;
import static java.util.Arrays.asList;

public class OracleCheckResponse {

    // The neow3j instance used in this example.
    static final Neow3j neow3j = neow3jTestnet();

    public static void main(String[] args) throws IOException {
        // The OracleExampleContract is deployed on testnet with the following address.
        Hash160 contractHash = new Hash160("0x4bf831c0fe505c2bf69c6269181cf712645433ce");
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
