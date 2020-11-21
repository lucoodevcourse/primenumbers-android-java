d ../package edu.luc.etl.cs313.android.primechecker.android;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

import edu.luc.etl.cs313.android.primechecker.util.PrimeCheckerUtil;

@RunWith(Parameterized.class)
public class TestPrimeCheckerUtil {

    public TestPrimeCheckerUtil(int input, boolean expected) {
        this.input = input;
        this.expected = expected;
    }

    private final int input;

    private final boolean expected;

    @Parameterized.Parameters(name = "Test {index}: isPrime({0})={1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { -7, false },
            { -1, false },
            { 0, false },
            { 1, false },
            { 2, true },
            { 3, true },
            { 4, false },
            { 5, true },
            { 6, false },
            { 7, true },
            { 8, false },
            { 9, false },
            { 10, false },
            { 1013, true },
            { 1014, false },
            { 6006, false },
            { 6007, true },
            { 6033, false },
            { 10007, true },
            { 10077, false },
            { 100003, true },
            { 100033, false },
            { 1000003, true },
            { 1000013, false },
            { 10000169, true },
            { 10001169, false },
            { 100000007, true },
            { 100000077, false }
        });
    }

    @Test
    public void testIsPrimeLocal() {
        assertEquals(expected, PrimeCheckerUtil.isPrimeLocal(input));
    }

    @Test
    public void testIsPrimeOrig() {
        assertEquals(expected, PrimeCheckerUtil.isPrimeOrig(input));
    }
}
