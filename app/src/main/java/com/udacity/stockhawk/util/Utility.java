package com.udacity.stockhawk.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by rodrigocastro on 30/01/17.
 */
public class Utility {

    public static DecimalFormat getPercentageFormat() {
        DecimalFormat percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");
        return percentageFormat;
    }

    public static DecimalFormat getDollarFormatWithPlus() {
        DecimalFormat dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");
        return dollarFormatWithPlus;
    }

    public static DecimalFormat getDollarFormat() {
        return (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
    }
}
