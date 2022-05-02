/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.interpreter;

import java.io.File;
import org.uast.astgen.base.Node;
import org.uast.astgen.exceptions.DestinationNotSpecified;
import org.uast.astgen.exceptions.InterpreterCouldNotWriteFile;
import org.uast.astgen.exceptions.InterpreterException;
import org.uast.astgen.exceptions.SourceNotSpecified;
import org.uast.astgen.rules.Program;
import org.uast.astgen.utils.FilesReader;

/**
 * The interpreter that loads the syntax tree in Json format,
 * then applies DSL rules and saves the result to a file.
 *
 * @since 1.0
 */
public class Interpreter {
    /**
     * The name of the source file.
     */
    private final File source;

    /**
     * The name of the destination file.
     */
    private final File destination;

    /**
     * The DSL program.
     */
    private final Program program;

    /**
     * Constructor.
     * @param source The source file
     * @param destination The destination file
     * @param program The program
     */
    public Interpreter(final File source, final File destination, final Program program) {
        this.source = source;
        this.destination = destination;
        this.program = program;
    }

    /**
     * Runs the interpreter.
     * @throws InterpreterException Can't execute the program for some reasons
     */
    public void run() throws InterpreterException {
        if (this.source == null) {
            throw SourceNotSpecified.INSTANCE;
        }
        if (this.destination == null) {
            throw DestinationNotSpecified.INSTANCE;
        }
        final Node unprocessed = new JsonDeserializer(
            new FilesReader(this.source.getPath()).readAsString(
                (FilesReader.CustomExceptionCreator<InterpreterException>) ()
                    -> new InterpreterException() {
                        @Override
                        public String getErrorMessage() {
                            return String.format(
                                "Could not read the file that contains source syntax tree: %s",
                                Interpreter.this.source.getPath()
                            );
                        }
                    }
            )
        ).convert();
        final Adapter adapter = new Adapter(this.program.getTransformations());
        final Node processed = adapter.convert(unprocessed);
        if (!new JsonSerializer(processed).serializeToFile(this.destination.getPath())) {
            throw new InterpreterCouldNotWriteFile(this.destination.getPath());
        }
    }
}
