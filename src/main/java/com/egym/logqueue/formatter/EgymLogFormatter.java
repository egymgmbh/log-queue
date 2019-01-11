/**
 * This file is part of the source code and related artifacts for eGym Application.
 *
 * Copyright © 2013 eGym GmbH
 */
package com.egym.logqueue.formatter;

import com.egym.logqueue.EgymLogRequestRecord;

/**
 * Formatters are in charge of converting a request record to a format which can be processed by the writer coming next in the pipeline.
 * Typical implementations focus on human- or machine-readable output.
 * <p>
 * All implementations must be <em>thread-safe</em>. It is recommended to make implementations immutable.
 * </p>
 *
 * @param <D>
 *            the input type, ie. the decorated log record type.
 * @param <F>
 *            the output log message type.
 */
public interface EgymLogFormatter<D extends EgymLogRequestRecord, F> {
	/**
	 * Formats a decorated request record.
	 *
	 * @param requestRecord
	 *            the input. Must not be null.
	 * @return the formatted log record or null.
	 */
	F format(D requestRecord);
}
