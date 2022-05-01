/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.utils.cli;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import org.uast.astgen.Action;

/**
 * Custom implementation of CLI file parameter converter.
 *
 * @since 1.0
 */
public class ActionConverter implements IStringConverter<Action> {
    /**
     * The option name.
     */
    private final String option;

    /**
     * Constructor.
     * @param option An option name
     */
    public ActionConverter(final String option) {
        this.option = option;
    }

    /**
     * Converts a command-line parameter to an action.
     *
     * @param value An action as a string
     * @return An action as an object
     */
    public Action convert(final String value) {
        final Action result;
        switch (value) {
            case "generate":
                result = Action.GENERATE;
                break;
            case "convert":
                result = Action.CONVERT;
                break;
            default:
                throw new ParameterException(
                    String.format(
                        "The parameter for the option [%s] is not valid action",
                        this.option
                    )
                );
        }
        return result;
    }
}
