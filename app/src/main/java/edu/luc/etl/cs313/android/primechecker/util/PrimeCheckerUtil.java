package edu.luc.etl.cs313.android.primechecker.util;

import java.util.function.IntConsumer;
import java.util.function.BooleanSupplier;

public enum PrimeCheckerUtil {;

    public static boolean isPrimeLocal(final long i) {
        return isPrimeLocal(i, () -> false, (final int k) -> { });
    }

    public static boolean isPrimeLocal(final long i, final BooleanSupplier isCancelled, final IntConsumer publishProgress) { // optimized non-Async/local isPrime method
        if (i < 2) return false;
        if (i == 2) return true;
        if (i % 2 == 0) return false;
        final long sqrt = Math.round(Math.sqrt(i));
        for (long k = 3; k <= sqrt; k += 2) {
            if (isCancelled.getAsBoolean() || i % k == 0) return false;
            publishProgress.accept((int) (k * 100 / sqrt));
        }
        return true;
    }

    public static boolean isPrimeOrig(final long i) {
        return isPrimeOrig(i, () -> false, (final int k) -> { });
    }

    // begin-method-isPrimeLong
    public static boolean isPrimeOrig(final long i, final BooleanSupplier isCancelled, final IntConsumer publishProgress) { // original isPrime, now used for Async execution
        if (i < 2) return false;
        final long half = i / 2;
        for (long k = 2; k <= half; k += 1) {
            if (isCancelled.getAsBoolean() || i % k == 0) return false;
            publishProgress.accept((int) (k * 100 / half));
        }
        return true;
    }
    // end-method-isPrimeLong

}
