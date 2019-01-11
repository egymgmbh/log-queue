/**
 * This file is part of the source code and related artifacts for eGym Application.
 *
 * Copyright © 2013 eGym GmbH
 */
package com.egym.logqueue.writer;

import net.jcip.annotations.ThreadSafe;

import com.google.inject.Singleton;

/**
 * Writes all log messages to stdout.
 */
@Singleton
@ThreadSafe
public class EgymLogStdOutWriter implements EgymLogWriter<String> {
	@Override
	public void write(String msg) {
		if (msg != null) {
			System.out.println(msg);
		}
	}
}
