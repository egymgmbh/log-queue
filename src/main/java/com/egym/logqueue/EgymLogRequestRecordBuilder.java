/**
 * This file is part of the source code and related artifacts for eGym Application.
 *
 * Copyright © 2013 eGym GmbH
 */
package com.egym.logqueue;

import java.util.ArrayList;
import java.util.List;

import net.jcip.annotations.NotThreadSafe;

import org.joda.time.DateTime;

/**
 * This class is used while a request is in progress to collect all the request-specific log records.
 */
@NotThreadSafe
class EgymLogRequestRecordBuilder {
	/** The point in time the request started. */
	private final DateTime timestamp;

	/** The list of log records collected during the life time of the request. */
	private final List<EgymLogRecord> logRecords;

	/**
	 * @param timestamp
	 *            The point in time the request started. Must not be null.
	 */
	EgymLogRequestRecordBuilder(DateTime timestamp) {
		if (timestamp == null) {
			throw new IllegalArgumentException("timestamp must not be null");
		}

		this.timestamp = timestamp;
		this.logRecords = new ArrayList<EgymLogRecord>();
	}

	/**
	 * Adds a log record to the internal list.
	 *
	 * @param logRecord
	 *            the record to add. Must not be null.
	 */
	public void addLogRecord(final EgymLogRecord logRecord) {
		if (logRecord == null) {
			throw new IllegalArgumentException("logRecord must not be null");
		}
		logRecords.add(logRecord);
	}

	/**
	 * Called at the end of a request to create an immutable copy of this builder.
	 *
	 * @return an {@link EgymLogRequestRecord} containing the same information as this builder.
	 */
	public EgymLogRequestRecord build() {
		return new EgymLogRequestRecord(timestamp, logRecords);
	}
}
