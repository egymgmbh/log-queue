/**
 * This file is part of the source code and related artifacts for eGym Application.
 *
 * Copyright © 2013 eGym GmbH
 */
package com.egym.logqueue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.jcip.annotations.Immutable;

import org.joda.time.DateTime;

/**
 * Contains all information collected about a request.
 */
@Immutable
public class EgymLogRequestRecord {
	/** The point in time the request started. */
	private final DateTime timestamp;

	/** The list of log records collected during the life time of the request. */
	private final List<EgymLogRecord> logRecords;

	/**
	 * @param timestamp
	 *            The point in time the request started. Must not be null.
	 * @param logRecords
	 *            The list of log records collected during the life time of the request. Must not be null. Must not contain null entries.
	 */
	EgymLogRequestRecord(DateTime timestamp, List<EgymLogRecord> logRecords) {
		if (timestamp == null) {
			throw new IllegalArgumentException("timestmap must not be null");
		}
		if (logRecords == null) {
			throw new IllegalArgumentException("logRecords must not be null");
		}

		this.timestamp = timestamp;
		// Create an immutable copy to ensure thread-safety.
		this.logRecords = Collections.unmodifiableList(new ArrayList<EgymLogRecord>(logRecords));
	}

	/**
	 * Copy constructor.
	 *
	 * @param requestRecord
	 *            the instance to copy. Must not be null.
	 */
	public EgymLogRequestRecord(EgymLogRequestRecord requestRecord) {
		this(requestRecord.getTimestamp(), requestRecord.getLogRecords());
	}

	/**
	 * @return The point in time the request started. Never null.
	 */
	public DateTime getTimestamp() {
		return timestamp;
	}

	/**
	 * @return The <em>immutable</em> list of log records collected during the life time of the request. Never null. Never contains null
	 *         entries.
	 */
	public List<EgymLogRecord> getLogRecords() {
		// Can be safely returned due to immutability.
		return logRecords;
	}
}
