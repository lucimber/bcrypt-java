/*
 * SPDX-FileCopyrightText: 2025 Lucimber UG
 * SPDX-License-Identifier: Apache-2.0
 */
package com.lucimber.crypto.bcrypt;

/**
 * Represents the cost factor (also known as rounds or work factor) for BCrypt hashing. The cost
 * factor determines how computationally expensive the hashing operation is. Higher values provide
 * better security but take longer to compute. The actual number of rounds is 2^costFactor.
 */
public final class CostFactor {
    /** Minimum allowed cost factor. */
    public static final int MIN_COST = 4;

    /** Maximum allowed cost factor. */
    public static final int MAX_COST = 31;

    /**
     * Default cost factor for new hashes. This provides a good balance between security and
     * performance.
     */
    public static final int DEFAULT_COST = 10;

    private final int value;

    /**
     * Creates a new CostFactor with the specified value.
     *
     * @param value the cost factor value (must be between MIN_COST and MAX_COST)
     * @throws IllegalArgumentException if the value is out of range
     */
    public CostFactor(int value) {
        if (value < MIN_COST || value > MAX_COST) {
            throw new IllegalArgumentException(
                    String.format(
                            "Cost factor must be between %d and %d, but was %d",
                            MIN_COST, MAX_COST, value));
        }
        this.value = value;
    }

    /**
     * Creates a CostFactor with the default value.
     *
     * @return a CostFactor with the default value
     */
    public static CostFactor defaultCost() {
        return new CostFactor(DEFAULT_COST);
    }

    /**
     * Gets the cost factor value.
     *
     * @return the cost factor value
     */
    public int getValue() {
        return value;
    }

    /**
     * Calculates the actual number of rounds for this cost factor.
     *
     * @return the number of rounds (2^costFactor)
     */
    public long getRounds() {
        return 1L << value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CostFactor that = (CostFactor) obj;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public String toString() {
        return String.format("CostFactor{value=%d, rounds=%d}", value, getRounds());
    }
}
