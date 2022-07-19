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
package org.cqfn.astranaut.base;

import java.util.List;

/**
 * A type of abstract syntax tree node.
 *
 * @since 0.1.5
 */
public interface Type {
    /**
     * Returns the type name.
     * @return The type name
     */
    String getName();

    /**
     * Returns the list of child types.
     * @return The list of descriptors
     */
    List<ChildDescriptor> getChildTypes();

    /**
     * The hierarchy of names of groups the node type belongs to.
     * @return The list of type names
     */
    List<String> getHierarchy();

    /**
     * Returns the value of some property (depends on implementation).
     * @param name The name of property
     * @return Property value (if the property is not defined, returns an empty string)
     */
    String getProperty(String name);

    /**
     * Creates a new builder who builds a node of this type.
     * @return A builder.
     */
    Builder createBuilder();

    /**
     * Checks whether the type belongs to group.
     * @param type The type name
     * @return Checking result, {@code true} if the type belongs to the group
     */
    default boolean belongsToGroup(final String type) {
        return this.getHierarchy().contains(type);
    }
}
