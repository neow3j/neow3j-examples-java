package io.neow3j.examples.jsonrpc;

import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.methods.response.NeoGetContractState.ContractState;
import io.neow3j.protocol.http.HttpService;

import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GetContractState {

    public static void main(String[] args) throws IOException {
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:40332"));

        ContractState state = neow3j.getContractState("3e1c7c20b1ddbb998b1048061e7665c426b85b14")
                .send()
                .getContractState();

        System.out.println("\n####################");
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(state.getManifest()));
        System.out.println("####################");
    }
}
