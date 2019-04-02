/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grokonez.jwtauthentication.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Digits;

/**
 *
 * @author smile
 */
@Entity     
 //@JsonIgnoreProperties({"transactions","user"})
public class UserCreditCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id ;
    private String creditcardno;
    @JsonIgnore
    private String cvv;
    @JsonIgnore
    private String expiryDate;
    @Digits (integer = 999, fraction = 2) 
    private double amountavailable = 0.00;
    @Digits (integer = 999, fraction = 2) 
    private double amountowned = 0.00;
    @Column(nullable = true)
    @Digits (integer = 999, fraction = 2)
    private double creditLimit = 0.00;
    
    @JsonIgnoreProperties("userCreditCard")
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToMany(fetch = FetchType.LAZY,
            cascade =  CascadeType.ALL,  mappedBy = "userCreditcard" )
      @JsonIgnoreProperties("userCreditcard")
    private Set<Transactions> transactions;
    
    public UserCreditCard() {
    }

    public UserCreditCard(String creditcardno, String cvv, String expiryDate, double amountavailable, double amountowned, double creditlimit) {
        this.creditcardno = creditcardno;
        this.cvv = cvv;
        this.expiryDate = expiryDate;
        this.amountavailable = amountavailable;
        this.amountowned = amountowned;
        this.creditLimit = creditLimit;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long Id) {
        this.Id = Id;
    }

    public String getCreditcardno() {
        return creditcardno;
    }

    public void setCreditcardno(String creditcardno) {
        this.creditcardno = creditcardno;
    }
    
    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public double getAmountavailable() {
        return amountavailable;
    }

    public void setAmountavailable(double amountavailable) {
        this.amountavailable = amountavailable;
    }

    public double getAmountowned() {
        return amountowned;
    }

    public void setAmountowned(double amountowned) {
        this.amountowned = amountowned;
    }

    public double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Transactions> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<Transactions> transactions) {
        this.transactions = transactions;
    }
    
    
    
      
}
