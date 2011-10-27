/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.impl;

/**
 * Process the invocation of a mock during a test.
 *
 * <p>
 * An invocation processor is assigned a stub processor and/or a story
 * processor, invoked in that order.
 * </p>
 */
class InvocationProcessor implements MockInvocationHandler {

    /** Logs the invocation processor activity. */
    private static final Logger logger = Logger.get(InvocationProcessor.class);
    /** Handles the stubbing of invocations. */
    private final StubProcessor stubProcessor;
    /** Validates the invocation regarding the story ongoing. */
    private final StoryProcessor storyProcessor;

    /**
     * Creates a new invocation processor.
     *
     * @param stubProcessor
     *            the invoked stub processor, <code>null</code> if none
     * @param storyProcessor
     *            the invoked story processor, <code>null</code> if none
     */
    InvocationProcessor(StubProcessor stubProcessor,
      StoryProcessor storyProcessor) {
        this.stubProcessor = stubProcessor;
        this.storyProcessor = storyProcessor;
    }

    public InvocationResultProvider invoke(Invocation invocation) {
        logger.trace("invoke", "invocation=", invocation);
        InvocationResultProvider resultProvider;

        resultProvider = stubProcessor.invoke(invocation);
        if (resultProvider == null) {
            logger.trace("invoke", "no result provider by the stub processor");
            return storyProcessor.invoke(invocation);
        } else {
            logger.trace("invoke", "one result provided by the stub processor");
            return resultProvider;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(64);
        builder.append('{');
        builder.append("stubProcessor=");
        builder.append(stubProcessor);
        builder.append(' ');
        builder.append("storyProcessor=");
        builder.append(storyProcessor);
        builder.append('}');
        return builder.toString();
    }
}
