package io.neow3j.examples.jsonrpc;

import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.methods.response.NeoValidateAddress;
import io.neow3j.protocol.http.HttpService;

import java.io.IOException;

public class ValidateNEOAddress {

    public static void main(String[] args) throws IOException {
        Neow3j neow3j = Neow3j.build(new HttpService("http://seed1.ngd.network:10332"));

        NeoValidateAddress validateReq = neow3j.validateAddress("ARvMqz3hEFE4qBkHAaPNxALquNQtBbH12f").send();

        System.out.println("\n####################");
        System.out.println("isValid=" + validateReq.getValidation().isValid());
        System.out.println("####################\n");
    }
}
