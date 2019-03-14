/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grokonez.jwtauthentication.message.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 *
 * @author smile
 */
public class TransferRequest {
    @NotBlank
    @Size(min=3, max = 60)
    private String senderaccountno;
    
    @NotBlank
    @Size(min=3, max = 60)
    private String receiveraccountno;

    private double amount;

    public String getSenderaccountno() {
        return senderaccountno;
    }

    public void setSenderaccountno(String senderaccountno) {
        this.senderaccountno = senderaccountno;
    }

    public String getReceiveraccountno() {
        return receiveraccountno;
    }

    public void setReceiveraccountno(String receiveraccountno) {
        this.receiveraccountno = receiveraccountno;
    }


    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    
    
}
