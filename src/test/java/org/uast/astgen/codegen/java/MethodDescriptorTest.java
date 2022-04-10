/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.codegen.java;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link MethodDescriptor} class.
 *
 * @since 1.0
 */
public class MethodDescriptorTest {
    /**
     * Creating simple header.
     */
    @Test
    public void voidHeaderWithoutMethod() {
        final MethodDescriptor descriptor = new MethodDescriptor();
        descriptor.setDescription("This method does nothing");
        final String header = descriptor.genHeader(1);
        Assertions.assertEquals(
            "    /\u002a*\n     * This method does nothing.\n     */\n",
            header
        );
    }
}
