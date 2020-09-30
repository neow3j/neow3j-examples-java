package io.neow3j.examples.contract_development;

import io.neow3j.compiler.Compiler;
import io.neow3j.compiler.Compiler.CompilationResult;
import io.neow3j.contract.SmartContract;
import io.neow3j.examples.contract_development.contracts.BongoCatToken;
import io.neow3j.model.NeoConfig;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.methods.response.NeoSendRawTransaction;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.transaction.Signer;
import io.neow3j.wallet.Account;
import io.neow3j.wallet.Wallet;

// Shows how a smart contract can be compiled programmatically and then deployed on a local
// Neo blockchain.
public class CompileAndDeploy {

    public static void main(String[] args) throws Throwable {

        // Set the magic number according to the Neo network's configuration. It is used when
        // signing transactions.
        NeoConfig.setMagicNumber(new byte[]{0x01, 0x03, 0x00, 0x0}); // Magic number 769

        // Set up the connection to the neo-node and the wallet for signing the transaction.
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:40332"));
        Account a = Account.fromWIF("L1WMhxazScMhUrdv34JqQb1HFSQmWeN2Kpc1R9JGKwL7CDNP21uR");
        Wallet w = Wallet.withAccounts(a);

        // Compile the BongotCatToken contract and construct a SmartContract object from it.
        CompilationResult res = new Compiler().compileClass(BongoCatToken.class.getCanonicalName());
        SmartContract sc = new SmartContract(res.getNef(), res.getManifest(), neow3j);

        // Deploy the contract's NEF and manifest. This creates, signs and send a transaction to
        // the neo-node.
        NeoSendRawTransaction response = sc.deploy()
                .wallet(w)
                .signers(Signer.calledByEntry(a.getScriptHash()))
                .sign()
                .send();

        if (response.hasError()) {
            System.out.printf("Deployment was not successful. Error message from neo-node "
                    + "was: '%s'", response.getError().getMessage());
        }
        System.out.println("Script hash of the deployd contract: " + sc.getScriptHash());
    }

}
