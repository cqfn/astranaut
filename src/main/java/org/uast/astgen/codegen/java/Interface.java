/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.uast.astgen.utils.StringUtils;

/**
 * Java interface.
 *
 * @since 1.0
 */
public final class Interface implements Entity, Type {
    /**
     * The brief description.
     */
    private final String brief;

    /**
     * The version.
     */
    private String version;

    /**
     * The interface name.
     */
    private final String name;

    /**
     * List of extended interfaces.
     */
    private List<String> interfaces;

    /**
     * The list of methods.
     */
    private final List<MethodDescriptor> methods;

    /**
     * Constructor.
     * @param brief The brief description
     * @param name The interface name
     */
    public Interface(final String brief, final String name) {
        this.brief = brief;
        this.version = "1.0";
        this.name = name;
        this.interfaces = Collections.emptyList();
        this.methods = new ArrayList<>(0);
    }

    /**
     * Specifies the interface version.
     * @param str The version
     */
    public void setVersion(final String str) {
        this.version = Objects.requireNonNull(str);
    }

    /**
     * Set the list of interface names, that the interface extends.
     * @param list The list of interface names
     */
    public void setInterfaces(final String... list) {
        this.interfaces = Arrays.asList(list);
    }

    /**
     * Adds the method descriptor to the interface.
     * @param method The method
     */
    public void addMethod(final MethodDescriptor method) {
        this.methods.add(method);
    }

    @Override
    public String getBrief() {
        return this.brief;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public String generate(final int indent) {
        final String tabulation = StringUtils.SPACE.repeat(indent * Entity.TAB_SIZE);
        final StringBuilder builder = new StringBuilder(128);
        this.generateHeader(builder, indent);
        builder.append(tabulation).append("public interface ").append(this.name);
        this.generateParents(builder);
        builder.append(" {\n");
        this.generateMethods(builder, indent + 1);
        builder.append(tabulation).append("}\n");
        return builder.toString();
    }

    /**
     * Generates parents list (what the class extends and implements).
     * @param builder String builder where to generate
     */
    private void generateParents(final StringBuilder builder) {
        if (!this.interfaces.isEmpty()) {
            builder.append(" extends ");
            boolean comma = false;
            for (final String iface : this.interfaces) {
                if (comma) {
                    builder.append(", ");
                }
                comma = true;
                builder.append(iface);
            }
        }
    }

    /**
     * Generated source code for methods.
     * @param builder String builder where to generate
     * @param indent Indentation
     */
    private void generateMethods(final StringBuilder builder, final int indent) {
        boolean flag = false;
        for (final MethodDescriptor descriptor : this.methods) {
            if (flag) {
                builder.append('\n');
            }
            flag = true;
            builder.append(descriptor.generate(indent));
        }
    }
}
