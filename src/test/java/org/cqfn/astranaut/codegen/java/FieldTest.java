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
 * Tests covering {@link Field} class.
 * @since 1.0.0
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods"})
class FieldTest {
    @Test
    void simpleField() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Simple field.",
                " */",
                "String value;",
                ""
            )
        );
        final Field field = new Field("String", "value", "Simple field");
        final boolean result = this.testCodegen(field, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void fieldWithInitialValue() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Field with initial value.",
                " */",
                "String value = \"test\";",
                ""
            )
        );
        final Field field = new Field("String", "value", "Field with initial value");
        field.setInitial("\"test\"");
        final boolean result = this.testCodegen(field, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void publicField() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Public field.",
                " */",
                "public String value;",
                ""
            )
        );
        final Field field = new Field("String", "value", "Public field");
        field.makePublic();
        final boolean result = this.testCodegen(field, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void protectedField() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Protected field.",
                " */",
                "protected String value;",
                ""
            )
        );
        final Field field = new Field("String", "value", "Protected field");
        field.makeProtected();
        final boolean result = this.testCodegen(field, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void privateField() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Private field.",
                " */",
                "private String value;",
                ""
            )
        );
        final Field field = new Field("String", "value", "Private field");
        field.makePrivate();
        final boolean result = this.testCodegen(field, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void staticField() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Static field.",
                " */",
                "static String value;",
                ""
            )
        );
        final Field field = new Field("String", "value", "Static field");
        field.makeStatic();
        final boolean result = this.testCodegen(field, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void finalField() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Final field.",
                " */",
                "final String value = \"test\";",
                ""
            )
        );
        final Field field = new Field("String", "value", "Final field");
        field.makeFinal("\"test\"");
        final boolean result = this.testCodegen(field, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void fieldWithLongInitialString() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Final field.",
                " */",
                "final String value =",
                "    \"The infantile goat accompanies this delightful sunset with an indifferent stare.\";",
                ""
            )
        );
        final Field field = new Field("String", "value", "Final field");
        field.makeFinal(
            "\"The infantile goat accompanies this delightful sunset with an indifferent stare.\""
        );
        final boolean result = this.testCodegen(field, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void fieldWithLongInitialCallChain() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Final field.",
                " */",
                "final String value =",
                "    new StringBuilder()",
                "        .append(\"aaaaa\")",
                "        .append(\"bbbbb\")",
                "        .append(\"ccccc\")",
                "        .append(\"ddddd\")",
                "        .toString();",
                ""
            )
        );
        final Field field = new Field("String", "value", "Final field");
        field.makeFinal(
            "new StringBuilder().append(\"aaaaa\").append(\"bbbbb\").append(\"ccccc\").append(\"ddddd\").toString()"
        );
        final boolean result = this.testCodegen(field, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void fieldWithLongInitialDepthCall() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Final field.",
                " */",
                "final Set<String> value =",
                "    new TreeSet<>(",
                "        Arrays.asList(",
                "            \"aaaaaaa\",",
                "            \"bbbbbbb\",",
                "            \"ccccccc\",",
                "            \"ddddddd\",",
                "            \"eeeeeee\",",
                "            \"fffffff\"",
                "        )",
                "    );",
                ""
            )
        );
        final Field field = new Field("Set<String>", "value", "Final field");
        field.makeFinal(
            "new TreeSet<>(Arrays.asList(\"aaaaaaa\", \"bbbbbbb\", \"ccccccc\", \"ddddddd\", \"eeeeeee\", \"fffffff\"))"
        );
        final boolean result = this.testCodegen(field, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void fieldWithVeryLongInitialString() {
        final Field field = new Field("String", "value", "Final field");
        field.makeFinal(
            "new StringBuilder().append(\"aaaaa\").append(\"The crazy stinky infantile old goat accompanies this delightful sunset with an indifferent stare.\").toString()"
        );
        Assertions.assertTrue(this.testBadField(field));
    }

    @Test
    void fieldWithStrangeClassName() {
        final Field field = new Field("String", "value", "Final field");
        field.makeFinal(
            "new TheCrazyStinkyInfantileOldGoatAccompaniesThisDelightfulSunsetWithAnIndifferentStareStringBuilder().toString()"
        );
        Assertions.assertTrue(this.testBadField(field));
    }

    @Test
    void fieldWithInitialValueWithNotClosedBracket() {
        final Field field = new Field("Set<String>", "value", "Final field");
        field.makeFinal(
            "new TreeSet<>(Arrays.asList(\"aaaaaaaaaaa\", \"bbbbbbb\", \"ccccccc\", \"ddddddd\", \"eeeeeee\", \"fffffff\")"
        );
        boolean oops = false;
        try {
            final SourceCodeBuilder builder = new SourceCodeBuilder();
            field.build(0, builder);
        } catch (final BaseException exception) {
            oops = true;
            Assertions.assertEquals("Codegen", exception.getInitiator());
            Assertions.assertEquals("Unclosed parenthesis", exception.getErrorMessage());
        }
        Assertions.assertTrue(oops);
    }

    @Test
    void fieldWithVeryLongInitialPart() {
        final Field field = new Field("Set<String>", "value", "Final field");
        field.makeFinal(
            "new TreeSet<>(Arrays.asList(\"aaaaaaa\", \"bbbbbbb\", \"The crazy stinky infantile old goat accompanies this delightful sunset with an indifferent stare\", \"ddddddd\", \"eeeeeee\", \"fffffff\"))"
        );
        Assertions.assertTrue(this.testBadField(field));
    }

    /**
     * Tests the source code generation from an object describing a field.
     * @param field Object describing a field
     * @param expected Expected generated code
     * @return Test result, {@code true} if the generated code matches the expected code
     */
    private boolean testCodegen(final Field field, final String expected) {
        boolean oops = false;
        boolean equals = false;
        try {
            final SourceCodeBuilder builder = new SourceCodeBuilder();
            field.build(0, builder);
            final String actual = builder.toString();
            equals = expected.equals(actual);
        } catch (final BaseException ignored) {
            oops = true;
        }
        return !oops && equals;
    }

    /**
     * Tests codegeneration of a field with bad parameters,
     *  the codegenerator is expected to throw an exception.
     * @param field Object describing a field
     * @return Testing result, {@code true} if exception was thrown
     */
    private boolean testBadField(final Field field) {
        boolean oops = false;
        try {
            final SourceCodeBuilder builder = new SourceCodeBuilder();
            field.build(0, builder);
        } catch (final BaseException ignored) {
            oops = true;
        }
        return oops;
    }
}
