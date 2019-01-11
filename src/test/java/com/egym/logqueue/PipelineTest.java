/**
 * This file is part of the source code and related artifacts for eGym Application.
 *
 * Copyright © 2013 eGym GmbH
 */
package com.egym.logqueue;

import static org.testng.Assert.*;

import java.util.List;

import com.egym.logqueue.config.EgymLogPipelineModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import com.egym.logqueue.formatter.EgymLogPlainTextFormatter;

/**
 * Tests the pipeline configuration and execution mechanism.
 */
@Test
public class PipelineTest {
	/**
	 * The test configuration.
	 */
	private static class EgymTestLogPipelineModule extends EgymLogPipelineModule {
		@Override
		protected void configure() {
			skipDecoration().formatWith(EgymLogPlainTextFormatter.class).writeTo(InMemoryWriter.class);
		}
	}

	private static final Logger log = LoggerFactory.getLogger(EgymLogQueueImpl.class);

	private EgymLogQueueImpl logQueue;

	private InMemoryWriter writer;

	/**
	 * Tests a pipeline configuration.
	 */
	@Test
	public void testWithRequest1() {
		givenPipelineConfiguration();
		whenLoggingRequest("Foo", "Bar");
		thenEnsureProperRequestOutput("Foo", "Bar");
	}

	/**
	 * Tests a pipeline configuration.
	 */
	@Test
	public void testWithRequest2() {
		givenPipelineConfiguration();
		whenLoggingRequest("Moerp");
		thenEnsureProperRequestOutput("Moerp");
	}

	/**
	 * Tests a pipeline configuration.
	 */
	@Test
	public void testWithRequest3() {
		givenPipelineConfiguration();
		whenLoggingRequest();
		thenEnsureProperRequestOutput();
	}

	@Test
	public void testWithoutRequest1() {
		givenPipelineConfiguration();
		whenLoggingOutsideOfRequest("Foo", "Bar");
		thenEnsureProperNonRequestOutput("Foo", "Bar");
	}

	@Test
	public void testWithoutRequest2() {
		givenPipelineConfiguration();
		whenLoggingOutsideOfRequest("a", "b", "c");
		thenEnsureProperNonRequestOutput("a", "b", "c");
	}

	@Test
	public void testWithoutRequest3() {
		givenPipelineConfiguration();
		whenLoggingOutsideOfRequest();
		thenEnsureProperNonRequestOutput();
	}

	private void givenPipelineConfiguration() {
		final Injector injector = Guice.createInjector(new EgymLogModule(), new EgymTestLogPipelineModule());
		logQueue = (EgymLogQueueImpl) injector.getInstance(EgymLogQueue.class);
		writer = injector.getInstance(InMemoryWriter.class);
	}

	private void whenLoggingRequest(String... logMessages) {
		logQueue.startRequest();

		whenLoggingOutsideOfRequest(logMessages);

		logQueue.endRequest();
	}

	private void whenLoggingOutsideOfRequest(String... logMessages) {
		for (String logMessage : logMessages) {
			log.info(logMessage);
		}
	}

	private void thenEnsureProperRequestOutput(String... expectedLogMessages) {
		final List<String> logMessages = writer.getLogMessages();

		assertNotNull(logMessages);
		assertEquals(logMessages.size(), 1);

		final String logMessage = logMessages.get(0);
		final String[] lines = logMessage.split("\n");

		// There's always at least one line.
		final int expectedNumLines = Math.max(1, expectedLogMessages.length);
		assertEquals(lines.length, expectedNumLines);

		for (int i = 0; i < expectedLogMessages.length; i++) {
			final String expectedLogMessage = expectedLogMessages[i];
			final String line = lines[i];

			assertEquals(getLogMessageAndAssertProperFormat(line), expectedLogMessage);
		}
	}

	private void thenEnsureProperNonRequestOutput(String... expectedLogMessages) {
		final List<String> logMessages = writer.getLogMessages();

		assertNotNull(logMessages);
		assertEquals(logMessages.size(), expectedLogMessages.length);

		for (int i = 0; i < expectedLogMessages.length; i++) {
			final String expectedLogMessage = expectedLogMessages[i];
			final String logMessage = logMessages.get(i);

			assertEquals(getLogMessageAndAssertProperFormat(logMessage), expectedLogMessage);
		}
	}

	private String getLogMessageAndAssertProperFormat(String line) {
		final String[] tokens0 = line.split("\\s+");
		assertEquals(tokens0[0], "");
		assertTrue(tokens0[1].matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}$"));
		assertTrue(tokens0[2].matches("^[0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3}$"));
		assertEquals(tokens0[3], "INFO");
		assertEquals(tokens0[4], "de.egym.egymapp.logqueue.EgymLogQueueImpl:");

		return tokens0[5];
	}
}
