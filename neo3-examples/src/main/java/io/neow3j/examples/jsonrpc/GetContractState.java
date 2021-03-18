package io.neow3j.examples.jsonrpc;

import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.methods.response.NeoGetContractState.ContractState;
import io.neow3j.protocol.http.HttpService;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class GetContractState {

    public static void main(String[] args) throws IOException {
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:40332"));

        ContractState state = neow3j.getContractState("0xef4073a0f2b305a38ec4050e4d3d28bc40ea63f5")
                .send()
                .getContractState();

        System.out.println("\n####################");
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(state));
        System.out.println("####################");
    }
}
