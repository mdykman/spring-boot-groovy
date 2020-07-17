package com.atsspec.rules.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;

import com.codahale.metrics.MetricRegistry;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariDataSource;

import groovy.sql.Sql;

@Configuration
public class DataBaseConfigurer {
	Properties properties;
	String username;
	String password;
	String database;
	String host;
	int minConnections = 5;
	int maxConnections = 20;

//	@Autowired
//	private MetricRegistry metricRegistry;
	
	public DataBaseConfigurer() {
		String dbf = System.getProperty("db.properties");
		InputStream is = null;
		String prefix = "";

		try {
			if (dbf != null && new File(dbf).exists()) {
				is = new FileInputStream(dbf);
				properties = new Properties();
				properties.load(is);
				is.close();
			} else {
				properties = System.getProperties();
				prefix="db.";
			}
			username = properties.getProperty(prefix + "username");
			if (username == null)
				throw new BeanCreationException(
						"unable to load username for database");
			password = properties.getProperty(prefix + "password");
			if (password == null)
				throw new BeanCreationException(
						"unable to load password for database");
			database = properties.getProperty(prefix + "database");
			if (database == null)
				throw new BeanCreationException(
						"unable to load database for database");

			host = properties.getProperty(prefix + "host");
			if (host == null)
				throw new BeanCreationException(
						"unable to load host for database");

			String s = properties.getProperty(prefix + "minConnections");
			if (s != null) {
				minConnections = Integer.parseInt(s);
			}
			s = properties.getProperty(prefix + "maxConnections");
			if (s != null) {
				maxConnections = Integer.parseInt(s);
			}
		} catch (NumberFormatException|IOException e) {
			throw new BeanCreationException(
					"error loading database parameters",e);

		}
		
		try {
			if(is!=null) is.close();
		} catch (IOException e) {
		}

	}

	@Bean(name = "datasource", destroyMethod = "close")
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	// @Lazy
	public DataSource dataSource(MetricRegistry mr) {
		MysqlDataSource mds = new MysqlDataSource();
		mds.setUser(username);
		mds.setPassword(password);
		mds.setURL("jdbc:mysql://" + host + "/" + database);

		HikariDataSource ds = new HikariDataSource();
		ds.setDataSource(mds);
		ds.setMinimumIdle(minConnections);
		ds.setMaximumPoolSize(maxConnections);
		ds.setLeakDetectionThreshold(2000);
		ds.setMaxLifetime(30000);
		ds.setIdleTimeout(120000);
		ds.setIsolateInternalQueries(true);
		ds.setMetricRegistry(mr);
		return ds;
	}

	@Bean(name = "connection", destroyMethod = "close")
	@Scope(WebApplicationContext.SCOPE_REQUEST)
	public Sql connection(DataSource ds) throws java.sql.SQLException {
		Sql s = new Sql(ds);
		return s;
	}
}
