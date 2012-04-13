/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple thread model to perform multi-threaded tests.
 *
 * <p>
 * A messenger is a basic computation unit executed by thread, which can be
 * used as a producer or consumer of messages. This implementation defines
 * the underlying runnable ad utilities to create threads.
 * </p>
 * <p>
 * A message is a simple string. Applications can:
 * </p>
 * <ul>
 * <li>Define a handler to the received messages, in order to perform
 *   analysis and processing.</li>
 * <li>Use the <code>post</code> method to produce a message to another
 *   messenger.<li>
 * </ul>
 *
 * <p>Notice that the string "exit" is reserved by the system to end the
 * thread.</p>
 */
class Messenger extends Thread {
    /** A standard timeout to <code>thread.join</code>. */
    private static final int JOIN_TIMESOUT_AFTER = 500;
    /** A static counter used to name messengers (to help debugging). */
    private static int counter = 0;
    /** Message reserved to ask to exit this thread. */
    public static final String EXIT_COMMAND = "exit";
    /** Processes the received messages, can be <code>null</code>. */
    private final MessengerProcessor rxProcessor;
    /** List of incoming messages, treated as a FIFO. */
    private final List<String> incomingMessages;
    /** A list of started messengers. Used by start and stop messengers. */
    private static final List<Messenger> startedMessengers = new ArrayList<Messenger>();

    /**
     * Creates a list that will be used as a synchronized FIFO to exchange
     * messages.
     *
     * @return The new FIFO.
     */
    private static List<String> aFifo() {
        return Collections.synchronizedList(new LinkedList<String>());
    }

    /**
     * Creates a new messenger.
     *
     * @param rxProcessor
     *            processes the received messages
     */
    Messenger(MessengerProcessor rxProcessor) {
        this.incomingMessages = aFifo();
        this.rxProcessor = rxProcessor;
        this.setName("messenger#" + counter++);
    }

    /**
     * Processes a new message if there is a processor.
     *
     * @param message
     *            the message
     */
    private void receiveMessage(String message) {
        if (rxProcessor != null) {
            rxProcessor.process(message);
        }
    }

    /**
     * Waits for messages sent by other messengers and calls the
     * associated processor if any.
     *
     * @return The next received message, <code>null</code> is the thread is
     * killed.
     */
    private String waitForIncomingMessages() {
        synchronized (incomingMessages) {
            while (incomingMessages.isEmpty()) {
                try {
                    incomingMessages.wait();
                } catch (InterruptedException ex) {
                    return null;
                }
            }

            return incomingMessages.remove(0);
        }
    }

    @Override
    public void run() {
        while (true) {
            String message = waitForIncomingMessages();
            if (message == null || message.trim().equals(EXIT_COMMAND)) {
                break;
            } else {
                receiveMessage(message);
            }
        }
    }

    /**
     * Posts a new message to this messenger.
     *
     * @param message
     *            the message
     */
    public void post(String message) {
        synchronized (incomingMessages) {
            incomingMessages.add(message);
            incomingMessages.notifyAll();
        }
    }

    /**
     * Posts a request to stop the thread.
     */
    public void exit() {
        post(EXIT_COMMAND);
    }

    /**
     * Creates a thread running a messenger.
     *
     * @param rxProcessor
     *            processes the received messages
     * @return The thread object.
     */
    public static Messenger aMessenger(MessengerProcessor rxProcessor) {
        return new Messenger(rxProcessor);
    }

    /**
     * Creates a thread running a messenger that does not perform any particular
     * operation upon reception of a message.
     *
     * @return The thread object.
     */
    public static Messenger anEmptyMessenger() {
        return aMessenger(null);
    }

    /**
     * Starts a bunch of messengers.
     *
     * @param messengers
     *            the list of threads to start
     */
    public static void startMessengers(Messenger... messengers) {
        for (Messenger messenger : messengers) {
            messenger.start();
            startedMessengers.add(messenger);
        }
    }

    /**
     * Ends a set of messengers, waiting to join.
     *
     * <p>
     * The messengers are registered by <code>startMessengers</code>.
     * </p>
     *
     * @throws InterruptedException
     */
    public static void stopMessengers() throws
      InterruptedException {
        try {
            for (Messenger messenger : startedMessengers) {
                messenger.exit();
                messenger.join(JOIN_TIMESOUT_AFTER);
            }
        } finally {
            startedMessengers.clear();
        }
    }
}
