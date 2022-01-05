package net.runelite.client.plugins.pvpplayerindicators.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StackFormatter {
    public static String quantityToStackSize(long quantity) {
        if (quantity < 0L)
            return "-" + quantityToStackSize((quantity == Long.MIN_VALUE) ? Long.MAX_VALUE : -quantity);
        if (quantity < 10000L)
            return NUMBER_FORMATTER.format(quantity);
        String suffix = SUFFIXES[0];
        long divideBy = 1L;
        for (int i = SUFFIXES.length - 1; i >= 0; i--) {
            divideBy = (long)Math.pow(10.0D, (i * 3));
            if (quantity / divideBy >= 1.0D) {
                suffix = SUFFIXES[i];
                break;
            }
        }
        String formattedString = NUMBER_FORMATTER.format(quantity / divideBy);
        formattedString = (formattedString.length() > 4) ? formattedString.substring(0, 4) : formattedString;
        return (formattedString.endsWith(".") ? formattedString.substring(0, 3) : formattedString) + suffix;
    }

    public static String quantityToRSStackSize(int quantity) {
        if (quantity == Integer.MIN_VALUE)
            return "-" + quantityToRSStackSize(2147483647);
        if (quantity < 0)
            return "-" + quantityToRSStackSize(-quantity);
        if (quantity < 100000)
            return Integer.toString(quantity);
        if (quantity < 10000000)
            return (quantity / 1000) + "K";
        return (quantity / 1000000) + "M";
    }

    public static String quantityToRSDecimalStack(int quantity) {
        return quantityToRSDecimalStack(quantity, false);
    }

    public static String quantityToRSDecimalStack(int quantity, boolean precise) {
        String quantityStr = String.valueOf(quantity);
        if (quantityStr.length() <= 4)
            return quantityStr;
        int power = (int)Math.log10(quantity);
        NumberFormat format = (precise && power >= 6) ? PRECISE_DECIMAL_FORMATTER : DECIMAL_FORMATTER;
        return format.format(quantity / Math.pow(10.0D, (power / 3 * 3))) + SUFFIXES[power / 3];
    }

    public static long stackSizeToQuantity(String string) throws ParseException {
        int multiplier = getMultiplier(string);
        float parsedValue = NUMBER_FORMATTER.parse(string).floatValue();
        return (long)(parsedValue * multiplier);
    }

    public static String formatNumber(long number) {
        return NUMBER_FORMATTER.format(number);
    }

    public static String formatNumber(double number) {
        return NUMBER_FORMATTER.format(number);
    }

    private static int getMultiplier(String string) throws ParseException {
        Matcher matcher = SUFFIX_PATTERN.matcher(string);
        if (!matcher.find())
            throw new ParseException(string + " does not resemble a properly formatted stack.", string.length() - 1);
        String suffix = matcher.group(1);
        if (!suffix.equals("")) {
            for (int i = 1; i < SUFFIXES.length; i++) {
                if (SUFFIXES[i].equals(suffix.toUpperCase()))
                    return (int)Math.pow(10.0D, (i * 3));
            }
            throw new ParseException("Invalid Suffix: " + suffix, string.length() - 1);
        }
        return 1;
    }

    private static final String[] SUFFIXES = new String[] { "", "K", "M", "B" };

    private static final Pattern SUFFIX_PATTERN = Pattern.compile("^-?[0-9,.]+([a-zA-Z]?)$");

    private static final NumberFormat NUMBER_FORMATTER = NumberFormat.getInstance(Locale.ENGLISH);

    private static final NumberFormat DECIMAL_FORMATTER = new DecimalFormat("#,###.#", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

    private static final NumberFormat PRECISE_DECIMAL_FORMATTER = new DecimalFormat("#,###.###", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
}
