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
 * Tests covering {@link Klass} class.
 * @since 1.0.0
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods"})
class KlassTest {
    @Test
    void simpleEmptyClass() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Empty class.",
                " */",
                "class Test0 {",
                "}",
                ""
            )
        );
        final Klass klass = new Klass("Test0", "Empty class");
        final boolean result = this.testCodegen(klass, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void simplePublicEmptyClass() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Public empty class.",
                " */",
                "public class Test1 {",
                "}",
                ""
            )
        );
        final Klass klass = new Klass("Test1", "Public empty class");
        klass.makePublic();
        final boolean result = this.testCodegen(klass, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void withVersionNumber() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Class with version number.",
                " * @since 1.0.0",
                " */",
                "class Test2 {",
                "}",
                ""
            )
        );
        final Klass klass = new Klass("Test2", "Class with version number");
        klass.setVersion("1.0.0");
        final boolean result = this.testCodegen(klass, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void implementsTwoInterfaces() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Class implementing interfaces.",
                " */",
                "class Test3 implements Test4, Test5 {",
                "}",
                ""
            )
        );
        final Klass klass = new Klass("Test3", "Class implementing interfaces");
        klass.setImplementsList("Test4", "Test5");
        final boolean result = this.testCodegen(klass, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void simpleProtectedEmptyClass() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Protected empty class.",
                " */",
                "protected class Test6 {",
                "}",
                ""
            )
        );
        final Klass klass = new Klass("Test6", "Protected empty class");
        klass.makeProtected();
        final boolean result = this.testCodegen(klass, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void simpleStaticEmptyClass() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Static empty class.",
                " */",
                "static class Test7 {",
                "}",
                ""
            )
        );
        final Klass klass = new Klass("Test7", "Static empty class");
        klass.makeStatic();
        final boolean result = this.testCodegen(klass, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void classWithNestedClasses() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Class with nested classes.",
                " */",
                "class Test8 {",
                "    /**",
                "     * First nested class.",
                "     */",
                "    class Test9 {",
                "    }",
                "",
                "    /**",
                "     * Second nested class.",
                "     */",
                "    class Test10 {",
                "    }",
                "}",
                ""
            )
        );
        final Klass klass = new Klass("Test8", "Class with nested classes");
        klass.addNested(new Klass("Test9", "First nested class"));
        klass.addNested(new Klass("Test10", "Second nested class"));
        final boolean result = this.testCodegen(klass, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void simpleStaticPrivateClass() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Private empty class.",
                " */",
                "private class Test11 {",
                "}",
                ""
            )
        );
        final Klass klass = new Klass("Test11", "Private empty class");
        klass.makePrivate();
        final boolean result = this.testCodegen(klass, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void simpleStaticFinalClass() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Final empty class.",
                " */",
                "final class Test12 {",
                "}",
                ""
            )
        );
        final Klass klass = new Klass("Test12", "Final empty class");
        klass.makeFinal();
        final boolean result = this.testCodegen(klass, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void classWithFields() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Class with fields.",
                " */",
                "class Test13 {",
                "    /**",
                "     * Fourth field.",
                "     */",
                "    public static final String fourth = \"test\";",
                "",
                "    /**",
                "     * Third field.",
                "     */",
                "    private static final String third = \"test\";",
                "",
                "    /**",
                "     * Second field.",
                "     */",
                "    public String second;",
                "",
                "    /**",
                "     * First field.",
                "     */",
                "    private String first;",
                "}",
                ""
            )
        );
        final Klass klass = new Klass("Test13", "Class with fields");
        final Field first = new Field("String", "first", "First field");
        first.makePrivate();
        klass.addField(first);
        final Field second = new Field("String", "second", "Second field");
        second.makePublic();
        klass.addField(second);
        final Field third = new Field("String", "third", "Third field");
        third.makePrivate();
        third.makeStatic();
        third.makeFinal("\"test\"");
        klass.addField(third);
        final Field fourth = new Field("String", "fourth", "Fourth field");
        fourth.makePublic();
        fourth.makeStatic();
        fourth.makeFinal("\"test\"");
        klass.addField(fourth);
        final boolean result = this.testCodegen(klass, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void classWithMethods() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Class with methods.",
                " */",
                "class Test14 {",
                "    /**",
                "     * Fourth method.",
                "     */",
                "    public void fourth() {",
                "    }",
                "",
                "    /**",
                "     * Third method.",
                "     */",
                "    public static void third() {",
                "    }",
                "",
                "    /**",
                "     * Second method.",
                "     */",
                "    private void second() {",
                "    }",
                "",
                "    /**",
                "     * First method.",
                "     */",
                "    private static void first() {",
                "    }",
                "}",
                ""
            )
        );
        final Klass klass = new Klass("Test14", "Class with methods");
        final Method first = new Method("void", "first", "First method");
        first.makePrivate();
        first.makeStatic();
        klass.addMethod(first);
        final Method second = new Method("void", "second", "Second method");
        second.makePrivate();
        klass.addMethod(second);
        final Method third = new Method("void", "third", "Third method");
        third.makePublic();
        third.makeStatic();
        klass.addMethod(third);
        final Method fourth = new Method("void", "fourth", "Fourth method");
        fourth.makePublic();
        klass.addMethod(fourth);
        final boolean result = this.testCodegen(klass, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void classWithConstructor() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Class with constructor.",
                " */",
                "class Test14 {",
                "    /**",
                "     * Constructor.",
                "     */",
                "    Test14(final String[] args) {",
                "    }",
                "}",
                ""
            )
        );
        final Klass klass = new Klass("Test14", "Class with constructor");
        final Constructor ctor = klass.createConstructor();
        ctor.addArgument("String[]", "args");
        final boolean result = this.testCodegen(klass, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void classWithTwoConstructor() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Class with two constructors.",
                " */",
                "class Test15 {",
                "    /**",
                "     * Constructor.",
                "     */",
                "    protected Test15(final int key, final int value) {",
                "    }",
                "",
                "    /**",
                "     * Constructor.",
                "     */",
                "    Test15() {",
                "        this(0, 0);",
                "    }",
                "}",
                ""
            )
        );
        final Klass klass = new Klass("Test15", "Class with two constructors");
        final Constructor first = klass.createConstructor();
        first.addArgument("int", "key");
        first.addArgument("int", "value");
        first.makeProtected();
        final Constructor second = klass.createConstructor();
        second.setBody("this(0, 0);");
        final boolean result = this.testCodegen(klass, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void classWithConstructorAndField() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Class with constructor and field.",
                " */",
                "class Test16 {",
                "    /**",
                "     * Field.",
                "     */",
                "    private int value;",
                "",
                "    /**",
                "     * Constructor.",
                "     */",
                "    Test16() {",
                "        this.value = 0;",
                "    }",
                "}",
                ""
            )
        );
        final Klass klass = new Klass("Test16", "Class with constructor and field");
        final Constructor ctor = klass.createConstructor();
        ctor.setBody("this.value = 0;");
        final Field field = new Field("int", "value", "Field");
        field.makePrivate();
        klass.addField(field);
        final boolean result = this.testCodegen(klass, expected);
        Assertions.assertTrue(result);
    }

    /**
     * Tests the source code generation from an object describing a class.
     * @param klass Object describing a class
     * @param expected Expected generated code
     * @return Test result, {@code true} if the generated code matches the expected code
     */
    private boolean testCodegen(final Klass klass, final String expected) {
        boolean oops = false;
        boolean equals = false;
        try {
            final SourceCodeBuilder builder = new SourceCodeBuilder();
            klass.build(0, builder);
            final String actual = builder.toString();
            equals = expected.equals(actual);
        } catch (final BaseException ignored) {
            oops = true;
        }
        return !oops && equals;
    }
}
