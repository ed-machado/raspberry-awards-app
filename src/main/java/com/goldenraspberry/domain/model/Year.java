package com.goldenraspberry.domain.model;

import java.util.Objects;

/**
 * Year value object
 * Representa um ano de lancamento de filme com validacao
 */
public class Year {

    private static final int MIN_YEAR = 1900;
    private static final int MAX_YEAR = 2100;

    private final int value;

    public Year(int value) {
        this.value = value;
        validateYear();
    }

    private void validateYear() {
        if (value < MIN_YEAR || value > MAX_YEAR) {
            throw new IllegalArgumentException(
                String.format("Year deve estar entre %d e %d, mas foi %d", MIN_YEAR, MAX_YEAR, value)
            );
        }
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Year year = (Year) o;
        return value == year.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Year{" +
                "value=" + value +
                '}';
    }
}
