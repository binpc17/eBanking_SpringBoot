package org.sid.ebankingbackend.services;

import org.sid.ebankingbackend.dto.*;
import org.sid.ebankingbackend.entities.AccountOperation;
import org.sid.ebankingbackend.exception.BalanceNotSufficientException;
import org.sid.ebankingbackend.exception.BankAccountNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankAccountService {
    CustomerDTO saveCustomer(CustomerDTO customerDTO);

    CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId);

    SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws BankAccountNotFoundException;

    List<CustomerDTO> listCustomers();

    List<BankAccountDTO> bankAccountLis();

    BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException;

    void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException;

    void credit(String accountId, double amount, String description) throws BankAccountNotFoundException;

    void transfert(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException;

    CustomerDTO getCustomer(Long customerId);

    CustomerDTO UpdateCustomer(CustomerDTO customerDTO);

    void deleteCustomer(Long customerDTO);

    //List<AccountOperationDTO> accountHystory(String accountId);

    List<AccountOperation> accountHystory(String accountId, int page, int size);

    AccountHistoryDTO getAccountHistory(String accountId, int page, int size);

    List<CustomerDTO> searchCustomers(String keyword);
}
