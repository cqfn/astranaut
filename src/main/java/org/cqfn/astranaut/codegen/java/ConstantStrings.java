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
import java.util.Map;
import java.util.TreeMap;

/**
 * Generates fields containing constant (final) strings.
 * @since 1.0.0
 */
public class ConstantStrings {
    /**
     * The maximum length of the field name.
     */
    private static final int MAX_NAME_LENGTH = 16;

    /**
     * Class in which to generate.
     */
    private final Klass klass;

    /**
     * Prefix to be added before the field name.
     */
    private final String prefix;

    /**
     * Brief description of the fields to be created, must contain '#' placeholder.
     */
    private final String brief;

    /**
     * Collection of created fields, where key is a string, value if full field name.
     */
    private final Map<String, String> fields;

    /**
     * Constructor.
     * @param klass Class in which to generate
     * @param prefix Prefix to be added before the field name
     * @param brief Brief description of the fields to be created
     */
    public ConstantStrings(final Klass klass, final String prefix, final String brief) {
        this.klass = klass;
        this.prefix = prefix.toUpperCase(Locale.ENGLISH);
        this.fields = new TreeMap<>();
        this.brief = ConstantStrings.checkBrief(brief);
    }

    /**
     * Creates a static field with 'String' type.
     * @param string Value of the field
     * @return Full name of the field
     */
    public String createStaticField(final String string) {
        final String variable;
        if (this.fields.containsKey(string)) {
            variable = this.fields.get(string);
        } else {
            final String name = this.formatName(string);
            final Field field = new Field(
                Strings.TYPE_STRING,
                name,
                this.brief.replace("#", string)
            );
            field.makePrivate();
            field.makeStatic();
            field.makeFinal(String.format("\"%s\"", string));
            this.klass.addField(field);
            variable = String.format("%s.%s", this.klass.getName(), name);
            this.fields.put(string, variable);
        }
        return variable;
    }

    /**
     * Formats the field name - adds underscores and trims to maximum length.
     * @param string Source string
     * @return Field name
     */
    private String formatName(final String string) {
        final String[] words = string.split("(?=[A-Z])");
        final String name = String.join("_", words).toUpperCase(Locale.ENGLISH);
        String result = this.addPrefix(name);
        do {
            if (result.length() <= ConstantStrings.MAX_NAME_LENGTH) {
                break;
            }
            result = this.addPrefix(ConstantStrings.removeVowelsExceptFirst(name));
            if (result.length() <= ConstantStrings.MAX_NAME_LENGTH) {
                break;
            }
            result = this.truncateAtConsonant(this.addPrefix(name));
        } while (false);
        return result;
    }

    /**
     * Adds a prefix to a name.
     * @param name Field name
     * @return Field name with prefix
     */
    private String addPrefix(final String name) {
        final String result;
        if (this.prefix.isEmpty()) {
            result = name;
        } else {
            result = String.format("%s_%s", this.prefix, name);
        }
        return result;
    }

    /**
     * Removes all vowels from the field name.
     * @param name Field name
     * @return Field name without vowels
     */
    private static String removeVowelsExceptFirst(final String name) {
        final StringBuilder builder = new StringBuilder();
        final int length = name.length();
        builder.append(name.charAt(0));
        for (int index = 1; index < length; index = index + 1) {
            final char chr = name.charAt(index);
            if (!ConstantStrings.isVowel(chr)) {
                builder.append(chr);
            }
        }
        return builder.toString();
    }

    /**
     * Cuts the field name to a consonant.
     * @param name Field name
     * @return Field name truncated by a consonant so that it satisfies
     *  the maximum length requirement
     */
    private String truncateAtConsonant(final String name) {
        String truncated = name;
        int index = ConstantStrings.MAX_NAME_LENGTH;
        while (index > this.prefix.length() + 1) {
            if (!ConstantStrings.isVowel(name.charAt(index - 1))) {
                truncated = name.substring(0, index);
                break;
            }
            index = index - 1;
        }
        if (index == this.prefix.length() + 1) {
            truncated = name.substring(0, ConstantStrings.MAX_NAME_LENGTH);
        }
        return truncated;
    }

    /**
     * Checks that the symbol is a vowel.
     * @param chr Symbol
     * @return Checking result
     */
    private static boolean isVowel(final char chr) {
        return "AEIOU".indexOf(Character.toUpperCase(chr)) != -1;
    }

    /**
     * Checks that the brief description of the fields to be created has '#' placeholder.
     * @param brief Description
     * @return The same description
     */
    private static String checkBrief(final String brief) {
        if (brief.indexOf('#') < 0) {
            throw new IllegalArgumentException();
        }
        return brief;
    }
}
