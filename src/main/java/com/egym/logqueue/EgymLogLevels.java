/**
 * This file is part of the source code and related artifacts for eGym Application.
 *
 * Copyright © 2013 eGym GmbH
 */
package com.egym.logqueue;

/**
 * {@link EgymLogLevel} related utilities.
 */
public class EgymLogLevels {
	/** The default log level threshold for everything happening <em>outside of a request</em>. */
	private static final EgymLogLevel THRESHOLD_DEFAULT = EgymLogLevel.INFO;

	/** The default log level threshold for everything happening <em>in a request</em>. */
	private static final EgymLogLevel THRESHOLD_REQUEST = EgymLogLevel.INFO;

	/** The threshold within a request which triggers the <em>audit mode</em>. See /README.md for details. */
	private static final EgymLogLevel THRESHOLD_REQUEST_AUDIT = EgymLogLevel.WARN;

	private EgymLogLevels() {
		throw new AssertionError("Do not instantiate");
	}

	/**
	 * Tests whether the given log record is equal to or exceeds the specified log threshold.
	 *
	 * @param logRecord
	 *            the log record to test. Must not be null.
	 * @param thresholdLogLevel
	 *            the threshold. Must not be null.
	 * @return True if the record's log level is equal to or exceeds the threshold.
	 */
	public static boolean hasSufficientLogLevel(EgymLogRecord logRecord, EgymLogLevel thresholdLogLevel) {
		if (logRecord == null) {
			throw new IllegalArgumentException("logRecord");
		}
		if (logRecord.getLogLevel() == null) {
			throw new IllegalArgumentException("logRecord.logLevel");
		}

		return isSufficientLogLevel(logRecord.getLogLevel(), thresholdLogLevel);
	}

	/**
	 * Tests whether the given log level is equal to or exceeds the specified log threshold.
	 *
	 * @param logLevel
	 *            the log level to test. Must not be null.
	 * @param thresholdLogLevel
	 *            the threshold. Must not be null.
	 * @return True if the record's log level is equal to or exceeds the threshold.
	 */
	public static boolean isSufficientLogLevel(EgymLogLevel logLevel, EgymLogLevel thresholdLogLevel) {
		if (logLevel == null) {
			throw new IllegalArgumentException("logLevel");
		}
		if (thresholdLogLevel == null) {
			throw new IllegalArgumentException("thresholdLogLevel");
		}

		return logLevel.ordinal() >= thresholdLogLevel.ordinal();
	}

	/**
	 * Calculates the maximum log level which appears in the log messages of the specified request descriptor. If the descriptor contains no
	 * log message, the lowest log level (TRACE) is returned.
	 *
	 * @param requestLogRecord
	 *            the request descriptor to analyze. Must not be null.
	 * @return the maximum log level discovered. Never null.
	 */
	public static EgymLogLevel calcMaxLogLevel(EgymLogRequestRecord requestLogRecord) {
		int maxLogLevelOrdinal = 0;

		if (requestLogRecord.getLogRecords() != null) {
			for (EgymLogRecord logRecord : requestLogRecord.getLogRecords()) {
				final EgymLogLevel logLevel = logRecord.getLogLevel();

				final int logLevelOrdinal = logLevel.ordinal();
				maxLogLevelOrdinal = Math.max(maxLogLevelOrdinal, logLevelOrdinal);
			}
		}

		return EgymLogLevel.values()[maxLogLevelOrdinal];
	}

	/**
	 * @return The default log level threshold for everything happening <em>outside of a request</em>.
	 */
	public static EgymLogLevel getThresholdDefault() {
		return THRESHOLD_DEFAULT;
	}

	/**
	 * @return The default log level threshold for everything happening <em>in a request</em>.
	 */
	public static EgymLogLevel getThresholdRequest() {
		return THRESHOLD_REQUEST;
	}

	/**
	 * @return The threshold within a request which triggers the <em>audit mode</em>. See /README.md for details.
	 */
	public static EgymLogLevel getThresholdRequestAudit() {
		return THRESHOLD_REQUEST_AUDIT;
	}
}
