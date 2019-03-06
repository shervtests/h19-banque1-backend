/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grokonez.jwtauthentication.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 *
 * @author smile
 */
@Entity       
public class UserCreditCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id ;
    private String creditcardno;
    @JsonIgnore
    private String cvv;
    @JsonIgnore
    private String expiryDate;
    private int amountavailable;
    private int amountowned;
    
    @JsonIgnoreProperties("userCreditCard")
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    
    public UserCreditCard() {
    }

    public UserCreditCard(String creditcardno, String cvv, String expiryDate, int amountavailable, int amountowned) {  
        this.creditcardno = creditcardno;
        this.cvv = cvv;
        this.expiryDate = expiryDate;
        this.amountavailable = amountavailable;
        this.amountowned = amountowned;
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

    public int getAmountavailable() {
        return amountavailable;
    }

    public void setAmountavailable(int amountavailable) {
        this.amountavailable = amountavailable;
    }

    public int getAmountowned() {
        return amountowned;
    }

    public void setAmountowned(int amountowned) {
        this.amountowned = amountowned;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    
    
      
}
