/*
 * Copyright Chris Liao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.jmin.bee.pool;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.jmin.bee.pool.util.ConnectionUtil;

/**
 * Statement resultset proxy super class
 * 
 * @author Chris.Liao
 * @version 1.0
 */
public abstract class ProxyResultSet implements ResultSet {
	private boolean isClosed;
	protected ResultSet delegate;
	private ProxyStatementWrapper proxyStatement;
	
	public ProxyResultSet(ResultSet delegate, ProxyStatementWrapper proxyStatement) {
		this.delegate = delegate;
		this.proxyStatement = proxyStatement;
	}
	public boolean isClosed() {
		return isClosed;
	}
	protected void updateLastActivityTime() throws SQLException {
		if (isClosed)throw new SQLException("ResultSet has been closed,access forbidden");
		this.proxyStatement.updateLastActivityTime();
	}
	
	public void close() throws SQLException {
		if (this.isClosed) {
			throw new SQLException("ResultSet has been closed");
		} else {
			this.isClosed = true;
			ConnectionUtil.close(delegate);
			this.delegate = null;
			this.proxyStatement = null;
		}
	}
}
