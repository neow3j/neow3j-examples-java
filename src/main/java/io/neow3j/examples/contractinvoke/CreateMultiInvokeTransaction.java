package io.neow3j.examples.contractinvoke;

import io.neow3j.contract.SmartContract;
import io.neow3j.protocol.core.response.NeoSendRawTransaction;
import io.neow3j.transaction.AccountSigner;
import io.neow3j.transaction.Transaction;
import io.neow3j.transaction.TransactionBuilder;
import io.neow3j.types.ContractParameter;
import io.neow3j.types.Hash160;

import java.util.ArrayList;
import java.util.HashMap;

import static io.neow3j.examples.Constants.ALICE;
import static io.neow3j.examples.Constants.NEOW3J;
import static io.neow3j.examples.Utils.trackSentTransaction;

/**
 * This example shows how to create a transaction that contains multiple contract invocations.
 */
public class CreateMultiInvokeTransaction {

    static final String MINT = "mint";
    static final String NAME = "name";
    static final String TOKEN_URI = "tokenURI";

    static final Hash160 OWNER_1 = new Hash160("f0b7455dc3413f70095cc06e7b671aaaaada8961");
    static final Hash160 OWNER_2 = new Hash160("4a207f225f1e3672cb6f57ace0de1a3da63d8e80");
    static final Hash160 OWNER_3 = new Hash160("ceeb02bf7f1289c574343ee1fd3d5aeb002b2405");

    static final String URI_1 = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSgoG6bMrfO7Jj25o4dP-WR2H_DCTRGFnlBLQ&usqp=CAU";
    static final String URI_2 = "https://neow3j.io/images/neow3j-neo3.png";
    static final String URI_3 = "https://neo-playground.dev/assets/images/horz-logo4x-2156x342.png";

    public static void main(String[] args) throws Throwable {
        // Before running this example, deploy the NonFungibleToken contract using the CompileAndDeploy example and
        // set the returned contract hash here accordingly.
        Hash160 contractHash = new Hash160("c65517c1ee197221fb482f0a5c5b09cd7732b722");
        SmartContract contract = new SmartContract(contractHash, NEOW3J);

        ArrayList<Hash160> ownerList = new ArrayList<>();
        ownerList.add(OWNER_1);
        ownerList.add(OWNER_2);
        ownerList.add(OWNER_3);

        ArrayList<String> tokenURIList = new ArrayList<>();
        tokenURIList.add(URI_1);
        tokenURIList.add(URI_2);
        tokenURIList.add(URI_3);

        // Create the transaction builder.
        TransactionBuilder transactionBuilder = new TransactionBuilder(NEOW3J);

        // Create mint scripts and add them to the transaction builder.
        HashMap<String, String> propertiesMap = new HashMap<>();
        for (int i = 0; i <= 2; i++) {
            propertiesMap.put(NAME, "awesomeToken #" + i);
            propertiesMap.put(TOKEN_URI, tokenURIList.get(i));

            // Build the script for the current id.
            byte[] invokeScript = contract.buildInvokeFunctionScript(MINT,
                    ContractParameter.hash160(ownerList.get(i)),
                    ContractParameter.integer(i),
                    ContractParameter.map(propertiesMap));

            // Add the script to the transaction builder.
            transactionBuilder.extendScript(invokeScript);
        }

        // Set the account ALICE as the signer, since it is the owner of the contract and sign the transaction.
        Transaction tx = transactionBuilder.signers(AccountSigner.calledByEntry(ALICE))
                .sign();

        NeoSendRawTransaction response = tx.send();
        trackSentTransaction(response);
    }

}
