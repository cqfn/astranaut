package org.cqfn.astgen;

/**
 * Main class.
 */
public final class Main {
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
        System.out.println("Welcome to AST Generator project!");
    }
}
