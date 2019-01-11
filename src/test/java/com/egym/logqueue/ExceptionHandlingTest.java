/**
 * This file is part of the source code and related artifacts for eGym Application.
 *
 * Copyright © 2013 eGym GmbH
 */
package com.egym.logqueue;

import static org.testng.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import com.egym.logqueue.config.EgymLogPipelineModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import com.egym.logqueue.formatter.EgymLogPlainTextFormatter;

/**
 * Ensures that the framework handles internal exceptions properly.
 */
@Test
public class ExceptionHandlingTest {
	/**
	 * The test configuration.
	 */
	private static class EgymTestLogPipelineModule extends EgymLogPipelineModule {
		@Override
		protected void configure() {
			skipDecoration().formatWith(EgymLogPlainTextFormatter.class).writeTo(FaultyWriter.class);
		}
	}

	private static final Logger log = LoggerFactory.getLogger(EgymLogQueueImpl.class);

	private EgymLogQueueImpl logQueue;

	private ByteArrayOutputStream stdErrStream;

	private ByteArrayOutputStream stdOutStream;

	/**
	 * Redirect stdout/stderr to our own streams.
	 */
	@BeforeMethod
	private void init() {
		stdErrStream = new ByteArrayOutputStream();
		stdOutStream = new ByteArrayOutputStream();
		System.setErr(new PrintStream(stdErrStream));
		System.setOut(new PrintStream(stdOutStream));
	}

	/**
	 * Reset stdout/stderr.
	 */
	@AfterMethod
	public void cleanup() {
		System.setErr(null);
		System.setOut(null);
	}

	@Test
	public void testExceptionHandling() {
		givenPipelineConfiguration();
		whenLoggingRequest("Fizz", "Buzz");
		thenEnsureExceptionGetsLoggedToStdErr();
	}

	private void thenEnsureExceptionGetsLoggedToStdErr() {
		final String stdErrContent = stdErrStream.toString();
		final String stdOutContent = stdOutStream.toString();

		// Print for diagnostic purposes.
		System.err.println(stdErrContent);
		System.out.println(stdOutContent);

		assertTrue(stdErrContent.length() > 0);
		assertTrue(stdErrContent.contains("RuntimeException"));

		assertEquals(stdOutContent, "");
	}

	private void givenPipelineConfiguration() {
		final Injector injector = Guice.createInjector(new EgymLogModule(), new EgymTestLogPipelineModule());
		logQueue = (EgymLogQueueImpl) injector.getInstance(EgymLogQueue.class);
	}

	private void whenLoggingRequest(String... logMessages) {
		logQueue.startRequest();

		for (String logMessage : logMessages) {
			log.info(logMessage);
		}

		logQueue.endRequest();
	}
}
