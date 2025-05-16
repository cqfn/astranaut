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
package org.cqfn.astranaut.codegen.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.cqfn.astranaut.dsl.LeftSideItem;
import org.cqfn.astranaut.dsl.TransformationDescriptor;
import org.cqfn.astranaut.dsl.UntypedHole;

/**
 * Generates code for a condition that consists of a single repeating node.
 * @since 1.0.0
 */
final class RepeatedNodeConditionGenerator implements ConditionGenerator  {
    /**
     * Transformation rule.
     */
    private final TransformationDescriptor rule;

    /**
     * Generation context.
     */
    private final Context context;

    /**
     * Set of matchers used in the condition.
     */
    private Set<String> matchers;

    /**
     * Constructor.
     * @param rule Transformation rule
     * @param context Generation context
     */
    RepeatedNodeConditionGenerator(final TransformationDescriptor rule, final Context context) {
        this.rule = rule;
        this.context = context;
        this.matchers = Collections.emptySet();
    }

    @Override
    public List<String> generate() {
        final List<String> code = new ArrayList<>(8);
        final LeftSideItem item = this.rule.getLeft().get(0);
        final String matcher = this.context.getMatchers().get(item.toString(false)).getName();
        this.matchers = Collections.singleton(matcher);
        code.addAll(
            Arrays.asList(
                "int consumed = 0;",
                "for (int offset = 0; index + offset < list.size(); offset = offset + 1) {",
                "    final Node node = list.get(index + offset);",
                String.format("    if (!%s.INSTANCE.match(node, extracted)) {", matcher),
                "        break;",
                "    }",
                "    consumed = consumed + 1;",
                "}",
                "if (consumed == 0) {",
                "    break;",
                "}"
            )
        );
        if (!(this.rule.getRight() instanceof UntypedHole)) {
            code.add(
                "final Fragment fragment = Fragment.fromNodes(list.subList(index, index + consumed));"
            );
        }
        return code;
    }

    @Override
    public Set<String> getMatchers() {
        return this.matchers;
    }
}
