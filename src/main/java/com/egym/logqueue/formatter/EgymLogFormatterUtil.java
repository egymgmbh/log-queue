/**
 * This file is part of the source code and related artifacts for eGym Application.
 *
 * Copyright © 2013 eGym GmbH
 */
package com.egym.logqueue.formatter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.egym.logqueue.EgymLogRecord;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.egym.logqueue.EgymLogLevel;

/**
 * Utilities used to format log messages.
 */
public class EgymLogFormatterUtil {
	private static final DateTimeFormatter FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");

	private static final Map<EgymLogLevel, String> logLevelLabels = createLogLevelLabels();

	private EgymLogFormatterUtil() {
		throw new AssertionError("Do not instantiate");
	}

	/**
	 * Formats the given {@link EgymLogRecord} in a nice, structured, human readable format. Uses the specified
	 * indentation string to indent the log message. All lines except the first one are indented by twice the indentation string.
	 *
	 * @param logRecord
	 *            the record to format. Must not be null.
	 * @param indentation
	 *            The indentation to use. Must not be null.
	 * @return the formatted log message.
	 */
	public static String formatLogRecord(final EgymLogRecord logRecord, final String indentation) {
		if (logRecord == null) {
			throw new IllegalArgumentException("logRecord must not be null");
		}
		if (indentation == null) {
			throw new IllegalArgumentException("indentation must not be null");
		}

		final StringBuilder str = new StringBuilder();

		str.append(indentation);
		str.append(formatTimestamp(logRecord.getTimestamp()));
		str.append(" ");
		str.append(formatLogLevel(logRecord.getLogLevel()));

		if (logRecord.getSource() != null) {
			str.append(" ");
			str.append(logRecord.getSource().getName());
		}

		str.append(": ");

		if (logRecord.getMessage() != null) {
			final String[] lines = logRecord.getMessage().split("\n");
			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];
				if (i > 0) {
					str.append('\n');
					str.append(indentation);
					str.append(indentation);
				}
				str.append(line);
			}
		}

		if (logRecord.getThrowable() != null) {
			final String stackTrace = ExceptionUtils.getFullStackTrace(logRecord.getThrowable());
			final String[] lines = stackTrace.split("\n");

			for (String line : lines) {
				str.append('\n');
				str.append(indentation);
				str.append(indentation);
				str.append(line);
			}
		}

		return str.toString();
	}

	/**
	 * Formats the given log level.
	 *
	 * @param logLevel
	 *            must not be null.
	 * @return the formatted log level.
	 */
	public static String formatLogLevel(EgymLogLevel logLevel) {
		if (logLevel == null) {
			throw new IllegalArgumentException("logLevel must not be null");
		}

		final String logLevelLabel = logLevelLabels.get(logLevel);
		if (logLevelLabel == null) {
			throw new AssertionError("Unknown logLevel: " + logLevel);
		}

		return logLevelLabel;
	}

	/**
	 * @return An unmodifiable map containing a label for each {@link EgymLogLevel} value. All labels have exactly the same length. Shorter
	 *         labels are prefixed with spaces to have the same length as the longest label.
	 */
	private static Map<EgymLogLevel, String> createLogLevelLabels() {
		final Map<EgymLogLevel, String> logLevelLabels = new HashMap<EgymLogLevel, String>();

		final int maxLength = calcMaxLogLevelNameLength();

		for (EgymLogLevel logLevel : EgymLogLevel.values()) {
			final String label = formatLogLevelLabel(logLevel, maxLength);
			logLevelLabels.put(logLevel, label);
		}

		return Collections.unmodifiableMap(logLevelLabels);
	}

	/**
	 * @return The length of the longest {@link EgymLogLevel} value name.
	 */
	private static int calcMaxLogLevelNameLength() {
		int maxLength = 0;
		for (EgymLogLevel logLevel : EgymLogLevel.values()) {
			maxLength = Math.max(logLevel.name().length(), maxLength);
		}
		return maxLength;
	}

	/**
	 * Formats a {@link EgymLogLevel} value's name. Adds a prefix consisting of whitespaces if the value's name is shorter than length.
	 *
	 * @param logLevel
	 *            the value to format. Must not be null.
	 * @param length
	 *            the output length. Must not be smaller than the logLevel name's length.
	 * @return the formatted log level label.
	 */
	private static String formatLogLevelLabel(EgymLogLevel logLevel, int length) {
		if (logLevel == null) {
			throw new IllegalArgumentException("logLevel must not be null");
		}
		if (length < 0) {
			throw new IllegalArgumentException("length must not be negative but is: " + length);
		}

		final String logLevelName = logLevel.name();
		final int requiredPadding = length - logLevelName.length();
		if (requiredPadding < 0) {
			throw new IllegalArgumentException("logLevelName.length(" + logLevelName.length() + ") exceeds length(" + length + ")");
		}

		StringBuilder str = new StringBuilder();

		// Add padding.
		for (int i = 0; i < requiredPadding; i++) {
			str.append(' ');
		}

		str.append(logLevelName);
		return str.toString();
	}

	/**
	 * Formats the given timestamp.
	 *
	 * @param timestamp
	 *            must not be null.
	 * @return the string representation of the timestamp.
	 */
	public static String formatTimestamp(DateTime timestamp) {
		if (timestamp == null) {
			throw new IllegalArgumentException("timestamp must not be null");
		}
		return FORMAT.print(timestamp);
	}
}
