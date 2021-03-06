/*
 * Copyright Chris Liao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.jmin.bee;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Connection pool configuration
 * 
 * @author Chris.Liao
 * @version 1.0
 */

public final class BeeDataSourceConfig {

	/**
	 * indicator to not allow to modify configuration after initialization
	 */
	private boolean inited;

	/**
	 * JDBC driver class
	 */
	private String driver;

	/**
	 * jdbc URL
	 */
	private String driverURL;

	/**
	 * jdbc User
	 */
	private String userName;

	/**
	 * jdbc Password
	 */
	private String password;

	/**
	 * connection Driver
	 */
	private Driver connectionDriver = null;

	/**
	 * connection extra properties
	 */
	private Properties jdbcProperties = new Properties();

	/**
	 * if true,first arrival,first taking if false,competition for all borrower
	 * to take idle connection
	 */
	private boolean fairMode;

	/**
	 * pool initialization size
	 */
	private int poolInitSize = 0;

	/**
	 * pool allow max size
	 */
	private int poolMaxSize = 10;

	/**
	 * 'PreparedStatement' cache size
	 */
	private int preparedStatementCacheSize = 10;

	/**
	 * borrower request timeout
	 */
	private long borrowerMaxWaitTime = 180000;

	/**
	 * max idle time for pooledConnection(milliseconds),default value: three
	 * minutes
	 */
	private long connectionIdleTimeout = 180000;

	/**
	 * a test SQL to check connection active state
	 */
	private String validationQuerySQL = "select 1";

	/**
	 * connection validate timeout:5 seconds
	 */
	private int validationQueryTimeout = 5;
	
	/**
	 * BeeCP implementation class name
	 */
	private String connectionPoolClassName = "org.jmin.bee.pool.ConnectionPool";

	/**
	 * milliseconds,max inactive time to check active for borrower
	 */
	private long maxInactiveTimeToCheck = 1000;

	public BeeDataSourceConfig(String driver, String url, String user, String password) {
		this.driver = driver;
		this.driverURL = url;
		this.userName = user;
		this.password = password;
		this.inited = false;
	}

	public void setInited(boolean inited) {
		if (!this.inited)
			this.inited = inited;
	}

	public String getDriver() {
		return driver;
	}

