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

IntegerLiteral <- 'int';
StringLiteral <- 'String', '""';

Addition <- left@Expression, right@Expression;
Subtraction <- left@Expression, right@Expression;
BinaryExpression <- Addition | Subtraction;

PrefixIncrement <- operand@Expression;
PrefixDecrement <- operand@Expression;
PrefixOperator <- PrefixIncrement | PrefixDecrement;

PostfixIncrement <- operand@Expression;
PostfixDecrement <- operand@Expression;
PostfixOperator <- PostfixIncrement | PostfixDecrement;

UnaryExpression <- PrefixIncrement | PostfixIncrement | PrefixDecrement | PostfixDecrement;

Expression <- IntegerLiteral | StringLiteral | BinaryExpression | UnaryExpression;
