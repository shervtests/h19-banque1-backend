/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grokonez.jwtauthentication.message.response;

/**
 *
 * @author smile
 */
public class Response {
 
   private  String transactionId ;
   private  String result;

    public Response(String transactionId, String result) {
        this.transactionId = transactionId;
        this.result = result;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
   
   
   
   
}