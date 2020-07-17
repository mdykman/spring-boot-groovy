package com.atsspec.rules;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;

public class RuleEngineMetrics implements MetricSet {

	Map<String, Metric> metrics = new ConcurrentHashMap<>();
	@Override
	public Map<String, Metric> getMetrics() {
		return metrics;
	}
	
	public void add(String name,Metric metric) {
		metrics.put(name, metric);
	}

}
