package io.neow3j.examples.jsonrpc;

import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.http.HttpService;

import java.io.IOException;

public class ValidateNEOAddress {

    public static void main(String[] args) throws IOException {
        Neow3j neow3j = Neow3j.build(new HttpService("http:localhost:40332"));

        String address = "AVGpjFiocR1BdYhbYWqB6Ls6kcmzx4FWhm";
        Boolean isValid = neow3j.validateAddress(address).send().getValidation().isValid();

        System.out.println("\n####################");
        if (isValid) {
            System.out.println("'" + address + "' is a valid Neo address.");
        } else {
            System.out.println("'" + address + "' is not a valid Neo address.");
        }
        System.out.println("####################");
    }
}