	public String getDriverURL() {
		return driverURL;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public void setDriver(String driver) {
		if (!this.inited)
			this.driver = driver;
	}

	public void setDriverURL(String driverURL) {
		if (!this.inited)
			this.driverURL = driverURL;
	}

	public void setUserName(String userName) {
		if (!this.inited)
			this.userName = userName;
	}

	public void setPassword(String password) {
		if (!this.inited)
			this.password = password;
	}

	public Driver getJdbcConnectionDriver() {
		return connectionDriver;
	}

	public Properties getJdbcProperties() {
		return new Properties(jdbcProperties);
	}

	public void addProperty(String key, String value) {
		if (!this.inited) {
			this.jdbcProperties.put(key, value);
		}
	}

	public void removeProperty(String key) {
		if (!this.inited) {
			this.jdbcProperties.remove(key);
		}
	}

	public boolean isFairMode() {
		return fairMode;
	}

	public void setFairMode(boolean fairMode) {
		if (!this.inited)
			this.fairMode = fairMode;
	}

	public int getPoolInitSize() {
		return poolInitSize;
	}

	public void setPoolInitSize(int poolInitSize) {
		if (!this.inited && poolInitSize >= 0) {
			this.poolInitSize = poolInitSize;
		}
	}

	public  int getPoolMaxSize() {
		return poolMaxSize;
	}

	public void setPoolMaxSize(int poolMaxSize) {
		if (!this.inited && poolMaxSize > 0) {
			this.poolMaxSize = poolMaxSize;
		}
	}

	public  int getPreparedStatementCacheSize() {
		return preparedStatementCacheSize;
	}

	public void setPreparedStatementCacheSize(int statementCacheSize) {
		if (!this.inited && statementCacheSize >= 0) {
			this.preparedStatementCacheSize = statementCacheSize;
		}
	}

	public long getConnectionIdleTimeout() {
		return connectionIdleTimeout;
	}

	public void setConnectionIdleTimeout(long connectionIdleTimeout) {
		if (!this.inited && connectionIdleTimeout > 0) {
			this.connectionIdleTimeout = connectionIdleTimeout;
		}
	}

	public long getBorrowerMaxWaitTime() {
		return borrowerMaxWaitTime;
	}

	public void setBorrowerMaxWaitTime(long borrowerMaxWaitTime) {
		if (!this.inited && borrowerMaxWaitTime > 0) {
			this.borrowerMaxWaitTime = borrowerMaxWaitTime;
		}
	}

	public String getValidationQuerySQL() {
		return validationQuerySQL;
	}

	public void setValidationQuerySQL(String validationQuerySQL) {
		if (!this.inited && validationQuerySQL != null && validationQuerySQL.trim().length() > 0) {
			this.validationQuerySQL = validationQuerySQL;
		}
	}

	public int getValidationQueryTimeout() {
		return validationQueryTimeout;
	}

	public void setValidationQueryTimeout(int validationQueryTimeout) {
		if (!this.inited && validationQueryTimeout > 0) {
			this.validationQueryTimeout = validationQueryTimeout;
		}
	}

	public long getMaxInactiveTimeToCheck() {
		return maxInactiveTimeToCheck;
	}

	public void setMaxInactiveTimeToCheck(long maxInactiveTimeToCheck) {
		if (!this.inited && maxInactiveTimeToCheck > 0) {
			this.maxInactiveTimeToCheck = maxInactiveTimeToCheck;
		}
	}

	private void loadJdbcDriver(String driver) throws IllegalArgumentException {
		try {
			Class.forName(driver, true, this.getClass().getClassLoader());
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("JDBC driver class[" + driver + "] not found");
		}
	}
	
	public String getConnectionPoolClassName() {
		return connectionPoolClassName;
	}
	public void setConnectionPoolClassName(String connectionPoolClassName) {
		if (!this.inited && connectionPoolClassName != null && connectionPoolClassName.trim().length() > 0) {
			this.connectionPoolClassName = connectionPoolClassName;
		}
	}

	/**
	 * check pool configuration
	 */
	public void check() {
		if (isNull(this.driver))
			throw new IllegalArgumentException("JDBC driver class can't be null");
		if (isNull(this.driverURL))
			throw new IllegalArgumentException("JDBC URL can't be null");
		try {
			this.connectionDriver = DriverManager.getDriver(this.driverURL);
		} catch (SQLException e) {}
		if(this.connectionDriver==null)this.loadJdbcDriver(this.driver);
			
		if (this.poolMaxSize <= 0)
			throw new IllegalArgumentException("Pool max size must be greater than zero");
		if (this.poolInitSize < 0)
			throw new IllegalArgumentException("Pool init size must be greater than zero");
		if (this.poolInitSize > poolMaxSize)
			throw new IllegalArgumentException("Error configeruation,pool init size must be less than pool max size");
		if (this.connectionIdleTimeout <= 0)
			throw new IllegalArgumentException("Connection max idle time must be greater than zero");
		if (this.borrowerMaxWaitTime <= 0)
			throw new IllegalArgumentException("Borrower max waiting time must be greater than zero");
		if (this.preparedStatementCacheSize < 0)
			throw new IllegalArgumentException("Statement cache Size must be greater than zero");
		
		//fix issue:#1 The check of validationQuerySQL has logic problem. Chris-2019-05-01 begin
		//if (this.validationQuerySQL != null && validationQuerySQL.trim().length() == 0) {
		if (!isNull(this.validationQuerySQL) && !this.validationQuerySQL.trim().toLowerCase().startsWith("select "))
		//fix issue:#1 The check of validationQuerySQL has logic problem. Chris-2019-05-01 end	
			throw new IllegalArgumentException("connection validate SQL must start with 'select '");
		//}

		if (!isNull(this.userName))
			this.jdbcProperties.put("user", this.userName);
		if (!isNull(this.password))
			this.jdbcProperties.put("password", this.password);
	}

	private boolean isNull(String value) {
		return (value == null || value.trim().length() == 0);
	}
}
