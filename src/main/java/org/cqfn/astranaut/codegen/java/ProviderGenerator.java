/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Ivan Kniazkov
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
import java.util.Locale;
import org.cqfn.astranaut.dsl.Program;

/**
 * Generates a provider that aggregates all generated factories and transformers.
 * @since 1.0.0
 */
public class ProviderGenerator {
    /**
     * Program implemented in DSL.
     */
    private final Program program;

    /**
     * Constructor.
     * @param program Program implemented in DSL
     */
    public ProviderGenerator(final Program program) {
        this.program = program;
    }

    /**
     * Creates compilation unit that contains a factory provider.
     * @param context Data required to generate Java source code
     * @return Compilation unit
     */
    public CompilationUnit createUnit(final Context context) {
        final Klass klass = new Klass(
            "Provider",
            "Provider that aggregates all factories and transformers."
        );
        klass.makePublic();
        klass.makeFinal();
        klass.setVersion(context.getVersion());
        klass.setImplementsList("org.cqfn.astranaut.core.base.Provider");
        final Constructor ctor = klass.createConstructor();
        ctor.makePrivate();
        ProviderGenerator.createGetFactoryMethod(klass);
        this.createMapOfFactories(context, klass);
        this.createGetTransformerMethod(context, klass);
        final CompilationUnit unit = new CompilationUnit(
            context.getLicense(),
            context.getPackage(),
            klass
        );
        ProviderGenerator.createOtherStaticFields(klass);
        unit.addImport("java.util.Locale");
        unit.addImport("java.util.Map");
        unit.addImport("org.cqfn.astranaut.core.base.Factory");
        unit.addImport("org.cqfn.astranaut.core.base.DefaultFactory");
        unit.addImport("org.cqfn.astranaut.core.base.Transformer");
        unit.addImport("org.cqfn.astranaut.core.utils.MapUtils");
        return unit;
    }

    /**
     * Creates different static fields.
     * @param klass The class in which the fields are created
     */
    private static void createOtherStaticFields(final Klass klass) {
        final Field instance = new Field(
            "Provider",
            "INSTANCE",
            "The provider instance"
        );
        instance.makePublic();
        instance.makeStatic();
        instance.makeFinal("new Provider()");
        klass.addField(instance);
        final Field deftrans = new Field(
            "Transformer",
            "DEF_TRANS",
            "Default transformer that doesn't transform anything"
        );
        deftrans.makePrivate();
        deftrans.makeStatic();
        deftrans.makeFinal("node -> node");
        klass.addField(deftrans);
    }

    /**
     * Creates the 'getFactory' method.
     * @param klass The class in which the method is created
     */
    private static void createGetFactoryMethod(final Klass klass) {
        final Method method = new Method("Factory", "getFactory");
        method.makePublic();
        method.addArgument("String", "language");
        method.setBody(
            String.join(
                "\n",
                Arrays.asList(
                    "final String lowercase = language.toLowerCase(Locale.ENGLISH);",
                    "return Provider.FACTORIES.getOrDefault(lowercase, DefaultFactory.EMPTY);"
                )
            )
        );
        klass.addMethod(method);
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

    /**
     * Creates the 'getTransformer' method.
     * @param context Data required to generate Java source code
     * @param klass The class in which the method is created
     */
    private void createGetTransformerMethod(final Context context, final Klass klass) {
        final Method method = new Method("Transformer", "getTransformer");
        method.makePublic();
        method.addArgument("String", "language");
        if (this.program.getAllTransformationDescriptors().isEmpty()) {
            method.setBody(
                "return Provider.DEF_TRANS;"
            );
        } else {
            final Field field = new Field(
                "Map<String, Transformer>",
                "TRANSFORMERS",
                "Collection of transformers supported by this provider"
            );
            field.makePrivate();
            field.makeStatic();
            final Package root = context.getPackage();
            final StringBuilder builder = new StringBuilder(128);
            builder.append("new MapUtils<String, Transformer>()");
            for (final String language : this.program.getAllLanguages()) {
                if (!this.program.getTransformationDescriptorsByLanguage(language).isEmpty()) {
                    final Package pkg = root.getSubpackage(language, "rules");
                    builder
                        .append(".put(\"")
                        .append(language)
                        .append("\", ")
                        .append(pkg.toString())
                        .append('.')
                        .append(language.substring(0, 1).toUpperCase(Locale.ENGLISH))
                        .append(language.substring(1))
                        .append("Transformer.INSTANCE)");
                }
            }
            builder.append(".make()");
            field.makeFinal(builder.toString());
            klass.addField(field);
            method.setBody(
                String.join(
                    "\n",
                    Arrays.asList(
                        "final String lowercase = language.toLowerCase(Locale.ENGLISH);",
                        "return Provider.TRANSFORMERS.getOrDefault(lowercase, Provider.DEF_TRANS);"
                    )
                )
            );
        }
        klass.addMethod(method);
    }
}
