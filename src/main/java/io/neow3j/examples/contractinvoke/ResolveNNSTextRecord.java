package io.neow3j.examples.contractinvoke;

import io.neow3j.contract.NeoNameService;
import io.neow3j.contract.types.NNSName;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.RecordType;

import static io.neow3j.examples.Constants.NEOW3J_TESTNET;

public class ResolveNNSTextRecord {

    // The neow3j instance used in this example.
    static final Neow3j neow3j = NEOW3J_TESTNET;

    public static void main(String[] args) throws Throwable {

        // Use neow3j.setNNSResolver(Hash160) to set a different resolver contract hash. For example, if you're
        // working with a privatenet and have a NNS contract deployed there with a different address.

        NeoNameService nameService = new NeoNameService(neow3j);
        NNSName exampleNNS = new NNSName("example.neow3j.neo");
        String neow3jResolvedTxtRecord = nameService.resolve(exampleNNS, RecordType.TXT);

        System.out.println("####################\n");
        System.out.println("Resolved txt record: " + neow3jResolvedTxtRecord);
        System.out.println("\n####################");
    }

}
