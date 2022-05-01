/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.utils.cli;

import java.util.Arrays;
import java.util.List;

/**
 * Custom implementation of CLI file parameter converter for the '--rules' option.
 *
 * @since 1.0
 */
public final class RulesFileConverter extends BaseFileConverter {
    /**
     * The list of valid file extensions.
     */
    private static final List<String> VALID_EXT = Arrays.asList("txt", "dsl");

    /**
     * Constructor.
     * @param option An option name
     */
    public RulesFileConverter(final String option) {
        super(option);
    }

    @Override
    public List<String> getValidExtensions() {
        return RulesFileConverter.VALID_EXT;
    }

    @Override
    public boolean fileMustExist() {
        return true;
    }
}
