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
package org.cqfn.astranaut.codegen.java;

import java.util.Arrays;
import org.cqfn.astranaut.exceptions.BaseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link Method} class.
 * @since 1.0.0
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods"})
class MethodTest {
    @Test
    void simpleMethod() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Simple method.",
                " */",
                "String getData() {",
                "    return \"\";",
                "}",
                ""
            )
        );
        final Method method = new Method("String", "getData", "Simple method");
        method.setBody("return \"\";");
        final boolean result = this.testCodegen(method, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void publicMethod() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Public method.",
                " */",
                "public String getData() {",
                "    return \"\";",
                "}",
                ""
            )
        );
        final Method method = new Method("String", "getData", "Public method");
        method.makePublic();
        method.setBody("return \"\";");
        final boolean result = this.testCodegen(method, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void protectedMethod() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Protected method.",
                " */",
                "protected String getData() {",
                "    return \"\";",
                "}",
                ""
            )
        );
        final Method method = new Method("String", "getData", "Protected method");
        method.makeProtected();
        method.setBody("return \"\";");
        final boolean result = this.testCodegen(method, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void privateMethod() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Private method.",
                " */",
                "private String getData() {",
                "    return \"\";",
                "}",
                ""
            )
        );
        final Method method = new Method("String", "getData", "Private method");
        method.makePrivate();
        method.setBody("return \"\";");
        final boolean result = this.testCodegen(method, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void staticMethod() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Static method.",
                " */",
                "static String getData() {",
                "    return \"\";",
                "}",
                ""
            )
        );
        final Method method = new Method("String", "getData", "Static method");
        method.makeStatic();
        method.setBody("return \"\";");
        final boolean result = this.testCodegen(method, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void finalMethod() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Final method.",
                " */",
                "final String getData() {",
                "    return \"\";",
                "}",
                ""
            )
        );
        final Method method = new Method("String", "getData", "Final method");
        method.makeFinal();
        method.setBody("return \"\";");
        final boolean result = this.testCodegen(method, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void overriddenMethod() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "@Override",
                "public String toString() {",
                "    return \"\";",
                "}",
                ""
            )
        );
        final Method method = new Method("String", "toString");
        method.makePublic();
        method.setBody("return \"\";");
        final boolean result = this.testCodegen(method, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void methodWithMultilineBody() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Method with multi-line body.",
                " */",
                "public void doSomething() {",
                "    if (true) {",
                "        System.out.println(\"it works!\");",
                "    }",
                "    return;",
                "}",
                ""
            )
        );
        final Method method = new Method(
            "void",
            "doSomething",
            "Method with multi-line body"
        );
        method.makePublic();
        method.setBody("   if (true) { System.out.println(\"it works!\"); } \n return; ");
        final boolean result = this.testCodegen(method, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void methodWithMultilineStatement() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Method with multi-line body.",
                " */",
                "public void doSomething() {",
                "    if (true) {",
                "        final StringBuilder builder = new StringBuilder();",
                "        builder.append('a')",
                "            .append('b')",
                "            .append('c');",
                "        System.out.println(builder.toString());",
                "    }",
                "    return;",
                "}",
                ""
            )
        );
        final Method method = new Method(
            "void",
            "doSomething",
            "Method with multi-line body"
        );
        method.makePublic();
        method.setBody(
            "if (true) { final StringBuilder builder = new StringBuilder(); builder.append('a')\n.append('b')\n.append('c');  System.out.println(builder.toString()); } \n return; "
        );
        final boolean result = this.testCodegen(method, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void methodWithArguments() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Method with arguments.",
                " */",
                "static int max(final int first, final int second) {",
                "    return first > second ? first : second;",
                "}",
                ""
            )
        );
        final Method method = new Method("int", "max", "Method with arguments");
        method.makeStatic();
        method.addArgument("int", "first");
        method.addArgument("int", "second");
        method.setBody("return first > second ? first : second;");
        final boolean result = this.testCodegen(method, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void methodWithSyntaxError() {
        final Method method = new Method("void", "doSomething");
        method.setBody("if (true) { aaa }");
        final SourceCodeBuilder builder = new SourceCodeBuilder();
        boolean oops = false;
        try {
            method.build(0, builder);
        } catch (final BaseException exception) {
            oops = true;
            Assertions.assertEquals("Codegen", exception.getInitiator());
            Assertions.assertEquals(
                "Syntax error in source code: 'aaa }'",
                exception.getErrorMessage()
            );
        }
        Assertions.assertTrue(oops);
    }

    @Test
    void methodWithLineThatIsTooLong() {
        final Method method = new Method("void", "doSomething");
        method.setBody(
            "System.out.println(\"The infantile goat accompanies this delightful sunset with an indifferent stare.\")"
        );
        final SourceCodeBuilder builder = new SourceCodeBuilder();
        Assertions.assertThrows(BaseException.class, () -> method.build(0, builder));
    }

    /**
     * Tests the source code generation from an object describing a method.
     * @param method Object describing a method
     * @param expected Expected generated code
     * @return Test result, {@code true} if the generated code matches the expected code
     */
    private boolean testCodegen(final Method method, final String expected) {
        boolean oops = false;
        boolean equals = false;
        try {
            final SourceCodeBuilder builder = new SourceCodeBuilder();
            method.build(0, builder);
            final String actual = builder.toString();
            equals = expected.equals(actual);
        } catch (final BaseException ignored) {
            oops = true;
        }
        return !oops && equals;
    }
}
