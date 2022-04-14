/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import java.util.List;
import org.uast.astgen.rules.Node;

/**
 * Generates type subclass source code for rules that describe nodes.
 *
 * @since 1.0
 */
final class NodeTypeConstructor extends NodeConstructor {
    /**
     * The 'List&lt;String&gt;' type.
     */
    private static final String LIST_STRING = "List<String>";

    /**
     * Static string generator to avoid Qulice error messages.
     */
    private final StaticStringGenerator ssg;

    /**
     * Constructor.
     * @param env The environment
     * @param rule The rule
     * @param klass The class to be filled
     */
    NodeTypeConstructor(final Environment env, final Node rule, final Klass klass) {
        super(env, rule, klass);
        this.ssg = new StaticStringGenerator(klass);
    }

    @Override
    public void construct() {
        final Node rule = this.getRule();
        final Klass klass = this.getKlass();
        final Method name = new Method("getName");
        name.setReturnType("String");
        name.setCode(String.format("return %s;", this.ssg.getFieldName(rule.getType())));
        klass.addMethod(name);
        this.fillHierarchy();
    }

    /**
     * Fills in everything related to the hierarchy.
     */
    private void fillHierarchy() {
        final Klass klass = this.getKlass();
        final List<String> hierarchy = this.getEnv().getHierarchy(this.getRule().getType());
        final StringBuilder init  = new StringBuilder(128);
        init.append("Collections.unmodifiableList(Arrays.asList(");
        boolean flag = false;
        for (final String item : hierarchy) {
            if (flag) {
                init.append(", ");
            }
            flag = true;
            init.append(this.ssg.getFieldName(item));
        }
        init.append("))");
        final Field field = new Field("Hierarchy", NodeTypeConstructor.LIST_STRING, "HIERARCHY");
        field.makePrivate();
        field.makeStaticFinal();
        field.setInitExpr(init.toString());
        klass.addField(field);
        final Method getter = new Method("getHierarchy");
        getter.setReturnType(NodeTypeConstructor.LIST_STRING);
        getter.setCode("return TypeImpl.HIERARCHY;");
        klass.addMethod(getter);
    }
}
