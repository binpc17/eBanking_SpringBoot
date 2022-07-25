package org.sid.ebankingbackend.web;

import org.sid.ebankingbackend.dto.CustomerDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.sid.ebankingbackend.services.BankAccountService;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@CrossOrigin("*")
public class CustomerRestController {
    BankAccountService bankAccountService;
@GetMapping("/customers")
    public List<CustomerDTO> customer(){
        return bankAccountService.listCustomers();
    }

    @GetMapping("/customers/search")
    public List<CustomerDTO> searchCustomer(@RequestParam(name = "keyword",defaultValue = "") String keyword){

    return bankAccountService.searchCustomers("%"+keyword+"%");
    }

    @GetMapping("/customers/{id}")
    public CustomerDTO getCustomer(@PathVariable(name = "id") Long customerId){

    return bankAccountService.getCustomer(customerId);
    }

    @PostMapping("/customers")
    public  CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO){
      return  bankAccountService.saveCustomer(customerDTO);
    }

    @PutMapping("/customers/{customerId}")
    public CustomerDTO updateCustomer(@PathVariable(name = "customerId") Long customerId, @RequestBody CustomerDTO customerDTO){
        customerDTO.setId(customerId);
        return bankAccountService.UpdateCustomer(customerDTO);
    }

    @DeleteMapping("/customers/{id}")
    public void deleteCustomer(@PathVariable(name = "id") Long customerId){
        bankAccountService.deleteCustomer(customerId);
    }
}
