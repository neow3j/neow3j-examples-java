package io.neow3j.examples.jsonrpc;

import io.neow3j.protocol.core.response.NeoGetNextBlockValidators.Validator;

import java.io.IOException;
import java.util.List;

import static io.neow3j.examples.Constants.NEOW3J;

public class GetInfoValidatorNodes {

    public static void main(String[] args) throws IOException {

        List<Validator> validators = NEOW3J.getNextBlockValidators()
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
