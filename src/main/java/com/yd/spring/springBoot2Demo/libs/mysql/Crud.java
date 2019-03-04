package com.yd.spring.springBoot2Demo.libs.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.sql.*;
import java.util.HashMap;


@Configuration
public class Crud {
	@Autowired
	private MysqlPool mysqlPool;

	public Crud() {

	}

	public HashMap[] selectQuery(String sql) throws Exception {
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			conn = mysqlPool.getConnection();
			statement = conn.prepareStatement(sql);

			resultSet = statement.executeQuery();

			HashMap<String, String>[] columnsInfo = getColumnNames(resultSet);

			int rowCount = 0;
			if (resultSet.last()) {
				rowCount = resultSet.getRow();
				resultSet.beforeFirst();
			}

			HashMap[] rows    = new HashMap[rowCount];
			int       rsIndex = 0;
			while (resultSet.next()) {

				HashMap row = new HashMap();
				for (int i = 0; i < columnsInfo.length; i++) {
					String columnName = columnsInfo[i].get("name");
					String columnType = columnsInfo[i].get("type");

					Object columnData = null;

					switch (columnType) {
						case "String":
							columnData = resultSet.getString(columnName);
							break;
						case "Long":
							columnData = resultSet.getLong(columnName);
							break;
						case "Timestamp":
							columnData = resultSet.getTimestamp(columnName);
							break;
						case "Double":
							columnData = resultSet.getDouble(columnName);
							break;
						default:
							columnData = resultSet.getString(columnName);
							break;
					}

					row.put(columnName, columnData);
				}

				rows[rsIndex] = row;
				rsIndex++;
			}
			return rows;
		} catch (SQLException e) {
			System.out.println(sql);
			e.printStackTrace();
			throw new IllegalArgumentException("MYSQL SELECT ERROR");
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			if (statement != null) {
				statement.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
	}

	public UpdateResult updateQuery(String sql) throws Exception {
		Connection conn = null;
		PreparedStatement statement = null;
		try {
			conn = mysqlPool.getConnection();
			statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			statement.executeUpdate();
			return new UpdateResult(statement);
		} catch (SQLException e) {
			System.out.print("\033[31;4m" + sql + "\033[0m\n");
			e.printStackTrace();
			throw new IllegalArgumentException("MYSQL UPDATE ERROR");
		} finally {
			if (statement != null) {
				statement.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
	}

	public static HashMap[] getColumnNames(ResultSet resultSet) throws SQLException {
		ResultSetMetaData rsMetaData = resultSet.getMetaData();

		// init Columns Array
		int       numberOfColumns = rsMetaData.getColumnCount();
		HashMap[] columns         = new HashMap[numberOfColumns];

		// get the column names and types
		for (int i = 1; i < numberOfColumns + 1; i++) {
			HashMap<String, String> columnsInfo = new HashMap();

			// get column name
			String columnName = rsMetaData.getColumnName(i);
			columnsInfo.put("name", columnName);

			// get column type
			int columnType = rsMetaData.getColumnType(i);
			switch (columnType) {
				case Types.CHAR:
				case Types.VARCHAR:
				case Types.BLOB:

					columnsInfo.put("type", "String");
					break;
				case Types.FLOAT:
				case Types.DOUBLE:
				case Types.REAL:
				case Types.DECIMAL:

					columnsInfo.put("type", "Double");
					break;
				case Types.TINYINT:
				case Types.SMALLINT:
				case Types.BIGINT:
				case Types.INTEGER:

					columnsInfo.put("type", "Long");
					break;

				case Types.DATE:
				case Types.TIME:
				case Types.TIMESTAMP:

					columnsInfo.put("type", "Timestamp");
					break;
				default:

					columnsInfo.put("type", "String");
					break;
			}

			columns[i - 1] = columnsInfo;
		}

		return columns;
	}
}
