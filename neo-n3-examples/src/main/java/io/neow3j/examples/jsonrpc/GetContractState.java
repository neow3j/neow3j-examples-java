package io.neow3j.examples.jsonrpc;

import static io.neow3j.examples.Constants.NEOW3J;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.neow3j.protocol.core.methods.response.NeoGetContractState.ContractState;

public class GetContractState {

    public static void main(String[] args) throws IOException {

        ContractState state = NEOW3J.getContractState("0xef4073a0f2b305a38ec4050e4d3d28bc40ea63f5")
                .send()
                .getContractState();

        System.out.println("\n####################");
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(state));
        System.out.println("####################");
    }
}
