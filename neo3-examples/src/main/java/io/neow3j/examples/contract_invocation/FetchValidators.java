package io.neow3j.examples.contract_invocation;

import io.neow3j.contract.NeoToken;
import io.neow3j.crypto.ECKeyPair.ECPublicKey;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.utils.Numeric;
import io.neow3j.wallet.Account;
import java.io.IOException;
import java.util.List;

@SuppressWarnings("unused")
public class FetchValidators {

    public static void main(String[] args) throws IOException {

        // Set up the connection to the neo-node and the wallet for signing the transaction.
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:40332"));
        Account a = Account.fromWIF("L1WMhxazScMhUrdv34JqQb1HFSQmWeN2Kpc1R9JGKwL7CDNP21uR");

        // Setup the NeoToken class with a node connection for further calls to the contract.
        NeoToken neo = new NeoToken(neow3j);

        // Get the public keys of nodes that are validators of the network.
        List<ECPublicKey> validators = neo.getValidators();

        System.out.println("####################\n");
        System.out.printf("The Neo network has %d validators. Their public keys are:%n",
                validators.size());
        for (ECPublicKey validator : validators) {
            System.out.println(" - " + Numeric.toHexStringNoPrefix(validator.getEncoded(true)));
        }
        System.out.println("\n####################");
    }
}
