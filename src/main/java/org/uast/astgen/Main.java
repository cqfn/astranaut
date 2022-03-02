/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen;

import java.util.logging.Logger;

/**
 * Main class.
 *
 * @since 1.0
 */
public final class Main {
    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    /**
     * Private constructor.
     */
    private Main() {
    }

    /**
     * The main function. Parses the command line and runs actions.
     *
     * @param args The command-line arguments
     */
    public static void main(final String... args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("No action specified.");
        }
        LOG.fine("Welcome to AST Generator project!");
    }
}
