package org.sid.ebankingbackend.repositories;

import org.sid.ebankingbackend.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
    //List<Customer> findByNameContains(String keyword);
    // SOme time findByNameContains does not Work and the best way is to write your own query like this
    @Query("SELECT C FROM Customer C WHERE C.name LIKE :kw")
    List<Customer> searchCustomer(@Param("kw") String keyword);
}
