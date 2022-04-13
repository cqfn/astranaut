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
     * The 'Expression' string.
     */
    private static final String STR_EXPRESSION = "Expression";

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
        final Klass klass = new Klass("Node that describes the 'Addition' type", "Addition");
        klass.makeFinal();
        klass.setInterfaces("BinaryExpression");
        final Field type = new Field("The type", "Type", "TYPE");
        type.makePublic();
        type.makeStaticFinal();
        type.setInitExpr("new TypeImpl()");
        klass.addField(type);
        final Field fragment = new Field(
            "The fragment associated with the node",
            "Fragment",
            "fragment"
        );
        klass.addField(fragment);
        final Field children = new Field("List  of child nodes", "List<Node>", "children");
        klass.addField(children);
        final Field left = new Field("Node with the 'left' tag", ClassTest.STR_EXPRESSION, "left");
        klass.addField(left);
        final Field right = new Field(
            "Node with the 'right' tag",
            ClassTest.STR_EXPRESSION,
            "right"
        );
        klass.addField(right);
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
