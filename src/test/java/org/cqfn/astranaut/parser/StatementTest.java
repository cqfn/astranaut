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
package org.cqfn.astranaut.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link Statement} class.
 * @since 1.0.0
 */
@SuppressWarnings("PMD.CloseResource")
class StatementTest {
    @Test
    void multilineStatement() {
        final Statement.Constructor ctor = new Statement.Constructor();
        ctor.setFilename("test.dsl");
        ctor.setBegin(1);
        ctor.setEnd(3);
        ctor.setCode("  This\n<-\n  0");
        final Statement stmt = ctor.createStatement();
        Assertions.assertEquals("test.dsl, 1-3", stmt.getLocation().toString());
        Assertions.assertEquals("This <- 0", stmt.getCode());
        Assertions.assertEquals("test.dsl, 1-3: This <- 0", stmt.toString());
    }

    @Test
    void singleLineStatement() {
        final Statement.Constructor ctor = new Statement.Constructor();
        ctor.setBegin(1);
        ctor.setEnd(1);
        final String code = "Addition <- left@expression, right@expression";
        ctor.setCode(code);
        final Statement stmt = ctor.createStatement();
        Assertions.assertEquals("1", stmt.getLocation().toString());
        Assertions.assertEquals("", stmt.getLanguage());
        Assertions.assertEquals(code, stmt.getCode());
        Assertions.assertEquals("1: ".concat(code), stmt.toString());
    }

    @Test
    void statementWithLanguage() {
        final Statement.Constructor ctor = new Statement.Constructor();
        ctor.setBegin(1);
        ctor.setEnd(1);
        final String code = "java: Synchronized <- StatementBlock";
        ctor.setCode(code);
        final Statement stmt = ctor.createStatement();
        Assertions.assertEquals("java", stmt.getLanguage());
        Assertions.assertEquals("Synchronized <- StatementBlock", stmt.getCode());
    }

    @Test
    void badStatement() {
        final Statement.Constructor ctor = new Statement.Constructor();
        Assertions.assertThrows(IllegalStateException.class, ctor::createStatement);
        ctor.setBegin(-2);
        Assertions.assertThrows(IllegalStateException.class, ctor::createStatement);
        ctor.setBegin(3);
        Assertions.assertThrows(IllegalStateException.class, ctor::createStatement);
        ctor.setEnd(1);
        Assertions.assertThrows(IllegalStateException.class, ctor::createStatement);
        ctor.setEnd(7);
        Assertions.assertThrows(IllegalStateException.class, ctor::createStatement);
    }
}
