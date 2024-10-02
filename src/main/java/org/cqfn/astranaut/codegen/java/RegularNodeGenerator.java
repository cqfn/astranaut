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

import java.util.Collections;
import java.util.Set;
import org.cqfn.astranaut.dsl.RegularNodeDescriptor;

/**
 * Generator that creates source code for a regular node, that is, a node that may have
 *  a limited number of some child nodes and no data.
 * @since 1.0.0
 */
public final class RegularNodeGenerator implements RuleGenerator {
    /**
     * The rule that describes regular node.
     */
    private final RegularNodeDescriptor rule;

    /**
     * Constructor.
     * @param rule The rule that describes regular node
     */
    public RegularNodeGenerator(final RegularNodeDescriptor rule) {
        this.rule = rule;
    }

    @Override
    public Set<CompilationUnit> createUnits(final Context context) {
        final String name = this.rule.getName();
        final String brief = String.format("Node of the '%s' type.", name);
        final Klass node = new Klass(name, brief);
        node.makePublic();
        node.makeFinal();
        node.setImplementsList("Node");
        node.setVersion(context.getVersion());
        node.addNested(this.createTypeClass(context));
        node.addNested(this.createBuilderClass(context));
        this.fillNodeClass(node);
        final CompilationUnit unit = new CompilationUnit(
            context.getLicense(),
            context.getPackage(),
            node
        );
        final String base = "org.cqfn.astranaut.core.base.";
        unit.addImport(base.concat("Node"));
        unit.addImport(base.concat("Type"));
        unit.addImport(base.concat("Builder"));
        return Collections.singleton(unit);
    }

    /**
     * Fills the class describing the node with fields and methods.
     * @param node Class describing the node
     */
    private void fillNodeClass(final Klass node) {
        final String name = this.rule.getName();
        final Field typename = new Field("String", "NAME", "Name of the type");
        typename.makePublic();
        typename.makeStatic();
        typename.makeFinal(String.format("\"%s\"", name));
        node.addField(typename);
        final Field typeobj = new Field("Type", "TYPE", "Type of the node");
        typeobj.makePublic();
        typeobj.makeStatic();
        typeobj.makeFinal(String.format("new %sType()", name));
        node.addField(typeobj);
    }

    /**
     * Creates a nested class that implements the node type.
     * @param context Context
     * @return Class description
     */
    private Klass createTypeClass(final Context context) {
        final String name = this.rule.getName();
        final Klass type = new Klass(
            String.format("%sType", name),
            String.format("Type implementation describing '%s' nodes.", name)
        );
        type.makePrivate();
        type.makeStatic();
        type.makeFinal();
        type.setImplementsList("Type");
        type.setVersion(context.getVersion());
        return type;
    }

    /**
     * Creates a nested class that implements the node builder.
     * @param context Context
     * @return Class description
     */
    private Klass createBuilderClass(final Context context) {
        final String name = this.rule.getName();
        final Klass builder = new Klass(
            String.format("Constructor"),
            String.format("Constructor (builder) that creates nodes of the '%s' type.", name)
        );
        builder.makePublic();
        builder.makeStatic();
        builder.makeFinal();
        builder.setImplementsList("Builder");
        builder.setVersion(context.getVersion());
        return builder;
    }
}
