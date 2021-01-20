package io.neow3j.examples.contractinvoke;

import java.io.IOException;
import io.neow3j.contract.ScriptHash;
import io.neow3j.contract.SmartContract;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.http.HttpService;

public class InvokeAxLabsToken {

    public static void main(String[] args) throws IOException {

        // Set up the connection to the neo-node.
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:40332"));

        // Setup the NeoToken class with a node connection for further calls to the contract.
        SmartContract axlabsToken = new SmartContract(new ScriptHash("fb77779026d593d1355865d87a8800ef61ed324b"), neow3j);
        System.out.println("Symbol: " + axlabsToken.callFuncReturningString("symbol"));
       }
}
