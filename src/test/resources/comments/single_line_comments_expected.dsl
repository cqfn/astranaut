StringLiteral <- $String$, $#$, $#$;



Addition <- left@Expression, right@Expression;
Subtraction <- left@Expression, right@Expression;
Expression <- BinaryExpression | Variable;
BinaryExpression <- Addition | Subtraction;
Variable <- Identifier;

ExpressionList <- {Expression};

js:

singleExpression(identifier(literal<#1>)) -> Variable(Identifier<#1>);
singleExpression(#1, literal<"+">, #2) -> Addition(#1, #2);

