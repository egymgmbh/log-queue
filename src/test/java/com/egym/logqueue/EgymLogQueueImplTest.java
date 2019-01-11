/**
 * This file is part of the source code and related artifacts for eGym Application.
 *
 * Copyright © 2013 eGym GmbH
 */
package com.egym.logqueue;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import com.egym.logqueue.config.EgymLogPipelineModule;
import com.egym.logqueue.decorator.EgymLogNoOpDecorator;
import com.egym.logqueue.formatter.EgymLogPlainTextFormatter;
import com.egym.logqueue.slf4j.EgymLogger;
import com.egym.logqueue.writer.EgymLogWriter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

@Test
public class EgymLogQueueImplTest {
	private static final String LOGGER_NAME = "FooLogger";

	@Mock
	private EgymLogWriter logWriter;

	@Mock
	private EgymLogger logger;

	private Injector injector;

	private EgymLogQueue logQueue;



	@BeforeMethod
	public void init() {
		DateTimeZone.setDefault(DateTimeZone.UTC);
		MockitoAnnotations.initMocks(this);

		injector = Guice.createInjector(new EgymLogModule(), new EgymLogPipelineModule() {
			@Override
			protected void configure() {
				decorateWith(EgymLogNoOpDecorator.class).formatWith(EgymLogPlainTextFormatter.class).writeTo(InMemoryWriter.class);
			}
		});

		when(logger.getName()).thenReturn(LOGGER_NAME);
		logQueue = injector.getInstance(EgymLogQueue.class);
	}

	@AfterMethod
	public void cleanup() {
		DateTimeUtils.setCurrentMillisSystem();
	}

	/**
	 * Tests the standard behavior without a request context.
	 */
	@Test
	public void testWithoutRequest() {
		final DateTime timestamp = new DateTime(2013, 12, 15, 17, 23, 42);
		final EgymLogRecord logRecord = new EgymLogRecord(timestamp, logger, EgymLogLevel.INFO, "Hello World", null);
		logQueue.log(logRecord);

		verify(logWriter).write("2013-12-15 17:23:42.000  INFO FooLogger: Hello World");
	}

	/**
	 * Tests the standard behavior in a request context.
	 */
	@Test
	public void testWithRequest() {
		DateTimeUtils.setCurrentMillisFixed(1387139060000L);

		logQueue.startRequest();

		final DateTime timestamp = new DateTime(2013, 12, 15, 17, 23, 42);
		logQueue.log(new EgymLogRecord(timestamp, logger, EgymLogLevel.INFO, "Hello World", null));
		logQueue.log(new EgymLogRecord(timestamp, logger, EgymLogLevel.INFO, "Good Bye World", null));

		verify(logWriter, never()).write(anyString());

		logQueue.endRequest();

		final ArgumentCaptor<String> logMsgCaptor = ArgumentCaptor.forClass(String.class);
		verify(logWriter).write(logMsgCaptor.capture());

		final String logMsg = logMsgCaptor.getValue();
		System.out.println(logMsg);

		final String[] lines = logMsg.split("\n");
		assertEquals(lines.length, 3);
		assertEquals(lines[0], "Request 2013-12-15 20:24:20.000 Some Header");
		assertEquals(lines[1], "\t2013-12-15 17:23:42.000  INFO FooLogger: Hello World");
		assertEquals(lines[2], "\t2013-12-15 17:23:42.000  INFO FooLogger: Good Bye World");
	}

	/**
	 * Ensures that log messages below INFO are not logged per default.
	 */
	@Test
	public void testWithRequestDebug() {
		DateTimeUtils.setCurrentMillisFixed(1387139060000L);

		logQueue.startRequest();

		final DateTime timestamp = new DateTime(2013, 12, 15, 17, 23, 42);
		logQueue.log(new EgymLogRecord(timestamp, logger, EgymLogLevel.INFO, "Hello World", null));
		logQueue.log(new EgymLogRecord(timestamp, logger, EgymLogLevel.DEBUG, "Good Bye World", null));

		verify(logWriter, never()).write(anyString());

		logQueue.endRequest();

		final ArgumentCaptor<String> logMsgCaptor = ArgumentCaptor.forClass(String.class);
		verify(logWriter).write(logMsgCaptor.capture());

		final String logMsg = logMsgCaptor.getValue();
		System.out.println(logMsg);

		// Make sure the DEBUG log message is not logged.
		final String[] lines = logMsg.split("\n");
		assertEquals(lines.length, 2);
		assertEquals(lines[0], "Request 2013-12-15 20:24:20.000 Some Header");
		assertEquals(lines[1], "\t2013-12-15 17:23:42.000  INFO FooLogger: Hello World");
	}

	/**
	 * Ensures that a log messages with level WARN triggers the audit mode which then also includes log messages with levels below INFO.
	 */
	@Test
	public void testWithRequestAudit() {
		DateTimeUtils.setCurrentMillisFixed(1387139060000L);

		logQueue.startRequest();

		final DateTime timestamp = new DateTime(2013, 12, 15, 17, 23, 42);
		logQueue.log(new EgymLogRecord(timestamp, logger, EgymLogLevel.INFO, "Hello World", null));
		logQueue.log(new EgymLogRecord(timestamp, logger, EgymLogLevel.DEBUG, "Good Bye World", null));
		logQueue.log(new EgymLogRecord(timestamp, logger, EgymLogLevel.WARN, "In soviet russia bug hunts you!", null));

		verify(logWriter, never()).write(anyString());

		logQueue.endRequest();

		final ArgumentCaptor<String> logMsgCaptor = ArgumentCaptor.forClass(String.class);
		verify(logWriter).write(logMsgCaptor.capture());

		final String logMsg = logMsgCaptor.getValue();
		System.out.println(logMsg);

		// Due to the audit triggered by the WARN log message, the DEBUG log message must not be included.
		final String[] lines = logMsg.split("\n");
		assertEquals(lines.length, 4);
		assertEquals(lines[0], "Request 2013-12-15 20:24:20.000 Some Header");
		assertEquals(lines[1], "\t2013-12-15 17:23:42.000  INFO FooLogger: Hello World");
		assertEquals(lines[2], "\t2013-12-15 17:23:42.000 DEBUG FooLogger: Good Bye World");
		assertEquals(lines[3], "\t2013-12-15 17:23:42.000  WARN FooLogger: In soviet russia bug hunts you!");
	}
}
