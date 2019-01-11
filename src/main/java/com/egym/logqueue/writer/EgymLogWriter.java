/**
 * This file is part of the source code and related artifacts for eGym Application.
 *
 * Copyright © 2013 eGym GmbH
 */
package com.egym.logqueue.writer;

/**
 * Used to write log messages.
 *
 * <p>
 * All implementations must be <em>thread-safe</em>. It is recommended to make implementations immutable.
 * </p>
 *
 * @param <T>
 *            the log message type.
 */
public interface EgymLogWriter<T> {
	/**
	 * Invoked for each log message.
	 *
	 * @param logMessage
	 *            the log message. May be null.
	 */
	void write(T logMessage);
}
