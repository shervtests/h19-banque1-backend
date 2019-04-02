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
public class ForgetPasswordRequest {
   
    @NotBlank
    @Size(min=3, max = 100)
    private String question1;

    @NotBlank
    @Size(min = 1, max = 100)
    private String answer1;
    
    @NotBlank
    @Size(min=3, max = 100)
    private String question2;

    @NotBlank
    @Size(min = 1, max = 100)
    private String answer2;
    
    @NotBlank
    @Size(min = 1, max = 40)
    private String username;
    
    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    public String getQuestion1() {
        return question1;
    }

    public void setQuestion1(String question1) {
        this.question1 = question1;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getQuestion2() {
        return question2;
    }

    public void setQuestion2(String question2) {
        this.question2 = question2;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
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

    
    
    
}
