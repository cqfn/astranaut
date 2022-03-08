/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.exceptions;

/**
 * Exception that occurs when the node contains children
 * not specified in a set of rules.
 *
 * @since 1.0
 */
public class NotSpecifiedException extends Exception {
    private static final long serialVersionUID = -9178324253245411642L;

    /**
     * Name of the child class.
     */
    private final String child;

    /**
     * Instantiates a new Exception caused by usage of child node
     * that was not specified in rules.
     *
     * @param child Name of the child class
     */
    public NotSpecifiedException(final String child) {
        super();
        this.child = child;
    }

    /**
     * Get error message.
     *
     * @return String Value of error message.
     */
    public String getErrorMessage() {
        return new StringBuilder()
            .append("The node ")
            .append(this.child)
            .append(" was not specified in green node rules or rules for current language.")
            .toString();
    }
}
