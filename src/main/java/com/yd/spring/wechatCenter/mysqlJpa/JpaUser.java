package com.yd.spring.wechatCenter.mysqlJpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class JpaUser {
	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	private Integer id;

	private String name;

	private String email;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void print() {
		System.out.println("id=" + id + " name=" + name + " email=" + email);
	}
}
