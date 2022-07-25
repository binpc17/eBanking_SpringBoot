package org.sid.ebankingbackend.web;

import org.sid.ebankingbackend.dto.*;
import org.sid.ebankingbackend.exception.BalanceNotSufficientException;
import org.sid.ebankingbackend.exception.BankAccountNotFoundException;
import org.sid.ebankingbackend.services.BankAccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
public class BankAccountRestAPI {
    BankAccountService bankAccountService;

    public BankAccountRestAPI(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping("/accounts/{accountId}")
    public BankAccountDTO getBankAccount(@PathVariable String accountId) throws BankAccountNotFoundException {
    return bankAccountService.getBankAccount(accountId);
    }

    @GetMapping("/accounts")
    public List<BankAccountDTO> listAccounts() {
        return bankAccountService.bankAccountLis();

    }

    @GetMapping("/accounts/{accountId}/operations")
    public List getHitory(@PathVariable String accountId) {
        int page=0; int size=20;
        return bankAccountService.accountHystory(accountId,page,size);

    }

    @GetMapping("/accounts/{accountId}/pageOperations")
    public AccountHistoryDTO getAccountHitory(@PathVariable String accountId, @RequestParam(name = "page",defaultValue = "0") int page, @RequestParam(name = "size",defaultValue = "5") int size) {

        return bankAccountService.getAccountHistory(accountId, page, size);

    }
    @PostMapping("/accounts/debit")
    public DebitDTO debit(@RequestBody DebitDTO debitDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        System.out.println("Controller DEBIT..");
         bankAccountService.debit(debitDTO.getAccountId(),debitDTO.getAmount() , debitDTO.getDescription());
        return debitDTO;
    }
    @PostMapping("/accounts/credit")
    public CreditDTO credit(@RequestBody CreditDTO creditDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        bankAccountService.credit(creditDTO.getAccountId(),creditDTO.getAmount() , creditDTO.getDescription());
        return creditDTO;
    }

    @PostMapping("/accounts/transfert")
    public void transfert(@RequestBody TransfertRequestDTO transfertRequestDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        bankAccountService.transfert(transfertRequestDTO.getAccountSource(),transfertRequestDTO.getAccountDestination() ,transfertRequestDTO.getAmount());
    }
}
