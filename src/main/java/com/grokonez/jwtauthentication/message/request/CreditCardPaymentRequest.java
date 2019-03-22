/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grokonez.jwtauthentication.message.request;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 *
 * @author smile
 */
public class CreditCardPaymentRequest {
    @NotBlank
    @Size(min=3, max = 60)
    private String senderaccountno;
    
    @NotBlank
    @Size(min=3, max = 60)
    private String creditcardno;
    @Digits (integer = 999, fraction = 2) 
    private double amount; 

    public String getSenderaccountno() {
        return senderaccountno;
    }

    public void setSenderaccountno(String senderaccountno) {
        this.senderaccountno = senderaccountno;
    }

    public String getCreditcardno() {
        return creditcardno;
    }

    public void setCreditcardno(String creditcardno) {
        this.creditcardno = creditcardno;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    
    
    
    
}
