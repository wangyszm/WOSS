package com.briup.util.impl;

import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import com.briup.util.Logger;

public class LoggerImpl implements Logger{
	private org.apache.log4j.Logger rootLogger;
	
	@Override
	public void init(Properties properties) {
		PropertyConfigurator.configure(properties.getProperty("log4jUrl"));
		rootLogger=org.apache.log4j.Logger.getRootLogger();
	}

	@Override
	public void debug(String msg) {
		rootLogger.debug(msg);
	}

	@Override
	public void debug(String msg, Object key) {
		rootLogger.debug(msg, null);
	}

	@Override
	public void info(String msg) {
		rootLogger.info(msg);
	}

	@Override
	public void info(String msg, Object key) {
		
	}

	@Override
	public void warn(String msg) {
		rootLogger.warn(msg);
	}

	@Override
	public void warn(String msg, Object key) {
		
	}

	@Override
	public void error(String msg) {
		rootLogger.error(msg);
	}

	@Override
	public void error(String msg, Object key) {
		
	}

	@Override
	public void fatal(String msg) {
		rootLogger.fatal(msg);
	}

	@Override
	public void fatal(String msg, Object key) {
		
	}

}
