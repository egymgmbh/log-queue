/**
 * This file is part of the source code and related artifacts for eGym Application.
 *
 * Copyright © 2013 eGym GmbH
 */
package com.egym.logqueue;

import static org.testng.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import net.jcip.annotations.NotThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Ensures that the case that no pipelines are configures at all works correctly. This case leads to a warning which is written to STDERR.
 */
@Test
@NotThreadSafe
public class NoPipelinesTest {
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
	public void testWarningMessage() {
		givenNoPipelines();
		whenNoFurtherAction();
		thenEnsureWarningMessageAndNoStdOutput();
	}

	@Test
	public void testLoggingWithoutPipeline() {
		givenNoPipelines();
		whenLoggingRequest("23", "42");
		thenEnsureWarningMessageAndNoStdOutput();
	}

	private void thenEnsureWarningMessageAndNoStdOutput() {
		final String stdErrContent = stdErrStream.toString();
		assertEquals(stdErrContent, "WARNING: You do not have any log pipelines configures. Logging will not work correctly.\n");

		final String stdOutContent = stdOutStream.toString();
		assertEquals(stdOutContent, "");
	}

	private void givenNoPipelines() {
		final Injector injector = Guice.createInjector(new EgymLogModule());
		logQueue = (EgymLogQueueImpl) injector.getInstance(EgymLogQueue.class);
	}

	private void whenNoFurtherAction() {
		// Nothing.
	}

	private void whenLoggingRequest(String... logMessages) {
		logQueue.startRequest();

		for (String logMessage : logMessages) {
			log.info(logMessage);
		}

		logQueue.endRequest();
	}
}
