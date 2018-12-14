package io.neow3j.examples.jsonrpc;

import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.methods.response.NeoGetValidators;
import io.neow3j.protocol.http.HttpService;

import java.io.IOException;

public class GetInfoValidatorNodes {

    public static void main(String[] args) throws IOException {
        Neow3j neow3j = Neow3j.build(new HttpService("http://seed1.ngd.network:10332"));

        NeoGetValidators getValidatorsReq = neow3j.getValidators().send();
        System.out.println(getValidatorsReq.getValidators());
    }

}
