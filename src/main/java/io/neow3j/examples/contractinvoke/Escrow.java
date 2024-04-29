package io.neow3j.examples.contractinvoke;

import io.neow3j.contract.NeoToken;
import io.neow3j.contract.SmartContract;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.response.InvocationResult;
import io.neow3j.protocol.core.response.NeoSendRawTransaction;
import io.neow3j.protocol.core.stackitem.StackItem;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.types.ContractParameter;
import io.neow3j.types.Hash160;
import io.neow3j.wallet.Account;
import io.neow3j.transaction.AccountSigner;

import java.util.Scanner;
import java.util.Arrays;
import java.util.List;
import java.math.BigInteger;

public class Escrow {
        public static void main(String[] args) throws Throwable {

                Account account = Account.fromWIF("KzrHihgvHGpF9urkSbrbRcgrxSuVhpDWkSfWvSg97pJ5YgbdHKCQ");

                // Transactions in the Trust contract refer to an "Agreement" between a trustor
                // and beneficiary/beneficiaries with conditions set by the trustor and
                // functions to process this agreement such as creation, updation, revocation
                // and withdrawal from the Trust.

                // Intial setup for the Trust contract
                // 1. Instantiate a connection to the locally running Neo node
                Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:50012"));

                // 2. Create a script hash of your locally deployed Escrow contract
                Hash160 scriptHash = new Hash160("87f7e931e45562a10ea80004026e1e6418d40bf8");

                // Initializing the NEO token
                NeoToken neo = new NeoToken(neow3j);

                // Function calls
                createTransaction(account, scriptHash, neow3j);
                getTransaction(account, scriptHash, neow3j);
                transferFunds(account, scriptHash, neo, neow3j);
        }

        private static void createTransaction(Account account, Hash160 scriptHash, Neow3j neow3j) throws Throwable {
                // Initialize the total amount with a sample value
                BigInteger totalAmount = new BigInteger("0");

                // 3. Instantiate a scanner to get user input
                Scanner scanner = new Scanner(System.in);

                // 4. Get all the information we'd need to create the agreement on-chain
                // 4.1. Get the name of the transaction from user input
                System.out.println("Enter the name of the transaction:");
                String transactionName = scanner.nextLine();

                // 4.2. Get the beneficiary address from user input
                System.out.println("Enter the beneficiary address:");
                String beneficiaryAddress = scanner.nextLine();

                // 4.3.Get the total amount to be sent to be held in escrow from user input
                System.out.println("Enter the total amount to be held in escrow:");
                totalAmount = scanner.nextBigInteger();

                // 4.4. Get a boolean input from the user to determine if the transaction is
                // revocable
                System.out.println("Is the transaction revocable? (true/false)");
                boolean isRevocable = scanner.nextBoolean();

                // 5. Close the scanner
                scanner.close();

                // 6. Construct the parameters using ContractParameter
                ContractParameter trustorParameter = ContractParameter.hash160(account.getScriptHash());
                ContractParameter transactionNameParameter = ContractParameter.string(transactionName);
                ContractParameter beneficiaryAddressParameter = ContractParameter.string(beneficiaryAddress);
                ContractParameter totalAmountParameter = ContractParameter.integer(totalAmount);
                ContractParameter isRevocableParameter = ContractParameter.bool(isRevocable);

                // 7. Call the createTransaction function in the Escrow contract
                NeoSendRawTransaction createTransactionResult = new SmartContract(scriptHash,
                                neow3j)
                                .invokeFunction("createTransaction", trustorParameter,
                                                transactionNameParameter,
                                                beneficiaryAddressParameter, totalAmountParameter, isRevocableParameter)
                                .signers(AccountSigner.calledByEntry(account))
                                .sign()
                                .send();

                System.out.println("createTransactionResult Txn Hash --> " +
                                createTransactionResult.getSendRawTransaction().getHash());
        }

        private static void getTransaction(Account account, Hash160 scriptHash, Neow3j neow3j) throws Throwable {
                // Read from contract storage
                ContractParameter accountParameter = ContractParameter.hash160(account.getScriptHash());

                InvocationResult getCreateTransactionResult = new SmartContract(scriptHash, neow3j)
                                .callInvokeFunction("getTransaction", Arrays.asList(accountParameter))
                                .getInvocationResult();

                StackItem result = getCreateTransactionResult.getFirstStackItem();

                List<StackItem> resutsAsStackItem = result.getList();

                String trustor = resutsAsStackItem.get(0).getAddress();
                System.out.print("Trustor: " + trustor + "\n");

                String transactionName = resutsAsStackItem.get(1).getString();
                System.out.print("Transaction Name: " + transactionName + "\n");

                String benificiary = resutsAsStackItem.get(2).getString();

                System.out.print("Beneficiary: " + benificiary + "\n");

                BigInteger totalAmount = resutsAsStackItem.get(3).getInteger();
                System.out.print("Total Amount: " + totalAmount + "\n");

                Boolean isRevocable = resutsAsStackItem.get(4).getBoolean();

                System.out.print("isRevocable: " + isRevocable + "\n");
        }

        // Function to transfer funds from trust to beneficiary
        private static void transferFunds(Account account, Hash160 scriptHash, NeoToken neo, Neow3j neow3j)
                        throws Throwable {
                // Read from contract storage and get the amount
                ContractParameter accountParameter = ContractParameter.hash160(account.getScriptHash());

                InvocationResult getCreateTransactionResult = new SmartContract(scriptHash, neow3j)
                                .callInvokeFunction("getTransaction", Arrays.asList(accountParameter))
                                .getInvocationResult();

                StackItem result = getCreateTransactionResult.getFirstStackItem();

                List<StackItem> resutsAsStackItem = result.getList();

                BigInteger totalAmount = resutsAsStackItem.get(3).getInteger();
                System.out.print("Total Amount: " + totalAmount + "\n");

                // Transfer the assets from the trust to the beneficiary
                neo.transfer(account, new Hash160(resutsAsStackItem.get(2).getString()), totalAmount);
        }

}
