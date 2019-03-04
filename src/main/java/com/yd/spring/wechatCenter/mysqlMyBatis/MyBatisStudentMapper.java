package com.yd.spring.wechatCenter.mysqlMyBatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface MyBatisStudentMapper {
	@Select("SELECT * FROM test.tbl_student WHERE id = #{id}")
	MyBatisStudent findById(@Param("id") int id);

	@Select("SELECT * FROM test.tbl_student")
	MyBatisStudent[] findByAll();
}
