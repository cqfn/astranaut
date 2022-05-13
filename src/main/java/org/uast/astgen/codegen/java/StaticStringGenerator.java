/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Ivan Kniazkov
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
package org.uast.astgen.codegen.java;

import java.util.Map;
import java.util.TreeMap;

/**
 * Generator of static strings.
 * Instead of using such a string directly in the source code, creates a static variable,
 * and returns the name of the variable.
 * This eliminates the Qulice error when the same static string is repeated two or more times.
 *
 * @since 1.0
 */
final class StaticStringGenerator {
    /**
     * Maximum length of field name.
     */
    private static final int NAME_LENGTH = 16;

    /**
     * The map contains existing strings.
     */
    private final Map<String, Field> map;

    /**
     * The class where to create static fields.
     */
    private final Klass klass;

    /**
     * Constructor.
     * @param klass The class where to create static fields
     */
    StaticStringGenerator(final Klass klass) {
        this.map = new TreeMap<>();
        this.klass = klass;
    }

    /**
     * Returns static field name by string value.
     * @param value The value
     * @return The field name
     */
    public String getFieldName(final String value) {
        String result;
        if (this.map.containsKey(value)) {
            result = this.map.get(value).getName();
        } else {
            boolean flag = false;
            final StringBuilder name = new StringBuilder();
            final int length = value.length();
            for (int index = 0; index < length; index = index + 1) {
                final char symbol = value.charAt(index);
                if (Character.isUpperCase(symbol) && flag) {
                    name.append('_');
                }
                flag = true;
                name.append(Character.toUpperCase(symbol));
            }
            result = name.toString();
            if (result.length() > StaticStringGenerator.NAME_LENGTH) {
                result = result.substring(0, StaticStringGenerator.NAME_LENGTH);
            }
            final Field field = new Field(
                String.format("The '%s' string", value),
                "String",
                result
            );
            field.makePrivate();
            field.makeStaticFinal();
            field.setInitExpr(String.format("\"%s\"", value));
            this.map.put(value, field);
            this.klass.addField(field);
        }
        return String.format("%s.%s", this.klass.getName(), result);
    }
}
