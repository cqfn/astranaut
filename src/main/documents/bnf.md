# Extended Backus–Naur form

capital letter = "A" | "B" | ... | "Z" ;

small letter = "a" | "b" | ... | "z" ;

letter = capital letter | small letter ;

digit = "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9" ;

white space = " " || "\t" || "\r" || "\n" ;

character = letter | digit | white space ;

number = digit, { digit } ;

escape sequence = "\", ("n" | "r" | "t") ;

string = '"' , { character - '"' | escape sequence }, '"' ;

class name = capital letter, { letter } ;

reserved word = "type" | "data" | "value" ;

name = letter, { letter | digit } - reserved word ;

identifier = small letter, { letter | digit } - reserved word ;

tagged name = identifier, "@", class name ;

child name = class name | tagged name ;

optional child name = "[", [white space], child name, [white space], "]" ;

child descriptor = child name | optional child name ;

delimiter = [white space], ",", [white space] ;

multi-type children list = child descriptor, { delimiter, child descriptor } ;

single-type children list = "{", [white space], child name, [white space], "}" ;

non-abstract node descriptor = class name, "<-", (multi-type children list | single-type children list) ;

abstract node descriptor = class name, "<-", class name, "|", class name, { "|", class name } ;

java identifier = letter, { letter | digit } ;

java symbol = character | "." | "(" | ")" | "#" ;

java code = (letter, { java symbol }) | "#" ;

literal node descriptor = class name, "<~", java identifier, delimiter, java code, delimiter, java code ;

node descriptor = non-abstract node descriptor | abstract node descriptor | literal node descriptor ;

hole = "#", number ;

data = "<", ( string | hole ), ">" ;

parameter = hole | parameterized name ;

parameters list = "(", parameter, { delimiter, parameter }, ")" ;

parameterized name = name, [parameters list | data] ;

translation = parameterized name, "->", parameterized name ;

rule = (node descriptor | translation), ";" ;

rules list = rule, { rule } ;

language descriptor : name, ":" ;

block := language descriptor, rules list ;

document : rules list, { block } ;

# Examples

Class name: `Expression`

Identifier: `left`

Tagged name: `left@Expression`

Optional name: `[Expression]`, `[ left@Expression ]`

Children list: `[ModifierList], Identifier, [Expression]`

Non-abstract node descriptor: `Addition <- left@Expression, right@Expression;`

Single-type node list descriptor: `ModifierList <- {Modifier};`

Abstract node descriptor: `Expression <- Addition | Subtraction | Multiplication;`

Literal node descriptor: `Integer <~ int, Integer.parseInt(#), String.valueOf(#);`

Name: `singleExpression`

Hole: `#0`

Data: `<"test">`, `<#0>`

Translation: `singleExpression(#1, literal<"+">, #2) -> Addition(#1, #2);`

Language descriptor: `java:`

Document:
```
Expression <- Literal | SimpleIdentifier;
Literal <- BooleanLiteral | NumericLiteral | StringLiteral | NullLiteral;
BooleanLiteral <~ boolean, Boolean.parseBoolean(#), String.valueOf(#);
NumericLiteral <~ String, #, #;
NullLiteral <~ String, #, #;
StringLiteral <~ String, #, #;
SimpleIdentifier <~ String, #, #;

JavaScript:
singleExpression(literal(literal<"null">)) -> NullLiteral<"null">;
singleExpression(literal(literal<"true">)) -> BooleanLiteral<"true">;
singleExpression(literal(literal<"false">)) -> BooleanLiteral<"false">;
assignable(identifier(literal<#1>)) -> SimpleIdentifier<#1>;
singleExpression(literal(numericLiteral(literal<#1>))) -> NumericLiteral<#1>;
singleExpression(literal(literal<#1>)) -> StringLiteral<#1>;
```
