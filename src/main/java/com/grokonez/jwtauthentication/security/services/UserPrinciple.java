package com.grokonez.jwtauthentication.security.services;

import com.grokonez.jwtauthentication.model.Role;
import com.grokonez.jwtauthentication.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class UserPrinciple implements UserDetails {
	private static final long serialVersionUID = 1L;

	private Long id;

	private String firstname;

	private String lastname;

	private String username;

	private String email;

	private String question1;

	private String answer1;

	private String question2;

	private String answer2;

	@JsonIgnore
	private String password;

	private String mobile;

	private String landline;
	private String creditcarno;
	private String accountno;
	private int amount;
	private int creditbalanceavailable;
	private int creditbalanceowned;

	private Collection<? extends GrantedAuthority> authorities;

	private String role;

	private String city;

	private String province;

	private String zip;

	private String address;

	public UserPrinciple(Long id, String firstname, String lastname, String username, int amount,
			int creditbalanceavailable, int creditbalanceowned, String email, String question1, String answer1,
			String question2, String answer2, String password, String address, String city, String province, String zip,
			String country, String mobile, String landline, Collection<? extends GrantedAuthority> authorities) {

		this.id = id;
		this.firstname = firstname;
		this.lastname = lastname;
		this.username = username;
		this.amount = amount;
		this.creditbalanceavailable = creditbalanceavailable;
		this.creditbalanceowned = creditbalanceowned;
		this.email = email;
		this.question1 = question1;
		this.answer1 = answer1;
		this.question2 = question2;
		this.answer2 = answer2;
		this.password = password;
		this.address = address;
		this.city = city;
		this.province = province;
		this.zip = zip;
		this.mobile = mobile;
		this.landline = landline;

		this.authorities = authorities;
	}

	public static UserPrinciple build(User user) {
		List<GrantedAuthority> authorities = user.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList());

		return new UserPrinciple(user.getId(), user.getFirstname(), user.getLastname(), user.getUsername(),
				user.getAmount(), user.getCreditbalanceavailable(), user.getCreditbalanceowned(), user.getEmail(),
				user.getQuestion1(), user.getAnswer1(), user.getQuestion2(), user.getAnswer2(), user.getPassword(),
				user.getAddress(), user.getCity(), user.getProvince(), user.getZip(), user.getCountry(),
				user.getMobile(), user.getLandline(), authorities);
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getLandline() {
		return landline;
	}

	public void setLandline(String landline) {
		this.landline = landline;
	}

	public String getCreditcarno() {
		return creditcarno;
	}

	public void setCreditcarno(String creditcarno) {
		this.creditcarno = creditcarno;
	}

	public String getAccountno() {
		return accountno;
	}

	public void setAccountno(String accountno) {
		this.accountno = accountno;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getCreditbalanceavailable() {
		return creditbalanceavailable;
	}

	public void setCreditbalanceavailable(int creditbalanceavailable) {
		this.creditbalanceavailable = creditbalanceavailable;
	}

	public int getCreditbalanceowned() {
		return creditbalanceowned;
	}

	public void setCreditbalanceowned(int creditbalanceowned) {
		this.creditbalanceowned = creditbalanceowned;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setQuestion1(String question1) {
		this.question1 = question1;
	}

	public void setAnswer1(String answer1) {
		this.answer1 = answer1;
	}

	public void setQuestion2(String question2) {
		this.question2 = question2;
	}

	public void setAnswer2(String answer2) {
		this.answer2 = answer2;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	public Long getId() {
		return id;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public String getEmail() {
		return email;
	}

	public String getAnswer1() {
		return answer1;
	}

	public String getQuestion1() {
		return question1;
	}

	public String getAnswer2() {
		return answer2;
	}

	public String getQuestion2() {
		return question2;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		UserPrinciple user = (UserPrinciple) o;
		return Objects.equals(id, user.id);
	}
}