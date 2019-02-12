package com.grokonez.spring.restapi.mongodb.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;


@Entity
@Table(name = "users")
public class User  extends AuditModel {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long id;
    private String firstname;
    private String lastname;
    private String username;
    private String password;
    private String question1;
    private String question2;
    private String question3;
    private String answer1;
    private String answer2;
    private String answer3;
    private String fullAddress;
    private String homePhone;
    private String mobile;
    private String email;
    private String chequing;
    private String saving;
    private String amountAvailOfBankAccount;
    private String limiteCredit;
    private String balance;

    private String chequingSaving;

    private String role;

    private String permission;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getChequingSaving() {
        return chequingSaving;
    }

    public void setChequingSaving(String chequingSaving) {
        this.chequingSaving = chequingSaving;
    }

    public User() {
    }

    public User( String firstname, String lastname, String username, String password, String question1, String question2, String question3, String answer1, String answer2, String answer3, String fullAddress, String homePhone, String mobile, String email, String chequing, String saving, String amountAvailOfBankAccount, String limiteCredit, String balance) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.question1 = question1;
        this.question2 = question2;
        this.question3 = question3;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.fullAddress = fullAddress;
        this.homePhone = homePhone;
        this.mobile = mobile;
        this.email = email;
        this.chequing = chequing;
        this.saving = saving;
        this.amountAvailOfBankAccount = amountAvailOfBankAccount;
        this.limiteCredit = limiteCredit;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getQuestion1() {
        return question1;
    }

    public void setQuestion1(String question1) {
        this.question1 = question1;
    }

    public String getQuestion2() {
        return question2;
    }

    public void setQuestion2(String question2) {
        this.question2 = question2;
    }

    public String getQuestion3() {
        return question3;
    }

    public void setQuestion3(String question3) {
        this.question3 = question3;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public void setAnswer3(String answer3) {
        this.answer3 = answer3;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getChequing() {
        return chequing;
    }

    public void setChequing(String chequing) {
        this.chequing = chequing;
    }

    public String getSaving() {
        return saving;
    }

    public void setSaving(String saving) {
        this.saving = saving;
    }

    public String getAmountAvailOfBankAccount() {
        return amountAvailOfBankAccount;
    }

    public void setAmountAvailOfBankAccount(String amountAvailOfBankAccount) {
        this.amountAvailOfBankAccount = amountAvailOfBankAccount;
    }


    public String getlimiteCredit() {
        return limiteCredit;
    }

    public void setlimiteCredit(String limiteCredit) {
        this.limiteCredit = limiteCredit;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
