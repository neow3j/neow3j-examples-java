package io.neow3j.examples.utils;

import io.neow3j.types.Hash160;
import io.neow3j.contract.NeoToken;
import io.neow3j.contract.NeoURI;

import java.math.BigDecimal;

public class BuildNeoURI {

    public static void main(String[] args) throws Throwable {

        Hash160 receiver = Hash160.fromAddress("NLnyLtep7jwyq1qhNPkwXbJpurC4jUT8ke");

        NeoURI uri = new NeoURI()
                .amount(new BigDecimal(22))
                .token(NeoToken.SCRIPT_HASH)
                .to(receiver);

        String uriAsString = uri.buildURI().getURIAsString();

        System.out.println("\n####################");
        System.out.println("NEP-9 URI: '" + uriAsString + "'");
        System.out.println("####################\n");
    }
}
