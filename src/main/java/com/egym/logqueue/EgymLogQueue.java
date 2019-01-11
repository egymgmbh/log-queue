/**
 * This file is part of the source code and related artifacts for eGym Application.
 *
 * Copyright © 2013 eGym GmbH
 */
package com.egym.logqueue;

/**
 * Implements per-request level logging by queuing all log records until the request ends and then producing an atomic log block for the
 * entire request. Also allows to define a threshold which, if exceeded at any given time during a request, leads to a decreased logging
 * threshold, thus providing more details when really needed. This mechanism is called the audit mode. See /README.md for details.
 */
public interface EgymLogQueue {
	/**
	 * Must be called when a request starts.
	 */
	void startRequest();

	/**
	 * Must be called when a request ends.
	 */
	void endRequest();

	/**
	 * Used to log a record.
	 *
	 * @param logRecord
	 *            the record to log. Must not be null.
	 */
	void log(EgymLogRecord logRecord);
}
