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

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.NamingManager;
import javax.naming.spi.ObjectFactory;
import javax.sql.DataSource;

/**
 * BeeDataSource factory
 * 
 * @author Chris.Liao
 * @version 1.0
 */
public final class BeeDataSourceFactory implements ObjectFactory {
	public final static String PROP_INITIALSIZE = "initialSize";
	public final static String PROP_MAXACTIVE = "maxActive";
	public final static String PROP_MAXWAIT = "maxWait";
	
	public final static String PROP_URL = "url";
	public final static String PROP_USERNAME = "username";
	public final static String PROP_PASSWORD = "password";
	public final static String PROP_DRIVERCLASSNAME = "driverClassName";
	public final static String PROP_VALIDATIONQUERY = "validationQuery";
	public final static String PROP_FAIR_MODE = "fairMode";
	
	public final static String PROP_VALIDATIONQUERY_TIMEOUT = "validationQueryTimeout";
	public final static String PROP_POOLPREPAREDSTATEMENTS = "poolPreparedStatements";
	public final static String PROP_MAXOPENPREPAREDSTATEMENTS = "maxOpenPreparedStatements";
	public final static String PROP_DEFAULTTRANSACTIONISOLATION = "defaultTransactionIsolation";
	public final static String PROP_MINEVICTABLEIDLETIMEMILLIS = "minEvictableIdleTimeMillis";
	
	public final static String PROVIDER_URL = "java.naming.provider.url";
	public final static String INITIAL_CONTEXT_FACTORY = "java.naming.factory.initial";
	public final static String SECURITY_PRINCIPAL = "java.naming.security.principal";
	public final static String SECURITY_CREDENTIALS = "java.naming.security.credentials";
	
	private Properties initProperties = new Properties();
	public BeeDataSourceFactory() {}
	public BeeDataSourceFactory(Properties initProperties) {
		if (initProperties != null)this.initProperties = initProperties;
	}
	public void addProperty(String key, String value) {
		this.initProperties.put(key, value);
	}
	public void removeProperty(String key) {
		this.initProperties.remove(key);
	}
	public void unbind(String jndi) throws NamingException {
		InitialContext ctx = new InitialContext(initProperties);
		ctx.unbind(jndi);
	}
	public DataSource lookup(String jndi) throws NamingException {
		InitialContext ctx = new InitialContext(initProperties);
		return (DataSource) ctx.lookup(jndi);
	}
	public void bind(String jndi, DataSource obj) throws NamingException {
		InitialContext ctx = new InitialContext(initProperties);
		ctx.bind(jndi, obj);
	}
	public DataSource create(BeeDataSourceConfig config) throws SQLException,NamingException {
		return new BeeDataSource((BeeDataSourceConfig) config);
	}
	
	/**
	 *
	 * @param obj
	 *            The possibly null object containing location or reference
	 *            information that can be used in creating an object.
	 * @param name
	 *            The name of this object relative to <code>nameCtx</code>, or
	 *            null if no name is specified.
	 * @param nameCtx
	 *            The context relative to which the <code>name</code> parameter
	 *            is specified, or null if <code>name</code> is relative to the
	 *            default initial context.
	 * @param environment
	 *            The possibly null environment that is used in creating the
	 *            object.
	 * @return The object created; null if an object cannot be created.
	 * @exception Exception
	 *                if this object factory encountered an exception while
	 *                attempting to create an object, and no other object
	 *                factories are to be tried.
	 *
	 * @see NamingManager#getObjectInstance
	 * @see NamingManager#getURLContext
	 */
	public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment)
			throws Exception {
		
		Reference ref = (Reference) obj;
		String driverClass=null,jdbcURL=null,jdbcUser=null,password=null;
		String initSize=null,maxSize=null,maxWait=null;
		String connectionIdleTimeout=null,fairMode=null;
		String validationQuerySQL=null,validationQueryTimeout=null;
		String needStatementCache=null,statementCacheSize=null;
	
		RefAddr ra = ref.get(PROP_DRIVERCLASSNAME);
        if(ra != null) driverClass= ra.getContent().toString();
        ra = ref.get(PROP_URL);
        if(ra != null) jdbcURL= ra.getContent().toString(); 
        ra = ref.get(PROP_USERNAME);
        if(ra != null) jdbcUser= ra.getContent().toString();
        ra = ref.get(PROP_PASSWORD);
        if(ra != null) password= ra.getContent().toString(); 
		BeeDataSourceConfig config = new BeeDataSourceConfig(driverClass,jdbcURL,jdbcUser,password);
	
	    ra = ref.get(PROP_INITIALSIZE);
        if(ra != null) initSize= ra.getContent().toString(); 
	    ra = ref.get(PROP_MAXACTIVE);
        if(ra != null) maxSize= ra.getContent().toString(); 
	    ra = ref.get(PROP_MAXWAIT);
        if(ra != null) maxWait= ra.getContent().toString(); 
 
        ra = ref.get(PROP_FAIR_MODE);
        if(ra != null) fairMode= ra.getContent().toString(); 
        ra = ref.get(PROP_VALIDATIONQUERY);
        if(ra != null) validationQuerySQL= ra.getContent().toString(); 
        ra = ref.get(PROP_VALIDATIONQUERY_TIMEOUT);
        if(ra != null) validationQueryTimeout= ra.getContent().toString(); 
        
        ra = ref.get(PROP_MINEVICTABLEIDLETIMEMILLIS);
        if(ra != null) connectionIdleTimeout= ra.getContent().toString(); 
       
		if (!isNull(maxSize))
			config.setPoolMaxSize(Integer.parseInt(maxSize));
		if (!isNull(initSize))
			config.setPoolInitSize(Integer.parseInt(initSize));
		if (!isNull(maxWait))
			config.setBorrowerMaxWaitTime(Integer.parseInt(maxWait));
		if (!isNull(validationQuerySQL))
			config.setValidationQuerySQL(validationQuerySQL);
		if (!isNull(validationQueryTimeout))
			config.setValidationQueryTimeout(Integer.parseInt(validationQueryTimeout));
		if (!isNull(connectionIdleTimeout))
			config.setConnectionIdleTimeout(Integer.parseInt(connectionIdleTimeout));
	
        ra = ref.get(PROP_POOLPREPAREDSTATEMENTS);
        if(ra != null) needStatementCache= ra.getContent().toString(); 
        ra = ref.get(PROP_MAXOPENPREPAREDSTATEMENTS);
        if(ra != null) statementCacheSize= ra.getContent().toString(); 
	
		if ("true".equals(needStatementCache) || "Y".equals(needStatementCache)) {
			if (!isNull(statementCacheSize))
				config.setPreparedStatementCacheSize(Integer.parseInt(statementCacheSize));
		} else {
			config.setPreparedStatementCacheSize(0);
		}
		
		if ("true".equalsIgnoreCase(fairMode) || "Y".equalsIgnoreCase(fairMode)) {
			config.setFairMode(true);
		} else {
			config.setFairMode(false);
		}
		return new BeeDataSource(config);
	}
	
	private boolean isNull(String value) {
		return (value == null || value.trim().length()==0);
	}
}
