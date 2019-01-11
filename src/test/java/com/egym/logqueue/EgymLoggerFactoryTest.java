/**
 * This file is part of the source code and related artifacts for eGym Application.
 *
 * Copyright © 2013 eGym GmbH
 */
package com.egym.logqueue;

import static org.testng.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.egym.logqueue.slf4j.EgymLoggerFactory;
import org.slf4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class EgymLoggerFactoryTest {
	private EgymLoggerFactory loggerFactory;

	@BeforeMethod
	public void init() {
		loggerFactory = new EgymLoggerFactory();
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testGetLoggerNull() {
		loggerFactory.getLogger(null);
	}

	@Test
	public void testGetLogger() {
		final Logger logger1 = loggerFactory.getLogger("foo");
		assertNotNull(logger1);
		assertEquals(logger1.getName(), "foo");

		final Logger logger2 = loggerFactory.getLogger("foo");
		assertTrue(logger1 == logger2);
	}

	@Test
	public void testGetLoggerConcurrency() {
		final int numThreads = 1000;
		final int threadIterations = 100;

		final List<String> names = Arrays.asList("foo", "bar", "fizz", "buzz");
		final ConcurrentMap<String, Logger> loggers = new ConcurrentHashMap<String, Logger>();
		final List<Thread> threads = new ArrayList<Thread>();

		for (int i = 0; i < numThreads; i++) {
			threads.add(new Thread() {
				@Override
				public void run() {
					for (int j = 0; j < threadIterations; j++) {
						final String name = names.get(j % names.size());
						final Logger logger = loggerFactory.getLogger(name);
						if (loggers.containsKey(name)) {
							if (loggers.get(name) != logger) {
								throw new AssertionError();
							}
						} else {
							loggers.putIfAbsent(name, logger);
						}
					}
				}
			});
		}

		for (Thread thread : threads) {
			thread.start();
		}

		for (Thread thread : threads) {
			while (thread.isAlive()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					throw new RuntimeException("Stop interrupting me!", e);
				}
			}
		}
	}
}
