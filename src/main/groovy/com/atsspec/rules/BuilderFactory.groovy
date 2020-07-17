package com.atsspec.rules;

import java.security.PublicKey;
import java.sql.Connection;









import groovy.sql.Sql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.context.WebApplicationContext;

import org.springframework.jmx.support.JmxUtils;
import org.springframework.web.context.WebApplicationContext;

import javax.management.MBeanServer;
import javax.sql.DataSource

import com.atsspec.rules.service.RuleController;
import com.codahale.metrics.*
import com.codahale.metrics.jmx.JmxReporter;

@Configuration
public class BuilderFactory {
	@Autowired
	private ApplicationContext context;


	@Bean("executor")
	@Scope(WebApplicationContext.SCOPE_REQUEST)
	public RuleExecutor getExecutor(RuleBuilder rb) {
		SimpleExecutor exec = new SimpleExecutor(rb)
		exec.setCategoryAttributes(rb.getCategoryAttributes())
		exec
	}


	@Bean("appMetrics")
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public RuleEngineMetrics appMetrics(MetricRegistry metricRegistry) {
		RuleEngineMetrics rem = new RuleEngineMetrics()
		metricRegistry.registerAll(rem)
		rem
	}

	@Bean("metricRegistry")
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public MetricRegistry metricRegistry() {
		MetricRegistry metrics = new MetricRegistry();
		MBeanServer mbs = JmxUtils.locateMBeanServer();

		JmxReporter reporter = JmxReporter.forRegistry(metrics).build()
		reporter.start()
		//		metrics.

		//JmxReporter
		return metrics
	}
/*
	@Bean("functionCache")
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public FunctionCache functionCache() {
		return new FunctionCache() {
					Map<String,Script> sc = new HashMap<>();
					public void setScript(String hash,Script script) {
						sc.put(hash,script)
					}
					public Script getScript(String hash) {
						sc.get(hash)
					}
				}
	}
*/
	
	@Bean("RuleBuilderFactory")
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public RuleBuilderFactory getRuleBuilderFactory() {
		return new  RuleBuilderFactory();
	}

	@Bean("RuleBuilder")
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public RuleBuilder getRuleBuilder(DataSource db,RuleBuilderFactory factory) {
		return factory.fetchRuleBuilder(new Sql(db))
	}
}
