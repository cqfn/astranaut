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
import java.util.List;
import java.util.Locale;

/**
 * Generates transformers for languages.
 * @since 1.0.0
 */
public final class TransformerGenerator {
    /**
     * The instance.
     */
    public static final TransformerGenerator INSTANCE = new TransformerGenerator();

    /**
     * Private constructor.
     */
    private TransformerGenerator() {
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
        klass.addMethod(
            TransformerGenerator.createMethodThatCollectConverters(target, pkg)
        );
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
        unit.addImport("org.cqfn.astranaut.core.utils.ObjectsLoader");
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
     * @return A method
     */
    private static Method createMethodThatCollectConverters(final String target,
        final Package pkg) {
        final Method method = new Method(
            "List<Converter>",
            "collectConverters",
            String.format("Collects converter objects for %s", target)
        );
        method.makePrivate();
        method.makeStatic();
        method.setReturnsDescription("List of converter objects");
        final List<String> code = Arrays.asList(
            "final List<Converter> list = new LinkedList<>();",
            String.format(
                "final String prefix = \"%s.Converter\";",
                pkg.toString()
            ),
            "final ObjectsLoader loader = new ObjectsLoader(prefix);",
            "int index = 0;",
            "while (true) {",
            "    final Object object = loader.loadSingleton(index);",
            "    if (!(object instanceof Converter)) {",
            "        break;",
            "    }",
            "    list.add((Converter) object);",
            "    index = index + 1;",
            "}",
            "return list;"
        );
        method.setBody(String.join("\n", code));
        return method;
    }
}
