/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Ivan Kniazkov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cqfn.astranaut.analyzer;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.cqfn.astranaut.dsl.AbstractNodeDescriptor;
import org.cqfn.astranaut.dsl.ChildDescriptorExt;
import org.cqfn.astranaut.dsl.NodeDescriptor;
import org.cqfn.astranaut.dsl.Program;
import org.cqfn.astranaut.dsl.RegularNodeDescriptor;
import org.cqfn.astranaut.dsl.ResultingSubtreeDescriptor;
import org.cqfn.astranaut.dsl.RightSideItem;
import org.cqfn.astranaut.dsl.Rule;
import org.cqfn.astranaut.dsl.TransformationDescriptor;
import org.cqfn.astranaut.exceptions.BaseException;
import org.cqfn.astranaut.parser.Location;

/**
 * Analyzer that analyzes an Astranaut program before code generation or execution
 *  and builds the necessary relationships between rules.
 * @since 1.0.0
 */
public class Analyzer {
    /**
     * DSL program.
     */
    private final Program program;

    /**
     * Rules in relation to the location of the DSL code from which they are parsed.
     */
    private final Map<Rule, Location> locations;

    /**
     * Constructor.
     * @param program DSL program
     * @param locations Rules in relation to the location of the DSL code
     */
    public Analyzer(final Program program, final Map<Rule, Location> locations) {
        this.program = program;
        this.locations = locations;
    }

    /**
     * Analyzes the DSL program and builds relationships between rules.
     * @throws BaseException If the DSL program contains errors
     */
    public void analyze() throws BaseException {
        final Set<String> languages = this.program.getAllLanguages();
        for (final String language : languages) {
            final Map<String, NodeDescriptor> nodes =
                this.program.getNodeDescriptorsByLanguage(language);
            this.linkNodes(nodes);
            final List<TransformationDescriptor> conversions =
                this.program.getTransformationDescriptorsByLanguage(language);
            this.checkTransformationRules(conversions);
        }
        for (final Rule rule : this.program.getAllRules()) {
            if (rule instanceof RegularNodeDescriptor) {
                Analyzer.addTagsToBaseNodes((NodeDescriptor) rule);
            }
        }
    }

    /**
     * Builds links between nodes: abstract and non-abstract nodes of the same language,
     *  child nodes.
     * @param descriptors Node descriptors mapped by their names
     * @throws BaseException If the DSL program contains errors
     */
    private void linkNodes(final Map<String, NodeDescriptor> descriptors)
        throws BaseException {
        for (final Map.Entry<String, NodeDescriptor> entry : descriptors.entrySet()) {
            final NodeDescriptor descriptor = entry.getValue();
            if (descriptor instanceof AbstractNodeDescriptor) {
                this.linkAbstractNode((AbstractNodeDescriptor) descriptor);
            } else if (descriptor instanceof RegularNodeDescriptor) {
                this.linkRegularNode((RegularNodeDescriptor) descriptor);
            }
        }
    }

    /**
     * Links abstract nodes to their subtypes and base descriptors.
     * @param descriptor The abstract node descriptor to process
     * @throws BaseException If the base node or subtype is not defined
     */
    private void linkAbstractNode(final AbstractNodeDescriptor descriptor)
        throws BaseException {
        for (final String subtype : descriptor.getSubtypes()) {
            final NodeDescriptor subdescr =
                this.program.getNodeDescriptorByNameAndLanguage(subtype, descriptor.getLanguage());
            if (subdescr == null) {
                throw new CommonAnalyzerException(
                    this.locations.get(descriptor),
                    String.format(
                        "The abstract node '%s' is the base for the node '%s' which is not defined",
                        descriptor.getName(),
                        subtype
                    )
                );
            }
            final boolean alien = !subdescr.getLanguage().equals(descriptor.getLanguage());
            final boolean revert = alien && subdescr instanceof AbstractNodeDescriptor;
            if (revert) {
                descriptor.addBaseDescriptor((AbstractNodeDescriptor) subdescr);
            } else {
                subdescr.addBaseDescriptor(descriptor);
            }
        }
    }

    /**
     * Links regular nodes to their child descriptors.
     * @param descriptor The regular node descriptor to process
     * @throws BaseException If the child node is not defined
     */
    private void linkRegularNode(final RegularNodeDescriptor descriptor)
        throws BaseException {
        for (final ChildDescriptorExt child : descriptor.getExtChildTypes()) {
            final NodeDescriptor rule =
                this.program.getNodeDescriptorByNameAndLanguage(
                    child.getType(),
                    descriptor.getLanguage()
                );
            if (rule == null) {
                throw new CommonAnalyzerException(
                    this.locations.get(descriptor),
                    String.format(
                        "The '%s' node contains a child node '%s' which is not defined",
                        descriptor.getName(),
                        child.getType()
                    )
                );
            }
            child.setRule(rule);
            descriptor.addDependency(rule);
        }
    }

    /**
     * Recursively adds tags to the base nodes of the given descriptor.
     *  For each base node descriptor, it merges the tags from the current descriptor
     *  and then recursively calls this method for the base node.
     * @param descriptor The node descriptor whose tags will be added to its base nodes.
     */
    private static void addTagsToBaseNodes(final NodeDescriptor descriptor) {
        final List<AbstractNodeDescriptor> bases = descriptor.getBaseDescriptors();
        final Map<String, ChildDescriptorExt> tags = descriptor.getTags();
        for (final AbstractNodeDescriptor base : bases) {
            base.mergeTags(tags);
            Analyzer.addTagsToBaseNodes(base);
        }
    }

    /**
     * Validates a list of transformation descriptors to ensure their right-hand sides are
     *  well-formed. For each descriptor, if the right-hand side is a resulting subtree,
     *  it recursively checks that the type of each node exists in the program and is properly
     *  defined for the descriptor's language.
     * @param descriptors The list of transformation descriptors to validate.
     * @throws BaseException If any resulting node refers to an undefined type.
     */
    private void checkTransformationRules(final List<TransformationDescriptor> descriptors)
        throws BaseException {
        for (final TransformationDescriptor descriptor : descriptors) {
            final RightSideItem right = descriptor.getRight();
            if (right instanceof ResultingSubtreeDescriptor) {
                this.checkResultingDescriptor(descriptor, (ResultingSubtreeDescriptor) right);
            }
        }
    }

    /**
     * Recursively validates a resulting subtree descriptor to ensure all node types are defined.
     *  This method checks whether the specified resulting node type is defined in the program
     *  for the given language. It then applies the same validation recursively to all children.
     *
     * @param descriptor The transformation descriptor containing the context (e.g., language).
     * @param subtree The resulting subtree descriptor to validate.
     * @throws BaseException If the type of any resulting node in the subtree is undefined.
     */
    private void checkResultingDescriptor(final TransformationDescriptor descriptor,
        final ResultingSubtreeDescriptor subtree) throws BaseException  {
        final String name = subtree.getType();
        final NodeDescriptor rule =
            this.program.getNodeDescriptorByNameAndLanguage(name, descriptor.getLanguage());
        if (rule == null) {
            throw new CommonAnalyzerException(
                this.locations.get(descriptor),
                String.format(
                    "The resulting node is of type '%s' which is not defined",
                    name
                )
            );
        }
        for (final RightSideItem child : subtree.getChildren()) {
            if (child instanceof ResultingSubtreeDescriptor) {
                this.checkResultingDescriptor(descriptor, (ResultingSubtreeDescriptor) child);
            }
        }
    }
}
