/**
 * This file is part of the source code and related artifacts for eGym Application.
 *
 * Copyright © 2013 eGym GmbH
 */
package com.egym.logqueue.slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.egym.logqueue.EgymLogLevel;
import com.egym.logqueue.EgymLogLevels;
import com.egym.logqueue.EgymLogQueue;
import com.egym.logqueue.EgymLogRecord;
import com.egym.logqueue.formatter.EgymLogFormatterUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.helpers.MessageFormatter;

import com.google.inject.Inject;

/**
 * The egym logger implementation. This is bound to slf4j in {@link EgymLoggerFactory} and handles <em>all</em> the log messages passed into
 * slf4j.
 */
public class EgymLogger implements Logger {
	/** Defines the log level threshold. Everything below this threshold will be ignored. */
	private static final EgymLogLevel THRESHOLD = EgymLogLevel.DEBUG;

	/**
	 * Defines logging thresholds based on logger names. This is useful to ignore certain packages which log excessively in the DEBUG level.
	 */
	private static final Map<String, EgymLogLevel> THRESHOLDS;

	/** The log queue. Statically injected by Guice. This is null in the early phase of the application initialization. */
	@Inject
	static EgymLogQueue logQueue;

	/** The logger name. */
	private final String name;

	/** Logger specific threshold. */
	private final EgymLogLevel threshold;

	static {
		final Map<String, EgymLogLevel> thresholds = new HashMap<String, EgymLogLevel>();
		// To avoid excessive logging we ignore everything from Hibernate which is below INFO.
		// TODO: This should be moved into the pipeline configuration.
		thresholds.put("org.hibernate", EgymLogLevel.INFO);
		THRESHOLDS = Collections.unmodifiableMap(thresholds);
	}

	/**
	 * @param name
	 *            the logger name.
	 */
	EgymLogger(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name must not be null");
		}

		this.name = name;

		EgymLogLevel threshold = THRESHOLD;

		for (String key : THRESHOLDS.keySet()) {
			if (name.startsWith(key)) {
				final EgymLogLevel specificThreshold = THRESHOLDS.get(key);
				if (specificThreshold.ordinal() > threshold.ordinal()) {
					threshold = specificThreshold;
				}
			}
		}

		this.threshold = threshold;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isTraceEnabled() {
		return isSufficientLogLevel(EgymLogLevel.TRACE);
	}

	@Override
	public void trace(String msg) {
		enqueue(EgymLogLevel.TRACE, msg);
	}

	@Override
	public void trace(String format, Object arg) {
		trace(format(format, arg));
	}

	@Override
	public void trace(String format, Object arg1, Object arg2) {
		trace(format(format, arg1, arg2));
	}

	@Override
	public void trace(String format, Object... arguments) {
		trace(format(format, arguments));
	}

	@Override
	public void trace(String msg, Throwable t) {
		enqueue(EgymLogLevel.TRACE, msg, t);
	}

	@Override
	public boolean isTraceEnabled(Marker marker) {
		return isSufficientLogLevel(EgymLogLevel.TRACE);
	}

	@Override
	public void trace(Marker marker, String msg) {
		trace(msg);
	}

	@Override
	public void trace(Marker marker, String format, Object arg) {
		trace(format, arg);
	}

	@Override
	public void trace(Marker marker, String format, Object arg1, Object arg2) {
		trace(format, arg1, arg2);
	}

	@Override
	public void trace(Marker marker, String format, Object... argArray) {
		trace(format, argArray);
	}

	@Override
	public void trace(Marker marker, String msg, Throwable t) {
		trace(msg, t);
	}

	@Override
	public boolean isDebugEnabled() {
		return isSufficientLogLevel(EgymLogLevel.DEBUG);
	}

	@Override
	public void debug(String msg) {
		enqueue(EgymLogLevel.DEBUG, msg);
	}

	@Override
	public void debug(String format, Object arg) {
		debug(format(format, arg));
	}

	@Override
	public void debug(String format, Object arg1, Object arg2) {
		debug(format(format, arg1, arg2));
	}

	@Override
	public void debug(String format, Object... arguments) {
		debug(format(format, arguments));
	}

	@Override
	public void debug(String msg, Throwable t) {
		enqueue(EgymLogLevel.DEBUG, msg, t);
	}

	@Override
	public boolean isDebugEnabled(Marker marker) {
		return isSufficientLogLevel(EgymLogLevel.DEBUG);
	}

	@Override
	public void debug(Marker marker, String msg) {
		debug(msg);
	}

	@Override
	public void debug(Marker marker, String format, Object arg) {
		debug(format, arg);
	}

	@Override
	public void debug(Marker marker, String format, Object arg1, Object arg2) {
		debug(format, arg1, arg2);
	}

	@Override
	public void debug(Marker marker, String format, Object... arguments) {
		debug(format, arguments);
	}

	@Override
	public void debug(Marker marker, String msg, Throwable t) {
		debug(msg, t);
	}

	@Override
	public boolean isInfoEnabled() {
		return isSufficientLogLevel(EgymLogLevel.INFO);
	}

	@Override
	public void info(String msg) {
		enqueue(EgymLogLevel.INFO, msg);
	}

	@Override
	public void info(String format, Object arg) {
		info(format(format, arg));
	}

	@Override
	public void info(String format, Object arg1, Object arg2) {
		info(format(format, arg1, arg2));
	}

	@Override
	public void info(String format, Object... arguments) {
		info(format(format, arguments));
	}

	@Override
	public void info(String msg, Throwable t) {
		enqueue(EgymLogLevel.INFO, msg, t);
	}

	@Override
	public boolean isInfoEnabled(Marker marker) {
		return isSufficientLogLevel(EgymLogLevel.INFO);
	}

