package com.grokonez.jwtauthentication.message.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class VerifyLogin2 {
	
    @NotBlank
    @Size(min=3, max = 100)
    private String question2;

    @NotBlank
    private String answer2;

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

}
