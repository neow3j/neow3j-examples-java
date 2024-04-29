package io.neow3j.examples.contractdevelopment.contracts;

import io.neow3j.devpack.Hash160;

public class Transaction {
    public Hash160 trustor;
    public String transactionName;
    public String beneficiary;
    public Integer totalAmount;
    public boolean isRevocable;

    public Transaction(Hash160 account, String transactionName, String beneficiaryAddress, Integer totalAmount,
            Boolean isRevocable) {
        this.trustor = account;
        this.transactionName = transactionName;
        this.beneficiary = beneficiaryAddress;
        this.totalAmount = totalAmount;
        this.isRevocable = isRevocable;
    }
}
