package com.grokonez.jwtauthentication.message.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class VerifyLogin1 {
    @NotBlank
    @Size(min=3, max = 100)
    private String question1;

    @NotBlank
    private String answer1;

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


}