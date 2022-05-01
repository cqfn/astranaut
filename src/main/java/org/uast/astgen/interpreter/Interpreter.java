/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.interpreter;

import org.uast.astgen.base.Node;
import org.uast.astgen.exceptions.InterpreterCouldNotWriteFile;
import org.uast.astgen.exceptions.InterpreterException;
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
     * @throws InterpreterException Can't execute the program for some reasons
     */
    public void run() throws InterpreterException {
        final Node root = new JsonDeserializer(
            new FilesReader(this.source).readAsString(
                (FilesReader.CustomExceptionCreator<InterpreterException>) ()
                    -> new InterpreterException() {
                        @Override
                        public String getErrorMessage() {
                            return String.format(
                                "Could not read the file that contains source syntax tree: %s",
                                Interpreter.this.source
                            );
                        }
                    }
            )
        ).convert();
        if (!new JsonSerializer(root).serializeToFile(this.destination)) {
            throw new InterpreterCouldNotWriteFile(this.destination);
        }
    }
}
