package com.yd.spring.wechatCenter.mysqlMyBatis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class MyBatisStudentApp {
	@Autowired
	private MyBatisStudentMapper myBatisStudentMapper;

	@PostConstruct
	public void init() {
		MyBatisStudent[] MyBatisStudentList = myBatisStudentMapper.findByAll();
		for (MyBatisStudent student: MyBatisStudentList) {
			student.print();
		}
	}
}
