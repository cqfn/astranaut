/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.exceptions;

/**
 * Base exception thrown by the interpreter.
 *
 * @since 1.0
 */
public abstract class InterpreterException extends BaseException {
    @Override
    public final String getInitiator() {
        return "Interpreter";
    }
}
