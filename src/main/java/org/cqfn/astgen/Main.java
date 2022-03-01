package org.cqfn.astgen;

import java.util.logging.Logger;

/**
 * Main class.
 */
public final class Main {
    /**
     * Logger.
     */
    private static Logger log = Logger.getLogger(Main.class.getName());

    /**
     * Private constructor.
     */
    private Main() {
    }

    /**
     * The main function. Parses the command line and runs actions.
     *
     * @param args the command-line arguments.
     */
    public static void main(final String... args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("No action specified.");
        }
        log.fine("Welcome to AST Generator project!");
    }
}
