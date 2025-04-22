package io.neow3j.examples.jsonrpc;

import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.response.NeoGetNextBlockValidators.Validator;

import java.io.IOException;
import java.util.List;

import static io.neow3j.examples.Constants.neow3jPrivatenet;

public class GetInfoValidatorNodes {

    // The neow3j instance used in this example.
    static final Neow3j neow3j = neow3jPrivatenet();

    public static void main(String[] args) throws IOException {

        List<Validator> validators = neow3j.getNextBlockValidators()
                .send()
                .getResult();

        System.out.println("\n####################");
        System.out.println(validators);
        if (!validators.isEmpty()) {
            System.out.println("\nFirst validator entry:");
            System.out.println("pubKey: " + validators.get(0).getPublicKey());
            System.out.println("votes: " + validators.get(0).getVotes());
            System.out.println("active: " + validators.get(0).getActive());
        }
        System.out.println("####################");
    }

}
