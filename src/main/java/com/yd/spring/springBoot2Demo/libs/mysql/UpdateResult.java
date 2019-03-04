package com.yd.spring.springBoot2Demo.libs.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateResult {
	public int affectedRows = 0;
	public int insertId     = 0;

	UpdateResult(PreparedStatement statement) throws SQLException {
		ResultSet insertInfo = statement.getGeneratedKeys();
		if (insertInfo.last()) {
			setInsertId(insertInfo.getInt(1));
		}
		setAffectedRows(statement.getUpdateCount());
	}

	public void setAffectedRows(int num) {
		affectedRows = num;
	}

	public void setInsertId(int id) {
		insertId = id;
	}
}
