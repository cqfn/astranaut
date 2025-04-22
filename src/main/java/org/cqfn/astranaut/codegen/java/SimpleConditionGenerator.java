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
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.cqfn.astranaut.dsl.LeftSideItem;
import org.cqfn.astranaut.dsl.TransformationDescriptor;
import org.cqfn.astranaut.dsl.UntypedHole;

/**
 * Generates code for a simple condition, that is, when there are one or more patterns,
 *  and each pattern is matched by one node.
 * @since 1.0.0
 */
final class SimpleConditionGenerator implements ConditionGenerator  {
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
    private final Set<String> matchers;

    /**
     * Constructor.
     * @param rule Transformation rule
     * @param context Generation context
     */
    SimpleConditionGenerator(final TransformationDescriptor rule, final Context context) {
        this.rule = rule;
        this.context = context;
        this.matchers = new TreeSet<>();
    }

    @Override
    public List<String> generate() {
        final List<String> code = new ArrayList<>(8);
        final StringBuilder condition = new StringBuilder(128);
        condition.append("final boolean matched = ");
        final List<LeftSideItem> left = this.rule.getLeft();
        for (int index = 0; index < left.size(); index = index + 1) {
            if (index > 0) {
                condition.append(" && ");
            }
            final LeftSideItem item = left.get(index);
            final Klass matcher = this.context.getMatchers().get(item.toString(false));
            final String name = matcher.getName();
            this.matchers.add(name);
            condition
                .append(name)
                .append(".INSTANCE.match(list.get(");
            if (index > 0) {
                condition.append(index).append(" + index), extracted)");
            } else {
                condition.append("index), extracted)");
            }
        }
        condition.append(';');
        code.add(condition.toString());
        code.addAll(
            Arrays.asList(
                condition.toString(),
                "if (!matched) {",
                "    break;",
                "}"
            )
        );
        final boolean fragment = !(this.rule.getRight() instanceof UntypedHole);
        if (fragment && left.size() < 2) {
            code.add("final Fragment fragment = list.get(index).getFragment();");
        } else if (fragment) {
            code.add(
                String.format(
                    "final Fragment fragment = Fragment.fromNodes(list.subList(index, index + %d));",
                    left.size()
                )
            );
        }
        return code;
    }

    @Override
    public Set<String> getMatchers() {
        return this.matchers;
    }
}
