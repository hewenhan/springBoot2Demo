package com.yd.spring.springBoot2Demo.mysqlMyBatis;

public class MyBatisStudent {
	private int    id;
	private String name;
	private String branch;
	private int    percentage;
	private int    phone;
	private String email;

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getBranch() {
		return branch;
	}

	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}

	public int getPercentage() {
		return percentage;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setPhone(int phone) {
		this.phone = phone;
	}

	public int getPhone() {
		return phone;
	}

	public void print() {
		System.out.println("id=" + id +
				" name=" + name +
				" email=" + email +
				" branch=" + branch +
				" percentage=" + percentage +
				" phone=" + phone +
				" email=" + email
		);
	}
}
