/**
 * This file is part of the source code and related artifacts for eGym Application.
 *
 * Copyright © 2013 eGym GmbH
 */
package com.egym.logqueue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InstanceBinding;

import com.egym.logqueue.config.EgymLogPipelineConfig;
import com.egym.logqueue.decorator.EgymLogDecorator;
import com.egym.logqueue.formatter.EgymLogFormatter;
import com.egym.logqueue.writer.EgymLogWriter;

/**
 * Takes care of instantiating the log pipelines from the Guice bindings.
 */
class EgymLogPipelineService {
	private final Injector injector;

	@Inject
	EgymLogPipelineService(final Injector injector) {
		this.injector = injector;
	}

	/**
	 * Creates all the pipelines for the configuration bound in Guice.
	 *
	 * @return a list of pipelines. Never null. Never contains null entries.
	 */
	public List<EgymLogPipeline> createPipelines() {
		final List<EgymLogPipelineConfig> configs = getPipelineConfigs();
		final List<EgymLogPipeline> pipelines = new ArrayList<>();

		for (EgymLogPipelineConfig logWiringConfig : configs) {
			pipelines.add(createPipeline(logWiringConfig));
		}

		return pipelines;
	}

	/**
	 * Creates a pipeline from a config.
	 *
	 * @param config
	 *            the config to use. Must not be null.
	 * @return a pipeline adhering to the config.
	 */
	private EgymLogPipeline createPipeline(EgymLogPipelineConfig config) {
		if (config == null) {
			throw new IllegalArgumentException("config must not be null");
		}

		final EgymLogDecorator logRecordDecorator = createDecoratorFromConfig(config);
		final EgymLogFormatter logFormatter = createFormatterFromConfig(config);
		final EgymLogWriter logWriter = createWriterFromConfig(config);
		return new EgymLogPipeline(logRecordDecorator, logFormatter, logWriter);
	}

	private EgymLogDecorator createDecoratorFromConfig(EgymLogPipelineConfig config) {
		final Class<? extends EgymLogDecorator> logDecoratorClazz = config.getLogDecoratorClazz();
		final EgymLogDecorator logRecordDecorator = injector.getInstance(logDecoratorClazz);

		if (logRecordDecorator == null) {
			throw new AssertionError("Failed to create decorator for config " + config);
		}

		return logRecordDecorator;
	}

	private EgymLogFormatter createFormatterFromConfig(EgymLogPipelineConfig config) {
		final Class<? extends EgymLogFormatter> logFormatterClazz = config.getLogFormatterClazz();
		final EgymLogFormatter logFormatter = injector.getInstance(logFormatterClazz);

		if (logFormatter == null) {
			throw new AssertionError("Failed to create formatter for config " + config);
		}

		return logFormatter;
	}

	private EgymLogWriter createWriterFromConfig(EgymLogPipelineConfig config) {
		final Class<? extends EgymLogWriter> logWriterClazz = config.getLogWriterClazz();
		final EgymLogWriter logWriter = injector.getInstance(logWriterClazz);

		if (logWriter == null) {
			throw new AssertionError("Failed to create writer for config " + config);
		}

		return logWriter;
	}

	/**
	 * Retrieves the {@link EgymLogPipelineConfig} instances from Guice.
	 *
	 * @return all bound config instances.
	 */
	private List<EgymLogPipelineConfig> getPipelineConfigs() {
		final Map<Key<?>, Binding<?>> bindings = injector.getBindings();
		if (bindings == null) {
			throw new IllegalStateException("injector.bindings must not be null");
		}

		final List<EgymLogPipelineConfig> logPipelineConfigs = new ArrayList<>();

		for (Key<?> key : bindings.keySet()) {
			final TypeLiteral<?> typeLiteral = key.getTypeLiteral();
			if (typeLiteral == null) {
				throw new IllegalStateException("typeLiteral must not be null");
			}

			final Class<?> type = typeLiteral.getRawType();
			if (type == null) {
				throw new IllegalStateException("type must not be null");
			}

			if (EgymLogPipelineConfig.class.isAssignableFrom(type)) {
				final Binding<?> binding = bindings.get(key);

				if (binding instanceof InstanceBinding) {
					final InstanceBinding instanceBinding = (InstanceBinding) binding;
					final EgymLogPipelineConfig config = (EgymLogPipelineConfig) instanceBinding.getInstance();
					if (config == null) {
						throw new IllegalStateException("The binding " + binding + " does not point to an instance.");
					}

					logPipelineConfigs.add(config);
				}
			}
		}

		return logPipelineConfigs;
	}
}
