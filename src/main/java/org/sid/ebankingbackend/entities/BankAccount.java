package org.sid.ebankingbackend.entities;

import org.sid.ebankingbackend.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", length = 4)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {
    @Id
    private String id;
    private double balance;
    private Date createAt;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    @ManyToOne
    private Customer customer;
    @OneToMany(mappedBy = "bankAccount", fetch = FetchType.LAZY)
    private List<AccountOperation> accountOperations;
}
/*
// Mapping Heritage we have 3 Trategies
>1. Singne Table
>2. Table per Class
 >3. Joinned Table
 // He have decided to choose Strategie >> Singne Table
 AND @DiscriminatorColumn with the colomne name TYPE >> will help us to make diferance be tween children classe
*/
