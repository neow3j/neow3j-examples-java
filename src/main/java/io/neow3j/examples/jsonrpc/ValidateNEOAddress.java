package io.neow3j.examples.jsonrpc;

import static io.neow3j.examples.Constants.NEOW3J;
import java.io.IOException;

public class ValidateNEOAddress {

    public static void main(String[] args) throws IOException {

        String address = "NWcx4EfYdfqn5jNjDz8AHE6hWtWdUGDdmy";

        Boolean isValid = NEOW3J.validateAddress(address)
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
