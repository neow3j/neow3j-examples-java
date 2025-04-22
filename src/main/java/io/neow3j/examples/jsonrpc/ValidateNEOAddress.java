package io.neow3j.examples.jsonrpc;

import io.neow3j.protocol.Neow3j;

import java.io.IOException;

import static io.neow3j.examples.Constants.neow3jPrivatenet;

public class ValidateNEOAddress {

    // The neow3j instance used in this example.
    static final Neow3j neow3j = neow3jPrivatenet();

    public static void main(String[] args) throws IOException {

        String address = "NWcx4EfYdfqn5jNjDz8AHE6hWtWdUGDdmy";

        Boolean isValid = neow3j.validateAddress(address)
                .send()
                .getValidation().isValid();

        System.out.println("\n####################");
        if (isValid) {
            System.out.println(address + " is a valid Neo address.");
        } else {
            System.out.println(address + " is not a valid Neo address.");
        }
        System.out.println("####################");
    }

}
