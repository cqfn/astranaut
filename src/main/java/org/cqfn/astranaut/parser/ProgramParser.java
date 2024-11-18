/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 Ivan Kniazkov
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
package org.cqfn.astranaut.parser;

import java.util.ArrayList;
import java.util.List;
import org.cqfn.astranaut.dsl.NodeDescriptor;
import org.cqfn.astranaut.dsl.Program;
import org.cqfn.astranaut.dsl.Rule;
import org.cqfn.astranaut.exceptions.BaseException;

/**
 * Parses a program written in the DSL language. The whole program.
 * @since 1.0.0
 */
@SuppressWarnings("PMD.CloseResource")
public class ProgramParser {
    /**
     * Current language.
     */
    private String language;

    /**
     * Constructor.
     */
    public ProgramParser() {
        this.language = "";
    }

    /**
     * Parses a program written in the DSL language.
     * @param reader Reader that reads the DSL source code and separates it into statements
     * @return Entire program
     * @throws BaseException If there is an error in the program
     */
    public Program parse(final DslReader reader) throws BaseException {
        final List<Rule> list = new ArrayList<>(0);
        Statement stmt = reader.getStatement();
        while (stmt != null) {
            final String newlang = stmt.getLanguage();
            if (!newlang.isEmpty()) {
                this.language = newlang;
            }
            final String code = stmt.getCode();
            if (code.contains("<-")) {
                final NodeDescriptorParser parser = new NodeDescriptorParser(this.language, stmt);
                final NodeDescriptor descr = parser.parseDescriptor();
                list.add(descr);
            } else {
                throw new CommonParsingException(
                    stmt.getLocation(),
                    "The rule does not contain a separator"
                );
            }
            stmt = reader.getStatement();
        }
        return new Program(list);
    }
}
