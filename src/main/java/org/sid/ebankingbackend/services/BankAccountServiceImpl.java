package org.sid.ebankingbackend.services;


import org.sid.ebankingbackend.dto.*;
import org.sid.ebankingbackend.entities.*;
import org.sid.ebankingbackend.enums.OperationType;
import org.sid.ebankingbackend.exception.BalanceNotSufficientException;
import org.sid.ebankingbackend.exception.BankAccountNotFoundException;
import org.sid.ebankingbackend.exception.CustomerNotFoundExpection;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.sid.ebankingbackend.mappers.BankAccountMapperImpl;
import org.sid.ebankingbackend.repositories.AccountOperationRepository;
import org.sid.ebankingbackend.repositories.BankAccountRepository;
import org.sid.ebankingbackend.repositories.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {
    /// For dependacy injection we can inject @Autowired but @Autowired is deprecated so that we can use constructore for dependacy i,jection
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private BankAccountRepository bankAccountRepository;
    @Autowired
    private AccountOperationRepository accountOperationRepository;
    @Autowired
    private BankAccountMapperImpl dtaMapper;

    Logger log = LoggerFactory.getLogger(this.getClass().getName()); //use lambok annotation >>> @XSlf4j

    public BankAccountServiceImpl(AccountOperationRepository accountOperationRepository) {
        this.accountOperationRepository = accountOperationRepository;
    }

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Saving new Customer");
        Customer customer = dtaMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);

        return dtaMapper.fromCustomer(savedCustomer);
    }

    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null); // If customer Then return NULL
        if (customer == null)
            throw new CustomerNotFoundExpection("Customer not found");
        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setCreateAt(new Date());
        currentAccount.setBalance(initialBalance);
        currentAccount.setOverDraft(overDraft);
        currentAccount.setCustomer(customer);

        CurrentAccount savedCurrentAccount = bankAccountRepository.save(currentAccount);

        return dtaMapper.fromCurrentBankAccount(savedCurrentAccount);

    }

    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws BankAccountNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new BankAccountNotFoundException("Customer not found")); // If customer Then return NULL
        if (customer == null)
            throw new CustomerNotFoundExpection("Customer not found");
        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setCreateAt(new Date());
        savingAccount.setBalance(initialBalance);
        savingAccount.setInterestRate(interestRate);
        savingAccount.setCustomer(customer);

        SavingAccount savedSavingAccount = bankAccountRepository.save(savingAccount);
        return dtaMapper.fromSavingBankAccount(savedSavingAccount);
    }


    @Override
    public List<CustomerDTO> listCustomers() {
        List<Customer> customers = customerRepository.findAll();
        List<CustomerDTO> customerDTOS = customers.stream()
                .map(customer -> dtaMapper.fromCustomer(customer))
                .collect(Collectors.toList());



      /* OR DO LIKE THIS
      List<CustomerDTO> customerDTOS= new ArrayList<>();

      for (Customer customer:customers){
        CustomerDTO customerDTO = bankAccountMapper.fromCustomer(customer);
        customerDTOS.add(customerDTO);
      }
      */
        return customerDTOS;
    }

    @Override
    public List<BankAccountDTO> bankAccountLis() {

        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        List<BankAccountDTO> bankAccountDTOS = bankAccounts.stream().map(bankAccount -> {
            if (bankAccount instanceof SavingAccount) {
                SavingAccount savingAccount = (SavingAccount) bankAccount;
                return dtaMapper.fromSavingBankAccount(savingAccount);
            } else {
                CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                return dtaMapper.fromCurrentBankAccount(currentAccount);
            }
        }).collect(Collectors.toList());
        return bankAccountDTOS;
    }

    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(() -> new BankAccountNotFoundException("BankAccount not found")); // If bankAccount Then return NULL
        if (bankAccount instanceof SavingAccount) {
            SavingAccount savingAccount = (SavingAccount) bankAccount;
            return dtaMapper.fromSavingBankAccount(savingAccount);
        } else {
            CurrentAccount currentAccount = (CurrentAccount) bankAccount;
            return dtaMapper.fromCurrentBankAccount(currentAccount);
        }

    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(() -> new BankAccountNotFoundException("BankAccount not found")); // If bankAccount Then return NULL

        if (bankAccount.getBalance() < amount) throw new BalanceNotSufficientException("Balance not sufficient ");
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setAmount(amount);
        accountOperation.setBankAccount(bankAccount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperationRepository.save(accountOperation);
        // Then Update Solde
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(() -> new BankAccountNotFoundException("BankAccount not found")); // If bankAccount Then return NULL
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setBankAccount(bankAccount);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperationRepository.save(accountOperation);
        // Then Update Solde
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfert(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException {
        debit(accountIdSource, amount, "Transfer");
        credit(accountIdDestination, amount, "Transfer from " + accountIdSource);
    }

    @Override
    public CustomerDTO getCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new CustomerNotFoundExpection("Customer Not found"));
        return dtaMapper.fromCustomer(customer);
    }

    @Override
    public CustomerDTO UpdateCustomer(CustomerDTO customerDTO) {
        log.info("Saving new Customer");
        Customer customer = dtaMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return dtaMapper.fromCustomer(savedCustomer);
    }

    @Override
    public void deleteCustomer(Long customerDTO) {
        customerRepository.deleteById(customerDTO);

    }

    @Override
    public List<AccountOperation> accountHystory(String accountId, int page, int size) {
        // List<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(accountId,page, size);
        //  return accountOperations.stream().map(op->dtaMapper.fromAccountOperation(op)).collect(Collectors.toList());
        List<AccountOperation> accountOperations = new ArrayList<>();
        return accountOperations;
    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(() ->
                new CustomerNotFoundExpection("Customer Not found"));

        Page<AccountOperation> accountOperation = accountOperationRepository.findByBankAccountIdOrderByOperationDateDesc(accountId, PageRequest.of(page, size));

        AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
        List<AccountOperationDTO> accountOperationDTOS = accountOperation.getContent().stream().map(op -> dtaMapper.fromAccountOperation(op)).collect(Collectors.toList());
        accountHistoryDTO.setAccountOperationDTOList(accountOperationDTOS);
        accountHistoryDTO.setAccountId(bankAccount.getId());
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setPageSize(page);
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setTotalPages(accountOperation.getTotalPages());

        return accountHistoryDTO;
    }

    @Override
    public List<CustomerDTO> searchCustomers(String keyword) {
        List<Customer> customers = customerRepository.searchCustomer(keyword);
        List<CustomerDTO> customerDTOS = customers.stream().map(cust -> dtaMapper.fromCustomer(cust)).collect(Collectors.toList());
        return customerDTOS;
    }

}
