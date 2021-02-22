package io.neow3j.examples.contractdev;

import io.neow3j.compiler.CompilationUnit;
import io.neow3j.compiler.Compiler;
import io.neow3j.contract.ContractManagement;
import io.neow3j.contract.ContractUtils;
import io.neow3j.contract.ScriptHash;
import io.neow3j.contract.SmartContract;
import io.neow3j.examples.contractdev.contracts.BongoCatToken;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.methods.response.NeoSendRawTransaction;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.transaction.Signer;
import io.neow3j.wallet.Account;
import io.neow3j.wallet.Wallet;
import java.nio.file.Path;
import java.nio.file.Paths;

// Shows how a smart contract can be compiled programmatically and then deployed on a local
// Neo blockchain.
public class CompileAndDeploy {

    // 43be3eea1aaf7973df08f2ee289bf1930c5963f4
    // NiCB5NXvaAqJfsZKXNdi3yenRcYJKezjwU
    public static void main(String[] args) throws Throwable {

        // Set up the connection to the neo-node
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:40332"));

        // Setup an account and wallet for signing the transaction. Make sure that the account has a
        // sufficient GAS balance to pay for the deployment.
        Account a = Account.fromWIF("L3kCZj6QbFPwbsVhxnB8nUERDy4mhCSrWJew4u5Qh5QmGMfnCTda");
        Wallet w = Wallet.withAccounts(a);

        // Compile the BongotCatToken contract and construct a SmartContract object from it.
        CompilationUnit res = new Compiler()
                .compileClass(BongoCatToken.class.getCanonicalName());

        // Write contract (compiled, NEF) to the disk
        Path buildNeow3jPath = Paths.get("build", "neow3j");
        buildNeow3jPath.toFile().mkdirs();
        ContractUtils.writeNefFile(res.getNefFile(), res.getManifest().getName(), buildNeow3jPath);

        // Write manifest to the disk
        ContractUtils.writeContractManifestFile(res.getManifest(), buildNeow3jPath);

        // Deploy the contract's NEF and manifest. This creates, signs and send a transaction to
        // the neo-node.
        NeoSendRawTransaction response = new ContractManagement(neow3j)
                .deploy(res.getNefFile(), res.getManifest())
                .signers(Signer.global(a.getScriptHash()))
                .wallet(w)
                .sign()
                .send();

        if (response.hasError()) {
            System.out.printf("Deployment was not successful. Error message from neo-node was: "
                    + "'%s'\n", response.getError().getMessage());
            return;
        }
        ScriptHash contractHash = SmartContract.getContractHash(
                a.getScriptHash(), res.getNefFile().getCheckSumAsInteger(),
                res.getManifest().getName());
        System.out.println("Script hash of the deployed contract: " + contractHash);
        System.out.println("Contract Address: " + contractHash.toAddress());
    }
}
