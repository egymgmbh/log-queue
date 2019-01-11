/**
 * This file is part of the source code and related artifacts for eGym Application.
 *
 * Copyright © 2013 eGym GmbH
 */
package com.egym.logqueue.slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.jcip.annotations.ThreadSafe;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

/**
 * The central authority used to create all slf4j loggers in the application.
 */
@ThreadSafe
public class EgymLoggerFactory implements ILoggerFactory {
	/** Keeps track of already created loggers. */
	private final ConcurrentMap<String, Logger> loggers = new ConcurrentHashMap<String, Logger>();

	@Override
	public Logger getLogger(final String name) {
		if (name == null) {
			throw new IllegalArgumentException("name must not be null");
		}

		final Logger logger = loggers.get(name);
		if (logger != null) {
			return logger;
		} else {
			Logger newInstance = new EgymLogger(name);
			Logger oldInstance = loggers.putIfAbsent(name, newInstance);
			return oldInstance == null ? newInstance : oldInstance;
		}
	}
}
