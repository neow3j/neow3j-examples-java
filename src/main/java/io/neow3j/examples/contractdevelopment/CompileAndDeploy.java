package io.neow3j.examples.contractdevelopment;

import static io.neow3j.contract.ContractUtils.writeContractManifestFile;
import static io.neow3j.contract.ContractUtils.writeNefFile;
import static io.neow3j.examples.Constants.ALICE;
import static io.neow3j.examples.Constants.NEOW3J_PRIVATENET;
import static java.lang.String.format;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import io.neow3j.compiler.CompilationUnit;
import io.neow3j.compiler.Compiler;
import io.neow3j.contract.ContractManagement;
import io.neow3j.contract.SmartContract;
import io.neow3j.examples.contractdevelopment.contracts.FungibleToken;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.response.NeoSendRawTransaction;
import io.neow3j.transaction.AccountSigner;
import io.neow3j.types.ContractParameter;
import io.neow3j.types.Hash160;
import io.neow3j.utils.Await;
import io.neow3j.wallet.Account;

/**
 * Shows how a smart contract can be compiled programmatically and then deployed on a local Neo blockchain.
 */
public class CompileAndDeploy {

    // The neow3j instance used in this example.
    static final Neow3j neow3j = NEOW3J_PRIVATENET;

    public static void main(String[] args) throws Throwable {
        CompilationUnit compUnit = compile(FungibleToken.class);
        // Parameter that is passed to the contract's "onDeployment" method. In this case this means that Alice will be
        // the owner of the contract.
        ContractParameter deploymentParam = ContractParameter.hash160(ALICE.getScriptHash());
        deployContract(neow3j, ALICE, compUnit, deploymentParam);
    }

    /**
     * Compiles the contractClass and writes the resulting Nef and manifest to files.
     */
    public static CompilationUnit compile(Class<?> contractClass) throws IOException {
        CompilationUnit compUnit = new Compiler().compile(contractClass.getCanonicalName());
        writeNefAndManifestFiles(compUnit);
        return compUnit;
    }

    /**
     * Deploys a compiled contract with the provided deployer account. Passes the 
     * 
     * @param neow3j The neow3j instance used to send the deployment transaction.
     * @param deployerAccount The account that deploys the contract, i.e., pays for it.
     * @param compUnit The compilation unit of the contract to deploy.
     * @param deploymentParam The parameter that is passed to the contract's {@code onDeployment} method.
     * @return the script hash of the deployed contract.
     */
    public static Hash160 deployContract(Neow3j neow3j, Account deployerAccount, CompilationUnit compUnit, 
        ContractParameter deploymentParam) throws Throwable {

        // Deploy the contract's NEF and manifest. This creates, signs and send a transaction to the Neo node.
        NeoSendRawTransaction response = new ContractManagement(neow3j)
                .deploy(compUnit.getNefFile(), compUnit.getManifest(), deploymentParam)
                .signers(AccountSigner.none(deployerAccount))
                .sign()
                .send();

        if (response.hasError()) {
            throw new Exception(format("Deployment was not successful. Error message from neo-node was: '%s'\n",
                    response.getError().getMessage()));
        } else {
            Await.waitUntilTransactionIsExecuted(response.getResult().getHash(), neow3j);
            Hash160 contractHash = SmartContract.calcContractHash(
                    ALICE.getScriptHash(),
                    compUnit.getNefFile().getCheckSumAsInteger(),
                    compUnit.getManifest().getName());
            System.out.println("Script hash of the deployed contract: " + contractHash);
            return contractHash;
        }
    }

    /**
     * Writes the Nef and manifest to files. You can find the files in your project root under {@code /build/neow3j}.
     */
    public static void writeNefAndManifestFiles(CompilationUnit compUnit) throws IOException {
        // Write contract (compiled, NEF) to the disk
        Path buildNeow3jPath = Paths.get("build", "neow3j");
        buildNeow3jPath.toFile().mkdirs();
        writeNefFile(compUnit.getNefFile(), compUnit.getManifest().getName(), buildNeow3jPath);

        // Write manifest to the disk
        writeContractManifestFile(compUnit.getManifest(), buildNeow3jPath);
    }

}
