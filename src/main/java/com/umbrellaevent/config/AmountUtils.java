package com.umbrellaevent.config;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AmountUtils {

    /**
     * Rounds a Double value to 2 decimal places.
     * @param value the value to round
     * @return the rounded value
     */
    public static Double roundToTwoDecimalPlaces(Double value) {
        if (value == null) {
            return null;
        }
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
