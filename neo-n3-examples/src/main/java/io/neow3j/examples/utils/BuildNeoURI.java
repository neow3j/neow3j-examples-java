package io.neow3j.examples.utils;

import io.neow3j.contract.Hash160;
import io.neow3j.contract.NeoToken;
import io.neow3j.contract.NeoURI;

public class BuildNeoURI {

    public static void main(String[] args) throws Throwable {

        Hash160 receiver = Hash160.fromAddress("NLnyLtep7jwyq1qhNPkwXbJpurC4jUT8ke");

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
