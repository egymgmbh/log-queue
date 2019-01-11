/**
 * This file is part of the source code and related artifacts for eGym Application.
 *
 * Copyright © 2013 eGym GmbH
 */
package com.egym.logqueue;

import net.jcip.annotations.ThreadSafe;
import com.egym.logqueue.decorator.EgymLogDecorator;
import com.egym.logqueue.formatter.EgymLogFormatter;
import com.egym.logqueue.writer.EgymLogWriter;

/**
 * A pipeline defines a way of processing log records. It consists of a decorator, a formatter and a writer. All request records are passed
 * through this chain in the {@link EgymLogPipeline#log(EgymLogRequestRecord)} method. Each step in the pipeline has the power to discard
 * the log record by returning null.
 */
@ThreadSafe
class EgymLogPipeline {
	private final EgymLogDecorator logDecorator;

	private final EgymLogFormatter logFormatter;

	private final EgymLogWriter logWriter;

	/**
	 * @param logDecorator
	 *            the log decorator. Must not be null.
	 * @param logFormatter
	 *            the log formatter. Must not be null.
	 * @param logWriter
	 *            the log writer. Must not be null.
	 */
	EgymLogPipeline(final EgymLogDecorator logDecorator, final EgymLogFormatter logFormatter, final EgymLogWriter logWriter) {
		if (logDecorator == null) {
			throw new IllegalArgumentException("logDecorator must not be null");
		}
		if (logFormatter == null) {
			throw new IllegalArgumentException("logFormatter must not be null");
		}
		if (logWriter == null) {
			throw new IllegalArgumentException("logWriter must not be null");
		}

		this.logDecorator = logDecorator;
		this.logFormatter = logFormatter;
		this.logWriter = logWriter;
	}

	/**
	 * Feeds a request record into the pipeline.
	 *
	 * @param requestRecord
	 *            the record to process. Must not be null.
	 */
	void log(final EgymLogRequestRecord requestRecord) {
		if (requestRecord == null) {
			throw new IllegalArgumentException("requestRecord must not be null");
		}

		final EgymLogRequestRecord decoratedRequestLogRecord = logDecorator.decorate(requestRecord);
		if (decoratedRequestLogRecord == null) {
			return;
		}

		final Object message = logFormatter.format(decoratedRequestLogRecord);
		if (message == null) {
			return;
		}

		logWriter.write(message);
	}
}
