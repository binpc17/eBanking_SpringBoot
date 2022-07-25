package org.sid.ebankingbackend.exception;

public class CustomerNotFoundExpection extends RuntimeException {
    public CustomerNotFoundExpection(String message){
        super(message);
    }
}
