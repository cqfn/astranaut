/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.codegen.java;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link MethodBody} class.
 *
 * @since 1.0
 */
public class MethodBodyTest {
    /**
     * Testing code generation with a single line.
     */
    @Test
    public void singleLine() {
        final MethodBody body = new MethodBody();
        body.setCode(" return x;");
        final String code = body.generate(2);
        Assertions.assertEquals("        return x;\n", code);
    }

    /**
     * Testing code generation with two lines.
     */
    @Test
    public void twoLines() {
        final MethodBody body = new MethodBody();
        body.setCode("int x = 2 + 3;\nreturn x;");
        final String code = body.generate(1);
        Assertions.assertEquals("    int x = 2 + 3;\n    return x;\n", code);
    }
}
