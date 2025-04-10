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

import java.util.Collections;
import java.util.Map;

/**
 * Data required to generate Java source code, used in all (or almost all) generated files.
 * @since 1.0.0
 */
public final class Context {
    /**
     * License.
     */
    private License license;

    /**
     * Package.
     */
    private Package pkg;

    /**
     * Version number.
     */
    private String version;

    /**
     * Collection of matchers mapped to textual representations of rules.
     */
    private Map<String, Klass> matchers;

    /**
     * Labels for converters.
     */
    private NumberedLabelGenerator clabels;

    /**
     * Private constructor.
     */
    private Context() {
    }

    /**
     * Returns the license (printed at the beginning of each generated file).
     * @return License object
     */
    public License getLicense() {
        return this.license;
    }

    /**
     * Returns the package (printed in each generated file, after license).
     * @return Package object
     */
    public Package getPackage() {
        return this.pkg;
    }

    /**
     * Returns the version number (printed at the beginning of each generated class or interface).
     * @return Version number
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Returns the matchers collection.
     * If matchers were not explicitly set, returns an empty map.
     * @return An immutable map of matchers
     */
    public Map<String, Klass> getMatchers() {
        return this.matchers;
    }

    /**
     * Returns the next unique converter name ('Converter0', 'Converter1', and so on).
     * @return Unique converter name
     */
    public String getNextConverterName() {
        if (this.clabels == null) {
            this.clabels = new NumberedLabelGenerator("Converter");
        }
        return this.clabels.getLabel();
    }

    /**
     * Class that helps to build context.
     * @since 1.0.0
     */
    public static class Constructor {
        /**
         * License.
         */
        private License license;

        /**
         * Package.
         */
        private Package pkg;

        /**
         * Version number.
         */
        private String version;

        /**
         * Collection of matchers.
         */
        private Map<String, Klass> matchers = Collections.emptyMap();

        /**
         * Sets the license.
         * @param object License
         */
        public void setLicense(final License object) {
            this.license = object;
        }

        /**
         * Sets the package.
         * @param object Package
         */
        public void setPackage(final Package object) {
            this.pkg = object;
        }

        /**
         * Sets the version number.
         * @param number Version number
         */
        public void setVersion(final String number) {
            this.version = number;
        }

        /**
         * Sets the matchers collection.
         * @param collection The map of matchers to be stored in the context
         */
        public void setMatchers(final Map<String, Klass> collection) {
            this.matchers = collection;
        }

        /**
         * Constructs a context from the specified data.
         * @return Context object
         */
        public Context createContext() {
            if (this.license == null || this.pkg == null || this.version == null) {
                throw new IllegalStateException();
            }
            final Context ctx = new Context();
            ctx.license = this.license;
            ctx.pkg = this.pkg;
            ctx.version = this.version;
            ctx.matchers = this.matchers;
            return ctx;
        }
    }
}
