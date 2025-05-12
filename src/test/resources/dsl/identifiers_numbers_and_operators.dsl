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

Comma <- 0;
',' -> Comma;

Whitespace <- 0;
' ' -> Whitespace;

SmallLetter <- 'char';
'a..z'<#1> -> SmallLetter<#1>;

CapitalLetter <- 'char';
'A..Z'<#1> -> CapitalLetter<#1>;

Underscore <- 'char', "'_'";
'_' -> Underscore;

Letter <- SmallLetter | CapitalLetter | Underscore;

Digit <- 'int';
'0..9'<#1> -> Digit<#1>;

LetterOrDigit <- Letter | Digit;

Identifier <- 'String', '""';
Letter<#1>, {LetterOrDigit<#1>} -> Identifier<#1>;
AssignableExpression <- Identifier | 0;

IntegerLiteral <- 'int';
Digit<#1>, {Digit<#1>} -> IntegerLiteral<#1>;

Expression <- Identifier | IntegerLiteral | AssignableExpression | Assignment | Addition;

Assignment <- left@AssignableExpression, right@Expression;
Addition <- left@Expression, right@Expression;

Plus <- 'char', "'+'";
'+' -> Plus;

Minus <- 'char', "'-'";
'-' -> Minus;

Equality <- 'char', "'='";
'=' -> Equality;

OperatorSymbol <- Plus | Minus | Equality;
Operator <- 'String', '""';
{OperatorSymbol<#1>} -> Operator<#1>;

AssignableExpression#1, [Whitespace], Operator<'='>, [Whitespace], Expression#2 -> Assignment(#1, #2);
Expression#1, [Whitespace], Operator<'+'>, [Whitespace], Expression#2 -> Addition(#1, #2);
