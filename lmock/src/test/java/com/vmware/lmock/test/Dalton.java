/* **************************************************************************
 * Copyright (C) 2010-2011 VMware, Inc. All rights reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0.
 * Please see the LICENSE file to review the full text of the Apache License 2.0.
 * You may not use this product except in compliance with the License.
 * ************************************************************************** */
package com.vmware.lmock.test;

import java.util.List;

import com.vmware.lmock.impl.Mock;

/**
 * Reference interface for the tests.
 *
 * Based on the Dalton brothers: Joe, Jack, William and Averell.
 *
 * A mock is created for each of them.
 */
public interface Dalton {
    public static Dalton joe = Mock.getObject("joe", Dalton.class);
    public static Dalton jack = Mock.getObject("jack", Dalton.class);
    public static Dalton william = Mock.getObject("william", Dalton.class);
    public static Dalton averell = Mock.getObject("averell", Dalton.class);

    /**
     * An exception specific to the Daltons.
     */
    public class SpecialDaltonException extends Exception {
        /** Class version, for serialization. */
        private static final long serialVersionUID = 1L;

        public SpecialDaltonException() {
            super();
        }

        public SpecialDaltonException(String msg) {
            super(msg);
        }
    };

    /**
     * A void method to nothing.
     */
    public void doNothing();

    /**
     * @throws SpecialDaltonException
     *             Hand-made exception, just to bother the population.
     */
    public void bother() throws SpecialDaltonException;

    /**
     * A dummy method, with no arguments and no return value.
     *
     * @return An arbitrary number.
     */
    public int ping();

    /**
     * Pings a brother and gets the brother's response.
     *
     * @param brother
     *            the brother
     * @return The pong reply from the brother.
     */
    public int ping(Dalton brother);

    /**
     * Pings a brother to send a message and gets the brother's response.
     *
     * @param brother
     *            the brother
     * @param message
     *            the message
     * @return The pong reply from the brother.
     */
    public int ping(Dalton brother, String message);

    /**
     * Pings a brother to send a message and gets the brother's response.
     *
     * @param brother
     *            the brother
     * @param message
     *            the message
     * @return The pong reply from the brother.
     */
    public int ping(Dalton brother, String... message);

    /**
     * Allows to parse the brothers from the smallest to the tallest.
     *
     * @return The next brother in the list.
     */
    public Dalton next();

    /**
     * Fills in the pocket with stuff.
     *
     * @param stuff
     *            the contents added to the pocket.
     */
    public void fillPocket(Object... stuff);

    /**
     * Fills in the pocket with stuff.
     *
     * @param stuff
     *            the contents added to the pocket.
     */
    public void fillPocketWithAPackOf(Object[] stuff);

    /**
     * @return The current contents in the pocket.
     */
    public Object[] emptyPocket();

    /**
     * @return A list of the pocket contents.
     */
    public List<Object> getPocketContents();

    /** @return An arbitrary boolean. */
    public boolean getBoolean();

    /** @return An arbitrary boolean. */
    public Boolean getBoolean_();

    /** @return An arbitrary character. */
    public char getChar();

    /** @return An arbitrary character. */
    public Character getChar_();

    /** @return An arbitrary byte. */
    public byte getByte();

    /** @return An arbitrary byte. */
    public Byte getByte_();

    /** @return An arbitrary short. */
    public short getShort();

    /** @return An arbitrary short. */
    public Short getShort_();

    /** @return An arbitrary integer. */
    public int getInt();

    /** @return An arbitrary integer. */
    public Integer getInt_();

    /** @return An arbitrary long. */
    public long getLong();

    /** @return An arbitrary long. */
    public Long getLong_();

    /** @return An arbitrary float. */
    public float getFloat();

    /** @return An arbitrary float. */
    public Float getFloat_();

    /** @return An arbitrary double. */
    public double getDouble();

    /** @return An arbitrary double. */
    public Double getDouble_();

    /**
     * @param v
     *            an arbitrary boolean value
     */
    public void setBoolean(boolean v);

    /**
     * @param v
     *            an arbitrary boolean value
     */
    public void setBoolean_(Boolean v);

    /**
     * @param v
     *            an arbitrary character
     */
    public void setChar(char v);

    /**
     * @param v
     *            an arbitrary character
     */
    public void setChar_(Character v);

    /**
     * @param v
     *            an arbitrary byte value
     */
    public void setByte(byte v);

    /**
     * @param v
     *            an arbitrary byte value
     */
    public void setByte_(Byte v);

    /**
     * @param v
     *            an arbitrary short value
     */
    public void setShort(short v);

    /**
     * @param v
     *            an arbitrary short value
     */
    public void setShort_(Short v);

    /**
     * @param v
     *            an arbitrary integer value
     */
    public void setInt(int v);

    /**
     * @param v
     *            an arbitrary integer value
     */
    public void setInt_(Integer v);

    /**
     * @param v
     *            an arbitrary long value
     */
    public void setLong(long v);

    /**
     * @param v
     *            an arbitrary long value
     */
    public void setLong_(Long v);

    /**
     * @param v
     *            an arbitrary float value
     */
    public void setFloat(float v);

    /**
     * @param v
     *            an arbitrary float value
     */
    public void setFloat_(Float v);

    /**
     * @param v
     *            an arbitrary double value
     */
    public void setDouble(double v);

    /**
     * @param v
     *            an arbitrary double value
     */
    public void setDouble_(Double v);

    /**
     * @param v
     *            an arbitrary object
     */
    public void setObject(Object v);
}
