/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 Ivan Kniazkov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cqfn.astranaut.cli;

import java.util.Arrays;
import java.util.Collections;
import org.cqfn.astranaut.core.utils.FilesReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link ArgumentParser} class.
 * @since 1.0.0
 */
class ArgumentParserTest {
    @Test
    void output() {
        final ArgumentParser parser = new ArgumentParser();
        Assertions.assertEquals("output", parser.getOutput());
        boolean oops = false;
        try {
            parser.parse(Arrays.asList("--output", "test1"));
        } catch (final CliException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        Assertions.assertEquals("test1", parser.getOutput());
    }

    @Test
    void outputShort() {
        final ArgumentParser parser = new ArgumentParser();
        boolean oops = false;
        try {
            parser.parse(Arrays.asList("-o", "test2"));
        } catch (final CliException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        Assertions.assertEquals("test2", parser.getOutput());
    }

    @Test
    void badOutput() {
        final ArgumentParser parser = new ArgumentParser();
        Assertions.assertThrows(
            CommonCliException.class,
            () -> parser.parse(Collections.singletonList("-o"))
        );
        Assertions.assertThrows(
            CommonCliException.class,
            () -> parser.parse(Arrays.asList("--output", "--license"))
        );
    }

    @Test
    void licenseGood() {
        final ArgumentParser parser = new ArgumentParser();
        final String filename = "LICENSE.txt";
        Assertions.assertTrue(parser.getLicence().startsWith("Copyright"));
        boolean oops = false;
        try {
            parser.parse(Arrays.asList("--license", filename));
        } catch (final CliException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        final String expected = new FilesReader(filename).readAsStringNoExcept();
        Assertions.assertEquals(expected, parser.getLicence());
    }

    @Test
    void licenseBad() {
        final ArgumentParser parser = new ArgumentParser();
        boolean oops = false;
        try {
            parser.parse(Arrays.asList("-l", "bad.file.name"));
        } catch (final CliException exception) {
            oops = true;
            Assertions.assertEquals("Command line interface", exception.getInitiator());
            Assertions.assertEquals(
                "Can't read the license file 'bad.file.name'",
                exception.getErrorMessage()
            );
        }
        Assertions.assertTrue(oops);
    }

    @Test
    void packageGood() {
        final ArgumentParser parser = new ArgumentParser();
        final String name = "org.cqfn.ast";
        Assertions.assertEquals("ast", parser.getPackage());
        boolean oops = false;
        try {
            parser.parse(Arrays.asList("--package", name));
        } catch (final CliException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        Assertions.assertEquals(name, parser.getPackage());
    }

    @Test
    void packageBad() {
        final ArgumentParser parser = new ArgumentParser();
        boolean oops = false;
        try {
            parser.parse(Arrays.asList("--package", "bad#package$name"));
        } catch (final CliException exception) {
            oops = true;
            Assertions.assertEquals(
                "The string 'bad#package$name' is not a valid Java package name",
                exception.getErrorMessage()
            );
        }
        Assertions.assertTrue(oops);
    }

    @Test
    void versionGood() {
        final ArgumentParser parser = new ArgumentParser();
        final String version = "2.1.13";
        Assertions.assertEquals("1.0.0", parser.getVersion());
        boolean oops = false;
        try {
            parser.parse(Arrays.asList("--version", version));
        } catch (final CliException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        Assertions.assertEquals(version, parser.getVersion());
    }

    @Test
    void versionBad() {
        final ArgumentParser parser = new ArgumentParser();
        boolean oops = false;
        try {
            parser.parse(Arrays.asList("-v", "1.0-bad"));
        } catch (final CliException exception) {
            oops = true;
            Assertions.assertEquals(
                "The string '1.0-bad' is not a valid version number",
                exception.getErrorMessage()
            );
        }
        Assertions.assertTrue(oops);
    }

    @Test
    void unknownArgument() {
        final ArgumentParser parser = new ArgumentParser();
        boolean oops = false;
        try {
            parser.parse(Arrays.asList("-o", "test3", "unknown", "-l", "LICENSE.txt"));
        } catch (final CliException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }
}
