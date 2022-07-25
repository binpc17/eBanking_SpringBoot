package org.sid.ebankingbackend.repositories;

import org.sid.ebankingbackend.entities.AccountOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface AccountOperationRepository extends JpaRepository<AccountOperation, Long> {
    //public List<AccountOperation> findByBankAccountId(String accountId, int page, int size);
    //Methode that do pagination
    public Page<AccountOperation> findByBankAccountIdOrderByOperationDateDesc(String accountId, Pageable pageable);
    // Page<AccountOperation> findByBankAccountIdPage(String accountId, int page, int size);

    List<AccountOperation> findByBankAccountId(String accountId);
}