	@Override
	public void info(Marker marker, String msg) {
		info(msg);
	}

	@Override
	public void info(Marker marker, String format, Object arg) {
		info(format, arg);
	}

	@Override
	public void info(Marker marker, String format, Object arg1, Object arg2) {
		info(format, arg1, arg2);
	}

	@Override
	public void info(Marker marker, String format, Object... arguments) {
		info(format, arguments);
	}

	@Override
	public void info(Marker marker, String msg, Throwable t) {
		info(msg, t);
	}

	@Override
	public boolean isWarnEnabled() {
		return isSufficientLogLevel(EgymLogLevel.WARN);
	}

	@Override
	public void warn(String msg) {
		enqueue(EgymLogLevel.WARN, msg);
	}

	@Override
	public void warn(String format, Object arg) {
		warn(format(format, arg));
	}

	@Override
	public void warn(String format, Object... arguments) {
		warn(format(format, arguments));
	}

	@Override
	public void warn(String format, Object arg1, Object arg2) {
		warn(format(format, arg1, arg2));
	}

	@Override
	public void warn(String msg, Throwable t) {
		enqueue(EgymLogLevel.WARN, msg, t);
	}

	@Override
	public boolean isWarnEnabled(Marker marker) {
		return isSufficientLogLevel(EgymLogLevel.WARN);
	}

	@Override
	public void warn(Marker marker, String msg) {
		warn(msg);
	}

	@Override
	public void warn(Marker marker, String format, Object arg) {
		warn(format, arg);
	}

	@Override
	public void warn(Marker marker, String format, Object arg1, Object arg2) {
		warn(format, arg1, arg2);
	}

	@Override
	public void warn(Marker marker, String format, Object... arguments) {
		warn(format, arguments);
	}

	@Override
	public void warn(Marker marker, String msg, Throwable t) {
		warn(msg, t);
	}

	@Override
	public boolean isErrorEnabled() {
		return isSufficientLogLevel(EgymLogLevel.ERROR);
	}

	@Override
	public void error(String msg) {
		enqueue(EgymLogLevel.ERROR, msg);
	}

	@Override
	public void error(String format, Object arg) {
		error(format(format, arg));
	}

	@Override
	public void error(String format, Object arg1, Object arg2) {
		error(format(format, arg1, arg2));
	}

	@Override
	public void error(String format, Object... arguments) {
		error(format(format, arguments));
	}

	@Override
	public void error(String msg, Throwable t) {
		enqueue(EgymLogLevel.ERROR, msg, t);
	}

	@Override
	public boolean isErrorEnabled(Marker marker) {
		return isSufficientLogLevel(EgymLogLevel.ERROR);
	}

	@Override
	public void error(Marker marker, String msg) {
		error(msg);
	}

	@Override
	public void error(Marker marker, String format, Object arg) {
		error(format, arg);
	}

	@Override
	public void error(Marker marker, String format, Object arg1, Object arg2) {
		error(format, arg1, arg2);
	}

	@Override
	public void error(Marker marker, String format, Object... arguments) {
		error(format, arguments);
	}

	@Override
	public void error(Marker marker, String msg, Throwable t) {
		error(msg, t);
	}

	/**
	 * Enqueues the specified log message.
	 *
	 * @param logLevel
	 *            the log level. Must not be null.
	 * @param msg
	 *            the log message. May be null.
	 */
	void enqueue(EgymLogLevel logLevel, String msg) {
		enqueue(logLevel, msg, null);
	}

	/**
	 * Enqueues the specified log message and throwable.
	 *
	 * @param logLevel
	 *            the log level. Must not be null.
	 * @param msg
	 *            the log message. May be null.
	 * @param t
	 *            the throwable. May be null.
	 */
	void enqueue(EgymLogLevel logLevel, String msg, Throwable t) {
		if (logLevel == null) {
			throw new IllegalArgumentException("logLevel must not be null");
		}

		// Skip everything which does not fulfill the log level threshold.
		if (!isSufficientLogLevel(logLevel)) {
			return;
		}

		final EgymLogRecord logRecord = new EgymLogRecord(DateTime.now(), this, logLevel, msg, t);

		if (logQueue == null) {
			// Fallback if log queue isn't initialized yet.
			System.out.println(EgymLogFormatterUtil.formatLogRecord(logRecord, "[NO LOG QUEUE] "));
		} else {
			logQueue.log(logRecord);
		}
	}

	/**
	 * Tests whether the given log level is equal to or exceeds the global threshold.
	 *
	 * @param logLevel
	 *            the log level to test. Must not be null.
	 * @return True if the record's log level is equal to or exceeds the global threshold.
	 */
	boolean isSufficientLogLevel(EgymLogLevel logLevel) {
		return EgymLogLevels.isSufficientLogLevel(logLevel, threshold);
	}

	/**
	 * Formats a log message with parameters.
	 *
	 * @param format
	 *            the format string / template. Must not be null.
	 * @param arg
	 *            the argument.
	 * @return the formatted log message.
	 */
	private String format(String format, Object arg) {
		return MessageFormatter.format(format, arg).getMessage();
	}

	/**
	 * Formats a log message with parameters.
	 *
	 * @param format
	 *            the format string / template. Must not be null.
	 * @param arg1
	 *            the first argument.
	 * @param arg2
	 *            the second argument.
	 * @return the formatted log message.
	 */
	private String format(String format, Object arg1, Object arg2) {
		return MessageFormatter.format(format, arg1, arg2).getMessage();
	}

	/**
	 * Formats a log message with parameters.
	 *
	 * @param format
	 *            the format string / template. Must not be null.
	 * @param args
	 *            the arguments.
	 * @return the formatted log message.
	 */
	private String format(String format, Object... args) {
		return MessageFormatter.arrayFormat(format, args).getMessage();
	}
}
