/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.parser;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.uast.astgen.exceptions.BadRuleSyntax;
import org.uast.astgen.exceptions.ExpectedUniqueNumbers;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.exceptions.UnexpectedNumberUsed;
import org.uast.astgen.rules.Descriptor;
import org.uast.astgen.rules.DescriptorAttribute;
import org.uast.astgen.rules.Hole;
import org.uast.astgen.rules.Parameter;
import org.uast.astgen.rules.Transformation;
import org.uast.astgen.scanner.TokenList;
import org.uast.astgen.utils.LabelFactory;

/**
 * Parser of {@link Transformation} rules.
 *
 * @since 1.0
 */
public class TransformationParser {
    /**
     * Source string.
     */
    private final String source;

    /**
     * Label factory.
     */
    private final LabelFactory labels;

    /**
     * Constructor.
     * @param source Source string
     */
    public TransformationParser(final String source) {
        this.source = source;
        this.labels = new LabelFactory();
    }

    /**
     * Parses source string as a {@link Transformation} descriptor.
     * @return A node descriptor
     * @throws ParserException If the source string can't be parsed as a transformation descriptor
     */
    public Transformation parse() throws ParserException {
        assert this.source.contains("->");
        final String[] pair = this.source.split("->");
        if (pair.length > 2) {
            throw BadRuleSyntax.INSTANCE;
        }
        final Descriptor left =  this.parsePart(pair[0]);
        final Descriptor right =  this.parsePart(pair[1]);
        TransformationRuleValidator.checkDescriptors(left, right);
        return new Transformation(
            left,
            right
        );
    }

    /**
     * Parses a part (left or right) of the rule.
     * @param code DSL code
     * @return A descriptor
     * @throws ParserException If the source string can't be parsed as a descriptor
     */
    private Descriptor parsePart(final String code) throws ParserException {
        TokenList tokens = new Tokenizer(code).getTokens();
        tokens = new BracketsParser(tokens).parse();
        return new DescriptorParser(tokens, this.labels).parse(DescriptorAttribute.NONE);
    }

    /**
     * Validator of {@link Transformation} rules.
     *
     * @since 1.0
     */
    private static class TransformationRuleValidator {
        /**
         * Separately checks the validity of hole numbers for data and for parameters.
         * Checks that the numbers are unique between the two groups.
         * @param left A left descriptor
         * @param right A right descriptor
         * @throws ParserException If checking failed
         */
        private static void checkDescriptors(
            final Descriptor left,
            final Descriptor right) throws ParserException {
            final List<Hole> lparams = new LinkedList<>();
            final List<Hole> ldata = new LinkedList<>();
            getAllHolesInRulePart(left, lparams, ldata);
            final List<Hole> rparams = new LinkedList<>();
            final List<Hole> rdata = new LinkedList<>();
            getAllHolesInRulePart(right, rparams, rdata);
            final List<Hole> lcommon = new LinkedList<>(lparams);
            lcommon.retainAll(ldata);
            for (final Hole hole : lcommon) {
                throw new ExpectedUniqueNumbers(hole.getValue());
            }
            final List<Hole> rcommon = new LinkedList<>(rdata);
            rcommon.retainAll(rparams);
            for (final Hole hole : rcommon) {
                throw new ExpectedUniqueNumbers(hole.getValue());
            }
            checkHolesInDescriptors(lparams, rparams);
            checkHolesInDescriptors(ldata, rdata);
        }

        /**
         * Checks the validity of hole numbers in a left and a right parts of the rule.
         * @param left A list of holes in a left part
         * @param right A list of holes in a right part
         * @throws ParserException If checking failed
         */
        private static void checkHolesInDescriptors(
            final List<Hole> left,
            final List<Hole> right) throws ParserException {
            final Map<Integer, Boolean> numbers = new TreeMap<>();
            for (final Hole hole : left) {
                final int number = hole.getValue();
                if (numbers.containsKey(number)) {
                    throw new ExpectedUniqueNumbers(number);
                } else {
                    numbers.put(number, false);
                }
            }
            for (final Hole hole : right) {
                final int number = hole.getValue();
                if (numbers.containsKey(number)) {
                    if (numbers.get(number)) {
                        throw new ExpectedUniqueNumbers(number);
                    }
                    numbers.put(number, true);
                } else {
                    throw new UnexpectedNumberUsed(number);
                }
            }
        }

        /**
         * Collects lists of parameter and data holes from the specified rule part.
         * @param descriptor A rule part
         * @param params A list of parameter holes
         * @param data A list of data holes
         */
        private static void getAllHolesInRulePart(
            final Descriptor descriptor,
            final List<Hole> params,
            final List<Hole> data) {
            if (descriptor.getData() instanceof Hole) {
                data.add((Hole) descriptor.getData());
            }
            for (final Parameter parameter : descriptor.getParameters()) {
                if (parameter instanceof Hole) {
                    params.add((Hole) parameter);
                }
                if (parameter instanceof Descriptor) {
                    getAllHolesInRulePart((Descriptor) parameter, data, params);
                }
            }
        }
    }
}
