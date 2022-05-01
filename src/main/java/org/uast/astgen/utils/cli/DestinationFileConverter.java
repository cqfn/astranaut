/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.utils.cli;

import java.util.Collections;
import java.util.List;

/**
 * Custom implementation of CLI file parameter converter for the '--destination' option.
 *
 * @since 1.0
 */
public final class DestinationFileConverter extends BaseFileConverter {
    /**
     * The list of valid file extensions.
     */
    private static final List<String> VALID_EXT = Collections.singletonList("json");

    /**
     * Constructor.
     * @param option An option name
     */
    public DestinationFileConverter(final String option) {
        super(option);
    }

    @Override
    public List<String> getValidExtensions() {
        return DestinationFileConverter.VALID_EXT;
    }

    @Override
    public boolean fileMustExist() {
        return false;
    }
}
