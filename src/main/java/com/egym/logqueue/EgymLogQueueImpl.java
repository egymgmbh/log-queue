/**
 * This file is part of the source code and related artifacts for eGym Application.
 *
 * Copyright © 2013 eGym GmbH
 */
package com.egym.logqueue;

import java.util.List;

import net.jcip.annotations.ThreadSafe;

import org.joda.time.DateTime;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
@ThreadSafe
class EgymLogQueueImpl implements EgymLogQueue {
	/** Keeps track of the per-thread request log builders. This is possible because each thread processes only one request at a time. */
	private static final ThreadLocal<EgymLogRequestRecordBuilder> threadRequestLogRecordBuilder = new ThreadLocal<EgymLogRequestRecordBuilder>();

	/** All configured logging pipelines. */
	private final List<EgymLogPipeline> pipelines;

	@Inject
	EgymLogQueueImpl(final EgymLogPipelineService pipelineService) {
		this.pipelines = pipelineService.createPipelines();
		pipelineSanityCheck();
	}

	@Override
	public void startRequest() {
		final EgymLogRequestRecordBuilder requestDescriptor = new EgymLogRequestRecordBuilder(DateTime.now());
		threadRequestLogRecordBuilder.set(requestDescriptor);
	}

	@Override
	public void endRequest() {
		final EgymLogRequestRecordBuilder requestRecordBuilder = threadRequestLogRecordBuilder.get();
		if (requestRecordBuilder == null) {
			throw new IllegalStateException("No active request. You need to call startRequest() first.");
		}

		try {
			final EgymLogRequestRecord requestLogRecord = requestRecordBuilder.build();
			flush(requestLogRecord);
			threadRequestLogRecordBuilder.remove();
		} catch (Exception e) {
			handleInternalLoggingFailure(e);
		}
	}

	@Override
	public void log(EgymLogRecord logRecord) {
		try {
			logInternal(logRecord);
		} catch (Exception e) {
			handleInternalLoggingFailure(e);
		}
	}

	/**
	 * Handles logging internally by appending the record to the currently active request record builder or by delegating it to the
	 * no-request path.
	 *
	 * @param logRecord
	 *            the record to log. Must not be null.
	 */
	private void logInternal(EgymLogRecord logRecord) {
		if (logRecord == null) {
			throw new IllegalArgumentException("logRecord must not be null");
		}

		final EgymLogRequestRecordBuilder requestRecordBuilder = threadRequestLogRecordBuilder.get();
		if (requestRecordBuilder == null) {
			// Print directly if not in a request.
			logWithoutRequest(logRecord);
		} else {
			// Otherwise the log record is added to the request specific queue.
			requestRecordBuilder.addLogRecord(logRecord);
		}
	}

	/**
	 * Logs a record without having a request context.
	 *
	 * @param logRecord
	 *            the record to log. Must not be null.
	 */
	private void logWithoutRequest(EgymLogRecord logRecord) {
		if (logRecord == null) {
			throw new IllegalArgumentException("logRecord must not be null");
		}

		// Ignore it if log level is too low.
		if (!EgymLogLevels.isSufficientLogLevel(logRecord.getLogLevel(), EgymLogLevels.getThresholdDefault())) {
			return;
		}

		final EgymLogRequestRecordBuilder requestRecordBuilder = new EgymLogRequestRecordBuilder(logRecord.getTimestamp());
		requestRecordBuilder.addLogRecord(logRecord);
		flush(requestRecordBuilder.build());
	}

	/**
	 * Flushes the request record by sending it into the pipelines.
	 *
	 * @param requestRecord
	 *            the record to flush. Must not be null.
	 */
	private void flush(EgymLogRequestRecord requestRecord) {
		if (requestRecord == null) {
			throw new IllegalArgumentException("requestRecord must not be null");
		}

		for (EgymLogPipeline pipeline : pipelines) {
			pipeline.log(requestRecord);
		}
	}

	/**
	 * Worst-case scenario: An exception occurs while logging. All we can do now is to fall back to stderr to avoid any further issues.
	 */
	private void handleInternalLoggingFailure(Exception e) {
		if (e == null) {
			return;
		}

		try {
			e.printStackTrace();
		} catch (Throwable t) {
			// If our worst case exception reporting fails with an exception there's not much we can do about it...
		}
	}

	/**
	 * Verifies that there is at least one pipeline and prints a warning if this condition is not met.
	 */
	private void pipelineSanityCheck() {
		if (pipelines == null || pipelines.isEmpty()) {
			System.err.println("WARNING: You do not have any log pipelines configures. Logging will not work correctly.");
		}

		for (EgymLogPipeline pipeline : pipelines) {
			if (pipeline == null) {
				throw new IllegalStateException("pipelines must not contain null entries");
			}
		}
	}
}
