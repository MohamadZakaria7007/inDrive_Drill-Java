package com.qakit.framework.ui;

import java.math.BigDecimal;

public final class Money {

    private Money() {}

    /** "Item total: $39.98" -> 39.98. BigDecimal, never double, for money. */
    public static BigDecimal parse(String text) {
        return new BigDecimal(text.substring(text.lastIndexOf('$') + 1).trim());
    }
}
