/**
 * This file is part of the source code and related artifacts for eGym Application.
 *
 * Copyright © 2013 eGym GmbH
 */
package com.egym.logqueue.slf4j;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import com.egym.logqueue.EgymLogLevel;
import com.egym.logqueue.EgymLogQueue;
import com.egym.logqueue.EgymLogRecord;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class EgymLoggerTest {
	@Mock
	private EgymLogQueue logQueue;

	private EgymLogger logger;

	@BeforeMethod
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testNoQueue() {
		logger = new EgymLogger("foo");
		logger.info("Hello World");
	}

	@Test
	public void testWithQueue() {
		final DateTime now = DateTime.now();
		final String message = "Hello World";

		logger = new EgymLogger("foo");
		EgymLogger.logQueue = logQueue;
		logger.info(message);

		final ArgumentCaptor<EgymLogRecord> logRecordCaptor = ArgumentCaptor.forClass(EgymLogRecord.class);
		verify(logQueue).log(logRecordCaptor.capture());

		final EgymLogRecord logRecord = logRecordCaptor.getValue();
		assertEquals(logRecord.getLogLevel(), EgymLogLevel.INFO);
		assertEquals(logRecord.getMessage(), message);
		assertEquals(logRecord.getSource(), logger);

		final int timeDiff = Seconds.secondsBetween(now, logRecord.getTimestamp()).getSeconds();
		if (timeDiff < 0 || timeDiff > 5) {
			throw new AssertionError("Timestamp out of range: timeDiff=" + timeDiff);
		}
	}

	@Test
	public void testIsSufficientLogLevel() {
		assertEquals(new EgymLogger("foo").isSufficientLogLevel(EgymLogLevel.TRACE), false);
		assertEquals(new EgymLogger("foo").isSufficientLogLevel(EgymLogLevel.DEBUG), true);
		assertEquals(new EgymLogger("foo").isSufficientLogLevel(EgymLogLevel.INFO), true);
		assertEquals(new EgymLogger("foo").isSufficientLogLevel(EgymLogLevel.WARN), true);
		assertEquals(new EgymLogger("foo").isSufficientLogLevel(EgymLogLevel.ERROR), true);
	}

	@Test
	public void testIsSufficientLogLevelForHibernate() {
		assertEquals(new EgymLogger("org.hibernate.foo").isSufficientLogLevel(EgymLogLevel.TRACE), false);
		assertEquals(new EgymLogger("org.hibernate.foo").isSufficientLogLevel(EgymLogLevel.DEBUG), false);
		assertEquals(new EgymLogger("org.hibernate.bar").isSufficientLogLevel(EgymLogLevel.INFO), true);
		assertEquals(new EgymLogger("org.hibernate.bar").isSufficientLogLevel(EgymLogLevel.WARN), true);
		assertEquals(new EgymLogger("org.hibernate.bar").isSufficientLogLevel(EgymLogLevel.ERROR), true);
	}

	@Test
	public void testMessageFormattingWithOneArgument() {
		logger = new EgymLogger("foo");
		EgymLogger.logQueue = logQueue;

		logger.debug("format string with arg1={}", "value1");

		ArgumentCaptor<EgymLogRecord> argument = ArgumentCaptor.forClass(EgymLogRecord.class);
		verify(logQueue).log(argument.capture());

		EgymLogRecord logRecord = argument.getValue();
		assertEquals(logRecord.getMessage(), "format string with arg1=value1");
	}

	@Test
	public void testMessageFormattingWithTwoArguments() {
		logger = new EgymLogger("foo");
		EgymLogger.logQueue = logQueue;

		logger.debug("format string with arg1={} and arg2={}", "value1", "value2");

		ArgumentCaptor<EgymLogRecord> logRecordCaptor = ArgumentCaptor.forClass(EgymLogRecord.class);
		verify(logQueue).log(logRecordCaptor.capture());

		EgymLogRecord logRecord = logRecordCaptor.getValue();
		assertEquals(logRecord.getMessage(), "format string with arg1=value1 and arg2=value2");
	}

	@Test
	public void testMessageFormattingWithThreeArguments() {
		logger = new EgymLogger("foo");
		EgymLogger.logQueue = logQueue;

		logger.debug("format string with arg1={} and arg2={} and arg3={}", "value1", "value2", "value3");

		ArgumentCaptor<EgymLogRecord> logRecordCaptor = ArgumentCaptor.forClass(EgymLogRecord.class);
		verify(logQueue).log(logRecordCaptor.capture());

		EgymLogRecord logRecord = logRecordCaptor.getValue();
		assertEquals(logRecord.getMessage(), "format string with arg1=value1 and arg2=value2 and arg3=value3");
	}
}
