/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import java.util.List;
import java.util.Locale;
import org.uast.astgen.rules.Node;

/**
 * Generates class source code for rules that describe ordinary nodes.
 *
 * @since 1.0
 */
final class OrdinaryNodeClassConstructor extends NodeConstructor {
    /**
     * The 'int' string.
     */
    private static final String STR_INT = "int";

    /**
     * Constructor.
     * @param env The environment
     * @param rule The rule
     * @param klass The class to be filled
     */
    OrdinaryNodeClassConstructor(final Environment env, final Node rule, final Klass klass) {
        super(env, rule, klass);
    }

    @Override
    public void construct() {
        final Constructor ctor = new Constructor(this.getRule().getType());
        ctor.makePrivate();
        this.getKlass().addConstructor(ctor);
        this.fillType();
        this.fillBuilder();
        this.createFragmentWithGetter();
        this.createCommonFields();
        this.createChildrenGetter();
        this.createTaggedFields();
    }

    /**
     * Fills in everything related to the type.
     */
    private void fillType() {
        final Klass subclass = this.createTypeClass();
        new OrdinaryNodeTypeConstructor(this.getEnv(), this.getRule(), subclass).run();
    }

    /**
     * Fills in everything related to the builder.
     */
    private void fillBuilder() {
        final Klass subclass = this.createBuilderClass();
        new OrdinaryNodeBuilderConstructor(this.getEnv(), this.getRule(), subclass).run();
    }

    /**
     * Creates, common fields and getters for them.
     */
    private void createCommonFields() {
        final Klass klass = this.getKlass();
        final Node rule = this.getRule();
        final Method data = new Method("getData");
        data.makeOverridden();
        data.setReturnType("String");
        data.setCode("return \"\";");
        klass.addMethod(data);
        klass.addField(new Field("List of child nodes", "List<Node>", "children"));
        final Method count = new Method("getChildCount");
        count.makeOverridden();
        count.setReturnType(OrdinaryNodeClassConstructor.STR_INT);
        if (rule.hasOptionalChild()) {
            count.setCode("return this.children.size();");
        } else {
            final Field num = new Field(
                "The number of children",
                OrdinaryNodeClassConstructor.STR_INT,
                "CHILD_COUNT"
            );
            num.makeStaticFinal();
            num.setInitExpr(String.valueOf(rule.getComposition().size()));
            klass.addField(num);
            count.setCode(String.format("return %s.CHILD_COUNT;", rule.getType()));
        }
        klass.addMethod(count);
    }

    /**
     * Creates fields for tagged nodes and getters for them.
     */
    private void createTaggedFields() {
        final Klass klass = this.getKlass();
        final List<TaggedChild> tags = this.getEnv().getTags(this.getType());
        for (final TaggedChild child : tags) {
            final String type = child.getType();
            final String tag = child.getTag();
            final Field field = new Field(
                String.format("Child with the '%s' tag", tag),
                type,
                tag
            );
            klass.addField(field);
            final Method getter = new Method(
                String.format("Returns the child with the '%s' tag", tag),
                String.format(
                    "get%s%s",
                    tag.substring(0, 1).toUpperCase(Locale.ENGLISH),
                    tag.substring(1)
                )
            );
            getter.setReturnType(type, "The node");
            getter.setCode(String.format("return this.%s;", tag));
            if (child.isOverridden()) {
                getter.makeOverridden();
            }
            klass.addMethod(getter);
        }
    }
}
