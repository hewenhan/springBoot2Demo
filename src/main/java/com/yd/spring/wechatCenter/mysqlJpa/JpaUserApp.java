package com.yd.spring.wechatCenter.mysqlJpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

import static java.lang.System.out;

@Configuration
public class JpaUserApp {
	@Autowired JpaUserRepository jpaUserRepository;

	@PostConstruct
	public void init() {
		JpaUser n = new JpaUser();
		n.setName("aaa");
		n.setEmail("111");
		jpaUserRepository.save(n);

		n = new JpaUser();
		n.setName("bbb");
		n.setEmail("222");
		jpaUserRepository.save(n);

		n = new JpaUser();
		n.setName("ccc");
		n.setEmail("333");
		jpaUserRepository.save(n);
		out.println("SAVED ALL USERS");

		Iterable<JpaUser> allJpaUser = jpaUserRepository.findAll();

		for (JpaUser jpaUser: allJpaUser) {
			jpaUser.print();
		}
	}
}
