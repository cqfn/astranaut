Addition <- left@Expression, right@Expression;
Subtraction <- left@Expression, right@Expression;
Expression <- Addition | Subtraction;
ExpressionList <- {Expression};

c:
AddressOf <- Expression;
