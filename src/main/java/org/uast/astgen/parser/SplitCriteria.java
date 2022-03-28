/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.parser;

import org.uast.astgen.scanner.Token;

/**
 * Criteria for the splitter.
 *
 * @since 1.0
 */
public interface SplitCriteria {
    /**
     * Checks if a token satisfies some criteria.
     * @param token Token
     * @return Checking result
     */
    boolean satisfies(Token token);
}
