/**
 * This file is part of the source code and related artifacts for eGym Application.
 *
 * Copyright © 2013 eGym GmbH
 */
package org.slf4j.impl;

import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

import com.egym.logqueue.slf4j.EgymLoggerFactory;

/**
 * This binds the custom egym logging framework. See {@link EgymLoggerFactory}. This class must be exactly in this package for slf4j to find
 * it.
 */
public class StaticLoggerBinder implements LoggerFactoryBinder {
	private static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();

	private static final String LOGGER_FACTORY_CLASS_STR = EgymLoggerFactory.class.getName();

	/**
	 * Declare the version of the SLF4J API this implementation is compiled against. The value of this field is usually modified with each
	 * release.
	 */
	// to avoid constant folding by the compiler, this field must *not* be final
	public static String REQUESTED_API_VERSION = "1.6.99"; // !final

	/**
	 * The ILoggerFactory instance returned by the {@link #getLoggerFactory} method should always be the same object
	 */
	private final ILoggerFactory loggerFactory;

	private StaticLoggerBinder() {
		loggerFactory = new EgymLoggerFactory();
	}

	/**
	 * Return the singleton of this class.
	 *
	 * @return the StaticLoggerBinder singleton
	 */
	public static final StaticLoggerBinder getSingleton() {
		return SINGLETON;
	}

	@Override
	public ILoggerFactory getLoggerFactory() {
		return loggerFactory;
	}

	@Override
	public String getLoggerFactoryClassStr() {
		return LOGGER_FACTORY_CLASS_STR;
	}
}
