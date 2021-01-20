package io.neow3j.examples.contractinvoke;

import io.neow3j.contract.NeoToken;
import io.neow3j.contract.NeoURI;
import io.neow3j.contract.ScriptHash;

public class BuildNeoURI {

    public static void main(String[] args) throws Throwable {

        ScriptHash receiver = ScriptHash.fromAddress("NLnyLtep7jwyq1qhNPkwXbJpurC4jUT8ke");

        NeoURI uri = new NeoURI()
                .amount(22)
                .asset(NeoToken.SCRIPT_HASH)
                .toAddress(receiver);

        String uriAsString = uri.buildURI().getURIAsString();

        System.out.println("\n####################");
        System.out.println("NEP-9 URI: '" + uriAsString + "'");
        System.out.println("####################\n");
    }
}
