/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grokonez.jwtauthentication.message.request;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


/**
 *
 * @author smile
 */
public class PaymentGatewayRequest {
    
    @NotBlank
    @Min(value = 8, message = "Account number should not be less than 8")
    private String merchantAccountNo;
    
    @NotBlank
    @Size(min=3, max = 60)
    private String firstName;
    @NotBlank
    @Size(min=3, max = 60)
    private String lastName;
    
    @NotBlank(message = "CCV Should not be blank")
    @Min(value = 3, message = "CVV should not be less than 16")
    private String cvv;
    
    /*@NotBlank(message = "Month Should not be blank")
    @Min(value = 2, message = "Month should not be less than 02 Characters")
    private String month;
    
    @NotBlank(message = "Year Should not be blank")
    @Min(value = 4, message = "Year should not be less than 04 Characters")
    private String year;*/
    @NotBlank(message = "Expiry Date Should not be blank")
    private String expiryDate;
            
    @Min(value = 16, message = "Credit card number should not be less than 16")
    private String ccNumber;
    
   
    @Digits (integer = 999, fraction = 2) 
    private double amount; 
    
    
    @NotBlank
    private String transactionDesc;


    public String getMerchantAccountNo() {
        return merchantAccountNo;
    }

    public void setMerchantAccountNo(String merchantAccountNo) {
        this.merchantAccountNo = merchantAccountNo;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    /*public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }*/

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
    
   

    public String getCcNumber() {
        return ccNumber;
    }

    public void setCcNumber(String ccNumber) {
        this.ccNumber = ccNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTransactionDesc() {
        return transactionDesc;
    }

    public void setTransactionDesc(String transactionDesc) {
        this.transactionDesc = transactionDesc;
    }
}
