package org.sid.ebankingbackend;

import org.sid.ebankingbackend.dto.BankAccountDTO;
import org.sid.ebankingbackend.dto.CurrentBankAccountDTO;
import org.sid.ebankingbackend.dto.CustomerDTO;
import org.sid.ebankingbackend.dto.SavingBankAccountDTO;
import org.sid.ebankingbackend.exception.CustomerNotFoundExpection;
import org.sid.ebankingbackend.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.stream.Stream;

@SpringBootApplication
public class EBankingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EBankingBackendApplication.class, args);
    }
// Test Something on server starting
    @Bean
    CommandLineRunner start(BankAccountService bankAccountService) {

        return args -> {
            Stream.of("Hassan", "Yassine", "Aicha").forEach(name -> {
                System.out.println(">>>" + name);
                CustomerDTO customer = new CustomerDTO();
                customer.setName(name);
                customer.setEmail(name + "@gmail.com");
                bankAccountService.saveCustomer(customer);
            });

            bankAccountService.listCustomers().forEach(customer -> {
                try {
                    bankAccountService.saveCurrentBankAccount(Math.random() * 9000, 9000, customer.getId());
                    bankAccountService.saveCurrentBankAccount(Math.random() * 9000, 9000, customer.getId());


                } catch (CustomerNotFoundExpection e) {
                    e.printStackTrace();
                }
            });

            // add fund AND remove fund
            try {
                List<BankAccountDTO> bankAccountList = bankAccountService.bankAccountLis();
                for (BankAccountDTO bankAccount : bankAccountList) {
                    String accountId;
                    if (bankAccount instanceof SavingBankAccountDTO) {
                        accountId = ((SavingBankAccountDTO) bankAccount).getId();
                    } else {
                        accountId = ((CurrentBankAccountDTO) bankAccount).getId();
                    }
                    System.out.println(">ID>>" + accountId);
                    for (int i = 0; i < 10; i++) {

                        bankAccountService.credit(accountId, 10000 + Math.random() * 12000, "Credit");
                        bankAccountService.debit(accountId, 1000, "Debit");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        };
    }

/*
@Bean
CommandLineRunner start(CustomerRepository customerRepository,
                            BankAccountRepository bankAccountRepository,
                            AccountOperationRepository accountOperationRepository){

        return args ->{
            Stream.of("Hassan","Yassine","Aicha").forEach(name->{
                System.out.println(">>>"+name);
                Customer customer = new Customer();
                customer.setName(name);
                customer.setEmail(name+"@gmail.com");
                customerRepository.save(customer);
            });
        };
    }
    */
}
