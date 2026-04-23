package com.example.util;

import java.text.NumberFormat;
import java.util.Locale;

public class RupiahFormatter {
    private static final NumberFormat FORMATTER;

    static {
        FORMATTER = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        FORMATTER.setMaximumFractionDigits(0);
    }

    private RupiahFormatter() {}

    public static String format(double value) {
        return FORMATTER.format(value).replace("Rp", "Rp ").replaceAll("\\s+", " ").trim();
    }
}
