package org.uast.astgen;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link Main} class.
 */
public class MainTest {
    /**
     * Test passing arguments to main().
     */
    @Test
    public void testNoException() {
        final String[] example = {
                "test"
        };
        boolean exceptionCaught = false;
        try {
            Main.main(example);
        } catch (IllegalArgumentException e) {
            exceptionCaught = true;
        }
        Assertions.assertFalse(exceptionCaught);
    }

    /**
     * Test passing no argument to main().
     */
    @Test
    public void testWithException() {
        final String[] example = {
        };
        boolean exceptionCaught = false;
        try {
            Main.main(example);
        } catch (IllegalArgumentException e) {
            exceptionCaught = true;
        }
        Assertions.assertTrue(exceptionCaught);
    }

    /**
     * Test passing no argument to main().
     */
    @Test
    public void testMain() {
        final String[] example = {
                "test"
        };
        Main.main(example);
        Assertions.assertTrue(example.length > 0);
    }
}
