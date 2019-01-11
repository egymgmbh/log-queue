/**
 * This file is part of the source code and related artifacts for eGym Application.
 *
 * Copyright © 2013 eGym GmbH
 */
package com.egym.logqueue.decorator;

import com.egym.logqueue.EgymLogRequestRecord;

/**
 * A log decorator is in charge of adding additional information to a request record. This is typically used to add meta information, like
 * the request URL or the logged in user to the record.
 * <p>
 * All implementations must be <em>thread-safe</em>. It is recommended to make implementations immutable.
 * </p>
 *
 * @param <D>
 *            the output type.
 */
public interface EgymLogDecorator<D extends EgymLogRequestRecord> {
	/**
	 * Decorates a request record.
	 *
	 * @param requestRecord
	 *            the input record. Must not be null.
	 * @return the decorated output record or null.
	 */
	D decorate(EgymLogRequestRecord requestRecord);
}
