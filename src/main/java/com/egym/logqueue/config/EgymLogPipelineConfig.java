/**
 * This file is part of the source code and related artifacts for eGym Application.
 *
 * Copyright © 2013 eGym GmbH
 */
package com.egym.logqueue.config;

import net.jcip.annotations.Immutable;
import com.egym.logqueue.EgymLogRequestRecord;
import com.egym.logqueue.decorator.EgymLogDecorator;
import com.egym.logqueue.formatter.EgymLogFormatter;
import com.egym.logqueue.writer.EgymLogWriter;

/**
 * Specifies a log pipeline consisting of a decorator, a formatter and a writer.
 *
 * @param <D>
 *            the decorated log request record type.
 * @param <F>
 *            the formatted log record type.
 */
@Immutable
public class EgymLogPipelineConfig<D extends EgymLogRequestRecord, F> {
	private final Class<? extends EgymLogDecorator<D>> logDecoratorClazz;

	private final Class<? extends EgymLogFormatter<D, F>> logFormatterClazz;

	private final Class<? extends EgymLogWriter<F>> logWriterClazz;

	public EgymLogPipelineConfig(Class<? extends EgymLogDecorator<D>> logDecoratorClazz,
			Class<? extends EgymLogFormatter<D, F>> logFormatterClazz, Class<? extends EgymLogWriter<F>> logWriterClazz) {

		if (logDecoratorClazz == null) {
			throw new IllegalArgumentException("logDecoratorClazz must not be null");
		}
		if (logFormatterClazz == null) {
			throw new IllegalArgumentException("logFormatterClazz must not be null");
		}
		if (logWriterClazz == null) {
			throw new IllegalArgumentException("logWriterClazz must not be null");
		}

		this.logDecoratorClazz = logDecoratorClazz;
		this.logWriterClazz = logWriterClazz;
		this.logFormatterClazz = logFormatterClazz;
	}

	public Class<? extends EgymLogDecorator<D>> getLogDecoratorClazz() {
		return logDecoratorClazz;
	}

	public Class<? extends EgymLogFormatter<D, F>> getLogFormatterClazz() {
		return logFormatterClazz;
	}

	public Class<? extends EgymLogWriter<F>> getLogWriterClazz() {
		return logWriterClazz;
	}

	@Override
	public String toString() {
		return "EgymLogWiringConfig(" + "logDecoratorClazz=" + logDecoratorClazz.getName() + ", logFormatterClazz="
				+ logFormatterClazz.getName() + ", logWriterClazz=" + logWriterClazz.getName() + ')';
	}
}
