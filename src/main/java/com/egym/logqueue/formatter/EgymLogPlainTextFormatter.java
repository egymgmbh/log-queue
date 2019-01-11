/**
 * This file is part of the source code and related artifacts for eGym Application.
 *
 * Copyright © 2013 eGym GmbH
 */
package com.egym.logqueue.formatter;

import com.egym.logqueue.EgymLogLevel;
import com.egym.logqueue.EgymLogLevels;
import com.egym.logqueue.EgymLogRecord;
import com.egym.logqueue.EgymLogRequestRecord;
import net.jcip.annotations.ThreadSafe;

import com.google.inject.Singleton;

/**
 * Produces human readable plain-text log output. All log records of a request are printed as a block, indented with tabs.
 */
@Singleton
@ThreadSafe
public class EgymLogPlainTextFormatter implements EgymLogFormatter<EgymLogRequestRecord, String> {
	@Override
	public String format(EgymLogRequestRecord requestRecord) {
		if (requestRecord == null) {
			throw new IllegalArgumentException("requestLogRecord must not be null");
		}

		final EgymLogLevel maxLogLevel = EgymLogLevels.calcMaxLogLevel(requestRecord);
		final boolean audit = EgymLogLevels.isSufficientLogLevel(maxLogLevel, EgymLogLevels.getThresholdRequestAudit());

		if (requestRecord.getLogRecords() == null || requestRecord.getLogRecords().isEmpty()) {
			return "";
		}

		final StringBuilder str = new StringBuilder();

		for (EgymLogRecord logRecord : requestRecord.getLogRecords()) {
			if (audit || EgymLogLevels.hasSufficientLogLevel(logRecord, EgymLogLevels.getThresholdRequest())) {
				str.append(EgymLogFormatterUtil.formatLogRecord(logRecord, "\t"));
				str.append("\n");
			}
		}

		return str.toString();
	}
}
