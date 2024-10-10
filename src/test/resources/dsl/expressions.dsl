/*
 * Copyright (c) 2024 John Doe
 */

import expressions.dsl; // we intentionally added this to test the handling of importing files that have already been imported

IntegerLiteral <- 'int', '0', 'String.valueOf(#)', 'Integer.parseInt(#);
StringLiteral <- 'String', '""';
Literal <- IntegerLiteral | StringLiteral;

Addition <- left@Expression, right@Expression;
Subtraction <- left@Expression, right@Expression;
BinaryOperation <- Addition | Subtraction;

Expression <- Literal | BinaryOperation;
ExpressionList <- {Expression};
