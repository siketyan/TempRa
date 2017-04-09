package io.github.siketyan.monitor.util;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;

public class SQLManager implements Closeable {
	private Connection connection;
	
	private final String type;
	private final String host;
	private final int port;
	private final String database;
	private final String user;
	private final String password;
	private final int timeout;
	
	public SQLManager(String sql, String host, int port, String database, String user, String password, int timeout)
			throws SQLException {
		this.type = sql;
		this.host = host;
		this.port = port;
		this.database = database;
		this.user = user;
		this.password = password;
		this.timeout = timeout;
		
		connect();
	}
	
	private void connect() throws SQLException {
		connection = DriverManager.getConnection(
						 "jdbc:" + type + "://"
									 + host + ":"
									 + port + "/"
							 		 + database,
						 user, password
					 );
	}
	
	@Deprecated
	public ResultSet executeQuery(String sql) throws SQLException {
		checkTimeout();
		try (Statement state = connection.createStatement()) {
			return state.executeQuery(sql);
		}
	}
	
	@Override
	public void close() throws IOException {
		try {
			connection.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void checkTimeout() throws SQLException {
		if (!connection.isValid(timeout)) {
			connect();
		}
	}
	
	public Statement getStatement() throws SQLException {
		checkTimeout();
		return connection.createStatement();
	}
	
    public PreparedStatement getPreparedStatement(String sql) throws SQLException {
        checkTimeout();
        return connection.prepareStatement(sql);
    }
}