package io.neow3j.examples.contractdev;

import io.neow3j.compiler.CompilationUnit;
import io.neow3j.compiler.Compiler;
import io.neow3j.contract.SmartContract;
import io.neow3j.examples.contractdev.contracts.BongoCatToken;
import io.neow3j.model.NeoConfig;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.methods.response.NeoSendRawTransaction;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.transaction.Signer;
import io.neow3j.wallet.Account;
import io.neow3j.wallet.Wallet;
import io.neow3j.contract.ManagementContract;
import io.neow3j.contract.ScriptHash;

// Shows how a smart contract can be compiled programmatically and then deployed on a local
// Neo blockchain.
public class CompileAndDeploy {

    public static void main(String[] args) throws Throwable {

        // Set the magic number according to the Neo network's configuration. It is used when
        // signing transactions.
        NeoConfig.setMagicNumber(new byte[]{0x01, 0x03, 0x00, 0x0}); // Magic number 769

        // Set up the connection to the neo-node
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:40332"));
        // Setup an account and wallet for signing the transaction. Make sure that the account has a
        // sufficient GAS balance to pay for the deployment.
        Account a = Account.fromWIF("L3kCZj6QbFPwbsVhxnB8nUERDy4mhCSrWJew4u5Qh5QmGMfnCTda");
        Wallet w = Wallet.withAccounts(a);

        // Compile the BongotCatToken contract and construct a SmartContract object from it.
        CompilationUnit res = new Compiler().compileClass(BongoCatToken.class.getCanonicalName());
        // Deploy the contract's NEF and manifest. This creates, signs and send a transaction to
        // the neo-node.
        NeoSendRawTransaction response = new ManagementContract(neow3j).deploy(res.getNefFile(), res.getManifest())
                .wallet(w)
                .signers(Signer.calledByEntry(a.getScriptHash()))
                .sign()
                .send();

        if (response.hasError()) {
            System.out.printf("Deployment was not successful. Error message from neo-node was: "
                    + "'%s'\n", response.getError().getMessage());
            return;
        }
        ScriptHash contractHash = SmartContract.getContractHash(
            a.getScriptHash(), res.getNefFile().getScript());
        System.out.printf("Script hash of the deployd contract: %s\n", contractHash);
    }
}
