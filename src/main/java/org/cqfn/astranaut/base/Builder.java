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
 * Object that creates nodes.
 *
 * @since 1.0
 */
public interface Builder {
    /**
     * Associate a new fragment with the node.
     * @param fragment A new fragment
     */
    void setFragment(Fragment fragment);

    /**
     * Associate a new data with the node (in a textual format).
     * @param str Data as a string
     * @return Result of operation, {@code true} if the new data is suitable for this type of node
     */
    boolean setData(String str);

    /**
     * Sets a new list of children.
     * @param list A list of children
     * @return Result of operation, {@code true} if the new children list is suitable
     *  for this type of node
     */
    boolean setChildrenList(List<Node> list);

    /**
     * Checks if the Creator is in a valid state,
     * that is, whether it can create a new node based on its internal data.
     * @return Checking result
     */
    boolean isValid();

    /**
     * Creates a specific node.
     * @return A node
     */
    Node createNode();
}
