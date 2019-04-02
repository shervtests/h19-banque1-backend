/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grokonez.jwtauthentication.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.print.attribute.standard.DateTimeAtCompleted;
import javax.validation.constraints.Digits;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

/**
 *
 * @author smile
 */
@Entity  
public class Transactions {
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 60)
    private TransType Transtype;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 60)
    private TransStatus Transstatus;
    
    
    
    private String Description;
     @Digits (integer = 999, fraction = 2) 
    private double credit;
      @Digits (integer = 999, fraction = 2) 
    private double debit  ;
       @Digits (integer = 999, fraction = 2) 
    private double currently_available_funds;
    
    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date transdate ;
    
    @Column(nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updatedAt;

    
    @PreUpdate
    protected void onUpdate() {
    updatedAt = new Date();
    }
    
    @PrePersist
    protected void onCreate() {
     transdate = new Date();
    }
    
    @JsonIgnoreProperties("transactions")
    @ManyToOne
    @JoinColumn(name = "useraccount_id", nullable = true)
    private UserAccount userAccount;

    @JsonIgnoreProperties("transactions")
    @ManyToOne
    @JoinColumn(name = "usercreditcard_id", nullable = true)
    private UserCreditCard userCreditcard;
    
    public Transactions() {
    }

  

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TransType getTranstype() {
        return Transtype;
    }

    public void setTranstype(TransType Transtype) {
        this.Transtype = Transtype;
    }

   /* public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }*/

    public Date getTransdate() {
        return transdate;
    }

    public void setTransdate(Date transdate) {
        this.transdate = transdate;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public double getDebit() {
        return debit;
    }

    public void setDebit(double debit) {
        this.debit = debit;
    }

    public double getCurrently_available_funds() {
        return currently_available_funds;
    }

    public void setCurrently_available_funds(double currently_available_funds) {
        this.currently_available_funds = currently_available_funds;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public UserCreditCard getUserCreditcard() {
        return userCreditcard;
    }

    public void setUserCreditcard(UserCreditCard userCreditcard) {
        this.userCreditcard = userCreditcard;
    }

    public TransStatus getTransstatus() {
        return Transstatus;
    }

    public void setTransstatus(TransStatus Transstatus) {
        this.Transstatus = Transstatus;
    }

  

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    
    
 public enum  TransType {
    INITIALBALANCE,
    VIREMENT_DE,
    VIREMENT_A,
    CARTE_DE_CREDIT,
    VIREMENT_DE_DESPEPINIERES,
    VIREMENT_A_DESPEPINIERES,
    WITHDRAWAL,
    DEPOSIT,
     PAIEMENT_DE_CLIENT


}

 
  public enum  TransStatus{
    CREATED,
    CANCELLED,
    COMMITTED
}
}


