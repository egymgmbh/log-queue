/**
 * This file is part of the source code and related artifacts for eGym Application.
 *
 * Copyright © 2013 eGym GmbH
 */
package com.egym.logqueue;

import net.jcip.annotations.Immutable;

import org.joda.time.DateTime;

import com.egym.logqueue.slf4j.EgymLogger;

/**
 * Contains all the information about one log record as it is created from each slf4j log call. This class is immutable and thread-safe.
 * However, the exception which is referenced in the 'throwable' field might be mutable and might not be thread-safe.
 */
@Immutable
public class EgymLogRecord {
	/** The point in time the log record was created. */
	private final DateTime timestamp;

	/** The logger which created this record. */
	private final EgymLogger source;

	/** The log level. */
	private final EgymLogLevel logLevel;

	/** The log message, if provided. */
	private final String message;

	/** The throwable, if provided. */
	private final Throwable throwable;

	/**
	 * @param timestamp
	 *            The point in time the log record was created. Must not be null.
	 * @param source
	 *            The logger which created this record. Must not be null.
	 * @param logLevel
	 *            The log level. Must not be null.
	 * @param message
	 *            The log message. May be null.
	 * @param throwable
	 *            The throwable. May be null.
	 */
	public EgymLogRecord(DateTime timestamp, EgymLogger source, EgymLogLevel logLevel, String message, Throwable throwable) {
		if (timestamp == null) {
			throw new IllegalArgumentException("timestamp must not be null");
		}
		if (source == null) {
			throw new IllegalArgumentException("source must not be null");
		}
		if (logLevel == null) {
			throw new IllegalArgumentException("logLevel must not be null");
		}

		this.timestamp = timestamp;
		this.source = source;
		this.logLevel = logLevel;
		this.message = message;
		this.throwable = throwable;
	}

	/**
	 * @return The point in time the log record was created. Never null.
	 */
	public DateTime getTimestamp() {
		return timestamp;
	}

	/**
	 * @return The logger which created this record. Never null.
	 */
	public EgymLogger getSource() {
		return source;
	}

	/**
	 * @return The log level. Never null.
	 */
	public EgymLogLevel getLogLevel() {
		return logLevel;
	}

	/**
	 * @return The log message, if provided. May be null.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Beware that the exception might not be thread-safe.
	 *
	 * @return the logged exception.
	 */
	public Throwable getThrowable() {
		return throwable;
	}
}
