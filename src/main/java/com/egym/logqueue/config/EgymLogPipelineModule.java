/**
 * This file is part of the source code and related artifacts for eGym Application.
 *
 * Copyright © 2013 eGym GmbH
 */
package com.egym.logqueue.config;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.internal.UniqueAnnotations;

import com.egym.logqueue.EgymLogRequestRecord;
import com.egym.logqueue.decorator.EgymLogDecorator;
import com.egym.logqueue.decorator.EgymLogNoOpDecorator;
import com.egym.logqueue.formatter.EgymLogFormatter;
import com.egym.logqueue.writer.EgymLogWriter;

/**
 * Abstract base class for pipeline configuratin modules.
 */
public abstract class EgymLogPipelineModule extends AbstractModule {
	/**
	 * Intermediate class used to chain the pipeline configuration.
	 *
	 * @param <D>
	 *            the type of the decorated request log records.
	 */
	public class DecorateWith<D extends EgymLogRequestRecord> {
		private final Class<? extends EgymLogDecorator<D>> logDecoratorClazz;

		/**
		 * @param logDecoratorClazz
		 *            the class to use for log decoration. Must not be null.
		 */
		private DecorateWith(Class<? extends EgymLogDecorator<D>> logDecoratorClazz) {
			if (logDecoratorClazz == null) {
				throw new IllegalArgumentException("logDecoratorClazz must not be null");
			}
			this.logDecoratorClazz = logDecoratorClazz;
		}

		/**
		 * Specifies the formatter to use for the log pipeline.
		 *
		 * @param logFormatterClazz
		 *            the type of formatter to use.
		 * @param <F>
		 *            the type of formatted request log record.
		 * @return intermediary object used to chain the following calls.
		 */
		public <F> FormatWith<D, F> formatWith(Class<? extends EgymLogFormatter<D, F>> logFormatterClazz) {
			return new FormatWith<>(logDecoratorClazz, logFormatterClazz);
		}
	}

	/**
	 * Intermediate class used to chain the pipeline configuration.
	 *
	 * @param <D>
	 *            the type of the decorated request log records.
	 * @param <F>
	 *            the type of formatted request log record.
	 */
	public class FormatWith<D extends EgymLogRequestRecord, F> {
		private final Class<? extends EgymLogDecorator<D>> logDecoratorClazz;

		private final Class<? extends EgymLogFormatter<D, F>> logFormatterClazz;

		/**
		 * @param logDecoratorClazz
		 *            the class to use for log decoration. Must not be null.
		 * @param logFormatterClazz
		 *            the type of formatter to use.
		 */
		private FormatWith(Class<? extends EgymLogDecorator<D>> logDecoratorClazz, Class<? extends EgymLogFormatter<D, F>> logFormatterClazz) {
			this.logDecoratorClazz = logDecoratorClazz;
			this.logFormatterClazz = logFormatterClazz;
		}

		/**
		 * Specifies the writer used to write log records. This finalizes the pipeline configuration.
		 *
		 * @param logWriterClazz
		 *            the type of writer to use.
		 */
		public void writeTo(Class<? extends EgymLogWriter<F>> logWriterClazz) {
			bind(logDecoratorClazz);
			bind(logFormatterClazz);
			bind(logWriterClazz);
			bind(Key.get(EgymLogPipelineConfig.class, UniqueAnnotations.create())).toInstance(
					new EgymLogPipelineConfig(logDecoratorClazz, logFormatterClazz, logWriterClazz));
		}
	}

	/**
	 * Configures a pipeline with the specified decorator.
	 *
	 * @param logDecoratorClazz
	 *            the class to use for log decoration. Must not be null.
	 *
	 * @return intermediary object used to chain the following calls.
	 */
	protected <D extends EgymLogRequestRecord> DecorateWith<D> decorateWith(Class<? extends EgymLogDecorator<D>> logDecoratorClazz) {
		if (logDecoratorClazz == null) {
			throw new IllegalArgumentException("logDecoratorClazz must not be null");
		}
		return new DecorateWith<>(logDecoratorClazz);
	}

	/**
	 * Configures a pipeline without a decorator.
	 *
	 * @return intermediary object used to chain the following calls.
	 */
	protected DecorateWith<EgymLogRequestRecord> skipDecoration() {
		return decorateWith(EgymLogNoOpDecorator.class);
	}
}
