package com.yd.spring.springBoot2Demo.libs.mysql;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class MysqlPool {
	private String host;
	private int    port;
	private String db;
	private String userName;
	private String password;
	private int    maxPool;

	private static final String DATABASE_DRIVER = "com.mysql.cj.jdbc.Driver";

	private BasicDataSource basicDataSource;
	private DataSource      dataSource;

	MysqlPool(
			@Value("${mysql.host}") String host,
			@Value("${mysql.port}") int port,
			@Value("${mysql.db}") String db,
			@Value("${mysql.userName}") String userName,
			@Value("${mysql.password}") String password,
			@Value("${mysql.maxPool}") int maxPool
	) {
		this.host = host;
		this.port = port;
		this.db = db;
		this.userName = userName;
		this.password = password;
		this.maxPool = maxPool;
		try {
			String hostUrl = "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.db;

			basicDataSource = new BasicDataSource();
			basicDataSource.setDriverClassName(DATABASE_DRIVER);
			basicDataSource.setUrl(hostUrl);
			basicDataSource.setUsername(this.userName);
			basicDataSource.setPassword(this.password);
			basicDataSource.setInitialSize(5);
			basicDataSource.setMinIdle(5);
			basicDataSource.setMaxTotal(this.maxPool);
			basicDataSource.setConnectionProperties(getConnectionProperties());

			dataSource = basicDataSource;

			System.out.println("MYSQL_POOL INITIALIZED");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	private String getConnectionProperties() {
		return "[]";
	}
}
