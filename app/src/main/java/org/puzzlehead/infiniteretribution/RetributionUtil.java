package org.puzzlehead.infiniteretribution;

import java.math.BigInteger;
import java.text.DecimalFormat;

/**
 * Created by Eric on 9/8/2016.
 */
public class RetributionUtil
{
    protected static final BigInteger BIG_MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
    protected static final BigInteger BIG_MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);

    protected static final DecimalFormat COUNT_FORMAT = new DecimalFormat("#,###,###,###,###,###,###");

    public static String countToString(long count)
    {
        if (count == Long.MAX_VALUE)
        {
            return "∞";
        }

        else if (count == Long.MAX_VALUE - 1)
        {
            return "71!";
        }

        else if (count == Long.MIN_VALUE)
        {
            return "-∞";
        }

        else if (count == Long.MIN_VALUE + 1)
        {
            return "-(71!)";
        }

        else
        {
            return COUNT_FORMAT.format(count);
        }
    }

    public static long add(long a, long b)
    {
        BigInteger count = BigInteger.valueOf(a).add(BigInteger.valueOf(b));

        if (count.compareTo(BIG_MAX_LONG) >= 0)
        {
            return Long.MAX_VALUE;
        }

        else if (count.compareTo(BIG_MIN_LONG) <= 0)
        {
            return Long.MIN_VALUE;
        }

        else
        {
            return a + b;
        }
    }

    public static long multiply(long a, long b)
    {
        BigInteger count = BigInteger.valueOf(a).multiply(BigInteger.valueOf(b));

        if (count.compareTo(BIG_MAX_LONG) >= 0)
        {
            return Long.MAX_VALUE;
        }

        else if (count.compareTo(BIG_MIN_LONG) <= 0)
        {
            return Long.MIN_VALUE;
        }

        else
        {
            return a * b;
        }
    }
}
