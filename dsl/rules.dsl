IntegerLiteral <- $int$, $String.valueOf(#)$, $Integer.parseInt(#)$, $NumberFormatException$;
StringLiteral <- $String$, $#$, $#$;
Identifier <- $String$, $#$, $#$;

Addition <- left@Expression, right@Expression;
Subtraction <- left@Expression, right@Expression;
Expression <- BinaryExpression | Variable;
BinaryExpression <- Addition | Subtraction;
Variable <- Identifier;
ExpressionList <- {Expression};

js:
singleExpression(identifier(literal<#1>)) -> Variable(Identifier<#1>);
