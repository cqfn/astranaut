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
package org.cqfn.astranaut.parser;

import java.util.Map;
import org.cqfn.astranaut.core.utils.MapUtils;

/**
 * A factory for creating parsers that parses items of the left side of transformation rules.
 * @since 1.0.0
 */
public final class LeftSideParsingFactory {
    /**
     * Mapping token classes and parsers that fit them.
     */
    private static final Map<Class<? extends Token>, LeftSideItemParser> PARSERS =
        new MapUtils<Class<? extends Token>, LeftSideItemParser>()
            .put(SymbolicToken.class, SymbolicDescriptorParser.INSTANCE)
            .make();

    /**
     * Private constructor.
     */
    private LeftSideParsingFactory() {
    }

    /**
     * Returns the suitable parser for the first token in the chain.
     * @param first First token in the token chain
     * @return Suitable parser or {@code null} if there is no parser for this token
     */
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static LeftSideItemParser getParser(final Token first) {
        return LeftSideParsingFactory.PARSERS.get(first.getClass());
    }
}
