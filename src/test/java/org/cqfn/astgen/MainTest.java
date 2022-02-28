package org.cqfn.astgen;

import org.junit.Assert;
import org.junit.Test;

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
        Assert.assertFalse(exceptionCaught);
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
        Assert.assertTrue(exceptionCaught);
    }
}
