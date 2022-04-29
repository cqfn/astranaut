/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.interpreter;

import java.io.IOException;
import org.uast.astgen.base.Node;
import org.uast.astgen.rules.Program;
import org.uast.astgen.utils.FilesReader;

/**
 * The interpreter that loads the syntax tree in Json format,
 * then applies DSL rules and saves the result to a file.
 *
 * @since 1.0
 */
@SuppressWarnings("PMD.SingularField")
public class Interpreter {
    /**
     * The name of the source file.
     */
    private final String source;

    /**
     * The name of the destination file.
     */
    private final String destination;

    /**
     * The DSL program.
     */
    @SuppressWarnings("PMD.UnusedPrivateField")
    private final Program program;

    /**
     * Constructor.
     * @param source The name of the source file
     * @param destination The name of the destination file
     * @param program The program
     */
    public Interpreter(final String source, final String destination, final Program program) {
        this.source = source;
        this.destination = destination;
        this.program = program;
    }

    /**
     * Runs the interpreter.
     * @throws IOException File read/write error
     */
    public void run() throws IOException {
        final Node root = new JsonDeserializer(
            new FilesReader(this.source).readAsString()
        ).convert();
        new JsonSerializer(root).serializeToFile(this.destination);
    }
}
