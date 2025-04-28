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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.cqfn.astranaut.dsl.Program;

/**
 * Generates transformers for languages.
 * @since 1.0.0
 */
public final class TransformerGenerator {
    /**
     * Program implemented in DSL.
     */
    private final Program program;

    /**
     * Constructor.
     * @param program Program implemented in DSL
     */
    public TransformerGenerator(final Program program) {
        this.program = program;
    }

    /**
     * Creates compilation unit that contains a factory.
     * @param language Language for which the factory is generated
     * @param context Data required to generate Java source code
     * @return Compilation unit
     */
    public CompilationUnit createUnit(final String language, final Context context) {
        this.getClass();
        final String capitalized = language
            .substring(0, 1)
            .toUpperCase(Locale.ENGLISH)
            .concat(language.substring(1));
        final String target;
        if (language.equals("common")) {
            target = "common cases";
        } else {
            target = String.format("the '%s' language", capitalized);
        }
        final String classname = capitalized.concat("Transformer");
        final String brief = String.format(
            "Transformer that performs transformations for %s",
            target
        );
        final Klass klass = new Klass(classname, brief);
        klass.makePublic();
        klass.makeFinal();
        klass.setVersion(context.getVersion());
        klass.setSuperclass("DefaultTransformer");
        final Constructor ctor = klass.createConstructor();
        ctor.makePrivate();
        ctor.setBody(
            String.format(
                "super(%s.collectConverters(), %sFactory.INSTANCE);",
                classname,
                capitalized
            )
        );
        final Field instance = new Field(
            "Transformer",
            "INSTANCE",
            "The transformer instance"
        );
        instance.makePublic();
        instance.makeStatic();
        instance.makeFinal(String.format("new %s()", classname));
        klass.addField(instance);
        final Package pkg = context.getPackage();
        this.createMethodThatCollectConverters(target, pkg, klass);
        final CompilationUnit unit = new CompilationUnit(
            context.getLicense(),
            pkg,
            klass
        );
        unit.addImport("java.util.List");
        unit.addImport("java.util.LinkedList");
        unit.addImport("org.cqfn.astranaut.core.base.Transformer");
        unit.addImport("org.cqfn.astranaut.core.algorithms.conversion.Converter");
        unit.addImport("org.cqfn.astranaut.core.algorithms.conversion.DefaultTransformer");
        unit.addImport(
            String.format(
                "%s.%sFactory",
                pkg.getParent().getSubpackage("nodes").toString(),
                capitalized
            )
        );
        return unit;
    }

    /**
     * Creates a method that collect converters from specified packages.
     * @param target Target language
     * @param pkg Package
     * @param klass The class in which the method is created
     */
    private void createMethodThatCollectConverters(final String target,
        final Package pkg, final Klass klass) {
        final Method method = new Method(
            "List<Converter>",
            "collectConverters",
            String.format("Collects converter objects for %s", target)
        );
        method.makePrivate();
        method.makeStatic();
        method.setReturnsDescription("List of converter objects");
        final List<String> code = new ArrayList<>(4);
        code.add("final List<Converter> list = new LinkedList<>();");
        if (!target.equals("common cases")
            && !this.program.getTransformationDescriptorsByLanguage("common").isEmpty()) {
            code.add(
                String.format(
                    "Converter.collectConverters(\"%s\", list);",
                    pkg.getParent().getParent().getSubpackage("common", "rules").toString()
                )
            );
        }
        code.add(
            String.format(
                "Converter.collectConverters(\"%s\", list);",
                pkg.toString()
            )
        );
        code.add("return list;");
        method.setBody(String.join("\n", code));
        klass.addMethod(method);
    }
}
