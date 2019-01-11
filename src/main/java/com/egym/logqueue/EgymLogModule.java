/**
 * This file is part of the source code and related artifacts for eGym Application.
 *
 * Copyright © 2013 eGym GmbH
 */
package com.egym.logqueue;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.egym.logqueue.slf4j.EgymLogger;

/**
 * The central module of the egym-log system. Your application needs to install this module in order for egym-log to work correctly.
 */
public class EgymLogModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(EgymLogQueue.class).to(EgymLogQueueImpl.class).in(Singleton.class);
		bind(EgymLogPipelineService.class);
		requestStaticInjection(EgymLogger.class);
	}
}
