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

IntegerLiteral <- 'int';
Digit<#1>, {Digit<#1>} -> IntegerLiteral<#1>;

Expression <- Identifier | IntegerLiteral | Addition;
Addition <- left@Expression, right@Expression;

Plus <- 'char', "'+'";
'+' -> Plus;

Minus <- 'char', "'-'";
'-' -> Minus;

OperatorSymbol <- Plus | Minus;
Operator <- 'String', '""';
{OperatorSymbol<#1>} -> Operator<#1>;

Expression#1, [Whitespace], Operator<'+'>, [Whitespace], Expression#2 -> Addition(#1, #2);
