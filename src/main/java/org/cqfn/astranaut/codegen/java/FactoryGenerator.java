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

import java.util.Locale;
import org.cqfn.astranaut.dsl.AbstractNodeDescriptor;
import org.cqfn.astranaut.dsl.NodeDescriptor;
import org.cqfn.astranaut.dsl.Program;

/**
 * Generates factories for nodes.
 * @since 1.0.0
 */
public class FactoryGenerator {
    /**
     * Program implemented in DSL.
     */
    private final Program program;

    /**
     * Constructor.
     * @param program Program implemented in DSL
     */
    public FactoryGenerator(final Program program) {
        this.program = program;
    }

    /**
     * Creates compilation unit that contains a factory.
     * @param language Language for which the factory is generated
     * @param context Data required to generate Java source code
     * @return Compilation unit
     */
    public CompilationUnit createUnit(final String language, final Context context) {
        final String name = language
            .substring(0, 1)
            .toUpperCase(Locale.ENGLISH)
            .concat(language.substring(1));
        final String classname = name.concat("Factory");
        final String brief = String.format(
            "Factory that creates nodes for the '%s' language.",
            name
        );
        final Klass klass = new Klass(classname, brief);
        klass.makePublic();
        klass.makeFinal();
        klass.setVersion(context.getVersion());
        klass.setSuperclass("DefaultFactory");
        final Constructor ctor = klass.createConstructor();
        ctor.makePrivate();
        ctor.setBody(String.format("super(%s.TYPES);", classname));
        final Field instance = new Field(
            "Factory",
            "INSTANCE",
            "The factory instance"
        );
        instance.makePublic();
        instance.makeStatic();
        instance.makeFinal(String.format("new %s()", classname));
        klass.addField(instance);
        FactoryGenerator.createMapOfProperties(language, klass);
        this.createMapOfTypes(language, klass);
        final CompilationUnit unit = new CompilationUnit(
            context.getLicense(),
            context.getPackage(),
            klass
        );
        unit.addImport("java.util.Map");
        unit.addImport("org.cqfn.astranaut.core.base.DefaultFactory");
        unit.addImport("org.cqfn.astranaut.core.base.Factory");
        unit.addImport("org.cqfn.astranaut.core.base.Type");
        unit.addImport("org.cqfn.astranaut.core.utils.MapUtils");
        return unit;
    }

    /**
     * Creates a collection containing the default properties of the nodes describing the language.
     * @param language Language for which the factory is generated
     * @param klass The class to which to add the field
     */
    private static void createMapOfProperties(final String language, final Klass klass) {
        final Field field = new Field(
            "Map<String, String>",
            "PROPERTIES",
            "Default properties of nodes describing the language"
        );
        field.makePublic();
        field.makeStatic();
        final String color;
        if (language.equals("common")) {
            color = "green";
        } else {
            color = "red";
        }
        final String initial = String.format(
            "new MapUtils<String, String>().put(\"language\", \"%s\").put(\"color\", \"%s\").make()",
            language,
            color
        );
        field.makeFinal(initial);
        klass.addField(field);
    }

    /**
     * Creates a collection that contains types supported by this factory.
     * @param language Language for which the factory is generated
     * @param klass The class to which to add the field
     */
    private void createMapOfTypes(final String language, final Klass klass) {
        final Field field = new Field(
            "Map<String, Type>",
            "TYPES",
            "Collection of types supported by this factory"
        );
        field.makePrivate();
        field.makeStatic();
        final StringBuilder builder = new StringBuilder(128);
        builder.append("new MapUtils<String, Type>()");
        for (final NodeDescriptor rule : this.program.getNodeDescriptorsForLanguage(language)) {
            if (!(rule instanceof AbstractNodeDescriptor)) {
                builder
                    .append(".put(")
                    .append(rule.getName())
                    .append(".NAME, ")
                    .append(rule.getName())
                    .append(".TYPE)");
            }
        }
        builder.append(".make()");
        field.makeFinal(builder.toString());
        klass.addField(field);
    }
}
