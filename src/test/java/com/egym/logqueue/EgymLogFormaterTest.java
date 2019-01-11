/**
 * This file is part of the source code and related artifacts for eGym Application.
 *
 * Copyright © 2013 eGym GmbH
 */
package com.egym.logqueue;

import static org.testng.Assert.*;

import com.egym.logqueue.formatter.EgymLogFormatterUtil;
import org.joda.time.DateTime;
import org.testng.annotations.Test;

@Test
public class EgymLogFormaterTest {
	@Test
	public void testFormatLogRecord() {
		testFormatLogRecord(new EgymLogRecord(DateTime.now(), null, EgymLogLevel.TRACE, "Hello\nWorld", null));
		testFormatLogRecord(new EgymLogRecord(DateTime.now(), null, EgymLogLevel.DEBUG, "Hello World", null));
		testFormatLogRecord(new EgymLogRecord(DateTime.now(), null, EgymLogLevel.INFO, "Hello World", null));
		testFormatLogRecord(new EgymLogRecord(DateTime.now(), null, EgymLogLevel.WARN, "Hello World", null));
		testFormatLogRecord(new EgymLogRecord(DateTime.now(), null, EgymLogLevel.ERROR, "Hello World", null));
		testFormatLogRecord(new EgymLogRecord(DateTime.now(), null, EgymLogLevel.ERROR, "Hello World", null));
		testFormatLogRecord(new EgymLogRecord(DateTime.now(), null, EgymLogLevel.ERROR, "Hello World", catchAndReturn()));
		testFormatLogRecord(new EgymLogRecord(DateTime.now(), null, EgymLogLevel.ERROR, "Hello World", null));
		testFormatLogRecord(new EgymLogRecord(DateTime.now(), null, EgymLogLevel.ERROR, "Hello World", null));
		testFormatLogRecord(new EgymLogRecord(DateTime.now(), null, EgymLogLevel.ERROR, "Hello World", null));
		testFormatLogRecord(new EgymLogRecord(DateTime.now(), null, EgymLogLevel.ERROR, "Hello World", null));
		testFormatLogRecord(new EgymLogRecord(DateTime.now(), null, EgymLogLevel.ERROR, "Hello World", null));
		testFormatLogRecord(new EgymLogRecord(DateTime.now(), null, EgymLogLevel.ERROR, "Hello World", null));
		testFormatLogRecord(new EgymLogRecord(DateTime.now(), null, EgymLogLevel.ERROR, "Hello World", null));
	}

	@Test
	public void testFormatTimestamp() {
		final DateTime timestamp = new DateTime(2013, 12, 15, 17, 23, 42, 73);
		final String str = EgymLogFormatterUtil.formatTimestamp(timestamp);

		assertEquals(str, "2013-12-15 17:23:42.073");
	}

	@Test
	public void testFormatLogLevel1() {
		testFormatLogLevel(EgymLogLevel.TRACE, "TRACE");
		testFormatLogLevel(EgymLogLevel.DEBUG, "DEBUG");
		testFormatLogLevel(EgymLogLevel.INFO, " INFO");
		testFormatLogLevel(EgymLogLevel.WARN, " WARN");
		testFormatLogLevel(EgymLogLevel.ERROR, "ERROR");
	}

	/**
	 * Ensures that all defined {@link EgymLogLevel} values are handled correctly.
	 */
	@Test
	public void testFormatLogLevel2() {
		for (EgymLogLevel logLevel : EgymLogLevel.values()) {
			assertNotNull(EgymLogFormatterUtil.formatLogLevel(logLevel));
		}
	}

	private void testFormatLogLevel(EgymLogLevel logLevel, String expectedLabel) {
		final String label = EgymLogFormatterUtil.formatLogLevel(logLevel);
		assertEquals(label, expectedLabel);
	}

	private void testFormatLogRecord(EgymLogRecord logRecord) {
		final String output = EgymLogFormatterUtil.formatLogRecord(logRecord, "\t");
		System.out.println(output);
	}

	/**
	 * Calls a method which throws an exception, catches the exception and returns it.
	 *
	 * @return the caught exception.
	 */
	private Throwable catchAndReturn() {
		try {
			throw1();
		} catch (Throwable t) {
			return t;
		}

		return null;
	}

	/**
	 * Calls a method which throws an exception, catches it and wraps it in another exception.
	 */
	private void throw1() {
		try {
			throw2();
		} catch (Exception e) {
			throw new RuntimeException("This should actually happen", e);
		}
	}

	/**
	 * Throws an exception.
	 */
	private void throw2() {
		throw new RuntimeException("Look ma, an exception!");
	}
}
