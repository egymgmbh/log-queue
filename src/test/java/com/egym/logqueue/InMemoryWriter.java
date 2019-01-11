/**
 * This file is part of the source code and related artifacts for eGym Application.
 *
 * Copyright © 2013 eGym GmbH
 */
package com.egym.logqueue;

import java.util.ArrayList;
import java.util.List;

import com.egym.logqueue.writer.EgymLogWriter;
import net.jcip.annotations.ThreadSafe;

import com.google.inject.Singleton;

/**
 * Simple writer implementation which is collecting all log records in a list.
 */
@Singleton
@ThreadSafe
class InMemoryWriter implements EgymLogWriter<String> {
	private final List<String> logMessages = new ArrayList<>();

	@Override
	public synchronized void write(String logMessage) {
		if (logMessage != null) {
			logMessages.add(logMessage);
		}
	}

	public synchronized List<String> getLogMessages() {
		return new ArrayList<>(logMessages);
	}
}
