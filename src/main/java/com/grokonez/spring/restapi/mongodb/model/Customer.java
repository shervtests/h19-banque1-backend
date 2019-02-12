package com.grokonez.spring.restapi.mongodb.model;

import org.springframework.data.annotation.Id;
import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "customers")
public class Customer extends AuditModel {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
	private Long id;

	private String name;
	private int age;
	private boolean active;

	public Customer() {
	}

	public Customer(String name, int age) {
		this.name = name;
		this.age = age;
	}

	public Long getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getAge() {
		return this.age;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "Customer [id=" + id + ", name=" + name + ", age=" + age + ", active=" + active + "]";
	}
}
