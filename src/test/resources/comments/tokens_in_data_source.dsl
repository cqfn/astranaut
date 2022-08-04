/* Identifier <- $String$, $#$, $#$;
*/
Addition <- left@Expression, right@Expression;
Subtraction <- left@Expression, right@Expression;
Expression <- BinaryExpression | Variable;
BinaryExpression <- Addition | Subtraction;
Variable <- Identifier;
// Multiplication <- left@Expression, right@Expression;
ExpressionList <- {Expression};

js:
singleExpression(#1, literal<"data/*data">, #2) -> SomeNode(#1, #2); /*  */
singleExpression(identifier(literal<#1>)) -> Variable(Identifier<#1>);
singleExpression(#1, literal<"data//data">, #2) -> SomeOtherNode(#1, #2); // xzc

/*
py:

self -> This;

expr(atom(literal<"comment/**comment">, #1)) -> StringLiteral<#1>;
expr(atom(literal<#1>)) -> StringLiteral<#1>;
*/