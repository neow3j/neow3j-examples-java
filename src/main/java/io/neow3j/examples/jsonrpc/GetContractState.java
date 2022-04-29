package io.neow3j.examples.jsonrpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.neow3j.contract.NeoToken;
import io.neow3j.protocol.core.response.ContractState;

import java.io.IOException;

import static io.neow3j.examples.Constants.NEOW3J;

public class GetContractState {

    public static void main(String[] args) throws IOException {

        ContractState state = NEOW3J.getContractState(NeoToken.SCRIPT_HASH)
                .send()
                .getContractState();

        System.out.println("\n####################");
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(state));
        System.out.println("####################");
    }

}
