package org.sid.ebankingbackend.dto;

import org.sid.ebankingbackend.enums.AccountStatus;
import lombok.Data;

import java.util.Date;

@Data
public class CurrentBankAccountDTO extends BankAccountDTO {
    private String id;
    private double balance;
    private Date createAt;
    private AccountStatus status;
    private CustomerDTO customerDTO;
    private double overDraft;
}

