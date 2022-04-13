/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.codegen.java;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.uast.astgen.utils.FilesReader;

/**
 * Tests for {@link Klass} class.
 *
 * @since 1.0
 */
public class ClassTest {
    /**
     * Typical class name.
     */
    private static final String CLASS_NAME = "Test";

    /**
     * The folder with test resources.
     */
    private static final String TESTS_PATH = "src/test/resources/codegen/java/";

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
    public void classWithOneMethod() {
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
    @Disabled
    public void addition() {
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
        final String expected = this.readTest("addition.txt");
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
        data.setReturnType("String");
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
