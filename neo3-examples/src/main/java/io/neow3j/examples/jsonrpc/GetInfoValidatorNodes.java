package io.neow3j.examples.jsonrpc;

import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.methods.response.NeoGetValidators.Validator;
import io.neow3j.protocol.http.HttpService;

import java.io.IOException;
import java.util.List;

public class GetInfoValidatorNodes {

    public static void main(String[] args) throws IOException {
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:40332"));

        List<Validator> validators = neow3j.getValidators()
                .send()
                .getValidators();

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
