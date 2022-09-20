/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Ivan Kniazkov
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

import java.io.IOException;
import org.cqfn.astranaut.core.utils.FilesReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Klass} class.
 *
 * @since 0.1.5
 */
@SuppressWarnings("PMD.TooManyMethods")
class ClassTest {
    /**
     * Typical class name.
     */
    private static final String CLASS_NAME = "Test";

    /**
     * The folder with test resources.
     */
    private static final String TESTS_PATH = "src/test/resources/codegen/java/";

    /**
     * The 'String' string.
     */
    private static final String STR_STRING = "String";

    /**
     * The 'Addition' string.
     */
    private static final String STR_ADDITION = "Addition";

    /**
     * The 'Expression' string.
     */
    private static final String STR_EXPRESSION = "Expression";

    /**
     * The 'int' string.
     */
    private static final String STR_INT = "int";

    /**
     * The 'Type' string.
     */
    private static final String STR_TYPE = "Type";

    /**
     * The 'Expression' string.
     */
    private static final String STR_FRAGMENT = "Fragment";

    /**
     * Generating a public class with one method.
     */
    @Test
    void classWithOneMethod() {
        final Klass klass = new Klass("Test class with one method", ClassTest.CLASS_NAME);
        klass.addMethod(this.createPublicMethod());
        final String expected = this.readTest("public_class_with_one_method.txt");
        final String actual = klass.generate(0);
        Assertions.assertEquals(expected, actual);
    }

    /**
     * Creating the whole 'real' class.
     */
    @Test
    void addition() {
        final Klass klass = new Klass(
            "Node that describes the 'Addition' type",
            ClassTest.STR_ADDITION
        );
        klass.makeFinal();
        klass.setInterfaces("BinaryExpression");
        this.createFields(klass);
        final Constructor ctor = new Constructor(ClassTest.STR_ADDITION);
        ctor.makePrivate();
        klass.addConstructor(ctor);
        this.createMethods(klass);
        this.createMoreMethods(klass);
        this.createInnerClass(klass);
        final String expected = this.readTest("addition.txt");
        final String actual = klass.generate(0);
        Assertions.assertEquals(expected, actual);
    }

    /**
     * Generating a public class with one method.
     */
    @Test
    void classWithSuppressedWarning() {
        final Klass klass = new Klass(
            "Test class with suppressed warning",
            ClassTest.CLASS_NAME
        );
        klass.addMethod(this.createPublicMethod());
        klass.suppressWarnings("PMD.TooManyMethods");
        final String expected = this.readTest("class_with_suppressed_warning.txt");
        final String actual = klass.generate(0);
        Assertions.assertEquals(expected, actual);
    }

    /**
     * Creates a method for test purposes.
     * @return Method
     */
    private Method createPublicMethod() {
        final Method method = new Method("This method does something", "doSomething");
        method.setCode("System.out.print(\"it works!\");");
        return method;
    }

    /**
     * Creates fields for the 'real' class.
     * @param klass The object where to create
     */
    private void createFields(final Klass klass) {
        final Field type = new Field("The type", ClassTest.STR_TYPE, "TYPE");
        type.makePublic();
        type.makeStaticFinal();
        type.setInitExpr("new TypeImpl()");
        klass.addField(type);
        final Field fragment = new Field(
            "The fragment associated with the node",
            ClassTest.STR_FRAGMENT,
            "fragment"
        );
        klass.addField(fragment);
        final Field children = new Field("List of child nodes", "List<Node>", "children");
        klass.addField(children);
        final Field left = new Field("Node with the 'left' tag", ClassTest.STR_EXPRESSION, "left");
        klass.addField(left);
        final Field right = new Field(
            "Node with the 'right' tag",
            ClassTest.STR_EXPRESSION,
            "right"
        );
        klass.addField(right);
    }

    /**
     * Creates methods for the 'real' class.
     * @param klass The object where to create
     */
    private void createMethods(final Klass klass) {
        final Method fragment = new Method("getFragment");
        fragment.setReturnType(ClassTest.STR_FRAGMENT);
        fragment.setCode("return this.fragment;");
        klass.addMethod(fragment);
        final Method type = new Method("getType");
        type.setReturnType(ClassTest.STR_TYPE);
        type.setCode("return Addition.TYPE;");
        klass.addMethod(type);
        final Method data = new Method("getData");
        data.setReturnType(ClassTest.STR_STRING);
        data.setCode("return \"\";");
        klass.addMethod(data);
        final Method childcnt = new Method("getChildCount");
        childcnt.setReturnType(ClassTest.STR_INT);
        childcnt.setCode("return 2;");
        klass.addMethod(childcnt);
    }

    /**
     * Creates methods for the 'real' class.
     * @param klass The object where to create
     */
    private void createMoreMethods(final Klass klass) {
        final Method child = new Method("getChild");
        child.addArgument(ClassTest.STR_INT, "index");
        child.setReturnType("Node");
        child.setCode("return this.children.get(index);");
        klass.addMethod(child);
        final Method left = new Method("getLeft");
        left.setReturnType(ClassTest.STR_EXPRESSION);
        left.setCode("return this.left;");
        klass.addMethod(left);
        final Method right = new Method("getRight");
        right.setReturnType(ClassTest.STR_EXPRESSION);
        right.setCode("return this.right;");
        klass.addMethod(right);
    }

    /**
     * Creates inner class for the 'real' class.
     * @param klass The object where to create
     */
    private void createInnerClass(final Klass klass) {
        final Klass inner = new Klass(
            "Type descriptor of the 'Addition' node",
            "TypeImpl"
        );
        inner.setInterfaces(ClassTest.STR_TYPE);
        inner.makePrivate();
        inner.makeStatic();
        klass.addClass(inner);
        this.createFieldsInner(inner);
        this.createComplexField(inner);
    }

    /**
     * Creates fields for the inner class of the 'real' class.
     * @param inner The object where to create
     */
    private void createFieldsInner(final Klass inner) {
        final Field name = new Field("The name", ClassTest.STR_STRING, "NAME");
        name.makePrivate();
        name.makeStaticFinal();
        name.setInitExpr("\"Addition\"");
        inner.addField(name);
        final Field binexpr = new Field(
            "The 'BinaryExpression' string",
            ClassTest.STR_STRING,
            "BINARY_EXPRESSION"
        );
        binexpr.makePrivate();
        binexpr.makeStaticFinal();
        binexpr.setInitExpr("\"BinaryExpression\"");
        inner.addField(binexpr);
        final Field expression = new Field(
            "The 'Expression' string",
            ClassTest.STR_STRING,
            "EXPRESSION"
        );
        expression.makePrivate();
        expression.makeStaticFinal();
        expression.setInitExpr("\"Expression\"");
        inner.addField(expression);
    }

    /**
     * Creates a field with complex initialization.
     * @param klass The object where to create
     */
    private void createComplexField(final Klass klass) {
        final Field field = new Field("Hierarchy", "List<String>", "HIERARCHY");
        field.makePrivate();
        field.makeStaticFinal();
        klass.addField(field);
        final String list = "TypeImpl.NAME, TypeImpl.BINARY_EXPRESSION, TypeImpl.EXPRESSION";
        final String init =
            String.format("Collections.unmodifiableList(Arrays.asList(%s))", list);
        field.setInitExpr(init);
    }

    /**
     * Reads test source from the file.
     * @param name The file name
     * @return Test source
     */
    private String readTest(final String name) {
        String result = "";
        boolean oops = false;
        try {
            result = new FilesReader(ClassTest.TESTS_PATH.concat(name))
                .readAsString();
        } catch (final IOException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        return result;
    }
}
