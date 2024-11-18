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
import org.cqfn.astranaut.dsl.Program;

/**
 * Generates a factory provider that aggregates all generated factories.
 * @since 1.0.0
 */
public class FactoryProviderGenerator {
    /**
     * Program implemented in DSL.
     */
    private final Program program;

    /**
     * Constructor.
     * @param program Program implemented in DSL
     */
    public FactoryProviderGenerator(final Program program) {
        this.program = program;
    }

    /**
     * Creates compilation unit that contains a factory provider.
     * @param context Data required to generate Java source code
     * @return Compilation unit
     */
    public CompilationUnit createUnit(final Context context) {
        final Klass klass = new Klass(
            "FactoryProvider",
            "Factory provider that aggregates all factories."
        );
        klass.makePublic();
        klass.makeFinal();
        klass.setVersion(context.getVersion());
        klass.setImplementsList("org.cqfn.astranaut.core.base.FactoryProvider");
        final Constructor ctor = klass.createConstructor();
        ctor.makePrivate();
        final Field instance = new Field(
            "FactoryProvider",
            "INSTANCE",
            "The factory provider instance"
        );
        instance.makePublic();
        instance.makeStatic();
        instance.makeFinal("new FactoryProvider()");
        klass.addField(instance);
        this.createMapOfFactories(context, klass);
        final Method method = new Method("Factory", "getFactory");
        method.makePublic();
        method.addArgument("String", "language");
        method.setBody(
            "return FactoryProvider.FACTORIES.getOrDefault(language, DefaultFactory.EMPTY);"
        );
        klass.addMethod(method);
        final CompilationUnit unit = new CompilationUnit(
            context.getLicense(),
            context.getPackage(),
            klass
        );
        unit.addImport("java.util.Map");
        unit.addImport("org.cqfn.astranaut.core.base.Factory");
        unit.addImport("org.cqfn.astranaut.core.base.DefaultFactory");
        unit.addImport("org.cqfn.astranaut.core.utils.MapUtils");
        return unit;
    }

    /**
     * Creates a collection that contains factories supported by this provider.
     * @param context Data required to generate Java source code
     * @param klass The class to which to add the field
     */
    private void createMapOfFactories(final Context context, final Klass klass) {
        final Field field = new Field(
            "Map<String, Factory>",
            "FACTORIES",
            "Collection of factories supported by this provider"
        );
        field.makePrivate();
        field.makeStatic();
        final Package root = context.getPackage();
        final StringBuilder builder = new StringBuilder(128);
        builder.append("new MapUtils<String, Factory>()");
        for (final String language : this.program.getAllLanguages()) {
            final Package pkg = root.getSubpackage(language, "nodes");
            builder
                .append(".put(\"")
                .append(language)
                .append("\", ")
                .append(pkg.toString())
                .append('.')
                .append(language.substring(0, 1).toUpperCase(Locale.ENGLISH))
                .append(language.substring(1))
                .append("Factory.INSTANCE)");
        }
        builder.append(".make()");
        field.makeFinal(builder.toString());
        klass.addField(field);
    }
}
