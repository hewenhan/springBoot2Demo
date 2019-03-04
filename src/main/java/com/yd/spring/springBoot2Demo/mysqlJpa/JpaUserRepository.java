package com.yd.spring.springBoot2Demo.mysqlJpa;

import org.springframework.data.repository.CrudRepository;

public interface JpaUserRepository extends CrudRepository<JpaUser, Integer> {

}
