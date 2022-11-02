package io.neow3j.examples.jsonrpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.neow3j.contract.NeoToken;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.response.ContractState;

import java.io.IOException;

import static io.neow3j.examples.Constants.NEOW3J_PRIVATENET;

public class GetContractState {

    // The neow3j instance used in this example.
    static final Neow3j neow3j = NEOW3J_PRIVATENET;

    public static void main(String[] args) throws IOException {

        ContractState state = neow3j.getContractState(NeoToken.SCRIPT_HASH)
                .send()
                .getContractState();

        System.out.println("\n####################");
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(state));
        System.out.println("####################");
    }

}
