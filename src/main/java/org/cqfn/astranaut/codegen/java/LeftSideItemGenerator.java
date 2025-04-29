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

/**
 * This generator is responsible for creating a class that implements a pattern descriptor
 *  or a typed hole located on the left side of transformation rules.
 * @since 1.0.0
 */
public abstract class LeftSideItemGenerator {
    /**
     * Generates a class that implements a pattern descriptor or a typed hole located
     *  on the left side of transformation rules.
     * @param context Generation context
     * @return A {@link Klass} instance representing a left-side implementation
     */
    public abstract Klass generate(LeftSideGenerationContext context);

    /**
     * Generates an instance field and a private constructor, and that turns the generated class
     *  into a singleton.
     * @param klass Class
     */
    protected static void generateInstanceAndConstructor(final Klass klass) {
        final Field instance = new Field("Matcher", "INSTANCE", "The instance");
        instance.makePublic();
        instance.makeStatic();
        instance.makeFinal(String.format("new %s()", klass.getName()));
        klass.addField(instance);
        final Constructor ctor = klass.createConstructor();
        ctor.makePrivate();
    }
}
