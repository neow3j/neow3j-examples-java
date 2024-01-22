package io.neow3j.examples.contractdevelopment;

import static io.neow3j.examples.Constants.ALICE;
import static io.neow3j.examples.Constants.NEOW3J_PRIVATENET;
import static io.neow3j.examples.contractdevelopment.CompileAndDeploy.writeNefAndManifestFiles;
import java.util.HashMap;
import io.neow3j.compiler.CompilationUnit;
import io.neow3j.compiler.Compiler;
import io.neow3j.contract.GasToken;
import io.neow3j.contract.SmartContract;
import io.neow3j.examples.contractdevelopment.contracts.ContractWithPlaceholders;
import io.neow3j.protocol.Neow3j;
import io.neow3j.types.Hash160;

/**
 * This example compiles the contract {@link ContractWithPlaceholders}, deploys it and verifies that the placeholders
 * were substituted correctly.
 */
public class CompileAndDeployContractWithPlaceholders {

    // The neow3j instance used in this example.
    static final Neow3j neow3j = NEOW3J_PRIVATENET;

    public static void main(String[] args) throws Throwable {
        // Define all placeholder values
        HashMap<String, String> replaceMap = new HashMap<>();
        replaceMap.put("allowedTokenContractHash", GasToken.SCRIPT_HASH.toString());
        replaceMap.put("ownerName", "Alice");
        replaceMap.put("ownerAddress", ALICE.getAddress());

        // Pass the replaceMap when compiling the contract
        CompilationUnit compUnit = new Compiler().compile(ContractWithPlaceholders.class.getCanonicalName(), replaceMap);
        writeNefAndManifestFiles(compUnit); // Store Nef and manifest

        // Deploy the contract
        Hash160 contractHash = CompileAndDeploy.deployContract(neow3j, ALICE, compUnit, null);

        // Verify that the placeholder values have been substituted correctly.
        SmartContract contract = new SmartContract(contractHash, neow3j);
        String ownerName = contract.callFunctionReturningString("getOwnerName");
        Hash160 ownerScriptHash = contract.callFunctionReturningScriptHash("getOwner");
        String symbol = contract.callFunctionReturningString("getSymbolOfAllowedContract");

        System.out.printf("Expected owner name: %s\n", "Alice");
        System.out.printf("Actual owner name:   %s\n", ownerName);
        System.out.printf("Expected owner address: %s\n", ALICE.getAddress());
        System.out.printf("Actual owner address:   %s\n", ownerScriptHash.toAddress());
        System.out.printf("Expected symbol: %s\n", GasToken.SYMBOL);
        System.out.printf("Actual symbol:   %s\n", symbol);
    }

}
