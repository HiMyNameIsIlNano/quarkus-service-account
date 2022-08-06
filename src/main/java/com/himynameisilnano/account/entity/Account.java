package com.himynameisilnano.account.entity;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import java.math.BigDecimal;
import java.util.Objects;

public class Account {

    private Long accountNumber;

    private Long customerNumber;

    private String customerName;

    private BigDecimal balance;

    private AccountStatus accountStatus;

    public Account() {
    }

    @JsonbCreator
    public Account(@JsonbProperty("accountNumber") Long accountNumber,
                   @JsonbProperty("customerNumber") Long customerNumber,
                   @JsonbProperty("customerName") String customerName,
                   @JsonbProperty("balance") BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.customerNumber = customerNumber;
        this.customerName = customerName;
        this.balance = balance;
        this.accountStatus = AccountStatus.OPEN;
    }

    public void makeOverdrawn() {
        this.accountStatus = AccountStatus.OVERDRAWN;
    }

    public void removeOverdrawnStatus() {
        this.accountStatus = AccountStatus.OPEN;
    }

    public void close() {
        this.accountStatus = AccountStatus.CLOSE;
    }

    public void withdrawFunds(BigDecimal amount) {
        balance = balance.subtract(amount);
    }

    public void addFunds(BigDecimal amount) {
        balance = balance.add(amount);
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public Long getCustomerNumber() {
        return customerNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(accountNumber, account.accountNumber) &&
                Objects.equals(customerNumber, account.customerNumber) &&
                Objects.equals(customerName, account.customerName) &&
                Objects.equals(balance, account.balance) &&
                accountStatus == account.accountStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber, customerNumber, customerName, balance, accountStatus);
    }
}
