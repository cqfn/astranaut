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
import org.cqfn.astranaut.dsl.SymbolDescriptor;
import org.cqfn.astranaut.parser.SymbolicToken;

/**
 * Generates a matcher class for matching symbols.
 * @since 1.0.0
 */
public final class SymbolMatcherGenerator extends LeftSideItemGenerator {
    /**
     * Item for which the matcher is generated.
     */
    private final SymbolDescriptor item;

    /**
     * Constructor.
     * @param item Item for which the matcher is generated
     */
    public SymbolMatcherGenerator(final SymbolDescriptor item) {
        this.item = item;
    }

    @Override
    public Klass generate(final LeftSideGenerationContext context) {
        final String brief = String.format(
            "Matches a node with the pattern '%s' and extracts it if matched",
            this.item.toString(false)
        );
        final Klass klass = new Klass(context.generateClassName(), brief);
        LeftSideItemGenerator.generateInstanceAndConstructor(klass);
        this.generateMatchMethod(klass);
        return klass;
    }

    /**
     * Generates and adds a {@code match} method to the given class.
     * @param klass The class to which the {@code match} method will be added
     */
    private void generateMatchMethod(final Klass klass) {
        final Method method = new Method("boolean", "match");
        klass.addMethod(method);
        method.makePublic();
        method.addArgument("Node", "node");
        method.addArgument("Extracted", "extracted");
        final List<String> code = new ArrayList<>(16);
        code.add("final String data = node.getData();");
        final SymbolicToken token = this.item.getToken();
        final String condition;
        if (token.getFirstSymbol() == token.getLastSymbol()) {
            condition = String.format(
                "node.belongsToGroup(\"Char\") && data.length() == 1 && data.charAt(0) == %s",
                token.getFirstSymbolAsQuotedString()
            );
        } else {
            condition = String.format(
                "node.belongsToGroup(\"Char\") && data.length() == 1 && data.charAt(0) >= %s && data.charAt(0) <= %s",
                token.getFirstSymbolAsQuotedString(),
                token.getLastSymbolAsQuotedString()
            );
        }
        if (this.item.getData() == null) {
            code.add(String.format("return %s;", condition));
        } else {
            code.addAll(
                Arrays.asList(
                    String.format("final boolean matches = %s;", condition),
                    "if (matches) {",
                    String.format("extracted.addData(%d, data);", this.item.getData().getNumber()),
                    "}",
                    "return matches;"
                )
            );
        }
        method.setBody(String.join("\n", code));
    }
}
